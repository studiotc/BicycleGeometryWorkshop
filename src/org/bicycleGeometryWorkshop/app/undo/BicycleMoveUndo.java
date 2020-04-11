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
 *
 * @author Tom
 */
public class BicycleMoveUndo extends BaseUndo {
    
    private BGWProject _project;
    private Bicycle _bicycle;
    private boolean _up;

    /**
     * Undo a Bicycle MoveUp or MoveDown.  MoveUp action is set by the flag up, MoveDown event if up = false;
     * @param project  The project.
     * @param bicycle The bicycle that was moved up or down.
     * @param up Flag for action to undo: true = MoveUp, false = moveDown
     */
    public BicycleMoveUndo(BGWProject project, Bicycle bicycle, boolean up) {
        _project = project;
        _bicycle = bicycle;
       
        _up = up;
        
    }
    
    @Override
    public void undo() {
        
        if(_up) {
            _project.moveBicycleDown(_bicycle);
        } else {
           _project.moveBicycleUp(_bicycle);
        }
        

    }

    @Override
    public void redo() {
        
        if(_up) {
            _project.moveBicycleUp(_bicycle);
        } else {
           _project.moveBicycleDown(_bicycle);
        }

    }    
    
    
}
