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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;
import org.bicycleGeometryWorkshop.geometry.Utilities;

/**
 * The saddle component of the bicycle.  The saddle attaches at the seat post mounting point.  
 * It also controls the sit point for the rider.
 * @author Tom
 */
public class Saddle extends BaseComponent {

    private DoubleAttribute _attSaddleMountOffset;
    private DoubleAttribute _attSitPointOffset;

    private DoubleAttribute _attSaddleLengthFront;
    private DoubleAttribute _attSaddleLengthRear;
    private DoubleAttribute _attRailLength;
    private DoubleAttribute _attRailDepth;
    
    private DoubleAttribute _attMountAngle;

    private Line2D _geomSaddleLine;
    private Line2D _geomRailLine;
    private Line2D _geomRailD1;
    private Line2D _geomRailD2;

    private Point2D _sitPoint;

    
    /**
     * Class constructor.  Construct the class with the owner.
     * @param owner The component owner.
     */
    public Saddle(ComponentOwner owner) {
        super(DataBaseKeys.SADDLE.toString(), owner);

        setIcon("SaddleIcon16.png");
        
        _sitPoint = new Point2D.Double(0, 0);

        double rng = 5000;

        
        _attMountAngle = addAngleAttribute("MountAngle", 0, -30,30, "The mounting angle is the tilt of the saddle.");
        _attSaddleMountOffset = addLengthAttribute("MountOffset", 0, -rng, rng, "The mounting offset is the horizontal position of the rails.");
        _attSitPointOffset = addLengthAttribute("SitPointOffset", -25, -rng, rng, "Where the rider contacts the saddle (calculated from the center of saddle).");

        _attSaddleLengthFront = addLengthAttribute("LengthFront", 150, 0, rng, "The length of the saddle from center to front.");
        _attSaddleLengthRear = addLengthAttribute("LengthRear", 120, 0, rng, "The length of the saddle from center to rear.");
        _attRailLength = addLengthAttribute("RailLength", 50, 1, rng, "The length  of the rail (amount of horizontal adjustment)");
        _attRailDepth = addLengthAttribute("RailDepth", 50, 0, rng, "The rail depth controls the height of the saddle from it's mount point.");
       

        _geomSaddleLine = addLine2D(); //new Line2D.Double(0,0,1,0);
        _geomRailLine = addLine2D(); //new Line2D.Double(0,0,1,0);
        _geomRailD1 = addLine2D(); //new Line2D.Double(0,0,1,0);
        _geomRailD2 = addLine2D(); //new Line2D.Double(0,0,1,0);   

  
        update();

    }

    /**
     * Update the saddle with the base point (connection point).
     * @param basePoint The base point (connection point).
     */
    public void updateSaddle(Point2D basePoint) {
        
        setBasePoint(basePoint);
        update();
    }
    
    /**
     * Update the geometry.
     */
    @Override
    public void updateGeometry() {

        //base point - SeatPostPoint from frame definition
        Point2D basePoint = getBasePoint();
        double x = basePoint.getX();
        double y = basePoint.getY();
        
        double mountAngle = _attMountAngle.getDoubleValue();
        double mTheta = Utilities.degreesToRadians(mountAngle);
        double mTheta90 = mTheta + (Math.PI / 2);        

        double r45 = Math.PI / 4;
        
        double saddleMountOffset = _attSaddleMountOffset.getDoubleValue();
        double sitPointOffset = _attSitPointOffset.getDoubleValue();

        double saddleLengthFront = _attSaddleLengthFront.getDoubleValue();
        double saddleLengthRear = _attSaddleLengthRear.getDoubleValue();

        double railLength = _attRailLength.getDoubleValue();
        double railDepth = _attRailDepth.getDoubleValue();
        double railHalfLen = railLength / 2;

        //project mount point from base point: mount offset @ mount angle (tilt)
        Point2D mountPoint = Utilities.polarPoint(basePoint, saddleMountOffset, mTheta);
        //center of top of saddle - top line layout
        Point2D topCenter = Utilities.polarPoint(mountPoint, railDepth, mTheta90);
        
        //rail start and ends
        Point2D railStart = Utilities.polarPoint(mountPoint, -railHalfLen, mTheta);
        Point2D railEnd = Utilities.polarPoint(mountPoint, railHalfLen, mTheta);     
        
        //project rail diagonals (length = diagonal of equal sizes)
        double rsl = Math.sqrt(2) * railDepth;
        Point2D railMountStart = Utilities.polarPoint(railStart, rsl, mTheta90 + r45);
        Point2D railMountEnd = Utilities.polarPoint(railEnd, rsl, mTheta90 - r45);         
        
        //saddle points
        Point2D saddleStart = Utilities.polarPoint(topCenter, -saddleLengthRear, mTheta);
        Point2D saddleEnd = Utilities.polarPoint(topCenter, saddleLengthFront, mTheta);       
        
        //calc the sitpoint
        Point2D sitPointTemp = Utilities.polarPoint(topCenter, sitPointOffset, mTheta); 
        //update sitpoint
        _sitPoint.setLocation(sitPointTemp);
        

        //rail lines
        _geomRailLine.setLine(railStart, railEnd);
        _geomRailD1.setLine(railMountStart, railStart);
        _geomRailD2.setLine(railEnd, railMountEnd);

        _geomSaddleLine.setLine(saddleStart, saddleEnd);

    }


    
    /**
     * Render the geometry.
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {

        renderGeometry(g2);

    }

    /**
     * The Sit Point for Rider Contact at the Sit Point
     *
     * @return The Sit Point of the Rider.
     */
    public Point2D getSitPoint() {

        return _sitPoint;

    }



}//end class
