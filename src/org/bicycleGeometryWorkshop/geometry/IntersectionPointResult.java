package org.bicycleGeometryWorkshop.geometry;

/**
 *  Enumerator for the Intersection point result.  
 *  This is the result type for an intersection operation.
 * @author Tom
 */
public enum IntersectionPointResult {
    
    /**
     * The operation was not successful - there is no point (i.e. No intersection, etc.).
     */
    FAILURE,
    
    /**
     * There was a single point as a result of the operation (i.e. Intersection of two lines).
     */
    ONE_POINT,
    
    /**
     * There were two points as a result of the operation (i.e. Line passing through Circle).
     */
    TWO_POINTS;
    
    
}
