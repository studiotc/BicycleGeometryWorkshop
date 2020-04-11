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

import org.bicycleGeometryWorkshop.attributes.EnumAttribute;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * This class is a controller class for the distinct types of handlebars.  
 * It controls which of the sub-type to display and routes the normal component calls 
 * to the selected handlebar sub-type.
 *  
 * @author Tom
 */
public class HandleBar extends BaseComponent implements ComponentOwner {
    
 
    private EnumAttribute _attHandlebarType;
    

    private HandleBarDrop _hbDrop;
    private HandleBarStraight _hbStraight;
    private HandleBarAero _hbAero;
    private HandleBarRiser _hbRiser;
    

    /**
     * Class constructor.  Initialize the class with the component owner.
     * @param owner The component owner.
     */
    public HandleBar(ComponentOwner owner)  {
        super(DataBaseKeys.HANDLEBAR.toString(), owner);
        
        setIcon("HandlebarIcon16.png");

        _hbDrop = new HandleBarDrop(this);
        _hbStraight = new HandleBarStraight(this);
        _hbAero = new HandleBarAero(this);
        _hbRiser = new HandleBarRiser(this);
        


        addSubComponent(_hbDrop);
        addSubComponent(_hbStraight);
        addSubComponent(_hbAero);
        addSubComponent(_hbRiser);
        
        //add the bar type
        _attHandlebarType = addEnumAttribute("BarType", HandleBarType.values(), HandleBarType.Drop, "The Type of Handlebar to use for display." ); 
   
         update();
    }
    
    /**
     * Update the handlebar with the base point.
     * @param basePoint The base Point of the handlebar.
     */
    public void updateHandleBar(Point2D basePoint) {
        setBasePoint(basePoint);
        update();
    }
    
    
 
    /**
     * Override SetBasePoint to transfer the basePoint to all sub-components.
     * @param point The base point.
     */
   @Override
   public void setBasePoint(Point2D point) {
       
       //set the base point for main - handpoints
       super.setBasePoint(point);
       
       //set the base point for sub components
       _hbDrop.setBasePoint(point);
       _hbStraight.setBasePoint(point);
       _hbAero.setBasePoint(point);
       _hbRiser.setBasePoint(point);
       
   }
    
    /**
     * Get the bounds of the handlebar.  This returns the bounds 
     * of the currently selected sub-handlebar.
     * @return The bounds of the handlebar.
     */
    @Override
    public Rectangle2D getBounds() {

      //HandleBarType hbt = HandleBarType.valueOf(_attHandlebarType.getValue());
      HandleBarType hbt = (HandleBarType)_attHandlebarType.getEnum(); 
      
      Rectangle2D bounds = new Rectangle2D.Double();
      
      switch(hbt) {
          
          case Drop:
              bounds = _hbDrop.getBounds();
              break;
          
          case Straight:
              bounds = _hbStraight.getBounds();
              break;

          case Riser:
              bounds = _hbRiser.getBounds();
              break;
              
          case Aero:
              bounds = _hbAero.getBounds();
              break;
       
          
      }        
        
      
      return bounds;
        
    }
    
    /**
     * Get the geometry  of the handlebar.  This returns the geometry 
     * of the currently selected sub-handlebar.
     * @return The geometry of the selected sub-handlebar.
     */
    @Override
    public ArrayList<Shape> getGeometry() {

//      HandleBarType hbt = HandleBarType.valueOf(_attHandlebarType.getValue());
      HandleBarType hbt = (HandleBarType)_attHandlebarType.getEnum();
      
      ArrayList<Shape> geom = null;
      
      switch(hbt) {
          
          case Drop:
              geom =  _hbDrop.getGeometry();
              break;
          
          case Straight:
              geom = _hbStraight.getGeometry();
              break;
              
          case Riser:
              geom = _hbRiser.getGeometry();
              break;              

          case Aero:
              geom = _hbAero.getGeometry();
              break;
 
          
      }        
        
        return geom;
        
    }    
    
    
    /**
     * Update the geometry of the handlebar.  This updates the geometry 
     * of the currently selected sub-handlebar.
     */
    @Override
    public void updateGeometry() {
        

      HandleBarType hbt = (HandleBarType)_attHandlebarType.getEnum();
        
      switch(hbt) {
          
          case Drop:
              _hbDrop.update();
              break;
          
          case Straight:
              _hbStraight.update();
              break;
              
          case Riser:
              _hbRiser.update();
              break;              

          case Aero:
              _hbAero.update();
              break;
       
          
      }
        
        
    }
    
    /**
     * Render the handlebar.  This renders the currently selected sub-handlebar.
     * 
     * @param g2 The graphics object to render to.
     */
    public void render(Graphics2D g2) {
        
       
      int strokeWidth = 20;
      BasicStroke cappedStroke = new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
      BasicStroke roundStroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
      
      Stroke curStroke = g2.getStroke();
        
              
        

      HandleBarType hbt = (HandleBarType)_attHandlebarType.getEnum();
        
      switch(hbt) {
          
          case Drop:
              g2.setStroke(cappedStroke);
              _hbDrop.render(g2);
              break;
          
          case Straight:
              g2.setStroke(roundStroke);
              _hbStraight.render(g2);
              break;
              
          case Riser:
              g2.setStroke(roundStroke);
              _hbRiser.render(g2);
              break;
              
          case Aero:
              g2.setStroke(cappedStroke);
              _hbAero.render(g2);
              break;

          
      }//end switch case    
      
      //restore stroke 
       g2.setStroke(curStroke);
        
 
        
    }
    
    
    /**
     * Render the hand points of the handlebars.
     * This renders the control points for the handlebar hand connection point(s).
     * @param g2  The graphics object to render to.
     * @param color  The color to use for rendering.
     * @param scale The scale of the current view to scale line weights, etc.
     */
    public void renderHandPoints(Graphics2D g2, Color color, float scale) {
        
      HandleBarType hbt = (HandleBarType)_attHandlebarType.getEnum();
        
      switch(hbt) {
          
          case Drop:
              _hbDrop.renderHandPoints(g2,color,scale);
              break;
          
          case Straight:
              _hbStraight.renderHandPoints(g2,color,scale);
              break;
              
          case Riser:
              _hbRiser.renderHandPoints(g2,color,scale);
              break;
              
          case Aero:
              _hbAero.renderHandPoints(g2,color,scale);
              break;

          
      }//end switch case         
        
        
        
        
    }


    /**
     * Get the hand point of the handlebar.  This selects the hand point
     * of the currently selected sub-handlebar.
     * @param position The hand position to select from the sub-handlebar.
     * @return The hand point of the current sub-handlebar.
     */
    public Point2D getHandPoint(HandleBarPosition position) {   
        
        Point2D handPoint = new Point2D.Double();
      HandleBarType hbt = (HandleBarType)_attHandlebarType.getEnum();
        
      switch(hbt) {
          
          case Drop:
              handPoint = _hbDrop.getHandPoint(position);
              break;
          
          case Straight:
              handPoint = _hbStraight.getHandPoint(position);
              break;
              
          case Riser:
              handPoint = _hbRiser.getHandPoint(position);
              break;
              
          case Aero:
              handPoint = _hbAero.getHandPoint(position);
              break;

          
      }//end switch case         
        
        return handPoint;
        
        
    } 
    


    /**
     * This class serves as the the component owner for the sub-handlebar.  
     * This component should bubble up all events from sub-handlebar.
     * @param compEvent The component event.
     */
    @Override
    public void componentChanged(ComponentChangeEvent compEvent) {
        
        ComponentChangeEvent ce = new ComponentChangeEvent(this, compEvent.getAttributeEvent());
        _owner.componentChanged(ce);
        
    }


    
}
