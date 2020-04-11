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

import java.util.Stack;

/**
 * 
 *  Undo Manager to handle the undo and redo stacks.
 *
 * @author Tom
 */
public class UndoManager {
    
    private static final int STACK_LIMIT = 50;
    
    private Stack<BaseUndo> _undoStack;
    
    private Stack<BaseUndo> _redoStack;
    
//    private UndoListener _listener;
    
    private boolean _isUpdating;
    
    private boolean _groupOpen;
    
    private UndoGroup _undoGroup;
    
    /**
     * Class constructor.
     */
    public UndoManager() {
        

        _undoStack = new Stack();
        _redoStack = new Stack();
        
        _isUpdating = false;
        
        _groupOpen = false;
        _undoGroup = null;
        
    }
    
    
    /**
     * Reset the undo manager - used to reset the manager when a file is opened.
     */
    public void reset() {
        
        _isUpdating = false;
        
        //clear all undos
        _undoStack.clear();
        //clear all redos
         _redoStack.clear();
        
    }
    
    /**
     * Push an Undo - records an undo object on the undo stack.
     * @param undo The Undo object to record.
     */
    public void pushUndo(BaseUndo undo) {
        
        //if updating from an undo or redo - don't accept additions
        //as events will fire off new undo objects
        
        if(!_isUpdating) {
            
            //push to group or stack?
            if(_groupOpen) {
                
                if(_undoGroup != null) {
                    _undoGroup.add(undo);
                }                
                
            } else {
                
                //do normal undo - no group
                System.out.println("***Pushing undo.");
                //push undo to stack
                _undoStack.push(undo);

                int stackSize = _undoStack.size();

                //do a stack size check
                if(stackSize > STACK_LIMIT) {

                    //trim the stack to limits
                    while(stackSize >= STACK_LIMIT) {

                        int last = _undoStack.size() - 1;
                        _undoStack.remove(last);
                        stackSize = _undoStack.size();

                    }

                }//end if stack limits                
                
                
                
            }//end if else group/stack
           

            //adding a new undo invalidates the redo stack,
            //so clear the redo stack (same for groups)
            _redoStack.clear();            
            
            
        } else {
            System.out.println("***Refused undo, updating...");
        }
        
        
    }
    
    /**
     * Open an undo group.
     */
    public void openGroup() {
        
        //open the group
        _groupOpen = true;
        //init new group object
        _undoGroup = new UndoGroup();        
        
    }
    
    /**
     * Close the undo group and push t undo stack
     */
    public void closeGroup() {
        
 
        if(_undoGroup != null) {
            if(_undoGroup.hasUndo()) {
                //push onto stack without checking limits
                _undoStack.push(_undoGroup);
            }            
        }

        //close the group
        _groupOpen = false;
        //clear the group
        _undoGroup = null;
        
        
    }
    
    /**
     * Undo the last action in the undo stack.
     */
    public void undo(){
        
        if(!_undoStack.empty()) {
            
            //disable any undo pushes
            //the undo action will generate events
            disable();
            
            BaseUndo undo  = _undoStack.pop();
            
            //undo the event
            //events will update the model and ui
            undo.undo();
            
            //put to redo stack
            _redoStack.push(undo);
            
            //notify the listener
//            notifyListener();
            
            //re-enable undo pushes
            enable();
            
        }
        
        
    }
    
    /**
     * Redo the last action on the redo stack
     */
    public void redo() {
        
        
        if(!_redoStack.empty()) {
            
            //disable any undo pushes
            //the redo action will generate events
            disable();
            
            BaseUndo undo  = _redoStack.pop();
            
            //redo the event
            undo.redo();
            
            //put to undo stack
            _undoStack.push(undo);
            
//            notifyListener();
            
            //re-enable the undo stack
            enable();
            
        }
        
        
    }
    
    /**
     * Disable the undo manager from accepting any new undos.
     * Since undo relies on the event chain to update the ui, etc.
     * When a undo is called, the resulting events  will try to create a new undo.
     * 
     * Also use this flag when opening a new file to suppress event undo's.
     * 
     */
    public void disable() {
        _isUpdating = true;
    }
    
    /**
     * Enable the undo manager to a accept new undos.
     *
     */
    public void enable() {
        _isUpdating = false;
    }
    

    
    
}
