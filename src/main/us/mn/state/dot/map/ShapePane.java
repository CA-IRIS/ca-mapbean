package us.mn.state.dot.shape;


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;


/**
  * ShapePane
  *
  * @author Douglas Lau
  */
public final class ShapePane extends JPanel {


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

  private int dummy;

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
    private ShapePane map;

    public MouseHelper(ShapePane p){
      map = p;
    }

		public void mousePressed( MouseEvent e ) {
			x1 = e.getX();
			y1 = e.getY();
			box = false;
		}
		public void mouseReleased( MouseEvent e ) {
      if(SwingUtilities.isLeftMouseButton(e)){
			  /*if( box ) {
				  box = false;
				  AffineTransform world;
				  try { world = at.createInverse(); }
				  catch( NoninvertibleTransformException ex ) { return; }
				  Point2D p1 = new Point2D.Double( x1, y1 );
				  world.transform( p1, p1 );
				  Point2D p2 = new Point2D.Double( x2, y2 );
				  world.transform( p2, p2 );
				  setBoundingBox( p1.getX(), p1.getY(), p2.getX(), p2.getY() );
			  }*/
       /* if(box){
        box = false;
        x2 = e.getX();
        y2 = e.getY();
        AffineTransform world;
			  try { world = at.createInverse(); }
			  catch( NoninvertibleTransformException ex ) { return; }
        Point2D p1 = new Point2D.Double(x1,y1);
        world.transform(p1, p1);
        Point2D p2 = new Point2D.Double( x2, y2 );
			  world.transform( p2, p2 );
        setBoundingBox(minX+p1.getX()-p2.getX(),minY+p1.getY()-p2.getY(),maxX+p1.getX()-p2.getX(),maxY+p1.getY()-p2.getY());
        resized();
        }*/
        /* x2 = e.getX();
        y2 = e.getY();
        map.moveMap(x1,y1,x2,y2);
        x1 = x2;
        y1 = y2;*/

      }

		}

    public void mouseClicked(MouseEvent e){
      if(SwingUtilities.isRightMouseButton(e)){
        System.out.println("mouse clicked");
        zoom(200);
        dummy++;
        System.out.println("Dummy = " + dummy);
        if (dummy > 4){
           setBoundingBox( mainLayer.minX, mainLayer.minY, mainLayer.maxX, mainLayer.maxY );
           resized();
        }
      }
    }



		public void mouseDragged( MouseEvent e ) {
      if(SwingUtilities.isLeftMouseButton(e)){
			  /*if( box ) drawBox( rect );
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
			  box = true;*/
        /*if (box) drawLine(x1,y1,x2,y2);
        x2 = e.getX();
        y2 = e.getY();
        drawLine(x1,y1,x2,y2);
        box = true;*/
        x2 = e.getX();
        y2 = e.getY();
        map.moveMap(x1,y1,x2,y2);
        x1 = x2;
        y1 = y2;
		  }
     }

    public void mouseMoved( MouseEvent e ) {
			if( box ) box = false;
		}
	}

  public void zoom(int size){
    System.out.println("zoom entered");
    Dimension d = this.getSize();
    System.out.println(d.getWidth() + " " + d.getHeight());
    Double w = new Double(d.getWidth()+200);
    Double h = new Double(d.getHeight()+200);
    d.setSize(w.intValue(),h.intValue());
    this.setSize(d);
  }


	/** Mouse helper class */
	private final MouseHelper mouse = new MouseHelper(this);


	/** Constructor */
   public ShapePane(){
     this.setDoubleBuffered(true);
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

  private void drawLine(int x1, int y1, int x2, int y2){
    Graphics g = getGraphics();
    if(g==null)return;
    g.setXORMode(Color.white);
    g.drawLine(x1,y1,x2,y2);
  }


	/** Draw the Main layer */
	public void drawMainLayer() {
		Image im = image;
		if( im == null ) return;
		synchronized( im ) {
			Graphics2D g = (Graphics2D)im.getGraphics();
       g.setRenderingHint( RenderingHints.KEY_RENDERING,
			 	RenderingHints.VALUE_RENDER_SPEED );
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
    System.out.println("Resized Scale = " + scale );
    repaint();
    /*
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
    */
	}

	/** Paint to the buffered image */
 /*	private Image paintBuffered( int w, int h, boolean antialias ) {
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
	}*/

  public void moveMap(int x1, int y1, int x2, int y2){
         System.out.println("Entering Move Map");
          Graphics2D g = (Graphics2D)this.getGraphics();
          final int w = getWidth();
		      if( w == 0 ) return;
		      final int h = getHeight();
		      if( h == 0 ) return;
          int dx = x2 - x1;
          int dy = y2 - y1;
          g.copyArea(0,0,w,h,dx,dy);
          g.setClip(0,0,w, h);
          g.setColor(Color.cyan);
          Rectangle2D rect1 = new Rectangle2D.Double();
          Rectangle2D rect2 = new Rectangle2D.Double();
          Polygon clipper = null;
          System.out.println("dx = " + dx +  " dy = " + dy);
          System.out.println("w= " + w + " h = " +h);
          if ((dx >=0)&&(dy >=0)){
             rect1.setRect(0,0,w,dy);
             rect2.setRect(0,0,dx,h-dy);
             g.clearRect(0,0,w,dy);
             g.clearRect(0,0,dx,h-dy);
             clipper = new Polygon(new int[]{0,w,w,dx,dx,0},new int[]{0,0,dy,dy,h,h},6);
          }
          else if ((dx < 0) && ( dy < 0)){  //fucked up
               g.clearRect(0,h+dy,w,-dy);
               g.clearRect(w+dx,0,-dx,h+dy);
               clipper = new Polygon(new int[]{w+dx,w,w,0,0,w+dx},new int[]{0,0,h,h,h+dy,h+dy},6);
          }
          else if (( dx <= 0) && ( dy >= 0)){
                System.out.println("3rd else");
               g.clearRect(0,0,w,dy);
               g.clearRect(w+dx,dy,-dx,h-dy);
               clipper = new Polygon(new int[]{0,w,w,w+dx,w+dx,0},new int[]{0,0,h,h,dy,dy},6);
          }
          else if (( dx >= 0 ) && ( dy <= 0 )){//fucked up
               g.clearRect(0,0,dx,h);
               g.clearRect(dx,h+dy,w-dx,-dy);
               clipper = new Polygon(new int[]{0,dx,dx,w,w,0},new int[]{0,0,h+dy,h+dy,h,h},6);
          }
          g.setClip(clipper);
          //g.setColor(Color.cyan);
          //g.fillRect(0,0,w,h);
          AffineTransform world;
          try { world = at.createInverse(); }
          catch( NoninvertibleTransformException ex ) { return; }
          Point2D p1 = new Point2D.Double(x1,y1);
          world.transform(p1, p1);
          Point2D p2 = new Point2D.Double( x2, y2 );
          world.transform( p2, p2 );
          //System.out.println("Before");
          //System.out.println("Width = " + w + " Height = " + h);
          //System.out.println("MinX = " + minX + " MaxX = " + maxX + " MinY = " + minY + " MaxY = " + maxY);
          setBoundingBox(minX+p1.getX()-p2.getX(),minY+p1.getY()-p2.getY(),maxX+p1.getX()-p2.getX(),maxY+p1.getY()-p2.getY());
          //System.out.println("After");
          //System.out.println("Width = " + w + " Height = " + h);
          //System.out.println("MinX = " + minX + " MaxX = " + maxX + " MinY = " + minY + " MaxY = " + maxY);
          //resized();

          Image image = createImage(w,h);
          Graphics2D buf = (Graphics2D)image.getGraphics();
          buf.setClip(clipper);
		      double scaleX = (double)w / width;
		      double scaleY = (double)h / height;
          //System.out.println("w = " + w + " h = " + h +" Width = " + width + " Height = " + height);
          //System.out.println("ScaleX = " + scaleX + " ScaleY = " + scaleY);
		      double scale = scaleX;
		      if( scale > scaleY ) scale = scaleY;
		      at.setToTranslation( -( minX * scale ), maxY * scale );
		      at.scale( scale, -scale );
          //System.out.println("Moved Scale = " + scale );
          /** repaint layers */
          //g.transform( at );
          buf.transform(at);

          for( int i = layers.size()-1; i >= 0; i-- ) {
			         Layer layer = (Layer)layers.get( i );
			         layer.paint( buf );
          }
          g.drawImage(image,0,0,null);

  }

	/** Add a new layer to the ShapePane */
	public void addLayer( Layer layer ) {
    layers.add( layer );
    if(layers.size()==1){
      mainLayer = layer;
  		setBoundingBox( layer.minX, layer.minY, layer.maxX, layer.maxY );
    }
	}

	/** Overridden to prevent flashing */
	public void update( Graphics g ) {
		paint( g );
	}


	/** Paint the shapes */
 /*	public void paint( Graphics g ) {
		Image im = image;
		if( im == null ) return;
		synchronized( im ) {
			g.drawImage( im, 0, 0, null );
		}
		if( mouse.box ) drawBox( mouse.rect );
	}*/

   public void paint ( Graphics g) {
    Graphics2D g2D = (Graphics2D)g;
    final int w = getWidth();
    final int h = getHeight();
    g2D.clearRect( 0, 0, w, h );
		g2D.transform( at );
    for( int i = (layers.size()-1); i >= 0; i-- ) {
		 Layer layer = (Layer)layers.get( i );
		 layer.paint( g2D );
    }
     g2D.dispose();
  }
}
