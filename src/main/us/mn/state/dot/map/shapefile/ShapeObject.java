/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2005  Minnesota Department of Transportation
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
package us.mn.state.dot.map.shapefile;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.map.MapObject;

/**
 * A ShapeObject represents a record from an ESRI shape file.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class ShapeObject implements MapObject {

	/** Geometric shape data */
	protected final Shape shape;

	/** Fields attached to the shape */
	protected Map fields = null;

	/** Create a new shape object */
	public ShapeObject(Shape s) {
		shape = s;
	}

	/** Get the shape to draw this object */
	public Shape getShape() {
		return shape;
	}

	/** Get the coordinate transform */
	public AffineTransform getTransform() {
		return null;
	}

	/** Set the field mapping */
	public void setFields(Map f) {
		fields = f;
	}

	/** Add a field mapping */
	public void addField(String key, Object value) {
		if(fields == null)
			fields = new HashMap();
		fields.put(key, value);
	}

	/** Get the value of the specified field */
	public Object getValue(String key) {
		if(fields == null)
			return null;
		else
			return fields.get(key);
	}
}
