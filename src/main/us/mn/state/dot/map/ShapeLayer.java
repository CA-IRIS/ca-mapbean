
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

	/** Set symbol to paint layer with */
	public final void setSymbol( Symbol s ){
		painter.setSymbol( s );
	}

	/** Renderer for this layer */
	private ShapeRenderer painter = null;

	/** Set the Renderer for the layer */
	public final void setRenderer( ShapeRenderer p ) {
		painter = p;
		updateLayer();
	}

	/** Get the Renderer for the layer */
	public final ShapeRenderer getRenderer(){
		return painter;
	}

	/** Array to hold all shape information */
	protected GeneralPath [] paths;

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
		DbaseReader dbFile = new DbaseReader( file.getInputStream( file.getEntry(
				fileName + ".dbf" ) ) );
		ShapeFile shapeFile = new ShapeFile( file.getInputStream(
				file.getEntry( fileName + ".shp" ) ));
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

	private final void createShapeLayer( ShapeFile file ){
		ArrayList list = file.getShapeList();
		paths = new GeneralPath [ list.size() ];
		for ( int i = 0; i < list.size(); i++ ) {
			GeneralPath path = new GeneralPath();
			path.append( ( PathIterator )list.get( i ), false );
			paths[ i ] = path;
		}
		painter = new DefaultRenderer();
		extent = new Rectangle2D.Double( file.getXmin(), file.getYmin(),
			( file.getXmax() - file.getXmin() ),
			( file.getYmax() - file.getYmin() ) );
		switch( file.getShapeType() ) {
		case ShapeTypes.POINT:
			painter.setSymbol( new CircleMarker() );
			break;
		case ShapeTypes.POLYLINE:
			painter.setSymbol( new SolidLine() );
			break;
		case ShapeTypes.POLYGON:
			painter.setSymbol( new FillSymbol() );
			break;
		}
	}

	/** Create a new Layer from a ShapeFile */
	public ShapeLayer( String fileName ) throws IOException {
		 this( fileName, fileName );
	}

	/** Paint this Layer */
	public final void paint( Graphics2D g ) {
		if ( isVisible() ) {
			for ( int i = ( paths.length - 1 ) ; i >= 0; i-- ){
				painter.paint( g, paths[ i ], i );
			}
		}
	}

	public final boolean mouseClick( int clickCount, Point2D p, Graphics2D g ){
		boolean result;
		java.util.List found = hit( p );
		result = ! found.isEmpty();
		return result;
	}

	private final java.util.List hit( Point2D p ){
		java.util.List result = new ArrayList();
		Rectangle2D r = null;
		for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
			r = paths[ i ].getBounds2D();
			if ( r.contains( p ) ) {
				result.add( paths[ i ] );
			}
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
				f.write( paths[ i ].getCurrentPoint().getX() + "\t" +
					paths[ i ].getCurrentPoint().getY() + "\t" );
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

	public final String getTip( Point2D p ){
		String result = null;
		Rectangle2D searchZone = new Rectangle2D.Double( ( p.getX() - 250 ),
			( p.getY() - 250 ), 500, 500 );
		Rectangle2D r = null;
		Point2D q = null;
		for ( int i = ( paths.length - 1 ); i >= 0; i-- ) {
			r = paths[ i ].getBounds2D();
			if ( ( r.getWidth() == 0) || ( r.getHeight() == 0 ) ) {
				q = paths[ i ].getCurrentPoint();
				if ( searchZone.contains( q ) ) {
					result = painter.getTip( this, i );
					break;
				}
			} else {
				if ( searchZone.contains( r ) || r.contains( searchZone ) ||
						r.intersects( searchZone ) ) {
					result = painter.getTip( this, i );
					break;
				}
			}
		}
		return result;
	}
}
