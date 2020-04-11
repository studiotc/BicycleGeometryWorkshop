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
 * Attribute to hold a double value.
 * @author Tom
 */
public class DoubleAttribute extends BaseAttribute {
    
    private double _value;
    private double _min;
    private double _max;
    
    /**
     * Class constructor.
     * @param name Than name of the attribute.
     * @param value The value of the attribute.
     * @param min The minimum value allowed.
     * @param max  Th maximum value allowed.
     * @param description Description of the attribute.
     */
    public DoubleAttribute(String name, double value, double min, double max, String description) {
        super(name, description);
        
        _value = value;
        _min = min;
        _max = max;
        
    }
    

    /**
     * Get the double for SQL.  
     * @return The value as a string for SQL insert.
     */
    @Override
    public String getSQLInsert() {
        return Double.toString(_value);
    }       
    
    /**
     * Get the data storage type for the database.  The double attribute uses the 'REAL' data type.
     * @return The data storage type (real).
     */
    @Override
    public AttributeDataType getSQLType() {
        return AttributeDataType.REAL; //"REAL";
    }    
    
     
    /**
     * Get the editor for this double attribute.
     * @return The editor component for this attribute.
     */
    @Override
    public JComponent getEditor() {
        return new DoubleAttributeEditor(this);
    }
    
    
    /**
     * Set the Double value.
     * @param value The value of the double.
     */
    public void setDouble(double value) {
        double oldValue = _value;
        _value = value;
        double newValue = _value;
        
        AttributeChangeEvent acEvent = new AttributeChangeEvent(this, oldValue, newValue ); 
        onChange(acEvent);
    }
    
    /**
     * Get the double value
     * @return The double value
     */
    public double getDoubleValue() {
        return _value;
    }
    
    /**
     * Parse the double from a String.
     * @param text  The text to parse as a double.
     * @return True if the string was successfully parsed, false otherwise.
     */
    public boolean setFromString(String text) {
        boolean goodDouble = true;
            try {
                double dval = Double.parseDouble(text);

                //hold to range of min/max
                if(dval < _min) {
                    dval = _min;
                    goodDouble = false;
                }
                
                if(dval > _max) {
                    dval = _max;
                    goodDouble = false;
                }
                
                //set the double value
                 setDouble(dval);   
                
   

            } catch (NumberFormatException ex) {
                //no good number
                goodDouble = false;

            }        

        return goodDouble;
    }  
    
    /**
     * Set the double from an object.  The object must be instance of a double.
     * @param object The object to cast as a double.
     */
    @Override
    public void setFromObject(Object object) {
       
        if(object instanceof Double) {
            double newValue = (Double)object;
            setDouble(newValue);
        }
        
    }    
    
}
