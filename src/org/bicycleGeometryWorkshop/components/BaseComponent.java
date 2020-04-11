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

import java.awt.Color;
import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.attributes.AttributeSet;
import org.bicycleGeometryWorkshop.attributes.AttributeSetOwner;
import org.bicycleGeometryWorkshop.attributes.DoubleAttribute;
import org.bicycleGeometryWorkshop.attributes.EnumAttribute;
import org.bicycleGeometryWorkshop.attributes.StringAttribute;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import org.bicycleGeometryWorkshop.attributes.AttributeChangeEvent;
import org.bicycleGeometryWorkshop.attributes.BooleanAttribute;
import org.bicycleGeometryWorkshop.attributes.ColorAttribute;
import org.bicycleGeometryWorkshop.attributes.LengthAttribute;

/**
 * BaseComponent class is basic building block for all the parts of a Bicycle.
 * All the various parts (frame, wheels, etc.) are built from this class.
 * <p>
 * This class is used to provide a  common  mechanism for storing 
 * an attribute set and geometry associated with a visual (physical) component.  
 * The component uses the attribute set for the inputs,
 * generates the geometry, and updates the bounding box.  
 * The component bounding box is managed internally,
 * and is used for calculating the display bounding box.
 * 
 * <p>
 * When an attribute within the component's attribute set is changed, the event handler
 * calls update on the component. Update in turn calls updateGeometry() and then updateBounds();
 * Every component defines updateGeometry(), and can not implement a unique updateBounds().
 * The geometry collection must be maintained (not left empty if references are broken) for the
 * updateBounds mechanism to function.
 * <p>
 * There are also factory methods for creating attributes and geometry.
 * 
 * @author Tom
 */
public abstract class BaseComponent implements AttributeSetOwner {
    
    private String _name;
    private Point2D _basePoint;
    private Rectangle2D _bounds;
    
    private AttributeSet _attributes;
    
    private ArrayList<Shape> _geometry;
    
    private ImageIcon _icon;
    
    protected ComponentOwner _owner;
    
    private ArrayList<BaseComponent> _subComponents;
    

    
    /**
     * Class constructor.
     * @param name  The name of the component.
     * @param owner The owner of the component for event notification.
     */
    public BaseComponent(String name, ComponentOwner owner) {
        
        _name = name;
        _attributes = new AttributeSet(name, this);
        
        _owner = owner;
        
        _subComponents = new ArrayList();
        
        _geometry = new ArrayList();
        
        URL defaultIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/ComponentIcon16.png");
        _icon = new ImageIcon(defaultIconURL);
        
        _basePoint = new Point2D.Double(0,0);
        _bounds = new Rectangle2D.Double(0,0,1,1);
        
        
    }

    /**
     * Get the Attribute set for this component
     * @return The attribute set.
     */
    public final AttributeSet getAttributeSet() {
        return _attributes;
    }    
    

    
    

    
    /**
     * Check to see if this component has sub-components.
     * @return True if this component has sub-components, false otherwise.
     */
    public final boolean hasSubComponents() {
        return !_subComponents.isEmpty();
    }
    
    /**
     * Get the list of sub-components.
     * @return The list of sub-components.
     */
    public final ArrayList<BaseComponent> getSubComponents() {
        return _subComponents;
    }
    
    /**
     * Add a sub-component to this component.
     * @param component The component to add as a sub-component.
     */
    public final void addSubComponent(BaseComponent component) {
        _subComponents.add(component);
    }
    
    
    /**
     * Set the icon from the resource directory icons.
     * The icon is used for display in the UI.
     * 
     * @param imageName The name of the icon with the extension.
     */
    public final void setIcon(String imageName) {
        String path = "/org/bicycleGeometryWorkshop/resources/icons/" + imageName;
       URL defaultIconURL = this.getClass().getResource(path);
        _icon = new ImageIcon(defaultIconURL);        
        
    }
    
    /**
     * get the image icon for the component
     * @return The component icon.
     */
    public ImageIcon getIcon() {
        return _icon;
    }
    
    

    /**
     * Add a Boolean attribute to the component.
     * @param name The name of the attribute.
     * @param value The value of the
     * @param description The description for the boolean.
     * @return The created boolean attribute.
     */
    protected final BooleanAttribute addBooleanAttribute(String name, boolean value, String description) {
        
        BooleanAttribute att = _attributes.addBoolean(name, value, description);
        return att;
        
    }
    
    /**
     * Add a Color attribute to the component.
     * @param name  The name of the attribute.
     * @param color The color to use.
     * @param description The description for the color.
     * @return The created color attribute.
     */
    protected final ColorAttribute addColorAttribute(String name, Color color, String description) {
        
        ColorAttribute att = _attributes.addColor(name, color, description);
        return att;
    }
    

    /**
     * Add a Length attribute to the component.  This is the most common attributes and 
     * is used to describe all linear measurements.  The length attribute is always considered to be in millimeters.
     * The value does not change when it is displayed in inches (that is solely a display mechanism).
     * 
     * @param name  The name of the attribute.
     * @param value  The length in millimeters.
     * @param min  The minimum value.
     * @param max  The maximum value.
     * @param description  The description of this attribute.
     * @return The created double attribute.
     */
    protected final DoubleAttribute addLengthAttribute(String name, double value, double min, double max, String description) {
        
        LengthAttribute att = _attributes.addLength(name, value, min, max, description);
        return att;
    }    
    
    
    /**
     * Add an Angle attribute to the component.  This is a double that is considered an angle described in degrees.
     * The suffix is set to 'deg'.
     * @param name  The name of the attribute.
     * @param value The angle in degrees.
     * @param min The minimum value.
     * @param max  The maximum value.
     * @param description The description of the attribute.
     * @return The created double attribute.
     */
    protected final DoubleAttribute addAngleAttribute(String name, double value, double min, double max, String description) {
        
        DoubleAttribute att = _attributes.addDoubleSuffix(name, value, min, max, "deg", description);
        return att;
    }      
    
    /**
     * Add a Double attribute to the component.  
     * 
     * @param name  The name of the attribute.
     * @param value The angle in degrees.
     * @param min The minimum value.
     * @param max  The maximum value.
     * @param description The description of the attribute.
     * @return The created double attribute.
     */
    protected final DoubleAttribute addDoubleAttribute(String name, double value, double min, double max, String description) {
        
        DoubleAttribute att = _attributes.addDouble(name, value, min, max, description);
        return att;
    }     
    

    /**
     * Add a string attribute to the component.
     * @param name The name of the attribute.
     * @param value  The value of the string.
     * @param description Description of this attribute.
     * @return The created string attribute.
     */
    protected final StringAttribute addStringAttribute(String name, String value, String description) {
        
        StringAttribute att = _attributes.addString(name, value, description);
        return att;
    }
    
    
    
    
    /**
     * Add an enum attribute to the component.
     * @param name The name of the attribute.
     * @param values The list of the enum values.
     * @param value The default value of the enum.
     * @param description  The description of the is enum attribute.
     * @return The created enum attribute.
     */
    protected final EnumAttribute addEnumAttribute(String name, Enum[] values, Enum value, String description) {
        
        EnumAttribute att = _attributes.addEnum(name, values,  value, description);
        return att;        
        
    }
    
    
    /**
     * Add a Line to the geometry collection
     * @return The created line.
     */
    protected final Line2D addLine2D() {
        
        Line2D line = new Line2D.Double(0,0,1,0);
        addGeometry(line);
        return line;
    }
    
    /**
     * Add an arc to the geometry collection
     * @return The created arc.
     */
    protected final Arc2D addArc2D() {
        
        Arc2D arc = new Arc2D.Double(0,0,1,1,0,Math.PI,Arc2D.OPEN);        
        addGeometry(arc);
        return arc;
    }
    
    /**
     * Add an Ellipse to the geometry collection
     * @return The created ellipse.
     */
    protected final Ellipse2D addEllipse2D() {
        
        Ellipse2D ell = new Ellipse2D.Double(0,0,10,10);
        addGeometry(ell);
        return ell;
        
    }
    
    /**
     * Add the shape to the geometry collection.
     * @param geom The shape to add
     */
    protected final void addGeometry(Shape geom) {
        
        _geometry.add(geom);
        
    }
    
    /**
     * Clear the Geometry Collection.
     */
    protected final void clearGeometry() {
        _geometry.clear();
    }
    
    /**
     * Helper function to render all the geometry.
     * @param g2 The graphics object to render to.
     */
    protected final void renderGeometry(Graphics2D g2) {
        
        for (Shape s : _geometry) {
            g2.draw(s);
        }         
        
    }
    
    
    /**
     * Internal componentChanged calls override updateGeometry and
     * then updates the bounding box from the geometry.
     */
    protected final void update() {
        
        updateGeometry();
        updateBounds();
        
        //could do dependency chaining here....
    }    
    
    /**
     * Method to componentChanged Geometry from input attribute(property) changes.
     */
    public abstract void updateGeometry();
    
    
    /**
     * Update the Bounds from the Geometry Collection
     */
    protected void updateBounds() {
        
        
      Rectangle2D bounds = new Rectangle2D.Double();
      boolean hasFirst = false;
        
       for(Shape s : _geometry) {
           
           Rectangle2D curBounds = s.getBounds2D();
           
           if(!hasFirst) {
               bounds = new Rectangle2D.Double();
               bounds.setFrame(curBounds);
               hasFirst = true;
           } else {
              bounds = Utilities.getUnionBoolean(bounds, curBounds);
           }
           
       }//end for
        
        _bounds.setFrame(bounds);
                
    }
    
    /**
     * Get the Name of the Component
     * @return The name of this component
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Get the bounds of the geometry
     * @return The bounds of the geometry
     */
    public Rectangle2D getBounds() {
        return _bounds;
    }   
    
    /**
     * Set the bounds - used internally for overriding getBounds().
     * @param bounds The bounds of the component.
     */
    protected void setBounds(Rectangle2D bounds) {
        _bounds = bounds;
    }
    
   /**
    * Get the list of geometry.
    * @return This components list of geometry.
    */
    public  ArrayList<Shape> getGeometry() {
        return _geometry;        
    }

    
    /**
     * Set the Base Point of the component.  The base point is
     * the attachment point for components.
     * @param basePoint The base point of the component.
     */
    public void setBasePoint(Point2D basePoint){
        
        _basePoint.setLocation(basePoint);
        
    }   
    
    /**
     * Get the Base Point (safe copy).
     * @return The base point of the component.
     */
    public Point2D getBasePoint() {
        return new Point2D.Double(_basePoint.getX(),_basePoint.getY() );
    }
    
    /**
     * Event notification from the attribute set that an attribute has changed.
     * @param attSet  The attribute set that has changed (same as the one stored internally)
     * @param attEvent The attributeChange event - contains all the information about the attribute and values.
     * 
     */
    @Override
    public  void attributeInSetChanged(AttributeSet attSet,  AttributeChangeEvent attEvent) {
        
        //update the component
        update();
        
        ComponentChangeEvent cce = new ComponentChangeEvent(this, attEvent);
        
        //notify owner if not null
        if(_owner != null) {
            _owner.componentChanged(cce);
        }
    }
    
    

    
}
