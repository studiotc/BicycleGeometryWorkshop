/*
 * The MIT License
 *
 * Copyright 2020 Tom.
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
package org.bicycleGeometryWorkshop.ui.measure;

import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.geometry.Vector2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;





/**
 * Base class for on-screen measuring utilities.
 *
 * @author Tom
 */
public abstract class Measure {

    private static final int FONT_SIZE = 14;
    private static final int LINE_SPACING = 18;
    private static final int LEFT_MARGIN = 4;

    //x color
    public static final Color COLOR_X  = new Color(220,0,0);
    //y color
    public static final Color COLOR_Y  = new Color(0,220,0);
    //distance/angle color
    public static final Color COLOR_DA  = new Color(255,255,255);
    //screen color/text color
    public static final Color COLOR_S  = new Color(255,255,255);
  
    
    private int _current_line_y;

    private Font _screenFont;
    

    private AffineTransform _wtsTransfom;

    /**
     * Class constructor.
     */
    public Measure() {

        //setup screen font
        _screenFont = new Font(Font.DIALOG, Font.BOLD, FONT_SIZE);



        //intialize transform
        _wtsTransfom = new AffineTransform();

        _current_line_y = LINE_SPACING;

    }

    /**
     * Initialize the measuring object or reset it after previous use. This is
     * called when set active. Any sub class should rely on reset() to
     * individual reset operations.
     *
     */
    public final void resetMeasure() {

        //set intial screen text y position (baseline)
        _current_line_y = LINE_SPACING;

        //reset transform to identity
        _wtsTransfom.setToIdentity();

        //call sub-class reset
        reset();
    }

    /**
     * Do any internal reset or preparations here. This will be called by
     * resetMeasure() when activated.
     */
    protected abstract void reset();

    /**
     * Update the World to Screen Transform from the current Graphics object
     * transform.
     *
     * @param transform The current graphics object transform.
     *
     */
    public void updateTransform(AffineTransform transform) {

        try {
            //attempt to get and assign inverse transform
            AffineTransform invsTrans = transform.createInverse();
            _wtsTransfom = invsTrans;

        } catch (Exception ex) {

            //safety check?...
            _wtsTransfom.setToIdentity();

        }

    }

    /**
     * Call method for when a Point is clicked.
     *
     * @param point The point clicked in screen coordinates.
     */
    public final void pointClicked(Point2D point) {

        Point2D transPoint = _wtsTransfom.transform(point, null);

        if (transPoint != null) {

            onPointClicked(transPoint);

        }

    }

    /**
     * Called from point clicked event.
     *
     * @param worldPoint The point in World Coordinates.
     */
    public abstract void onPointClicked(Point2D worldPoint);

    public final void dynamicPoint(Point2D point) {

        Point2D transPoint = _wtsTransfom.transform(point, null);

        if (transPoint != null) {

            onDynamicPoint(transPoint);

        }

    }

    /**
     * Method to update measure with the current dynamic point (i.e.: mouse
     * point on MouseMove)
     *
     * @param worldPoint Current dynamic point (or mouse point) in world
     * coordinates.
     */
    public abstract void onDynamicPoint(Point2D worldPoint);

    /**
     * Render any Graphics that need to be painted in world space.
     *
     * @param g2 The graphics object to paint to.
     * @param scale The scale of the current transform for line weights, etc.
     */
    public abstract void renderInWorld(Graphics2D g2, float scale);

    /**
     * Render screen message.
     *
     * @param g2  Th graphics object to render to.
     */
    public abstract void renderInScreen(Graphics2D g2);

    
    /**
     * Resets the screen layout line counter.
     * Call this at the beginning of each screen draw session
     * to reset the line counter.
     */
    public final void resetScreenLayout() {
        
        _current_line_y = LINE_SPACING;
        
    }
    
    /**
     * Get a BaasicStroke with the line weight set.
     * @param scale Scale for the stroke line weight.
     * @return A BasicStroke with line weight setup;
     */
    private BasicStroke getStroke(float scale) {
        
       float lw = 1.5f / scale;

       return new BasicStroke(lw);
        
    }
    
    /**
     * Get a BasicStroke with a dashed line that uses the provided scale for dashes and width.
     * @param scale  The scale of the line weight and dashed spacing.
     * @return The dashed stroke.
     */
    private BasicStroke getDashedStroke(float scale) {
        
        float lw = 1.5f / scale;
        float s1 = 6 / scale;
        float s2 = 12 / scale;

        float[] cl = {s1, s1};
        BasicStroke dashStroke = new BasicStroke(lw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 1.0f, cl, 0);        
        
        return dashStroke;
        
    }
    
    /**
     * Render a Line of text to the screen. Subsequent calls will layout below
     * each preceeding line.
     *
     * @param g2 The graphics object to draw to.
     * @param text The text to display.
     * @param color  The color to use for the line.
     */
    public final void renderScreenLine(Graphics2D g2, String text, Color color) {

        //int y = LINE_SPACING + (LINE_SPACING * line);
        //set font and color
        g2.setFont(_screenFont);
        g2.setColor(color);

        //draw the text
        g2.drawString(text, LEFT_MARGIN, _current_line_y);

        //increment line spacing
        _current_line_y += LINE_SPACING;
    }
    
    /**
     * Render a Line of text to the screen. Subsequent calls will layout below
     * each preceeding line.  Uses default text color.
     * 
     * @param g2 The graphics object to draw to.
     * @param text The text to display.
     */
    public final void renderScreenLine(Graphics2D g2, String text) {
        
        renderScreenLine( g2,  text,  COLOR_S);
        
    }

    /**
     * render a cross shaped handle.
     * @param g2  The graphics object to render to.
     * @param point  Location of the handle.
     * @param scale Scale for line weight, etc.
     */
    public void renderCrossHandle(Graphics2D g2, Point2D point, float scale) {


        g2.setColor(COLOR_X);
        g2.setStroke(getStroke(scale));

        double x = point.getX();
        double y = point.getY();

        double length = 10 / scale;

        Line2D lineHoriz = new Line2D.Double(x - length, y, x + length, y);
        Line2D lineVert = new Line2D.Double(x, y - length, x, y + length);

        g2.draw(lineHoriz);
        g2.draw(lineVert);

    }
    
    /**
     * Render a Dot Handle - center point for arcs, etc.
     * This renders an open circle.
     * 
     * @param g2 The graphics object to render to.
     * @param point Location of the handle.
     * @param scale  Scale for line weight, etc.
     */
    public void renderDotHandle(Graphics2D g2, Point2D point, float scale) {


        g2.setColor(COLOR_X);
        g2.setStroke(getStroke(scale));

        double x = point.getX();
        double y = point.getY();

        double length = 10 / scale;
        
        x -= length;
        y -= length;
        
        double w = length * 2;
        double h = length * 2;

        Ellipse2D ellipse = new Ellipse2D.Double(x,y,w,h);
        g2.draw(ellipse);
 

    }    
    

    /**
     * Render a Line.
     * @param g2 The graphics object to paint to.
     * @param startPoint Start point of the line.
     * @param endPoint End point of the line.
     * @param scale Scale for line weight,etc.
     * @param color Color of the line to draw.
     * @param dashed True to show dashed, false to draw solid line.
     */
    public void renderLine(Graphics2D g2, Point2D startPoint, Point2D endPoint, float scale, Color color, boolean dashed) {

        
        g2.setColor(color);
        if(dashed) {
             g2.setStroke(getDashedStroke(scale));
        } else {
           g2.setStroke(getStroke(scale)); 
        }
        

        Line2D mLine = new Line2D.Double(startPoint, endPoint);
        g2.draw(mLine);

    }
    
    

    
    /**
     * Render an arc.  This will render an arc
     *  for the smallest angle of  the given points.
     * 
     * @param g2 The graphics object to paint to.
     * @param basePoint Base point or center of arc.
     * @param pointA Point to define an angle. 
     * @param pointB Point to define an angle.
     * @param scale Scale for line weight,etc.
     */
    public void renderArc(Graphics2D g2, Point2D basePoint, Point2D pointA, Point2D pointB, float scale) {
        
       
        g2.setColor(COLOR_DA);
        g2.setStroke(getStroke(scale));       
        
        double radius = 0;
        double da = basePoint.distanceSq(pointA);
        double db = basePoint.distanceSq(pointB);
        
        Vector2D va = Vector2D.fromLine(basePoint, pointA);
        va.normalize();
        Vector2D vb = Vector2D.fromLine(basePoint, pointB);
        vb.normalize();
        
        double thetaSweep = Vector2D.angle(va, vb);
        //convert to degrees
        double angleSweep = Utilities.radiansToDegrees(thetaSweep);
        
        //use shorter of two segments for radius
        if(da > db) {
            radius = basePoint.distance(pointB);
        } else {
            radius = basePoint.distance(pointA);
        }
        
        //get the segment angles
        double thetaA = Utilities.anglePointPoint(basePoint, pointA);
        double thetaB = Utilities.anglePointPoint(basePoint, pointB);        
        
        
        //reverse the direction for drawing arc
        thetaA *= -1;
        thetaB *= -1;
        
        double angleA = Utilities.radiansToDegrees(thetaA);
        double angleB = Utilities.radiansToDegrees(thetaB);
        
        //normalize angles
        if(angleA < 0) angleA += 360;
        if(angleB < 0) angleB += 360;
        

        double posSweep = 0;
        double negSweep = 0;
        double angleStart = 0;
        
        //resolve swep start angle
        if(angleA < angleB) {
            posSweep = angleB - angleA;
            negSweep = (360 - angleB) + angleA;
            if(posSweep < negSweep) {
                angleStart = angleA;
            } else {
                angleStart = angleB;
            }
            
        } else {
            posSweep = angleA - angleB;
            negSweep = (360 - angleA) + angleB;
            if(posSweep < negSweep) {
                angleStart = angleB;
            } else {
                angleStart = angleA;
            }            
  
        }

        //setup the arc
        Arc2D arc = new Arc2D.Double();
        arc.setArcByCenter(basePoint.getX(), basePoint.getY(), radius, angleStart, angleSweep, Arc2D.OPEN);
        //draw the arc
        g2.draw(arc);
        
        
    }
    
    

}
