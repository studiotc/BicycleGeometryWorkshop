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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.bicycleGeometryWorkshop.attributes.AttributeSet;
import org.bicycleGeometryWorkshop.components.BaseComponent;
import org.bicycleGeometryWorkshop.components.Bicycle;
import org.bicycleGeometryWorkshop.ui.BicycleGeometryWorkshopUI;

/**
 *  This class handles all the Library database calls.  The library is used for 
 * importing or exporting common components in projects.  This holds an instance
 * of the Database class and points it at the local library file: './Library/library.bgw'.
 * @author Tom
 */
public class BGWLibrary {
    
    private static final String LIBRARY_FILE = "library";
    
    private String _workingDir;
    private BGWDataBase _db;
    
    private HashMap<String, ArrayList<ComponentRecord>> _compLib;
    

    /**
     * Class constructor.
     */
    public BGWLibrary() {
        
        _workingDir = System.getProperty("user.dir");
        System.out.println("Working Directory = " + _workingDir);
        
        _compLib = new HashMap();
        
        _db = new BGWDataBase();
        

        
    }
    
    /**
     * Get the library path.  This should point to a subdirectory named 'Library' with the library file being named 'library.bgw'.
     * @return The library file path.
     */
    public String getLibraryPath() {
        
        String ps = File.separator;
        String path = _workingDir + ps + "Library" + ps + LIBRARY_FILE + "." + BGWDataBase.FILE_EXT;        
        return path;
    }
    
    
    /**
     * Open the Library
     *  
     */
    public void openLibrary() {
        
 
        String libName = getLibraryPath();
        
        System.out.println("LibraryFile: " + libName);
        File libFile = new File(libName);
        
        if(libFile.exists()) {
            
            
            boolean opened = _db.openAsLibrary(libName);
            
            if(opened) {
                //System.out.println("LibraryFile: " + libName + " opened.");
            }
            
        } else {
            
            String nl = System.lineSeparator();
            String mssg = "Error: the Library file: " + nl + "'" + libName + "'" + nl + "Could not be located.  Please check Library file location or create a new one and restart.";
            JOptionPane.showMessageDialog(null, mssg);
            //System.out.println("*Error* LibraryFile: " + libName + " does not exist.");
            
        }
        
    }
    
    
    
    /**
     * Close the Library.  
     */
    public void closeLibrary() {
        
        _db.closeConn();
        
    }
    
    
    /**
     * Export a component to the library.
     * @param component  The component to export
     */
    public void exportComponent(BaseComponent component) {
        
      boolean isBicycle = false;
       
       if(component instanceof Bicycle) {
           isBicycle = true;
        }        
        
        
        AttributeSet attSet = component.getAttributeSet();
        
        //System.out.println(">>Exporting to Library: " + attSet.getName());
        if(!isBicycle) {
            
            _db.librarySaveComponent(component);
            
        } else {
            
            Bicycle b = (Bicycle)component;
            _db.libraryInsertBicycle( b);
        }
        
        
    }
    
    
    
    /**
     * Import a component from the library.  This detects bicycles
     * and handles them as a special case.
     * @param component The component to import from the database.
     */
   public void importComponent(BaseComponent component) {
        
       boolean isBicycle = false;
       int queryOwnerId = BGWDataBase.NO_OWNER_ID;
       
       if(component instanceof Bicycle) {
           isBicycle = true;
           queryOwnerId = 0;
        }
       
        AttributeSet attSet = component.getAttributeSet();
        String tableName = attSet.getName();
        
       // System.out.println("<<Importing from Library: " + tableName);
        
        ArrayList<ComponentRecord> comps = _db.getComponentLibraryList( tableName, queryOwnerId);
        
        JFrame frame = BicycleGeometryWorkshopUI.getActiveFrame();
        DBImportDialog importDialog = new DBImportDialog(frame);        
        //loadthe list
        importDialog.loadList(comps);
        //show the dialog
        boolean dialogOk = importDialog.showDialog(tableName);
        
        //check flag for removal of library items
        if(importDialog.getRemoveOnClose()) {
            
            //get the list of records to delete
            ArrayList<ComponentRecord> delComps = importDialog.getDeleteList();            
            
            for(ComponentRecord cr : delComps) {
                
                String tName = cr.getTableName();
                
                if(isBicycle) {
                    int ownerId = cr.getOwnerId();
                    Bicycle bicycle = (Bicycle)component;
                    //remove complete bicycle and components
                    _db.libraryDeleteBicycle(bicycle, ownerId);
                    
                } else {
                    int id = cr.getId();
                   _db.libraryRemoveComponent(tName, id); 
                }
                
                
            }
            
        }
        
        //check for import
        if(dialogOk) {
            
             ComponentRecord cr = importDialog.getImportComponent();
             if(cr != null) {
                 
                 
                 if(isBicycle) {
                     
                     int ownerId = cr.getOwnerId();
                     Bicycle bicycle = (Bicycle)component;
                     _db.libraryImportBicycle(bicycle, ownerId);
                     
                 } else {
                     int id = cr.getId();
                    _db.libraryLoadAttributeSet(attSet, id); 
                 }
                 
                 
                 
             }
            
            
            
        }

        //release dialog
        importDialog.dispose();
        
    }    
    
    
    
}
