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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.util.jar.*;
import java.net.*;
import us.mn.state.dot.shape.DbaseReader.*;

/**
  * ShapeLayer is a class for drawing ESRI shape files.
  *
  * @author Douglas Lau
  * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
  * @version $Revision: 1.34 $ $Date: 2001/05/12 00:10:40 $
  */
public class ShapeLayer extends AbstractLayer {

	//private DbaseReader dbFile;
	private Field [] fields;
	
	private Symbol symbol = null;

	/** Set symbol to paint layer with */
	public final void setSymbol( Symbol s ){
		symbol = s;
	}

	/** Array to hold all shape information */
	protected Shape [] paths;

	public final Field [] getFields(){
		return fields;
	}

	public final Field getField( String name ){
		for ( int i = 0; i < fields.length; i++ ) {
			if ( ( fields[ i ].getName()).equalsIgnoreCase( name ) ) {
				return fields[ i ];
			}
		}
		return null;
	}
	
	public ShapeLayer( URL fileLocation, String layerName ) throws IOException {
		setName( layerName );
		String url = fileLocation.toExternalForm();
		if ( url.endsWith( ".shp" ) ) {
			url = url.substring( 0, url.length() - 4 ) + ".dbf";
		} else { 
			throw new IOException( "fileLocation must be a '.shp' file" );
		}
		ShapeFile shapeFile = new ShapeFile( fileLocation.openStream() );
		URL dbUrl = new URL( url );
		DbaseReader dbFile = new DbaseReader( dbUrl.openStream() );
		fields = dbFile.getFields();
		createShapeLayer( shapeFile );
	}
	
	public ShapeLayer( String fileName, String layerName, String jarFileName )
			 throws IOException {
		setName( layerName );
		JarFile file = new JarFile( ShapeLayer.class.getResource(
			jarFileName ).getFile() );
		DbaseReader dbFile = new DbaseReader( file.getInputStream(
			file.getEntry( fileName + ".dbf" ) ) );
		ShapeFile shapeFile = new ShapeFile( file.getInputStream(
			file.getEntry( fileName + ".shp" ) ) );
		fields = dbFile.getFields();
		createShapeLayer( shapeFile );
	}
	
	public ShapeLayer( String fileName, String layerName ) throws IOException {
		setName( layerName );
		URL url = ShapeLayer.class.getResource( "/" + fileName + ".dbf" );
		//URL url = new URL("file:\\" + fileName + ".dbf");
		if ( url == null ) {
			System.out.println( "File " + fileName + ".dbf was not found" );
		}
		DbaseReader dbFile = new DbaseReader( url );
		url = ShapeLayer.class.getResource( "/" + fileName + ".shp" );
		//url = new URL("file:\\" + fileName + ".shp");
		if ( url == null ) {
			System.out.println( "File " + fileName + ".shp was not found" );
		}
		ShapeFile file = new ShapeFile( url );
		fields = dbFile.getFields();
		createShapeLayer( file );
	}

	private int shapeType;

	private final void createShapeLayer( ShapeFile file ){
		ArrayList list = file.getShapeList();
		paths = new GeneralPath [ list.size() ];
		for ( int i = 0; i < list.size(); i++ ) {
			GeneralPath path = new GeneralPath();
			path.append( ( PathIterator )list.get( i ), false );
			paths[ i ] = path;
		}
		extent = new Rectangle2D.Double( file.getXmin(), file.getYmin(),
			( file.getXmax() - file.getXmin() ),
			( file.getYmax() - file.getYmin() ) );
		shapeType = file.getShapeType();
		switch( shapeType ) {
		case ShapeFactory.POINT:
			setSymbol( new CircleMarker() );
			break;
		case ShapeFactory.POLYLINE:
			setSymbol( new SolidLine() );
			break;
		case ShapeFactory.POLYGON:
			setSymbol( new FillSymbol() );
			break;
		}
	}

	/** Create a new Layer from a ShapeFile */
	/*public ShapeLayer( String fileName ) throws IOException {
		 this( fileName, fileName );
	}*/

	/** Paint this Layer */
	public void paint( Graphics2D g, LayerRenderer renderer ) {
		for ( int i = ( paths.length - 1 ) ; i >= 0; i-- ){
			Symbol drawSymbol = symbol;
			if ( renderer != null ) {
				drawSymbol = renderer.render( i );
			}
			drawSymbol.draw( g, paths[ i ] );
		}
	}
	
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			int[] selections ) {
		if ( selections == null ) {
			return;
		}
		for ( int i = ( selections.length - 1 ); i >= 0; i-- ) {
			Symbol drawSymbol = symbol;
			if ( renderer != null ) {
				drawSymbol = renderer.render( i );
			}
			drawSymbol.draw( g, paths[ selections[ i ] ] );
		}
	}
	
	public int search( Rectangle2D searchArea ) {
		int result = -1;
		if ( searchArea.getWidth() == 0 || searchArea.getHeight() == 0 ) {
			for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
				Shape target = paths[ i ];
				Point2D point = new Point2D.Double( searchArea.getX(),
					searchArea.getY() );
				if ( target.contains( point ) ) {
					result = i;
					break;
				}
			}
		} else {
			for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
				Shape target = paths[ i ];
				if ( target.intersects( searchArea ) ||
						target.contains( searchArea )  ||
						searchArea.contains( target.getBounds2D() ) ) {
					result = i;
					break;
				} 
			}
		}
		return result;
	}
	
	public int search( Point2D p ) {
		int result = -1;
		switch( shapeType ) {
		/*case ShapeFactory.POINT:
			Symbol symbol = painter.getSymbol();
			if ( symbol == null ) {
				return result;
			}
			double size = symbol.getSize();
			Rectangle2D r = new Rectangle2D.Double( p.getX() - ( size / 2 ),
				p.getY() - ( size / 2 ), size, size );
			for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
				if ( r.contains( paths[ i ].getBounds() ) ) {
					result.add( paths[ i ] );
					break;
				}
			}
			break;*/
		case ShapeFactory.POLYLINE: case ShapeFactory.POLYGON:
			//Rectangle2D r = null;
			for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
				if ( paths[ i ].contains( p ) ) {
					result = i;
					break;
				}
			}
			break;
		}
		return result;
	}

	
	public final java.util.List getPaths( Point2D p ){
		java.util.List result = new ArrayList();
		switch( shapeType ) {
		/*case ShapeFactory.POINT:
			Symbol symbol = painter.getSymbol();
			if ( symbol == null ) {
				return result;
			}
			double size = symbol.getSize();
			Rectangle2D r = new Rectangle2D.Double( p.getX() - ( size / 2 ),
				p.getY() - ( size / 2 ), size, size );
			for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
				if ( r.contains( paths[ i ].getBounds() ) ) {
					result.add( paths[ i ] );
					break;
				}
			}
			break;*/
		case ShapeFactory.POLYLINE: case ShapeFactory.POLYGON:
			//Rectangle2D r = null;
			for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
				if ( paths[ i ].contains( p ) ) {
					result.add( paths[ i ] );
					break;
				}
			}
			break;
		}
		return result;
	}

	public void printData( OutputStream out ){   //only works for point shapes
		Field fields [] = this.getFields();
		PrintWriter writer = new PrintWriter( out );
		writer.write( "XCoord\tYCoord\t" );
		for ( int i = 0; i < fields.length; i++ ) {
			writer.write( fields[ i ].getName() + "\t" );
		}
		writer.write( "\r\n" );
		for ( int i = 0; i < fields[ 0 ].getLength(); i++ ){
			writer.write( paths[ i ].getBounds2D().getX() + "\t" +
				paths[ i ].getBounds2D().getY() + "\t" );
			for ( int j = 0; j < fields.length; j++ ) {
				writer.write( fields[ j ].getStringValue( i ) + "\t" );
			}
			writer.write( "\r\n" );
		}
		writer.flush();
	}
}