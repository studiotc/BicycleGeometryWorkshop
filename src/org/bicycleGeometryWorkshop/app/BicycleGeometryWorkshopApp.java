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

import org.bicycleGeometryWorkshop.ui.BicycleGeometryWorkshopUI;

/**
 *  This is the class that serves as the entry point for the main application (UI). 
 *  This initializes and starts the UI. 
 * @author Tom
 */
public class BicycleGeometryWorkshopApp {

    /**
     * Main method creates a new runnable object and starts it.
     * The runnable object loads and shows the UI.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
  
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               startWorkshopUI();
              
            }
        });  
        
    }
    
    
    /**
     * Start the UI.
     */
    public static void startWorkshopUI() {
        
        BicycleGeometryWorkshopUI ui = new BicycleGeometryWorkshopUI();
    
        
    }
    

    
    

    
}
