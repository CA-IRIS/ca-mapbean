
//Title:        Your Product Name
//Version:      
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;


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