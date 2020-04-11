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

import javax.swing.JComponent;

/**
 * Attribute to hold a length.  The length is always considered a distance in millimeters.  
 * This inherits it's functionality from the double attribute, but locks the suffix.  
 * The class is also used for identification.
 * @author Tom
 */
public class LengthAttribute extends DoubleAttribute {
    
    private final static String LENGTH_SUFFIX = "mm";
    
    public LengthAttribute(String name, double value, double min, double max, String description) {
        super( name, value, min, max, description);
        
        super.setSuffix(LENGTH_SUFFIX);
        
    }

    
    
    /**
     * Intercept any suffix changes and ignore them.  This value is always considered to  be millimeters.
     * @param suffix The suffix of the length (ignored).
     */
    @Override
    public void setSuffix(String suffix) {
        //do nothing
    }
    
    /**
     * Return the fixed unit suffix for millimeters.
     * @return The millimeters suffix.
     */
    @Override
    public String getSuffix() {
        return LENGTH_SUFFIX;
    }
    
    /**
     * Get the editor component for the length attribute.
     * @return The editor component.
     */
    @Override
    public JComponent getEditor() {
        return new LengthAttributeEditor(this);
    }
    
    
}
