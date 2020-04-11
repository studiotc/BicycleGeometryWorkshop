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
package org.bicycleGeometryWorkshop.app;

import org.bicycleGeometryWorkshop.components.Bicycle;

/**
 *  The listener interface for receiving project events.  This is used by the UI
 * as a link between the project and navigator panel.
 * @author Tom
 */
public interface ProjectListener {
    
    /**
     * Invoked when the project viewer need to be redrawn.
     * This will redraw all the bicycles in the project.
     */
    public void redrawViewer();
    
    
    /**
     * Invoked when when a Bicycle changed event occurs.
     */
    public void bicycleChanged();
    
    /**
     * Invoked when the report needs to be updated.
     */
    public void updateReport();
    
    /**
     * Invoked when the project is loaded from the database.
     */
    public void projectedLoadedFromDB();
    
    /**
     * Invoked when a bicycle is added to the project.
     * @param bicycle The Bicycle that was added.
     */
    public void bicycleAdded(Bicycle bicycle);
    
    /**
     * Invoked when a bicycle is restored from an undo delete.
     * @param bicycle The bicycle that was restored.
     * @param index The position of the bicycle in the list.
     */
    public void bicycleRestored(Bicycle bicycle, int index);
    
    /**
     * Invoked when a bicycle is moved up in the list.
     * @param bicycle The bicycle that is moved up.
     */
    public void bicycleMovedUp(Bicycle bicycle);
    
    /**
     * Invoked when a bicycle is moved down in the list.
     * @param bicycle The bicycle that is moved down.
     */
    public void bicycleMovedDown(Bicycle bicycle);
    
    /**
     * Invoked when a bicycle is deleted from the project.
     * @param bicycle The deleted bicycle.
     */
    public void bicycleDeleted(Bicycle bicycle);
    
    
}
