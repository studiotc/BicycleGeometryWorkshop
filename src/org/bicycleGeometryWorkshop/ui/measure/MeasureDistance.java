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
 * The MeasureDistacne class is the on-screen measurement class for linear distances.  
 * This prompts the user to select points and displays the x/y deltas and measurement.
 * @author Tom
 */
public class MeasureDistance extends Measure {

  
    //enum for the tool state
    private enum DistMeasureState { GET_FIRST, GET_SECOND, HAS_SECOND;}
    
    
    private Point2D _firstPoint;
    private Point2D _secondPoint;
    
    private Point2D _dynamicPoint;
    private Point2D _deltaPoint;
    
    private DistMeasureState _state;

  
    private double _distance;
    private double _deltaX;
    private double _deltaY;

 


    /**
     * Class constructor.
     */
    public MeasureDistance() {
        
 
        _firstPoint = new Point2D.Double();
        _secondPoint =  new Point2D.Double();
        
        _dynamicPoint = new Point2D.Double();
        _deltaPoint = new Point2D.Double();
        
        _state = DistMeasureState.GET_FIRST;
        
  
        _distance = 0;
        _deltaX = 0;
        _deltaY = 0;



    }

    /**
     * Reset the distance measurement state.
     */
    @Override
    protected void reset() {
        
        _state = DistMeasureState.GET_FIRST;

        _firstPoint = new Point2D.Double();
        _dynamicPoint = new Point2D.Double();
        _secondPoint =  new Point2D.Double();        


    }

    /**
     * Updates the distance measurement state when a point is clicked.
     * @param worldPoint The point in world coordinates that was clicked.
     */    
    @Override
    public void onPointClicked(Point2D worldPoint) {

        switch(_state) {
            
            case GET_FIRST:
                _firstPoint = worldPoint;
                _dynamicPoint = worldPoint;
                _state = DistMeasureState.GET_SECOND;
                break;
            
            case GET_SECOND:
                _secondPoint = worldPoint;
                //calcs
                _distance = _firstPoint.distance(_secondPoint);
                _deltaX = _secondPoint.getX() - _firstPoint.getX();
                _deltaY = _secondPoint.getY() - _firstPoint.getY();
                
                _deltaPoint.setLocation( _secondPoint.getX(), _firstPoint.getY());
                
                _state = DistMeasureState.HAS_SECOND;
                break;     
                
           case HAS_SECOND:

               _firstPoint = worldPoint;
                _dynamicPoint = worldPoint;
                _state = DistMeasureState.GET_SECOND;
                _distance = 0;
                break;                  
            
            
        }
        


    }

    /**
     * Updates the distance measurement state when the mouse moves.  The dynamic point is the current mouse point.
     * @param worldPoint The current mouse point in world coordinates.
     */    
    @Override
    public void onDynamicPoint(Point2D worldPoint) {

        _dynamicPoint = worldPoint;
        
        switch(_state) {
            

            case GET_SECOND:
                //_dynamicPoint = worldPoint;
                _distance = _firstPoint.distance(_dynamicPoint);
                _deltaX = _dynamicPoint.getX() - _firstPoint.getX();
                _deltaY = _dynamicPoint.getY() - _firstPoint.getY();   
                
                
                _deltaPoint.setLocation( _dynamicPoint.getX(), _firstPoint.getY());
                
                break;     
  
            
        }

    }

    /**
     * Render the measurement in the world.  This is the portion of the measurement 
     * that displays the picked points and delta lines and control points.
     * @param g2  The graphics object to render to.
     * @param scale The  scale to render at.  This is for line weights, etc.
     */    
    @Override
    public void renderInWorld(Graphics2D g2, float scale) {

        switch(_state) {
            
            case GET_FIRST:
                renderCrossHandle(g2,_dynamicPoint, scale);
                break;
            
            case GET_SECOND:
                
                renderLine(g2, _firstPoint, _deltaPoint, scale, COLOR_X, true);
                renderLine(g2, _dynamicPoint, _deltaPoint, scale, COLOR_Y, true);                
                
                renderLine(g2,_firstPoint, _dynamicPoint, scale, COLOR_DA, false);
                
                renderCrossHandle(g2,_deltaPoint,scale);
                renderCrossHandle(g2,_firstPoint,scale);
                renderCrossHandle(g2,_dynamicPoint,scale);
                

                
                break;     
                
           case HAS_SECOND:
               
                renderLine(g2, _firstPoint, _deltaPoint, scale, COLOR_X, true);
                renderLine(g2, _secondPoint, _deltaPoint, scale, COLOR_Y, true);               
               
                renderLine(g2,_firstPoint, _secondPoint, scale, COLOR_DA, false);
                
                renderCrossHandle(g2,_deltaPoint,scale);
                renderCrossHandle(g2,_firstPoint,scale);
                renderCrossHandle(g2,_secondPoint,scale);
                
                //render new point handle
                renderCrossHandle(g2,_dynamicPoint, scale);
                break;                  
            
            
        }        
     
    }
    
  
    /**
     * Render the distance measurement to the screen.  This is 
     * the text portion of the measurement which displays the 
     * prompts, deltas, and distance.
     * @param g2 The graphics object to render to.
     */
    @Override
    public void renderInScreen(Graphics2D g2) {


        boolean reportDist = false;
        
        //reset the layout for text
        resetScreenLayout();
        
        String mssg = "[<ESC> to Cancel measuring]";
        renderScreenLine(g2, mssg);        
        
        
        
        switch(_state) {
            
            case GET_FIRST:
                mssg = "Select first point for distance:";
                renderScreenLine(g2, mssg); 

                break;
            
            case GET_SECOND:
                mssg = "Select second point for distance:";
                renderScreenLine(g2, mssg);                 
                reportDist = true;
                break;     
                
           case HAS_SECOND:
                mssg = "Select first point for new distance:";
                renderScreenLine(g2, mssg);                
               reportDist = true;
                break;                  
            
            
        }         
        


        if (reportDist) {

            String distStr = "Distance = " + Utilities.formatLengthDisplayUnits(_distance);
            String deltaXStr = "Delta X = " + Utilities.formatLengthDisplayUnits(_deltaX);
            String deltaYStr = "Delta Y = " + Utilities.formatLengthDisplayUnits(_deltaY);
        

            renderScreenLine(g2, deltaXStr, COLOR_X);
            renderScreenLine(g2, deltaYStr, COLOR_Y);
            renderScreenLine(g2, distStr);
            

        }

    }

}//end class
