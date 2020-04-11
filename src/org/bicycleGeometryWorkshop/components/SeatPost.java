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
 * The seat post component of the bicycle.  This component connects at the seat post point at the frame and 
 * controls the extension and later offset of the saddle.
 * @author Tom
 */
public class SeatPost extends BaseComponent {

    //private DoubleAttribute _attSeatTubeAngle;
    private DoubleAttribute _attMountHeight;
    private DoubleAttribute _attMountOffset;

    private Line2D _geomSeatPost;
    private Ellipse2D _geomMountCenter;

    private double _seatTubeAngle;

    private Point2D _mountPoint;
    
    private Path2D _path;

    /**
     * Class constructor.  COnstruct the seat post with the component owner.
     * @param owner The owner of the seat post component.
     */    
    public SeatPost(ComponentOwner owner) {
        super(DataBaseKeys.SEATPOST.toString(), owner);

        setIcon("SeatpostIcon16.png");

        _mountPoint = new Point2D.Double(0, 0);

        _seatTubeAngle = 90;

        //_attSeatTubeAngle = addAttrDouble("SeatTubeAngle", 90);
        _attMountHeight = addLengthAttribute("Height", 120, 0, 1000, "The mounting height of the seat post (end of the seat tube to saddle rail centerline).");
        _attMountOffset = addLengthAttribute("Offset", -30, -500, 500, "The offset of the saddle mounting bracket");

        _geomSeatPost = addLine2D();
        _geomMountCenter = addEllipse2D();
        
        _path = new Path2D.Double();

        update();

    }

    /**
     * Update the seat post with the base point and the seat tube angle
     *
     * @param basePoint The base point of the Seat post (connection point).
     * @param seatTubeAngle The seat tube angle of the frame.
     */
    public void updateSeatPost(Point2D basePoint, double seatTubeAngle) {

        setBasePoint(basePoint);
        _seatTubeAngle = seatTubeAngle;
        update();
    }


    /**
     * Update the geometry.
     */    
    @Override
    public void updateGeometry() {

        //base point - SeatPostPoint from frame definition
        Point2D basePoint = getBasePoint();
        
        double seatTubeAngle = _seatTubeAngle; 
        double mountHeight = _attMountHeight.getDoubleValue();
        double mountOffset = _attMountOffset.getDoubleValue();
        
        double spTheta = Math.PI - Utilities.degreesToRadians(seatTubeAngle);
        double osTheta = spTheta - (Math.PI / 2);//90 degree turn for offset
        
        double tubeLength = mountHeight - 30;
        
        //control point - applied mount height
        Point2D ctrlPoint = Utilities.polarPoint(basePoint, mountHeight, spTheta);
        //end of seat post tube - held back for offset
        Point2D endPoint = Utilities.polarPoint(basePoint, tubeLength, spTheta);
        //mounting point - 90 degree turn and offset from control point
        Point2D mntPoint = Utilities.polarPoint(ctrlPoint, mountOffset, osTheta);
        
//        double projDist = mountHeight - Math.abs(mountOffset) / 2;
        
//        Point2D bendPoint = Utilities.polarPoint(basePoint, projDist, spTheta);
//        Point2D endPoint = Utilities.polarPoint(basePoint, mountHeight, spTheta);
//        endPoint = Utilities.translatePoint(endPoint, mountOffset, 0);
//        


        _path.reset();
        _path.moveTo(basePoint.getX(), basePoint.getY());
//        _path.lineTo(bendPoint.getX(), bendPoint.getY());
//        _path.lineTo(endPoint.getX(), endPoint.getY());
        _path.lineTo(endPoint.getX(), endPoint.getY());
        _path.lineTo(mntPoint.getX(), mntPoint.getY());
        
        //update the mounting pointfor the saddle.
        _mountPoint.setLocation(mntPoint);
        
        //mounting center mark
        double mc = 30;
        double hc = mc / 2;
        _geomMountCenter.setFrame(mntPoint.getX() - hc, mntPoint.getY() - hc, mc, mc);        
        
    }

    /**
     * Get the Mount Point for the Saddle.
     *
     * @return Point2D Center Point for Saddle Mounting
     */
    public Point2D getMountPoint() {
        return new Point2D.Double(_mountPoint.getX(), _mountPoint.getY());
    }

    /**
     * Render the SeatPost
     *
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {

        // renderGeometry(g2);
//        g2.draw(_geomSeatPost);
        g2.fill(_geomMountCenter);

        BasicStroke ns = new BasicStroke(20, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        Stroke s = g2.getStroke();
        
        g2.setStroke(ns);
        
        g2.draw(_path);
        
        g2.setStroke(s);

    }

}
