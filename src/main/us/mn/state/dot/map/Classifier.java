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
 * A classifier is used to classify a double value into a range of values.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.3 $ $Date: 2001/07/09 21:10:19 $ 
 */
public class Classifier {

	private Range[] bins;
	
	/**
	 * Create a new Classifier.
	 */
	public Classifier( int size ) {
		bins = new Range[ size ];
	}

	/** 
	 * Set the bin at the given index to the given Range.
	 */
	public void setBin( int index, Range bin ) {
		if ( index < 0 || index >= bins.length ) {
			throw new IllegalArgumentException();
		}
		bins[ index ] = bin;
	}

	/** 
	 * Get the class break for the given value.
	 */
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