
//Title:        DoubleField
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Double field for dbase files.

package us.mn.state.dot.shape;

import java.io.*;

public final class DoubleField extends NumericField {

	/** Field properties */
	private final double [] data;

	/** Create a new dBase field */
	public DoubleField( String name, int size, int offset, int length ){
		super( Field.DOUBLE_FIELD, name, offset, length );
		data = new double[ size ];
	}

	public int getLength(){
		return data.length;
	}

	/** Get the field's value from a given record */
	public final double getValue( int record ) {
		return data[ record ];
	}

	public void setValue( int record, double value ){
		data[ record ] = value;
	}

	void setValue( int index, String record ){
		data[ index ] = Double.parseDouble( record );
	}

	public String getStringValue( int record ){
		return new Double( data[ record ] ).toString();
	}

	public final int getRenderingClass( int index, double[] classBreaks ){
		int result = -1;
		for (int i = 0; i < classBreaks.length ; i++){
			if ( data[ index ] <= classBreaks[ i ] ) {
				result = i;
				break;
			}
		}
		if (data[ index ] > classBreaks[ classBreaks.length - 1 ]){
			result = classBreaks.length - 1;
		}
		return result;
	}
}
