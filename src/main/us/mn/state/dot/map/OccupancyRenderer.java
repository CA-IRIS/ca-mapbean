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

import java.awt.Color;

/**
 * Renderer for showing occupancy on gpoly layer.
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.4 $ $Date: 2003/05/06 20:58:15 $ 
 */
public class OccupancyRenderer extends ClassBreaksRenderer {

	public OccupancyRenderer( String field, MapTip tip ) {
		super( field, 4, "Occupancy" );
		setSymbol( 0, ( new FillSymbol( Color.gray, "No Data" ) ) );
		setSymbol( 1, ( new FillSymbol( Color.green, "0-13%" ) ) );
		setSymbol( 2, ( new FillSymbol( Color.orange, "14-22%" ) ) );
		setSymbol( 3, ( new FillSymbol( Color.red, "23-99%" ) ) );
		setSymbol( 4, ( new FillSymbol( Color.gray ) ) );
		setBreaks( new double[]{ 0, 13, 22, 99 } );
		this.setTip( tip );
	}
} 