/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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
package us.mn.state.dot.shape.shapefile;

import java.awt.Shape;
import java.util.Map;
import us.mn.state.dot.shape.MapObject;

/**
 * A ShapeObject represents a record from an ESRI shape file.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class ShapeObject implements MapObject {

	/** Map containing this ShapeObjects data */
	protected final Map fieldMap;

	/** Shape that contains the geo data for this object */
	protected Shape shape;

	/** Create a new ShapeObject */
	public ShapeObject(Shape shape, Map fields) {
		this.shape = shape;
		this.fieldMap = fields;
	}

	/** Get the shape to draw this object */
	public Shape getShape() {
		return shape;
	}

	/** Add a field to this ShapeObject */
	public void addField(String key, Object value) {
		fieldMap.put(key, value);
	}

	/** Get the value of the specified field */
	public Object getValue(String key) {
		return fieldMap.get(key);
	}

	/** Get the fields of this Shape Object */
	public String[] getFields() {
		return (String[])fieldMap.keySet().toArray(new String[] {});
	}
}
