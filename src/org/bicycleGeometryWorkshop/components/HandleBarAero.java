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
import org.bicycleGeometryWorkshop.ui.Graphics;

/**
 * This is the aero handlebar sub-handlebar.  It emulates an aero style handlebar.  It is designed to be able
 * to be configured as a straight clip-on with grips or a clip-on with two parallel bars or as bullhorn style bars (-rise).
 *
 * @author Tom
 */
public class HandleBarAero extends BaseComponent {
    
    private DoubleAttribute _attRotation;
    private DoubleAttribute _attRise;
    private DoubleAttribute _attExtension;
    private DoubleAttribute _attGrip;
    private DoubleAttribute _attGripAngle;

    private BooleanAttribute _attSecondSegment;
    private DoubleAttribute _attSecondExtension;
    private DoubleAttribute _attGripFromEnd;

    private Line2D _geomBase;
    private Line2D _geomExtension;
    private Line2D _geomGrip;
    

    private Point2D _handPoint;
    
    
    
    /**
     * Class constructor.  Initialize the class with the component owner.
     * @param owner The component owner.
     */
    public HandleBarAero(ComponentOwner owner)  {
        super("AeroBar", owner);
        
        setIcon("hbAeroIcon16.png");
        
        double rng = 500;

        _attRotation = addAngleAttribute("MountingAngle", 0, -360, 360, "The mounting angle of the handlebars.");
        _attRise = addLengthAttribute("Rise", 25, -rng , rng, "The rise of the handlebar.");
        _attExtension = addLengthAttribute("Length", 150, 0 , rng, "The length of the bar to the grip.");
        _attGrip = addLengthAttribute("GripLength", 100, 0, rng, "The length of the grip or the length of transition if there is a second segment.");
       
        _attGripAngle = addAngleAttribute("GripAngle", 30,-90,90, "The angle of the grip");
        
        _attSecondSegment = addBooleanAttribute("SecondSegment", false, "If true, this aero bar has a second straight segment and the grip is treated as a transition.");
        _attSecondExtension = addLengthAttribute("SecondLength", 150, 0 , rng, "The length of the extension beyond the transition (grip).");
       
        
        _attGripFromEnd = addLengthAttribute("GripFromEnd", 60, 0 , rng, "The hand position point offset from the end of the bar.");
        

        
        _geomBase = addLine2D(); 
        _geomGrip  = addLine2D(); 
        _geomExtension = addLine2D(); 
        
        _handPoint = new Point2D.Double();
 
        
         update();
    }
    

    /**
     * Update the aero bar.
     */
    @Override
    public void updateGeometry() {
        
        //base point 
        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();
        
        double theta = Utilities.degreesToRadians(_attRotation.getDoubleValue());
        
        double rise = _attRise.getDoubleValue();
        double ext = _attExtension.getDoubleValue();
        double grip = _attGrip.getDoubleValue();
        boolean hasSecond = _attSecondSegment.getBooleanValue();
        double secondExt = _attSecondExtension.getDoubleValue();
        double gripOffset = _attGripFromEnd.getDoubleValue();
        
        
        double gTheta = Utilities.degreesToRadians(_attGripAngle.getDoubleValue());
        

        //start point 
        Point2D startPoint = new Point2D.Double(x ,y + rise);
   
        //end/base of grip
        Point2D ctlPoint1 = Utilities.polarPoint(startPoint, ext, 0);
        //end of grip/ start second extension
        Point2D ctlPoint2 = Utilities.polarPoint(ctlPoint1, grip, gTheta);
        
 
        //extension point - end of second extension
        Point2D endPoint = Utilities.polarPoint(ctlPoint2, secondExt,0);
            
     

       //temp grip offset
       double gp = gripOffset;
       
       if(hasSecond) {
           
           if(gp > secondExt ) {
               gp -= secondExt;
               
               //check lengths
               if (gp > grip) {
                   gp -= grip;
                   //handpoint on base bar
                   _handPoint = Utilities.polarPoint(startPoint, ext - gp, 0);

               } else {
                   //handpoint on grip
                   _handPoint = Utilities.polarPoint(ctlPoint1, grip - gp, gTheta);
               }               
               
               
               
           } else {
               
               //handpoint on second extension
               _handPoint = Utilities.polarPoint(ctlPoint2,  secondExt - gp, 0);
           }
           
       } else {
           
           //check lengths
            if(gp > grip) {
                gp -= grip;
                //handpoint on base bar
                _handPoint = Utilities.polarPoint(startPoint,  ext - gp, 0);
                
            } else {
                //handpoint on grip
                _handPoint = Utilities.polarPoint(ctlPoint1,  grip  - gp, gTheta);
            }          
           
           
       }
       
 
       

        Path2D path = new Path2D.Double();
  
        path.moveTo(basePoint.getX(), basePoint.getY());//start
        if(rise != 0.0) {
            path.lineTo(startPoint.getX(), startPoint.getY());//rise line
        }
        if(ext > 0) {
            path.lineTo(ctlPoint1.getX(), ctlPoint1.getY());//extension
        }
        if(grip > 0) {
            path.lineTo(ctlPoint2.getX(), ctlPoint2.getY());//grip/transition
        }
        if(hasSecond) {
            path.lineTo(endPoint.getX(), endPoint.getY());//second segment
        }
        
        //transform for rotation
        AffineTransform trans = AffineTransform.getRotateInstance(theta, x, y);

        //transform hand point
        _handPoint = trans.transform(_handPoint, null);
        //transform the path
        Shape shapePath = trans.createTransformedShape(path);        
        
        //clear and reset geometry for bounding box
        clearGeometry();
        addGeometry(shapePath);
               
        
    }
    
    /**
     * Get the hand point of the handlebar.  The hand position is ignored as the aero bar only supplies one hand position.  
     * @param position The hand position to query (ignored for aero bar).
     * @return The hand point of the aero bar.
     */
    public Point2D getHandPoint(HandleBarPosition position) {
        
        return _handPoint;
        
    }
    
    /**
     * Render the aero bar.
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
