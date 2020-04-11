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
import org.bicycleGeometryWorkshop.attributes.EnumAttribute;
import java.awt.geom.Point2D;
import org.bicycleGeometryWorkshop.database.DataBaseKeys;

/**
 * The rider pose controls the posture of the stick figure on the bicycle.  All the general rotations of extremities can be specified (foot, ankle, arm, wrist).  
 * The rotation of components can also be specified (pedals and cranks).  The out of saddle offset is stored here also.
 * @author Tom
 */
public class RiderPose extends BaseComponent {
    
    //crank position
     private DoubleAttribute _attCrankRotation;
     //ankle rotation / heel pitch up
     private DoubleAttribute _attPedalRotation;
     
      private DoubleAttribute _attToeAngle;
     
     private DoubleAttribute _attArmBend;
     private DoubleAttribute _attWristBend;
     private EnumAttribute _attHandPosition;
     
     private DoubleAttribute _attOutOfSaddleX;
     private DoubleAttribute _attOutOfSaddleY;
     private EnumAttribute _attSaddlePosition;
    
     /**
      * Class constructor.  Construct the class with the component owner.
      * @param owner The component owner.
      */
    public RiderPose(ComponentOwner owner) {
        super(DataBaseKeys.POSE.toString(), owner);
        
        setIcon("PoseIcon16.png");
        
        _attCrankRotation = addAngleAttribute("CrankRotation", 0, -360, 360, "Angle of crank rotation (clockwise from horizontal)");
        _attPedalRotation = addAngleAttribute("PedalRotation", 0,-60, 60, "Angle of pedal rotation on spindle.");
        
        _attToeAngle = addAngleAttribute("FootAngle", 10, 0, 90, "This is the angle of the foot in relation to the pedal platform");
        
        _attArmBend = addAngleAttribute("ArmAngle", 25, 0, 180, "This is the rotation of the lower arm and controls the torso angle.");
        _attWristBend = addAngleAttribute("WristAngle", 0, -60, 60, "This is the angle of the wrist.");
        

        _attHandPosition = addEnumAttribute("HandPosition", HandleBarPosition.values(), HandleBarPosition.PositionA, "Controls which of the hand positions to use.");
        

        _attSaddlePosition = addEnumAttribute("SaddlePosition", SaddlePosition.values(), SaddlePosition.Seated , "Controls wether the rider uses the seated or out of saddle (standing) position");
        
        _attOutOfSaddleX = addLengthAttribute("OutOfSaddleX", -50, -1000, 10000, "X value of out of saddle position (calculated from bottom bracket).");
        _attOutOfSaddleY = addLengthAttribute("OutOfSaddleY", 725, 0, 100000,  "Y value of out of saddle position (calculated from bottom bracket).");
         
        
    }

    /**
     * Get the crank rotation in degrees.  This is the rotation of the cranks.  Positive values are clockwise (the natural stroke direction).
     * @return The crank rotation in degrees.
     */
    public double getCrankRotation() {
        return _attCrankRotation.getDoubleValue();
    }
    
    /**
     * Get the the pedal rotation in degrees.  This is the rotation of the pedals on their axis.
     * @return The pedal rotation in degrees.
     */
    public double getPedalRotation() {
        return _attPedalRotation.getDoubleValue();
    }    
    
    /**
     * Get the toe angle in degrees.  This  is the bend at the ball of the foot.
     * @return The toe angle in degrees.
     */
    public double getToeAngle() {
        return _attToeAngle.getDoubleValue();
    }      
    
    /**
     * Get the wrist bend in degrees. This is the bend at the wrist in relation to the arm.
     * @return The wrist bend in degrees.
     */
    public double getWristBend() {
        return _attWristBend.getDoubleValue();
    }        
    
    /**
     * Get the arm bend in degrees.  This is the bend in the elbow.
     * @return The arm bend in degrees.
     */
    public double getArmBend() {
        return _attArmBend.getDoubleValue();
    }
    
    /**
     * Get the handlebar position.  This is the selector for multiple hand positions (if the handlebar has multiple positions).
     * @return Th handlebar position.
     */
    public HandleBarPosition getHandleBarPosition() {

        HandleBarPosition hbp = (HandleBarPosition)_attHandPosition.getEnum();
        return hbp;
    }
    
    /**
     * Get the saddle position.  This is the controller for seated or out of saddle.
     * @return The saddle position.
     */
    public SaddlePosition getSaddlePosition() {
        
        SaddlePosition sp = (SaddlePosition)_attSaddlePosition.getEnum();
        return sp;
    }    
    
    /**
     * Get the out of saddle position.  This point is an offset from the bottom bracket (bottom bracket not factored in here).
     * @return The out of saddle point.
     */
    public Point2D getOutOfSaddlePoint() {
        double x = _attOutOfSaddleX.getDoubleValue();
        double y = _attOutOfSaddleY.getDoubleValue();
        
        return new Point2D.Double(x,y);
    }
    
    /**
     * There is no geometry in the rider pose.
     */
    @Override
    public void updateGeometry() {
        //no geometry
    }

 
    
    
}
