
//Title:        StringField
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  String field for dbase files.

package us.mn.state.dot.shape;

import java.io.*;

public class StringField extends Field {
    /** Field properties */
    private String [] data;

    /** Create a new dBase field */
    public StringField(String name, int size, int offset, int length ){
    	super(Field.STRING_FIELD, name, offset, length);
    	data = new String[size];
	}

	public int getLength(){
		return data.length;
	}

    /** Get the field's value from a given record */
    public String getValue( int record ) {
		return data[record];
	}

	public void setValue( int record, String value ){
		data[record] = value;
	}

	public String getStringValue(int record){
   		return getValue(record);
    }
}
