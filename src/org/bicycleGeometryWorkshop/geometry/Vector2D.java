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
package org.bicycleGeometryWorkshop.geometry;

import java.awt.geom.Point2D;

/**
 * Simple 2D Vector class.
 * @author Tom
 */
public class Vector2D {
    

    private double _x;
    private double _y;
    
    /**
     * Default Constructor
     * 
     */
    public Vector2D() {
        
        _x = 1;
        _y = 0;
        
    }
    
    /**
     * Constructor with X and Y Components
     * @param x X Component of the Vector
     * @param y y Component of the Vector
     */
    public Vector2D(double x, double y) {
        
        _x = x;
        _y = y;
        
    }

    /**
     * Copy Constructor
     * @param other Vector2d to clone
     */
    public Vector2D(Vector2D other) {
        
        _x = other.x();
        _y = other.y();
        
    }
     
    /**
     * Set the the Vector
     * @param x X Component of the Vector
     * @param y Y Component of the Vector
     */
    public void set(double x, double y) {
        
        _x = x;
        _y = y;
 
    }
    
    /**
     * Set the X Component of the Vector
     * @param x X Component of the Vector
     */
    public void x(double x) {
        _x = x;
    }
    
    /**
     * Set the Y Component of the Point
     * @param y Y Component of the Point
     */
    public void y(double y) {
        _y = y;
    }
    
    
    /**
     * 
     * The X Component of the Vector
     * @return The x component.
     */
    public double x() {
        return _x;
    }
    
    
    /**
     * 
     * The Y Component of the Vector
     * @return The y component.
     */
    public double y() {
        return _y;
    }    
    
    /**
     * Scale the Vector
     * @param s Scale factor for the Vector
     */
    public void scale(double s) {
        _x *= s;
        _y *= s;
        
    }
    
    /**
     * Angle of Point from Origin(0,0)
     * @return angle in radians of point from origin(0,0)
     */
    public double angle() {
        return Math.atan2(_y,_x);
    }

    
    /**
     * Magnitude of the Vector
     * @return magnitude of the Vector
     */
    public double magnitude() {
        
        double ms = _x * _x + _y * _y;   
        return Math.sqrt(ms);
        
    }
    
    /**
     * Magnitude of the Vector squared
     * @return squared magnitude of the Vector
     */
    public double magnitudeSquared() {
               
        return _x * _x + _y * _y;        
        
    }
    
    /**
     * Normalize the Vector.
     * 
     */
    public void normalize() {
        
        double m = this.magnitude();
        if(m != 0){
            _x = _x / m;
            _y = _y / m;
        }

    }
    
    /**
     * Dot product of this vector and another
     * @param v Other Vector
     * @return Dot Product
     */
    public double dot(Vector2D v) {
        
        return _x * v.x() + _y * v.y(); 
        
    }
    
    
    /**
     * Dot product of two Vectors
     * @param va First Vector
     * @param vb Second Vector
     * @return Dot product of vectors
     */
    public static double dot(Vector2D va, Vector2D vb) {
        
        return va.x() * vb.x() + va.y() * vb.y();
        
    }
    
    /**
     * Angle between two vectors.
     * @param va First Vector
     * @param vb Second Vector
     * @return Angle between the two vectors.
     */
    public static double angle(Vector2D va, Vector2D vb) {
        
        double dp = dot(va,vb);
        return Math.acos(dp);
        
    }
    
    
    /**
     * Vector from line defined by start and end points.
     * @param start Start point of the line.
     * @param end End point of the line.
     * @return New Vector2d defined by the points - this is not normalized.
     */
    public static Vector2D fromLine(Point2D start, Point2D end) {
        
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return new Vector2D(dx,dy);
  
    }    
    
    
    
}
