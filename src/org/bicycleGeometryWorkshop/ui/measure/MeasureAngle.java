/*
 * The MIT License
 *
 * Copyright 2020 Tom.
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
package org.bicycleGeometryWorkshop.ui.measure;

import org.bicycleGeometryWorkshop.geometry.Utilities;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * The MeasureAngle class is the on-screen measurement class for angular distances.  
 * This prompts the user to select points and displays the base angles and angle between them.
 * @author Tom
 */
public class MeasureAngle extends Measure {
    
    
    private enum MeasueAngleState { GET_BASE, GET_FIRST, GET_SECOND, HAS_SECOND; }
    
    private MeasueAngleState _state;
    
    private Point2D _basePoint;
    private Point2D _firstPoint;
    private Point2D _secondPoint;    
    
    private Point2D _dynamicPoint;
    
    private double _angleSegA;
    private double _angleSegB;
    private double _angle;
    

    
    /**
     * Class constructor.
     */
    public MeasureAngle() {
        
        _state = MeasueAngleState.GET_BASE;
        
        _basePoint = new Point2D.Double();
        _firstPoint = new Point2D.Double();
        _secondPoint = new Point2D.Double();
        _dynamicPoint = new Point2D.Double();
        
        _angleSegA = 0;
        _angleSegB = 0;
        _angle = 0;
        

        
    }

    /**
     * Reset the angle measurement state.
     */    
    @Override
    protected void reset() {
        
       _state = MeasueAngleState.GET_BASE;
        
        _basePoint = new Point2D.Double();
        _firstPoint = new Point2D.Double();
        _secondPoint = new Point2D.Double();
        _dynamicPoint = new Point2D.Double(); 
        
        _angleSegA = 0;
        _angleSegB = 0;
        _angle = 0;        
        
        
    }
    
    


    /**
     * Updates the angle measurement state when a point is clicked.
     * @param worldPoint The point in world coordinates that was clicked.
     */
    @Override
    public void onPointClicked(Point2D worldPoint) {
        
        switch(_state) {
            
            case GET_BASE:
                _basePoint = worldPoint;
                _state = MeasueAngleState.GET_FIRST;

                break;
                
                
            case GET_FIRST:
                _firstPoint = worldPoint;
                
                _angleSegA = Utilities.anglePointPoint(_basePoint, _firstPoint);
                
                _state = MeasueAngleState.GET_SECOND;                
                
                break;
                
                
            case GET_SECOND:
                _secondPoint = worldPoint;
                
                _angleSegB = Utilities.anglePointPoint(_basePoint, _secondPoint);
                _angle = Utilities.vectorAngle(_basePoint, _firstPoint, _secondPoint);
                
                
                _state = MeasueAngleState.HAS_SECOND;                 
                
                
                break;
                
                
            case HAS_SECOND:
                reset();
                _basePoint = worldPoint;
                _state = MeasueAngleState.GET_FIRST;                  
                
                break;
  
        }//end switch
        
        
    }

    /**
     * Updates the angle measurement state when the mouse moves.  The dynamic point is the current mouse point.
     * @param worldPoint The current mouse point in world coordinates.
     */
    @Override
    public void onDynamicPoint(Point2D worldPoint) {
        
        _dynamicPoint = worldPoint;
        
        switch(_state) {
            
            case GET_BASE:
                
                
                break;
                
                
            case GET_FIRST:
                
                _angleSegA = Utilities.anglePointPoint(_basePoint, _dynamicPoint);
                break;
                
                
            case GET_SECOND:
                
                _angleSegB = Utilities.anglePointPoint(_basePoint, _dynamicPoint);
                _angle = Utilities.vectorAngle(_basePoint, _firstPoint, _dynamicPoint);

                break;
                
                
            case HAS_SECOND:
                
                
                break;
  
        }//end switch
        
        
    }

    /**
     * Render the measurement in the world.  This is the portion of the measurement 
     * that displays the picked points and angle lines and arcs.
     * @param g2  The graphics object to render to.
     * @param scale The  scale to render at.  This is for line weights, etc.
     */
    @Override
    public void renderInWorld(Graphics2D g2, float scale) {
       
        
        switch(_state) {
            
            case GET_BASE:
                //renderDotHandle(g2,_dynamicPoint, scale);
                renderCrossHandle(g2,_dynamicPoint, scale);
                
                break;
                
                
            case GET_FIRST:
                renderLine(g2, _basePoint, _dynamicPoint, scale, COLOR_X, true);
                renderDotHandle(g2,_basePoint, scale);
                renderCrossHandle(g2,_dynamicPoint, scale);
                break;
                
                
            case GET_SECOND:
                
                renderLine(g2, _basePoint, _firstPoint, scale, COLOR_X, true);
                renderLine(g2, _basePoint, _dynamicPoint, scale, COLOR_Y, true);
                
                renderDotHandle(g2,_basePoint, scale);
                renderCrossHandle(g2,_firstPoint, scale);
                 
                renderCrossHandle(g2,_dynamicPoint, scale);
                
                renderArc(g2,_basePoint, _firstPoint, _dynamicPoint, scale);
                
                break;
                
                
            case HAS_SECOND:
                
                renderLine(g2, _basePoint, _firstPoint, scale,COLOR_X, true );
                renderLine(g2, _basePoint, _secondPoint, scale, COLOR_Y, true);
                
                renderDotHandle(g2,_basePoint, scale);
                renderCrossHandle(g2,_firstPoint, scale);
                 
                renderCrossHandle(g2,_secondPoint, scale);   
                
                renderArc(g2,_basePoint, _firstPoint, _secondPoint, scale);
                
                //show for new base point
                //renderDotHandle(g2,_dynamicPoint, scale);
                renderCrossHandle(g2,_dynamicPoint, scale);                
                
                break;
  
        }//end switch        
        
        
    }

    /**
     * Render the angle measurement to the screen.  This is 
     * the text portion of the measurement which displays the 
     * prompts, base angles, and angle.
     * @param g2 The graphics object to render to.
     */
    @Override
    public void renderInScreen(Graphics2D g2) {
        
       //reset the layout for text
        resetScreenLayout(); 
        
        double segA = Utilities.radiansToDegreesNorm(_angleSegA);
        double segB = Utilities.radiansToDegreesNorm(_angleSegB);
        double angle = Utilities.radiansToDegreesNorm(_angle);
        
        String segAtext = "Line A = " + Utilities.formatAngle(segA);
        String segBtext = "Line B = " + Utilities.formatAngle(segB);
        String angleABtext = "Angle AB = " + Utilities.formatAngle(angle);
        
        //show cancel message
        String mssg = "[<ESC> to Cancel measuring]";
        renderScreenLine(g2, mssg);         
        
        switch(_state) {
            
            case GET_BASE:
                
                mssg = "Select base point for angle:";
                renderScreenLine(g2, mssg);
                
                break;
                
                
            case GET_FIRST:
                mssg = "Select first point for angle:";
                renderScreenLine(g2, mssg);
                
                renderScreenLine(g2, segAtext, COLOR_X);
                break;
                
                
            case GET_SECOND:
                mssg = "Select second point for angle:";
                renderScreenLine(g2, mssg);
                
                renderScreenLine(g2, segAtext, COLOR_X);  
                renderScreenLine(g2, segBtext, COLOR_Y);
                renderScreenLine(g2, angleABtext);                
                
                break;
                
                
            case HAS_SECOND:
                
                mssg = "Select new base point for angle:";
                renderScreenLine(g2, mssg);
                

                renderScreenLine(g2, segAtext, COLOR_X);  
                renderScreenLine(g2, segBtext, COLOR_Y);
                renderScreenLine(g2, angleABtext);                
                
                break;
  
        }//end switch        
        
        
    }
    
}
