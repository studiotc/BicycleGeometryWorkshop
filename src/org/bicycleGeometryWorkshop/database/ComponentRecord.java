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

/**
 *
 * @author Tom
 */
public class ComponentRecord {
    
    private String _tableName;
    private int _id;
    private int _ownerId;
    private String _name;
    
    /**
     * Initialize a ComponentRecord
     * @param tableName The table name.
     * @param id  The record id.
     * @param ownerId  owner id of the record.  This will be the bicycle id if the component is a member of a bicycle.
     * @param name The name of the component.
     */
    public ComponentRecord(String tableName, int id, int ownerId, String name) {
        
        _tableName = tableName;
        _id = id;
        _ownerId = ownerId;
        _name = name;
        
    }
    
    /**
     * Get the table name of the record.
     * @return The table name.
     */
    public String getTableName() {
        return _tableName;
    }
    
    /**
     * Get the id of the record.
     * @return The id of the record.
     */
    public int getId() {
        return _id;
    }
    
    /**
     * Get the owner id of the record.
     * @return The owner id of the record.
     */
    public int getOwnerId() {
        return _ownerId;
    }    
    
    /**
     * Get the name field of the record.
     * @return The name of the component.
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Get the SQL select string for this record.  This generates the SQL to select the record.
     * @return The SQL select command string.
     */
    public String getSQLSelect() {
        return "SELECT * FROM '" + _tableName + "' WHERE 'id'=" + _id + ";";
    }
    
    /**
     * Get the path of the record: table : id : name.  
     * Used for testing.
     * @return The path string: table:id:name.
     */
    public String getPath() {
        return _tableName + ":" + _id + ": "+ _name;
    }
    
    /**
     * The name of the record.
     * @return The string (the name of the record).
     */
    @Override
    public String toString() {
        return _name;
    }
    
    
}
