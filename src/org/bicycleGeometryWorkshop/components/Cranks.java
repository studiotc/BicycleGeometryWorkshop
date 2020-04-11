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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 *  Cranks component of the Bicycle.  This component attaches at the bottom bracket point calculated in the frame.
 * @author Tom
 */
public class Cranks extends BaseComponent {

    private  DoubleAttribute _attLength;
   
    
    private double _rotation;

    private  Line2D _geomCrankRight;
    private  Line2D _geomCrankLeft;



    private  Point2D _crankPointRight;
    private  Point2D _crankPointLeft;

    /**
     * Class constructor.  Constructor the class with the component owner.
     * @param owner The component owner.
     */
    public Cranks( ComponentOwner owner) {
        super(DataBaseKeys.CRANKS.toString(),  owner);

        setIcon("CranksIcon16.png");
        

        _attLength = addLengthAttribute("Length", 170, 1, 1000, "The length of the crank arm.");
        _rotation = 0;
        

        _crankPointRight = new Point2D.Double(0, 0);
        _crankPointLeft = new Point2D.Double(0, 0);

        _geomCrankRight = addLine2D();
        _geomCrankLeft = addLine2D();


        
        update();

    }


    /**
     * Update the cranks with base point (bottom bracket center) and the crank rotation.
     * @param basePoint  The base point of the cranks.
     * @param rotation The rotation of the cranks.
     */
    public void updateCranks(Point2D basePoint, double rotation) {
        
        setBasePoint(basePoint);
        //update the crank rotation
        _rotation = rotation;
        update();        
        
    }
    
    /**
     * Update the geometry.
     */
    @Override
    public void updateGeometry() {

        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();

        double length = _attLength.getDoubleValue();
        

        //crank angles
        double thetaRight = -Utilities.degreesToRadians(_rotation);
        double thetaLeft = Math.PI + thetaRight;


        //crank center points
        Point2D crankRightCen = Utilities.polarPoint(basePoint, length, thetaRight);
        Point2D crankLeftCen = Utilities.polarPoint(basePoint, length, thetaLeft);
        

        //crank points
        _crankPointRight.setLocation(crankRightCen);
        _crankPointLeft.setLocation(crankLeftCen);

        //update geometry
        _geomCrankRight.setLine(basePoint, crankRightCen);
        _geomCrankLeft.setLine(basePoint, crankLeftCen);



    }

 
    /**
     * Render the right hand crank.
     * @param g2 The graphics object to render to.
     */
    public void renderRight(Graphics2D g2) {

       BasicStroke ns = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        Stroke s = g2.getStroke();
        
        g2.setStroke(ns);

        g2.draw(_geomCrankRight);
        
        g2.setStroke(s);


    }

    
    /**
     * Render the left hand crank.
     * @param g2 The graphics object to render to.
     */    
    public void renderLeft(Graphics2D g2) {

        BasicStroke ns = new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        Stroke s = g2.getStroke();
         g2.setStroke(ns);
         
        g2.draw(_geomCrankLeft);

        g2.setStroke(s);

    }

    /**
     * Get the Right Pedal Point for the Cranks.
     *
     * @return Point2D Pedal Point for Foot Contact
     */
    public Point2D getCrankPointRight() {
        return new Point2D.Double(_crankPointRight.getX(), _crankPointRight.getY());
    }

    /**
     * Get the Left Pedal Point for the Cranks.
     *
     * @return Point2D Pedal Point for Foot Contact
     */
    public Point2D getCrankPointLeft() {
        return new Point2D.Double(_crankPointLeft.getX(), _crankPointLeft.getY());
    }
    
    


}
