package us.mn.state.dot.shape;


import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;


/**
  * Layer is a class for drawing ESRI shape files.
  *
  * @author Douglas Lau
  */
public final class Layer {


	/** Bounding box */
	public final double minX;
	public final double maxX;
	public final double minY;
	public final double maxY;


	/** Color to paint this layer */
	private Color color = null;

	/** Set the Color to paint the layer */
	public void setColor( Color c ) {
		color = c;
	}


	/** ShapePainter for this layer */
	private ShapePainter painter = null;

	/** Set the ShapePainter for the layer */
	public void setPainter( ShapePainter p ) {
		painter = p;
	}


	/** Array to hold all shape information */
	private final GeneralPath [] paths;


	/** Create a new Layer from a ShapeFile */
	public Layer( String fileName ) throws IOException {
		ShapeFile file = new ShapeFile( fileName );
		ArrayList list = file.getShapeList();
		paths = new GeneralPath [ list.size() ];
		for( int i = 0; i < list.size(); i++ ) {
			GeneralPath path = new GeneralPath();
			path.append( (PathIterator)list.get( i ), false );
			paths[ i ] = path;
		}
		minX = file.getXmin();
		maxX = file.getXmax();
		minY = file.getYmin();
		maxY = file.getYmax();
		switch( file.getShapeType() ) {
			case ShapeTypes.POINT:
				layerPainter = new PointPainter();
				break;
			case ShapeTypes.POLYLINE:
				layerPainter = new PolylinePainter();
				break;
			case ShapeTypes.POLYGON:
				layerPainter = new PolygonPainter();
				break;
		}
	}


	/** Paint this Layer */
	public void paint( Graphics2D g ) {
		layerPainter.paint( g );
	}


	/** LayerPainter for this layer */
	private LayerPainter layerPainter;


	/** LayerPainter interface */
	private interface LayerPainter {
		public void paint( Graphics2D g );
	}


	/**
	  * Layer$PointPainter is a LayerPainter for ESRI shape files which
	  * use the Point shape type.  It is responsible for painting the
	  * Point into a graphics context.
	  */
	private final class PointPainter implements LayerPainter {

		/** Paint a point shape file layer */
		public void paint( Graphics2D g ) {
			if( color != null ) g.setPaint( color );
			if( painter != null )
				for( int i = 0; i < paths.length; i++ )
					painter.paint( g, paths[ i ], i );
		}
	}


	/**
	  * Layer$PolylinePainter is a LayerPainter for ESRI shape files which
	  * use the PolyLine shape type.  It is responsible for painting the
	  * PolyLines into a graphics context.
	  */
	private final class PolylinePainter implements LayerPainter {

		/** Paint a polyline shape file layer */
		public void paint( Graphics2D g ) {
			if( color != null ) g.setPaint( color );
			for( int i = 0; i < paths.length; i++ )
				g.draw( paths[ i ] );
		}
	}


	/**
	  * Layer$PolygonPainter is a LayerPainter for ESRI shape files which
	  * use the Polygon shape type.  It is responsible for painting the
	  * Polygons into a graphics context.
	  */
	private final class PolygonPainter implements LayerPainter {

		/** Paint a polygon shape file layer */
		public void paint( Graphics2D g ) {
			if( color != null ) g.setPaint( color );
			int i;
			if( painter == null ) {
				for( i = 0; i < paths.length; i++ ) {
					g.fill( paths[ i ] );
				}
			}
			else {
				for( i = 0; i < paths.length; i++ ) {
					painter.paint( g, paths[ i ], i );
				}
			}
		}
	}
}
