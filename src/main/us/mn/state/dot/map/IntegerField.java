
//Title:        IntegerField
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Integer field for dbase files.

package us.mn.state.dot.shape;

import java.io.*;

public class IntegerField extends NumericField {

    /** Field properties */
    private int [] data;

    /** Create a new dBase field */
    public IntegerField(String name, int size, int offset, int length){
    	super(Field.INTEGER_FIELD, name, offset, length);
     	data = new int[size];
	}
	
	public int getLength(){
		return data.length;
	}

    /** Get the field's value from a given record */
    public int getValue( int record ) {
    	return data[record];
    }

    public void setValue( int record, int value ){
		data[record] = value;
	}

	void setValue(int index, String value){
		data[index] = Integer.parseInt(value);
	}

	public String getStringValue(int record){
		return new Integer(data[record]).toString();
	}

	public void setData(int[] data){
		this.data = data;
	}

	public int[] getData(){
		return this.data;
	}

	public int getRenderingClass(int index, double[] classBreaks){
		int result = -1;
		for (int i = 0; i < classBreaks.length ; i++) {
			if ( getValue(index) <= classBreaks[i] ) {
				result = i;
				break;
			}
		}
		if (getValue(index) > classBreaks[classBreaks.length - 1]){
			result = classBreaks.length - 1;
		}
		return result;
	}
}