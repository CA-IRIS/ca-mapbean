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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import us.mn.state.dot.map.AbstractLayer;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.SimpleTheme;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.Theme;
import us.mn.state.dot.map.symbol.PenSymbol;

/**
 * ShapeLayer is a class for drawing ESRI shape files.
 *
 * @author Douglas Lau
 * @author Erik Engstrom
  */
public class ShapeLayer extends AbstractLayer {

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
		String f = url.toExternalForm();
		if(!f.endsWith(".shp"))
			throw new IOException("URL must be a '.shp' file");
		ShapeFile s = new ShapeFile(url, verbose);
		shapeType = s.getShapeType();
		extent = s.getExtent();
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

	/** Get the symbol to draw the shape layer */
	protected Symbol getSymbol() {
		switch(shapeType) {
			case ShapeFile.POINT:
				return new PenSymbol("Point",
					Outline.createSolid(Color.GREEN, 40),
					Color.WHITE);
			case ShapeFile.POINT_Z:
				return new PenSymbol("PointZ",
					Outline.createSolid(Color.BLACK, 20),
					Color.WHITE);
			case ShapeFile.POLYLINE:
				return new PenSymbol("PolyLine",
					Outline.createSolid(Color.BLACK, 20),
					null);
			case ShapeFile.POLYLINE_M:
				return new PenSymbol("PolyLineZ",
					Outline.createSolid(Color.BLACK, 20),
					null);
			case ShapeFile.POLYGON:
				return new PenSymbol("Polygon", null,
					Color.BLACK);
		}
		return null;
	}

	/** Create a new layer state */
	public LayerState createState() {
		return new LayerState(this, new SimpleTheme(getSymbol()));
	}

	/** Paint the layer */
	public void paint(Graphics2D g, Theme theme) {
		for(ShapeObject s: shapes) {
			Symbol symbol = theme.getSymbol(s);
			if(symbol != null)
				symbol.draw(g, s);
		}
	}

	/** Search for a shape which contains the specified point */
	public MapObject search(Point2D p) {
		for(ShapeObject s: shapes) {
			if(s.getShape().contains(p))
				return s;
		}
		return null;
	}

	/** Get a list of all shape objects on the layer */
	public List<ShapeObject> getShapes() {
		return shapes;
	}
}
