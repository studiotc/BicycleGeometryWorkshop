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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *  This is the editor class for a double attribute.  
 *  A JTextField is used as the editing component.
 * @author Tom
 */
public class DoubleAttributeEditor extends JTextField implements DocumentListener, ActionListener, FocusListener, AttributeChangeListener {

    DoubleAttribute _attr;

    private boolean _isDirty;

    private final String _suffix;

    private boolean _allowEvents;

    /**
     * Class Constructor.  Initialize the editor with the attribute.
     * @param attr The double attribute to edit.
     */
    public DoubleAttributeEditor(DoubleAttribute attr) {
        super();

        _attr = attr;

        _isDirty = false;

        _allowEvents = true;

        _suffix = attr.getSuffix();
       
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
     * Paint the standard UI Component, and add an overlay of the units suffix.
     * @param g The graphics object to paint to.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!_suffix.isEmpty()) {

            Rectangle rect = this.getBounds();

            Graphics2D g2 = (Graphics2D) g;

            g2.setPaint(Color.GRAY);

            //String suffix = "mm";
            FontMetrics fm = g2.getFontMetrics(this.getFont());
            Rectangle2D sb = fm.getStringBounds(_suffix, g);

            float sw = (float) sb.getWidth();
            float sh = (float) sb.getHeight();
            float pad = 4;

            g2.drawString(_suffix, rect.width - sw - pad, sh + pad);

        }

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

        updateAttribute();
    }

    /**
     * Set the attribute from the text field.
     */
    private void updateAttribute() {

        if (!_allowEvents) {
            return;
        }

        if (_isDirty) {

            String text = this.getText();

            //set from string or reset display on rejection
            if (!_attr.setFromString(text)) {
                double cVAl = _attr.getDoubleValue();
                text = Double.toString(cVAl);
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
        updateAttribute();
    }

    /**
     * Update the displayed value from the attribute change event.
     * @param attEvent The attribute event.
     */
    @Override
    public void attributeChanged(AttributeChangeEvent attEvent) {

        _allowEvents = false;

        double dval = _attr.getDoubleValue();

        this.setText(Double.toString(dval));

        //make sure to set this as the change flags it as dirty
        //and then when focus changes the attribute gets set...
        _isDirty = false;

        _allowEvents = true;

    }

}
