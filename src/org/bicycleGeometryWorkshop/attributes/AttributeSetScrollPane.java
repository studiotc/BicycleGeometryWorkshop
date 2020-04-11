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

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.JScrollPane;



/**
 *  This class wraps the AttributeSetPanel for display in the UI.  The scroll panel also
 *  manages the component removed and added from the UI events (ContainerListener).  
 *  This is used to manage units system listeners for mm/in display.  The container events are passed down to the individual attribute editors.
 * 
 * @author Tom
 */
public class AttributeSetScrollPane extends JScrollPane implements ContainerListener {
    
    
    private AttributeSetPanel _panel;

    /**
     * Class constructor.  This takes the AttributeSetPanel for display, and to pass events to.
     * @param panel The AttributeSetPanel for display and events.
     */
    public AttributeSetScrollPane(AttributeSetPanel panel) {
        super(panel);
        
        _panel = panel;
        
        init();
        
    }
    
    /**
     * Initialize the listener for the panel.
     */
    private void init() {

        //add container listener
        this.addContainerListener(this);        
        
    }
    

    /**
     * Called when the component is added to the UI (now visible).
     * @param e The container event.
     */
    @Override
    public void componentAdded(ContainerEvent e) {
        
        //System.out.println("Scroll Pane added");
        _panel.parentDisplayed();
        
    }

    /**
     * Called when the component is removed from the UI (not displayed).
     * @param e The container event.
     */
    @Override
    public void componentRemoved(ContainerEvent e) {
        //System.out.println("Scroll Pane removed");
        _panel.parentRemoved();
    }
    
    
    
    
}
