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
package org.bicycleGeometryWorkshop.ui.tree;

import org.bicycleGeometryWorkshop.attributes.AttributeSetPanel;
import org.bicycleGeometryWorkshop.components.BaseComponent;
import org.bicycleGeometryWorkshop.components.Bicycle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.bicycleGeometryWorkshop.app.VisualPreferences;
import org.bicycleGeometryWorkshop.components.BicycleDisplay;
import org.bicycleGeometryWorkshop.components.RiderMeasurements;
import org.bicycleGeometryWorkshop.components.RiderPose;
import org.bicycleGeometryWorkshop.ui.BicycleGeometryWorkshopUI;

/**
 *  The NavigatorTree is a JTree that holds the nodes for selecting and editing components.  
 *  The main nodes on the tree are the Rider Measurements, The Rider Pose, and the Bicycle Group Node.  
 * The bicycle group node holds all the bicycle nodes which in turn hold all the component level nodes.  
 * Selecting any node will bring up it's AttributeSetPanel into the UI so the component may be edited.  
 * The tree nodes also have right-click menus for additional functions (re-ordering the bicycle list, adding and deleting bicycles, etc.).
 * @author Tom
 */
public class NavigatorTree extends JTree implements TreeSelectionListener, MouseListener {
    
    private DefaultMutableTreeNode _rootNode;
    private ComponentTreeNode _riderNode;
    private ComponentTreeNode _poseNode;
    
    private ComponentTreeNode _bicycleGroupNode;
    
    
    private NavigatorListener _listener;
    
    
    /**
     * Class constructor.
     * @param listener The navigator listener used to respond to events.
     */
    public NavigatorTree( NavigatorListener listener) {
        super(new DefaultMutableTreeNode("root"));
        

        _listener = listener;
        
 
        TreeModel tm = this.getModel();
        
        _rootNode = (DefaultMutableTreeNode) tm.getRoot();
        _riderNode = new ComponentTreeNode("Rider");
        _poseNode = new ComponentTreeNode("Pose");
        
      
        
         _bicycleGroupNode = new BicycleGroupTreeNode("Bicycles");
        
        init();
        
    }
    
    private void init() {
        

       this.setCellRenderer(new NavigatorTreeCellRenderer());
        
        _rootNode.add(_riderNode);
        _rootNode.add(_poseNode);
        _rootNode.add(_bicycleGroupNode);
        
        _bicycleGroupNode.setMenu(getBicycleMainMenu());

        //expand root path
        this.expandPath(new TreePath(_rootNode.getPath()));
        this.setRootVisible(false);
        
        this.expandPath(new TreePath(_bicycleGroupNode.getPath()));

        //register listener
        this.addTreeSelectionListener(this);
        
        this.addMouseListener(this);
        
        _riderNode.setMenu(getComponentMenu("Rider"));
        _poseNode.setMenu(getComponentMenu("Pose"));
        
            

        
    }

    /**
     * Clear the current Tree.
     */
    public void clearNavigator() {
        
        _riderNode.setUserObject("null");
        _poseNode.setUserObject("null");
        
        _bicycleGroupNode.removeAllChildren();
        
    }

    /**
     * Set the Rider editor in the Tree Node.
     *
     * 
     * @param riderSize The rider size object.
     */
 public void setRiderMeasurements(RiderMeasurements riderSize) {
        
     _riderNode.setIcon(riderSize.getIcon());
     _riderNode.setUserObject(riderSize.getAttributeSet().getEditor());
     _riderNode.setComponent(riderSize);
        
    }    
    

    /**
     * Set the Pose editor in the Tree Node.
     *
     * 
     * @param riderPose  The rider pose object.
     */
    public void setRiderPose(RiderPose riderPose) {
        
        _poseNode.setIcon(riderPose.getIcon());
        _poseNode.setUserObject(riderPose.getAttributeSet().getEditor());
        _poseNode.setComponent(riderPose);
    }
    
    /**
     * Set the visual preferences.
     * This gets placed in the tree at the Bicycle GroupNode
     * @param vPrefs The visual preferences object.
     */
    public void setVisualPreferences(VisualPreferences vPrefs) {
        
        _bicycleGroupNode.setIcon(vPrefs.getIcon());
        _bicycleGroupNode.setUserObject(vPrefs.getAttributeSet().getEditor());
        _bicycleGroupNode.setComponent(vPrefs);         
        
    }

    
    public void addBicycle(Bicycle bicycle) {
        addBicycle(bicycle, -1);
    }
    /**
     * Add a Bicycle to the Navigator. This creates a node for the Bicycle under
     * the bicycles sections and creates all the sub-nodes for the components.
     *
     * @param bicycle Bicycle to add to the navigator tree.
     * @param index Index of bicycle in tree.  A value of -1 will insert to end.
     */
    public void addBicycle(Bicycle bicycle, int index) {
        
        AttributeSetPanel bicycleEditor = bicycle.getAttributeSet().getEditor(); //getBicycleEditor();
        BicycleTreeNode bicycleNode = new BicycleTreeNode(bicycle, bicycleEditor);
        bicycleNode.setMenu(getBicycleMenu());
        bicycleNode.setIcon(bicycle.getIcon());
        
        ArrayList<BaseComponent> _compList = bicycle.getComponentList();
        
        for (BaseComponent comp : _compList) {
            
            ComponentTreeNode compNode = makeComponentNode(bicycleNode, comp);
            
            if(comp.hasSubComponents()) {
                
               ArrayList<BaseComponent> subComps = comp.getSubComponents();
               
               for(BaseComponent subComp : subComps) {
                   
                   makeComponentNode(compNode, subComp);
                   
               }
                
                
            }
            
            
        }//end for first level componenets

        if(index == -1) {
            //attach main node to tree
            _bicycleGroupNode.add(bicycleNode);            
        } else {
            _bicycleGroupNode.insert(bicycleNode, index);
        }

        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.reload();

        //amke sure bicycles is expanded
        this.expandPath(new TreePath(_bicycleGroupNode.getPath()));
        // model.getRoot().

    }
    
    /**
     * Create a component tree node to display the specified BaseComponent.
     * @param parent  The parent node of this node.
     * @param component The BaseComponent to associate with the tree  node.
     * @return The tree node with the reference to the component.
     */
    private ComponentTreeNode makeComponentNode(ComponentTreeNode parent, BaseComponent component) {
        
            AttributeSetPanel editor = component.getAttributeSet().getEditor();
            String ename = component.getName();
            ComponentTreeNode compNode = new ComponentTreeNode(editor);
            compNode.setMenu(getComponentMenu(ename));
            compNode.setIcon(component.getIcon());
            compNode.setComponent(component);
            //add component node to bicycle node
            parent.add(compNode);        
        
            return compNode;
        
    }

    /**
     * Set the default editor in panel by triggering a selection. Use the Rider
     * as it is the first node.
     */
    public void setDefaultEditorSelected() {

        //_riderNode.
        this.setSelectionPath(new TreePath(_riderNode.getPath()));
        //this.setSelectionRow(0);
        
    }

    /**
     * Updates the Bicycle Node names.
     * This is called to update the bicycle node name.
     */
    public void refreshBicycleNames() {
        
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();

        //go thorught the bicycles and force update.
        Enumeration e = _bicycleGroupNode.children();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            model.nodeChanged(node);
            
        }//end while
        

        
    }

    /**
     * Load the editor when a node is selected.
     *
     * @param e The tree selection event.
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        
        JTree tree = (JTree) e.getSource();
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
        if(selectedNode instanceof ComponentTreeNode) {
            
            ComponentTreeNode ctn = (ComponentTreeNode)selectedNode;
            BaseComponent bc = ctn.getComponent();
            AttributeSetPanel asp = bc.getAttributeSet().getEditor();

            
            _listener.editorSelected(asp);
            
        }
        

        
    }
    
    
    /**
     * Mouse Clicked Event - not used.
     * @param e The mouse event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {  }
    
    /**
     * Mouse Pressed Event - not used.
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) { }
    
    /**
     * Mouse Released Event.  This triggers the tree node menu if this was a right-click.
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            
            int cx = e.getX();
            int cy = e.getY();
            int row = this.getClosestRowForLocation(e.getX(), e.getY());
            this.setSelectionRow(row);

            //get the selected node
            ComponentTreeNode node = (ComponentTreeNode) this.getLastSelectedPathComponent();

            //just in case...
            if (node == null) {
                return;
            }
            
            if (node.hasMenu()) {
                
                JPopupMenu menu = node.getMenu();
                menu.show(e.getComponent(), cx, cy);
                
            }
            
        }//end if right click        

    }
    
    /**
     * Mouse Entered Event - not used.
     * @param e The mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {  }
    
    /**
     * Mouse Exited Event - not used.
     * @param e The mouse event.
     */
    @Override
    public void mouseExited(MouseEvent e) { }

    /**
     * Build the Menu for Bicycle Nodes.
     *
     * @return The JPopup menu for the Bicycle Node.
     */
    private JPopupMenu getBicycleMenu() {
        
        JPopupMenu bMenu = new JPopupMenu("Bicycle");

        /**
         * Move Up Menu Item
         */
        JMenuItem moveUpItem = new JMenuItem("Move Up");
        ActionListener moveUpAction = (ActionEvent ev) -> {
            menuMoveBicycleUp();
        };
        moveUpItem.addActionListener(moveUpAction);

        /**
         * Move Down Menu Item
         */
        JMenuItem moveDownItem = new JMenuItem("Move Down");
        ActionListener moveDownAction = (ActionEvent ev) -> {
            menuMoveBicycleDown();
        };
        moveDownItem.addActionListener(moveDownAction);

        /**
         * Isolate Bicycle Menu Item
         */        
        JMenuItem isolateItem = new JMenuItem("Isolate Visibility");
        ActionListener isolateAction = (ActionEvent ev) -> {
            menuIsolateBicycle();
        };
        isolateItem.addActionListener(isolateAction);        
        
        /**
         * Delete Menu Item
         */
        JMenuItem deleteItem = new JMenuItem("Delete");
        ActionListener delAction = (ActionEvent ev) -> {
            menuDeleteBicycle();
        };
        deleteItem.addActionListener(delAction);
        
        /**
         * Export menu item
         */
        JMenuItem exportItem = new JMenuItem("Export to Library");
        
        ActionListener alExport = (ActionEvent ev) -> {
            menuExportComponent();
        };
        
        exportItem.addActionListener(alExport);
        
 
        /**
         * Import menu item
         */
        JMenuItem importItem = new JMenuItem("Import from Library");
        ActionListener alImport = (ActionEvent ev) -> {
            menuImportComponent();
        };  
        
        importItem.addActionListener(alImport);
        


        /**
         * Pack Menu
         */
        bMenu.add(moveUpItem);
        bMenu.add(moveDownItem);
        bMenu.addSeparator();
        //---------------------
        bMenu.add(isolateItem);
        bMenu.addSeparator();
        //---------------------
        bMenu.add(exportItem);
        bMenu.add(importItem);        
        bMenu.addSeparator();
        //---------------------
        bMenu.add(deleteItem);
        
        return bMenu;
        
    }
    
    /**
     * Get the Bicycle Group node menu.
     * @return The JPopupMenu for the bicycle group menu
     */
    private JPopupMenu getBicycleMainMenu() {
        
        JPopupMenu bMenu = new JPopupMenu("BicycleMain");
        
        JMenuItem addItem = new JMenuItem("Add Bicycle");
        
        ActionListener al = (ActionEvent ev) -> {
            menuAddBicycle();
        };
        
        addItem.addActionListener(al);
        //add the item to the main menu
        bMenu.add(addItem);
        
        bMenu.addSeparator();
        
        JMenu visibleMenu = new JMenu("Visibility");
        
        JMenuItem turnAllOnItem = new JMenuItem("All Bicycles On");
        ActionListener allOnl = (ActionEvent ev) -> {
            menuAllVisibility(true);
        };   
        turnAllOnItem.addActionListener(allOnl);
        visibleMenu.add(turnAllOnItem);
        
        
        JMenuItem turnAllOffItem = new JMenuItem("All Bicycles Off");
        ActionListener allOffl = (ActionEvent ev) -> {
            menuAllVisibility(false);
        };   
        turnAllOffItem.addActionListener(allOffl);
        visibleMenu.add(turnAllOffItem);       
        
        bMenu.add(visibleMenu);
//        bMenu.addSeparator();
        
        /*  display modes */
        JMenu displayMenu = new JMenu("Display");
        
        
        JMenuItem showRBItem = new JMenuItem("All to Bicycle and Rider");
        ActionListener showRBl = (ActionEvent ev) -> {
            menuAllDisplay(BicycleDisplay.BicycleAndRider);
        };   
        showRBItem.addActionListener(showRBl);
        displayMenu.add(showRBItem);        
        
        
        
        JMenuItem showBItem = new JMenuItem("All to Bicycle");
        ActionListener showBl = (ActionEvent ev) -> {
            menuAllDisplay(BicycleDisplay.Bicycle);
        };   
        showBItem.addActionListener(showBl);
        displayMenu.add(showBItem); 
        
        JMenuItem showFItem = new JMenuItem("All to Frame Only");
        ActionListener showFl = (ActionEvent ev) -> {
            menuAllDisplay(BicycleDisplay.FrameOnly);
        };   
        showFItem.addActionListener(showFl);
        displayMenu.add(showFItem);        
        
        bMenu.add(displayMenu);
        
        return bMenu;
        
    }
    
 
    /**
     * Get the popup menu for the component node.
     * @param compName The component name.
     * @return The JPopup menu to associated with the node.
     */
    private JPopupMenu getComponentMenu(String compName) {
        
        JPopupMenu compMenu = new JPopupMenu(compName);
        
        JMenuItem exportItem = new JMenuItem("Export to Library");
        
        ActionListener alExport = (ActionEvent ev) -> {
            menuExportComponent();
        };
        
        exportItem.addActionListener(alExport);
        
 
        JMenuItem importItem = new JMenuItem("Import from Library");
        ActionListener alImport = (ActionEvent ev) -> {
            menuImportComponent();
        };  
        
        importItem.addActionListener(alImport);
        


        //add the item to the main menu
        compMenu.add(importItem);
//        compMenu.addSeparator();
        compMenu.add(exportItem);
        
        
        
        return compMenu;        
        
    }

    /**
     * Call to add a bicycle for the menu item
     */
    private void menuAddBicycle() {
        _listener.addBicycle();
 
    }
    
    /**
     * Call to make all bicycles visible or hidden (not displayed).
     * @param visible True to make all bicycles visible, false to turn off all bicycles.
     */
    private void menuAllVisibility(boolean visible) {
        
        _listener.bicycleVisibility(visible);
        
    }
    
    /**
     * Call to set the display mode of all bicycles.
     * @param display The display mode to apply to all bicycles.
     */
    private void menuAllDisplay(BicycleDisplay display) {
        
        _listener.bicycleDisplay(display);
    }

    /**
     * Call to isolate the visibility of the currently selected bicycle.  
     * This turns all the other bicycles off or invisible.
     */
    private void menuIsolateBicycle() {
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
        if (selectedNode instanceof BicycleTreeNode) {
            
            BicycleTreeNode btn = (BicycleTreeNode) selectedNode;
            Bicycle b = btn.getBicycle();
            
            _listener.bicycleIsolate(b);
  
        }
        
    }
    
    
    
    /**
     * Call to delete a bicycle from the Bicycle menu.
     */
    private void menuDeleteBicycle() {
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
        if (selectedNode instanceof BicycleTreeNode) {
            
            BicycleTreeNode btn = (BicycleTreeNode) selectedNode;
            Bicycle b = btn.getBicycle();
            
            JFrame frame = BicycleGeometryWorkshopUI.getActiveFrame();
            int result = JOptionPane.showConfirmDialog(frame, "Delete Bicycle: " + b.getBicycleName() + "?", "Delete Bicycle?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                //make call to delete
                _listener.deleteBicycle(b);
            }
  
        }
        
    }

    /**
     * Menu call to move a bicycle up
     */
    private void menuMoveBicycleUp() {
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
        if (selectedNode instanceof BicycleTreeNode) {
            
            BicycleTreeNode btn = (BicycleTreeNode) selectedNode;
            Bicycle b = btn.getBicycle();
            //make call to move up
            _listener.moveBicycleUp(b);

        }
        
    }

    /**
     * Menu call to move a bicycle down
     */
    private void menuMoveBicycleDown() {
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
        if (selectedNode instanceof BicycleTreeNode) {
            
            BicycleTreeNode btn = (BicycleTreeNode) selectedNode;
            Bicycle b = btn.getBicycle();
            //make call to move down
            _listener.moveBicycleDown(b);

        }
        
    }
    
    /**
     * Called to import a component from the Library.
     */
   private void menuImportComponent() {
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
            
        if (selectedNode instanceof ComponentTreeNode) {
            
            ComponentTreeNode ctn = (ComponentTreeNode) selectedNode;
            BaseComponent comp = ctn.getComponent();
            
            //make call to move down
            _listener.importComponent(comp);

        }
        
    }     
    
   /**
    *  Called to export the selected component to the Library.
    */
   private void menuExportComponent() {
        
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();

        //this can be null if a branch is collapsed
        if (selectedNode == null) {
            return;
        }
        
            
            if (selectedNode instanceof ComponentTreeNode) {
            
            ComponentTreeNode ctn = (ComponentTreeNode) selectedNode;
            BaseComponent comp = ctn.getComponent();
            
            //make call to move down
            _listener.exportComponent(comp);

        }
        
    }    
    
    
    /**
     * Move a bicycle in the list.  This is the callback from move up or down
     * @param bicycle The bicycle to move in list.
     * @param up True moves the bicycle up, while false moves it down the list.
     */
    public void moveBicycleInList(Bicycle bicycle, boolean up) {
        
        
        BicycleTreeNode btn = getBicycleTreeNode(bicycle);
        
        if(btn != null) {
            
            int count = _bicycleGroupNode.getChildCount();
            int index = _bicycleGroupNode.getIndex(btn);
            
            index += up ? -1 : 1;
            
            index = Math.max(index, 0);
            index = Math.min(index, count - 1);
            
            //move the node - insert does the removal
           _bicycleGroupNode.insert(btn, index );
            
           updateBicycleGroupNode();           
            
        }        
        
        
        
    }
    
    
    
    /**
     * Remove a Bicycle from the Bicycle group node.
     * @param bicycle The bicycle to remove.
     */
    public void removeBicycle(Bicycle bicycle) {
        
        BicycleTreeNode btn = getBicycleTreeNode(bicycle);
        
        if(btn != null) {
            _bicycleGroupNode.remove(btn);
            
            //clear the editor panel by selecting default
            //setDefaultEditorSelected();
            
            //slect the first bicycle - clears the editor panel by loading new one
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) _bicycleGroupNode.getChildAt(0);
            this.setSelectionPath(new TreePath(childNode.getPath()));
            
            updateBicycleGroupNode();
        }
        
 
        
    }

    /**
     * Update the tree model and the bicycle group node.
     * Call after adding, removing, or moving nodes.
     */
    private void updateBicycleGroupNode() {
        
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.reload();

            //expand bicycles
            this.expandPath(new TreePath(_bicycleGroupNode.getPath()));         
        
    }
    
    
    /**
     * Find the index of the bicycle node under the bicycle group node based on
     * the bicycle it contains.
     *
     * @param bicycle Bicycle to search for.
     * @return The index of the node or -1 if it is not found.
     */
    private int getBicycleNodeIndex(Bicycle bicycle) {
        
        int foundIndex = -1;
        int count = _bicycleGroupNode.getChildCount();
        
        for (int i = 0; i < count; i++) {
            
            DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) _bicycleGroupNode.getChildAt(i);
            
            if (cNode instanceof BicycleTreeNode) {
                
                BicycleTreeNode btn = (BicycleTreeNode) cNode;
                Bicycle b = btn.getBicycle();
                
                if (b.equals(bicycle)) {
                    foundIndex = 1;
                    break;
                }
                
            }
            
        }
        
        return foundIndex;
        
    }
    
    
    private BicycleTreeNode getBicycleTreeNode(Bicycle bicycle) {
        
        BicycleTreeNode node = null;
        int count = _bicycleGroupNode.getChildCount();
        for (int i = 0; i < count; i++) {
            
            DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) _bicycleGroupNode.getChildAt(i);
            
            if (cNode instanceof BicycleTreeNode) {
                
                BicycleTreeNode btn = (BicycleTreeNode) cNode;
                Bicycle b = btn.getBicycle();
                
                if (b.equals(bicycle)) {
                    node = btn;
                    break;
                }
                
            }
            
        }
        
        return node;
        
    }    
    
    
    
}//end class
