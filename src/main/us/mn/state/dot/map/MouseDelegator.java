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

/**
 * MouseDelegator.java
 *
 * Created on June 30, 2000, 1:51 PM
 */

package us.mn.state.dot.shape;

import java.awt.event.*;
import us.mn.state.dot.shape.event.*;

/**
 *
 * @author  engs1eri
 * @version 
 */
public class MouseDelegator extends MouseAdapter {

	/**
	 * The MapBean.
	 */
	private MapBean map;
	
	/**
	 * The registered MapMouseModes.
	 */
	//private ArrayList mouseModes = new ArrayList();
		
	/**
	 * The active MapMouseMode.
	 */
	private MapMouseMode activeMouseMode;
	
    /** Creates new MouseDelegator */
    public MouseDelegator( MapBean map ) {
		this.map = map;
		setActiveMouseMode( new ZoomMouseMode() );
    }
	
	/** 
	 * Sets the active MapMouseMode.
	 */
	public void setActiveMouseMode( MapMouseMode mode ) {
		activeMouseMode = mode;
		map.setCursor( activeMouseMode.getCursor() );
		map.addMouseListener( mode );
		map.addMouseMotionListener( mode );
	}
	
	/**
	 * Gets the active MapMouseMode.
	 */
	public MapMouseMode getActiveMouseMode() {
		return activeMouseMode;
	}
}