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
package org.bicycleGeometryWorkshop.ui;

import org.bicycleGeometryWorkshop.components.BicycleListener;
import org.bicycleGeometryWorkshop.ui.measure.Measure;
import org.bicycleGeometryWorkshop.ui.measure.MeasureAngle;
import org.bicycleGeometryWorkshop.ui.measure.MeasureDistance;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.bicycleGeometryWorkshop.app.BGWProject;
import org.bicycleGeometryWorkshop.app.VisualPreferences;
import org.bicycleGeometryWorkshop.components.BicycleChangeEvent;

/**
 * This class is the primary display component.  It is responsible for 
 * displaying the project and all the associated bicycles.  It also holds the references to the 
 * measuring tools which are activated through this class.
 * @author Tom
 */
public class ProjectViewer extends JPanel implements BicycleListener, MouseListener, MouseMotionListener, KeyListener {

    private BGWProject _project;
    
    private VisualPreferences _visualPreferences;

    // private ScreenMeasure _measure;
    private boolean _isMeasureActive;
    private Measure _measureActive;
    private MeasureDistance _measureDistance;
    private MeasureAngle _measureAngle;

    /**
     * Class constructor.  This sets the reference to the project.
     * @param project The project to display in the viewer.
     */
    public ProjectViewer( BGWProject project) {
        super();

        _project = project;
        _visualPreferences = project.getVisualPreferences();

        
        // _measure = new ScreenMeasure();
        _isMeasureActive = false;
        _measureActive = null;
        _measureDistance = new MeasureDistance();
        _measureAngle = new MeasureAngle();

        initViewer();

    }

    /**
     * Set the Project - used for new project.
     * @param project The project to view.
     */
    public void setProject(BGWProject project) {
        
        _project = project;

        
    }
    
    /**
     * Initial the viewer.
     */
    private void initViewer() {

        this.setBackground(Color.GRAY);
       // this.setBackground(Color.GRAY);
        //set prefereed size
        this.setPreferredSize(new Dimension(600, 600));
        this.setMinimumSize(new Dimension(120, 120));

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);

    }

    /**
     * Paint the component.  This gets the calculated bounding box from the project, 
     * does a "zoom extents" to center the project and then repaints the project (all the bicycles).  
     * A side product is that the measuring tools are updated with the transform calculated here.
     * @param g The graphics object to paint to.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //graphics object
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        //repaint background from preferences color
        g2.setBackground(_visualPreferences.getBackgroundColor());
        g2.clearRect(0, 0, this.getWidth(), this.getHeight());        

        //grab copy of current ransform
        AffineTransform origTrans = g2.getTransform();

        double vw = this.getWidth();
        double vh = this.getHeight();

        Rectangle2D fbounds = _project.getBounds();
        double fw = fbounds.getWidth();
        double fh = fbounds.getHeight();

        //width vs height scale
        double sw = vw / fw;
        double sh = vh / fh;

        //use smaller
        double scale = sw < sh ? sw : sh;

        //add padding
        scale *= 0.85;

        //calc translation to center bounds
        double tx = vw / 2 - scale * fbounds.getCenterX();
        double ty = vh / 2 + scale * fbounds.getCenterY();

        //perform transform
//        g2.translate(tx,ty);
//        g2.scale(scale,-scale);
        //new version
        AffineTransform gTrans = new AffineTransform();
        gTrans.translate(tx, ty);
        gTrans.scale(scale, -scale);

        //apply transform to graphics object
        g2.transform(gTrans);

 
        //update the transform regardless of measuring state
        _measureDistance.updateTransform(gTrans);
        _measureAngle.updateTransform(gTrans);

        //calc lineweight scale and check
        float lwScale = (float) scale;
        if (lwScale == 0) {
            lwScale = 0.001f;
        }


        /**
         *   Render the Project
         */
        //render the project (all bicycles, etc)
        _project.render(g2, lwScale);

        //render measure in world if it is active
        if (_isMeasureActive) {
            if (_measureActive != null) {
                _measureActive.renderInWorld(g2, lwScale);
            }
        }


        //restore original transform
        g2.setTransform(origTrans);

        //render measure screen if it is active
        if (_isMeasureActive) {
            if (_measureActive != null) {
                _measureActive.renderInScreen(g2);
            }
        }

    }

    /**
     * Called to notify the viewer that a bicycle has changed.  
     * This calls a repaint on the viewer.
     * @param bce The bicycle change event.
     */
    @Override
    public void bicycleChanged(BicycleChangeEvent bce) {
        this.repaint();
    }

    /**
     * Mouse Clicked Event - not used.
     * @param e The mouse event.
     */        
    @Override
    public void mouseClicked(MouseEvent e) { }

    /**
     * Mouse Pressed Event - not used.
     * @param e The mouse event.
     */     
    @Override
    public void mousePressed(MouseEvent e) {  }

    
    /**
     * Mouse Released Event.  Check for left click and fire a point clicked event if there is an active measuring tool.
     * @param e The mouse event.
     */     
    @Override
    public void mouseReleased(MouseEvent e) {

        if (SwingUtilities.isLeftMouseButton(e)) {

            if (_isMeasureActive) {
                if (_measureActive != null) {
                    Point2D mousePoint = new Point2D.Double(e.getX(), e.getY());
                    _measureActive.pointClicked(mousePoint);
                }
            }

        }

        this.repaint();

    }



    /**
     * The Mouse Entered event.  This requests focus in the window for the interactive routines (measure) to work.  
     * Otherwise it is a panel that doesn't receive focus.
     * @param e The mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {

        this.requestFocus();
    }

    /**
     * Mouse Exited Event - not used.
     * @param e The mouse event.
     */    
    @Override
    public void mouseExited(MouseEvent e) { }

    /**
     * Mouse Dragged Event - not used.
     * @param e The mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {  }

    /**
     * Mouse Moved Event.  Update the dynamic point if  a measuring tool is active.
     * @param e The mouse event.
     */    
    @Override
    public void mouseMoved(MouseEvent e) {

        if (_isMeasureActive) {
            if (_measureActive != null) {
                
                Point2D mousePoint = new Point2D.Double(e.getX(), e.getY());
                _measureActive.dynamicPoint(mousePoint);
            }
        }

        this.repaint();

    }

    /**
     * Key Typed Event - not used.
     * @param e The key event.
     */
    @Override
    public void keyTyped(KeyEvent e) { }

    /**
     * Key Pressed Event - not used.
     * @param e The key event.
     */    
    @Override
    public void keyPressed(KeyEvent e) { }

    
    /**
     * Key Released Event .  Check for the escape key and cancel any active measuring tool.
     * @param e The key event.
     */    
    @Override
    public void keyReleased(KeyEvent e) {

      
        //check for cancel to clear active measure
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            cancelMeasure();
        }

    }

    /**
     * Activate the distance measuring tool.
     */
    public void beginMeasureDistance() {

        _isMeasureActive = true;
        _measureDistance.resetMeasure();
        _measureActive = _measureDistance;
        this.repaint();

    }

    /**
     * Activate the angle measuring tool.
     */
    public void beginMeasureAngle() {

        _isMeasureActive = true;
        _measureAngle.resetMeasure();
        _measureActive = _measureAngle;
        this.repaint();

    }
    
    /**
     * Cancel the active measurement.
     */
    public void cancelMeasure() {
        
            _isMeasureActive = false;
            _measureActive = null;

            this.repaint();        
        
    }

}//end class
