
//Title:        DoubleField
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Double field for dbase files.

package us.mn.state.dot.shape;

import java.io.*;

public class DoubleField extends Field {

    /** Field properties */
    private double [] data;

    /** Create a new dBase field */
    public DoubleField(String name, int size, int offset, int length ){
    	super(Field.DOUBLE_FIELD,name, offset, length);
     	data = new double[size];
    }

    /** Get the field's value from a given record */
    public double getValue( int record ) {
    	return data[record];
    }

    public void setValue( int record, double value ){
    	data[record] = value;
    }

    void setValue(int index, String record){
    	data[index] = Double.parseDouble(record);
    }

    public String getStringValue(int record){
    	return new Double(data[record]).toString();
    }
}