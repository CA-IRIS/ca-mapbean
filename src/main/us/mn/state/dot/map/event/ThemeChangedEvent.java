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

import java.util.EventObject;

/**
 * Theme changed events are sent to ThemeChangedListeners whenever a themes data
 * is changed.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.4 $ $Date: 2003/05/06 20:58:15 $ 
 */
public class ThemeChangedEvent extends EventObject {

	private int reason;
	public final static int GEOGRAPHY = 1,
							DATA = 2,
							SHADE = 4,
							HIGHLIGHT = 8,
							ANIMATION = 16,
							SELECTION = 32;
	
    /**
	 * Creates new ThemeChangedEvent 
	 */   
	public ThemeChangedEvent( Object source, int why ){
        super( source );
        reason = why;
    }
	
	/**
	 * Get the reason the Theme changed.
	 */
	public int getReason(){
        return reason;
    }
    
    public boolean testReason( int test ){
        return ( test & reason ) > 0;
    }

}