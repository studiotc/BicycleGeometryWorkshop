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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.ui.Graphics;

/**
 * The rise bar handlebar sub-component.  This emulates a typical riser bar that has two segments 
 * common to BMX and mountain bikes.  There is only one hand position provided by riser bars.
 * @author Tom
 */
public class HandleBarRiser extends BaseComponent {

    private DoubleAttribute _attRotation;

    private DoubleAttribute _attRise;
    private DoubleAttribute _attOffset;
    private DoubleAttribute _attAngle;

    private DoubleAttribute _attGripOffset;
    private DoubleAttribute _attGripAngle;    
    
    private Line2D _geomRiser;
    private Line2D _geomExtension;

    private Point2D _handPoint;

    
    /**
     * Class constructor.  Construct the riser bar with component owner.
     * @param owner The component owner.
     */
    public HandleBarRiser(ComponentOwner owner) {
        super("RiserBar", owner);

        setIcon("hbRiserIcon16.png");

        double rng = 500;

        _attRotation = addAngleAttribute("MountingAngle", 90, -360, 360, "The mouting angle in the clamp (rotation forward or back).");
        _attRise = addLengthAttribute("Rise", 80, 0, rng, "The height of the riser section from the clamp.");
        _attAngle = addAngleAttribute("UpperAngle", 15, -360, 360, "The projected angle of the upper handlebar tube after the riser section (angle in side view).");
        _attOffset = addLengthAttribute("ProjectedOffset", 50, 0, rng, "The projected distance from the top of the riser to the hand point (distance in side view).");

        _attGripOffset = addLengthAttribute("GripOffset", 15, 0 , rng, "The offset of the grasping point from the center of the handlebar.");
        _attGripAngle = addAngleAttribute("GripAngle", 90, -360, 360, "The projection angle for the grasping point from the center of teh handlebar.");


        _geomRiser = addLine2D();
        _geomExtension = addLine2D();

        _handPoint = new Point2D.Double();

        update();
    }

    @Override
    public void updateGeometry() {

        //base point 
        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();

        double riser = _attRise.getDoubleValue();
        double offset = _attOffset.getDoubleValue();
        double thetaRot = Utilities.degreesToRadians(_attRotation.getDoubleValue());
        double thetaAngle = Utilities.degreesToRadians(_attAngle.getDoubleValue());
        //thetaRot = Math.PI - thetaRot;

        //riser point
        Point2D rp = Utilities.polarPoint(basePoint, riser, thetaRot);
        //extensionPoint
        Point2D ep = Utilities.polarPoint(rp, offset, thetaRot + thetaAngle);

        
        double gOffset = _attGripOffset.getDoubleValue();
        double gTheta =  Utilities.degreesToRadians(_attGripAngle.getDoubleValue());
        
        Point2D gripPoint = Utilities.polarPoint(ep, gOffset, gTheta);
        //set the handpoint
        _handPoint.setLocation(gripPoint);

        Path2D path = new Path2D.Double();

        path.moveTo(basePoint.getX(), basePoint.getY());
        path.lineTo(rp.getX(), rp.getY());
        path.lineTo(ep.getX(), ep.getY());

        clearGeometry();
        addGeometry(path);

        //these references are broken now...
        _geomRiser.setLine(basePoint, rp);
        _geomExtension.setLine(rp, ep);

    }

    /**
     * Get the hand point associate with the riser bar.  The position is 
     * ignored as the riser bar only provides one hand position.
     * @param position  The hand position (ignored).
     * @return The hand position of the riser bar.
     */
    public Point2D getHandPoint(HandleBarPosition position) {

        return _handPoint;

    }

    /**
     * Render the geometry.
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {
        renderGeometry(g2);
    }

    /**
     * Render the hand point.
     *
     * @param g2 The graphics object to render to.
     * @param color The color to use for rendering.
     * @param scale The scale of the current view.
     */
    public void renderHandPoints(Graphics2D g2, Color color, float scale) {

        Graphics.renderCrossHandle(g2, color, _handPoint, scale);

    }

}
