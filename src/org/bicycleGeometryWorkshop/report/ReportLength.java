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

/**
 * This class represents a length in the report and it's display honors the display units class settings (in/mm).  
 * This stores a length (double that is always treated as millimeters) to display in the report.
 * @author Tom
 */
public class ReportLength extends ReportValue{
    private double _length;
    
    /**
     * Class constructor.  
     * @param length The length to display in the report.
     */
    public ReportLength(double length) {
        super();
        _length = length;
    }
    
    /**
     * The string representation of the length.  This also applies the suffix to the length.
     * @return The length sting representation with the proper suffix.
     */
    @Override
    public String toString() {
        return Utilities.formatLengthDisplayUnits(_length);
    }    
}
