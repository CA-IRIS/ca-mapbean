
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description
package us.mn.state.dot.shape;

import java.util.Vector;
import java.util.ListIterator;
import java.awt.geom.*;
import java.awt.*;

public abstract class AbstractLayer implements Layer {

	public AbstractLayer() {
	}

	Rectangle2D.Double extent = null;

	public Rectangle2D getExtent() {
		return extent;
	}

	/*public void paint(Graphics2D g) {
		//TODO: implement this us.mn.state.dot.shape.Layer method;
	}

	public Vector hit(Point2D p) {
		//TODO: implement this us.mn.state.dot.shape.Layer method;
	}

	public String getTip(Point2D p) {
		//TODO: implement this us.mn.state.dot.shape.Layer method;
	} */

	private boolean visible = true;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean b) {
		visible = b;
		repaintLayer(this);
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	private Vector layerListeners = new Vector();

	public void addLayerListener(LayerListener l) {
		if (!layerListeners.contains(l)) {
			layerListeners.add(l);
		}
	}

	public void updateLayer(Layer l) {
		ListIterator it = layerListeners.listIterator();
		while (it.hasNext()){
			((LayerListener)it.next()).updateLayer(l);
		}
	}

	public void repaintLayer(Layer l) {
		ListIterator it = layerListeners.listIterator();
		while (it.hasNext()){
			((LayerListener)it.next()).repaintLayer(l);
		}
	}
}