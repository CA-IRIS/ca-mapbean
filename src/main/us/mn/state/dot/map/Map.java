
//Title:        Map
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import us.mn.state.dot.dds.client.*;

public final class Map extends JViewport implements LayerListener {

	/** static variables for mouse behavior */
	public static final int NONE = 0;
	public static final int SELECT = 1;
	public static final int ZOOM = 2;
	public static final int PAN = 3;

	/** Map panel */
	private MapPane map = new MapPane(this);

	public Graphics2D getMapGraphics(){
		Graphics2D g = (Graphics2D) map.getGraphics();
		g.transform(map.getTransform());
		return g;
	}

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();

	/** Constructor */
	public Map() {
		this(new Vector());
	}

	public Map(Vector layers){
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
		this.setView(map);
		this.setToolTipText("");
		ListIterator li = layers.listIterator();
		while (li.hasNext()) {
			addLayer((Layer) li.next());
		}
		setMouseAction(SELECT);
	}

	/** holds current mouse behavior*/
	private int mouseAction = SELECT;

	/** Sets the action occuring on mouse events */
	public void setMouseAction(int m){
		switch (m) {
		case NONE: case ZOOM: case PAN:
			mouseAction = m;
			this.setToolTipText(null);
			break;
		case SELECT:
			mouseAction = m;
			this.setToolTipText("");
			break;
		default:
			return;
		}
	}

	/** Gets the action occuring on mouse events */
	public int getMouseAction(){
		return mouseAction;
	}

	public Layer getLayer(String name){
		return map.getLayer(name);
	}

	/** Sets extent to given coordinates  */
	public void home(){
		map.setPreferredSize(this.getSize());
		this.revalidate();
	}

	/** Add a new layer to the ShapePane */
	public void addLayer( Layer layer ) {
		map.addLayer(layer);
		layer.addLayerListener(this);
	}

	public void refresh(){
		map.repaint();
	}

	public void refreshLayer(int index){
		map.refreshLayer(index);
	}

	public void refreshLayer(Layer l){
		map.refreshLayer(l);
	}

	/** Draw an XOR box (rubberbanding box) */
	void drawBox( Rectangle r ) {
		Graphics g = getGraphics();
		if ( g == null ) {
			return;
		}
		g.setXORMode( Color.white );
		g.drawRect( r.x, r.y, r.width, r.height );
	}

	/** Pan to point on map */
	void panTo(Point p){
		Point scrollTo = p;

		if ((scrollTo.getX() + this.getWidth()) > map.getWidth()) {
			scrollTo.x = map.getWidth() - this.getWidth();
		}
		if ((scrollTo.getY() + this.getHeight()) > map.getHeight()) {
			scrollTo.y = map.getHeight()- this.getHeight();
		}
		if (scrollTo.getX() < 0) {
			scrollTo.x = 0;
		}
		if (scrollTo.getY() < 0) {
			scrollTo.y = 0;
		}
		this.setViewPosition(scrollTo);
	}

	public String getToolTipText(MouseEvent e) {
		String strings = null;
		AffineTransform t = map.getTransform();
		AffineTransform world = null;
		try {
			world = t.createInverse();
		} catch (NoninvertibleTransformException ex) {
			ex.printStackTrace();
		}
		double xCoord = e.getPoint().getX();
		double yCoord = e.getPoint().getY();
		Point2D viewPosition = this.getViewPosition();
		Point2D p1 = new Point2D.Double(xCoord + viewPosition.getX(),
			yCoord + viewPosition.getY());
		Point2D p = world.transform(p1, new Point(0, 0));
		ArrayList layers = map.getLayers();
		ListIterator it = layers.listIterator();
		while (it.hasNext()){
			Layer l = (Layer) it.next();
			strings = l.getTip(p);
			if (strings != null) {
				break;
			}
		}
		return strings;
	}

	public JToolTip createToolTip(){
		return new JMultiLineToolTip();
	}

	/** Mouse helper class */
	private final MouseHelper mouse = new MouseHelper((JViewport) this);

	public void updateLayer(Layer l) {
		//if (this.isShowing()) {
			map.refreshLayer(l);
		//}
	}

	public void repaintLayer(Layer l) {
		//if (this.isShowing()){
			map.refresh();
		//}
	}

	public void setExtent(Rectangle2D r){
		map.setExtent(r);
	}

	/** Inner class to take care of mouse events */
	private class MouseHelper extends MouseAdapter implements MouseMotionListener {
		boolean box = false;
		int x1;
		int y1;
		int x2;
		int y2;
		Rectangle rect = new Rectangle();
		private Point last = new Point();
		private Point scrollTo = new Point();
		private JViewport viewport;

		public MouseHelper (JViewport viewport){
			this.viewport = viewport;
		}

		public void mousePressed( MouseEvent e ) {
			last = e.getPoint();
			x1 = e.getX();
			y1 = e.getY();
			box = false;
		}

		public void mouseReleased( MouseEvent e ) {
			if (SwingUtilities.isLeftMouseButton(e)){
				switch (mouseAction){
				//Mouse Select
				case SELECT:
					break;
				//Mouse Zoom
				case ZOOM:
					if ( box ) {
						box = false;
						y2 = e.getY();
						x2 = e.getX();
						int w;
						int h;
						int temp;
						if (x1 > x2){
							temp = x1;
							x1 = x2;
							x2 = temp;
						}
						w = x2 - x1;
						if (y1 > y2) {
							temp = y1;
							y1 = y2;
							y2 = temp;
						}
						h = y2 - y1;

						//Calculate the new size of the panel
						double oldWidth = map.getMapWidth();
						double oldHeight = map.getMapHeight();
						double ratioMap = oldWidth/oldHeight;
						double width = 0;
						double height = 0;
						double viewWidth = viewport.getViewRect().getWidth();
						double viewHeight = viewport.getViewRect().getHeight();
						if (w >= h) {
							width = oldWidth * (viewWidth / w);
							height = width / ratioMap;
						} else if (h > w) {
							height = oldHeight * (viewHeight / h);
							width = height * ratioMap;
						}

						//Set the new size of the panel
						Point viewLocation = viewport.getViewPosition();
						map.setMinimumSize(new Dimension((int) width,
							(int) height));
						map.setPreferredSize(new Dimension((int) width,
							(int) height));
						map.setSize((int) width, (int) height);

						//Scroll to the center of the zoom rectangle
						viewWidth = viewport.getViewRect().getWidth();
						viewHeight = viewport.getViewRect().getHeight();
						double newX1 = ((x1 + viewLocation.getX()
							- map.getShiftX()) * (map.getWidth() / oldWidth));
						double newY1 = ((y1 + viewLocation.getY()
							- map.getShiftY()) * (map.getHeight() / oldHeight));
						double newX2 = ((x2 + viewLocation.getX()
							- map.getShiftX()) * (map.getWidth() / oldWidth));
						double newY2 = ((y2 + viewLocation.getY()
							- map.getShiftY()) * (map.getHeight() / oldHeight));
						double newBoxWidth = newX2-newX1;
						double newBoxHeight = newY2-newY1;
						Point pan = new Point((int) (newX1
							- ((viewWidth - newBoxWidth) / 2)),
							(int) (newY1 - ((viewHeight - newBoxHeight) / 2)));
						panTo(pan);
					}
					break;
					//Mouse Pan
					case PAN:
						break;
					}
				}
			}

		public void mouseClicked(MouseEvent e){
			if (SwingUtilities.isRightMouseButton(e)){
				switch (mouseAction){
				case SELECT:
					break;
				case ZOOM:
					Double width = new Double(map.getSize().getWidth() / 1.5);
					Double height = new Double(map.getSize().getHeight() / 1.5);
					map.setMinimumSize(new Dimension(width.intValue(),
						height.intValue()));
					map.setPreferredSize(new Dimension(width.intValue(),
						height.intValue()));
					map.setSize(width.intValue(), height.intValue());
					break;
				case PAN:
					break;
				}
			} else if (SwingUtilities.isLeftMouseButton(e)) {
				switch (mouseAction){
				case SELECT:
					Graphics2D g = (Graphics2D) map.getGraphics();
					AffineTransform t = map.getTransform();
					AffineTransform world;
					try {
						world = t.createInverse();
					} catch (NoninvertibleTransformException ex) {
						ex.printStackTrace();
						return;
					}
					double pointX = e.getPoint().getX();
					double pointY = e.getPoint().getY();
					Point2D viewPosition = viewport.getViewPosition();
					Point2D p1 = new Point2D.Double(pointX +
						viewPosition.getX(), pointY + viewPosition.getY());
					Point2D p = world.transform(p1, new Point(0, 0));
					ArrayList layers = map.getLayers();
					ListIterator it = layers.listIterator();
					g.setTransform(t);
					boolean found = false;
					while (it.hasNext()){
						Layer l = (Layer) it.next();
						found = l.mouseClick(e.getClickCount(), p, g);
						if ( found ) {
							break;
						}
					}
				case ZOOM:
					break;
				case PAN:
					break;
				}
			}
		}

		public void mouseDragged( MouseEvent e ) {
			if (SwingUtilities.isLeftMouseButton(e)){
				x2 = e.getX();
				y2 = e.getY();
				switch (mouseAction){
				case SELECT:
					break;
				case ZOOM:
					if ( box ){
						drawBox( rect );
					}
					rect.x = x1;
					if ( x2 < x1 ) {
						rect.x = x2;
					}
					rect.width = x2 - x1;
					if ( rect.width < 0 ) {
						rect.width = -rect.width;
					}
					rect.y = y1;
					if ( y2 < y1 ) {
						rect.y = y2;
					}
					rect.height = y2 - y1;
					if ( rect.height < 0 ) {
						rect.height = -rect.height;
					}
					drawBox( rect );
					box = true;
					break;
				case PAN:
					Point viewPos = viewport.getViewPosition();
					Point offset = new Point((x2 - last.x),
						(y2 - last.y));
					last.x = x2;
					last.y = y2;
					scrollTo.x = viewPos.x - offset.x;
					scrollTo.y = viewPos.y - offset.y;
					panTo(scrollTo);
					break;
				}
			}
		}

		public void mouseMoved( MouseEvent e ) {
			if ( box ) {
				box = false;
			}
		}
	}
}
