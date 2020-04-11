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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.bicycleGeometryWorkshop.components.BicycleDisplay;

/**
 *  This panel is used for rendering tree cells based on the TreeCellRenderer Interface.  
 * 
 * @author Tom
 */
public class TreeCellRendererPanel extends JPanel {

    private ImageIcon _iconRider;
    private ImageIcon _iconPose;

    private ImageIcon _iconBicycle;
    private ImageIcon _iconFrame;
    
    private ImageIcon _iconBicycleGroup;

    private ImageIcon _iconNodeOn;
    private ImageIcon _iconNodeOff;

    private JLabel _iconLabel;

    private JLabel _bicycleOnOffLabel;
    private JLabel _bicycleRiderLabel;
    private JLabel _bicycleFrameLabel;
    private JLabel _bicycleCompLabel;

    private JLabel _nameLabel;
    
    private Color _bgColor;
    private Color _borderColor;
    private Border _border;
    private Border _emptyBorder;

    /**
     * Class constructor.
     */
    public TreeCellRendererPanel() {
        super();

        init();
    }

    /**
     * Initialize the panel
     */
    private void init() {

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setVgap(2);
        flowLayout.setHgap(2);

        this.setLayout(flowLayout);

        this.setOpaque(false);
        
        _iconLabel = new JLabel();

        _bicycleOnOffLabel = new JLabel();
        _bicycleRiderLabel = makeColoredLabel("R"); //new JLabel("R");
        _bicycleFrameLabel = makeColoredLabel("F"); //new JLabel("F");
        _bicycleCompLabel = makeColoredLabel("C"); //new JLabel("C");
              
      

        _nameLabel = new JLabel("<name>");
        
       
        add(_iconLabel);
        add(_bicycleOnOffLabel);
        add(_bicycleRiderLabel);
        add(_bicycleFrameLabel);
        add(_bicycleCompLabel);

        add(_nameLabel);
        
        /*
            ---  Resolve Icons ---
         */
        URL riderIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/RiderIcon16.png");
        URL poseIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/PoseIcon16.png");

        URL bicycleIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/BicycleIcon16.png");
        URL frameIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/FrameIcon16.png");
        
        
        URL groupIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/BicycleGroup16.png");

        URL nodeOnIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/NodeOnIcon16.png");
        URL nodeOffIconURL = this.getClass().getResource("/org/bicycleGeometryWorkshop/resources/icons/NodeOffIcon16.png");

        _iconRider = new ImageIcon(riderIconURL);
        _iconPose = new ImageIcon(poseIconURL);

        _iconBicycle = new ImageIcon(bicycleIconURL);
        _iconFrame = new ImageIcon(frameIconURL);
        
        _iconBicycleGroup = new ImageIcon(groupIconURL);

        _iconNodeOn = new ImageIcon(nodeOnIconURL);
        _iconNodeOff = new ImageIcon(nodeOffIconURL);
        
        _bgColor = new Color(184,207,229);
        _borderColor = new Color(99,130,191);
        _border = BorderFactory.createLineBorder(_borderColor, 1);
        _emptyBorder = BorderFactory.createEmptyBorder();

    }
    
    /**
     * Make a colored label for rider display settings.
     * @param name  The name to display in the label.
     * @return The label with a colored background.
     */
    private JLabel makeColoredLabel(String name) {
        
        JLabel label = new JLabel(name);
        
        label.setHorizontalAlignment(JLabel.CENTER);
        
        label.setOpaque(true);
        
        Dimension minSize = new Dimension(14,14);
        label.setMinimumSize(minSize);        
        label.setPreferredSize(minSize);

        Border clrBrdr = BorderFactory.createLineBorder(Color.BLACK, 1);
        label.setBorder(clrBrdr);        
        
        return label;
        
    }
    
    

    /**
     * Set opaque and color settings for selected.
     * @param selected True if node is selected, false otherwise.
     */
    public void setSelected(boolean selected) {

        if (selected) {
            this.setOpaque(true);
            this.setBackground(_bgColor);
            this.setBorder(_border);
 
        } else {
            this.setOpaque(false);
            
            this.setBorder(_emptyBorder);
            
        }

    }
    
    /**
     * Set Display as a component (icon and name)
     * @param name Name of the component.
     * @param icon Icon for the component.
     */
    public void setComponent(String name, ImageIcon icon) {
        
        hideBicycleIcons();
        
        _iconLabel.setIcon(icon);
        _nameLabel.setText(name);
        
        
    }
    
    /**
     * Set the component to display a bicycle group node.
     */
   public void setBicycleGroup() {
        
        hideBicycleIcons();
        
        _iconLabel.setIcon(_iconBicycleGroup);
        _nameLabel.setText("Bicycles");
        
        
    }    
    
   /**
    * Set the panel to display a bicycle.
    * @param name  The name of the bicycle.
     * @param display The display mode of the bicycle.
    * @param visible The visibility state of the bicycle.
    * @param riderColor  The rider color.
    * @param frameColor  The frame color.
    * @param compColor The component color.
    */
    public void setBicycle(String name, BicycleDisplay display, boolean visible, Color riderColor, Color frameColor, Color compColor) {
        
        showBicycleIcons();
        
        //set the name
        _nameLabel.setText(name); 
        
        //resolve icon
        ImageIcon icon = _iconBicycle;
        switch(display) {
            case BicycleAndRider :
                icon = _iconPose;
                break;
            case Bicycle :
                icon = _iconBicycle;
                break;
            case FrameOnly :
                icon = _iconFrame;
                
        }
        //set the icon
        _iconLabel.setIcon(icon);
               
        
        if(visible) {
          _bicycleOnOffLabel.setIcon(_iconNodeOn);
        } else {
            _bicycleOnOffLabel.setIcon(_iconNodeOff);
        }
        
        _bicycleRiderLabel.setBackground(riderColor);
        _bicycleFrameLabel.setBackground(frameColor);
        _bicycleCompLabel.setBackground(compColor);       
        
        
    }
    
    /**
     * Hide the bicycle icons
     */
    private void hideBicycleIcons() {

        _bicycleOnOffLabel.setVisible(false);
        _bicycleRiderLabel.setVisible(false);
        _bicycleFrameLabel.setVisible(false);
        _bicycleCompLabel.setVisible(false);
        
    }
    
    /**
     * Show the bicycle icons
     */
    private void showBicycleIcons() {
       
        
        _bicycleOnOffLabel.setVisible(true);
        _bicycleRiderLabel.setVisible(true);
        _bicycleFrameLabel.setVisible(true);
        _bicycleCompLabel.setVisible(true);
        
    }    
    
    

}
