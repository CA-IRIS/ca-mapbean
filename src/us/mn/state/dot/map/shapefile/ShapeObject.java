/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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
 */
package us.mn.state.dot.map.shapefile;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import us.mn.state.dot.map.MapObject;

/**
 * A ShapeObject represents a record from an ESRI shape file.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class ShapeObject implements MapObject {

	/** Identity transform */
	static protected final AffineTransform IDENTITY_TRANSFORM =
		new AffineTransform();

	/** Geometric shape data */
	protected final Shape shape;

	/** Fields attached to the shape */
	protected Map<String, Object> fields = null;

	/** Create a new shape object */
	public ShapeObject(PathIterator p) {
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		path.append(p, false);
		shape = path;
	}

	/** Get the shape to draw this object */
	public Shape getShape() {
		return shape;
	}

	/** Get the coordinate transform */
	public AffineTransform getTransform() {
		return IDENTITY_TRANSFORM;
	}

	/** Get the inverse coordinate transform */
	public AffineTransform getInverseTransform() {
		return IDENTITY_TRANSFORM;
	}

	/** Set the field mapping */
	public void setFields(Map<String, Object> f) {
		if(fields == null)
			fields = f;
		else
			fields.putAll(f);
	}

	/** Add a field mapping */
	public void addField(String key, Object value) {
		if(fields == null)
			fields = new HashMap<String, Object>();
		fields.put(key, value);
	}

	/** Get the value of the specified field */
	public Object getValue(String key) {
		// FIXME: added by mtod, D10 specific, 05/06/08
                if( key.equals("STA")) key="STATION";

		if(fields == null)
			return null;
		else
			return fields.get(key);
	}

	/** toString */
	public String toString() {
		if(fields == null)
			return "(ShapeObject: no fields)";

		StringBuilder r=new StringBuilder("(ShapeObject: ");
		for (Iterator iter = fields.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry)iter.next();
			if (entry==null) continue;
			r.append(entry.getKey()==null ? "null" : entry.getKey().toString());
			r.append("=");
			r.append(entry.getValue()==null ? "null" : entry.getValue().toString());
			if (iter.hasNext())
		    		r.append(", ");
		}
    		r.append(")");
		return r.toString();
	}

}
