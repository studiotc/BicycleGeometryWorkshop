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
package org.bicycleGeometryWorkshop.ui.tree;

import org.bicycleGeometryWorkshop.components.Bicycle;

/**
 *  This a custom tree node to hold a reference to a bicycle in the navigator.  
 *  This relies heavily on the ComponentTreeNode for functionality and is used mainly for class identification.
 * @author Tom
 */
public class BicycleTreeNode extends ComponentTreeNode {
    
    private Bicycle _bicycle;
    
    /**
     * Class constructor.
     * @param bicycle The bicycle to associate with the tree node.
     * @param userObject The attributteEdior to associate with this node.
     */
    public BicycleTreeNode(Bicycle bicycle, Object userObject) {
        super(userObject);
        
        _bicycle = bicycle;
        
        setComponent(bicycle);
        
    }
    
    /**
     * Get the bicycle associated with this tree node.
     * @return The bicycle associated with the tree node.
     */
    public Bicycle getBicycle() {
        return _bicycle;
    }
    
    /**
     * The string form is the same as the name of the Bicycle associated with the tree node.
     * @return The name of the bicycle or a text string indicating null (not a null object).
     */
    @Override
    public String toString() {
        
        if(_bicycle != null) {
            
          return _bicycle.getBicycleName();  
        } else {
            return "[NULL BICYCLE]";
        }
        
    }
    
}
