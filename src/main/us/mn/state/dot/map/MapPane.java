
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

public final class MapPane extends JPanel {

	/** Main Layer (drawn on top, bounds set) */
	private Layer mainLayer;

	/** Array to hold background layer information */
	private final ArrayList layers = new ArrayList();

	public ArrayList getLayers(){
		return layers;
	}

	/** Transformation to draw shapes in the ShapePane */
	private final AffineTransform at = new AffineTransform();

	AffineTransform getTransform(){
		return at;
	}

	/** Optional viewport */
	private Map viewport;

	/** Bounding box */
	private Rectangle2D extent;

	/** Scale factor */
	private double scale;
	private double shiftX;
	private double shiftY;

	double getShiftX(){
		return shiftX;
	}

	double getShiftY(){
		return shiftY;
	}

	/** Returns width of main layer */
	public double getMapWidth() {
		return extent.getWidth() * scale;
	}

	/** Retruns height of main layer */
	public double getMapHeight() {
		return extent.getHeight() * scale;
	}

	/** Returns layer at index l */
	public Layer getLayer(int l) {
		return (Layer) layers.get(l);
	}

	public Layer getLayer(String name) {
		Layer result = null;
		ListIterator li = layers.listIterator();
		while (li.hasNext()) {
			Layer temp = (Layer) li.next();
			if (name.equals(temp.getName())) {
				result = temp;
				break;
			}
		}
		return result;
	}

	/** Set the bounding box for display */
	public void setExtent(Rectangle2D r){
		extent = r;
		resized();
		repaint();
	}

	/** Constructor */
	public MapPane(){
		this(null);
	}

	public MapPane(Map vp) {
		viewport = vp;
		addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e ) {
				resized();
				repaint();
			}
		});
	}

	/** Called when the ShapePane is resized */
	public void resized() {
		if (!layers.isEmpty()) {
			final int w = getWidth();
			if ( w == 0 ) {
				return;
			}
			final int h = getHeight();
			if ( h == 0 ) {
				return;
			}
			double scaleX = (double) w / extent.getWidth();
			double scaleY = (double) h / extent.getHeight();
			double scaleTemp = scaleX;
			shiftX = 0;
			shiftY = (h - (extent.getHeight() * scaleTemp)) / 2;
			if ( scaleTemp > scaleY ) {
				scaleTemp = scaleY;
				shiftY = 0;
				shiftX = (w - (extent.getWidth() * scaleTemp)) / 2;
			}
			scale = scaleTemp;
			at.setToTranslation( -( extent.getMinX() * scale )
				+ shiftX, (extent.getMaxY() * scale) + shiftY );
			at.scale( scale, -scale );
		}
	}

	/** Add a new layer to the ShapePane */
	public void addLayer( Layer layer ) {
		layers.add( layer );
		setExtent( layer.getExtent());
	}

	/** Overridden to prevent flashing */
	public void update( Graphics g ) {
		paint( g );
	}

	public void refreshLayer(int index){
		if (this.isShowing()){
			Graphics2D g = (Graphics2D) this.getGraphics();
			if (g == null) {
				return;
			}
			g.transform( at );
			ListIterator it = layers.listIterator(index + 1);
			while (it.hasPrevious()){
				((Layer) it.previous()).paint(g);
			}
		}
	}

	public void refreshLayer(Layer l){
		if (this.isShowing()){
			Graphics2D g = (Graphics2D) this.getGraphics();
			if (g != null){
				g.transform( at );
				ListIterator it = layers.listIterator(layers.lastIndexOf((Object) l));
				if (it.hasNext ()) {
					it.next();
				}
				while (it.hasPrevious()){
					((Layer) it.previous()).paint(g);
				}
			}
		}
	}

	public void refresh(){
		if (this.isShowing()){
			Graphics g = this.getGraphics();
			paint(g);
		}
	}


	/** Paint the shapes */
	public void paint ( Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		final int w = getWidth();
		final int h = getHeight();
		g2D.clearRect( 0, 0, w, h );
		g2D.transform( at );
		for ( int i = (layers.size() - 1); i >= 0; i-- ) {
			Layer layer = (Layer)layers.get( i );
			if (layer.isVisible()){
				layer.paint( g2D );
			}
		}
	}
}
