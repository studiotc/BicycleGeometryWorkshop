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
package org.bicycleGeometryWorkshop.database;

/**
 *  String enum for organizing table names.
 * These are used for the database table names, 
 * changing them will break compatibility.
 * @author Tom
 */
public enum DataBaseKeys {
    
    /**
     * Rider Geometry
     */
    RIDER_GEOM("RiderGeometry"),
    /**
     * Rider measurements
     */
    RIDER_MEASURE("Rider"),
    /**
     * Rider Pose
     */
    POSE("Pose"),
    /**
     * Preferences
     */
    BICYCLE_PREF("Preferences"),
    /**
     * Bicycle
     */
    BICYCLE("Bicycle"),
    /**
     * Frame
     */
    FRAMESET("FrameSet"),
    /**
     * Wheels
     */
    WHEELS("Wheels"),
    /**
     * Cranks
     */
    CRANKS("Cranks"),
    /**
     * Pedals
     */
    PEDALS("Pedals"),
    /**
     * Seat Post
     */
    SEATPOST("SeatPost"),
    /**
     * Saddle
     */
    SADDLE("Saddle"),
    /**
     * Stem
     */
    STEM("Stem"),
    /**
     * Handlebars
     */
    HANDLEBAR("Handlebar");

    private final String _name;

    /**
     * Constructor for enum.  Construct the enum with the text value.
     * @param text The text to associate with he enum.
     */
    DataBaseKeys(final String name) {
        _name = name;
    }


    /**
     * to String - return the name.
     * @return The name of the database key.
     */
    @Override
    public String toString() {
        return _name;
    }    
    
    
}
