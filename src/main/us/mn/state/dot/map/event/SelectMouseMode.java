/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package us.mn.state.dot.shape.event;

import java.awt.event.MouseEvent;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.3 $ $Date: 2001/04/19 16:49:31 $
 */
public class SelectMouseMode extends MouseModeAdapter {
	
	/**
     * Mouse Mode identifier, which is "Select".
     * This is returned on getID()
     */
    public final static transient String MODE_ID = "Select".intern();

    /**
	 * Creates new SelectMouseMode 
	 * Default constructor.  Sets the ID to the modeID, and the
     * consume mode to true. 
     */
	public SelectMouseMode() {
		this( true );
	}
    
	/**
     * Construct a SelectMouseMode.
     * The constructor that lets you set the consume mode. 
     * @param consumeEvents the consume mode setting.
     */
    public SelectMouseMode( boolean consumeEvents ){
		super( MODE_ID, consumeEvents );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseClicked( MouseEvent e ) {
		mouseSupport.fireMapMouseClicked( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mousePressed( MouseEvent e ) {
		mouseSupport.fireMapMousePressed( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseReleased( MouseEvent e ) {
		mouseSupport.fireMapMouseReleased( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseEntered( MouseEvent e ) {
		mouseSupport.fireMapMouseEntered( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseExited( MouseEvent e ) {
		mouseSupport.fireMapMouseExited( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseDragged( MouseEvent e ) {
		mouseSupport.fireMapMouseDragged( e );
    }

    /**
     * Fires the MapMouseSupport method.
     * @param e mouse event.
     */
    public void mouseMoved( MouseEvent e ) {
		mouseSupport.fireMapMouseMoved( e );
    }

}