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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class serves as the  container for the an individual Attribute Editor.  
 * This panel holds a label for the attribute and the attribute provided editing component.  
 * The editor also receives events when the host AttributePanel is added and removed from the UI.  This is so the 
 * Length attributes can be updated from changes in the units display system (mm or inches).
 * @author Tom
 */
public class AttributeEditor extends JPanel  {
    
    private BaseAttribute _attr;
    
    private JLabel _attrLabel;
    private JComponent _editor;
    

    /**
     * Class constructor.  
     * @param attr The attribute to edit in the editor.
     */
    public AttributeEditor(BaseAttribute attr) {
        super();
        
        _attr = attr;
        _attrLabel = new JLabel();
        _editor = null;
        
        
        init();
        
    }
    
    /**
     * Initialize the editor panel.  Layout the label and editor.
     * Note: BoxLayout likes all the sizes and alignments set to work properly.
     */
    private void init() {
        
 
        BoxLayout bl = new BoxLayout(this, BoxLayout.X_AXIS);
        this.setLayout(bl);
        
        int rowHeight = 24;
        Dimension eminD = new Dimension(60,rowHeight);
        Dimension eprfD = new Dimension(240,rowHeight);
        Dimension emaxD = new Dimension(800,rowHeight);
        
        
        
        this.setMinimumSize(eminD);
        this.setPreferredSize(eprfD);
        this.setMaximumSize(emaxD);

        
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setAlignmentY(Component.TOP_ALIGNMENT); 
        
        
        /*** set dimensions for sub components ***/
        Dimension minD = new Dimension(30,rowHeight);
        Dimension prfD = new Dimension(800,rowHeight);
        Dimension maxD = new Dimension(800,rowHeight);        
        
        _attrLabel.setHorizontalAlignment(JLabel.LEFT);
        
        
        _attrLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        _attrLabel.setAlignmentY(Component.TOP_ALIGNMENT);  
        
        _attrLabel.setMinimumSize(minD);
        _attrLabel.setPreferredSize(prfD);
        _attrLabel.setMaximumSize(new Dimension(200,rowHeight));

        _attrLabel.setLocation(0,0);
        _attrLabel.setText(_attr.getName());
        

        String desc = _attr.getDescription();
        if(!desc.isEmpty()) {
            _attrLabel.setToolTipText( desc );
        }

        
        this.add(_attrLabel);
//        this.add(Box.createHorizontalStrut(20));
        this.add(Box.createHorizontalGlue());
        
        _editor = _attr.getEditor();
        
        if(_editor != null) {
            _editor.setMinimumSize(minD);
            _editor.setPreferredSize(prfD); 
            _editor.setMaximumSize(maxD);
            _editor.setAlignmentX(Component.RIGHT_ALIGNMENT);
            _editor.setAlignmentY(Component.TOP_ALIGNMENT);             

            this.add(_editor);
        } 
        
        

        
    }
    
    
  /**
   * Get the display flag for this Attribute.
   * @return True if the editor should be displayed, false otherwise.
   */
    public boolean isDisplayed() {
        return _attr.getDisplay();
    }


    /**
     * Called when the parent scroll  pane is removed from the UI. 
     * Notify listeners to detach from units system.
     */
    public void parentRemoved() {
        
       if(_editor != null) {
           if(_editor instanceof LengthAttributeEditor) {
               LengthAttributeEditor lae = (LengthAttributeEditor) _editor;
               lae.parentRemoved();
           }
       }
        
    }

    /**
     * Called when the parent scroll pane is displayed in the UI. 
     * Notify the listeners to update and listen to the units system.
     */
    public void parentDisplayed() {
        
       if(_editor != null) {
           if(_editor instanceof LengthAttributeEditor) {
               LengthAttributeEditor lae = (LengthAttributeEditor) _editor;
               lae.parentDisplayed();
           }
       }        
        
    }  
    
}
