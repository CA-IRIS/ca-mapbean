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

package us.mn.state.dot.shape.shapefile;

import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.Shape;
import java.util.*;
import java.io.*;
import java.net.*;
import us.mn.state.dot.shape.*;

/**
  * ShapeLayer is a class for drawing ESRI shape files.
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.2 $ $Date: 2001/08/09 21:03:34 $
  */
public class ShapeLayer extends AbstractLayer {

	/** The records of the shapefile. */
	protected ShapeObject [] shapes;
	
	/** The type of the shape file. */
	private int shapeType;
	
	/**
	 * Create a new ShapeLayer from the specified filename.
	 */
	public ShapeLayer( String fileName, String layerName ) throws IOException {
		this( ShapeLayer.class.getResource( "/" + fileName + ".shp" ),
			layerName);
	}
	
	public ShapeLayer( URL fileLocation, String layerName ) throws IOException {
		setName( layerName );
		String url = fileLocation.toExternalForm();
		if ( !url.endsWith( ".shp" ) ) {
			throw new IOException( "fileLocation must be a '.shp' file" );
		}
		ShapeFile shapeFile = new ShapeFile( fileLocation );
		this.extent = shapeFile.getExtent();
		List shapeList = shapeFile.getShapeList();
		shapeType = shapeFile.getShapeType();
		shapes = ( ShapeObject[] ) shapeList.toArray( 
			new ShapeObject[ shapeList.size() ] );
	}
	
	public Theme getTheme() {
		Symbol symbol = null;
		switch( shapeType ) {
			case ShapeFactory.POINT:
				symbol = new CircleMarker();
				break;
			case ShapeFactory.POLYLINE:
				symbol = new SolidLine();
				break;
			case ShapeFactory.POLYGON:
				symbol = new FillSymbol();
				break;
		}
		return new Theme( this, new DefaultRenderer( symbol ) );
	}

	/** Paint this Layer */
	public void paint( Graphics2D g, LayerRenderer renderer ) {
		for ( int i = ( shapes.length - 1 ) ; i >= 0; i-- ){
			renderer.render( g, shapes[ i ] );
		}
	}
	
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			MapObject[] selections ) {
		if ( selections == null ) {
			return;
		}
		for ( int i = ( selections.length - 1 ); i >= 0; i-- ) {
			renderer.render( g, shapes[ i ] );
		}
	}
	
	public MapObject search( Rectangle2D searchArea, LayerRenderer renderer ) { //FIXME to use renderer.
		MapObject result = null;
		if ( searchArea.getWidth() == 0 || searchArea.getHeight() == 0 ) {
			for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
				Shape target = shapes[ i ].getShape();
				Point2D point = new Point2D.Double( searchArea.getX(),
					searchArea.getY() );
				if ( target.contains( point ) ) {
					result = shapes[ i ];
					break;
				}
			}
		} else {
			for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
				Shape target = shapes[ i ].getShape();
				if ( target.intersects( searchArea ) ||
						target.contains( searchArea )  ||
						searchArea.contains( target.getBounds2D() ) ) {
					result = shapes[ i ];
					break;
				} 
			}
		}
		return result;
	}
	
	public MapObject search( Point2D p, LayerRenderer renderer ) { //FIXME to use renderer.
		MapObject result = null;
		switch( shapeType ) {
		case ShapeFactory.POINT:
			break;
		case ShapeFactory.POLYLINE: case ShapeFactory.POLYGON:
			for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
				if ( shapes[ i ].getShape().contains( p ) ) {
					result = shapes[ i ];
					break;
				}
			}
			break;
		}
		return result;
	}

	
	public final java.util.List getPaths( Point2D p, LayerRenderer renderer ){ //FIXME to use renderer
		java.util.List result = new ArrayList();
		switch( shapeType ) {
		case ShapeFactory.POINT:
			break;
		case ShapeFactory.POLYLINE: case ShapeFactory.POLYGON:
			for ( int i = ( shapes.length - 1 ); i >= 0; i-- ) {
				if ( shapes[ i ].getShape().contains( p ) ) {
					result.add( shapes[ i ] );
					break;
				}
			}
			break;
		}
		return result;
	}
	
	/**
	 * Get the ShapeObject at the given index.
	 */
	public ShapeObject getShapeObject( int index ) {
		return shapes[ index ];
	}

	public void printData( OutputStream out ){   //only works for point shapes
		PrintWriter writer = new PrintWriter( out );
		writer.write( "XCoord\tYCoord\t" );
		String[] fields = shapes[0].getFields();
		for ( int i = 0; i < fields.length; i++ ) {
			writer.write( fields[ i ] + "\t" );
		}
		writer.write( "\r\n" );
		for ( int i = 0; i < shapes.length; i++ ){
			ShapeObject shapeObject = shapes[ i ];
			Shape shape = shapeObject.getShape();
			writer.write( shape.getBounds2D().getX() + "\t" +
				shape.getBounds2D().getY() + "\t" );
			for ( int j = 0; j < fields.length; j++ ) {
				writer.write( shapeObject.getValue( fields[ j ] ).toString() +
					"\t" );
			}
			writer.write( "\r\n" );
		}
		writer.flush();
	}
}
