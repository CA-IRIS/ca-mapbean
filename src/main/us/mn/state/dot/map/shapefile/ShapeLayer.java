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
package us.mn.state.dot.map.shapefile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import us.mn.state.dot.map.AbstractLayer;
import us.mn.state.dot.map.DefaultRenderer;
import us.mn.state.dot.map.LayerRenderer;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.Theme;
import us.mn.state.dot.map.symbol.CircleMarker;
import us.mn.state.dot.map.symbol.FillSymbol;
import us.mn.state.dot.map.symbol.SolidLine;

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
	public ShapeLayer(String fileName, String layerName)
		throws IOException
	{
		this(ShapeLayer.class.getResource("/" + fileName + ".shp"),
			layerName);
	}

	/** Create a new shape layer */
	public ShapeLayer(URL fileLocation, String layerName)
		throws IOException
	{
		super(layerName);
		String url = fileLocation.toExternalForm();
		if(!url.endsWith(".shp")) throw new IOException(
			"fileLocation must be a '.shp' file");
		ShapeFile shapeFile = new ShapeFile( fileLocation );
		this.extent = shapeFile.getExtent();
		shapes = shapeFile.getShapeList();
		shapeType = shapeFile.getShapeType();
	}

	/** Get the symbol to draw the shape layer */
	protected Symbol getSymbol() {
		switch(shapeType) {
			case ShapeFactory.POINT:
				return new CircleMarker();
			case ShapeFactory.POLYLINE:
				return new SolidLine();
			case ShapeFactory.POLYGON:
				return new FillSymbol();
		}
		return null;
	}

	/** Get the theme to use for this layer */
	public Theme getTheme() {
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
	public MapObject search(Point2D p, LayerRenderer renderer) {
		Iterator it = shapes.iterator();
		while(it.hasNext()) {
			MapObject o = (MapObject)it.next();
			Shape s = renderer.getShape(o);
			if(s != null && s.contains(p)) return o;
		}
		return null;
	}
}
