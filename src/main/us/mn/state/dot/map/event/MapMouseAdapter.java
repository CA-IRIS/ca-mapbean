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
 * 
 * $Source: /home/lau1dou/cvsroot/MapBean/src/main/us/mn/state/dot/map/event/Attic/MapMouseAdapter.java,v $
 * $Revision: 1.2 $
 * $Date: 2000/07/13 21:38:50 $
 * $Author: engs1eri $
 * 
 * **********************************************************************
 */

package us.mn.state.dot.shape.event;

import java.awt.event.MouseEvent;

/**
 * Basic implementation of the MapMouseListener interface provided as
 * a convenience.  If you extend an object from this adapter, you just
 * have to implement the methods that you want to deal with.
 */
public abstract class MapMouseAdapter implements MapMouseListener {

    /**
     * Return a list of the modes that are interesting to the
     * MapMouseListener.  You MUST override this with the modes you're
     * interested in.
     */
    public String[] getMouseModeServiceList(){
		return null;
    }

    // Mouse Listener events
    ////////////////////////

    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param e MouseEvent
     * @return false
     */
    public boolean mousePressed( MouseEvent e ){ 
		return false; // did not handle the event
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseReleased( MouseEvent e ){
		return false;
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseClicked( MouseEvent e ){
		return false;
    }

    /**
     * Invoked when the mouse enters a component.
     * @param e MouseEvent
     */
    public void mouseEntered( MouseEvent e ){
    }

    /**
     * Invoked when the mouse exits a component.
     * @param e MouseEvent
     */
    public void mouseExited( MouseEvent e ){
    }

    // Mouse Motion Listener events
    ///////////////////////////////

    /**
     * Invoked when a mouse button is pressed on a component and then 
     * dragged.  The listener will receive these events if it
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseDragged( MouseEvent e ){
		return false;
    }

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons down).
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseMoved( MouseEvent e ){
		return false;
    }

    /**
     * Handle a mouse cursor moving without the button being pressed.
     * Another layer has consumed the event.
     */
    public void mouseMoved(){
    }
}
