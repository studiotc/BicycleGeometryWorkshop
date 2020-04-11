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
 * Static functions for quadratic and cubic curve functions.  Functions were collected from various examples here:
 * https://rosettacode.org/wiki/Bitmap/B%C3%A9zier_curves/Cubic
 * 
 * 
 * @author Tom
 */
public class Curves {
    
    /**
     * Point on Quadratic Bezier Curve.
     * @param sp The start point of the curve.
     * @param cp The control point of the curve.
     * @param ep  The end pont of the curve.
     * @param t  The 't' value of the curve (scale along length).
     * @return The point on the curve.
     */
    public static Point2D quadraticBezierPoint(Point2D sp, Point2D cp,  Point2D ep, double t) {
        
        
        double x1 = sp.getX();
        double y1 = sp.getY();
        
        double x2 = cp.getX();
        double y2 = cp.getY();

        double x3 = ep.getX();
        double y3 = ep.getY();

        double a = Math.pow((1.0 - t), 2.0);
        double b = 2.0 * t * (1.0 - t);
        double c = Math.pow(t, 2.0);
        double x = a * x1 + b * x2 + c * x3;
        double y = a * y1 + b * y2 + c * y3;        
        
        return new Point2D.Double(x,y);
        
        
    }
    
    
    
    
    /**
     * Point on Cubic BEzier Curve\.
     * @param sp  The start point of the curve.
     * @param cp1  The first control point of the curve.
     * @param cp2 The second control point of the curve.
     * @param ep The end point of the curve.
     * @param t The 't' value of the curve (scale along length).
     * @return The point on the curve.
     */
    public static Point2D cubicBezierPoint(Point2D sp, Point2D cp1, Point2D cp2, Point2D ep, double t) {
        


    double x1 = sp.getX();
    double y1 = sp.getY();

    double x2 = cp1.getX();
    double y2 = cp1.getY();    
    
    double x3 = cp2.getX();
    double y3 = cp2.getY();    

    double x4 = ep.getX();
    double y4 = ep.getY();  
    
    double u = 1.0 - t;
    double a = u * u * u;
    double b = 3.0 * t * u * u;
    double c = 3.0 * t * t * u;
    double d = t * t * t;
    
    
    double x = a * x1 + b * x2 + c + x3 + d * x4;
    double y = a * y1 + b * y2 + c + y3 + d * y4;

    return new Point2D.Double(x,y);
        
    }
    
    
    
}

//cubic bezier examples


//      var d = i / b3Seg;
//      var a = 1 - d;
//      var b = a * a;
//      var c = d * d;
//      a = a * b;
//      b = 3 * b * d;
//      c = 3 * a * c;
//      d = c * d;
//      px[i] = parseInt(a * x1 + b * x2 + c * x3 + d * x4);
//      py[i] = parseInt(a * y1 + b * y2 + c * y3 + d * y4);        
        

//https://rosettacode.org/wiki/Bitmap/B%C3%A9zier_curves/Cubic#JavaScript        
//            val t = i.toDouble() / n
//            val u = 1.0 - t
//            val a = u * u * u
//            val b = 3.0 * t * u * u
//            val c = 3.0 * t * t * u
//            val d = t * t * t
//            pts[i].x = (a * p1.x + b * p2.x + c * p3.x + d * p4.x).toInt()
//            pts[i].y = (a * p1.y + b * p2.y + c * p3.y + d * p4.y).toInt()
