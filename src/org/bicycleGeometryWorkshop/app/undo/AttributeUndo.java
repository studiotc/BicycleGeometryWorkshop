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
package org.bicycleGeometryWorkshop.app.undo;

import org.bicycleGeometryWorkshop.attributes.AttributeChangeEvent;
import org.bicycleGeometryWorkshop.attributes.BaseAttribute;

/**
 * Attribute undo/redo object.
 * This relies on the underlying events to update the Component/Bicycle/Project/UI.
 * @author Tom
 */
public class AttributeUndo extends BaseUndo {

    private final AttributeChangeEvent _attEvent;
    
    /**
     * Class Constructor.  Stores an AttributeEvent to set and unset an attribute value.
     * @param attEvent The attribute event to store in the undo.
     */
    public AttributeUndo(AttributeChangeEvent attEvent) {
        _attEvent = attEvent;
    }

    /**
     * Undo the attribute change.  This restores the previous attribute value.
     */
    @Override
    public void undo() {
        
        BaseAttribute att = _attEvent.getAttribute();
        att.setFromObject(_attEvent.getOldValue());

    }

    /**
     * Redo the attribute change.  This restores the attribute to the new value.
     */
    @Override
    public void redo() {
        
        BaseAttribute att = _attEvent.getAttribute();
        att.setFromObject(_attEvent.getNewValue());        

    }

}
