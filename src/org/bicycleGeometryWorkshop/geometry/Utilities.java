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
package org.bicycleGeometryWorkshop.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import org.bicycleGeometryWorkshop.app.UnitsDisplay;

/**
 * General collection of utilities and helper functions.
 * @author Tom
 */
public class Utilities {

    /**
     * Get the expanded bounds that include both rectangles
     *
     * @param r1 Rectangle to include
     * @param r2 Rectangle to include
     * @return Rectangle that includes both rectangles
     */
    public static Rectangle2D getUnionBoolean(Rectangle2D r1, Rectangle2D r2) {

        double x1 = Math.min(r1.getMinX(), r2.getMinX());
        double y1 = Math.min(r1.getMinY(), r2.getMinY());
        double x2 = Math.max(r1.getMaxX(), r2.getMaxX());
        double y2 = Math.max(r1.getMaxY(), r2.getMaxY());

        return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);

    }

    /**
     * Polar Point.
     *
     * @param base The base Point
     * @param dist Polar distance
     * @param theta Polar angle
     * @return Point at polar coordinates from base point
     */
    public static Point2D polarPoint(Point2D base, double dist, double theta) {

        double x = base.getX() + dist * Math.cos(theta);
        double y = base.getY() + dist * Math.sin(theta);

        return new Point2D.Double(x, y);

    }

    /**
     * Lengthen a line. Project a distance from the end or start point. This
     * will lengthen (or shorten with negative distance) a line a relative
     * distance.
     *
     * @param sp Start point of the Line.
     * @param ep End point of the Line
     * @param dist The distance to project the line (relative lengthen/shorten).
     * @param start True if project off of start point, false projects off of
     * end point.
     * @return The lengthened (or shortened) line.
     */
    public static Line2D lengthenLine(Point2D sp, Point2D ep, double dist, boolean start) {
        Line2D newLine = new Line2D.Double();
        //double theta = 0;
        if (start) {
            double theta = anglePointPoint(ep, sp);
            //project off of start point
            Point2D newStart = polarPoint(sp, dist, theta);
            newLine.setLine(newStart, ep);
        } else {
            double theta = anglePointPoint(sp, ep);
            Point2D newEnd = polarPoint(ep, dist, theta);
            newLine.setLine(sp, newEnd);
        }

        return newLine;

    }

    /**
     * Offset a line. Creates offset lines to the given line.
     *This can create a parallel shape if start and end distances are the same,
     * or a tapered shape (rear triangle) if different distances are given.
     * @param line The line to offset.
     * @param startDist  The start distance to offset.
     * @param endDist  The end distance to offset.
     * 
     * @param left True to offset to left side (defined by start point to end),
     * false to offset to right side.
     * @return The offset line.
     */
    public static Line2D offsetLine(Line2D line, double startDist, double endDist, boolean left) {

        Point2D sp = line.getP1();
        Point2D ep = line.getP2();

        double theta = anglePointPoint(sp, ep);
        double pTheta = 0;
        double HPI = Math.PI / 2;

        if (left) {
            //left is positive rotation
            pTheta = theta + HPI;
        } else {
            //right is negative roation
            pTheta = theta - HPI;
        }

        //create polar offset point
        Point2D projPntS = polarPoint(new Point2D.Double(), startDist, pTheta);
        Point2D projPntE = polarPoint(new Point2D.Double(), endDist, pTheta);

        //add the polar point to the line points
        Point2D osp = addPoints(sp, projPntS);
        Point2D oep = addPoints(ep, projPntE);

        //return the offset line
        return new Line2D.Double(osp, oep);

    }

    /**
     * Add two points
     *
     * @param a Point a.
     * @param b Point b.
     * @return The result of adding the points.
     */
    public static Point2D addPoints(Point2D a, Point2D b) {

        return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());

    }

    /**
     * Trim a line by a line. Calculates the intersection of two lines, and
     * trims the first line to the intersection point. Either the start point or
     * end point is preserved based on the flag for start.
     *
     * @param line Line to trim.
     * @param trim Line to trim to.
     * @param start True to keep the start point, false to keep the end point.
     * @return The trimmed line.
     */
    public static Line2D trimLineToLine(Line2D line, Line2D trim, boolean start) {

        Point2D sp = new Point2D.Double();
        Point2D ep = new Point2D.Double();
        Point2D ip = new Point2D.Double();

        IntersectionPoint ipObj = lineLineIntersect(line, trim);

        if (ipObj.result() == IntersectionPointResult.ONE_POINT) {
            ip = ipObj.getIntersection1();
        }

        //preserve start or end?
        //do preserve line direction!
        if (start) {
            sp.setLocation(line.getP1());
            ep.setLocation(ip);
        } else {
            sp.setLocation(ip);
            ep.setLocation(line.getP2());
        }

        //return the trimmed line
        return new Line2D.Double(sp, ep);
    }

    /**
     * Trim a line to a circle.  Preserves the line direction.
     * @param line  The line to trim.
     * @param center  Center of circle.
     * @param radius  Radius of circle.
     * @param start True to keep line start point, false to keep line end point.
     * @return The trimmed line or a copy of the same line if trim fails.
     */
    public static Line2D trimLineToCircle(Line2D line, Point2D center, double radius, boolean start) {

        IntersectionPoint ipObj = lineCircleIntersect(line.getP1(), line.getP2(), center, radius);
        IntersectionPointResult result = ipObj.result();
       
        Line2D trimLine = new Line2D.Double();
        
        switch(result) {
            
            case ONE_POINT :
                
                if(start) {
                    //preserve start
                   trimLine.setLine(line.getP1(), ipObj.getIntersection1());
                    
                } else {
                    //preserve end point and direction
                   trimLine.setLine( ipObj.getIntersection1(), line.getP2());
                    
                }
                break;
  
            case TWO_POINTS:
                
                Point2D pa = new Point2D.Double();
                Point2D pb = new Point2D.Double();
                
                //which line point to use?
                if(start) {
                    pa = line.getP1();
                } else {
                    pa = line.getP2();
                }
                //dist squared to int points
                double d1 = pa.distanceSq(ipObj.getIntersection1());
                double d2 = pa.distanceSq(ipObj.getIntersection2());
                
                //use closest point
                if(d1 < d2) {
                    pb = ipObj.getIntersection1();
                } else {
                    pb = ipObj.getIntersection2();
                }
                
                if(start) {
                    //point a is start point
                   trimLine.setLine(pa, pb);
                } else {
                    //point a is end point
                   trimLine.setLine(pb, pa);
                }                
 
                break;
                
            case FAILURE:
                //return same line - but use copy for consistancy
                trimLine.setLine(line.getP1(), line.getP2());
                
                break;
            
        }
        
        return trimLine;
        
    }

 
    /**
     * Vector angle.  Constructs two vectors from the 3 points and calculates the angle between them.
     * @param basePoint  Base point.
     * @param pointA  The first point (end point of first vector).
     * @param pointB The second point (end point of second vector).
     * @return The angle between the vectors
     */
    public static double vectorAngle(Point2D basePoint, Point2D pointA, Point2D pointB) {

        Vector2D va = Vector2D.fromLine(basePoint, pointA);
        va.normalize();
        Vector2D vb = Vector2D.fromLine(basePoint, pointB);
        vb.normalize();

        double angle = Vector2D.angle(va, vb);

        return angle;

    }

    /**
     * Angle from start point to end point
     *
     * @param start Start point
     * @param end End Point
     * @return Angle from start point to end point
     */
    public static double anglePointPoint(Point2D start, Point2D end) {

        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        return Math.atan2(dy, dx);
    }

    /**
     * Length of a Line helper function.
     *
     * @param l line to query length from.
     * @return The length of the line.
     */
    public static double lineLength(Line2D l) {

        Point2D p1 = l.getP1();
        Point2D p2 = l.getP2();

        return p1.distance(p2);

    }

    /**
     * Degrees to Radians
     *
     * @param deg Degrees
     * @return Radians
     */
    public static double degreesToRadians(double deg) {

        return Math.PI * deg / 180.0;

    }

    /**
     * Radians to Degrees
     *
     * @param rad Radians
     * @return Degrees
     */
    public static double radiansToDegrees(double rad) {

        return rad * (180.0 / Math.PI);

    }

    /**
     * Get the normalized (0 to 360 degrees) angle in degrees of the radian angle.
     * @param rad  The angle in radians.
     * @return The angle in degrees from 0 to 360.
     */
    public static double radiansToDegreesNorm(double rad) {

        double deg = radiansToDegrees(rad);
        if (deg < 0) {
            deg += 360;
        }
        return deg;
    }

    /**
     * Translate a Point making a copy
     *
     * @param basePoint Point to translate from.
     * @param x X value for translation
     * @param y Y value for translation
     * @return The translated Point.
     */
    public static Point2D translatePoint(Point2D basePoint, double x, double y) {

        return new Point2D.Double(basePoint.getX() + x, basePoint.getY() + y);

    }

    /**
     * Get the Circle Circle intersection
     * 
     * Code derived from example on Paul Bourke's website:
     *  http://paulbourke.net/geometry/circlesphere/
     * (Intersection of two circles)
     * Derived from example by Jonathan Greig:
     * "Contribution from Jonathan Greig."
     * http://paulbourke.net/geometry/circlesphere/tangentpointtocircle.zip
     *
     * @param c0  Circle 0 center point.
     * @param r0 Circle 0 radius.
     * @param c1 Circle 1 center point.
     * @param r1 Circle 1 radius.
     * @return  The intersection point.
     */
    public static IntersectionPoint circleCircleIntersection(Point2D c0, double r0, Point2D c1, double r1) {

        double px0 = c0.getX();
        double py0 = c0.getY();
        double px1 = c1.getX();
        double py1 = c1.getY();

        double dx = px1 - px0;
        double dy = py1 - py0;
        double d = Math.sqrt(dx * dx + dy * dy); //Distance between centers

        /*Circles share centers. This results in division by zero,
      infinite solutions or one circle being contained within the other. */
        if (d == 0.0) {
            return new IntersectionPoint();
        } //Circles do not touch each other
        else if (d > (r0 + r1)) {
            return new IntersectionPoint();
        } //One circle is contained within the other
        else if (d < (r0 - r1)) {
            return new IntersectionPoint();
        }

        double a = ((r0 * r0) - (r1 * r1) + (d * d)) / (2.0f * d);
        //Solve for h by substituting a into a^2 + h^2 = r0^2
        double h = Math.sqrt((r0 * r0) - (a * a));

        //Find point p2 by adding the a offset in relation to line d to point p0
        double px2 = px0 + (dx * a / d);
        double py2 = py0 + (dy * a / d);

        //Tangent circles have only one intersection
        if (d == (r0 + r1)) {
            return new IntersectionPoint(px2, py2);
        }

        //Get the perpendicular slope by multiplying by the negative reciprocal
        //Then multiply by the h offset in relation to d to get the actual offsets
        double mx = -(dy * h / d);
        double my = (dx * h / d);

        //Add the offsets to point p2 to obtain the intersection points
        double ix1 = px2 + mx;
        double iy1 = py2 + my;
        double ix2 = px2 - mx;
        double iy2 = py2 - my;

        return new IntersectionPoint(ix1, iy1, ix2, iy2);

    }//end circleCircleIntersection

    /**
     * Line Line intersection wrapper for two lines.  This uses the pint version for the calculation.
     * @param l1  The first line for the intersection.
     * @param l2  The second line for the intersection.
     * @return The result of the intersection calculation.
     */
    public static IntersectionPoint lineLineIntersect(Line2D l1, Line2D l2) {

        return lineLineIntersect(l1.getP1(), l1.getP2(), l2.getP1(), l2.getP2());

    }

    /**
     * Line Line Intersection using Point2d class.
     *
     * Code derived from example Paul Bourke's website:
     * http://paulbourke.net/geometry/pointlineplane/
     * (Intersection point of two line segments in 2 dimensions)
     * Derived from example by Paul Bourke:
     * "Original C code by Paul Bourke"
     * http://paulbourke.net/geometry/pointlineplane/pdb.c
     *
     * @param s1 Line 1 start point.
     * @param e1 Line 1 end point.
     * @param s2 Line 2 start point.
     * @param e2 Line 2 end point.
     * @return IntersectionPoint with either the solution (intersection point)
     * or failure flag set.
     */
    public static IntersectionPoint lineLineIntersect(Point2D s1, Point2D e1, Point2D s2, Point2D e2) {

        double x1 = s1.getX();
        double y1 = s1.getY();
        double x2 = e1.getX();
        double y2 = e1.getY();
        double x3 = s2.getX();
        double y3 = s2.getY();
        double x4 = e2.getX();
        double y4 = e2.getY();

        
        double den = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        double numA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        double numB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

        //check for same or parallel
        if(den == 0) {
            if(numA == 0 && numB == 0) {
                //coincident - just fail...
                return new IntersectionPoint();
            } else {
                //parallel: denom == 0
                return new IntersectionPoint(); 
            } 
        }
        
        
        //test if intersection is on segment
        double mua = numA / den;
        double mub = numB / den;

        //test if intersection is on line
        if (mua < 0 || mua > 1 || mub < 0 || mub > 1) {
            //do nothing here to allow for apparent intersection (projected)
            //return new IntersectionPoint();
        }

        double lix = x1 + mua * (x2 - x1);
        double liy = y1 + mua * (y2 - y1);

        return new IntersectionPoint(lix, liy);

    }//LineIntersect

    /**
     * Line Circle Intersection
     *  
     * Code derived from example Paul Bourke's website:
     * http://paulbourke.net/geometry/circlesphere/
     * 
     * (Intersection of a Line and a Sphere (or circle))
     * Derived from example by Iebele Abel.
     * "Source code example by Iebele Abel."
     * http://paulbourke.net/geometry/circlesphere/source.cpp
     *
     * @param sp Line start point.
     * @param ep Line end point.
     * @param cp Circle center point.
     * @param radius Circle radius.
     * @return Intersection result with intersection points if there was a
     * solution.
     */
    public static IntersectionPoint lineCircleIntersect(Point2D sp, Point2D ep, Point2D cp, double radius) {

        double x1 = sp.getX();
        double y1 = sp.getY();
        double x2 = ep.getX();
        double y2 = ep.getY();
        double x3 = cp.getX();
        double y3 = cp.getY();
        double r = radius;

//        double x, y, z;
        double a, b, c, mu, i;

        double z1 = 0;
        double z2 = 0;
        double z3 = 0;

        a = square(x2 - x1) + square(y2 - y1); // + square(z2 - z1);
        b = 2 * ((x2 - x1) * (x1 - x3)
                + (y2 - y1) * (y1 - y3)
                + (z2 - z1) * (z1 - z3));
        c = square(x3) + square(y3)
                + square(z3) + square(x1)
                + square(y1) + square(z1)
                - 2 * (x3 * x1 + y3 * y1 + z3 * z1) - square(r);
        i = b * b - 4 * a * c;

        if (i < 0.0) {
            // no intersection
            return new IntersectionPoint();
        }

        if (i == 0.0) {
            // one intersection
            //System.out.println("One Line Circle intersection.");

            mu = -b / (2 * a);
            double ix = x1 + mu * (x2 - x1);
            double iy = y1 + mu * (y2 - y1);
            //p[3] = z1 + mu*(z2-z1);
            return new IntersectionPoint(ix, iy);
        }

        if (i > 0.0) {
            // two intersections
            //System.out.println("Two Line Circle intersections:");
            double mua = (-b + Math.sqrt(square(b) - 4 * a * c)) / (2 * a);
            double mub = (-b - Math.sqrt(square(b) - 4 * a * c)) / (2 * a);
            //segment checks a & b
            boolean sega = mua >= 0.0 && mua <= 1.0;
            boolean segb = mub >= 0.0 && mub <= 1.0;

            //first intersection
            double ix1 = x1 + mua * (x2 - x1);
            double iy1 = y1 + mua * (y2 - y1);
            //second intersection
            double ix2 = x1 + mub * (x2 - x1);
            double iy2 = y1 + mub * (y2 - y1);
            //on segment?
            if (sega & segb) {
                return new IntersectionPoint(ix1, iy1, ix2, iy2);
            } else if (sega) {
                return new IntersectionPoint(ix1, iy1);
            } else if (segb) {
                return new IntersectionPoint(ix2, iy2);
            } else {
                return new IntersectionPoint();
            }

        }

        //compilier hapiness
        return new IntersectionPoint();

    }//end lineCircleIntersect

    /**
     * Helper function - find the square of a number.
     *
     * @param a Number to square.
     * @return The number squared
     */
    public static double square(double a) {
        return a * a;
    }

    /**
     * Format a distance for display (round to one decimal place).
     *
     * @param dist The distance to format.
     * @return The formatted distance with the suffix "mm" (all units in mm)
     */
    public static String formatLength(double dist) {

        DecimalFormat df = new DecimalFormat("#.#");
        String result = df.format(dist);
        result += " mm";

        return result;

    }

    /**
     * Format a length for screen display. This formats a length based on the
     * current settings in UnitsDisplay.
     *
     * @param natural The natural length to display
     * @return The formatted String.
     */
    public static String formatLengthDisplayUnits(double natural) {

        String format = "#.#";
        if (UnitsDisplay.usingNaturalUnits()) {
            format = "#.#"; //mm prec
        } else {
            format = "#.##"; //in prec
        }

        double conv = UnitsDisplay.getDisplayLength(natural);

        DecimalFormat df = new DecimalFormat(format);
        String result = df.format(conv);
        result += " " + UnitsDisplay.getUnitsSuffix();

        return result;

    }

    /**
     * Format an angle for display (round to 1 decimal place).
     *
     * @param angle The angle to format
     * @return The formated angle with degrees ('deg') suffix
     */
    public static String formatAngle(double angle) {

        DecimalFormat df = new DecimalFormat("#.#");
        String result = df.format(angle);
        result += " deg";

        return result;

    }

}
