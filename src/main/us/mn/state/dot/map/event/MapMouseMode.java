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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * Interface for handling mouse behavior while the mouse is operating
 * over the MapBean.  A "MouseMode" object exists to interpret the
 * meaning of mouse events.  For instance, you could have a mode where
 * mouse events (click, drag-select) are interpreted as navigation
 * commands, (recenter, zoom-and-recenter).  There may be other modes
 * depending on how your application wants to interpret MouseEvents.
 * @see SelectMouseMode
 */
public interface MapMouseMode extends MouseListener, MouseMotionListener {

    /**
     * Returns the id (MapMouseMode name).
     * This name should be unique for each MapMouseMode.
     * @return String ID
     */
    public String getID();

    /**
     * Gets the mouse cursor recommended for use when this mouse mode
     * is active.
     * @return Cursor the mouse cursor recommended for use when this
     * mouse mode is active.
     */
    public Cursor getCursor();

    /**
     * Add a MapMouseListener to the MouseMode.
     * @param l the MapMouseListener to add.
     */
    public void addMapMouseListener( MapMouseListener l );

    /**
     * Remove a MapMouseListener from the MouseMode.
     * @param l the MapMouseListener to remove.
     */
    public void removeMapMouseListener( MapMouseListener l );

    /**
     * Remove all MapMouseListeners from the mode.
     */
    public void removeAllMapMouseListeners();
}
