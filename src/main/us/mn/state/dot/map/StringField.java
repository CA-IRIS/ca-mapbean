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
 * Field for storing strings.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.7 $ $Date: 2001/05/12 00:10:40 $
 */
public class StringField extends Field {

	/** Field properties */
	private String [] data;
	
	/** Create a new dBase field */
	public StringField( String name, int size, int length ){
    		super( Field.STRING_FIELD, name, length );
	    	data = new String[ size ];
	}

	public int getLength(){
		return data.length;
	}

	/** Get the field's value from a given record */
	public String getValue( int record ) {
		if ( record >= data.length ) {
			return record + " " + this.getName();
		}
		return data[ record ];
	}

	public void setValue( int record, String value ){
		data[ record ] = value;
	}

	public String getStringValue( int record ){
		return getValue( record );
	}
}
