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
public class Classifier {

	private Range[] bins;

	public Classifier( int size ) {
		bins = new Range[ size ];
	}

	public void setBin( int index, Range bin ) {
		if ( index < 0 || index >= bins.length ) {
			throw new IllegalArgumentException();
		}
		bins[ index ] = bin;
	}

	public int getClassBreak( double value ) {
		int result = -1;
		for ( int i = 0; i < bins.length; i++ ) {
			if ( bins[ i ].checkRange( value ) ) {
				result = i;
				break;
			}
		}
		return result;
	}
} 