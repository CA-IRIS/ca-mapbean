
//Title:        SignClient
//Version:
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public abstract class AbstractLayer implements Layer {

	/** extent of layer */
	Rectangle2D.Double extent = null;

    /** get the extent of the layer */
	public Rectangle2D getExtent() {
		return extent;
	}

	private boolean visible = true;

	/** returns true if the layer is visible */
	public boolean isVisible() {
		return visible;
	}

	/** Shows or hides this layer depending on the value of parameter b */
	public void setVisible( boolean b ) {
		visible = b;
		repaintLayer();
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName( String s ) {
		name = s;
	}

	private java.util.List layerListeners = new ArrayList();

	public void addLayerListener( LayerListener l ) {
		if ( ! layerListeners.contains( l ) ) {
			layerListeners.add( l );
		}
	}

	public void updateLayer() {
		ListIterator it = layerListeners.listIterator();
		while ( it.hasNext() ){
			(( LayerListener ) it.next() ).updateLayer( this );
		}
	}

   public void repaintLayer() {
		ListIterator it = layerListeners.listIterator();
		while ( it.hasNext() ){
			(( LayerListener ) it.next() ).refresh();
		}
	}
}