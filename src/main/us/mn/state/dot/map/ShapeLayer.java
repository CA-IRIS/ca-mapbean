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

/**
  * ShapeLayer is a class for drawing ESRI shape files.
  *
  * @author Douglas Lau
  */

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.util.jar.*;
import java.net.*;
import us.mn.state.dot.shape.DbaseReader.*;

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
		//painter = new DefaultRenderer();
		extent = new Rectangle2D.Double( file.getXmin(), file.getYmin(),
			( file.getXmax() - file.getXmin() ),
			( file.getYmax() - file.getYmin() ) );
		shapeType = file.getShapeType();
		switch( shapeType ) {
		case ShapeTypes.POINT:
			setSymbol( new CircleMarker() );
			break;
		case ShapeTypes.POLYLINE:
			setSymbol( new SolidLine() );
			break;
		case ShapeTypes.POLYGON:
			setSymbol( new FillSymbol() );
			break;
		}
	}

	/** Create a new Layer from a ShapeFile */
	public ShapeLayer( String fileName ) throws IOException {
		 this( fileName, fileName );
	}

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
	
	public final java.util.List getPaths( Point2D p ){
		java.util.List result = new ArrayList();
		switch( shapeType ) {
		/*case ShapeTypes.POINT:
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
		case ShapeTypes.POLYLINE: case ShapeTypes.POLYGON:
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

	public boolean writeToFile( FileWriter f ){   //only works for point shapes
		Field fields [] = this.getFields();
		try {
			f.write( "XCoord\tYCoord\t" );
			for ( int i = 0; i < fields.length; i++ ) {
				f.write( fields[ i ].getName() + "\t" );
			}
			f.write( "\r\n" );
			for ( int i = 0; i < fields[ 1 ].getLength(); i++ ){
				System.out.println( i );
				f.write( paths[ i ].getBounds().getX() + "\t" +
					paths[ i ].getBounds().getY() + "\t" );
				for ( int j = 0; j < fields.length; j++ ) {
					f.write( fields[ j ].getStringValue( i ) + "\t" );
				}
				f.write( "\r\n" );
			}
			f.flush();
		} catch ( IOException ex ) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}