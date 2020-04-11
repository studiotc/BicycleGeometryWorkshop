/*
 * The MIT License
 *
 * Copyright 2020 Tom.
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

/**
 *  Event Object for a change in Attribute.  This stores the attribute and 
 *  the previous and current values of the attribute.
 * @author Tom
 */
public class AttributeChangeEvent {
    
    private final Object _oldValue;
    private final Object _newValue;
    
  
    
    private BaseAttribute _attribute;
    
    /**
     * Class constructor.
     * @param attribute  The attribute that changed.
     * @param oldValue The old (previous) value of the attribute.
     * @param newValue The new (current) value of the attribute.
     */
    public AttributeChangeEvent(BaseAttribute attribute, Object oldValue, Object newValue) {
        
        _attribute = attribute;
        
        _oldValue = oldValue;
        _newValue = newValue;
        
        
    }
    
    /**
     * The  name of the event attribute.
     * @return The name of the attribute.
     */
    public String getAttrbuteName() {
        return _attribute.getName();
        
    }
    
    /**
     * The event attribute.  This is the attribute that has changed.
     * @return The attribute.
     */
    public BaseAttribute getAttribute() {
        return _attribute;
    }
    
    /**
     * The value of the attribute before it was changed.
     * @return The previous value of the attribute.
     */
    public Object getOldValue() {
        return _oldValue;
    }
    
    /**
     * The current value of the attribute.
     * @return The value of the attribute.
     */
    public Object getNewValue() {
        return _newValue;
    }
    
    /**
     * String form for testing.
     * @return The string describing the event.
     */
    @Override
    public String toString() {
        
        String str = _attribute.getName() + " : " + _oldValue.toString() + " to: " + _newValue.toString();
        
        
        return str;
        
        
    }
    
    
}
