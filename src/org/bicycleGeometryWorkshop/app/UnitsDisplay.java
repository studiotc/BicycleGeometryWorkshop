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

import java.util.ArrayList;

/**
 * Static class functions to handle the display units: millimeters or inches.  
 * This class tracks the current settings (mm or in) and holds a listener list 
 * to notify other components when it changes.
 * 
 * @author Tom
 */
public class UnitsDisplay {
    
    //conversion factor
    private final static double CONVF = 25.4; //inches to mm
    
    //internal flag to switch between 
    //millimeters (natural units/internal units and
    //imperial units (imperial units)
    private static boolean useNaturalUnits = true;
    
    private static ArrayList<UnitsListener> listeners  = new ArrayList();
    
    /**
     * Sets the use of natural units (millimeters).
     */
    public final static void setNaturalUnits() {
        useNaturalUnits = true;
        
        onUnitsChange();
    }
    
    /**
     * Sets the use of imperial units (inches)
     */
    public final static void setImperialUnits() {
        useNaturalUnits = false;
                
        onUnitsChange();
    }
    
    
    /**
     * Get the natural units setting (mm or inches).
     * Used to query inches or millimeters.
     * @return true for millimeters, false for inches.
     */
    public final static boolean usingNaturalUnits() {
        return useNaturalUnits;
    }
    
    
    
    /**
     * Convert inches to Millimeters.
     * @param inches Inch vale to convert.
     * @return The value in Millimeters.
     */
    public final static double inToMM(double inches) {
        
        return inches * CONVF;
        
    }
    
    /**
     * 
     * Convert Millimeters to Inches.
     * @param mm  Millimeter value to convert.
     * @return The value in inches.
     */
    public final static double mmToIn(double mm) {
        
        return mm / CONVF;
        
    }
    
    /**
     * Get the Suffix according to the current natural units setting.
     * @return The suffix for the current unit.
     */
    public final static String getUnitsSuffix() {
        
        if(useNaturalUnits) {
            return "mm";
        } else {
            return "in";
        }
        
        
    }
    
    /**
     * Get the display length from a natural number.
     * @param natural The natural number to convert (or preserve).
     * @return The number converted or preserved based on natural units setting.
     */
    public final static double getDisplayLength(double natural) {
        
        if(useNaturalUnits) {
            return natural;
        } else {
            return mmToIn(natural);
        }
        
        
    }
    
    /**
     * Get the natural length from a display length.
     * @param length  The length to convert (or preserve). 
     * @return The number converted or preserved based on natural units setting.
     */
    public final static double getNaturalLength(double length) {
        
        if(useNaturalUnits) {
            return length;
        } else {
            return inToMM(length);
        }
        
    }
    
    /**
     * Notify listeners.
     */
    private static void onUnitsChange() {
        
        for(UnitsListener l : listeners) {
            l.unitsDisplayChanged();
        }
        
    }
    
    /**
     * Add a listener.
     * @param listener The units listener to add.
     */
    public final static void addListener(UnitsListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener.
     * @param listener The units listener to remove.
     */
    public final static void removeListener(UnitsListener listener) {
        
        listeners.remove(listener);
        
    }
    
}
