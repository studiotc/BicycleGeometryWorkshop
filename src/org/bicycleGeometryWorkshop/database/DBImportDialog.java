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
package org.bicycleGeometryWorkshop.database;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

/**
 * Data Base import dialog.  This dialog is used for importing components from the library.  
 * It also provides minimal management functions for deleting records.
 * @author Tom
 */
public class DBImportDialog extends JDialog {
    
    private boolean _okSelected;
    
    private boolean _closeAndRemove;
    
    private JList _compList;
    
    private DefaultListModel _listModel;
    
    private JLabel _label;
    
    private JPopupMenu _menu;
    

    private JButton _removeButton;
    
    private JButton _okButton;
    
    private ArrayList<ComponentRecord> _deleteList;
    private ComponentRecord _importComponent;
    
    /**
     * Class constructor.  Construct the dialog with a reference to the owner frame.
     * @param owner The owner frame.
     */
    public  DBImportDialog(JFrame owner) {
        super(owner, true);
        
        
        _deleteList = new ArrayList();
        
        _listModel = new DefaultListModel();
    
        _compList = new JList(_listModel);
    
        _compList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        _okSelected = false;
        _importComponent = null;
        
        _closeAndRemove  = false;
        
        _label = new JLabel("Import Component");
        
        _menu = new JPopupMenu("RemoveMenu");
        //add menu items
        createMenu();
        
        init();
    
    }
    

   
    /**
     * Initialize the components and do the layout.
     */    
    private void init() {
        
        this.setTitle("Import");
        
        //this.setLayout(null);
        this.setLocationRelativeTo(this.getOwner());
        
        FlowLayout flowLayout = new FlowLayout();
         flowLayout.setHgap(0);
         flowLayout.setVgap(0);
         this.setLayout(flowLayout);
         
        _okButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");
        
        _removeButton = new JButton("Remove and No Import");
        _removeButton.setEnabled(false);
        _removeButton.setToolTipText("Exit the dialog and remove library items without importing.");
  
        //remove action
        ActionListener removeAction = (ActionEvent ev) -> {
            closeAndRemove();
        };
        //add the listener
        _removeButton.addActionListener(removeAction);        
        
        
        //ok action
        ActionListener okAction = (ActionEvent ev) -> {
            okSelected();
        };
        //add the listener
        _okButton.addActionListener(okAction);        
          
        //cancel action
        ActionListener cancelAction = (ActionEvent ev) -> {
            cancelSelected();
        };
        //add the listener
        cancelButton.addActionListener(cancelAction);         
        
        //container panel
        JPanel panel = new JPanel();
        panel.setLayout(null);    
        
        //panel.setBackground(Color.red);
        
        int width = 180;
        int listHeight = 240;
        
        int pad = 4;
        
        int buttonWidth = (width / 2) - (pad / 2);
        int buttonHeight = 24;
        
        int x1 = pad;
        int x2 = x1 + buttonWidth + pad; //second button x
        int panelWidth = width + pad + pad;
        
        int y1 = pad;
        int y2 = y1 + listHeight  + pad;
        int y3 = y2 + buttonHeight + pad;
        int panelHeight = y3 + buttonHeight + pad;
        
        
        //_label.setBounds(x1, y1, width, buttonHeight);
        //set list location and size 
        _compList.setBounds(x1,y1,width, listHeight);
        
        _compList.setComponentPopupMenu(_menu);
        
        _removeButton.setBounds(x1, y2, width, buttonHeight);
        //set buttons locations and sizes  
        _okButton.setBounds(x1, y3, buttonWidth, buttonHeight);
        cancelButton.setBounds(x2, y3, buttonWidth, buttonHeight);
        
        Dimension panelDim = new Dimension(panelWidth, panelHeight);
        //set panel size
        //panel.setSize(panelDim);
        panel.setPreferredSize(panelDim);

        //add to panel
        //panel.add(_label);
        panel.add(_compList);
        panel.add(_removeButton);
        panel.add(_okButton);
        panel.add(cancelButton);
        
        
        //Dimension frameDim = new Dimension(x2 + 8 , y3 + 30);        
        
        //add panel to dialog
        this.add(panel);
        
        this.pack();
        //this.invalidate();
        
        //this.setSize(frameDim);
       // this.setPreferredSize(frameDim);
        this.setResizable(false);        
        
    }
       
    

    
    /**
     * Add the menu items to the right-click menu
     */
    private void createMenu() {
        
        
        
        JMenuItem deleteItem = new JMenuItem("Remove from Library");
 
        ActionListener undoAL = (ActionEvent ev) -> {
            menuRemoveItem();
        };
        //add the listener
        deleteItem.addActionListener(undoAL);        
        _menu.add(deleteItem);        
        
        
    }
    
    
    /**
     * Ok Button selected - import component and close
     */
    private void okSelected() {
        
        int index = _compList.getSelectedIndex();
        
        if(index >= 0) {
            
            //_compList.get
            ComponentRecord cr = (ComponentRecord)_compList.getSelectedValue();
            
            //set the component to import
            _importComponent = cr;

            //set flag for remove
            _closeAndRemove = true;
            //set flag for ok
            _okSelected = true;
            this.setVisible(false);            
            
            
        }        
        
        

        
    }
    
    
    /**
     * Close dialog.
     */
    private void cancelSelected() {
        
        //do no removals
        _closeAndRemove = false;
        //no ok
        _okSelected = false;
        this.setVisible(false);
        
    }
    
    /**
     * Close dialog, but flag to remove unwanted items.
     */
    private void closeAndRemove() {
        
        
        _closeAndRemove = true;
        _okSelected = false;
        this.setVisible(false);
        
        
    }
    
    /**
     * Get the OK selected flag.
     * @return True if ok selected, false otherwise.
     */
    public boolean getOkSelected() {
        return _okSelected;
    }
    
    /**
     * Get the flag for removing records on close.
     * @return True if there are elements to delete.
     */
    public boolean getRemoveOnClose() {
        return _closeAndRemove;
    }
    
    /**
     * Get the list of records to delete.
     * @return The list of component records to delete.
     */
    public ArrayList<ComponentRecord> getDeleteList() {
        return _deleteList;
    }
    
    /**
     * Get the component record to import.
     * @return The component record to import.
     */
    public ComponentRecord getImportComponent() {
        return _importComponent;
    }
    
    /**
     * Remove a component from the library - called from the right-click menu.
     */
    private void menuRemoveItem() {
        
        int index = _compList.getSelectedIndex();
        
        if(index >= 0) {
            
            //_compList.get
            ComponentRecord cr = (ComponentRecord)_compList.getSelectedValue();
//            System.out.println("remove record: " + cr.getPath());
            
            
            //add to delete list
            _deleteList.add(cr);
            
            //remove item from list
            _listModel.removeElement(cr);
            
            //enable button
            _removeButton.setEnabled(true);
            
            
        }
        
        
        
    }
    
    
    /**
     * Load the list of component records.
     * @param comps List of Component Records to display.
     */
    public void loadList(ArrayList<ComponentRecord> comps) {
        
        //this hsould be clear anywways...
        _listModel.clear();
        
        if(comps.isEmpty()) {
            
            String noComps = "<No Components>";
            _listModel.addElement(noComps);
            _okButton.setEnabled(false);
            _compList.setEnabled(false);
            
        } else {
            for(ComponentRecord cr : comps) {
                _listModel.addElement(cr);
            }            
            
        }
        

        
        
    }
    
    /**
     * Show the dialog.
     * @param compName Name of the Component.
     * @return True if dialog selected and item, false if it was canceled.
     */
    public boolean showDialog(String compName) {
        
        String importMssg = "Import: " + compName;
        this.setTitle(importMssg);
        

        _deleteList.clear();
        _okSelected = false;
        
        
        //show the dialog
        this.setVisible(true);
        
        
        return _okSelected;
        
    }
    
    
    
}
