/*
 * The MIT License
 *
 * Copyright 2019 Tom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.bicycleGeometryWorkshop.components;

import java.awt.Color;
import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.attributes.DoubleAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.attributes.BooleanAttribute;
import org.bicycleGeometryWorkshop.geometry.Curves;
import org.bicycleGeometryWorkshop.geometry.IntersectionPoint;
import org.bicycleGeometryWorkshop.geometry.IntersectionPointResult;
import org.bicycleGeometryWorkshop.ui.Graphics;

/**
 * Drop handlebar handlebar sub-component. The drop handlebar emulates 
 * standard road drop bars and provides for four hand positions.  The drop bar 
 * is constructed in the frame of reach and drop, with or without straight sections.
 * @author Tom
 */
public class HandleBarDrop extends BaseComponent {

    private DoubleAttribute _attReach;
    private DoubleAttribute _attDrop;
    private DoubleAttribute _attExtension;
    private DoubleAttribute _attRotation;

    private BooleanAttribute _attHasRamp;
    private DoubleAttribute _attRampAngle;
    private DoubleAttribute _attRampLength;

    private BooleanAttribute _attHasDrop;
    private DoubleAttribute _attDropAngle;
    private DoubleAttribute _attDropLength;

    private DoubleAttribute _attHoodT;
    private DoubleAttribute _attDropT;
    private DoubleAttribute _attExtT;

    private DoubleAttribute _attCenterHeight;

    private Shape _path;

    private Point2D _handPointA;
    private Point2D _handPointB;
    private Point2D _handPointC;
    private Point2D _handPointD;

    /**
     * Class constructor.  Construct the class with the component owner.
     * @param owner The component owner.
     */
    public HandleBarDrop(ComponentOwner owner) {
        super("DropBar", owner);

        setIcon("hbDropIcon16.png");

        double rng = 500;

        _attRotation = addAngleAttribute("MountingAngle", 0, -360, 360, "The mounting angle of the handlebars.");

        _attReach = addLengthAttribute("Reach", 80, 0, rng, "The forward reach of the drop bar.");
        _attDrop = addLengthAttribute("Drop", 120, 0, rng, "The downard drop of the drop bar.");
        _attExtension = addLengthAttribute("Extension", 60, 0, rng, "The length of the straight ends.");

        _attHasRamp = addBooleanAttribute("HasRamp", true, "Toggle for handle bar ramp sections (straight section above curvature).");
        _attRampAngle = addAngleAttribute("RampAngle", -10, -60, 0, "The angle of the start of the ramp.");
        _attRampLength = addLengthAttribute("RampLength", 50, 0, rng, "The length of the ramp before it curves.");

        _attHasDrop = addBooleanAttribute("HasDrop", true, "Toggle for straight grip section in drop portion of the bar.");
        _attDropAngle = addAngleAttribute("DropAngle", 35, 0, 60, "The angle drop grip section measured from horizontal.");
        _attDropLength = addLengthAttribute("DropLength", 80, 0, rng, "The length of the drop grip section.");

        _attHoodT = addDoubleAttribute("HoodT", 0.5, 0.0, 1.0, "The hood position determined by the scale(T) along the handlebar length (0.0 to 1.0)");
        _attDropT = addDoubleAttribute("DropT", 0.5, 0.0, 1.0, "The drop position determined by the scale(T) along the drops length (0.0 to 1.0)");
        _attExtT = addDoubleAttribute("ExtensionT", 0.25, 0.0, 1.0, "The extension position determined by the scale(T) along the extension length (0.0 to 1.0)");
        _attCenterHeight = addLengthAttribute("CenterHeight", 15, 0, rng, "The height of the center grip osition above teh bar centerline.");

        //hand points
        _handPointA = new Point2D.Double();
        _handPointB = new Point2D.Double();
        _handPointC = new Point2D.Double();
        _handPointD = new Point2D.Double();

        _path = new Path2D.Double();

        update();
    }

    /**
     * Update the drop bar geometry.
     */
    @Override
    public void updateGeometry() {

        //base point 
        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();

        double reach = _attReach.getDoubleValue();
        double drop = _attDrop.getDoubleValue();
        double extension = _attExtension.getDoubleValue();
        double theta = Utilities.degreesToRadians(_attRotation.getDoubleValue());

        double rampTheta = Utilities.degreesToRadians(_attRampAngle.getDoubleValue());
        double rampLength = _attRampLength.getDoubleValue();

        //angle of straight drop section
        double dropTheta = Utilities.degreesToRadians(_attDropAngle.getDoubleValue());
        double dropLength = _attDropLength.getDoubleValue();

        //t values for hand placement
        double hoodT = _attHoodT.getDoubleValue();
        double dropT = _attDropT.getDoubleValue();
        double extT = _attExtT.getDoubleValue();

        boolean hasRamp = _attHasRamp.getBooleanValue();
        boolean hasDrop = _attHasDrop.getBooleanValue();

        //calc end and extension first
        //end point of curve or drop grip section - last point before extension 
        Point2D endPoint = new Point2D.Double(x, y - drop);

        //vertical line at reach for intersections
        Line2D reachLine = new Line2D.Double(x + reach, y, x + reach, y - drop);

        //start point for first quadratic bezier point
        Point2D startPnt1 = new Point2D.Double();
        //control point for first quadratic
        Point2D cntrlPnt1 = new Point2D.Double();

        //end point of first quad and start point of second quad
        Point2D midPnt = new Point2D.Double();

        //contorl point for second quadratic
        Point2D cntrlPnt2 = new Point2D.Double();
        //edn point of second quadratic
        Point2D endPnt2 = new Point2D.Double();

        //is there a rampe defined?
        if (!hasRamp) {

            startPnt1.setLocation(x, y);
            //control point at top horizontal from mount
            cntrlPnt1.setLocation(x + reach, y);

        } else {


            //end of ramp
            Point2D rampEnd = Utilities.polarPoint(basePoint, rampLength, rampTheta);
            startPnt1 = rampEnd;

            //ramp line for intersection
            Line2D rampLine = new Line2D.Double(basePoint, rampEnd);

            IntersectionPoint ip = Utilities.lineLineIntersect(rampLine, reachLine);
            IntersectionPointResult ipr = ip.result();
            if (ipr == IntersectionPointResult.ONE_POINT) {
                cntrlPnt1 = ip.getIntersection1();
            } else {
                //fail - default position horizontal from....
                cntrlPnt1.setLocation(x + reach, rampEnd.getY());
            }

        }

        if (!hasDrop) {

            //end point 2
            endPnt2 = endPoint;
            //control point at bottom horizontal from drop
            cntrlPnt2.setLocation(x + reach, y - drop);

        } else {

            //top of drop grip
            Point2D dropBegin = Utilities.polarPoint(endPoint, dropLength, dropTheta);
            //use for curve
            endPnt2 = dropBegin;
            //make a line for intersection
            Line2D dropLine = new Line2D.Double(endPoint, dropBegin);
            //calc intersection
            IntersectionPoint ip = Utilities.lineLineIntersect(dropLine, reachLine);
            IntersectionPointResult ipr = ip.result();
            if (ipr == IntersectionPointResult.ONE_POINT) {
                cntrlPnt2 = ip.getIntersection1();
            } else {
                //fail - default position horizontal from....
                cntrlPnt2.setLocation(x + reach, dropBegin.getY());
            }

        }

        //difference in y between start and end of curve
        double curveH = startPnt1.getY() - endPnt2.getY();

        //y value for curve mid
        double curveMidY = endPnt2.getY() + curveH / 2;

        //curve midpoint
        midPnt = new Point2D.Double(x + reach, curveMidY);

        /**
         * * Construct Path  **
         */
        //cast to acces path methods
        Path2D path = (Path2D) _path;
        path.reset();
        //move to base point
        path.moveTo(x, y);

        if (hasRamp) {
            path.lineTo(startPnt1.getX(), startPnt1.getY());
        }

        path.quadTo(cntrlPnt1.getX(), cntrlPnt1.getY(), midPnt.getX(), midPnt.getY());
        path.quadTo(cntrlPnt2.getX(), cntrlPnt2.getY(), endPnt2.getX(), endPnt2.getY());


        if (hasDrop) {
            path.lineTo(endPoint.getX(), endPoint.getY());
        }
        //line to extension
        if (extension > 0) {
            path.lineTo(x - extension, y - drop);
        }

        /**
         * * Hand Positions **
         */
        //top of bar at center - TODO bar diameter
        double cenHgt = _attCenterHeight.getDoubleValue();
        _handPointA.setLocation(x, y + cenHgt);

        if (hasRamp) {
            //use point on ramp
            double hlfT = 0.5;
            double t = 0.0;
            if (hoodT > hlfT) {
                t = (hoodT - hlfT) / hlfT;
                _handPointB = Curves.quadraticBezierPoint(startPnt1, cntrlPnt1, midPnt, t);
            } else {
                t = hoodT / hlfT;
                _handPointB = Utilities.polarPoint(basePoint, rampLength * t, rampTheta);
            }

        } else {
            //use point on bezier
            _handPointB = Curves.quadraticBezierPoint(startPnt1, cntrlPnt1, midPnt, hoodT);

        }

        if (hasDrop) {
            //use point on drop
            _handPointC = Utilities.polarPoint(endPnt2, -dropLength * dropT, dropTheta);
        } else {

            _handPointC = Curves.quadraticBezierPoint(midPnt, cntrlPnt2, endPnt2, dropT);
        }

        if (extension > 0) {
            _handPointD = new Point2D.Double(x - (extension * extT), y - drop);
        } else {
            _handPointD = endPoint;
        }

        //transform for rotation
        AffineTransform trans = AffineTransform.getRotateInstance(theta, x, y);

        //regenerating this breaks the references...
        _path = trans.createTransformedShape(path);

        //repack geometry since references are broken.
        clearGeometry();
        addGeometry(_path);

        //update the hand points (except for A which isn't affected by the rotation)
        _handPointB = trans.transform(_handPointB, null);
        _handPointC = trans.transform(_handPointC, null);
        _handPointD = trans.transform(_handPointD, null);

    }

    /**
     * Get the hand point of the drop bar.  
     * @param position The hand position to retrieve from the drop bar.
     * @return The hand point defined for the position.
     */
    public Point2D getHandPoint(HandleBarPosition position) {

        Point2D hp = new Point2D.Double();

        switch (position) {

            case PositionA:
                hp = _handPointA;
                break;

            case PositionB:
                hp = _handPointB;
                break;

            case PositionC:
                hp = _handPointC;
                break;

            case PositionD:
                hp = _handPointD;
                break;

        }

        return hp;

    }

    /**
     * Render the drop bar.
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {


        //draw the path
        g2.draw(_path);

    }

    /**
     * Render the hand point.
     *
     * @param g2 The graphics object to render to.
     * @param color The color to use for rendering.
     * @param scale The scale of the current view.
     */
    public void renderHandPoints(Graphics2D g2, Color color, float scale) {

        Graphics.renderCrossHandle(g2, color, _handPointA, scale);
        Graphics.renderCrossHandle(g2, color, _handPointB, scale);
        Graphics.renderCrossHandle(g2, color, _handPointC, scale);
        Graphics.renderCrossHandle(g2, color, _handPointD, scale);

    }

}
