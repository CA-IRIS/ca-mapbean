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

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.2 $ $Date: 2001/04/19 16:49:31 $ 
 */
public final class Range {

	private final double bottom;
	private final double top;

	public Range( double lower, double upper ) {
		if ( lower > upper ) {
			throw new IllegalArgumentException();
		}
		bottom = lower;
		top = upper;
	}

	public boolean checkRange( double value ) {
		boolean result = false;
		if ( value >= bottom && value <= top ) {
			result = true;
		}
		return result;
	}

	public boolean checkRange( int value ) {
	    return checkRange( ( double ) value );
	}
}