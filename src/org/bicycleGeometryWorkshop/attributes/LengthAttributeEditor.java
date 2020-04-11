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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bicycleGeometryWorkshop.app.UnitsDisplay;
import org.bicycleGeometryWorkshop.app.UnitsListener;

/**
 * This is the editor class for the length attribute.  
 * This is a text field that displays the suffix and also responds to changes in the UnitsDisplay Class.
 * @author Tom
 */
public class LengthAttributeEditor extends JTextField implements DocumentListener, ActionListener, FocusListener, AttributeChangeListener, UnitsListener {

    LengthAttribute _attr;

    private boolean _isDirty;


    private boolean _allowEvents;

    /**
     * Class Constructor.  Initialize the editor with the attribute.
     * @param attr The attribute to edit.
     */
    public LengthAttributeEditor(LengthAttribute attr) {
        super();

        _attr = attr;

        _isDirty = false;

        _allowEvents = true;

        
       
        init();
        
    }

    /**
     * Initialize the editor.
     */
    private void init() {

        this.getDocument().addDocumentListener(this);
        this.addActionListener(this);
        this.addFocusListener(this);

        this.setMinimumSize(new Dimension(24, 24));
        this.setPreferredSize(new Dimension(60, 24));

        double dval = _attr.getDoubleValue();

        this.setText(Double.toString(dval));

        _attr.addListener(this);
        
        
        
       

        _isDirty = false;

    }

    /**
     * Paint the component and the suffix.  This uses the super.paintCOmponent and 
     * then paints the suffix on top of that.
     * @param g The Graphics object to paint to.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        String suffix = UnitsDisplay.getUnitsSuffix();

        Rectangle rect = this.getBounds();

        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint(Color.GRAY);

        //String suffix = "mm";
        FontMetrics fm = g2.getFontMetrics(this.getFont());
        Rectangle2D sb = fm.getStringBounds(suffix, g);

        float sw = (float) sb.getWidth();
        float sh = (float) sb.getHeight();
        float pad = 4;

        g2.drawString(suffix, rect.width - sw - pad, sh + pad);

    }


    /**
     * Flag a change in the editor when text is inserted.
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
        validateEntry();
    }

   /**
     * Set the attribute from the text field.
     */    
    private void validateEntry() {

        if (!_allowEvents) {
            return;
        }

        if (_isDirty) {

            String text = this.getText();
            
            try {
                
                double value = Double.parseDouble(text);
                double conv = UnitsDisplay.getNaturalLength(value);
                _attr.setDouble(conv);
                      
                
            } catch (NumberFormatException e) {
                //revert
                double natural = _attr.getDoubleValue();
                double display = UnitsDisplay.getDisplayLength(natural);
                
                text = Double.toString(display);
                this.setText(text);                
                
            }

            //reset flag
            _isDirty = false;
        }

    }

    
    /**
     * Focus gained event - not used.
     * @param e The focus event.
     */    
    @Override
    public void focusGained(FocusEvent e) { }

    /**
     * Update the  attribute from the text field contents when the mouse moves away.
     * @param e The focus event.
     */    
    @Override
    public void focusLost(FocusEvent e) {
        validateEntry();
    }

    
    /**
     * Update the displayed value from the attribute change event.
     * @param attEvent The attribute event.
     */    
    @Override
    public void attributeChanged(AttributeChangeEvent attEvent) {

            updateDisplay();

    }

    /**
     * Update the display text from the attribute value.
     */
    private void updateDisplay() {
        
        _allowEvents = false;

        double natural = _attr.getDoubleValue();
        //get the display value
        double display = UnitsDisplay.getDisplayLength(natural);

        
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(display);        

        this.setText(formatted);

        //make sure to set this as the change flags it as dirty
        //and then when focus changes the attribute gets set...
        _isDirty = false;

        _allowEvents = true;        
         
        
    }
    
    /**
     * UnitsDisplay Listener method.  This is called when the display units are changed.
     */
    @Override
    public void unitsDisplayChanged() {
        
        updateDisplay();
       
    }
    
    
    /**
     * This is called when the parent panel is removed from the UI.  
     * This needs to detach the UnitsDisplay listener.
     */
    public void parentRemoved() {
        
        UnitsDisplay.removeListener(this);
    }
    
    /**
     * This is called when the parent panel is added to the UI.  
     * This attaches the UnitsDispaly listener and updates from those settings.
     */
    public void parentDisplayed() {

        //add the listener
        UnitsDisplay.addListener(this);
        //call listener method to initiate
        unitsDisplayChanged();
        
    }
    


  

}
