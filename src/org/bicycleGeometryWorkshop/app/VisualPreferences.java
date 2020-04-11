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
package org.bicycleGeometryWorkshop.app;

import java.awt.Color;
import org.bicycleGeometryWorkshop.attributes.ColorAttribute;
import org.bicycleGeometryWorkshop.attributes.BooleanAttribute;
import org.bicycleGeometryWorkshop.attributes.EnumAttribute;
import org.bicycleGeometryWorkshop.components.BaseComponent;
import org.bicycleGeometryWorkshop.components.BicycleDisplay;
import org.bicycleGeometryWorkshop.components.BicycleLayout;
import org.bicycleGeometryWorkshop.components.ComponentOwner;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * This class is for the Project's display preferences.  
 * 
 * @author Tom
 */
public class VisualPreferences extends BaseComponent {

    private ColorAttribute _attBgColor;
    private BooleanAttribute _attShowGround;
    private ColorAttribute _attGroundColor;
    private ColorAttribute _attAnalysisColor;
    private BooleanAttribute _attShowAnalysis;
    private BooleanAttribute _attShowControlPoints;
    private ColorAttribute _attControlPointColor;
    
    private EnumAttribute _attBicycleLayout;
    
    /**
     * Class constructor.  This uses the BaseComponent class just to manage attributes and does not have any geometry.
     * @param owner The component owner (listener).
     */
    public VisualPreferences(ComponentOwner owner) {
        super(DataBaseKeys.BICYCLE_PREF.toString(), owner);
        
        
//        _attBicycleLayout =  addEnumAttribute("BicycleLayout", BicycleLayout.values(), BicycleLayout.RearGround.name(), "Controls the layout of the Bicycle for comparison.");
        _attBicycleLayout =  addEnumAttribute("BicycleLayout", BicycleLayout.values(), BicycleLayout.RearGround, "Controls the layout of the Bicycle for comparison.");
       
        //background color
        _attBgColor = addColorAttribute("BackgroundColor", Color.GRAY, "Background color for the Bicycle display.");
        
        //rider points
        _attShowControlPoints = addBooleanAttribute("ShowRiderPoints", true, "The rider points are the control points for the rider position.");
        _attControlPointColor = addColorAttribute("ControlPointColor", new Color(255,0,0,128), "Color of the control points.");
                
        //analysis lines
        _attShowAnalysis = addBooleanAttribute("ShowAnalysis", false, "The analysis is the set of reference lines for common measurements.");
        _attAnalysisColor = addColorAttribute("AnalysisColor", Color.LIGHT_GRAY, "Color of analysis lines.");
        
        _attShowGround = addBooleanAttribute("ShowGround", true, "The ground line is the reference line for the riding surface.");
        _attGroundColor = addColorAttribute("GroundColor", Color.BLACK, "Color of the ground line.");
        

        //hide the name attribute - not used for this
        this.getAttributeSet().getNameAttribute().setDisplay(false);
        

        
        
    }
    
    /**
     * Required override - this does nothing here.
     */
    @Override
    public void updateGeometry() {
       //nothing to do here....
    }
    
    /**
     * The color of the background of the display.
     * @return The background color of the display.
     */
    public Color getBackgroundColor() {
        return _attBgColor.getColor();
    }
   
   /**
    * Get the show ground setting.
    * This is the flag to show or hide the ground line.
    * The ground line is the line representing the literal ground or riding surface.
    * @return True to show the ground line, false to not display the ground.
    */
   public boolean showGround() {
        return _attShowGround.getBooleanValue();
    }     
    
    /**
     * The color of the ground line.
     * THe ground line is shown for reference when the full bicycle is displayed.
     * @return The color of the ground line when the bicycle display is on.
     */
   public Color getGroundColor() {
        return _attGroundColor.getColor();
    }
   
   /**
    * The color of the analysis lines.
    * @return The color to display the analysis lines.
    */
   public Color getAnalysisColor() {
        return _attAnalysisColor.getColor();
    }

   /**
    * Get the show analysis setting.
    * This is the flag to show or hide the analysis.
    * @return True to show the analysis, false to not display the analysis.
    */
   public boolean showAnalysis() {
        return _attShowAnalysis.getBooleanValue();
    }   
    
   /**
    * Get the show control points setting.
    * The control points are the points that control the riders pose.
    * @return True to show the rider control points, false to not show the points.
    */
   public boolean showControlPoints() {
       return _attShowControlPoints.getBooleanValue();
   }
   
   /**
    * Get the color of the control points (rider position points).
    * @return The color of the control points (rider position points).
    */
   public Color getControlPointColor() {
        return _attControlPointColor.getColor();
    }   
   
   
   /**
    * Get the Bicycle Layout setting.
    * @return The bicycle layout.
    */
   public BicycleLayout getBicycleLayout() {
       
       return (BicycleLayout)_attBicycleLayout.getEnum(); //BicycleLayout.valueOf(_attBicycleLayout.getValue());
      
   }
    
}
