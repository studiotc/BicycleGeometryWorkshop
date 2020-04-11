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

import java.awt.BasicStroke;
import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.attributes.DoubleAttribute;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * The stem component of a bicycle.  This component attaches at the stem point 
 * of the frame and controls the attachment point of the handlebars.
 * 
 * @author Tom
 */
public class Stem extends BaseComponent {
    
    
    //private DoubleAttribute _attHeadTubeAngle;
    private DoubleAttribute _attLength;
    private DoubleAttribute _attAngle;
    private DoubleAttribute _attRise;
    private DoubleAttribute _attSpacer;
    private DoubleAttribute _attHeadSet;
    
    private double _headTubeAngle;
    

    private Point2D _mountPoint;
    
    private Ellipse2D _geomEnd;
 
    private Path2D _path;
 
    /**
     * Class constructor.  COnstruct the stem with the component owner.
     * @param owner The owner of the stem component.
     */
    public Stem(ComponentOwner owner) {
        super(DataBaseKeys.STEM.toString(), owner);
        
        setIcon("StemIcon16.png");
        
        _headTubeAngle = 90;
        
        //_attHeadTubeAngle = addAttrDouble("HeadTubeAngle", 90);
        _attLength = addLengthAttribute("Length", 100,0,5000, "The length of the stem from the fork to the handle bar.");
        _attAngle = addAngleAttribute("Angle", -6,-180,180, "The angle of stem from the head tube.");
        _attRise = addLengthAttribute("Rise", 25,0,5000, "The height of the stem from the base to the cener line of the extension.");
 
        _attSpacer = addLengthAttribute("Spacer", 5,0,1000, "The height of any spacers between the headset and stem.");
         _attHeadSet = addLengthAttribute("HeadSetTop", 15, 0, 100, "Height of the top headset bearing.");
        
        _mountPoint = new Point2D.Double(0,0);
        

        _geomEnd = addEllipse2D();        
    
        _path = new Path2D.Double();
        
        update();
    }
    
    
    /**
     * Update the Stem with the base point (connection point) and the head tube angle.
     * @param basePoint The base point (connection point).
     * @param headTubeAngle The head tube angle of the frame.
     */
    public void updateStem(Point2D basePoint, double headTubeAngle) {
        
        //set the base point
        setBasePoint(basePoint);
        
        //set the head tube angle
        _headTubeAngle = headTubeAngle;
        
        //call update
        update();
        
    }
    
    /**
     * Update the stem geometry.
     */
    @Override
    public void updateGeometry() {
        
        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();
        
        double headTubeAngle = _headTubeAngle;// _attHeadTubeAngle.getDoubleValue(); 
        double length = _attLength.getDoubleValue(); 
        double angle =_attAngle.getDoubleValue(); 
        double rise = _attRise.getDoubleValue();       
        double spacer = _attSpacer.getDoubleValue(); 
        double headSet = _attHeadSet.getDoubleValue();
        
        //head tube direction
        double htTheta = Math.PI - Utilities.degreesToRadians(headTubeAngle);
        
        double cenHeight = rise + spacer + headSet;
        Point2D cenPoint = Utilities.polarPoint(basePoint, cenHeight, htTheta);
        
        
        //get the stem angle
        double extAngle = Utilities.degreesToRadians(angle);
        //make offset from head tube anlge
        double extTheta = htTheta - (Math.PI / 2) + extAngle;
        
        Point2D hbPoint = Utilities.polarPoint(cenPoint, length, extTheta);
        

        double barDia = 25;
        double hbd = barDia / 2;
        
        //handlebar mounting point
        _mountPoint.setLocation(hbPoint);
        

        //set the ellipse end
        _geomEnd.setFrame(hbPoint.getX() - hbd, hbPoint.getY() - hbd, barDia, barDia);
        
        
        _path.reset();
        
        _path.moveTo(basePoint.getX(), basePoint.getY());
        _path.lineTo(cenPoint.getX(), cenPoint.getY());
        _path.lineTo(hbPoint.getX(), hbPoint.getY());
        
        
    }
    

    /**
     * Get the mounting point for the handlebars.
     * @return The handlebar mounting point.
     */
    public Point2D getMountPoint() {
        return new Point2D.Double(_mountPoint.getX(),_mountPoint.getY());
    }
    
    
    /**
     * Render the Stem.
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {
        
 
       BasicStroke ns = new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        Stroke s = g2.getStroke();
        
        g2.setStroke(ns);
        
        g2.draw(_path);
        
        g2.setStroke(s);        
        

        g2.fill(_geomEnd);
        
    }


    
}
