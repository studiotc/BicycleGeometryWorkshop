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

import org.bicycleGeometryWorkshop.report.ReportField;
import org.bicycleGeometryWorkshop.report.Report;
import org.bicycleGeometryWorkshop.geometry.IntersectionPoint;
import org.bicycleGeometryWorkshop.geometry.IntersectionPointResult;
import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.attributes.AttributeSet;
import org.bicycleGeometryWorkshop.attributes.BooleanAttribute;
import org.bicycleGeometryWorkshop.attributes.ColorAttribute;
import org.bicycleGeometryWorkshop.attributes.EnumAttribute;
import org.bicycleGeometryWorkshop.attributes.StringAttribute;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.bicycleGeometryWorkshop.app.VisualPreferences;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;
import org.bicycleGeometryWorkshop.attributes.AttributeChangeEvent;
import org.bicycleGeometryWorkshop.ui.Graphics;

/**
 *  Bicycle component - contains and manages all the sub components for a bicycle. 
 * This also maintains the rider geometry  and holds references to the rider measurements and pose 
 * for updating the rider geometry.
 * @author Tom
 */
public class Bicycle extends BaseComponent implements ComponentOwner {

    private FrameSet _frame;

    private Wheel _wheels;

    private Stem _stem;

    private SeatPost _seatPost;
    private Saddle _saddle;

    private Cranks _cranks;

    private Pedals _pedals;

    private HandleBar _handlebars;

    private Rider _rider;

    private boolean _isUpdating;

    private BicycleListener _bicycleListener;

  

    private RiderPose _pose;

    private VisualPreferences _visualPrefs;

    private AttributeSet _bicycleAttributes;
    private StringAttribute _bicycleNameAttr;

    private EnumAttribute _bicycleDisplay;

    private BooleanAttribute _bicycleVisible;

    private ColorAttribute _bicycleColor;
    private ColorAttribute _riderColor;
    private ColorAttribute _componentColor;

    private Point2D _outOfSaddlePoint;
    


    private BicycleAnalysisResult _analysis;
    private Report _report;

    private ArrayList<BaseComponent> _componentList;

    /**
     *
     * Class constructor.
     * 
     * @param name  The name of he bicycle
     * @param riderSize  A reference to the RiderMEasurements component.
     * @param pose A reference to the RiderPose component.
     * @param vPrefs The visual preference object to reference.
     */
    public Bicycle(String name, RiderMeasurements riderSize, RiderPose pose, VisualPreferences vPrefs) {
        super(DataBaseKeys.BICYCLE.toString(), null);
        //set the icon
        setIcon("BicycleIcon16.png");
        

        //get the pose
        _pose = pose; 

        //rider component - dependent on external componentChanged since it
        //has no internal attributes  to trigger componentChanged.
        _rider = new Rider(riderSize, pose);

        
        _visualPrefs = vPrefs;

        _bicycleAttributes = getAttributeSet(); //new AttributeSet(DataBaseKeys.BICYCLE.toString(), name, this);

        //get the name attribute
        _bicycleNameAttr = _bicycleAttributes.getNameAttribute();
        //disable notifications and set name - tries to fire events on null objects...
        _bicycleNameAttr.enableNotification(false);
        _bicycleNameAttr.setString(name);
        _bicycleNameAttr.enableNotification(true);


        //no need for a reference to these
        addStringAttribute("Manufacturer", "ACME", "The Bicycle manufacturer.");
        addStringAttribute("Model", "Model 1", "The Model of the Bicycle.");
        addStringAttribute("Size", "Medium", "The listed size of the Bicycle.");
        addStringAttribute("Note", "Note..", "Note on the Bicycle.");

        //display setting
//        _bicycleDisplay = addEnumAttribute("Display", BicycleDisplay.values(), BicycleDisplay.BicycleAndRider.name(), "Controls if Rider and Components are displayed.");
        _bicycleDisplay = addEnumAttribute("Display", BicycleDisplay.values(), BicycleDisplay.BicycleAndRider, "Controls if Rider and Components are displayed.");
        //visibitlity setting
        _bicycleVisible = addBooleanAttribute("Visible", true, "Sets the Bicycle's visibility.");

        //rider color
        _riderColor = addColorAttribute("RiderColor", new Color(255, 255, 0, 255), "Color used for the the Rider.");
        //frame color
        _bicycleColor = addColorAttribute("FrameColor", new Color(0, 60, 220, 255), "Color used for the Bicycle Frame and Forks.");
        //component color
        //_componentColor = addColorAttribute("ComponentColor", new Color(128, 128, 128, 255), "Color used for components.");
         _componentColor = addColorAttribute("ComponentColor", new Color(60, 60, 60, 255), "Color used for components.");


        _frame = new FrameSet(this);

        _wheels = new Wheel(this);

        _cranks = new Cranks(this);
        _pedals = new Pedals(this);

        _stem = new Stem(this);
        _handlebars = new HandleBar(this);

        _seatPost = new SeatPost(this);
        _saddle = new Saddle(this);

        //list of componenets used for ui loading
        _componentList = new ArrayList();

        _componentList.add(_frame);
        _componentList.add(_wheels);
        _componentList.add(_cranks);
        _componentList.add(_pedals);
        _componentList.add(_stem);
        _componentList.add(_handlebars);
        _componentList.add(_seatPost);
        _componentList.add(_saddle);

        //attribute set editor to edit database
        //_attrSetEditor = new AttributeSetEditor(name);
        _outOfSaddlePoint = new Point2D.Double(0, 0);

        _bicycleListener = null;

        _analysis = new BicycleAnalysisResult();

        _report = new Report();

       

        //load editor
        //_attrSetEditor.loadDataBase(_attrDB);
        _isUpdating = false;

        
        //do initial build
        update();

    }

    /**
     * Set the Bicycle Display mode (Rider and Bicycle, Bicycle, or Frame only).
     * @param display The display mode to set in bicycle.
     */
    public void setBicycleDisplay(BicycleDisplay display) {
        
        _bicycleDisplay.setEnum(display); //setFromString(display.name());
        
    }
 
    /**
     * Set the Bicycles Visibility.
     * @param visibile True to make visible, false to turn off.
     */
    public void setBicycleVisibility(boolean visibile) {
        
        _bicycleVisible.setBoolean(visibile);
        
    }
 
    /**
     * Get the list of components that are a a part of the bicycle.
     *
     * @return A list of all the components of the bicycle (FrameSet, Wheels, etc.).
     */
    public ArrayList<BaseComponent> getComponentList() {
        return _componentList;
    }

    /**
     * Set the Geometry Listener. Listens for model changes.
     *
     * @param bicycleListener Object to receive componentChanged notifications
     */
    public void setBicycleListener(BicycleListener bicycleListener) {

        _bicycleListener = bicycleListener;

    }

    /**
     * Called after a load to force an update
     */
    public void updateAllComponents() {

 

        _frame.update();

        _wheels.update();

        _cranks.update();
        _pedals.update();

        _stem.update();
        _handlebars.update();

        _seatPost.update();
        _saddle.update();

        _rider.update();

        // _isUpdating = false;
        update();

    }

    /**
     * Get the Bicycle analysis report.
     *
     * @return The Bicycle Report.
     */
    public Report getReport() {

        //update the report name for renames
        String bName = getBicycleName();
        _report.setName(bName); 
        
        return _report;
    }


    /**
     * Get the name of the bicycle. This is simply for descriptive purposes.
     *
     * @return The name of the Bicycle
     */
    public final String getBicycleName() {

        StringAttribute sa = _bicycleAttributes.getNameAttribute();
        //retunr the name value
        return sa.getString();
    }

    /**
     * Get the display setting of the bicycle.
     * @return The bicycle display setting.
     */
    public BicycleDisplay getDisplay() {
        return (BicycleDisplay)_bicycleDisplay.getEnum(); //BicycleDisplay.valueOf(_bicycleDisplay.getValue());
    }
    
    /**
     * Get the bicycle visibility setting.
     * @return True if visible, false if not visible.
     */
    public boolean getVisiblilty() {
        return _bicycleVisible.getBooleanValue();
    }
    
    /**
     * Get the Rider color.
     * @return The rider color.
     */
    public Color getRiderColor() {
        return _riderColor.getColor();
    }
    
    /**
     * Get the Frame color.
     * @return The frame color.
     */
    public Color getFrameColor() {
        return _bicycleColor.getColor();
    }   
    
    /**
     * Get the component color.
     * @return The component color.
     */
    public Color getComponentColor() {
        return _componentColor.getColor();
    }     
    

    /**
     * Called by component when an attribute changes. This will trigger an
     * overall rebuild from all components unless the flag is set to ignore
     * dependent updates (endless loop).
     *
     * @param compEvent The component event.
     */
    @Override
    public void componentChanged(ComponentChangeEvent compEvent) {

        //System.out.println(">>Component changed: " + compEvent);

        //check for flag and rebuild
        if (!_isUpdating) {
            update();
            
            BicycleChangeEvent bce = new BicycleChangeEvent(this, compEvent);
            //notify listener
            if (_bicycleListener != null) {
                _bicycleListener.bicycleChanged(bce);
            }            
            
            
        } else {
            //for development...
            //System.out.println("*** Update called when update already in process ***");
        }

    }

    /**
     * Called when one of the Bicycle Attributes is changed
     *
     * @param attSet  The attribute set.
     * @param attEvent  The attribute change event object.
     */
    @Override
    public void attributeInSetChanged(AttributeSet attSet, AttributeChangeEvent attEvent) {

        //System.out.println("::Bicycle Attribute changed: " + attEvent);

       
        
        //update the bounds for visibility
        updateBounds();
        
        //create bicycle event        
        BicycleChangeEvent bcEvent = new BicycleChangeEvent(this,attEvent);
        
        //hmm - udpdate for display changes
        if (_bicycleListener != null) {
            _bicycleListener.bicycleChanged(bcEvent);
        }

    }

    /**
     * Update from the Rider or Pose when they change in the project.
     * @param compEvent The component event that triggered the change.
     */
    public void updateFromRiderPose(ComponentChangeEvent compEvent) {
        
        //update geometry and bounds
        update();
        
    }
  
    
    /**
     * Rebuild the Model from the Frame.  Assume frame is updated (from attribute
     * change) and then rebuild all since the frame is the root of the
     * structure.
     */
    @Override
    public void updateGeometry() {

        //set flag to avoid endlesss updates since some components can fire componentChanged from the rebuild?
        //hmm... this shouldn't be - no overlap between exposed and internally needed vars - seperate ...
        _isUpdating = true;

        Point2D basePoint = getBasePoint();
         
        basePoint = resolveBasePoint();
        
        _frame.setBasePoint(basePoint);
        _frame.update();
        
        //BicycleDisplay display = BicycleDisplay.valueOf(_bicycleDisplay.getSQLInsert());
        //get the wheel base
        // double wb = _frame.getWheelBase();
        Point2D bbPoint = _frame.getBottomBracketPoint();

        //update the wheels with bae point and wheelbase
        _wheels.updateWheel(basePoint, _frame.getWheelBase());

        //update the stem with base point and the head tube angle
        _stem.updateStem(_frame.getStemPoint(), _frame.getHeadTubeAngle());

        //update handlebars from stem
        _handlebars.updateHandleBar(_stem.getMountPoint());

        //update the seat post
        _seatPost.updateSeatPost(_frame.getSeatPoint(), _frame.getSeatTubeAngle());

        //update the saddle
        _saddle.updateSaddle(_seatPost.getMountPoint());

        //update the cranks - bottom bracket base point and pose crankRotation
        _cranks.updateCranks(bbPoint, _pose.getCrankRotation());

        //update the pedals from the cranks and ankle rotation from the pose
        _pedals.updatePedals(_cranks.getCrankPointRight(), _cranks.getCrankPointLeft(), _pose.getPedalRotation());

        /**
         * Resolve Rider Position - seated or out of saddle
         */
        //update rider position
        //out of saddle point : x,y offset as point
        Point2D ousp = _pose.getOutOfSaddlePoint();
        double x = bbPoint.getX() + ousp.getX();//offset from bottom bracket
        double y = bbPoint.getY() + ousp.getY();//offset from bottom bracket

        //update out of saddle position
        _outOfSaddlePoint.setLocation(x, y);
        //prep an empty point for rider base point
        Point2D riderBasePoint = new Point2D.Double();

        //get the current setting
        SaddlePosition sp = _pose.getSaddlePosition();
        //set the rider base point
        switch (sp) {
            case OutOfSaddle:

                riderBasePoint = _outOfSaddlePoint;
                break;

            case Seated:
                riderBasePoint = _saddle.getSitPoint();
                break;
        }

        /**
         * Update the Rider
         */
        //get the handpoint
        Point2D handPoint = _handlebars.getHandPoint(_pose.getHandleBarPosition());
        //update the rider with the base point, pedal points, and hand point
        _rider.updateRider(riderBasePoint, _pedals.getPedalPointRight(), _pedals.getPedalPointLeft(), handPoint);

        //update analysis
        updateAnalysis();

        //update bounds
        updateBounds();


        _isUpdating = false;

    }

  
    /**
     * Layout the bicycle based on preferences.
     * This creates an offset from the BicycleLayout setting.
     * The default base point is the center of the rear wheel,
     * so the offset is calculated from that point.
     * @return The resolved base point.
     */
    private Point2D resolveBasePoint() {
        
        Point2D basePoint = getBasePoint();
        
        double wheelRad  = _wheels.getWheelRadius();
        double wheelBase = _frame.getWheelBase();

        BicycleLayout bl = _visualPrefs.getBicycleLayout();
        
        switch(bl) {
            
            case RearGround :
                
                //move above ground based on wheel radius
                basePoint = Utilities.translatePoint(basePoint, 0, wheelRad);                 
                
                break;
                
            case RearWheel :
                
                //do nothing here - this is the default for the frame
                
                break;
                
                
            case FrontGround :
                //move to front wheet above ground based on wheel radius
                basePoint = Utilities.translatePoint(basePoint, -wheelBase, wheelRad); 
                break;
                
            case FrontWheel :
                //move to front wheel
                basePoint = Utilities.translatePoint(basePoint, -wheelBase, 0); 
                break;
                
            case BottomBracket :
                
                Point2D bbp = _frame.getBottomBracketOffset();
                
                basePoint.setLocation(-bbp.getX(), -bbp.getY());
                
                break;
                
                
            case BottomBracketGround :
                
                Point2D bbp2 = _frame.getBottomBracketOffset();
                
                basePoint.setLocation(-bbp2.getX(), wheelRad);
                
                break;                
                
                
        }
        
        
        //return the modified base point
        return basePoint;
    }
    
    
    /**
     * Update the Bounds - only collect bounds for visible objects.
     * 
     */
    @Override
    public void updateBounds() {
        //if not visible - use minimal bounds
        if (!_bicycleVisible.getBooleanValue()) {
            Rectangle2D bounds = new Rectangle2D.Double(0, 0, 0.001, 0.001);
            setBounds(bounds);
            return;
        }

//        BicycleDisplay display = BicycleDisplay.valueOf(_bicycleDisplay.getValue());
        BicycleDisplay display = (BicycleDisplay)_bicycleDisplay.getEnum();

        //update bounds
        Rectangle2D bounds = _frame.getBounds();

        if (display != BicycleDisplay.FrameOnly) {

            bounds = Utilities.getUnionBoolean(bounds, _wheels.getBounds());

            bounds = Utilities.getUnionBoolean(bounds, _stem.getBounds());

            bounds = Utilities.getUnionBoolean(bounds, _seatPost.getBounds());
            bounds = Utilities.getUnionBoolean(bounds, _saddle.getBounds());

            
            bounds = Utilities.getUnionBoolean(bounds, _cranks.getBounds());
            bounds = Utilities.getUnionBoolean(bounds, _pedals.getBounds());

            bounds = Utilities.getUnionBoolean(bounds, _handlebars.getBounds());

        }

        if (display == BicycleDisplay.BicycleAndRider) {

            //include rider bounds if showing
            bounds = Utilities.getUnionBoolean(bounds, _rider.getBounds());
        }

        //set the bounds
        setBounds(bounds);

    }

 

    /**
     * Render the bicycle.
     *
     * @param g2  The graphics object to render to.
     * @param scale  The scale of the current view - used to scale line weights, etc.
     * @param vPrefs The visual preferences to reference when rendering.
     */
    public void render(Graphics2D g2, float scale, final VisualPreferences vPrefs) {

        if (!_bicycleVisible.getBooleanValue()) {
            return;
        }

        float bikeLW = 2 / scale;  //lineweight for frame and componenets
        float riderLW = 6 / scale; //lineweight for rider
        BasicStroke bikeStroke = new BasicStroke(bikeLW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        BasicStroke riderStroke = new BasicStroke(riderLW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

        Color riderColor = _riderColor.getColor();
        Color frameColor = _bicycleColor.getColor();
        Color compColor = _componentColor.getColor();



        

//        BicycleDisplay display = BicycleDisplay.valueOf(_bicycleDisplay.getValue());
        BicycleDisplay display = (BicycleDisplay)_bicycleDisplay.getEnum();

        //draw rider left leg on very bottom
        if (display == BicycleDisplay.BicycleAndRider) {

            g2.setPaint(riderColor);
            g2.setStroke(riderStroke);

            _rider.renderLeft(g2, riderColor);
        }

        //draw components left hand side
        if (display != BicycleDisplay.FrameOnly) {

            if(vPrefs.showGround()) {
                g2.setColor(vPrefs.getGroundColor());
                g2.setStroke(bikeStroke); 
                //draw gorund line on bottom
                g2.draw(_analysis.groundLine);

            }
            
            g2.setPaint(compColor);
            g2.setStroke(bikeStroke);

            //render on bottom
            _cranks.renderLeft(g2);
            _pedals.renderLeft(g2);
            //render wheels   
            _wheels.render(g2);

            //render stem
            _stem.render(g2);

        }

        //draw frame
        g2.setPaint(frameColor);
        g2.setStroke(bikeStroke);
        //frame
        _frame.render(g2);
               

        //draw components right hand side
        if (display != BicycleDisplay.FrameOnly) {

            g2.setPaint(compColor);
            g2.setStroke(bikeStroke);

            //right cranks over frame
            _cranks.renderRight(g2);

            _handlebars.render(g2);

            _seatPost.render(g2);
            _saddle.render(g2);

        }

        //draw this on top of components
        if(vPrefs.showAnalysis()) {
            //set the color
            g2.setColor(vPrefs.getAnalysisColor());
            //render analysis on bottom
            renderAnalysis(g2, scale);            
        }        
        
        
        //draw rider right side on top
        if (display == BicycleDisplay.BicycleAndRider) {

            g2.setPaint(riderColor);
            g2.setStroke(riderStroke);

            _rider.renderRight(g2, riderColor);


        }
        
        //draw on top of rider
        if (display != BicycleDisplay.FrameOnly) {

            g2.setPaint(compColor);
            g2.setStroke(bikeStroke);
            _pedals.renderRight(g2);
        }
        
        //check for control point display
        if(vPrefs.showControlPoints() && display == BicycleDisplay.BicycleAndRider) {
            
            Color cc = vPrefs.getControlPointColor();
            //sit point
            Graphics.renderCrossHandle(g2, cc, _saddle.getSitPoint(), scale);
            //out of saddle
            Graphics.renderCrossHandle(g2, cc,_outOfSaddlePoint,scale );
            //pedal points
            Graphics.renderCrossHandle(g2, cc, _pedals.getPedalPointLeft(), scale);
            Graphics.renderCrossHandle(g2, cc, _pedals.getPedalPointRight(), scale);
            //handlebar points
            _handlebars.renderHandPoints(g2, cc, scale);
            
            
        }
        

    }

    /**
     * Update the analysis and report
     */
    private void updateAnalysis() {

        //report values from the frame instead of recalculating
        _report.reportDistance(ReportField.ForkLength, _frame.getForkLength());
        _report.reportDistance(ReportField.WheelBase, _frame.getWheelBase());
        _report.reportDistance(ReportField.FrontCenter, _frame.getFrontCenter());
        
        double wheelBase = _frame.getWheelBase();
        double wheelRadius = _wheels.getWheelRadius();

//        _report.reportDistance(ReportField.WheelBase, wheelBase);
        
        //rear wheel base point
        Point2D rwBase = _wheels.getBasePoint();
        //front wheel base point
        Point2D fwBase = Utilities.translatePoint(rwBase, wheelBase, 0);
        
        _analysis.wheelBaseLine = new Line2D.Double(rwBase,fwBase);

        //ground line start
        Point2D glStart = Utilities.translatePoint(rwBase, -wheelRadius, -wheelRadius);
        Point2D glEnd = Utilities.translatePoint(fwBase, wheelRadius, -wheelRadius);
        //need this for calc below
        Line2D groundLine = new Line2D.Double(glStart, glEnd);

        _analysis.groundLine = groundLine;

        //line from front wheel to ground
        Point2D fwAtGround = Utilities.translatePoint(fwBase, 0, -wheelRadius);

        // Line2D wtog = new Line2D.Double(fwBase, fwAtGround);
        _analysis.frontWheelLine = new Line2D.Double(fwBase, fwAtGround);

        //head tube section
        double hta1 = Math.PI - Utilities.degreesToRadians(_frame.getHeadTubeAngle());
        double hta2 = hta1 + Math.PI;
        double rakeTheta = hta1 + (Math.PI / 2);//90 pos rotation = forkrake
        
        //fork rake line
        double rakeLen = _frame.getForkRake();
        Point2D rakePnt = Utilities.polarPoint(fwBase, rakeLen, rakeTheta);
        _analysis.forkRakeLine = new Line2D.Double(fwBase,rakePnt);
        
        //build head tube line (projected)
        Point2D stemPoint = _frame.getStemPoint();
        Point2D projStemPoint = Utilities.polarPoint(stemPoint, 1000, hta2);
        Line2D headLineProj = new Line2D.Double(stemPoint, projStemPoint);

        //prep to 0 in case of failure...
        _report.reportDistance(ReportField.Trail, 0);

        //get projected intersection to ground
        IntersectionPoint insPnt = Utilities.lineLineIntersect(headLineProj, groundLine);

        if (insPnt.result() == IntersectionPointResult.ONE_POINT) {

            projStemPoint = insPnt.getIntersection1();

            //trail distance
            double trail = fwAtGround.distance(projStemPoint);
            _report.reportDistance(ReportField.Trail, trail);

        }

        //head tube to ground
        //Line2D headLine = new Line2D.Double(stemPoint, projStemPoint);
        _analysis.headTubeLine = new Line2D.Double(stemPoint, projStemPoint);

        //bottom bracket point
        Point2D bbPoint = _frame.getBottomBracketPoint();
        //reach / stack point
        Point2D rsPoint = new Point2D.Double(bbPoint.getX(), stemPoint.getY());

        //front center line
        _analysis.frontCenterLine = new Line2D.Double(bbPoint, fwBase);


        Line2D stackLine = new Line2D.Double(bbPoint, rsPoint);
        Line2D reachLine = new Line2D.Double(rsPoint, stemPoint);

        _analysis.stackLine = stackLine;
//        _analysis.reachLine = reachLine;

        double stackDist = Utilities.lineLength(stackLine);
        double reachDist = Utilities.lineLength(reachLine);

        //bottom bracket to front wheel - front center measruement
        double fcDist = bbPoint.distance(fwBase);


        //_report.reportDistance(ReportField.FrontCenter, fcDist);

        //get the seat post point
        Point2D seatPostPoint = _frame.getSeatPoint();

        //calc the intersection
        insPnt = Utilities.lineLineIntersect(bbPoint, seatPostPoint, stemPoint, rsPoint);

        double effectiveTT = 0;
        if (insPnt.result() == IntersectionPointResult.ONE_POINT) {

            Point2D ip = insPnt.getIntersection1();
            //effective Top Tube
            effectiveTT = stemPoint.distance(ip);

            //effective top tube
            Line2D ETopTube = new Line2D.Double(ip, stemPoint);
            _analysis.reachLine = ETopTube;
            
            Line2D ESeatPost = new Line2D.Double(bbPoint, ip);
            _analysis.seatPostLine = ESeatPost;

        } 

        _report.reportDistance(ReportField.ETopTube, effectiveTT);

        //update the rider portion of the report
        _rider.updateReport(_report);

        //System.out.println("Reach = " + reach + ". Stack = " + stack);
    }



    /**
     * Simple class to transfer data internally.
     */
    private class BicycleAnalysisResult {

        Line2D groundLine;
        
        
        Line2D wheelBaseLine;
        Line2D frontCenterLine;
        
        Line2D stackLine;
        Line2D reachLine;

        Line2D seatPostLine;
        
        Line2D headTubeLine;
        Line2D frontWheelLine;
        
        Line2D forkRakeLine;

    }

    /**
     * Render the analysis lines (centerlines of some common measurements)
     *
     * @param g2 Graphics object to paint to.
     * @param scale Scale of current display for line weight handling.
     */
    private void renderAnalysis(Graphics2D g2, float scale) {

        float analysisLW = 1 / scale;
       

        float s1 = 18 / scale;
        float s2 = 6 / scale;
        
        //check bounds - 0 will throw exception in graphics object...
        s1 = Math.max(s1, 0.00002f);
        s2 = Math.max(s2, 0.00001f);

        float[] cl = {s1, s2, s2, s2};
        BasicStroke clStroke = new BasicStroke(analysisLW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 1.0f, cl, 0);

        g2.setStroke(clStroke);

        g2.draw(_analysis.wheelBaseLine);
        g2.draw(_analysis.frontCenterLine);
        
        g2.draw(_analysis.frontWheelLine);

        g2.draw(_analysis.headTubeLine);
        g2.draw(_analysis.forkRakeLine);

        g2.draw(_analysis.stackLine);
        g2.draw(_analysis.reachLine);
        
        g2.draw(_analysis.seatPostLine);
        
        _frame.renderLines(g2);

    }
    

  
    
    
    

    
}
