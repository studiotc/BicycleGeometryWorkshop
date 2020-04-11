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
package org.bicycleGeometryWorkshop.ui.tree;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import org.bicycleGeometryWorkshop.components.BaseComponent;

/**
 * Custom tree to node to hold a BaseComponent, icon, and menu reference for right-click menu.
 * The tree node is created when a bicycle is added to the navigator and this node is packed from a bicycle component.
 * 
 * @author Tom
 */
public class ComponentTreeNode extends DefaultMutableTreeNode {
    
    private JPopupMenu  _menu;
    
    private ImageIcon _icon;
    
    private BaseComponent _component;
    
    /**
     * Class constructor.  The class is constructed with a reference to the BaseComponent.
     * @param userObject The BaseComponent for the tree node.
     */
    public ComponentTreeNode(Object userObject) {
        super(userObject);
        
 

        _component = null;
        
        _icon = null;
        
        _menu = null;
        
    }
    
    
    /**
     * Set the user Object of the tree node - usually an AttributeSetPanel or String.
     * This will also update the display name.
     * @param userObject The user object to be stored in the node.
     */
    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        
        //updateName();
        
    }
    
    /**
     * Set the component of this tree node.
     * @param component The tree nods component.
     */
    public final void  setComponent(BaseComponent component) {
        _component = component;
        
        
    }
    
    public BaseComponent getComponent() {
        return _component;
    }
    
    /**
     * Set the Icon for this tree node.
     * The icon is displayed in the tree.
     * @param icon The icon for the tree node.
     */
    public void setIcon(ImageIcon icon) {
        _icon = icon;
    }
    
    /**
     * Get the Icon for the tree node.
     * @return The icon associated with the node.
     */
    public ImageIcon getIcon() {
        return _icon;
    }
    
    /**
     * Check to see if menu is set
     * @return True if the tree node has a menu, false if there is no menu.
     */
    public boolean hasMenu() {
        return _menu != null;
    }
    
    /**
     * Get the menu associated with the tree node.
     * @return JMenu associated with this tree node.
     */
    public JPopupMenu  getMenu() {
        return _menu;
    }
    
    /**
     * Set the Menu associated with this tree node.
     * @param menu The menu to associate with the tree node.
     */
    public void setMenu(JPopupMenu  menu) {
        _menu = menu;
    }
    
    /**
     * The string form uses the underlying components name if it is not null.
     * @return The name of the component or '[NULL]' text string (not a null object) if the component is null.
     */
    @Override
    public String toString() {
        
        if(_component != null) {  
            return _component.getName();
        }   else {
            return "[NULL]";
        }    
        
        
        
    }
    
    
}
