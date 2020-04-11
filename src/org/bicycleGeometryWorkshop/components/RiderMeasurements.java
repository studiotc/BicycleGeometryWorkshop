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

import org.bicycleGeometryWorkshop.database.DataBaseKeys;
import org.bicycleGeometryWorkshop.attributes.AttributeChangeEvent;
import org.bicycleGeometryWorkshop.attributes.AttributeSet;
import org.bicycleGeometryWorkshop.attributes.DoubleAttribute;

/**
 * Class for Rider size - this is class for UI interaction that shares it's
 * values with all the rider objects in The Bicycle collection (one rider per
 * bicycle) of a project.
 *
 * @author Tom
 */
public class RiderMeasurements extends BaseComponent {



    private DoubleAttribute _attrHeight;
    private DoubleAttribute _attrSternalNotch;
    
    private DoubleAttribute _attrInseam;

    private DoubleAttribute _attrHipHeight;
    private DoubleAttribute _attrKneeHeight;

    private DoubleAttribute _attrToeLength;
    private DoubleAttribute _attrFootLength;
    private DoubleAttribute _attrAnkleHeight;

    private DoubleAttribute _attrUpperArm;
    private DoubleAttribute _attrLowerArm;
    private DoubleAttribute _attrWristToPalm;

    
    private double _sitHipOffset;
    private double _upperLegLength;
    private double _lowerLegLength;
    private double _torsoLength;
    
    private double _headHeight;
    private double _headWidth;
    private double _neckLength;
    
    /**
     * Class constructor.  Constructor the class with the component owner.
     * @param owner The component owner.
     */
    public RiderMeasurements(ComponentOwner owner) {
        super(DataBaseKeys.RIDER_MEASURE.toString(), owner);
        
        setIcon("RiderIcon16.png");


        double min = 1;
        double max = 10000;
 
        
        _sitHipOffset = 0;
        _upperLegLength = 0;
        _lowerLegLength = 0;
        _torsoLength = 0;
        
        _headHeight = 0;
        _headWidth = 0;
        _neckLength = 0;

        _attrHeight = addLengthAttribute("Height", 1778, min, max,  "The overal height of the rider (top of head to floor)"); //5'10"
        _attrSternalNotch = addLengthAttribute("SternalNotch", 1473, min, max,  "The height of the sternal notch above the floor.");
        
        _attrInseam = addLengthAttribute("Inseam", 864, min, max, "The height of the sit bone above the floor.");
        

        _attrHipHeight = addLengthAttribute("HipHeight", 915, min, max,  "The height of the hip center of rotation above the floor.");
        _attrKneeHeight = addLengthAttribute("KneeHeight", 430, min, max,  "The height of the knee center of rotation above the floor.");

        _attrToeLength = addLengthAttribute("ToeLength", 64, min, max,  "The distance from the ball of the foot to the end of the toe (used for display only)");
        _attrFootLength = addLengthAttribute("FootLength", 140, min, max,  "The horizontal distance from the ball of the foot to the ankle center of rotation. ");
        _attrAnkleHeight = addLengthAttribute("AnkleHeight", 35, min, max,  "The height of the ankle center of rotation above the floor.");

        _attrUpperArm = addLengthAttribute("UpperArm", 330, min, max,  "The length of the upper arm (shoulder to elbow).");
        _attrLowerArm = addLengthAttribute("LowerArm", 275, min, max,  "The length of the lower arm (elbow to wrist)."); //270 & 280 measured
        _attrWristToPalm = addLengthAttribute("WristToPalm", 63, min, max,  "The distance from the wrist to the center of the palm (grasping point).");
        
        updateCalculatedValues();
        

    }
    
    /**
     * Update the internal calculated values that are dependent on the attributes.
     * Attributes are used for input from the user and values for the actual rider geometry 
     * are generated from those measurements where needed -  some are used directly.
     */
    private void updateCalculatedValues() {
        
       //hip height - inseam = sit bone to hip center of rotation
        _sitHipOffset = _attrHipHeight.getDoubleValue() - _attrInseam.getDoubleValue();
        
        _torsoLength = _attrSternalNotch.getDoubleValue() - _attrInseam.getDoubleValue();
        
        _lowerLegLength = _attrKneeHeight.getDoubleValue() - _attrAnkleHeight.getDoubleValue();
        
        _upperLegLength = _attrHipHeight.getDoubleValue() - _attrKneeHeight.getDoubleValue() ;
        
        
        //head height uses artistic proportion of 8 heads to the body height
        _headHeight = _attrHeight.getDoubleValue() / 8;
        //head width uses golden ratio
        _headWidth = _headHeight / 1.61803;
        
        //half the head height - top of head to aprx base of skull
        double hh = _headHeight / 2;
        
        //neck length is from height of sternal notch to base of skull
        //neck length is total height - sternal notch - top of head to base of skull
        _neckLength = _attrHeight.getDoubleValue() - _attrSternalNotch.getDoubleValue() - hh;
        
    }
    

  

    /**
     * Get the rider height.  This is the measurement of the standing rider 
     * from the top of the head to the floor.
     * @return The rider height.
     */
    public double getHeight() {
        return _attrHeight.getDoubleValue();
    }

    /**
     * Get the length of the torso.  The torso is from the sit point 
     * to the shoulders.  
     * @return The length of the torso.
     */    
    public double getTorso() {
        return _torsoLength;
    }

    /**
     * Get the hip offset.  The hip offset is the distance from the sit point 
     * to the center of rotation of the hip.
     * @return The hip offset.
     */
    public double getHipOffset() {
        return _sitHipOffset;
    }

    /**
     * Get the length of the upper leg (thigh).  The upper leg is from the 
     * center of rotation of the hip to the center of rotation of the knee.
     * @return The length of the upper leg (thigh).
     */
    public double getUpperLeg() {
        return _upperLegLength;
    }

    /**
     * Get the length of the lower leg (shin).  The lower keg is from the 
     * center of rotation of the knee to the center of rotation of the ankle.
     * @return The length of the lower leg (shin).
     */
    public double getLowerLeg() {
        return _lowerLegLength;
    }

    /**
     * Get the length of the toe.  The toe length is from the center of 
     * rotation of the ball of the foot to the tip of the big toe.
     * @return The length of the toe.
     */
    public double getToe() {
        return _attrToeLength.getDoubleValue();
    }

    /**
     * Get the length of the foot.  The foot length is from the center 
     * of rotation of the ball of the foot to the vertical projection of the 
     * center of rotation of the ankle.
     * @return The length of the foot.
     */
    public double getFoot() {
        return _attrFootLength.getDoubleValue();
    }

    /**
     * Get the height of the ankle.  The ankle height is the distance from 
     * the floor to the center of rotation of the ankle.
     * @return The height of the ankle.
     */
    public double getAnkleHeight() {
        return _attrAnkleHeight.getDoubleValue();
    }

    /**
     * Get the length of the upper arm.  The upper arm is the distance from 
     * the shoulder center of rotation to the elbow center of rotation.
     * @return The length of the upper arm.
     */
    public double getUpperArm() {
        return _attrUpperArm.getDoubleValue();
    }

    /**
     * Get the length of the lower arm.  The lower arm is the distance from 
     * the elbow center of rotation to the wrist center of rotation.
     * @return The length of the lower arm.
     */
    public double getLowerArm() {
        return _attrLowerArm.getDoubleValue();
    }

    /**
     * Get the distance from the wrist to palm.  This is the distance from the 
     * center of rotation of the wrist to the center of the palm which is 
     * considered the grasping point (center of grasp).
     * @return The distance from the wrist to the center of the palm.
     */
    public double getWristToPalm() {
        return _attrWristToPalm.getDoubleValue();
    }

    /**
     * Get the height of the head.  The height of the head is calculated from the height / 8 (artistic proportions)
     * @return The height of the head.
     */
    public double getHeadHeight() {
        return _headHeight;
    }
    
    /**
     * Get the width of the head.  The width of the head uses the golden ratio in relation to the head height ( head height / 1.61803).
     * @return The width of the head.
     */
    public double getHeadWidth() {
        return _headWidth;
    }
    
    
    /**
     * Override attribute change event to recalculate internal values.
     * Update internal values and pass event on.
     * @param attSet  Attribute set that has been modified.
     * @param attEvent Attribute event for attribute that has changed.
     */
    @Override
    public  void attributeInSetChanged(AttributeSet attSet,  AttributeChangeEvent attEvent) {

        updateCalculatedValues();
        //pass event
        super.attributeInSetChanged(attSet, attEvent);
     
     
    }
    
 

    /**
     * There is no geometry to update.
     */
    @Override
    public void updateGeometry() {
       
        //no geometry
    }



}
