
//Title:        FieldFactory
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Creates fields from inputstream of dbasereader.

package us.mn.state.dot.shape;

import java.io.*;

public class FieldFactory {

    static Field createField(InputStream i, int o, int records) throws IOException {
    	ByteBuffer buffer = new ByteBuffer( i, 32 );
    	String name = (buffer.getString( 0, 11 )).trim();
    	char type = buffer.getString( 11, 1 ).charAt( 0 );
    	int offset = o;
    	int length = buffer.getByte( 16 );
    	int decimal = buffer.getByte( 17 );
    	Field result = null;
    	switch (type) {
    	case 'C': case 'D':
    	    result = new StringField(name, records, offset, length);
            break;
    	case 'N':
    	    if (decimal == 0 ) {
            	result = new IntegerField(name, records, offset, length);
            } else {
            	result = new DoubleField(name, records, offset, length);
            }
            break;
    	case 'L':
            result = new BooleanField(name, records, offset, length);
            break;
    	}
    	return result;
    }
} 