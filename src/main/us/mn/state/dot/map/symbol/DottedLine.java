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

import java.awt.*;

/**
 * A LineSymbol that paints a dotted line _ _ _ _ _ .
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.5 $ $Date: 2001/07/09 21:10:19 $ 
 */
public class DottedLine extends LineSymbol {

	private BasicStroke stroke;

	/**
	 * Create a new DottedLine. With the default color.
	 */
	public DottedLine() {
		this( Color.black );
	}

	/**
	 * Create a new DottedLine using the given color.
	 * @param c, the color to use.
	 */
	public DottedLine( Color c ){
		super( c );
	}

	protected Stroke createStroke(){
		return new BasicStroke( getSize(), BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 10, new float[]{120, 40}, 0 );
	}
}