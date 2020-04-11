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
package org.bicycleGeometryWorkshop.app;

import org.bicycleGeometryWorkshop.database.BGWDataBase;
import org.bicycleGeometryWorkshop.components.BicycleListener;
import org.bicycleGeometryWorkshop.geometry.Utilities;
import org.bicycleGeometryWorkshop.components.Bicycle;
import org.bicycleGeometryWorkshop.components.ComponentOwner;
import org.bicycleGeometryWorkshop.report.Report;
import org.bicycleGeometryWorkshop.components.RiderMeasurements;
import org.bicycleGeometryWorkshop.components.RiderPose;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.bicycleGeometryWorkshop.app.undo.AttributeUndo;
import org.bicycleGeometryWorkshop.app.undo.BaseUndo;
import org.bicycleGeometryWorkshop.app.undo.BicycleAddUndo;
import org.bicycleGeometryWorkshop.app.undo.BicycleDeleteUndo;
import org.bicycleGeometryWorkshop.app.undo.BicycleMoveUndo;
import org.bicycleGeometryWorkshop.app.undo.UndoManager;
import org.bicycleGeometryWorkshop.attributes.AttributeChangeEvent;
import org.bicycleGeometryWorkshop.components.BicycleChangeEvent;
import org.bicycleGeometryWorkshop.components.BicycleDisplay;
import org.bicycleGeometryWorkshop.components.BicycleEventType;
import org.bicycleGeometryWorkshop.components.ComponentChangeEvent;
import org.bicycleGeometryWorkshop.report.ReportValue;
import org.bicycleGeometryWorkshop.ui.BicycleGeometryWorkshopUI;

/**
 * This is the main class for the project presented in the UI. The project
 * consists of the rider measurements, the rider pose, project preferences, and
 * the collection of bicycles.
 * <p>
 * The project's undo queue is managed internally in the UndoManager class.
 * Component change events are captured at the project level and added to the
 * undo queue.
 * <p>
 * The BGWProject class handles file IO internally in the BGWDataBAse class. The
 * majority of IO is done through that class with the exception of the
 * BGWLibrary class which serves as a wrapper over the database.
 *
 *
 * @author Tom
 */
public class BGWProject implements ComponentOwner, BicycleListener {

    private RiderMeasurements _riderSize;
    private RiderPose _riderPose;

    private ArrayList<Bicycle> _bicycles;
    //private Bicycle _activeBicycle;

    private VisualPreferences _visualPrefs;

    private ProjectListener _listener;

    private BGWDataBase _db;
    private boolean _isDirty;

    private UndoManager _undo;

    /**
     * Class constructor. The class is initialized with the project listener
     * (UI).
     *
     * @param listener The project listener.
     */
    public BGWProject(ProjectListener listener) {

        _isDirty = false; //no chenges to save

        _db = new BGWDataBase();

        _listener = listener;

        _riderSize = new RiderMeasurements(this);
        _riderPose = new RiderPose(this);

        //visual preferences
        _visualPrefs = new VisualPreferences(this);

        //intialize list
        _bicycles = new ArrayList();

        _undo = new UndoManager();

        //create default bicycle
        createDefaultBicycle();

    }

    /**
     * Add a default bicycle without notifying the listener.
     */
    private void createDefaultBicycle() {

        Bicycle bicycle = new Bicycle("Bicycle 1", _riderSize, _riderPose, _visualPrefs);
        //set listener
        bicycle.setBicycleListener(this);
        //add to the collection
        _bicycles.add(bicycle);

    }

    //<editor-fold defaultstate="collapsed" desc="Undo Methods">
    /**
     * Perform an undo action.
     */
    public void undo() {
        _undo.undo();
    }

    /**
     * Perform a redo action.
     */
    public void redo() {
        _undo.redo();
    }

    /**
     * Add an undo to the stack from outside the project. This is done for UI
     * changes (background color, etc).
     *
     * @param undo The undo object to push to the undo stack.
     */
    public void pushUndo(BaseUndo undo) {

        _undo.pushUndo(undo);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="File Methods (open, save,etc)">
    /**
     * Do a file dirty check (save changes to file?).
     *
     * @return True if the operation can continue, false is the flag to cancel.
     */
    public boolean dirtyCheck() {
        boolean proceed = false;

        if (_isDirty) {
            String mssg = "The project has unsaved changes, save changes?";

            JFrame frame = BicycleGeometryWorkshopUI.getActiveFrame();
            int result = JOptionPane.showConfirmDialog(frame, mssg, "Project changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            //check results
            if (result == JOptionPane.YES_OPTION) {
                //atempt to save
                proceed = _db.saveFile(this);
            } else if (result == JOptionPane.NO_OPTION) {
                //discard changes
                proceed = true;
            } else {
                proceed = false;
            }

        } else {
            //file not dirty
            proceed = true;
        }

        return proceed;
    }

    /**
     * Save the file
     */
    public void saveFile() {

        _db.saveFile(this);
        _isDirty = false;
    }

    /**
     * Save file to new file.
     */
    public void saveFileAs() {

        _db.saveFileAs(this);
        _isDirty = false;
    }

    public void openFile() {

        if (!dirtyCheck()) {
            return;
        }

        boolean opened = _db.openFile(this);

        if (opened) {

            postLoadUpdate();
//            _undo.reset();
//            System.out.println("--Updating after open...");
//            //attach geometry listeners to bicycles
//            for (Bicycle b : _bicycles) {
//                b.setBicycleListener(this);
//                b.updateAllComponents();
//            }
//            
//            _listener.projectedLoadedFromDB();
//            
//            _listener.updateReport();
//            
//            _listener.redrawViewer();
//            
//            _isDirty = false;

        }

    }

    /**
     * Open the default file if it exists.
     */
    public void openDefaultFile() {

        String workingDir = System.getProperty("user.dir");
        String ps = File.separator;
        String path = workingDir + ps + "Library" + ps + "default" + "." + BGWDataBase.FILE_EXT;

        File defaultFile = new File(path);
        //open if it exists
        if (defaultFile.exists()) {

            //do update on successfull load
            if (_db.openFile(this, path)) {

                postLoadUpdate();
                //reset the database flags for open file
                _db.resetOpenFlags();

            }

        }

    }

    /**
     * Update the project after it has been loaded from disk.
     */
    private void postLoadUpdate() {

        _undo.reset();
        System.out.println("--Updating after open...");
        //attach geometry listeners to bicycles
        for (Bicycle b : _bicycles) {
            b.setBicycleListener(this);
            b.updateAllComponents();
        }

        _listener.projectedLoadedFromDB();

        _listener.updateReport();

        _listener.redrawViewer();

        _isDirty = false;

    }

    /**
     * Get the saved file path (path and file name). If the file has not been
     * saved this will be an empty string.
     *
     * @return The file path or an empty string.
     */
    public String getFilePath() {
        return _db.getFilePath();
    }

    /**
     * Get the flag for changes to the project.
     *
     * @return True if the project needs to be saved, false otherwise.
     */
    public boolean isDirty() {
        return _isDirty;
    }
//</editor-fold>

    /**
     * Add a Bicycle to the Collection at the specified index. If index == -1,
     * then add to the end, otherwise insert into position.
     *
     * @param name The name of the Bicycle
     *
     *
     *
     */
    public final void addBicycle(String name) {
        Bicycle bicycle = new Bicycle(name, _riderSize, _riderPose, _visualPrefs);
        //set listener
        bicycle.setBicycleListener(this);

        _bicycles.add(bicycle);

        //project changed
        _isDirty = true;

        BicycleAddUndo addUndo = new BicycleAddUndo(this, bicycle);
        _undo.pushUndo(addUndo);

        //notify listener
        _listener.bicycleAdded(bicycle);

        //return bicycle;
    }

    /**
     * Restore a Bicycle - used from undo delete.
     *
     * @param bicycle The bicycle to add.
     * @param index The index in the list
     */
    public final void restoreBicycle(Bicycle bicycle, int index) {

        //set listener
        bicycle.setBicycleListener(this);


        //check index for range
        int pos = index;  

        if(pos == -1) {
            //add to list
            _bicycles.add( bicycle);
        } else {
            //insert into list
            _bicycles.add(pos, bicycle);            
        }


        //project changed
        _isDirty = true;

        //notify listener - use orignal index for -1 signal
        _listener.bicycleRestored(bicycle, index);

 
    }

    /**
     * Delete a Bicycle from the project.
     *
     * @param bicycle Bicycle to delete.
     *
     */
    public void deleteBicycle(Bicycle bicycle) {

        //don't delete the last bicycle
        if (_bicycles.size() > 1) {
            //double check and delete
            if (_bicycles.contains(bicycle)) {

                int index = _bicycles.indexOf(bicycle);
                _bicycles.remove(bicycle);

                //push undo
                BicycleDeleteUndo deleteUndo = new BicycleDeleteUndo(this, bicycle, index);
                _undo.pushUndo(deleteUndo);

                updateUI();

                //send event for deletd (updates navigator);
                _listener.bicycleDeleted(bicycle);

                //project changed
                _isDirty = true;

            }
        }//end if size

    }

    /**
     * Move Bicycle up in the list.  This moves
     * the bicycle higher up the draw order, it 
     * will be drawn over lower bicycles.
     *
     * @param bicycle  The bicycle to move up.
     *
     */
    public void moveBicycleUp(Bicycle bicycle) {

        int index = _bicycles.indexOf(bicycle);

        if (index >= 0) {
            //int index = _bicycles.indexOf(bicycle);
            if (index - 1 >= 0) {

                _bicycles.remove(bicycle);
                _bicycles.add(index - 1, bicycle);

                BicycleMoveUndo moveUndo = new BicycleMoveUndo(this, bicycle, true);
                _undo.pushUndo(moveUndo);

                //update ui
                updateUI();
                //notify listener
                _listener.bicycleMovedUp(bicycle);

                //project changed
                _isDirty = true;

            }

        }

    }

    /**
     * Move a bicycle down in the list.
     * This will move it lower and the draw order.
     * It will be draw below bicycles higher in the list.
     *
     * @param bicycle  The bicycle to move down the list.
     *
     */
    public void moveBicycleDown(Bicycle bicycle) {

        int index = _bicycles.indexOf(bicycle);
        if (index >= 0) {

            int count = _bicycles.size();
            if (index < count - 1) {

                _bicycles.remove(bicycle);
                _bicycles.add(index + 1, bicycle);

                BicycleMoveUndo moveUndo = new BicycleMoveUndo(this, bicycle, false);
                _undo.pushUndo(moveUndo);

                updateUI();
                //notify listener
                _listener.bicycleMovedDown(bicycle);
                //project changed
                _isDirty = true;

            }

        }

    }

    /**
     * Set all the Bicycles to this visibility (all on or off).
     *
     * @param visible True to turn all bicycles on, false to turn all bicycles
     * off.
     */
    public void setAllVisibility(boolean visible) {

        for (Bicycle b : _bicycles) {
            b.setBicycleVisibility(visible);
        }

    }

    /**
     * Set all Bicycles to this display mode.
     *
     * @param display The display mode to apply to all the bicycles.
     */
    public void setAllDisplay(BicycleDisplay display) {
        for (Bicycle b : _bicycles) {
            b.setBicycleDisplay(display);
        }
    }

    public void bicycleIsolate(Bicycle bicycle) {

        for (Bicycle b : _bicycles) {

            if (b.equals(bicycle)) {
                b.setBicycleVisibility(true);
            } else {
                b.setBicycleVisibility(false);
            }
        }

    }

    /**
     * Common UI Update tasks to call on listener
     */
    private void updateUI() {

        _listener.updateReport();

        _listener.redrawViewer();

    }

    /**
     * Get the report data for the JPanel.
     *
     * @return The column header and data array.
     */
    public ReportValue[][] getReportData() {

        int bCount = _bicycles.size();

        ReportValue[] cols = Report.getColumnHeads();
        ReportValue[][] data = new ReportValue[bCount][];

        for (int i = 0; i < bCount; i++) {

            Bicycle b = _bicycles.get(i);
            Report r = b.getReport();
            ReportValue[] dataRow = r.getDataRow(cols);
            data[i] = dataRow;

        }

        return data;

    }

    /**
     * Update the Model - this is called when Rider Size or Rider Pose is changed.
     *
     * @param compEvent The component event object.
     */
    @Override
    public void componentChanged(ComponentChangeEvent compEvent) {

        String compName = compEvent.getComponent().getName();
        System.out.println(">>Shared Component Changed: " + compName);

        for (Bicycle b : _bicycles) {
            b.updateFromRiderPose(compEvent);
        }

        //update the report
        _listener.updateReport();
        //redraw the viewer
        _listener.redrawViewer();

        //add the undo
        AttributeUndo au = new AttributeUndo(compEvent.getAttributeEvent());
        _undo.pushUndo(au);

        //project changed
        _isDirty = true;

        _listener.bicycleChanged();

    }

    /**
     * Called by a bicycle to notify geometry.
     *
     * @param bicycleEvent attribute change
     */
    @Override
    public void bicycleChanged(BicycleChangeEvent bicycleEvent) {

        //project changed
        _isDirty = true;

        //notify UI to redraw viewer
        // System.out.println("Bicycle in project changed");
        _listener.updateReport();

        _listener.redrawViewer();

        _listener.bicycleChanged();

        //create undo
        BicycleEventType eType = bicycleEvent.getEventType();
        AttributeChangeEvent attEvent = null;
        switch (eType) {
            case ComponentAttribute:
                attEvent = bicycleEvent.getComponentEvent().getAttributeEvent();
                break;
            case BicycleAttribute:
                attEvent = bicycleEvent.getAttributeEvent();
                break;
        }
        if (attEvent != null) {
            AttributeUndo au = new AttributeUndo(attEvent);
            _undo.pushUndo(au);
        }

    }

    /**
     * Start an undo group. Catch all undo events in a group. (used mainly for
     * import)
     */
    public void undoOpenGroup() {
        _undo.openGroup();
    }

    /**
     * Close an undo group. Stops grouping undos.
     *
     */
    public void undoCloseGroup() {
        _undo.closeGroup();
    }


    /**
     * Get the rider measurements.
     *
     * @return The rider measurements component.
     */
    public RiderMeasurements getRiderSize() {
        return _riderSize;
    }

    /**
     * Get the rider pose.
     * @return The rider pose component.
     */
    public RiderPose getRiderPose() {
        return _riderPose;
    }

    /**
     * Get the visual preferences.  The visual preferences
     * controls color and appearance items like background color, etc.
     * @return The visual  preferences object.
     */
    public VisualPreferences getVisualPreferences() {
        return _visualPrefs;
    }

    /**
     * Get the list of bicycles.
     *
     * @return A list of all bicycles in the project.
     */
    public ArrayList<Bicycle> getBicycles() {

        return _bicycles;
    }

    /**
     * Get the bounds of the project - used for the viewer.
     * This is the bounding box that encompasses all the bicycles
     * so that the viewer can perform an auto "zoom extents" operation.
     *
     * @return The bounds of the project.
     */
    public Rectangle2D getBounds() {

        Rectangle2D bounds = new Rectangle2D.Double(0, 0, 1, 1);
        boolean hasFirst = false;

        for (Bicycle b : _bicycles) {
            //get the bounds
            Rectangle2D cb = b.getBounds();

            //get the area of the bounds - invisible bouns have area < 1
            double area = cb.getWidth() * cb.getHeight();

            if (area > 1) {

                if (!hasFirst) {
                    bounds.setFrame(cb);
                    hasFirst = true;
                } else {
                    bounds = Utilities.getUnionBoolean(bounds, cb);
                }

            }

        }//end for        

        return bounds;
    }

    /**
     * Renders the bicycles in the project.  This is called from the project viewer.
     *
     * @param g2  The graphics object to render to.
     * @param scale  The scale of the current view - used for scaling line weights, etc.
     */
    public void render(Graphics2D g2, float scale) {

        int bikeCount = _bicycles.size();

        //render
        for (int i = bikeCount - 1; i >= 0; i--) {

            Bicycle b = _bicycles.get(i);
            b.render(g2, scale, _visualPrefs);

        }

    }


}
