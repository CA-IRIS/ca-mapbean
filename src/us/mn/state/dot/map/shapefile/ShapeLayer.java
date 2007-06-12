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

import java.awt.Color;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Style;

/**
 * ShapeLayer is a class for drawing ESRI shape files.
 *
 * @author Douglas Lau
 * @author Erik Engstrom
  */
public class ShapeLayer extends Layer {

	/** List of fields in the Dbase file */
	protected final LinkedList<String> fields;

	/** List of shapes from the shapefile */
	protected final List<ShapeObject> shapes;

	/** The type of the shape file */
	protected final int shapeType;

	/** Create a new ShapeLayer from the specified filename */
	public ShapeLayer(String f, String layerName) throws IOException {
		this(ShapeLayer.class.getResource("/" + f + ".shp"), layerName,
			false);
	}

	/** Create a new shape layer */
	public ShapeLayer(URL url, String layerName, boolean verbose)
		throws IOException
	{
		super(layerName);
		if(url == null)
			throw new FileNotFoundException();
		String f = url.toExternalForm();
		if(!f.endsWith(".shp"))
			throw new IOException("URL must be a '.shp' file");
		ShapeFile s = new ShapeFile(url, verbose);
		shapeType = s.getShapeType();
		extent.setRect(s.getExtent());
		shapes = s.getShapeList();
		fields = readDbaseFile(f);
		if(verbose)
			fields.add("path");
	}

	/** Write the shape object data to a print stream */
	public void write(PrintStream out) {
		boolean first = true;
		for(String f: fields) {
			if(first)
				first = false;
			else
				out.print(',');
			out.print(f);
		}
		out.println();
		for(ShapeObject shape: shapes) {
			first = true;
			for(String f: fields) {
				if(first)
					first = false;
				else
					out.print(',');
				out.print(shape.getValue(f));
			}
			out.println();
		}
	}

	/** Read the Dbase file for the shape file */
	protected LinkedList<String> readDbaseFile(String f)
		throws IOException
	{
		URL url = new URL(f.substring(0, f.length() - 4) + ".dbf");
		try {
			DbaseInputStream in = new DbaseInputStream(url);
			try {
				readDbase(in);
				return in.getFields();
			}
			finally {
				in.close();
			}
		}
		catch(FileNotFoundException e) {
			return new LinkedList<String>();
		}
	}

	/** Read the contents of the Dbase file */
	protected void readDbase(DbaseInputStream in) throws IOException {
		for(ShapeObject s: shapes)
			s.setFields(in.nextRecord());
	}

	/** Get the style to draw the shape layer */
	protected Style getStyle() {
		switch(shapeType) {
			case ShapeFile.POINT:
				return new Style("Point",
					Outline.createSolid(Color.GREEN, 40),
					Color.WHITE);
			case ShapeFile.POINT_Z:
				return new Style("PointZ",
					Outline.createSolid(Color.BLACK, 20),
					Color.WHITE);
			case ShapeFile.POLYLINE:
				return new Style("PolyLine",
					Outline.createSolid(Color.BLACK, 20),
					null);
			case ShapeFile.POLYLINE_M:
				return new Style("PolyLineZ",
					Outline.createSolid(Color.BLACK, 20),
					null);
			case ShapeFile.POLYGON:
				return new Style("Polygon", null,
					Color.BLACK);
		}
		return null;
	}

	/** Iterate through the shapes in the layer */
	public MapObject forEach(MapSearcher s) {
		for(ShapeObject o: shapes) {
			if(s.next(o))
				return o;
		}
		return null;
	}

	/** Create a new layer state */
	public LayerState createState() {
		return new LayerState(this, new ShapeTheme(getStyle()));
	}
}
