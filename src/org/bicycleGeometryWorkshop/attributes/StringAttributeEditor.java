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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This is the editor class for the string attribute.  
 * This is a text field that displays the string.
 * @author Tom
 */
public class StringAttributeEditor extends JTextField implements DocumentListener, ActionListener , FocusListener , AttributeChangeListener{
    
    
    private StringAttribute _attr;
 
    
    private boolean _isDirty;
    
    private boolean _allowEvents;
    
    /**
     * Class constructor.  INtialize the class with the attribute o edit.
     * @param attr The string attribute to edit.
     */
    public StringAttributeEditor(StringAttribute attr) {
        super();
 
        _isDirty = false;
                
        _allowEvents = true;
        
        _attr = attr;
        
        
        init();
    }
    
    /**
     * Initialize the editor.
     */
    private void init() {
             
        
        this.setText(_attr.getString());        
        
        this.getDocument().addDocumentListener(this);
        this.addActionListener(this);
        this.addFocusListener(this);
        

        _attr.addListener(this);
        
        _isDirty = false;
        
    }

   /**
     * Flag a change when text is inserted.
     * @param e The document event.
     */        
    @Override
    public void insertUpdate(DocumentEvent e) {
        _isDirty = true;
    }

   /**
     * Flag a change when text is deleted.
     * @param e The document event.
     */        
    @Override
    public void removeUpdate(DocumentEvent e) {
        _isDirty = true;
    }

   /**
     * Flag a change when text is changed.
     * @param e The document event.
     */        
    @Override
    public void changedUpdate(DocumentEvent e) {
        _isDirty = true;
    }

    /**
     * Action handler for the text field.  This handles the enter action.
     * @param e The action event.
     */     
    @Override
    public void actionPerformed(ActionEvent e) {

        applyAttributeChange();
    }
    
    /**
     * Focus gained event - not used.
     * @param e The focus event.
     */       
   @Override
    public void focusGained(FocusEvent e) {  }

    
    /**
     * Update the  attribute from the text field contents when the mouse moves away.
     * @param e The focus event.
     */    
    @Override
    public void focusLost(FocusEvent e) {
 
        applyAttributeChange();
    }    
    
    
    /**
     * Apply any changes to the Attribute.
     */
    private void applyAttributeChange() {
        
        //suppress events
        if(!_allowEvents) return;
        //changes made?
        if(_isDirty) {
            
            String text = this.getText();
            if(text.isEmpty() ) {
               //restore string
               this.setText(_attr.getString());

            } else {
               _attr.setString(text); 
            }            
            
            _isDirty = false;
            
        }
        
        
    }
    
    


    @Override
    public void attributeChanged( AttributeChangeEvent attEvent) {
        
         _allowEvents = false;
        this.setText(_attr.getString());
        //make sure this is flagged as clean as focus events
        //will fire and do a double update...
        _isDirty = false;
         _allowEvents = true;
    }
    
    
}
