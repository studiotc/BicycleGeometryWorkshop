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

import org.bicycleGeometryWorkshop.app.ProjectListener;
import org.bicycleGeometryWorkshop.ui.tree.NavigatorTree;
import org.bicycleGeometryWorkshop.ui.tree.NavigatorListener;
import org.bicycleGeometryWorkshop.attributes.AttributeSetPanel;
import org.bicycleGeometryWorkshop.components.Bicycle;
import org.bicycleGeometryWorkshop.report.Report;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import org.bicycleGeometryWorkshop.database.BGWLibrary;
import org.bicycleGeometryWorkshop.app.BGWProject;
import org.bicycleGeometryWorkshop.app.UnitsDisplay;
import org.bicycleGeometryWorkshop.app.UnitsListener;
import org.bicycleGeometryWorkshop.app.undo.AttributeUndo;
import org.bicycleGeometryWorkshop.attributes.AttributeSetScrollPane;
import org.bicycleGeometryWorkshop.components.BaseComponent;
import org.bicycleGeometryWorkshop.components.BicycleDisplay;
import org.bicycleGeometryWorkshop.components.ComponentChangeEvent;
import org.bicycleGeometryWorkshop.components.ComponentOwner;
import org.bicycleGeometryWorkshop.report.ReportValue;

/**
 * This is the main UI class. This class manages the Project, Navigator, editor
 * area, Viewer, and Report.
 * <p>
 * The UI acts as an intermediary between the Project and the Navigator via the
 * listener class (Navigator and Project listeners). This was done instead of plugging one
 * into the other and allowed for easier integration of the undo mechanism.
 * 
 * @author Tom
 */
public class BicycleGeometryWorkshopUI extends JFrame implements NavigatorListener, ProjectListener, UnitsListener, ComponentOwner {

    //frame reference for dialog boxes/option panes
    private static JFrame UI_JFRAME = null;

    private static final String PROJECT_NAME = "Bicycle Geometry Workshop";
    private static final String PROJECT_PHASE = "Beta";
    private static final float PROJECT_VER = 1.0f;

    private JPanel _panelRight;
    //bicycle viewer
    private ProjectViewer _viewer;

    private JTable _reportTable;
    private DefaultTableModel _reportModel;

    //split pane (vertical) for navigator and editor
    private JSplitPane _controlPaneLeft;

    private JSplitPane _viewPaneRight;

    private BGWProject _project;
    private NavigatorTree _navigator;

    private BGWLibrary _library;

    private UnitsMMAction _actionMMUnits;
    private UnitsINAction _actionINUnits;
    private AttributeSetScrollPane _lastScrollPane;

    /**
     *
     * Class Constructor.
     */
    public BicycleGeometryWorkshopUI() {
        super();

        _library = new BGWLibrary();

        _library.openLibrary();

        _project = new BGWProject(this);

        _viewer = new ProjectViewer(_project);

        //setup the report table     
        _reportModel = new DefaultTableModel();
        _reportTable = new JTable(_reportModel);

        //control pane panel
        _controlPaneLeft = new JSplitPane();

        _viewPaneRight = new JSplitPane();

        //new version
        _panelRight = new JPanel();

        _lastScrollPane = null;

        _navigator = new NavigatorTree(this);

        loadNavigatorFromProject();

        initUI();

        //try to load default project
        //do this last or the events will fire and hit nulls...
        _project.openDefaultFile();

        //this needs to happen here last.
        setFrameTitle();
        
        //set the frame visible
        this.setVisible(true);        

    }

    /**
     * Load the Navigator from the project
     */
    private void loadNavigatorFromProject() {

        _navigator.clearNavigator();

        //set the rider measurements and pose editors
        _navigator.setRiderMeasurements(_project.getRiderSize());
        _navigator.setRiderPose(_project.getRiderPose());

        _navigator.setVisualPreferences(_project.getVisualPreferences());

        ArrayList<Bicycle> bicycles = _project.getBicycles();

        for (Bicycle b : bicycles) {
            _navigator.addBicycle(b);
        }

        //set the default editor
        _navigator.setDefaultEditorSelected();

        //update the report
        this.updateReport();

    }

    /**
     * *
     * Initialize the UI
     */
    private void initUI() {

        this.setMinimumSize(new Dimension(200, 200));
        this.setPreferredSize(new Dimension(1200, 800));

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //window listenrs
        JFrame thisFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                //do shutdown
                onWindowClose(we);

            }

            @Override
            public void windowActivated(WindowEvent e) {

                //set the current window to the static reference
                UI_JFRAME = thisFrame;
            }

        });

        //actions for units menu
        _actionMMUnits = new UnitsMMAction(this);
        _actionINUnits = new UnitsINAction(this);
        //intialize the states - call listener method once
        unitsDisplayChanged();
        //add window as listener
        UnitsDisplay.addListener(this);

        /**
         * Build the menus
         */
        constructMenus();

        /**
         * Panels
         */
        JSplitPane topLevelSplitPane = new JSplitPane();
        topLevelSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        _viewPaneRight.setOrientation(JSplitPane.VERTICAL_SPLIT);

        //add the viewer
        _viewPaneRight.setTopComponent(_viewer);

        JScrollPane tableSp = new JScrollPane(_reportTable);
        tableSp.setPreferredSize(new Dimension(300, 60));

        _viewPaneRight.setBottomComponent(tableSp);

        _controlPaneLeft.setOrientation(JSplitPane.VERTICAL_SPLIT);
        _controlPaneLeft.setDividerLocation(240);

        JScrollPane scrollTree = new JScrollPane(_navigator);
        //set to top placement in control pane
        _controlPaneLeft.setTopComponent(scrollTree);

        AttributeSetScrollPane assp = new AttributeSetScrollPane(_project.getRiderSize().getAttributeSet().getEditor());
        _controlPaneLeft.addContainerListener(assp);
        _controlPaneLeft.setBottomComponent(assp);
        _lastScrollPane = assp;

        //add control panel (vertical split)
        topLevelSplitPane.setLeftComponent(_controlPaneLeft);

        //set the right hand pane
        topLevelSplitPane.setRightComponent(_viewPaneRight);

        //add the top level split pane
        this.add(topLevelSplitPane);

        //pack the UI
        this.pack();


        //set the divider location via proportion
        _viewPaneRight.setDividerLocation(0.90);

//        //set the frame visible
//        this.setVisible(true);

    }

    /**
     * this is the project name (Bicycle Geometry Workshop) along with version ID for use in the title
     * and About Dialog.
     * @return A string with project name and version number.
     */
    public static String projectName() {
        return PROJECT_NAME + " [" + PROJECT_PHASE + " : " + PROJECT_VER + "] ";
    }
    /**
     * Update the title with file dirty status and file path if available.
     */
    private void setFrameTitle() {

        String title = projectName(); //PROJECT_NAME + " [" + PROJECT_PHASE + " : " + PROJECT_VER + "] ";

        //get the file path from the project
        String filePath = _project.getFilePath();
        boolean dirty = _project.isDirty();

        if (dirty) {
            title += " | *** \u0394 *** | ";  //"\u0394"  *!*
        } else {
            title += " | \u2713 No Changes | ";
        }

        if (filePath.isEmpty()) {
            title += "<File not saved>";
        } else {
            title += filePath;
        }

        //set the title
        this.setTitle(title);

    }

    /**
     * Window Closing Event. Check for any open databases, etc...
     *
     * @param we Window Event for closing.
     */
    private void onWindowClose(WindowEvent we) {

        boolean closeOk = _project.dirtyCheck();

        if (closeOk) {

            System.out.println("Closing window...");
            //close the library
            if (_library != null) {
                _library.closeLibrary();
            }

            //clear units listener
            UnitsDisplay.removeListener(this);

            //clear reference to frame
            UI_JFRAME = null;

            System.out.println("Exiting...");
            //exit system
            System.exit(0);

        }//end if do closse

    }

    /**
     * Static reference to the current window. This is updated when the window
     * is activated. The reference is used for dialog display as the reference
     * to the owner window.
     *
     * @return A reference to the current window.
     */
    public static final JFrame getActiveFrame() {
        return UI_JFRAME;
    }

    /**
     * Construct the menus.
     *
     */
    private void constructMenus() {

        JMenuBar menuBar = new JMenuBar();

        int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        /**
         * File Menu Section
         */
        JMenu fileMenu = new JMenu("File");

        // -- New project --
        JMenuItem projNewItem = new JMenuItem("New");
        ActionListener pniAL = (ActionEvent ev) -> {
            projectNew();
        };
        //add the listener
        projNewItem.addActionListener(pniAL);

        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, scMask);
        projNewItem.setAccelerator(ctrlN);

        //add to menu
        fileMenu.add(projNewItem);

        //add seperator
        fileMenu.addSeparator();

        // -- Open project --
        JMenuItem projOpenItem = new JMenuItem("Open");
        ActionListener poiAL = (ActionEvent ev) -> {
            projectOpen();
        };
        //add the listener
        projOpenItem.addActionListener(poiAL);

        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, scMask);
        projOpenItem.setAccelerator(ctrlO);
        //add to menu
        fileMenu.add(projOpenItem);

        //add seperator
        fileMenu.addSeparator();

        // -- Save project --
        JMenuItem projSaveItem = new JMenuItem("Save");
        ActionListener psiAL = (ActionEvent ev) -> {
            projectSave();
        };
        //add the listener
        projSaveItem.addActionListener(psiAL);

        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, scMask);
        projSaveItem.setAccelerator(ctrlS);

        //add to menu
        fileMenu.add(projSaveItem);

        // -- SaveAs project --
        JMenuItem projSaveAsItem = new JMenuItem("Save As");
        ActionListener psaiAL = (ActionEvent ev) -> {
            projectSaveAs();
        };
        //add the listener
        projSaveAsItem.addActionListener(psaiAL);

        KeyStroke ctrlSShift = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK + Event.SHIFT_MASK);
        projSaveAsItem.setAccelerator(ctrlSShift);
        //add to menu
        fileMenu.add(projSaveAsItem);



        //add seperator
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        ActionListener exitAL = (ActionEvent ev) -> {
            closeWindow();
        };
        //add the listener
        exitItem.addActionListener(exitAL);
        //add to menu
        fileMenu.add(exitItem);

        //add file menu to menu bar
        menuBar.add(fileMenu);

        /**
         * Edit Menu Items
         */
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoItem = new JMenuItem("Undo");
        ActionListener undoAL = (ActionEvent ev) -> {
            performUndo();
        };
        //add the listener
        undoItem.addActionListener(undoAL);

        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, scMask);
        undoItem.setAccelerator(ctrlZ);
        //add to menu
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("Redo");
        ActionListener redoAL = (ActionEvent ev) -> {
            performRedo();
        };
        //add the listener
        redoItem.addActionListener(redoAL);

        KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, scMask);
        redoItem.setAccelerator(ctrlY);
        editMenu.add(redoItem);

        editMenu.addSeparator();

        /**
         * Units
         */
        JMenu unitsMenu = new JMenu("Display Units");

        JMenuItem mmItem = new JMenuItem(_actionMMUnits);
        unitsMenu.add(mmItem);

        JMenuItem inItem = new JMenuItem(_actionINUnits);
        unitsMenu.add(inItem);

        editMenu.add(unitsMenu);

        //add edit menu to menu bar
        menuBar.add(editMenu);

        /**
         * Measuring Menu Items
         */
        JMenu measureMenu = new JMenu("Measure");

        //distance measurement
        JMenuItem measureDistMenuItem = new JMenuItem("Distance");

        ActionListener alDist = (ActionEvent ev) -> {
            measureDistance();
        };

        measureDistMenuItem.addActionListener(alDist);

        KeyStroke ctrlD = KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK);
        measureDistMenuItem.setAccelerator(ctrlD);        
        
        measureMenu.add(measureDistMenuItem);

        //angle measurement
        JMenuItem mesureAngleMenuItem = new JMenuItem("Angle");
        ActionListener alAngle = (ActionEvent ev) -> {
            measureAngle();
        };

        mesureAngleMenuItem.addActionListener(alAngle);
        
        KeyStroke ctrlA = KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK);
        mesureAngleMenuItem.setAccelerator(ctrlA);        

        measureMenu.add(mesureAngleMenuItem);

        measureMenu.addSeparator();

        //cancel measurement
        JMenuItem measureCancelMenuItem = new JMenuItem("Cancel");
        ActionListener alCancel = (ActionEvent ev) -> {
            measureCancel();
        };

        measureCancelMenuItem.addActionListener(alCancel);

        measureMenu.add(measureCancelMenuItem);

        //add to main menu bar
        menuBar.add(measureMenu);

        /**
         * Help and About
         */
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuItem = new JMenuItem("Help Index");
        ActionListener alHelp = (ActionEvent ev) -> {
            showHelp();
        };
        helpMenuItem.addActionListener(alHelp);
        helpMenu.add(helpMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        ActionListener alAbout = (ActionEvent ev) -> {
            JFrame frame = BicycleGeometryWorkshopUI.getActiveFrame();
            AboutDialog about = new AboutDialog(frame);
            boolean shown = about.showDialog();
        };
        aboutMenuItem.addActionListener(alAbout);
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        //set menu bar to window
        this.setJMenuBar(menuBar);

    }

    /**
     * This is the component owner event handler. This handles the events
     * generated from the visual preferences object, that is the only component
     * monitored here.
     *
     * @param compEvent The component changed event object.
     */
    @Override
    public void componentChanged(ComponentChangeEvent compEvent) {

        //add an undo tothe project
        AttributeUndo au = new AttributeUndo(compEvent.getAttributeEvent());
        _project.pushUndo(au);

        redrawViewer();

    }

//<editor-fold defaultstate="collapsed" desc="Action Classes for Display Units menu ">
    /**
     * Action for the display units sub-menu items. Using an action allows for
     * check next to name and disabling of menu items.
     */
    abstract class UnitsAction extends AbstractAction {

        private JFrame frame;
        private static final String CHECK_STR = "\u2713";
        private String name;
        private boolean checked;
        private String displayName;

        public UnitsAction(JFrame frame, String name, String desc) {
            super(name);
            this.frame = frame;
            this.name = name;
            this.displayName = name;
            checked = false;
            putValue(SHORT_DESCRIPTION, desc);

            updateDisplayName();

        }

        public void setChecked(boolean checked) {
            this.checked = checked;
            updateDisplayName();
        }

        protected void repaintFrame() {
            frame.repaint();
        }

        private void updateDisplayName() {

            if (checked) {
                displayName = CHECK_STR + name;
            } else {
                displayName = name;
            }
            putValue(Action.NAME, displayName);
        }

    }

    /**
     * Action for Display Units - Millimeters menu item
     */
    class UnitsMMAction extends UnitsAction {

        public UnitsMMAction(JFrame frame) {
            super(frame, "Millimeters", "Set the display units to mm");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UnitsDisplay.setNaturalUnits();
            updateReport();
            repaintFrame();
        }
    }

    /**
     * Action for Display Units - Inches menu item
     */
    class UnitsINAction extends UnitsAction {

        public UnitsINAction(JFrame frame) {
            super(frame, "Inches", "Set the display units to inches");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UnitsDisplay.setImperialUnits();
            updateReport();
            repaintFrame();
        }
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Measure Menu methods section">
    /**
     * Start measuring a distance.
     */
    private void measureDistance() {
        _viewer.beginMeasureDistance();
    }

    /**
     * Start measuring an angle.
     */
    private void measureAngle() {
        _viewer.beginMeasureAngle();
    }

    /**
     * Cancel any active measurement (menu)
     */
    private void measureCancel() {
        _viewer.cancelMeasure();
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="File Menu methods section">
    /**
     * Close the WIndow and exit the application. This is for the menu to exit
     * the application.
     */
    private void closeWindow() {

        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Method to start a new default project - used as a menu hook.
     */
    private void projectNew() {

        //check for changes to save
        //do this check externaly to project since
        //we are creating a new project.
        if (!_project.dirtyCheck()) {
            return;
        }

        //start a new project
        _project = new BGWProject(this);

        //open default if it exists
        _project.openDefaultFile();

        //reload the navigator
        loadNavigatorFromProject();
        //set the title
        setFrameTitle();

        //since this is a new reference, reset the project
        //in the viewer
        _viewer.setProject(_project);
        //redraw the viewer
        redrawViewer();
        //System.out.println("New Project not implemented yet...");
    }

    /**
     * Method to open a project - used as a menu hook.
     */
    private void projectOpen() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        System.out.println("Opening Project...");
        _project.openFile();

        setFrameTitle();

        this.setCursor(Cursor.getDefaultCursor());

    }

    private void projectSave() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        System.out.println("Saving Project...");
        _project.saveFile();

        setFrameTitle();

        this.setCursor(Cursor.getDefaultCursor());

    }

    private void projectSaveAs() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        System.out.println("Saving Project As...");
        _project.saveFileAs();

        setFrameTitle();

        this.setCursor(Cursor.getDefaultCursor());

    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Edit Menu Undo/Redo methods section">
    /**
     * Undo the last action in the project.
     */
    private void performUndo() {
        System.out.println("Project undo...");
        _project.undo();
    }

    /**
     * Redo the last undone action in the project.
     */
    private void performRedo() {
        System.out.println("Project redo...");
        _project.redo();

    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="NavigatorListener methods section">
    /**
     * Call to load an attribute set editor into the container panel.
     *
     * @param editor The editor to load in the ui.
     */
    @Override
    public void editorSelected(AttributeSetPanel editor) {

        // _attrScrollPane = new JScrollPane(editor);
        AttributeSetScrollPane scrollPane = new AttributeSetScrollPane(editor);

        if (_lastScrollPane != null) {

            _controlPaneLeft.remove(_lastScrollPane);
            _controlPaneLeft.removeContainerListener(_lastScrollPane);
        }

        _controlPaneLeft.addContainerListener(scrollPane);
        _lastScrollPane = scrollPane;

        //hold and reset position as this operation wants to change it...
        int dLoc = _controlPaneLeft.getDividerLocation();
        //set bottom component
        _controlPaneLeft.setBottomComponent(scrollPane);
        //reset divider location
        _controlPaneLeft.setDividerLocation(dLoc);

        //scrollPane.invalidate();
        _controlPaneLeft.invalidate();
        // _attrScrollPane.validate();
        // System.out.println("Editor Loaded");

    }

//    public void editorSelectedOLD(AttributeSetPanel editor) {
//
//        // _attrScrollPane = new JScrollPane(editor);
//        AttributeSetScrollPane scrollPane = new AttributeSetScrollPane(editor);
//        
//        if (_lastScrollPane != null) {
//            
//            _controlPaneLeft.remove(_lastScrollPane);
//            _controlPaneLeft.removeContainerListener(_lastScrollPane);
//        }
//
//
//        _controlPaneLeft.addContainerListener(scrollPane);
//        _lastScrollPane = scrollPane;
//
//        //hold and reset position as this operation wants to change it...
//        int dLoc = _controlPaneLeft.getDividerLocation();
//        //set bottom component
//        _controlPaneLeft.setBottomComponent(scrollPane);
//        //reset divider location
//        _controlPaneLeft.setDividerLocation(dLoc);
//
//        //scrollPane.invalidate();
//        _controlPaneLeft.invalidate();
//        // _attrScrollPane.validate();
//        // System.out.println("Editor Loaded");
//
//    }
    /**
     * NavigatorListener add a Bicycle to the project.
     */
    @Override
    public void addBicycle() {

        _project.addBicycle("New Bicycle");

    }

    /**
     * NavigatorListener delete a Bicycle from the project.
     *
     * @param bicycle The bicycle to delete from the project.
     */
    @Override
    public void deleteBicycle(Bicycle bicycle) {

        _project.deleteBicycle(bicycle);

    }

    /**
     * NavigatorListener move a bicycle up in the list (draw order).
     *
     * @param bicycle Bicycle to move up.
     */
    @Override
    public void moveBicycleUp(Bicycle bicycle) {

        _project.moveBicycleUp(bicycle);

    }

    /**
     * NavigatorListener move a bicycle down in the list (draw order).
     *
     * @param bicycle The bicycle to move down.
     */
    @Override
    public void moveBicycleDown(Bicycle bicycle) {

        _project.moveBicycleDown(bicycle);

    }

    /**
     * NavigatorListener export component to Library. Export the component to
     * the Library.
     *
     * @param component The component to export to the library.
     */
    @Override
    public void exportComponent(BaseComponent component) {

        _library.exportComponent(component);

    }

    /**
     * NavigatorListener import component from Library. Import a component from
     * the Library.
     *
     * @param component The component to load the values into.
     */
    @Override
    public void importComponent(BaseComponent component) {

        //wrap the import action in an undo group
        //even if the user cancels and no events are generated
        //the undo group will be discarded if empty
        _project.undoOpenGroup();

        _library.importComponent(component);

        _project.undoCloseGroup();

    }

    @Override
    public void bicycleDisplay(BicycleDisplay display) {

        _project.undoOpenGroup();

        _project.setAllDisplay(display);

        _project.undoCloseGroup();

    }

    @Override
    public void bicycleVisibility(boolean visible) {

        _project.undoOpenGroup();

        _project.setAllVisibility(visible);

        _project.undoCloseGroup();

    }

    @Override
    public void bicycleIsolate(Bicycle bicycle) {

        _project.undoOpenGroup();

        _project.bicycleIsolate(bicycle);

        _project.undoCloseGroup();

    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="ProjectListener interface methods">
    /**
     * Repaint the Viewer Panel
     */
    @Override
    public void redrawViewer() {

        _viewer.repaint();

    }

    /**
     * Bicycle has changed.
     */
    @Override
    public void bicycleChanged() {

        //update the title for dirty flag
        setFrameTitle();

        _navigator.refreshBicycleNames();

    }

    /**
     * Update the Report Table with the current bicycle analysis data. Also used
     * to refresh display unit changes.
     */
    @Override
    public void updateReport() {

        ReportValue data[][] = _project.getReportData();
        _reportModel.setDataVector(data, Report.getColumnHeads());

    }

    /**
     * Reload the Navigator Panel after opening a file.
     */
    @Override
    public void projectedLoadedFromDB() {

        loadNavigatorFromProject();

    }

    /**
     * Project event for Bicycle Moved Up.
     *
     * @param bicycle The Bicycle that was moved.
     */
    @Override
    public void bicycleMovedUp(Bicycle bicycle) {

        _navigator.moveBicycleInList(bicycle, true);

    }

    /**
     * Project event for Bicycle moved down
     *
     * @param bicycle The Bicycle that was moved
     */
    @Override
    public void bicycleMovedDown(Bicycle bicycle) {

        _navigator.moveBicycleInList(bicycle, false);

    }

    /**
     * Project event for bicycle deleted.
     *
     * @param bicycle The Bicycle that was Deleted.
     */
    @Override
    public void bicycleDeleted(Bicycle bicycle) {

        _navigator.removeBicycle(bicycle);

    }

    /**
     * Project Event for a new bicycle
     *
     * @param bicycle The bicycle that was added to the project.
     */
    @Override
    public void bicycleAdded(Bicycle bicycle) {

        _navigator.addBicycle(bicycle);

        //update the report
        updateReport();

    }

    @Override
    public void bicycleRestored(Bicycle bicycle, int index) {

        _navigator.addBicycle(bicycle, index);

        //update the report
        updateReport();

    }
//</editor-fold>

    /**
     * UnitsListener Interface Called by UnitsDisplay when the display units are
     * changed. This updates the units menu actions.
     */
    @Override
    public void unitsDisplayChanged() {

        if (UnitsDisplay.usingNaturalUnits()) {
            _actionMMUnits.setEnabled(false);
            _actionMMUnits.setChecked(true);

            _actionINUnits.setEnabled(true);
            _actionINUnits.setChecked(false);

        } else {

            _actionMMUnits.setEnabled(true);
            _actionMMUnits.setChecked(false);

            _actionINUnits.setEnabled(false);
            _actionINUnits.setChecked(true);

        }

    }
    
    /**
     * Show the help file - this points to an html help page.
     */
    private void showHelp() {
        
        char sep = File.separatorChar;
        String ls = System.getProperty("line.separator");
        
        String helpPath = "Help" + sep + "index.html";
        String workingDir = System.getProperty("user.dir");
        
        String fullPath = workingDir + sep + helpPath;
        
        System.out.println("Help file: " + fullPath);
        
        File helpFile = new File(fullPath);
        
        JFrame frame = BicycleGeometryWorkshopUI.getActiveFrame();
        
        if(helpFile.exists()) {
            
            if(!launchBrowser(helpFile.toURI())) {
                
                String message = "There was an error opening the help index." + ls;
                message += "The help file ('index.html') should be located in the 'Help' directory.";
                JOptionPane.showMessageDialog(frame, message);
                
            }
            
            
        } else {
            String message = "The help file could not be found at: " + fullPath + "." + ls;
            message += "The help file ('index.html') should be located in the 'Help' directory.";
            
            JOptionPane.showMessageDialog(frame, message);
            
        }
        
        
        
    }
    
    /**
     * Launch the Browser to the specified URI.
     * @param uri  The URI to show in browser.
     * @return True if he browser was launched successfully, false otherwise.
     */
    private boolean launchBrowser(URI uri) {
        boolean success = false;
        
        try {
            
            if(Desktop.isDesktopSupported()) {
                
                Desktop desktop = Desktop.getDesktop();
                
                if(desktop != null ) {
                    if(desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(uri);
                        success = true;
                    }
                }
            } 
             
        } catch(Exception ex) {
            
            //don't need to do anything here...

        }
        
        
        return success;
    }
    

  

}
