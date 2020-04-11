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
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;
import org.bicycleGeometryWorkshop.geometry.IntersectionPoint;
import org.bicycleGeometryWorkshop.geometry.IntersectionPointResult;

/**
 * The frame component of the bicycle.  This emulates a standard diamond frame bicycle frame.   This is the core component that most all other components attach to.  
 * Since this is meant to be entered from exiting manufacturers specs, the bare minimum is used to calculate the frame.
 * It is constructed from the rear forward using the chainstay and bottom bracket drop as a base for the drop and reach.  
 * The head tube is projected down and the fork length is projected as well (taking into account rake) to get to the front wheel.
 * 
 * 
 * @author Tom
 */
public class FrameSet extends BaseComponent {

    private static final double BB_OUTER_DIA = 40; //34.8;
    private static final double BB_INNER_DIA = 34.8;
    private static final double AXLE_DIA = 15;
    private static final double LUG_DIA = 30;
    

    
    private DoubleAttribute _attChainstay;
    private DoubleAttribute _attBottomBracketDrop;
    
    private DoubleAttribute _attHeadTube;

    private DoubleAttribute _attHeadSetBottom;
    
    private DoubleAttribute _attTopTubeOffset;
    private DoubleAttribute _attBottomTubeOffset;
    

    private DoubleAttribute _attForkRake;

    
    private DoubleAttribute _attStack;
    private DoubleAttribute _attReach;

    private DoubleAttribute _attSeatTubeTop;
    private DoubleAttribute _attSeatTubeCenter;

    private DoubleAttribute _attHeadTubeAngle;
    private DoubleAttribute _attSeatTubeAngle;
    

    
    private double _wheelBase;
    private double _frontCenter;
    private double _forkLength;
    

    private Point2D _stemPoint;
    private Point2D _seatPoint;
    private Point2D _bottomBracketPoint;

    private Line2D _geomFork;
    private Line2D _geomHeadTube;

    private Line2D _geomTopTube;
    private Line2D _geomBottomTube;
    private Line2D _geomSeatTube;
    private Line2D _geomChainstay;
    private Line2D _geomSeatstay;
    
    private ArrayList<Shape> _shapes;


    /**
     * *
     * Class Constructor.  Initialize the class with the owner.
     * @param owner The owner of the component.
     */
    public FrameSet(ComponentOwner owner) {
        super(DataBaseKeys.FRAMESET.toString(), owner);

        setIcon("FrameIcon16.png");
        
        double min = 1;
        double max = 2000;
        

       _attChainstay = addLengthAttribute("Chainstay", 392, min, max, "The length of the chainstay.");
     
       
        _attBottomBracketDrop = addLengthAttribute("BottomBracketDrop", 55, 0, max, "The distance the bottom bracket drops below the wheels center line.");
        
        _attStack = addLengthAttribute("Stack", 542, min, max, "Vertical distance from bottom bracket to the top of the head tube.");
        _attReach = addLengthAttribute("Reach", 400, min, max, "Horizontal distance from the bottom bracket to the top of the head tube.");
        
        
        _attSeatTubeAngle = addAngleAttribute("SeatTubeAngle", 74.4, 0 , 180, "The angle of the seat tube (clockwise from horizontal");
        
        _attSeatTubeTop = addLengthAttribute("SeatTubeTop", 530, min, max, "The distance from the bottom bracket to the top of the seat tube.");
        _attSeatTubeCenter = addLengthAttribute("SeatTubeCenter", 490, min, max, "The distance from the bottom bracket to where the top tube intersects the seat tube.");

        //if this is horizontal - some methods will fail... min > 0 & max < 180
        _attHeadTubeAngle = addAngleAttribute("HeadTubeAngle", 73.5, 5, 175, "The angle of the head tube (clockwise from horizontal).");
        
       _attHeadTube = addLengthAttribute("HeadTube", 150, min, max, "The length of the head tube.");
        //_attHeadSetTop = addLengthAttribute("HeadSetTop", 0, 0, max, "Height of the top headset.");
        _attHeadSetBottom = addLengthAttribute("HeadSetBottom", 0, 0, max, "Height of bottom headset between fork and heaset tube.");
        
        _attTopTubeOffset = addLengthAttribute("TopTubeOffset", 125, 0, max, "Offset from bottom of Head Tube to center line of Top Tube");
        _attBottomTubeOffset = addLengthAttribute("BottomTubeOffset", 65, 0, max, "Offset from bottom of Head Tube to center line of Bottom Tube.");
        

        _attForkRake = addLengthAttribute("ForkRake", 35, min, max, "The distance of the axle offset from the line of the headtube");        
        
        //wheelbase
        _wheelBase = 0;
        _frontCenter = 0 ;
        _forkLength = 0;
 

      
        
        //geometry
        //stem connection point
        _stemPoint = new Point2D.Double(0, 0);
        //seat connection point
        _seatPoint = new Point2D.Double(0, 0);
        _bottomBracketPoint = new Point2D.Double(0, 0);

        //intialize geometry variables - broken out for convience
        _geomFork = addLine2D();
        _geomHeadTube = addLine2D();

        _geomTopTube = addLine2D();
        _geomBottomTube = addLine2D();
        _geomSeatTube = addLine2D();
        _geomChainstay = addLine2D();
        _geomSeatstay = addLine2D();

//        _controlFrame = new ControlFrame();
        
        //testing
        _shapes = new ArrayList();
        
        
        //do initial run
        update();

    }

  


    /**
     * Wheel Base of the frame - distance between the wheel centers.
     *
     * @return The wheel base distance.
     */
    public double getWheelBase() {
        return _wheelBase; //_attWheelBase.getDoubleValue(); 
    }




    /**
     * Head Tube Angle
     *
     * @return The Head Tube Angle.
     */
    public double getHeadTubeAngle() {

        return _attHeadTubeAngle.getDoubleValue(); //_inputHeadTubeAngle;

    }



    /**
     * Seat Tube Angle.
     *
     * @return The Seat Tube angle
     */
    public double getSeatTubeAngle() {

        return _attSeatTubeAngle.getDoubleValue(); //_inputSeatTubeAngle;

    }

    /**
     * Get the Stem point.  This is the connection point for the stem.
     * @return The connection point for the stem.
     */
    public Point2D getStemPoint() {
        return new Point2D.Double(_stemPoint.getX(), _stemPoint.getY());
    }

    
    /**
     * Get the Seat Point.  this is the connection point for the seat post.
     * @return The connection point for the seat post.
     */
    public Point2D getSeatPoint() {
        return new Point2D.Double(_seatPoint.getX(), _seatPoint.getY());
    }

    /**
     * Get the bottom bracket point.  This is the connection piint for the cranks.
     * @return The connection point for the cranks.
     */
    public Point2D getBottomBracketPoint() {
        return new Point2D.Double(_bottomBracketPoint.getX(), _bottomBracketPoint.getY());
    }

  
    /**
     * Report Methods
     * 
     */
    
    
    /**
     * Front center measurement.
     * @return THe front center measurement.
     */
    public double getFrontCenter() {
        
        return _frontCenter;
        
    }
    
    /**
     * Fork length measurement.
     * @return The fork length measurement.
     */
    public double getForkLength() {
        
        return _forkLength;
    }
    
    
    /**
     * Get the fork rake.
     * @return The fork rake distance.
     */
    public double getForkRake() {
        return _attForkRake.getDoubleValue();
    }
   
    /**
     * New updateGeometry
     */
    @Override
    public void updateGeometry() {
        

        //set base point for all methods
        Point2D basePoint = getBasePoint();


        //calc bottom bracket location
        double bttmBrktDrop = _attBottomBracketDrop.getDoubleValue();
        double chainstay = _attChainstay.getDoubleValue();
        double reach = _attReach.getDoubleValue(); 
        double stack = _attStack.getDoubleValue(); 
        
        double forkRake = _attForkRake.getDoubleValue();
        double headAng = _attHeadTubeAngle.getDoubleValue();        
        
        //headtube section
        double headTube = _attHeadTube.getDoubleValue();
        double headSB = _attHeadSetBottom.getDoubleValue();
        //headset tube offsets
        double ttOffset = _attTopTubeOffset.getDoubleValue();
        double btOffset = _attBottomTubeOffset.getDoubleValue();        
       
        
        /**
         * Bottom Bracket
         */
        //calculate 
        //a2 + b2 = c2 ~ a2 = c2 - b2
        double bb2 = bttmBrktDrop * bttmBrktDrop;
        double cs2 = chainstay * chainstay;
      
        // horizontal length of chainstay (delta x)
        double csHoriz = Math.sqrt(cs2 - bb2);
  
        //bottom bracket control point
        Point2D cpBottomBracket =  Utilities.translatePoint(basePoint, csHoriz, -bttmBrktDrop);
        
        /*** set the Bottom Bracket Point ***/
        _bottomBracketPoint.setLocation(cpBottomBracket);
        

        //set chainstay line
        _geomChainstay.setLine(cpBottomBracket, basePoint);

        /**
         * HeadTube
         */
        //headTube top center point
        Point2D cpHeadTubeTop =  Utilities.translatePoint(cpBottomBracket, reach, stack);
        //head tube angle pointing towards ground
        double htTheta = -Utilities.degreesToRadians(headAng);
        //total headset length
        double hsLength = headTube +  headSB;         
        //head tube bottom
        Point2D cpHeadTubeBottom = Utilities.polarPoint(cpHeadTubeTop, hsLength, htTheta);
        //these are specified from the bottom up
        double topTubeOS = headTube - ttOffset;
        double bottomTubeOS = headTube - btOffset;
        //tube points at headset
        Point2D cpTopTubeEP = Utilities.polarPoint(cpHeadTubeTop, topTubeOS, htTheta);
        Point2D cpBttomTubeEP = Utilities.polarPoint(cpHeadTubeTop, bottomTubeOS, htTheta);
        
        //head tue line
        //this should be from ground up (start to end)     
        _geomHeadTube.setLine(cpHeadTubeBottom, cpHeadTubeTop);
        
        /*** set the stem point ***/
        _stemPoint.setLocation(cpHeadTubeTop);        
        
        /**
         * Fork
         */
        
        //rotate head angle positive 90 degrees
        double rakeThetaPos = htTheta + Math.PI / 2;
        //rake points - define an offset line rom headtube
        Point2D rakePnt1 = Utilities.polarPoint(cpHeadTubeTop, forkRake, rakeThetaPos);
        Point2D rakePnt2 = Utilities.polarPoint(cpHeadTubeBottom, forkRake, rakeThetaPos);
        
        //point horizontal from base point (for intersection below)
        Point2D cpHorizProj = Utilities.translatePoint(basePoint, 100, 0);
        //prepare front wheel point
        Point2D cpFrontWheel = new Point2D.Double();
        
        //calc intersection
        IntersectionPoint ip = Utilities.lineLineIntersect(basePoint, cpHorizProj, rakePnt1, rakePnt2);
        
        //get intersection of horizontal line with rake line - this is the front wheel point
        if(ip.result() == IntersectionPointResult.ONE_POINT) {
            cpFrontWheel = ip.getIntersection1();
        } else {
            //crap.... shouldn't happen... 
            //make sure head tube is never parallel to ground
         }
        
        /***  set the wheelbase ***/
        _wheelBase = basePoint.distance(cpFrontWheel);  
        
        /*** set front center ***/
        _frontCenter =  cpBottomBracket.distance(cpFrontWheel);  
        
        //rotate head angle positive 90 degrees
        double rakeThetaNeg = htTheta - Math.PI / 2;
        //project rake back from front wheel to get projected end of fork
        Point2D cpForkProjection = Utilities.polarPoint(cpFrontWheel, forkRake, rakeThetaNeg);
        
        //get the fork length
        _forkLength = cpHeadTubeBottom.distance(cpForkProjection);

        //this should be from ground up (start to end)
        _geomFork.setLine(cpFrontWheel, cpHeadTubeBottom);
        


        /**
         * Seat Post, Top Tube, Bottom Tube, and Seat stay.
         */
        double stTop = _attSeatTubeTop.getDoubleValue();
        double stCen = _attSeatTubeCenter.getDoubleValue();
        double stAng = _attSeatTubeAngle.getDoubleValue();

        //seat tube angle
        double seatTubeAngle = Math.PI - Utilities.degreesToRadians(stAng);

        //seat post tube points
        Point2D cpSeatPostTop =  Utilities.polarPoint(cpBottomBracket , stTop, seatTubeAngle);        
        Point2D cpSeatPostCenter =  Utilities.polarPoint(cpBottomBracket , stCen, seatTubeAngle);
        
        //update the frame
//        frame.SeatPost.setLine(cpBottomBracket, cpSeatPostTop);
        _geomSeatTube.setLine(cpBottomBracket, cpSeatPostTop);
        
         /*** set the seat point ***/
        _seatPoint.setLocation(cpSeatPostTop);
        
        //seat stay line
        _geomSeatstay.setLine(basePoint, cpSeatPostCenter);
        
        //top tube and bottom tube
        _geomTopTube.setLine(cpSeatPostCenter, cpTopTubeEP);
        _geomBottomTube.setLine(cpBottomBracket,cpBttomTubeEP ); 
     
       
        //build the shapes
        buildShapes();
        
    }
    
    
    /**
     * Build the frame geometry (shapes).
     */
    private void buildShapes() {
        
        //clear shapes
        _shapes.clear();
        
        double bbDia = BB_OUTER_DIA;
        double bbRad = bbDia / 2;
        
        double bbInDia = BB_INNER_DIA;
        double bbInRad = bbInDia / 2;
        
        double boltDia = 10;
        double boltRad = boltDia / 2;
        
        double lugDia = 30;
        double lugRad = lugDia / 2;
        
        //head tube diameter
        double htDia =32;
        double htRad = htDia / 2;
        
        //seat tube diameter
        double stDia =27;
        double stRad = stDia / 2;
        
        //top tube
        double ttDia = 25;
        double ttRad = ttDia / 2;
        
        //bottom tube
        double btDia = 27;
        double btRad = btDia / 2;
        
        //chainstay
        double ctDia = 20;
        double ctRad = ctDia / 2;
        double ctDia2 = 12;
        double ctRad2 = ctDia2 / 2;
        
        //wheel points
        Point2D rwPoint = getBasePoint();
        Point2D fwPoint = Utilities.translatePoint(rwPoint, _wheelBase, 0);
                
        
        /*** Head Tube **/
        //head tube offset lines
        Line2D headTubeLine = new Line2D.Double(_geomHeadTube.getP1(), _geomHeadTube.getP2());
        double hsSpace = _attHeadSetBottom.getDoubleValue();
        headTubeLine = Utilities.lengthenLine(_geomHeadTube.getP1(), _geomHeadTube.getP2(), -hsSpace, true);
        
        Line2D headTubeL = Utilities.offsetLine(headTubeLine,  htRad, htRad,  true);
        Line2D headTubeR = Utilities.offsetLine(headTubeLine,  htRad, htRad, false);
        
        //init path
        Path2D htPath = makeShapeFromLines(headTubeR, headTubeL);
        
        //add head tube shape
        _shapes.add(htPath);
        
        
         /*** Seat Post Tube ***/
        //seat tube offset lines
        Line2D seatTubeL = Utilities.offsetLine(_geomSeatTube,  stRad,stRad,  true);
        Line2D seatTubeR = Utilities.offsetLine(_geomSeatTube,  stRad, stRad, false);  

       
        //generate the path
        Path2D spPath = makeShapeFromLines(seatTubeR, seatTubeL);        
        Shape spShape  = subtractCircleFromShape(spPath, _bottomBracketPoint, bbRad);
        _shapes.add(spShape);        
       
        
         /***  Top Tube  ***/
        //top tube lines
        Line2D topTubeL = Utilities.offsetLine(_geomTopTube,  ttRad, ttRad, true);
        Line2D topTubeR = Utilities.offsetLine(_geomTopTube,  ttRad, ttRad, false);

        //trim top tube left line 
        topTubeL = Utilities.trimLineToLine(topTubeL, headTubeL, true);
        topTubeL = Utilities.trimLineToLine(topTubeL, seatTubeR, false);
        
        //trim top tube right line 
        topTubeR = Utilities.trimLineToLine(topTubeR, headTubeL, true);
        topTubeR = Utilities.trimLineToLine(topTubeR, seatTubeR, false);  
        
        //top tube path
        Path2D ttPath = makeShapeFromLines(topTubeR, topTubeL);
        //add top tube shape
        _shapes.add(ttPath);        
        
        
        /***  Bottom Tube ***/
        Line2D bottomTubeL = Utilities.offsetLine(_geomBottomTube,  btRad, btRad,  true);
        Line2D bottomTubeR = Utilities.offsetLine(_geomBottomTube,  btRad, btRad,  false);        
        
        //trim bottom tube lines
        bottomTubeL = Utilities.trimLineToLine(bottomTubeL, headTubeL, true);
        bottomTubeR = Utilities.trimLineToLine(bottomTubeR, headTubeL, true);
        
        //trim to bottom bracket
        bottomTubeL = Utilities.trimLineToCircle(bottomTubeL, _bottomBracketPoint, bbRad, false);
        bottomTubeR = Utilities.trimLineToCircle(bottomTubeR, _bottomBracketPoint, bbRad, false);
        
        Path2D btPath = makeShapeFromLines(bottomTubeR, bottomTubeL);
        Shape btShape  = subtractCircleFromShape(btPath, _bottomBracketPoint, bbRad);
        
        _shapes.add(btShape);
        
        /*** Chainstay Tube ***/
        Line2D chainstayTubeL = Utilities.offsetLine(_geomChainstay,  ctRad, ctRad2,  true);
        Line2D chainstayTubeR = Utilities.offsetLine(_geomChainstay,  ctRad, ctRad2,  false); 
               
        
        Path2D ctPath = makeShapeFromLines(chainstayTubeR, chainstayTubeL);
        Shape ctShape  = subtractCircleFromShape(ctPath, _bottomBracketPoint, bbRad);
        ctShape  = subtractCircleFromShape(ctShape, rwPoint, lugRad);
        _shapes.add(ctShape);
        
        
        /*** Seatstay line ***/
        Line2D seatstayTubeL = Utilities.offsetLine(_geomSeatstay,  ctRad2, ctRad,  true);
        Line2D seatstayTubeR = Utilities.offsetLine(_geomSeatstay,  ctRad2, ctRad,  false);         

        seatstayTubeL = Utilities.trimLineToLine(seatstayTubeL, seatTubeL, true);
        seatstayTubeR = Utilities.trimLineToLine(seatstayTubeR, seatTubeL, true);         
        
        Path2D seatStayPath = makeShapeFromLines(seatstayTubeR, seatstayTubeL);
        Shape seatStayShape  = subtractCircleFromShape(seatStayPath, rwPoint, lugRad);
        _shapes.add(seatStayShape);
        
        
        /*** Fork Shape ***/
        
        //head tube lines using full headtube line - includes spacer
        Line2D htL = Utilities.offsetLine(_geomHeadTube,  htRad, htRad,  true);
        Line2D htR = Utilities.offsetLine(_geomHeadTube,  htRad, htRad, false);        
        
        //lines parallel to fork line
        Line2D flL = Utilities.offsetLine(_geomFork,  ctRad2, ctRad2,  true);
        Line2D flR = Utilities.offsetLine(_geomFork,  ctRad2, ctRad2, false);        
        
        //use start points of forks lines and the start points of 
        //head tube lines as endpoints to create tapered fork
        Line2D forkLineL = new Line2D.Double(flL.getP1(), htL.getP1() );
        Line2D forkLineR = new Line2D.Double(flR.getP1(), htR.getP1() );     
        
         Path2D forkPath = makeShapeFromLines(forkLineR, forkLineL);
         Shape forkShape  = subtractCircleFromShape(forkPath, fwPoint, lugRad);
         _shapes.add(forkShape);
        
        /*** Bottom Bracket and lugs donuts ***/
        Shape bbShape = makeDonut(_bottomBracketPoint, bbRad, bbInRad);
        Shape fLug = makeDonut(fwPoint, lugRad, boltRad);
        Shape rLug = makeDonut(rwPoint, lugRad, boltRad);
        
        _shapes.add(bbShape);
        _shapes.add(fLug);
        _shapes.add(rLug);
        
    }
    
    /**
     * Make a close path from two lines that define edges of a rectangular shape.  The lines are assumed
     * to be pointing in the same direction and parallel.  This method is used to make tube shapes.
     * 
     * @param a First line or edge..
     * @param b  Second line or edge.
     * @return The closed shape produced by the edge lines.
     */
    private Path2D makeShapeFromLines(Line2D a, Line2D b) {
        
        
        //init path
        Path2D.Double path = new Path2D.Double();
        
        //start on right side
        path.moveTo(a.getX1(), a.getY1());
        //line to second point
        path.lineTo(a.getX2(), a.getY2());
        //line to end point left side
        path.lineTo(b.getX2(), b.getY2());
        //line to left start point
         path.lineTo(b.getX1(), b.getY1());
        path.closePath();        
        
        return path;
        
    }
    
    /**
     * Remove a circle from a shape.
     * @param path  The path to remove the circle from.
     * @param center The center of the circle.
     * @param radius The radius of the circle.
     * @return The shape with the circle removed.
     */
    private Shape subtractCircleFromShape(Shape path, Point2D center, double radius) {
        
         Ellipse2D ellipse = new Ellipse2D.Double();
         ellipse.setFrameFromCenter(center.getX(), center.getY(), center.getX() - radius, center.getY() - radius);
         
         Area areaA = new Area(path);
         Area areab = new Area(ellipse);
         areaA.subtract(areab);
         
         //path.append(arc, true);
         
        return areaA;        

    }

    /**
     * Creates a donut shape - used for the bottom bracket and "Lugs".
     * @param ca  Center of donut.
     * @param ra  Radius A - this needs to be large than radius b.
     * @param rb  radius B - this needs to be smaller than radius a.
     * @return The donut shape.
     */
    private Shape makeDonut(Point2D ca, double ra, double rb) {
        
         Ellipse2D ea = new Ellipse2D.Double();
         ea.setFrameFromCenter(ca.getX(), ca.getY(), ca.getX() - ra, ca.getY() - ra);        
        
         Ellipse2D eb = new Ellipse2D.Double();
         eb.setFrameFromCenter(ca.getX(), ca.getY(), ca.getX() - rb, ca.getY() - rb);
         
         Area areaA = new Area(ea);
         Area areab = new Area(eb);
         areaA.subtract(areab);
 
         
        return areaA;        

    }    
    
    
    
    /**
     * Calculate the raw bottom bracket offset - no base point
     * is used - the offset is from 0,0.
     * @return The bottom bracket offset point.
     */
    public Point2D getBottomBracketOffset() {
        
        double bttmBrktDrop = _attBottomBracketDrop.getDoubleValue();
        double chainstay = _attChainstay.getDoubleValue();        
        
        //calculate 
        //a2 + b2 = c2 ~ a2 = c2 - b2
        double bb2 = bttmBrktDrop * bttmBrktDrop;
        double cs2 = chainstay * chainstay;
      
        // horizontal length of chainstay (delta x)
        double csHoriz = Math.sqrt(cs2 - bb2);
  
        //bottom bracket control point
        return new Point2D.Double( csHoriz, -bttmBrktDrop);        
        
    }
    

    /**
     * Render the Frame Geometry
     * @param g2 Graphics object to render to.
     */
    public void render(Graphics2D g2) {


        double ad = AXLE_DIA;
        double ar = AXLE_DIA / 2;
        
        //testing shapes
        for(Shape s : _shapes) {
            g2.fill(s);
        }        

    }


    

    /**
     * Do some selective rendering on the geometry center lines.
     * @param g2 The graphics object to render to.
     */
    public void renderLines(Graphics2D g2) {
        
        
        g2.draw(_geomFork);
//        g2.draw(_geomHeadTube);

        g2.draw(_geomTopTube);
        g2.draw(_geomBottomTube);
//        g2.draw(_geomSeatTube );
        g2.draw(_geomChainstay);
        g2.draw(_geomSeatstay);        
        
        
        
    }

}//end class
