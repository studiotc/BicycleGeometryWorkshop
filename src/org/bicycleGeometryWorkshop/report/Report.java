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
package org.bicycleGeometryWorkshop.report;

import org.bicycleGeometryWorkshop.geometry.Utilities;

import java.util.HashMap;

/**
 *  The report class manages all the data to display in the UI JTable.  
 *  The report is packed from the components (bicycles) and presented when called by the UI.
 * @author Tom
 */
public class Report {

    private HashMap<ReportField, ReportValue> _report;

    private String _name;

    /**
     * Class constructor.
     */
    public Report() {

        _name = "<report>";
        _report = new HashMap();

        init();

    }

    /**
     * Set the name of the report - this is the Bicycle Name.
     * @param name The Name of the Bicycle
     */
    public void setName(String name) {
        _name = name;
        _report.put(ReportField.Name, new ReportValue(name));
    }

    /**
     * Initialize the report - make sure all fields are represented with a default.
     * 
     */
    private void init() {

        ReportField[] fields = ReportField.values();

        for (ReportField f : fields) {

            _report.put(f, new ReportValue());

        }

    }

    /**
     * Get a data row from the report.  Use the column heads as a guide
     * to ensure the order of elements in the array.
     * @param colHeads The Column heads - used for ordering the data.
     * @return An array of ReportValues.
     */
    public ReportValue[] getDataRow(ReportValue[] colHeads) {

        int fSize = colHeads.length;

        ReportValue[] data = new ReportValue[fSize];

        for (int i = 0; i < fSize; i++) {

            //get the string column name
            String curField = colHeads[i].toString();

            try {
                //convert to field
                ReportField f = ReportField.valueOf(curField);
                if (_report.containsKey(f)) {
                    ReportValue report = _report.get(f);
                    data[i] = report;
                }

            } catch (Exception ex) {

                data[i] = new ReportValue("*ERR*");

            }


        }

        return data;

    }

    /**
     * Add an angle to the report.
     *
     * @param field The field this angle belongs to.
     * @param theta The angle as raw radians (converted and formatted to degrees
     * internally).
     */
    public void reportAngle(ReportField field, double theta) {

        double angle = Utilities.radiansToDegrees(theta);
        _report.put(field, new ReportAngle(angle));

    }

    /**
     * Add a distance to the report
     *
     * @param field The field this distance belongs to.
     * @param dist The distance (formated for distance internally).
     */
    public void reportDistance(ReportField field, double dist) {

        _report.put(field, new ReportLength(dist));

    }

    /**
     * Get an array of all the fields to be used 
     * as column heads for a JTable.
     *
     * @return  String array of all the column heads.
     */
    public static ReportValue[] getColumnHeads() {

        ReportField[] fields = ReportField.values();

        int fSize = fields.length;

        ReportValue[] heads = new ReportValue[fSize];

        for (int i = 0; i < fSize; i++) {
            ReportField f = fields[i];
            heads[i] = new ReportValue(f.name());

        }

        return heads;

    }
    
    /**
     * Get an empty data row to be used when constructing a JTable.
     * @return String array of empty data.
     */
    public static ReportValue[] getEmtpyDataRow() {

        int fSize =  ReportField.values().length;

        ReportValue[] data = new ReportValue[fSize];

        for (int i = 0; i < fSize; i++) {
            data[i] = new ReportValue();
        }

        return data;

    }    
    
    

}
