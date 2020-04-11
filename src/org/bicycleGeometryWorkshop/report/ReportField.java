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
package org.bicycleGeometryWorkshop.report;


//static final String COLUMN_HEADS = {"Stack", 'Reach", "Trail", "FrontCenter", "TorsoH", "TorsoUA", "Elbow", "RThighTorso", "RKnee", "RKneePedal", "LThighTorso", "LKnee", "LKneePedal"};
/**
 *  Frame Report Fields.  This serves as the column headers and keys for the report.  
 *  The values in this enum define the fields for the report.
 * @author Tom
 */
public enum ReportField {
    /**
     * The name of the bicycle.
     */
    Name,
    
    /**
     * The wheel base.
     */
    WheelBase,
    
    /**
     * The front center measurement.
     */
    FrontCenter,
    
    /**
     * The length of the forks.
     */
    ForkLength,
    
    
    /**
     * The trial of the bicycle.
     */
    Trail,
    
    /**
     * The effective top tube measurement.
     */
    ETopTube,
    
    /**
     * The Torso angle measured from the horizontal.
     */
    TorsoH,
    
    
    /**
     * The angle between torso and upper arm.
     */
    TorsoUA,
    
    /**
     * The angle of elbow bend.
     */
    Elbow,
    
   
    /**
     * The angle between the right thigh and torso.
     */
    RThighTorso,
    
    /**
     * The angle of right knee bend.
     */
    RKnee,
    
    /**
     * The horizontal distance between the right knee and the right pedal spindle center.
     */
    RKneePedal,
    
    /**
     * The angle between the left thigh and torso.
     */
    LThighTorso,
    
    /**
     * The angle of left knee bend.
     */
    LKnee,
    
    /**
     * The horizontal distance between the left knee and the left pedal spindle center.
     */
    LKneePedal;
    
    
}
