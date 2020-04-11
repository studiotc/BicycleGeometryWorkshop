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
package org.bicycleGeometryWorkshop.attributes;

import java.util.ArrayList;
import javax.swing.JComponent;

/**
 * Attribute base class. The base class defines the basic structure and 
 * handles notification events to the owner.  It also manages the secondary listener notifications.  
 * The owner will always be notified (unless the overrides are on) while the listener collection holds the optionally added listeners.  
 * Notifications can be overridden to suppress events when necessary.
 *
 * @author Tom
 */
public abstract class BaseAttribute {

    private String _name;

    private String _suffix;
    
    private String _description;

    private AttributeSet _ownerSet;

//    private boolean _readOnly;

    private boolean _display;

    private boolean _notificationEnabled;

    private ArrayList<AttributeChangeListener> _listeners;

    /**
     * Class constructor.
     * @param name The name of the attribute.
     */
    public BaseAttribute(String name) {
        this(name, "", "");

    }
    
    /**
     * Class constructor.
     * @param name  The name of the Attribute.
     * @param description Description of the attribute for display.
     */
   public BaseAttribute(String name, String description) {
        this(name, "", description);


    }    

   /**
    * Class constructor.
    * @param name  The name of the attribute.
    * @param suffix  The suffix of the attribute.
    * @param description Description of the attribute for display.
    */
    public BaseAttribute(String name, String suffix, String description) {

        _name = name;
        _suffix = suffix;
        _description = description;

        _ownerSet = null;

//        _readOnly = false;

        _listeners = new ArrayList();

        _display = true;

        //default is that notifications are enabled
        _notificationEnabled = true;

    }


    /**
     * Get the description of the attribute.
     * This is used for UI elements (tool tips).
     * @return The description of the attribute.
     */
    public String getDescription() {
        return _description;
    }
    
    /**
     * Set the description of the attribute.
     * @param description The description of the attribute.
     */
    public void setDescription(String description) {
         _description = description;
    }    
    
    /**
     * Enable or disable notifications to the attribute owner and listeners.
     *
     * @param enabled True to permit notifications when the attribute is
     * changed, false will disable notifications.
     */
    public void enableNotification(boolean enabled) {
        _notificationEnabled = enabled;
    }

    /**
     * Get the notification enabled setting.
     * @return True if notifications are enabled, false otherwise.
     */
    public boolean getNotifactionEnabled() {
        return _notificationEnabled;
    }
    /**
     * Get the display setting. If the display is off, the editor will not be
     * added to the parent panel.
     *
     * @return True if the attribute is to be displayed, false otherwise.
     */
    public boolean getDisplay() {
        return _display;
    }

    /**
     * Set the display of the Attribute - shows or hides the attribute editor.
     * @param display True to show the editor, false to hide the editor.
     */
    public void setDisplay(boolean display) {
        _display = display;
    }

    /**
     * Add a listener to the attribute.
     * @param listener The listener to add the listener list.
     */
    public void addListener(AttributeChangeListener listener) {

        _listeners.add(listener);

    }



    /**
     * Get the Name of the Attribute.
     *
     * @return The name of the attribute.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the Suffix of this Attribute.
     *
     * @return The suffix.
     */
    public String getSuffix() {
        return _suffix;
    }

    /**
     * Set the Attributes Suffix
     *
     * @param suffix The suffix to display.
     */
    public void setSuffix(String suffix) {
        _suffix = suffix;

    }


    
    /**
     * Set the attribute from an object.  This is used to set the object from data pulled from the database.
     * The object must be tested and cast internally.  The object is a data type support in a database column.
     * @param object The object to be cast to internal data.
     */
    public abstract void setFromObject(Object object);
    
    

    /**
     * Get the Editor for this attribute. This is a JComponent (text field, drop
     * down list, etc) that is provided as an editor. The component is attached
     * to the attribute editor when generated.
     *
     * @return JComponent for editing the attribute.
     */
    public abstract JComponent getEditor();

    /**
     * Sets the owner attribute set
     *
     * @param set  The attribute set that is the owner of the attribute.
     */
    public void setOwnerSet(AttributeSet set) {
        _ownerSet = set;
    }

    /**
     * Called to notify the attribute owner a change has been made.
     * @param acEvent The event to be sent with notification.
     */
    protected void onChange(AttributeChangeEvent acEvent) {

        //check if notifactions are enabled
        if (_notificationEnabled) {

            
            if (_ownerSet != null) {
                _ownerSet.attributeChanged(acEvent);
            }


            for (AttributeChangeListener l : _listeners) {
                l.attributeChanged(acEvent);
            }

        }

    }



    /**
     * This is the string representation of the attribute to use
     * when generating the SQL Insert statement.
     *
     * @return The string value of the attribute.
     */
    public abstract String getSQLInsert();

 

    /**
     * Get the Attribute data type for translation to the SQLite Database.
     * @return The attribute data type to sue for storage in the database.
     */
    public abstract AttributeDataType getSQLType();
    
    

    /**
     * String form test - not currently used.
     * @return The class name and attribute name.
     */
    @Override
    public String toString() {
        String cName = this.getClass().getSimpleName();
        return cName + " : " + getName() + " : " + getSQLInsert();
    }
    

}
