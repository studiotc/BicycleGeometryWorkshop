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
package org.bicycleGeometryWorkshop.attributes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class is a set of all the attributes associated with a BaseComponent.  This class 
 * also serves as an intermediary for all attribute change events (it passes them to the component level via the owner).
 * <p>
 * The AttributeSet creates a default name attribute for the set.  The name attribute is used 
 * for general purposes and also for display in the Library functions for import/export to Library.
 * 
 *
 * @author Tom
 */
public class AttributeSet implements AttributeChangeListener {
    
    //name/key for 'name' attribute that each set owns
    public static final String NAME_ATT_ID = "Name";
    
    private StringAttribute _attName;
 
    private String _name;
    

    
    //use a linked hashmap to preserve the order 
    //in which the attributes are added to the set
     private LinkedHashMap<String, BaseAttribute> _attributes;
    
    private AttributeSetOwner _owner;
    
    private AttributeSetPanel _editor;
    

    /**
     * Class constructor.
     * @param name  The name of the attribute set.
     * @param owner The owner of the attribute set.
     */
    public AttributeSet(String name, AttributeSetOwner owner) {
        this(name, "Default", owner);
    }
    
    /**
     * Main Constructor
     * @param setName The name of the AttributeSet (as appears in the editor title).
     * @param defaultName The default name assigned to the name attribute of the set.
     * @param owner The AttributeSetOwner that is notified on changes.
     */
    public AttributeSet(String setName, String defaultName, AttributeSetOwner owner) {
        
        _attributes = new LinkedHashMap();
        
        //attribute set name
        _name = setName;
        //owner
        _owner = owner;
        //editor
        _editor  = new AttributeSetPanel(this);
        //default name identifier for the component
        _attName = makeNameAttribute(defaultName);        
        
    }
    
    /**
     * Helper function to make the name attribute for the set.
     * @param defaultName  The name of the attribute ('Name').
     * @return The name attribute.
     */
    private  StringAttribute makeNameAttribute(String defaultName) {
        return addString(NAME_ATT_ID, defaultName, "Name of the component.");
    }
    
    
    /**
     * The Name of the AttributeSet
     * @return The name of the AttributeSet
     */
    public String getName() {
        return  _name;
    }
    
    
    /**
     * Get the Name Attribute that is a member of the set - not the same as the name of the set itself.
     * @return The string attribute that holds the name value for the attribute set.
     */
    public StringAttribute getNameAttribute() {
        return _attName;
    }
    
    
    /**
     * This needs to work after open - check call chain...
     * @return An AttributeSetPanel that holds all the editors for the AttributeSet.
     */
    public AttributeSetPanel getEditor() {
        

        //force update before it's returned
        //it's intial state may not be packed... TODO.. packEditor..
        _editor.attributesUpdated();

        return  _editor;
       // return new AttributeSetPanel(this);
        
    }
    
    
    /**
     * Add an attribute to the set and setup notifications.
     * 
     * 
     * @param attr Attribute to add to set.
     */
    public void addAttribute(BaseAttribute attr) {
        
        String name = attr.getName();
        
        //remove previous...
        if(!_attributes.containsKey(name)) {
           _attributes.remove(name);
        }
        
        //set listener for notifications
        //attr.addListener(this);

        attr.setOwnerSet(this);
        
        //add attribute
         _attributes.put(name, attr);
         
        
    }
    
    /**
     * Factory method to add a BooleanAttribute to the set.
     * @param name  Name of the Attribute.
     * @param value Value of the Attribute.
     * @param description  Description of the Attribute for display on screen.
     * @return The BooleanAttribute that was created and added to the set.
     */
    public BooleanAttribute addBoolean(String name, boolean value, String description) {
        
        BooleanAttribute ba = new BooleanAttribute(name, value, description);
        
        addAttribute(ba);
        return ba;
        
    }
    
    /**
     * Factory method to add a ColorAttribute to the set.
     * 
     * @param name The name of the Attribute.
     * @param color The color value of the Attribute.
     * @param description Description of the Attribute for display on screen.
     * @return The ColorAttribute that was created and added to the set.
     */
    public ColorAttribute addColor(String name, Color color, String description) {
        
        ColorAttribute ca = new ColorAttribute(name, color, description);
        addAttribute(ca);
        return ca;
        
    }
    
    

    /**
     *  Factory method to add an EnumAttribute to the set.
     *  This creates an Enum attribute and adds it to the set.
     * 
     * @param name The name of the Attribute.
     * @param values The list of Enum values.
     * @param value  The current Enum value to use.
     * @param description Description of the Attribute for display on screen.
     * @return The EnumAttribute that was created and added to the set.
     */
    public  EnumAttribute addEnum(String name, Enum[] values, Enum value, String description) {
        
        EnumAttribute ea = new EnumAttribute(name, values,  value, description);
        addAttribute(ea);
        return ea;
        
    }    
    
    
    
    /**
     * Factory method to add a DoubleAttribute to the set.
     * This double has a suffix
     * @param name The name of the Attribute.
     * @param value The value of the attribute.
     * @param min The minimum value of the DoubleAtribute.
     * @param max The maximum value of the DoubleAttribute.
     * @param suffix The suffix to display for the DoubleAttribbute.
     * @param description Description of the Attribute for display on screen.
     * @return  The DoubleAttribute that was created and added to the set.
     */
    public DoubleAttribute addDoubleSuffix(String name, double value, double min, double max, String suffix, String description) {
        
        DoubleAttribute da = new DoubleAttribute(name,value,min,max, description);
        da.setSuffix(suffix);
        addAttribute(da);
        return da;
        
    }
    
    
    /**
     * Factory method to add a DoubleAttribute to the set.
     * 
     * @param name The name of the Attribute.
     * @param value The value of the attribute.
     * @param min The minimum value of the DoubleAtribute.
     * @param max The maximum value of the DoubleAttribute.
     * @param description Description of the Attribute for display on screen.
     * @return  The DoubleAttribute that was created and added to the set.
     */
    public DoubleAttribute addDouble(String name, double value, double min, double max, String description) {
        
        DoubleAttribute da = new DoubleAttribute(name,value,min,max, description);
        addAttribute(da);
        return da;
        
    }    
    
    
    /**
     * Factory method for adding a Length attribute to the set.  This creates and ads a length attribute to the set.  
     * This Length is expressed in millimeters internally.
     * @param name  The name of the length.
     * @param value The value of the length (in mm).
     * @param min The minimum value (in mm).
     * @param max  The maximum value (in mm).
     * @param description  The description of the length.
     * @return The LengthAttribute that was created.
     */
   public LengthAttribute addLength(String name, double value, double min, double max, String description) {
        
        LengthAttribute la = new LengthAttribute(name,value,min,max, description);
        addAttribute(la);
        return la;
        
    }    
    
    
   /**
    * Factory method to add a Sting attribute.  This creates a string attribute and adds it to the set.
    * @param name  The name of the attribute.
    * @param value The current string value of the attribute.
    * @param description Description of the Attribute for display on screen.
    * @return The SringAttribute that was created and added to the set.
    */
    public StringAttribute addString(String name, String value, String description) {
        StringAttribute sa = new StringAttribute(name,value, description);
        addAttribute(sa);
        
        return sa;
    }
    
 
    
    /**
     * Attribute Changed Notification.  This is called when an attribute has changed.
     * 
     * @param attEvent The attribute event.
     */
    @Override
    public void attributeChanged(AttributeChangeEvent attEvent) {
        
        //System.out.println("Attribute Set: Attribute Changed Notification: " + attEvent);
        
        //attribute has changed event
        if(_owner != null) {
            _owner.attributeInSetChanged(this, attEvent);
        }
 
    }
    
    /**
     * Get an attribute from the set. (not currently used)
     * @param name  The name of the attribute to retrieve from the set.
     * @return The attribute or null if the attribute was not found in the set.
     */
    public BaseAttribute getAttribute(String name) {
        
        BaseAttribute attr = null;
        
        if(name.isEmpty()) return null;
        
        if(_attributes.containsKey(name)) {
            attr = _attributes.get(name);
        }
        
        return attr;
        
    }
    
    /**
     * Get an ArrayList of the Attributes.
     * @return The list of Attributes.
     */
    public ArrayList<BaseAttribute> getAttributes() {
        
        ArrayList<BaseAttribute> atts = new ArrayList();
        
         return new ArrayList(_attributes.values());
 
    }
    
    
    

    /**
     * The string form of the AttributeSet is simply the name of the AttributeSet.
     * @return The name of the attribute set.
     */
    @Override
    public String toString() {
        return _name;
    }
    



    
}
