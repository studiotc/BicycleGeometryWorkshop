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
package org.bicycleGeometryWorkshop.app.undo;

import org.bicycleGeometryWorkshop.app.BGWProject;
import org.bicycleGeometryWorkshop.components.Bicycle;

/**
 *  Undo class for a delete Bicycle event.  This undo is generated when a bicycle is deleted from the project.
 * @author Tom
 */
public class BicycleDeleteUndo extends BaseUndo {
    
    private BGWProject _project;
    private Bicycle _bicycle;
    private int _position;
    
    /**
     * Class Constructor.  This takes a reference to the project and to the bicycle that was deleted.
     * @param project  The project the bicycle was deleted from (this is the current project).
     * @param bicycle   The bicycle that was deleted from the project.
     * @param position The position in the list (for display order).
     */    
    public BicycleDeleteUndo(BGWProject project, Bicycle bicycle, int position) {
        _project = project;
        _bicycle = bicycle;
        _position = position;
        
        
    }
    
    /**
     * Undo the delete.  This restores the bicycle to the project.
     */
    @Override
    public void undo() {
        
        //add the bicycle back
        _project.restoreBicycle(_bicycle, _position);

    }

    /**
     * Redo the delete.  This deletes the bicycle from the project.
     */
    @Override
    public void redo() {
        
       _project.deleteBicycle(_bicycle);

    }    
    
    
}
