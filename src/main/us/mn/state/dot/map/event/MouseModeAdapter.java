/* **********************************************************************
 * 
 *  BBNT Solutions LLC, A part of GTE
 *  10 Moulton St.
 *  Cambridge, MA 02138
 *  (617) 873-2000
 * 
 *  Copyright (C) 1998, 2000
 *  This software is subject to copyright protection under the laws of 
 *  the United States and other countries.
 * 
 * **********************************************************************
 */

package us.mn.state.dot.shape.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;


/**
 * Base class of the MouseModes.  It takes care of the administrative
 * aspects of being a mouse mode, but does not respond to MouseEvents.
 * <p>
 * This class delegates much of the work of managing its listeners to
 * a MapMouseSupport object.
 * @see MapMouseSupport
 */
public class MouseModeAdapter implements MapMouseMode {

    /**
     * The identifier for the mode, which is also the name that will
     * be used in a used interface describing the mode to a user.
     */
    protected String id = null;

    /**
     * The object used to handle the listeners and to pass out the
     * event to the layers interesed in it.
     */
    protected MapMouseSupport mouseSupport;

    /**
     * The cursor that appears on the map when this Mouse Mode is
     * active.
     */
    protected Cursor cursor = Cursor.getDefaultCursor();

    /**
     * Construct an AbstractMouseMode.
     * Default constructor, allocates the mouse support object.
     */
    public MouseModeAdapter() {
		this( "Unnamed Mode", true );
    }

    /**
     * Construct an AbstractMouseMode.
     * @param name the ID of the mode.
     * @param shouldConsumeEvents if true, events are propagated to
     * the first MapMouseListener that successfully processes the
     * event, if false, events are propagated to all MapMouseListeners
     */
    public MouseModeAdapter(String name,boolean shouldConsumeEvents) {
		mouseSupport = new MapMouseSupport( shouldConsumeEvents );
		id = name;
    }

    /**
     * Returns the id (mode name).
     * @return String ID
     */
    public String getID(){
		return id;
    }

    /**
     * Set the id (mode name).
     * @param id string that identifies the delegate.
     */
    public void setID( String name ){
		id = name;
    }

    /**
     * Gets the mouse cursor recommended for use when this mouse mode
     * is active.
     * @return Cursor the mouse cursor recommended for use when this
     * mouse mode is active.
     */
    public Cursor getCursor() {
		return cursor;
    }

    /**
     * Sets the cursor that is recommended for use on the map
     * when this mouse mode is active.
     * @param curs the cursor that is recommended for use on the map
     * when this mouse mode is active. 
     */
    public void setCursor( Cursor cursor ) {
		this.cursor = cursor;
    }

    /**
     * Sets how the delegate passes out events.  If the value passed
     * in is true, the delegate will only pass the event to the first
     * listener that can respond to the event.  If false, the delegate
     * will pass the event on to all its listeners.
     * @param value true for limited distribution.
     */
    public void setConsumeEvents( boolean value ){
		mouseSupport.setConsumeEvents( value );
    }

    /**
     * Returns how the delegate (and it's mouse support) is set up to
     * distribute events.
     * @return true if only one listner gets to act on an event.
     */
    public boolean isConsumeEvents(){
		return mouseSupport.isConsumeEvents();
    }

    /**
     * Add a MapMouseListener to the MouseMode.  The listener will
     * then get events from the delegator if the delegator is
     * active. 
     * @param l the MapMouseListener to add.
     */
    public void addMapMouseListener( MapMouseListener l ) {
		mouseSupport.addMapMouseListener( l );
    }

    /**
     * Remove a MapMouseListener from the MouseMode.
     * @param l the MapMouseListener to remove.
     */
    public void removeMapMouseListener( MapMouseListener l ) {
		mouseSupport.removeMapMouseListener( l );
    }

    /**
     * Remove all MapMouseListeners from the mode.
     */
    public void removeAllMapMouseListeners() {
		mouseSupport.removeAllMapMouseListeners();
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mouseClicked( MouseEvent e ) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mousePressed( MouseEvent e ) {}

    /**
     * Invoked when a mouse button has been released on a component.
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mouseReleased( MouseEvent e ) {}

    /**
     * Invoked when the mouse enters a component.
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mouseEntered( MouseEvent e ) {}

    /**
     * Invoked when the mouse exits a component.
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mouseExited( MouseEvent e ) {}

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mouseDragged( MouseEvent e ) {}

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons no down).
     * This does nothing.  Extend this class to add functionality.
     * @param e MouseEvent
     */
    public void mouseMoved( MouseEvent e ) {}
}
