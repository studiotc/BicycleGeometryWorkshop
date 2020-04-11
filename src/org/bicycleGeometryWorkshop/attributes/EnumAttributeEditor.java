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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 * Editor for EnumAttributes.  This presents a list of all the Enum values in a CombocBox 
 * with the current Enum value as the selected index.
 * @author Tom
 */
public class EnumAttributeEditor  extends JComboBox implements ActionListener, AttributeChangeListener {
    
    private EnumAttribute _attr;
 
    
    private boolean _allowEvents;    
    
    /**
     * Class constructor.  Initialize the editor from the attribute.
     * @param attr The attribute to load in the editor.
     */
    public EnumAttributeEditor(EnumAttribute attr) {
        super();
        _allowEvents = true;
                
        _attr = attr;

        init();        
        
    }
    
    /**
     * Do the initialization.
     */
    private void init() {
        
      
        loadList();
        
        _attr.addListener(this);
        
        //link listener
        this.addActionListener(this);
        
    } 
    
    /**
     * Load the list of attributes for display.
     * The Enum values are converted to a list of strings for the combo box.
     * The selected index is set from the current Enum value;
     */
    private void loadList() {
        
        
        this.removeAllItems();
        
        ArrayList<String> values = _attr.getValueList();
        String value = _attr.getEnum().name();
        
        int index = 0;
        
        
        for(int i = 0; i < values.size(); i++) {
            String s = values.get(i);
            if(s.equals(value)) index = i;
            this.addItem(s);
        }
        
        //set the index
        this.setSelectedIndex(index);        
        
    }
    
    
    /**
     * Action handler for list index changes.  Update the attribute 
     * from the selected item in the list.
     * @param e The action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
       
        if(!_allowEvents) return;
        
        String item = (String)this.getSelectedItem();

        _attr.setEnumFromString(item);
        
    }    

    /**
     * Update the index of the selected item when the attribute changes.
     * @param attEvent The attribute event.
     */
    @Override
    public void attributeChanged( AttributeChangeEvent attEvent) {
        
        _allowEvents = false;
        //eh brute force...
        loadList();        

        _allowEvents = true;
    }
    
    
}
