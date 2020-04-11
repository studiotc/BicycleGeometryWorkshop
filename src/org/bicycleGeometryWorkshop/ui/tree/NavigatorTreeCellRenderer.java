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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import org.bicycleGeometryWorkshop.components.Bicycle;

/**
 * This the TreeCellRenderer implementation for the NavigatorTree 
 * used to paint icons and control the display of tree nodes.
 * @author Tom
 */
public class NavigatorTreeCellRenderer implements  TreeCellRenderer {

    private TreeCellRendererPanel _cellPanel;
    
    /**
     * Class constructor.
     */
    public NavigatorTreeCellRenderer() {
        
        _cellPanel = new TreeCellRendererPanel();
            
        
    }
    
    /**
     * Get the swing component to use for painting the tree cell.
     * @param tree  The current tree.
     * @param value The value or current tree node.
     * @param selected The selected state of the node.
     * @param expanded The expanded state of the node.
     * @param leaf Flag for leaf node.
     * @param row The row the tree node is on.
     * @param hasFocus Flag for has focus.
     * @return The component used for painting.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        
        _cellPanel.setSelected(selected);
  
        if(value instanceof BicycleGroupTreeNode) {
            
            _cellPanel.setBicycleGroup();
            
            
        } else if (value instanceof BicycleTreeNode) {
            
            BicycleTreeNode btn = (BicycleTreeNode)value;
            String name = btn.toString();//gets teh name
            ImageIcon icon = btn.getIcon();
            
            Bicycle b = btn.getBicycle();
            
            _cellPanel.setBicycle(name, b.getDisplay(), b.getVisiblilty(), b.getRiderColor(), b.getFrameColor(), b.getComponentColor());            
            
            
        } else if(value instanceof ComponentTreeNode) {
            
            ComponentTreeNode ctn = (ComponentTreeNode)value;
            String name = ctn.toString();//gets teh name
            ImageIcon icon = ctn.getIcon();
            
            _cellPanel.setComponent(name, icon);
            
        } 
        
        
        return _cellPanel;
        
        
    }
    
    
    
    
}
