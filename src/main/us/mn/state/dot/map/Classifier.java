
//Title:        Your Product Name
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

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