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

import org.bicycleGeometryWorkshop.attributes.AttributeDataType;
import org.bicycleGeometryWorkshop.attributes.AttributeSet;
import org.bicycleGeometryWorkshop.attributes.BaseAttribute;
import org.bicycleGeometryWorkshop.components.Bicycle;
import org.bicycleGeometryWorkshop.components.RiderMeasurements;
import org.bicycleGeometryWorkshop.components.RiderPose;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bicycleGeometryWorkshop.app.BGWProject;
import org.bicycleGeometryWorkshop.app.VisualPreferences;
import org.bicycleGeometryWorkshop.components.BaseComponent;
import org.bicycleGeometryWorkshop.ui.BicycleGeometryWorkshopUI;
import org.sqlite.SQLiteConfig;

/**
 * This is the data base class for the project.  It uses an SQLite database. 
 * This class handles all statement creation and the the IO functions.
 * 
 * @author Tom
 */
public class BGWDataBase {

    private boolean _connOpen;
    private Connection _conn;
    private Statement _sqlCmd;

    public static final String BICYCLE_LIST_TABLE_NAME = "bicycle_list";
    public static final String ID_COL_NAME = "id";
    public static final String OWNER_ID_COL_NAME = "owner_id";
//    public static final String SUBCOMP_ID_COL_NAME = "subcomp_owner_id";
    public static final String ORDER_COL_NAME = "order";
    public static final int NO_OWNER_ID = -1;

    public static final String FILE_EXT = "bgw";
    private static final String FILE_DESC = "Bicycle Geometry Workshop (." + FILE_EXT + ")";

    private String _dbPath;
    private boolean _requestFile;
    private boolean _dbInitialized;

    /**
     * Class constructor.
     */
    public BGWDataBase() {

        _connOpen = false;
        _conn = null;

        _sqlCmd = null;

        _dbPath = "";
        _dbInitialized = false;
        _requestFile = true;

        //testConn();
    }

    /**
     * Get the database file path.
     *
     * @return The database path or an empty string if it has not been saved.
     */
    public String getFilePath() {
        return _dbPath;
    }

    /**
     * Get the connection string
     *
     * @param dbpath file path of the database
     * @return
     */
    private String getConnString(String path) {

        return "jdbc:sqlite:" + path;

    }

    /**
     * Open the connection to the Database.
     *
     * @param path  The path of the database to connect to.
     * @return true if connection opened successfully, false otherwise.
     */
    public boolean openConnection(String path) {

        try {

            Class.forName("org.sqlite.JDBC");

            //build connection string
            String connString = getConnString(path);
            //get a config - future?...
            SQLiteConfig config = new SQLiteConfig();
            //config.enforceForeignKeys(true);

            _conn = DriverManager.getConnection(connString, config.toProperties());
            _sqlCmd = _conn.createStatement();

            _connOpen = true;
            _dbPath = path;

        } catch (Exception e) {

            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            _connOpen = false;
            _dbPath = "";
        }

        return _connOpen;

    }

    /**
     * Close the connection. Closes the Connection and the Statement.
     */
    public void closeConn() {

        try {

            if (_sqlCmd != null) {
                _sqlCmd.close();
            }

            if (_conn != null) {
                _conn.close();
            }

        } catch (SQLException ex) {

            //no action here...
        }//end try/catch

        _connOpen = false;

    }

    /**
     * Get the database file path - dialog file selector. Used for Save and
     * SaveAs file operations.
     *
     * @return Path if file was selected, an empty string otherwise.
     */
    private GetFileInfo getDBFilePath(boolean save) {

        //defaults to no file selected, no path, file doesn't exist
        GetFileInfo fileInfo = new GetFileInfo();

        //setup filter
        FileFilter filter = new FileNameExtensionFilter(FILE_DESC, FILE_EXT);
        JFileChooser fileChooser = new JFileChooser();
        //apply filter
        fileChooser.setFileFilter(filter);

        JFrame frame = BicycleGeometryWorkshopUI.getActiveFrame();
        int returnVal = -1;

        //saving or opening file?
        if (save) {
            returnVal = fileChooser.showSaveDialog(frame);
        } else {
            returnVal = fileChooser.showOpenDialog(frame);
        }

        //check dialog result
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();
            String fname = file.getPath();

            //do extension check
            if (!fname.endsWith("." + FILE_EXT)) {
                fname += "." + FILE_EXT;
            }

            //use name with extension verified for check
            File fileCheck = new File(fname);

            //check for overwrite
            if (fileCheck.exists()) {
                //flag exists
                fileInfo.fileExists = true;

                if (save) {
                    //ask for overwrite....
                    String mssg = "Overwrite file: " + fname + " ?";
                    int result = JOptionPane.showConfirmDialog(frame, mssg, "File Exists", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                    //if overwrite ok, store path and set flag
                    if (result == JOptionPane.OK_OPTION) {
                        //set path and flag as good selection
                        fileInfo.filePath = fname;
                        fileInfo.fileSelected = true;
                    }

                } else {
                    //opening the file
                    fileInfo.filePath = fname;
                    fileInfo.fileSelected = true;
                }

                //file doesn't exist
            } else {
                if (save) {
                    //info is new file
                    fileInfo.filePath = fname;
                    fileInfo.fileSelected = true;
                } else {
                    //nothing to open - file not found!
                    String mssg = "The file: \"" + fname + "\" was not found.";
                    JOptionPane.showMessageDialog(frame, mssg, "File not found", JOptionPane.ERROR_MESSAGE);
                    fileInfo.fileSelected = false;
                }
            }

        }//end if dialog approve 

        return fileInfo;

    }

    /**
     * Simple class for a function return to provide information about selected
     * files
     *
     */
    private class GetFileInfo {

        boolean fileSelected;
        String filePath;
        boolean fileExists;

        /**
         * Constructor - set all false and empty.
         */
        GetFileInfo() {
            fileSelected = false;
            filePath = "";
            fileExists = false;
        }

    }

    
    /**
     * Open a file from from the database.  This will prompt for the database path.
     * @param project  The project to load from the database.
     * @return True if the project was opened, false for cancel.
     */
    public boolean openFile(BGWProject project) {
        
        //String dbPath = getDBOpenFilePath();
        GetFileInfo getFileInfo = getDBFilePath(false);

        //bail if no file selected
        if (!getFileInfo.fileSelected) {
            return false;
        }        
        
        String path = getFileInfo.filePath;
        
        //open the file
        return openFile(project, path);
        
    }
    /**
     * Open a file from an SQLite DB..
     *
     * 
     * @param project The project to open from the database.
     * @param path PAth of the database to open.
     * @return True if File was successfully loaded.
     */
    public boolean openFile(BGWProject project, String path) {

        RiderMeasurements rSize = project.getRiderSize();
        RiderPose rPose = project.getRiderPose();
        ArrayList<Bicycle> bicycles = project.getBicycles();
        
        
        AttributeSet sizeSet = rSize.getAttributeSet();
        AttributeSet poseSet = rPose.getAttributeSet();
        
        VisualPreferences prefs = project.getVisualPreferences();
        AttributeSet prefSet = prefs.getAttributeSet();

//        //String dbPath = getDBOpenFilePath();
//        GetFileInfo getFileInfo = getDBFilePath(false);
//
//        //bail if no file selected
//        if (!getFileInfo.fileSelected) {
//            return false;
//        }

        //flagfor success
        boolean fileloaded = false;
        //set these up....
        _dbPath = path; ///getFileInfo.filePath;
        _dbInitialized = true;
        _requestFile = false;

        if (openConnection(_dbPath)) {

            System.out.println("Opening File: " + _dbPath);

            try {

                _sqlCmd.execute("BEGIN");

                //clear bicycles
                bicycles.clear();

                //load rider size
                loadAttributeSetFromOwner(sizeSet, NO_OWNER_ID);
                //load pose
                loadAttributeSetFromOwner(poseSet, NO_OWNER_ID);
                //load preferences
                loadAttributeSetFromOwner(prefSet, NO_OWNER_ID);

                ArrayList<Integer> idList = getBicycles();

                for (int i : idList) {
                    System.out.println("Bicycle id: " + i);

                    Bicycle b = new Bicycle("default", rSize, rPose, prefs);
                    bicycles.add(b);

//                    loadBicycle(b, i);
                    loadBicycleNew(b, i);

                }

                _sqlCmd.execute("END");

                //close the connection
                closeConn();
                
                fileloaded = true;

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
            }

        } else {

            System.out.println("Conn not open, could not open file");
        }
        
        return fileloaded;

    }

    /**
     * Resets the open flags so the database assumes it is a blank file.
     * This is used after opening the default file.
     */
    public void resetOpenFlags() {
        
        _dbPath = "";
        _dbInitialized = true;
        _requestFile = true;        
        
    }
    
    
    /**
     * Get the bicycle master list of the current database.
     *
     * @return List of all the bicycle ids.
     */
    private ArrayList<Integer> getBicycles() throws SQLException {

        ArrayList<Integer> idList = new ArrayList();
//        String selCmd = bicycleListTableSelectCmd();

        String selCmd = "SELECT * FROM " + BICYCLE_LIST_TABLE_NAME;

        if (_connOpen) {

            ResultSet tlResult = _sqlCmd.executeQuery(selCmd);

            while (tlResult.next()) {

                int id = tlResult.getInt(ID_COL_NAME);
                idList.add(id);
            }

        } else {

            System.out.println("Conn not open to select bicycles in master");
        }

        return idList;
    }


    /**
     * Load a bicycle from the database.
     *
     * @param bicycle The bicycle to load with the database values.
     * @param owner The owner id.
     * @throws SQLException
     */
    private void loadBicycleNew(Bicycle bicycle, int owner) throws SQLException {

        //load the bicycle seperately as it is not included in the 
        //component list
        AttributeSet bAttSet = bicycle.getAttributeSet();
        loadAttributeSetFromOwner(bAttSet, owner);

        //load all the bicycle componenets
        ArrayList<BaseComponent> comps = bicycle.getComponentList();

        for (BaseComponent comp : comps) {

            AttributeSet attSet = comp.getAttributeSet();
            loadAttributeSetFromOwner(attSet, owner);
            
           //do for any sub components as well
            if(comp.hasSubComponents()) {
                ArrayList<BaseComponent> subComps = comp.getSubComponents();
                for(BaseComponent subComp : subComps) {
                    
                    AttributeSet scAttSet = subComp.getAttributeSet();
                    loadAttributeSetFromOwner(scAttSet, owner);                    

                }
 
            }//end if sub comps             
            
            

        }//end for comp

    }

    /**
     * Library method to remove a bicycle. This removes the main owner entry
     * (bicycles list) and all the associated components.
     *
     * @param bicycle The bicycle to delete - used for table reference.
     * @param id Id of the owner table entry (bicycles list - this id appears as
     * owner_id in components).
     */
    public void libraryDeleteBicycle(Bicycle bicycle, int id) {

        if (_connOpen) {

            try {

                _sqlCmd.execute("BEGIN");

                //remove owner table entry - remember no cascading events are defined
                // this all has to be done manually.
                deleteBicycleListTableEntry(id);

                AttributeSet bAttSet = bicycle.getAttributeSet();
                String bicycleTableName = bAttSet.getName();
                deleteBicycleComponentTableEntry(bicycleTableName, id);

                ArrayList<BaseComponent> comps = bicycle.getComponentList();// attDb = bicycle.getAttributeDB();

                for (BaseComponent comp : comps) {

                    AttributeSet attSet = comp.getAttributeSet();
                    String tableName = bAttSet.getName();
                    deleteBicycleComponentTableEntry(tableName, id);
                    
                    //do for any sub components as well
                    if(comp.hasSubComponents()) {
                        ArrayList<BaseComponent> subComps = comp.getSubComponents();
                        for(BaseComponent subComp : subComps) {

                            AttributeSet scAttSet = subComp.getAttributeSet();
                            String scTableName = scAttSet.getName();
                            deleteBicycleComponentTableEntry(scTableName, id);                    

                        }

                    }//end if sub comps                      
                    
                    

                }

                _sqlCmd.execute("END");

                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
            }

        } else {
            //todo...

        }

    }

    /**
     * Library method to Load a Bicycle.
     *
     * @param bicycle The bicycle to Load
     * @param id The id of the Bicycle to import.
     */
    public void libraryImportBicycle(Bicycle bicycle, int id) {

        if (_connOpen) {

            try {
                
                _sqlCmd.execute("BEGIN");
                
                loadBicycleNew(bicycle, id);
                
                _sqlCmd.execute("END");
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
            }

        } else {
            //todo...

        }

    }

    /**
     * Library method to add a Bicycle.
     *
     * @param bicycle  The Bicycle to write to the database.
     */
    public void libraryInsertBicycle(Bicycle bicycle) {

        if (_connOpen) {

            try {

                _sqlCmd.execute("BEGIN");

                insertBicycleNew(0, bicycle);

                _sqlCmd.execute("END");

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
            }

        } else {

        }

    }

    /**
     * Load an AttributeSet from the library database.
     * @param attSet  The AttributeSet to load.
     * @param id The id of the record.
     */
    public void libraryLoadAttributeSet(AttributeSet attSet, int id) {

        if (_connOpen) {
            
            String tblName = attSet.getName();
            String selCmd = "SELECT * FROM " + tblName;
            selCmd += " WHERE " + ID_COL_NAME + " = " + id + ";";

            try {

                _sqlCmd.execute("BEGIN");

                loadAttributeSet(attSet, selCmd);

                _sqlCmd.execute("END");

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
            }

        } else {

        }

    }

    /**
     * Load an Attribute Set from the database. Uses the ownerID of the Bicycle
     * that the component belongs to. Use the NO_OWNER_ID for list of library
     * parts.
     *
     * @param attSet AttributeSet to load from the database.
     * @param ownerId Id of the owner or NO_OWNER_ID for library items.
     */
    private void loadAttributeSetFromOwner(AttributeSet attSet, int ownerId) throws SQLException {

        String tblName = attSet.getName();
        String selCmd = "SELECT * FROM " + tblName;
        selCmd += " WHERE " + OWNER_ID_COL_NAME + " = " + ownerId + ";";

        loadAttributeSet(attSet, selCmd);

    }

    /**
     * Load an Attribute Set.  This sets the values of the attributes in
     * the AttributeSet from the database.
     *
     * @param attSet AttributeSet to load
     * @param cmd SQL Statement to execute.
     */
    private void loadAttributeSet(AttributeSet attSet, String cmd) throws SQLException {

        if (_connOpen) {

            ResultSet rs = _sqlCmd.executeQuery(cmd);
            //move first
            rs.next();

            //tlResult.
            ArrayList<BaseAttribute> attList = attSet.getAttributes();

            for (BaseAttribute att : attList) {

                String colName = att.getName();
                AttributeDataType sqlType = att.getSQLType();

                Object valueObj = rs.getObject(colName);
                att.setFromObject(valueObj);

            }//end for

        } else {

            System.out.println("Conn not open to select bicycles in master");
        }

    }

    /**
     * Save the current file to a new target file.
     *
     * 
     * @param project  The project to save.
     */
    public void saveFileAs(BGWProject project) {

        GetFileInfo getFileInfo = getDBFilePath(true);
        //Was a file selected?
        if (!getFileInfo.fileSelected) {
            return;
        }

        _requestFile = false;
        //set the path
        _dbPath = getFileInfo.filePath;

        _dbInitialized = false;
        //save the file to the current target
//        writeFile(project);
        
        boolean writeOk = writeFile(project);

    }

    /**
     * Save the file.
     *
     * 
     * @param project The project to save.
     * @return True if the write was carried out, false if canceled or write error.
     */
    public boolean saveFile(BGWProject project) {

        //see if the db is intialized (saved before)
        if (_requestFile) {

            GetFileInfo getFileInfo = getDBFilePath(true);
            //Was a file selected?
            if (!getFileInfo.fileSelected) {
                return false; //canceled
            } else {
                //set the db path
                _dbPath = getFileInfo.filePath;
                _requestFile = false;
            }

        }

        //write the file
        boolean writeOk = writeFile(project);
        
        return writeOk;

    }

    /**
     * Write the current file to the DataBase.
     *
     * 
     * @param project The project to save.
     * @return True if the write was successful, false if it failed.
     */
    public boolean writeFile(BGWProject project) {

        boolean writeSuccess = false;

        //open the connection
        if (openConnection(_dbPath)) {

            try {
                
        RiderMeasurements rSize = project.getRiderSize();
        RiderPose rPose = project.getRiderPose();
        VisualPreferences prefs = project.getVisualPreferences();
        
        ArrayList<Bicycle> bicycles = project.getBicycles();                
                

                //add the rider and pose
                AttributeSet sizeSet = rSize.getAttributeSet();
                AttributeSet poseSet = rPose.getAttributeSet();
                
                //preferences
                AttributeSet prefSet = prefs.getAttributeSet();

                _sqlCmd.execute("BEGIN");

                //create table def for rider size
                prepareTable(sizeSet);

                //create table def for rider pose
                prepareTable(poseSet);
                
                //create table def for preferences
                prepareTable(prefSet);                

                //bicycle association list table
                prepareBicycleListTable();

                //get single bicycle as template
                Bicycle bTmplt = bicycles.get(0);
                //write tables
//                prepareBicycleTables(bTmplt.getAttributeDB());
                prepareBicycleTablesNew(bTmplt);

                //flag initialized
                _dbInitialized = true;

                //insert rider - no owner
                insertAttributeSet(sizeSet,NO_OWNER_ID, NO_OWNER_ID);

                //insert pose - no owner
                insertAttributeSet(poseSet,NO_OWNER_ID, NO_OWNER_ID);
                
                //insert prefs - no owner
                 insertAttributeSet(prefSet,NO_OWNER_ID, NO_OWNER_ID);

                int bl = bicycles.size();
                for (int i = 0; i < bl; i++) {
                    Bicycle b = bicycles.get(i);
                    insertBicycleNew(i, b);

                }

                _sqlCmd.execute("END");

                //close the connection
                closeConn();

                writeSuccess = true;
                
            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);

            }

        } else {

            //failed to open....
            System.out.println("Failed to open db " + _dbPath);
        }

        return writeSuccess;

    }


    /**
     * Write the Bicycle list table.
     */
    private void prepareBicycleListTable() {

        if (_connOpen) {

            try {

                String createLookup = "CREATE TABLE IF NOT EXISTS " + BICYCLE_LIST_TABLE_NAME;
                createLookup += " (id INTEGER PRIMARY KEY AUTOINCREMENT, '" + ORDER_COL_NAME + "' INTEGER);";

                _sqlCmd.execute(createLookup);

                String clearTable = "DELETE FROM " + BICYCLE_LIST_TABLE_NAME + ";";
                _sqlCmd.execute(clearTable);

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);

            }

        }//end if

    }

    /**
     * Delete a Bicycle table entry (bicycle list entry)
     *
     * @param id The id of the Bicycle List entry.
     */
    private void deleteBicycleListTableEntry(int id) {

        if (_connOpen) {

            try {

                String cmd = "DELETE FROM " + BICYCLE_LIST_TABLE_NAME + " WHERE " + ID_COL_NAME + " = " + id + ";";
                _sqlCmd.execute(cmd);

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);

            }

        }//end if

    }

    /**
     * Delete a table entry that is associated with a Bicycle. This is called
     * when a bicycle is removed to remove a component entry.
     *
     * @param tableName The name of the table to delete the record from.
     * @param ownerId The owner id of the record - this is the id from the
     * bicycle list table.
     */
    private void deleteBicycleComponentTableEntry(String tableName, int ownerId) {

        if (_connOpen) {

            try {

                String cmd = "DELETE FROM " + tableName + " WHERE " + OWNER_ID_COL_NAME + " = " + ownerId + ";";
                _sqlCmd.execute(cmd);

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);

            }

        }//end if

    }


    /**
     * Prepare the Bicycle tables. This creates the table if it doesn't exist,
     * and/or deletes all the records from the table.
     *
     * @param bicycle The bicycle to use as a template for the table names.
     * @throws SQLException Exception thrown on database error.
     */
    public void prepareBicycleTablesNew(Bicycle bicycle) throws SQLException {

        AttributeSet bAttSet = bicycle.getAttributeSet();
        prepareTable(bAttSet);

        //load all the bicycle componenets
        ArrayList<BaseComponent> comps = bicycle.getComponentList();// attDb = bicycle.getAttributeDB();

        for (BaseComponent comp : comps) {

            AttributeSet attSet = comp.getAttributeSet();
            prepareTable(attSet);
            
            //do for any sub components as well
            if(comp.hasSubComponents()) {
                ArrayList<BaseComponent> subComps = comp.getSubComponents();
                for(BaseComponent subComp : subComps) {
                    
                    AttributeSet scAttSet = subComp.getAttributeSet();
                    prepareTable(scAttSet);                    
                    
                    
                }
                
                
            }
            
            

        }

    }

    /**
     * Initialize a table (create it) or delete all entries from it if it
     * already exists.
     *
     * @param attSet  The attribute set to prepare tables for.
     * @throws SQLException Exception thrown on database error.
     */
    private void prepareTable(AttributeSet attSet) throws SQLException {

        if (_connOpen) {

            //create the table if it doesn't exist
            String createTable = getSQLTableCreate(attSet);
            _sqlCmd.execute(createTable);

            //if it exists, this will clear it
            String clearTable = "DELETE FROM " + attSet.getName();
            _sqlCmd.execute(clearTable);

        } else {

            System.out.println("Conn not open to init or clear table definition");
        }

    }


    /**
     * Insert a Bicycle into the DataBase.
     *
     * @param order The order of the bicycle (display order - order in bicycle
     * list and navigator).
     * @param bicycle The bicycle to save.
     * @throws SQLException Exception thrown on database error.
     */
    private void insertBicycleNew(int order, Bicycle bicycle) throws SQLException {

        int ownerId = -1;

        ownerId = insertBicycleListEntry(order);

        AttributeSet bAttSet = bicycle.getAttributeSet();
        insertAttributeSet(bAttSet, ownerId, NO_OWNER_ID);

        //load all the bicycle componenets
        ArrayList<BaseComponent> comps = bicycle.getComponentList();// attDb = bicycle.getAttributeDB();

        for (BaseComponent comp : comps) {

            AttributeSet attSet = comp.getAttributeSet();
            int ca_id = insertAttributeSet(attSet, ownerId, NO_OWNER_ID);
            
            //do for any sub components as well
            if(comp.hasSubComponents()) {
                ArrayList<BaseComponent> subComps = comp.getSubComponents();
                for(BaseComponent subComp : subComps) {
                    
                    AttributeSet scAttSet = subComp.getAttributeSet();
                    insertAttributeSet(scAttSet, ownerId, ca_id);                    

                }
 
            }//end if sub comps          

        }//end for comps

    }

    /**
     * Inserts an Entry into the Bicycle list table (the master list of
     * bicycles).
     *
     * @param order The list order of the bicycle.
     * @return The id of the records for all the bicycle's sub tables.
     * @throws SQLException Exception thrown on database error.
     */
    private int insertBicycleListEntry(int order) throws SQLException {

        int result = -1;

        if (_connOpen) {

            String insCmd = "INSERT INTO " + BICYCLE_LIST_TABLE_NAME;
            insCmd += " ('order') VALUES (" + order + ");";

            _sqlCmd.execute(insCmd);

            //get the generated key
            ResultSet insResult = _sqlCmd.getGeneratedKeys();
            insResult.next();
            //id to return
            result = insResult.getInt(1);

        } else {

            System.out.println("Conn not open to insert AttributeSet");
        }

        return result;

    }

    /**
     * Insert an AttributeSet into the database. Assumes the tables are created.
     *
     * @param attSet The AttributeSet to insert into database.
     * @param ownerId The owner id of the record (or -1 for no owner)
     * @param subCompOwnerId The owner id of the parent record when this is a sub-component record (or -1 for no owner)
     * @throws SQLException Exception thrown on database error.
     */
    private int insertAttributeSet(AttributeSet attSet, int ownerId, int subCompOwnerId) throws SQLException {

        int result = -1;
        if (_connOpen) {

            String insCmd = getSQLTableInsert(attSet, ownerId);

            _sqlCmd.execute(insCmd);
            
            //get the generated key
            ResultSet insResult = _sqlCmd.getGeneratedKeys();
            insResult.next();
            //id to return
            result = insResult.getInt(1);            
            

        } else {

            System.out.println("Conn not open to insert AttributeSet");
        }
        
        return result;

    }


    /**
     * Get the table create string for this attribute set.
     *
     *
     * @param attSet The attribute set to create
     *
     * @return The table create string for this AttributeSet
     */
    private String getSQLTableCreate(AttributeSet attSet) {

        String cmd = "CREATE TABLE IF NOT EXISTS " + attSet.getName() + "( ";

        cmd += "id INTEGER PRIMARY KEY AUTOINCREMENT, ";

        //owner id column
        cmd += OWNER_ID_COL_NAME + " INTEGER NOT NULL, ";
        

        ArrayList<BaseAttribute> attributes = attSet.getAttributes();

        int al = attributes.size();
        for (int i = 0; i < al; i++) {

            BaseAttribute att = attributes.get(i);

            AttributeDataType sqlType = att.getSQLType();
            String attName = att.getName();

            //quote the name
            attName = "'" + attName + "'";
            cmd += attName + " " + sqlType.name() + " NOT NULL";

            if (i < al - 1) {
                cmd += ", ";
            }

        }

        cmd += " );";
        return cmd;

    }

    /**
     * Get the insert command for this attribute set.
     *
     * @param attSet The AttributeSet to generate insert for.
     * @param ownerId The ownerID of the record - this will be the bicycle id or -1.
     * 
     *
     * @return The sql statement to insert into the table.
     */
    private String getSQLTableInsert(AttributeSet attSet, int ownerId) {

        String cmd = "INSERT INTO " + attSet.getName();

        //set the owner first
        String columnString = OWNER_ID_COL_NAME + ", ";
        String valueString = ownerId + ", ";

        
        ArrayList<BaseAttribute> attributes = attSet.getAttributes();

        int al = attributes.size();
        for (int i = 0; i < al; i++) {

            BaseAttribute att = attributes.get(i);

            AttributeDataType sqlType = att.getSQLType();
            String attName = att.getName();
            String attValue = att.getSQLInsert();

            //quote the name
            attName = "'" + attName + "'";

            //quote raw text
            if (sqlType == AttributeDataType.TEXT) {
                attValue = "'" + attValue + "'";
            }

            columnString += attName;
            valueString += attValue;

            if (i < al - 1) {

                columnString += ", ";
                valueString += ", ";
            }

        }

        cmd = cmd + " ( " + columnString + ") VALUES (" + valueString + " );";

        return cmd;

    }

    /**
     * Simple open as Library - opens the db provided in the path.
     *
     * @param path  The path to the library database.
     * @return  True if the library is opened, false if it fails to open.
     */
    public boolean openAsLibrary(String path) {

        _dbPath = path;
        _dbInitialized = true;
        _requestFile = false;

        return openConnection(_dbPath);

    }

    /**
     * Library method to save a component from it's attribute set.
     *
     * @param component The component to save.
     */
    public void librarySaveComponent(BaseComponent component) {

        if (_connOpen) {
            try {

                _sqlCmd.execute("BEGIN");
                
                
                AttributeSet attSet = component.getAttributeSet();
                int compId = insertAttributeSet(attSet, NO_OWNER_ID, NO_OWNER_ID);

                _sqlCmd.execute("END");

            } catch (SQLException ex) {

                //do?...
            }
        } else {
            
            String mssg = "Error: No connection to the LIbrary File.  Check the Library file and restart.";
            JOptionPane.showMessageDialog(null, mssg);            

        }

    }

    /**
     * Remove a Library Component
     *
     * @param tableName Table to remove component from.
     * @param id The id of the record to remove.
     */
    public void libraryRemoveComponent(String tableName, int id) {

        if (_connOpen) {

            try {

                _sqlCmd.execute("BEGIN");

                String cmd = "DELETE FROM '" + tableName + "' WHERE " + ID_COL_NAME + " = " + id + ";";
                _sqlCmd.execute(cmd);

                _sqlCmd.execute("END");

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);

            }

        } else {

            String mssg = "Error: No connection to the Library File.  Check the Library file and restart.";
            JOptionPane.showMessageDialog(null, mssg);              
            //System.out.println("Conn not open to insert AttributeSet");
        }

    }

    /**
     * Get the list of Component records for a component in the library.
     * This requires tweak for bicycles vs components.  Bicycle tables (not bicycle list table)
     * will always have an owner id of other than -1.  Components that have been exported
     * will always have an owner id of -1 (no owner id). 
     *
     * @param tableName  The name of the table to get the list from.
     * @param queryOwnerId  The ownerID.  If this is no owner id (-1), it will filter components that do not belong to a bicycle.
     * @return The list of component records for the table.
     */
    public ArrayList<ComponentRecord> getComponentLibraryList(String tableName, int queryOwnerId) {

        ArrayList<ComponentRecord> compList = new ArrayList();
        
        //prepare condition
        String operand = "";
        if(queryOwnerId == NO_OWNER_ID) {
            operand = " == " ; //for components - filters bicycle components
        } else {
            operand = " <> "; //for bicycles
        }
        
        //build query
        String nameCol = AttributeSet.NAME_ATT_ID;
        String cols = ID_COL_NAME + " , " + OWNER_ID_COL_NAME + " , " + nameCol;
        String selCmd = "SELECT " + cols + " FROM " + tableName + " WHERE " + OWNER_ID_COL_NAME + operand + queryOwnerId + ";";

     

        if (_connOpen) {

            try {

                ResultSet tlResult = _sqlCmd.executeQuery(selCmd);

                while (tlResult.next()) {

                    int id = tlResult.getInt(ID_COL_NAME);
                    int ownerId = tlResult.getInt(OWNER_ID_COL_NAME);
                    String name = tlResult.getString(nameCol);
                    ComponentRecord cr = new ComponentRecord(tableName, id, ownerId, name);
                    compList.add(cr);
                }

            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);

            }

        } else {

            String mssg = "Error: No connection to the Library File.  Check the Library file and restart.";
            JOptionPane.showMessageDialog(null, mssg);             
//            System.out.println("Conn not open to select bicycles in master");
        }

        return compList;
    }

}
