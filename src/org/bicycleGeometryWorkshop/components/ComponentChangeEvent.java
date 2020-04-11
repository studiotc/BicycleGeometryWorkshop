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
package org.bicycleGeometryWorkshop.components;

import org.bicycleGeometryWorkshop.attributes.AttributeChangeEvent;

/**
 *  Component Change Event - fired when an attribute inside the component changes.
 * @author Tom
 */
public class ComponentChangeEvent {
    
    private BaseComponent _component;
    private AttributeChangeEvent _attEvent;
    
    /**
     * Class Constructor.  Construct the event with a reference to the component and th attribute event.
     * @param component  The event component.
     * @param acEvent The event attribute event.
     */
    public ComponentChangeEvent(BaseComponent component, AttributeChangeEvent acEvent) {
        
        _component = component;
        
        _attEvent = acEvent;
        
    }
    
    
    /**
     * Get the Component that triggered the event.
     * @return The component that triggered the event.
     */
    public BaseComponent getComponent() {
        return _component;
    }
    
    
    /**
     * Get the AttributeEvent associated with the component change event.
     * @return The Attribute change event.
     */
    public AttributeChangeEvent getAttributeEvent() {
        
        return _attEvent;
        
    }
    
    /**
     * String form for testing and development.
     * @return The string form of the component event.
     */
    @Override
    public String toString() {
        String mssg = _component.getName();
        mssg += " changed::" + _attEvent.toString();
        return mssg;
    }
    
}
