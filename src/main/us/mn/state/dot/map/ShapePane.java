package us.mn.state.dot.shape;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;


/**
  * ShapePane
  *
  * @author Douglas Lau
  */
public final class ShapePane extends Canvas {


	/** Main Layer (drawn on top, bounds set) */
	private Layer mainLayer;


	/** Array to hold background layer information */
	private final ArrayList layers = new ArrayList();


	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();


	/** Width and height in World coordinates */
	private double width;
	private double height;


	/** Bounding box */
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;


	/** Set the bounding box for display */
	private void setBoundingBox( double x1, double y1, double x2, double y2 ) {
		minX = x1;
		maxX = x2;
		if( maxX < minX ) {
			maxX = x1;
			minX = x2;
		}
		minY = y1;
		maxY = y2;
		if( maxY < minY ) {
			maxY = y1;
			minY = y2;
		}
		width = maxX - minX;
		height = maxY - minY;
		resized();
	}


	/** Image used for double-buffered rendering */
	private Image image = null;


	/** Inner class to take care of mouse events */
	private class MouseHelper extends MouseAdapter
		implements MouseMotionListener
	{
		boolean box = false;
		int x1;
		int y1;
		int x2;
		int y2;
		Rectangle rect = new Rectangle();

		public void mousePressed( MouseEvent e ) {
			x1 = e.getX();
			y1 = e.getY();
			box = false;
		}
		public void mouseReleased( MouseEvent e ) {
			if( box ) {
				box = false;
				AffineTransform world;
				try { world = at.createInverse(); }
				catch( NoninvertibleTransformException ex ) { return; }
				Point2D p1 = new Point2D.Double( x1, y1 );
				world.transform( p1, p1 );
				Point2D p2 = new Point2D.Double( x2, y2 );
				world.transform( p2, p2 );
				setBoundingBox( p1.getX(), p1.getY(), p2.getX(), p2.getY() );
			}
		}
		public void mouseDragged( MouseEvent e ) {
			if( box ) drawBox( rect );
			x2 = e.getX();
			y2 = e.getY();
			rect.x = x1;
			if( x2 < x1 ) rect.x = x2;
			rect.width = x2 - x1;
			if( rect.width < 0 ) rect.width = -rect.width;
			rect.y = y1;
			if( y2 < y1 ) rect.y = y2;
			rect.height = y2 - y1;
			if( rect.height < 0 ) rect.height = -rect.height;
			drawBox( rect );
			box = true;
		}
		public void mouseMoved( MouseEvent e ) {
			if( box ) box = false;
		}
	}


	/** Mouse helper class */
	private final MouseHelper mouse = new MouseHelper();


	/** Constructor */
	public ShapePane( Layer l ) {
		mainLayer = l;
		setBoundingBox( l.minX, l.minY, l.maxX, l.maxY );
		addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e ) {
				resized();
			}
		});
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
	}


	/** Draw an XOR box (rubberbanding box) */
	private void drawBox( Rectangle r ) {
		Graphics g = getGraphics();
		if( g == null ) return;
		g.setXORMode( Color.white );
		g.drawRect( r.x, r.y, r.width, r.height );
	}


	/** Draw the Main layer */
	public void drawMainLayer() {
		Image im = image;
		if( im == null ) return;
		synchronized( im ) {
			Graphics2D g = (Graphics2D)im.getGraphics();
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON );
			g.transform( at );
			mainLayer.paint( g );
		}
	}


	/** Called when the ShapePane is resized */
	private void resized() {
		final int w = getWidth();
		if( w == 0 ) return;
		final int h = getHeight();
		if( h == 0 ) return;
		double scaleX = (double)w / width;
		double scaleY = (double)h / height;
		double scale = scaleX;
		if( scale > scaleY ) scale = scaleY;
		at.setToTranslation( -( minX * scale ), maxY * scale );
		at.scale( scale, -scale );
		image = null;
		Thread t = new Thread() {
			public void run() {
//				image = paintBuffered( w, h, false );
//				repaint();
				image = paintBuffered( w, h, true );
				drawMainLayer();
				repaint();
			}
		};
		t.start();
	}


	/** Paint to the buffered image */
	private Image paintBuffered( int w, int h, boolean antialias ) {
		Image im = createImage( w, h );
		Graphics2D g = (Graphics2D)im.getGraphics();
		if( antialias ) g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON );
		g.clearRect( 0, 0, w, h );
		g.transform( at );
		for( int i = 0; i < layers.size(); i++ ) {
			Layer layer = (Layer)layers.get( i );
			layer.paint( g );
		}
		return im;
	}


	/** Add a new layer to the ShapePane */
	public void addLayer( Layer layer ) {
		layers.add( layer );
	}


	/** Overridden to prevent flashing */
	public void update( Graphics g ) {
		paint( g );
	}


	/** Paint the shapes */
	public void paint( Graphics g ) {
		Image im = image;
		if( im == null ) return;
		synchronized( im ) {
			g.drawImage( im, 0, 0, null );
		}
		if( mouse.box ) drawBox( mouse.rect );
	}
}
