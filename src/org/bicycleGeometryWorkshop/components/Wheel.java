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

import org.bicycleGeometryWorkshop.attributes.DoubleAttribute;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * The Wheel component of the bicycle.  This class manages the geometry and graphics for both bicycle wheels.  
 * The wheels attach to the frame at the rear and front wheel centers defined by the frame.
 * @author Tom
 */
public class Wheel extends BaseComponent {

    private static double HUB_DIA = 70;

    private DoubleAttribute _attWheelDiameter;


    private Ellipse2D _geomTireFront;
    private Ellipse2D _geomHubFront;
    
    private Ellipse2D _geomTireRear;
    private Ellipse2D _geomHubRear;    
 
    
    
     private double _wheelBase;
    
    private double _hubDiameter;
    


    /**
     * Class constructor.  Construct the wheels with the component owner.
     * @param owner The component owner.
     */
    public Wheel(ComponentOwner owner) {
        super(DataBaseKeys.WHEELS.toString(), owner);

        setIcon("WheelsIcon16.png");
        
         _attWheelDiameter = addLengthAttribute("Diameter", 700,1,5000, "The diameter of the wheel.");

        _wheelBase = 1000;
        _hubDiameter = HUB_DIA;

        //geometry
        _geomTireFront = addEllipse2D();
        _geomHubFront = addEllipse2D();
        _geomTireRear = addEllipse2D();
        _geomHubRear = addEllipse2D();
        

        update();

    }
    
    
    /**
     * Update the wheels, calls update on the component.
     * @param basePoint  Base point for the Wheels.
     * @param wheelBase The wheel base of the bicycle (distance between wheels).
     */
    public void updateWheel(Point2D basePoint, double wheelBase) {
        
        setBasePoint(basePoint);
        _wheelBase = wheelBase;
        update();
        
    }


    /**
     * Used by analysis.
     *
     * @return The wheel radius
     */
    public double getWheelRadius() {
        return _attWheelDiameter.getDoubleValue() / 2;
    }

    
    /**
     * Update the geometry.
     */
    @Override
    public void updateGeometry() {

        Point2D basePoint = getBasePoint();

        
        double rwCenX = basePoint.getX();
        double rwCenY = basePoint.getY();
        
        double fwCenX = rwCenX + _wheelBase;
        double fwCenY = rwCenY ;
        

        double wheelDiameter = _attWheelDiameter.getDoubleValue();
        
        double wr = wheelDiameter / 2;

        double hr = _hubDiameter / 2;

        //rear tire and hub
        _geomTireRear.setFrame(rwCenX - wr, rwCenY - wr, wheelDiameter, wheelDiameter);
        _geomHubRear.setFrame(rwCenX - hr, rwCenY - hr, _hubDiameter, _hubDiameter);
        

        //front tire and hub
        _geomTireFront.setFrame(fwCenX - wr , fwCenY - wr, wheelDiameter, wheelDiameter);
        _geomHubFront.setFrame(fwCenX - hr , fwCenY - hr, _hubDiameter, _hubDiameter);      
        
        

    }

    /**
     * Render the wheels.
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {
        
  
        g2.draw(_geomTireFront);
        g2.draw(_geomTireRear);
        
        g2.draw(_geomHubFront);
        g2.draw(_geomHubRear);     
          
  
    }
    
 

}
