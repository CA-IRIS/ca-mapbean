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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import us.mn.state.dot.shape.AbstractLayer;
import us.mn.state.dot.shape.DefaultRenderer;
import us.mn.state.dot.shape.LayerRenderer;
import us.mn.state.dot.shape.MapObject;
import us.mn.state.dot.shape.Symbol;
import us.mn.state.dot.shape.Theme;
import us.mn.state.dot.shape.symbol.CircleMarker;
import us.mn.state.dot.shape.symbol.FillSymbol;
import us.mn.state.dot.shape.symbol.SolidLine;

/**
 * ShapeLayer is a class for drawing ESRI shape files.
 *
 * @author Douglas Lau
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  */
public class ShapeLayer extends AbstractLayer {

	/** The records of the shapefile */
	protected ShapeObject[] shapes;

	/** The type of the shape file */
	private int shapeType;

	/** Create a new ShapeLayer from the specified filename */
	public ShapeLayer(String fileName, String layerName)
		throws IOException
	{
		this(ShapeLayer.class.getResource("/" + fileName + ".shp"),
			layerName);
	}

	public ShapeLayer(URL fileLocation, String layerName)
		throws IOException
	{
		setName(layerName);
		String url = fileLocation.toExternalForm();
		if(!url.endsWith(".shp")) throw new IOException(
			"fileLocation must be a '.shp' file");
		ShapeFile shapeFile = new ShapeFile( fileLocation );
		this.extent = shapeFile.getExtent();
		List shapeList = shapeFile.getShapeList();
		shapeType = shapeFile.getShapeType();
		shapes = (ShapeObject[]) shapeList.toArray(
			new ShapeObject[shapeList.size()]);
	}

	public Theme getTheme() {
		Symbol symbol = null;
		switch(shapeType) {
			case ShapeFactory.POINT:
				symbol = new CircleMarker();
				break;
			case ShapeFactory.POLYLINE:
				symbol = new SolidLine();
				break;
			case ShapeFactory.POLYGON:
				symbol = new FillSymbol();
				break;
			default:
			    symbol = null;
		}
		return new Theme(this, new DefaultRenderer(symbol));
	}

	/** Paint the layer */
	public void paint(Graphics2D g, LayerRenderer renderer) {
		for(int i = (shapes.length - 1); i >= 0; i--) {
			renderer.render(g, shapes[i]);
		}
	}

	public MapObject[] getMapObjects() {
		return shapes;
	}

	public MapObject search( Rectangle2D searchArea, LayerRenderer renderer ) {
		MapObject result = null;
		for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
			MapObject object = shapes[ i ];
			Shape target = null;
			if ( renderer == null ) {
				target = object.getShape();
			} else {
				target = renderer.getShape( object );
			}
			if ( target.intersects( searchArea ) ||
					target.contains( searchArea )  ||
					searchArea.contains( target.getBounds2D() ) ) {
				result = object;
				break;
			}
		}
		return result;
	}

	public MapObject search( Point2D p, LayerRenderer renderer ) {
		MapObject result = null;
		for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
			MapObject object = shapes[ i ];
			Shape target = renderer.getShape( object );
			if ( target.contains( p ) ) {
				result = object;
				break;
			}
		}
		return result;
	}

	public List getPaths(Point2D p, LayerRenderer renderer) {
		List result = new ArrayList();
		for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
			MapObject object = shapes[ i ];
			Shape target = renderer.getShape( object );
			if ( target.contains( p ) ) {
				result.add( object );
				break;
			}
		}
		return result;
	}

	/** Get the ShapeObject at the given index */
	public ShapeObject getShapeObject(int index) {
		return shapes[index];
	}
}
