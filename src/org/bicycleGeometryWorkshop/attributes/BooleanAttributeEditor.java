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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;

/**
 *  This is the editor class for a Boolean attribute (JCheckBox). 
 * @author Tom
 */
//public class BooleanAttributeEditor  extends JToggleButton implements ActionListener , AttributeChangeListener {
public class BooleanAttributeEditor  extends JCheckBox implements ActionListener , AttributeChangeListener {    
    
    private BooleanAttribute _attr;

    private boolean _allowEvents;    
    
    /**
     * Class constructor.
     * @param attr The boolean attribute to edit in the editor.
     */
    public BooleanAttributeEditor(BooleanAttribute attr) {
        super();
        _allowEvents = true;
                
        _attr = attr;

        init();        
        
    }
    
    /**
     * Initialize the control.
     */
    private void init() {
               
        //set the selected state
       this.setSelected(_attr.getBooleanValue());

        _attr.addListener(this);
        
        //set listener
        this.addActionListener(this);
        
    } 
    
    /**
     * Action for the toggle button.
     * @param e The action event object.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
       
        //suppress events
        if(!_allowEvents) return;
        
        _attr.setBoolean(this.isSelected());
        
    }    

    /**
     * Listener for attribute changes outside of editor.
     * @param attEvent The attribute change event.
     */
    @Override
    public void attributeChanged( AttributeChangeEvent attEvent) {

        _allowEvents = false;
        
        this.setSelected(_attr.getBooleanValue());        

        _allowEvents = true;
        
    }
    
    
    /**
     * Overriding the setSelected() in order to change the text on the toggle button.
     * 
     * @param selected True if selected, false otherwise.
     */
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        //update the text
        if(selected) {
            this.setText("True");
        } else {
            this.setText("False");
        } 
        
    }
    

    
}
