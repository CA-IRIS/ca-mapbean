
//Title:        IncidentLayer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Displays incidents as icons on map.

package us.mn.state.dot.shape;

import us.mn.state.dot.dds.client.*;
import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.ListSelectionModel;

public final class IncidentLayer implements Layer, IncidentListener {
	private Vector layerListeners = new Vector();
	private Incident [] incidents = null;
	private Rectangle2D.Double extent = null;
	private ListSelectionModel selectionModel = null;
	private boolean visible = true;

	public Rectangle2D getExtent(){
		return extent;
	}

	public String getName(){
		return new String("incidents");
	}

	public IncidentLayer() {
		this(null);
	}

	public IncidentLayer(ListSelectionModel m) {
		selectionModel = m;
	}

	public ListSelectionModel getSelectionModel(){
		return selectionModel;
	}

	public void setSelectionModel(ListSelectionModel selectionModel){
		this.selectionModel = selectionModel;
	}

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


	public void update(Incident[] incidents){
		this.incidents = incidents;
		/* Set Extent */
		double maxX = incidents[0].getX();
		double maxY = incidents[0].getY();
		double minX = maxX;
		double minY = maxY;
		for (int i = 1; i < incidents.length; i++) {
			if (incidents[i].getX() < minX) {
				minX = incidents[i].getX();
			} else if (incidents[i].getX() > maxX) {
				maxX = incidents[i].getX();
			}
			if (incidents[i].getY() < minY) {
				minY = incidents[i].getY();
			} else if (incidents[i].getY() > maxY) {
				maxY = incidents[i].getY();
			}
		}
		extent = new Rectangle2D.Double(minX, minY, (maxX - minX)
			, (maxY - minY));
		updateLayer(this);
	}

	public void paint(Graphics2D g){
		if (visible) {
			if(incidents != null) {
				for(int i = 0; i < incidents.length; i++){
					incidents[i].paint(g);
				}
			}
		}
	}

	public Vector hit(Point2D p){
		double x = 0;
		double y = 0;
		Vector result = new Vector();
		if (selectionModel != null) {
			selectionModel.clearSelection();
		} else {
			System.out.println("Selection model is null");
		}

		if (incidents != null) {
			for( int i = 0; i < incidents.length; i++ ) {
				x = incidents[i].getX();
				y = incidents[i].getY();
				Rectangle2D r = new Rectangle2D.Double((x - 500),
					(y - 500), 1000, 1000 );
				if(r.contains(p)) {
					result.add(incidents[i]);
					if (selectionModel != null) {
						selectionModel.addSelectionInterval(i,i);
					}
				}
			}
		}
		return result;
	}

	public String getTip(Point2D p){
		Vector v = hit(p);
		String result = null;
		ListIterator li = v.listIterator();
		if (li.hasNext()){
			result = ((Incident) li.next()).toString();
		}
		return result;
	}

	public void setVisible(boolean b) {
		visible = b;
		repaintLayer(this);
	}

	public boolean isVisible() {
		return visible;
	}
}
