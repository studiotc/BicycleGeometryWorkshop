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
 * Bicycle Change Event - fired when a Bicycle is changed
 * @author Tom
 */
public class BicycleChangeEvent {
    
    private Bicycle _bicycle;
    private ComponentChangeEvent _compEvent;
    private AttributeChangeEvent _attEvent;
    
    private BicycleEventType _eventType;
    
    /**
     * Class constructor.  Construct event with Bicycle and Component event.
     * @param bicycle The bicycle for the event.
     * @param compEvent The component for the event.
     */
    public BicycleChangeEvent(Bicycle bicycle, ComponentChangeEvent compEvent) {
        
        _eventType = BicycleEventType.ComponentAttribute;
        
        _bicycle = bicycle;
        
        _compEvent = compEvent;
        
        _attEvent = null;
         
    }
    
    /**
     * Class constructor.  Construct event with Bicycle and Attribute event.
     * @param bicycle The bicycle for the event.
     * @param attEvent The attribute for the event.
     */
    public BicycleChangeEvent(Bicycle bicycle, AttributeChangeEvent attEvent) {
        
        _eventType = BicycleEventType.BicycleAttribute;
        
        _bicycle = bicycle;
        
        _compEvent = null;
        
        _attEvent = attEvent;
         
    }    
    
    /**
     * Get the event type of the vent.
     * @return The event type.
     */
    public BicycleEventType getEventType() {
        return _eventType;
    }
    
    
    
    /**
     * Get the bicycle that triggered the event
     * @return The Bicycle associated with the event.
     */
    public Bicycle getBicycle() {
        return _bicycle;
    }
    
    /**
     *  Get the ComponenetChangeEvent associated with this event.
     * @return The component event associated with this bicycle event.
     */
    public ComponentChangeEvent getComponentEvent() {
        return _compEvent;
    }
    
    /**
     * Get the attribute set associated with this event.
     * @return The attribute event associated with this event.
     */
    public AttributeChangeEvent getAttributeEvent() {
        
        return _attEvent;
        
    }
    
}
