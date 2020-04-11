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
package org.bicycleGeometryWorkshop.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * About Dialog box for general project information.
 * @author Tom
 */
public class AboutDialog extends JDialog implements ActionListener{
    
    private JButton _okButton;
    
    private JTextArea _textArea;
    
    private JFrame _owner;
    
    /**
     * Initialize the dialog box.
     * 
     * @param frame The parent frame for modal display.
     */
    public AboutDialog(JFrame frame) {
        super(frame, true);
        
        _owner = frame;
        
        init();
        
    }
    
    /**
     * Initialize the controls.
     */
    private void init() {
        
        this.setTitle("About");
        
        JPanel panel = new JPanel();
        BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(bl);        
        
        Dimension minD = new Dimension(60,60);
        Dimension prfD = new Dimension(200,200);
        Dimension maxD = new Dimension(200,200);        
        
        _textArea = new JTextArea(getAboutText());
        
        _textArea.setLineWrap(true);
        _textArea.setWrapStyleWord(true);        
        _textArea.setEditable(false);

        _textArea.setMinimumSize(minD);
        _textArea.setPreferredSize(prfD);
         _textArea.setMaximumSize(maxD);
        
        _okButton = new JButton("Ok");
        _okButton.setMinimumSize(new Dimension(60,60));
        _okButton.setPreferredSize(new Dimension(200,24));
        _okButton.setMaximumSize(new Dimension(200,24));
        
        _okButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        _okButton.setAlignmentY(Component.TOP_ALIGNMENT);          
        
        _okButton.addActionListener(this);
        
        
        JScrollPane textPane = new JScrollPane(_textArea);
        textPane.setPreferredSize(prfD);
        
        panel.add(textPane);
        panel.add(_okButton);
        
        this.add(panel);
        this.pack();
        
        this.setResizable(false);
        
        
    }
    
    /**
     * Show the dialog.
     * @return True (always)...
     */
    public boolean showDialog() {
        
        resolvePosition();
        
        this.setVisible(true);
        
        return true;
        
    }
    
    /**
     * Resolve the dialog position on screen.
     */
    private void resolvePosition() {
        
        
        //set elative location if not null
        if(_owner != null) {
            
            Rectangle fb = _owner.getBounds();
            Rectangle cb = this.getBounds();
            
            double fcx  = fb.getCenterX();
            double fcy  = fb.getCenterY();
 
            double cw = cb.getWidth() / 2;
            double ch = cb.getHeight() / 2;
            
            int x = (int)Math.round(fcx - cw );
            int y = (int)Math.round(fcy - ch);            
            
            this.setLocation(x,y);
           
        }       
        
        
        
    }    

    /**
     * Action handler for close.
     * @param e The action event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        this.setVisible(false);
        
    }
    
    /**
     * Get the text to display.
     * @return The text to display.
     */
    private String getAboutText() {
        String NL = System.lineSeparator();
        String about = BicycleGeometryWorkshopUI.projectName() + NL;
//        about += "Bicycle Geometry Workshop Version 0.7." + NL;
        about += "2020 by Tom C., released under the MIT License" +NL + NL;
        
        
        about += "Geometry function contributions from examples on: http://paulbourke.net/" + NL;
        
        about += "-Line Line Interesection" + NL + "  'C' Example by Paul Bourke" + NL;
        about += "-Line Circle Intersection" + NL + "  'C' Example by Iebele Abel" + NL;
        about += "-Circle Circle Intersection" + NL + "  'C++' Example by Jonathan Greig" + NL;
        about += "(See source for full credits and links)";
        
        return about;
    }
    
}
