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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * JPanel class for displaying Attribute Editors.  This panel takes an attribute set 
 * and lays out all the individual attribute editors.  This panel is shown when the 
 * owner component is selected in the Navigator Tree.
 * @author Tom
 */
public class AttributeSetPanel extends JPanel  {

    private static final int ROW_HEIGHT = 24;
    private static final int ROW_WIDTH = 220;
    private static final int PAD = 2;

    private AttributeSet _attributes;
    
    private JLabel _headingLabel;
    
    private ArrayList<AttributeEditor> _editors;

    /**
     * Class constructor.  Load the attribute set and pack the individual editors.
     * @param attributes The attribute set to load.
     */
    public AttributeSetPanel(AttributeSet attributes) {

        _attributes = attributes;

        _headingLabel = new JLabel();


        init();
        

        _editors = new ArrayList();
        
        initEditors();
        
        pack();

    }
    
    /**
     * Initialize the panel.
     */
    private void init() {
        

        _headingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        _headingLabel.setAlignmentY(Component.TOP_ALIGNMENT);    
        
        
        Dimension minD = new Dimension(60,ROW_HEIGHT);
        Dimension prfD = new Dimension(120,ROW_HEIGHT);
        Dimension maxD = new Dimension(800,ROW_HEIGHT);
        
        _headingLabel.setMinimumSize(minD);
        _headingLabel.setPreferredSize(prfD);
        _headingLabel.setMaximumSize(maxD);  
        
         Border border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.darkGray);
        _headingLabel.setBorder(border);
        

        
    }
    
    
    /**
     * Create list of editors internally.  Editors are added
     * to the panel based on their display setting.
     */
    private void initEditors() {
        
        _editors.clear();
        
        ArrayList<BaseAttribute> attList = _attributes.getAttributes();

        for (BaseAttribute ba : attList) {
            AttributeEditor attEditor = new AttributeEditor(ba);
            _editors.add(attEditor);

        }//end for         
        
    
        
        
    }
    
    /**
     * Refresh the components.  This re-gathers the editors from the 
     * AttributeSet and re-packs them in the panel.
     */
    public void attributesUpdated() {
        

        initEditors();
        
        pack();
        
        
    }
    
    
    /**
     * Pack the components into the panel.
     */
    public final void pack() {

       //remove all components
        this.removeAll();        
        
        BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(bl);

        _headingLabel.setText(_attributes.getName());

        this.add(_headingLabel);

        this.add(Box.createRigidArea(new Dimension(0,4)));

        for (AttributeEditor editor : _editors) {
            if(editor.isDisplayed()) {
                this.add(editor);

            }
        }//end for    
   

    }
    
    /**
     * Add some padding to the controls.
     * @return The insets for the panel.
     */
    @Override
    public Insets getInsets() {
        return new Insets(2,4,2,2);
    }

    /**
     * The string form is the name of the attribute set.
     * @return The name of the attribute set.
     */
    @Override
    public String toString() {
        return _attributes.getName();
    }
    
    
    /**
     * The attribute that holds that name of the component.
     * Every attribute set has a default name attribute.
     * @return The name attribute string value.
     */
    public String getAttributeSetNameAtt() {
        
        String nameAtt = _attributes.getNameAttribute().getString();
        
        return nameAtt;
    }
    

    /**
     * Called when the parent scroll  pane is removed from the UI. 
     * Notify all the editors that they are no longer displayed and should detach from the units system listeners.
     */
    public void parentRemoved() {
        
         for(AttributeEditor editor : _editors) {
            editor.parentRemoved();
        } 
    
        
    }

    /**
     * Called when the parent scroll pane is removed from the UI.
     * Notify all the editors that they should update and listen to the units system.
     */
    public void parentDisplayed() {
        
        for(AttributeEditor editor : _editors) {
            editor.parentDisplayed();
        }        
        
        
    }


}
