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

package us.mn.state.dot.shape;

import java.awt.event.MouseAdapter;

import us.mn.state.dot.shape.event.MapMouseMode;
import us.mn.state.dot.shape.event.ZoomMouseMode;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.7 $ $Date: 2003/05/06 20:58:15 $ 
 */
public class MouseDelegator extends MouseAdapter {

	/**
	 * The MapBean.
	 */
	private MapBean map;
		
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