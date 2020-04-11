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
 * Attribute to contain a boolean value.
 * @author Tom
 */
public class BooleanAttribute extends BaseAttribute {
    
  
    private boolean _value;
    
    /**
     * Class constructor.
     * @param name  The name of the attribute.
     * @param value The boolean value.
     * @param description Description of the attribute.
     */
    public BooleanAttribute(String name, boolean value, String description) {
        super(name, description);
        
        _value = value;
        
    }
    
    /**
     * Set the Attribute.
     * @param value The value of the boolean.
     */
    public void setBoolean(boolean value) {
        boolean oldValue = _value;
        
        _value = value;
        
        //notify owner
        AttributeChangeEvent acEvent = new AttributeChangeEvent(this, oldValue, _value );
        onChange(acEvent);
    }
    
 
    
    /**
     * Get the boolean for SQL. 
     * @return The string value for SQL insert.
     */
    @Override
    public String getSQLInsert() {
        return Boolean.toString(_value);
    }
    

   /**
     * Value for Boolean - Attribute for boolean needs to override this.
     *
     * @return The boolean value
     */
    
    public boolean getBooleanValue() {
        return _value;
    }    
    

    /**
     * Get the SQL data type for storage.  Booleans are stored as integers.
     * @return The data storage type used in the database (integer).
     */
    @Override
    public AttributeDataType getSQLType() {
        return AttributeDataType.INT; 
    }
    
   
    /**
     * Get the boolean attribute editor component.
     * @return The JComponet for boolean editing.
     */
    @Override
    public JComponent getEditor() {
        return  new BooleanAttributeEditor(this);
    }    

    /**
     * Set from a general object instance.
     * For boolean this should be a boolean.
     * @param object The object to cast to a boolean value.
     */
    @Override
    public void setFromObject(Object object) {
        
        if(object instanceof Boolean) {
            //coming from the undo queue
            boolean newValue = (Boolean)object;
            setBoolean(newValue);
        } else if (object instanceof Integer) {
            //coming from sql
            int intObj = (Integer)object;
            
            boolean b = false;
            if(intObj == 0) {
                b = false;
            } else if (intObj ==1) {
                b = true;
            }           
            
            
            setBoolean(b);
            
        }
        
        
    }
    
    
    
    
    
}
