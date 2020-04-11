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
package org.bicycleGeometryWorkshop.ui.tree;

import org.bicycleGeometryWorkshop.attributes.AttributeSetPanel;
import org.bicycleGeometryWorkshop.components.BaseComponent;
import org.bicycleGeometryWorkshop.components.Bicycle;
import org.bicycleGeometryWorkshop.components.BicycleDisplay;

/**
 * Listener object for the NavigatorTree.  The navigator calls these in response 
 * to menu actions to notify the UI of requested actions to apply to the project.
 *
 * @author Tom
 */
public interface NavigatorListener {
    
    /**
     * Called when a Tree Node is selected to return the editor associated with that node.
     * @param editor AttributeSetPanel selected for loading in UI.
     */
    public void editorSelected(AttributeSetPanel editor);
    
    /**
     * Called to add a Bicycle to the project.
     */
    public void addBicycle();
    
    /**
     * Called to delete a Bicycle
     * @param bicycle The Bicycle to delete.
     *  
     */
    public void deleteBicycle(Bicycle bicycle);
    
    
    /**
     * Called to move a bicycle up in display.
     * @param bicycle  Bicycle to move up.
     * 
     */
    public void moveBicycleUp(Bicycle bicycle);
    
    /**
     * Called to move a bicycle down in display.
     * @param bicycle  Bicycle to move down.
     * 
     */    
    public void moveBicycleDown(Bicycle bicycle);
    
    /**
     * Called to export a component to the library
     * @param component Component to export to library.
     */
    public void exportComponent(BaseComponent component);
    
    /**
     * Called  to import a component from the library.
     * @param component Component used to import into.
     */
    public void importComponent(BaseComponent component);
    
    
    /**
     * Called to set all the Bicycles Display.
     * @param display The Bicycle display mode to apply to all bicycles.
     */
    public void bicycleDisplay(BicycleDisplay display);
    
    /**
     * Called to set all the Bicycles Visibility.
     * @param visible True to set the bicycles as visible, false to turn the bicycles off.
     */
    public void bicycleVisibility(boolean visible);
    
    /**
     * Called to isolate the visibility of a bicycle (turn all others off).
     * @param bicyle The bicycle to isolate
     */
    public void bicycleIsolate(Bicycle bicyle);
    
}
