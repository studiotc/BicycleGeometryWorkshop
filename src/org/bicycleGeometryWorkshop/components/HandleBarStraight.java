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
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.ui.Graphics;

/**
 * Straight handlebar handlebar sub-component.  The straight handlebar allows 
 * for an offset and mounting rotation.  The straight handlebar only defines one hand point.
 * @author Tom
 */
public class HandleBarStraight extends BaseComponent {
    
    
    private DoubleAttribute _attMountAngle;
    private DoubleAttribute _attProjectOffset;
    
    private DoubleAttribute _attGripOffset;
    private DoubleAttribute _attGripAngle;

    private Point2D _handPoint;
    private Line2D _geomHandlebar;
    
    
    /**
     * Class Constructor.  Construct the class with the component owner.
     * @param owner The component owner.
     */
    public HandleBarStraight(ComponentOwner owner)  {
        super("StraightBar", owner);
        
        setIcon("hbStraightIcon16.png");
        
        double rng = 5000;

        _attMountAngle = addAngleAttribute("MountingAngle", 150, -360, 360, "The mounting angle of the handlebar.");
        _attProjectOffset = addLengthAttribute("ProjectedOffset", 80, 0 , rng, "The projected length of the handlebar (in side view).");
        
        _attGripOffset = addLengthAttribute("GripOffset", 15, 0 , rng, "The offset of the grasping point from the center of the handlebar.");
         _attGripAngle = addAngleAttribute("GripAngle", 90, -360, 360, "The projection angle for the grasping point from the center of teh handlebar.");

        _handPoint = new Point2D.Double();
   
        _geomHandlebar = addLine2D(); 
        
        
         update();
    }
    

    /**
     * Update the geometry.
     */
    @Override
    public void updateGeometry() {
        
        //base point 
        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();
        
        double os = _attProjectOffset.getDoubleValue();
        double theta = Utilities.degreesToRadians(_attMountAngle.getDoubleValue());
       // theta = Math.PI - theta;
        Point2D ep = Utilities.polarPoint(basePoint, os, theta);
        
        _geomHandlebar.setLine(x, y , ep.getX() ,ep.getY());
        
        double gOffset = _attGripOffset.getDoubleValue();
        double gTheta =  Utilities.degreesToRadians(_attGripAngle.getDoubleValue());

        Point2D gripPoint = Utilities.polarPoint(ep, gOffset, gTheta);
        _handPoint.setLocation(gripPoint);
        
        
    }
    
    /**
     * Get the hand point defined by the straight handlebar.  
     * The straight handlebar only defines one hand point.
     * @param position The handlebar position - not used here.
     * @return The hand point of he handlebar.
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
     * @param g2 The graphics object to render to.
     * @param color  The color to use for rendering.
     * @param scale The scale of the current view.
     */
    public void renderHandPoints(Graphics2D g2, Color color, float scale) {
        
        Graphics.renderCrossHandle(g2, color, _handPoint, scale);

        
    }

    
}
