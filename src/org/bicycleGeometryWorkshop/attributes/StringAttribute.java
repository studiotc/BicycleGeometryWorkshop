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

import javax.swing.JComponent;

/**
 *  Attribute to hold a string.  This is a simple attribute for string values.
 * @author Tom
 */
public class StringAttribute extends BaseAttribute {
    
    private String _value;
    
    /**
     * Class constructor.
     * @param name  The name of he attribute.
     * @param value  The value of the string attribute.
     * @param description Description of the attribute.
     */
    public StringAttribute(String name, String value, String description) {
        super(name, description);
        
        _value = value;
        
    }
    
    /**
     * Set the Attribute
     * @param value String to set as the value of the attribute.
     */
    public void setString(String value) {
        String oldValue = _value;
        _value = value;
        //notify owner
         AttributeChangeEvent acEvent = new AttributeChangeEvent(this, oldValue, _value ); 
        onChange(acEvent);
    }
    
    
    /**
     * Get the SQL type for database storage.  String attributes use the 'TEXT' type.
     * @return The data type used for storage in the database.
     */
    @Override
    public AttributeDataType getSQLType() {
        return AttributeDataType.TEXT; 
    }    
    
    /**
     * Get the String for the SQL statement.
     * @return The string for SQL.
     */
    @Override
    public String getSQLInsert() {
        return _value;
    }    
    
    
    /**
     * Get the String.
     * @return The string value.
     */
    public String getString() {
        return _value;
    }
    
  
    /**
     * Get the editor for the string attribute.
     * @return The editor for this attribute.
     */
    @Override
    public JComponent getEditor() {
        return  new StringAttributeEditor(this);
    }    
    
    /**
     * Set the attribute from an object.  The object must be an instance of a string.
     * @param object The object to cast as a string.
     */
    @Override
     public void setFromObject(Object object) {
        if(object instanceof String) {
            String newValue = (String)object;
            setString(newValue);
        }
    }
    
}
