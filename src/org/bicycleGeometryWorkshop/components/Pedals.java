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

import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.attributes.DoubleAttribute;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * The pedals component of the bicycle.  The pedals connect at the cranks and 
 * define the foot connection points of the rider.
 * @author Tom
 */
public class Pedals extends BaseComponent {

    private DoubleAttribute _attHeight;
    
    private DoubleAttribute _attOffset;

    private double _pedalRotation;

    private double _pedalWidth;

    private Point2D _rightCrankPoint;
    private Point2D _leftCrankPoint;

    private Point2D _rightPedalPoint;
    private Point2D _leftPedalPoint;

    
    private Line2D _geomPedalRight;
    private Line2D _geomPedalLeft;    
    
    /**
     * Class constructor.  Construct the class with the component owner.
     * @param owner The component owner.
     */
    public Pedals(ComponentOwner owner) {
        super(DataBaseKeys.PEDALS.toString(), owner);

        setIcon("PedalsIcon16.png");
        
        _attHeight = addLengthAttribute("Height", 15, 0, 500, "This is the height from the axle centerline to the bottom of the foot (bearing surface).");
        
        _attOffset =  addLengthAttribute("Offset", 0, -500, 500, "This is the forward or backwards mounting offset in relation to the axle centerline.");
        
   

        //ankle rotation / heel pitch up
        _pedalRotation = 0;
                
        _pedalWidth = 75;

        _rightCrankPoint = new Point2D.Double(0, 0);
        _leftCrankPoint = new Point2D.Double(0, 0);

        _rightPedalPoint = new Point2D.Double(0, 0);
        _leftPedalPoint = new Point2D.Double(0, 0);
        
        _geomPedalRight =  addLine2D();
        _geomPedalLeft =  addLine2D();   
        
        update();

    }

 
    
    /**
     * Set the Crank Points - these are the output points from the cranks.
     * @param rightPoint  The right hand crank center point.
     * @param leftPoint  The left hand crank center point.
     * @param rotation The pedal rotation from the pose.
     */
    public void updatePedals(Point2D rightPoint, Point2D leftPoint, double rotation ) {

        _rightCrankPoint.setLocation(rightPoint);
        _leftCrankPoint.setLocation(leftPoint);
        
         _pedalRotation = rotation;
        
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

    
        double platformHeight = _attHeight.getDoubleValue();
        double offset = _attOffset.getDoubleValue();

        double HPi = Math.PI / 2;

        //pedal rotation
        double pedalTheta = Utilities.degreesToRadians(_pedalRotation);
        //ankle angle: 90 degees rotated back towards 0
        double thetaAnkle = HPi - pedalTheta;
        

        //center points of pedals
        Point2D cenPntRight = Utilities.polarPoint(_rightCrankPoint, platformHeight, thetaAnkle);
        Point2D cenPntLeft = Utilities.polarPoint(_leftCrankPoint, platformHeight, thetaAnkle);
        
        
        //pedal platform centers
        Point2D connPntRight = Utilities.polarPoint(cenPntRight, offset, thetaAnkle - HPi);
        Point2D connPntLeft = Utilities.polarPoint(cenPntLeft, offset, thetaAnkle - HPi);
        
        //pedal points
        _rightPedalPoint.setLocation(connPntRight);
        _leftPedalPoint.setLocation(connPntLeft);        
        
        //pedal geometry
        double phw = _pedalWidth / 2;
        
        //pedal line points start/end
        Point2D pedRS = Utilities.polarPoint(cenPntRight, phw, thetaAnkle + HPi);
        Point2D pedRE = Utilities.polarPoint(cenPntRight, phw, thetaAnkle - HPi);
        
        Point2D pedLS = Utilities.polarPoint(cenPntLeft, phw, thetaAnkle + HPi);
        Point2D pedLE = Utilities.polarPoint(cenPntLeft, phw, thetaAnkle - HPi);        
        
        //update geometry
 
        _geomPedalRight.setLine(pedRS, pedRE);
        _geomPedalLeft.setLine(pedLS, pedLE);
        
        
    }

    /**
     * Get the Right Pedal Point for the Cranks.
     *
     * @return The pedal point.
     */
    public Point2D getPedalPointRight() {
        
        return new Point2D.Double(_rightPedalPoint.getX(), _rightPedalPoint.getY());
    }

    /**
     * Get the Left Pedal Point for the Cranks.
     *
     * @return The pedal point.
     */
    public Point2D getPedalPointLeft() {
        return new Point2D.Double(_leftPedalPoint.getX(), _leftPedalPoint.getY());
    }

   
    /**
     * Render the right pedal.
     * @param g2 The graphics object to render to.
     */    
    public void renderRight(Graphics2D g2) {

        g2.draw(_geomPedalRight);

    }

    /**
     * Render the left pedal.
     * @param g2 The graphics object to render to.
     */
    public void renderLeft(Graphics2D g2) {

        g2.draw(_geomPedalLeft);


    }    
    
 

}
