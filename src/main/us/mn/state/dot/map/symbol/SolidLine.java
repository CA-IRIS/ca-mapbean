/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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
package us.mn.state.dot.shape.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * A LineSymbol that renders a solid line.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class SolidLine extends LineSymbol {

	/** Create a new SolidLine */
	public SolidLine() {
		this(Color.black);
	}

	/**
	 * Create a new SolidLine using the given color.
	 * @param c, color to use.
	 */
	public SolidLine( Color c ){
		super( c );
	}

	/**
	 * Create a new SolidLine using the given color of the given size.
	 * @param c, color to use.
	 * @param size, width of the line created.
	 */
	public SolidLine( Color c, int size ) {
		super( c, size );
	}

	protected Stroke createStroke() {
		return new BasicStroke(size);
	}
}
