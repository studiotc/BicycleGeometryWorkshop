
package org.bicycleGeometryWorkshop.geometry;

import java.awt.geom.Point2D;



/**
 *  Intersection Result Object - stores the result of an geometry intersection.
 * @author nataga
 */


public class IntersectionPoint {
  
  private final Point2D _intPoint1;
  private final Point2D _intPoint2;
  

  private final IntersectionPointResult _intPointResult;
 
  
  /**
   * Constructor for a single intersection point.
   * @param ix  Intersection point 1, x.
   * @param iy  Intersection point 1, y.
   */
  public IntersectionPoint(double ix, double iy) {
    
      
      _intPoint1 = new Point2D.Double(ix,iy);
      _intPoint2 = new Point2D.Double();
      
      _intPointResult = IntersectionPointResult.ONE_POINT;
      
    
  }//end constructor  
  
  

  /**
   * Constructor for two intersection points.
   * @param ix1  Intersection point 1, x.
   * @param iy1  Intersection point 1, y.
   * @param ix2  Intersection point 2, x.
   * @param iy2  Intersection point 2, y.
   */
  public IntersectionPoint(double ix1, double iy1, double ix2, double iy2) {
    
      
      _intPoint1 = new Point2D.Double(ix1,iy1);
      _intPoint2 = new Point2D.Double(ix2, iy2);
      
      _intPointResult = IntersectionPointResult.TWO_POINTS;


  }//end constructor
  

  
  /**
   * Constructor used to indicate failure: no intersection point.
   */
    public IntersectionPoint() {
    

      _intPoint1 = new Point2D.Double();
      _intPoint2 = new Point2D.Double();
      
      _intPointResult = IntersectionPointResult.FAILURE;        

    
  }
  
    /**
     * The Result state of the Intersection calculation.
     * This can be failure, one point, or two points.
     * @return The intersection point result object.
     */
    public IntersectionPointResult result() {
        
        return _intPointResult;
    }
  
 
    
    /**
     * Get the first Intersection point.
     * @return The first intersection point.
     */
    public Point2D getIntersection1() {
        return _intPoint1;
    }
  
    /**
     * Get the second Intersection point.
     * @return The second intersection point.
     */
    public Point2D getIntersection2() {
        return _intPoint2;
    }    
    
}//end class