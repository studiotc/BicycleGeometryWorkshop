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
import javax.swing.JComponent;

/**
 *  Attribute to hold a color value with alpha.
 * @author Tom
 */
public class ColorAttribute extends BaseAttribute {
    
  
    private Color _value;
    
    /**
     * Class Constructor.
     * @param name  The name of the attribute.
     * @param value The color value.
     * @param description Description of the attribute.
     */
    public ColorAttribute(String name, Color value, String description) {
        super(name, description);
        
        _value = value;
        
    }
    
    /**
     * Get the color associated with the attribute.
     * @return The color associated with this attribute.
     */
    public Color getColor() {
        return _value;
    }
    
    /**
     * Set the Attribute
     * @param value The color for the attribute.
     */
    public void setColor(Color value) {
        Color oldColor = _value;
        _value = value;
        //notify owner
        AttributeChangeEvent acEvent = new AttributeChangeEvent(this, oldColor, _value );        
        onChange(acEvent);
    }
    
    /**
     * Get the data type for storage in the database.  
     * Colors are stored in hex string form in the database.
     * @return The data type used for storage in he database (text).
     */
    @Override
    public AttributeDataType getSQLType() {
        return AttributeDataType.TEXT; //"TEXT";
    }    
    

    
    /**
     * Set the Attribute from a string
     * @param text The String to parse
     * @return True, Strings are always accepted (even empty)
     */
    public boolean setFromString(String text) {
        
        boolean result = false;
        String prefix = "#";
        String cString = text;
    
        
        if(!cString.startsWith(prefix)) {
            cString = prefix + cString;
        }
        System.out.println("Decoding color: " + cString);
        //check it out...
        try {
            
            int r = Integer.valueOf( text.substring( 1, 3 ), 16 );
            int g = Integer.valueOf( text.substring( 3, 5 ), 16 );
            int b = Integer.valueOf( text.substring( 5, 7 ), 16 );
            int a = Integer.valueOf( text.substring( 7, 9 ), 16 );
            
            Color newColor = new Color(r,g,b,a);
            //call set color to trigger event
            setColor(newColor);

           result = true;
        } catch (NumberFormatException ex) {
            //do nothing here.. 
            System.out.println("Failed to Decode color: " + cString);
            
        }
        
 
        //onChange();
        return result;
    }
    
    /**
     * Get the String value as a hex formatted string.
     * https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
     * @return The String value of the color (hex formatted string) for SQL insert.
     */
    @Override
    public String getSQLInsert() {
        int r = _value.getRed();
        int g = _value.getGreen();
        int b = _value.getBlue();
        int a = _value.getAlpha();
        
        String hex = String.format("#%02x%02x%02x%02x", r, g, b, a);  
        
        return hex;
    }
    
  
   
    /**
     * Get the editor for this color attribute.
     * @return The editor component for this attribute.
     */
    @Override
    public JComponent getEditor() {
        return  new ColorAttributeEditor(this);
    }    

    
    /**
     * Set from an object.  This expects the object to be either a Color or a string.  
     * The undo queue uses the Color while the database uses the string form.
     * @param object The object to cast or parse into a color.
     */
    @Override
    public void setFromObject(Object object) {
       
        if(object instanceof Color) {
            //this is coming in from the undo queue
            Color newColor = (Color)object;
            setColor(newColor);
        } else if (object instanceof String) {
            //this is incoming from SQL
            String colorStr = (String)object;
            setFromString(colorStr);
            
        }
        
    }
    
    
}
