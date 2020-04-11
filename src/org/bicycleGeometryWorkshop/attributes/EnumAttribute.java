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

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JComponent;

/**
 * Attribute to hold an Enum value.
 * @author Tom
 */
public class EnumAttribute extends BaseAttribute {
    
    private Enum _enumValue;
    private ArrayList<Enum> _enumList;    


    
    /***
     * Class constructor.  Constructs an EnumAttribute from an enum value and list of enums
     * along with the name and description.
     * @param name  The name of the enum attribute.
     * @param values List of all enum values.
     * @param value The current enum value.
     * @param description The description of the attribute.
     */
    public EnumAttribute(String name,  Enum[] values, Enum value, String description) {
        super(name,description);
        
        //grab the current enum
        _enumValue = value;
        //list of all teh enums
        _enumList = new ArrayList(Arrays.asList(values));
        
    }
    
    /**
     * Get the SQL type this attribute is to be stored as in the database.
     * @return The SQL type used for storage.
     */
    @Override
    public AttributeDataType getSQLType() {
        return AttributeDataType.TEXT; //"TEXT";
    }    
    /**
     * Get the String value of the currently selected Enum for SQL.
     * Enums are stored as a string representation of their name.
     * @return The String value of the Enum.  
     */
    @Override
    public String getSQLInsert() {
        return _enumValue.name(); //_value;
    }    

    
    /**
     * Get the Enum value.  This needs to be cast to the appropriate Enum.
     * @return The current Enum value.
     */
    public Enum getEnum() {
        
        return _enumValue;
    }
    
    /**
     * Set the and fire the change event. 
     * @param value The enum value to use.
     */
    public void setEnum(Enum value) {
        
        if(_enumList.contains(value)) {
            
            Enum oldValue = _enumValue;
            
            _enumValue = value;
            
            Enum newValue = _enumValue;
            
           AttributeChangeEvent acEvent = new AttributeChangeEvent(this, oldValue, newValue ); 
           onChange(acEvent);            
            
            
        }
        
        
    }
    
   
    
    /**
     * Get a string based list of all the values for display in the editor.
     * @return An array list of the string values of the Enum.
     */
    public ArrayList<String> getValueList() {
        
        ArrayList<String> names = new ArrayList();
        for(Enum e : _enumList) {
            names.add(e.name());
        }
        
        return names; //_enumValues;
    }
    

   
   /**
    * Parses a string to a valid Enum (if possible) 
    * and sets the current Enum value.
    * @param name The name to parse as an Enum.
    * @return True if the name was parsed to an Enum value, false otherwise.
    */
   public boolean setEnumFromString(String name) {
       boolean found = false;
       Enum newEnum = getEnumValue(name);
       if(newEnum != null) {
           
           setEnum(newEnum);
           
       } 
       
       return found;
       
   }
   
   
   /**
    * Check to see if a string is a valid enum in this attribute.
    * This is to make sure a string can be properly converted to the enum.
    * @param name The name of the enum to check.
    * @return True if this is a valid enum name and can be converted to an enum, false otherwise.
    */
    public Enum getEnumValue(String name) {
        Enum target = null;

         for(Enum e : _enumList) {
             if(name.equals( e.name() )){
                 target = e;
                 break;
             }  
         }
        
        return target;
  
    }   
   
   

    /**
     * Get the editor for the enum attribute. 
     * @return The editor for the attribute.
     */
    @Override
    public JComponent getEditor() {
        return new EnumAttributeEditor(this);
    }
    
    /**
     * Set the Enum from an object provided by either SQL (string)
     * or the Undo Queue (Enum).
     * @param object The object to convert to an Enum.
     */
    @Override
    public void setFromObject(Object object) {
        if(object instanceof String) {
            //coming in from SQL
            String newValue = (String)object;
            setEnumFromString(newValue);
        } else if (object instanceof Enum) {
            //coming from undo queue
            Enum newEnum = (Enum)object;
            setEnum(newEnum);
            
        }
    }
    
}
