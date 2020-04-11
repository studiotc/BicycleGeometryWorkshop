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

import java.util.ArrayList;

/**
 * Undo Group class.  This class is for collecting multiple udo events into one group
 * so they can be treated as a single undo event.  
 * @author Tom
 */
public class UndoGroup extends BaseUndo{
    
    private ArrayList<BaseUndo> _undoList;
    
    
    /**
     * Class constructor.
     */
    public UndoGroup() {
        
        _undoList = new ArrayList();
        
    }
    
    /**
     * Add an Undo to the group.
     * @param undo The undo to add.
     */
    public void add(BaseUndo undo) {
        
        _undoList.add(undo);
        
    }
    
    /**
     * See if any members were added to the group.
     * @return True if it has undos.
     */
    public boolean hasUndo() {
        return !_undoList.isEmpty();
    }

    /**
     * Undo the group - undo all individual undos.
     */
    @Override
    public void undo() {
       
        int count = _undoList.size();
        for(int i =0; i < count; i++) {
            
            BaseUndo undo = _undoList.get(i);
            //undo the undo
            undo.undo();
            
        }
        
    }

    /**
     * Redo the group - redo all the individual undos.
     */
    @Override
    public void redo() {
        
        //move through this list backwards
        // so the uredos are executed in proper order
        int count = _undoList.size();
        for(int i =count -1; i >= 0; i--) {
            
            BaseUndo undo = _undoList.get(i);
            //redo the undo
            undo.redo();
            
        }        
        
        
    }
    
    
}
