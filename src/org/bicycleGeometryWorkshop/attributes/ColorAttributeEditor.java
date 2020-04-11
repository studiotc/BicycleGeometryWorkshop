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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import radialcolordialog.RadialColorDialog;

/**
 * This is the editor class for a color attribute.  
 * This is a button that launches the RadialColorDialog for color selection.
 * @author Tom
 */
public class ColorAttributeEditor extends JButton implements ActionListener, AttributeChangeListener {

    private ColorAttribute _attr;
 

    /**
     * Class constructor.  Initialize the editor with the attribute.
     * @param attr The color attribute to edit.
     */
    public ColorAttributeEditor(ColorAttribute attr) {
        super();

        _attr = attr;

        init();
    }

    /**
     * Initialize the editor.
     */
    private void init() {

        
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        
        this.addActionListener(this);

  
        this.setText("");

        _attr.addListener(this);
        
        //setButtonText();
        makeImage();


    }


    /**
     * Make the Color Image for the button.
     */
    private void makeImage() {

        Color bg = _attr.getColor();

        int w = 48; 
        int h = 16; 
        
        BufferedImage bicb = radialcolordialog.CheckerBoard.makeImage(w, h, 8);

        //BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bicb.getGraphics();
        g.setColor(bg);
        g.fillRect(0, 0, w, h);


        //black outline
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, w -1, h -1);
        
        //dispose of graphics
        g.dispose();
        
        Icon icon = new ImageIcon(bicb);
        
        this.setIcon(icon);

    }


    /**
     * Action handler for the button.  This launches the color dialog.
     * @param e The action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        showGetColorDialog();
    }

    /**
     * Show the color selection dialog and update color from the dialog.
     */
    private void showGetColorDialog() {

        Color curColor = _attr.getColor();

        JFrame frame = (JFrame) SwingUtilities.getRoot(this);
        
        RadialColorDialog rcd = new RadialColorDialog(frame);
        
        String title = "Select Color for: " + _attr.getName();
        boolean ok = rcd.showDialog(curColor, title);
        

        if (ok) {
            Color newColor = rcd.getColor();
            _attr.setColor(newColor);

            //update the button image
            makeImage();
        }

        rcd.dispose();

    }

 
    
    /**
     * This is called when the color attribute changes.  
     * Regenerate the background image used for the button when the attribute changes.
     * @param attEvent The attribute event.
     */
    @Override
    public void attributeChanged( AttributeChangeEvent attEvent) {
        
        makeImage();
       // this.validate();
        
    }

}
