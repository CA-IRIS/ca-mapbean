
//Title:        Field
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  The Field class is the parent class for all field information for the dbase table part of a shape file.

package us.mn.state.dot.shape;

import java.io.*;

public abstract class Field {
    private final int type;
    private final String name;
    final int offset;
    final int length;

    /** Constants for field type */
    public static final int INTEGER_FIELD = 3;
    public static final int DOUBLE_FIELD = 5;
    public static final int STRING_FIELD = 8;
    public static final int BOOLEAN_FIELD = 11;
    public static final int POINT_FIELD = 21;
    public static final int LINE_FIELD = 22;
    public static final int POLYGON_FIELD = 23;

    public Field(int type, String name, int offset, int length){
    	this.type = type;
     	this.name = name;
     	this.offset = offset;
     	this.length = length;
    }

    public void loadData(int index, ByteBuffer record){
    	String temp = record.getString(offset, length).trim();
    	setValue(index, temp);
    }

    abstract void setValue(int index, String value);

    public abstract String getStringValue(int index);

    public String getName(){
    	return name;
    }

    public int getType(){
    	return type;
    }
}