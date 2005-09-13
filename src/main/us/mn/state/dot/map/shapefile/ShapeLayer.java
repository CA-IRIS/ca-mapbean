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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import us.mn.state.dot.map.AbstractLayer;
import us.mn.state.dot.map.DefaultRenderer;
import us.mn.state.dot.map.LayerRenderer;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.Theme;
import us.mn.state.dot.map.symbol.PenSymbol;

/**
 * ShapeLayer is a class for drawing ESRI shape files.
 *
 * @author Douglas Lau
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  */
public class ShapeLayer extends AbstractLayer {

	/** List of shapes from the shapefile */
	protected final List shapes;

	/** The type of the shape file */
	protected final int shapeType;

	/** Create a new ShapeLayer from the specified filename */
	public ShapeLayer(String f, String layerName)
		throws IOException
	{
		this(ShapeLayer.class.getResource("/" + f + ".shp"), layerName);
	}

	/** Create a new shape layer */
	public ShapeLayer(URL url, String layerName) throws IOException {
		super(layerName);
		String f = url.toExternalForm();
		if(!f.endsWith(".shp"))
			throw new IOException("URL must be a '.shp' file");
		ShapeFile s = new ShapeFile(url);
		shapeType = s.getShapeType();
		extent = s.getExtent();
		shapes = s.getShapeList();
		readDbaseFile(f);
	}

	/** Read the Dbase file for the shape file */
	protected void readDbaseFile(String f) throws IOException {
		URL url = new URL(f.substring(0, f.length() - 4) + ".dbf");
		try {
			DbaseInputStream in = new DbaseInputStream(url);
			try { readDbase(in); }
			finally {
				in.close();
			}
		}
		catch(FileNotFoundException e) {
			// Ignore
		}
	}

	/** Read the contents of the Dbase file */
	protected void readDbase(DbaseInputStream in) throws IOException {
		Iterator it = shapes.iterator();
		while(in.hasNext() && it.hasNext()) {
			ShapeObject s = (ShapeObject)it.next();
			s.setFields(in.nextRecord());
		}
	}

	/** Get the symbol to draw the shape layer */
	protected Symbol getSymbol() {
		switch(shapeType) {
			case ShapeFile.POINT:
				return new PenSymbol("Point",
					Outline.createSolid(Color.BLACK, 20),
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

	/** Create the default theme */
	public Theme createTheme() {
		return new Theme(this, new DefaultRenderer(getSymbol()));
	}

	/** Paint the layer */
	public void paint(Graphics2D g, LayerRenderer renderer) {
		Iterator it = shapes.iterator();
		while(it.hasNext()) {
			ShapeObject shape = (ShapeObject)it.next();
			renderer.render(g, shape);
		}
	}

	/** Search for a shape which contains the specified point */
	public MapObject search(Point2D p) {
		Iterator it = shapes.iterator();
		while(it.hasNext()) {
			MapObject o = (MapObject)it.next();
			Shape s = o.getShape();
			if(s != null && s.contains(p))
				return o;
		}
		return null;
	}
}
