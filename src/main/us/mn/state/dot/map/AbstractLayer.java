package us.mn.state.dot.shape;

import java.util.*;
import java.awt.geom.*;
import java.awt.*;

/** 
 * The AbstractLayer implements much of the functionality of the Layer
 * interface.
 *
 * @author Erik Engstrom
 * @since 1.0
 */
public abstract class AbstractLayer implements Layer {
	/** extent of layer */
	protected Rectangle2D.Double extent =new Rectangle2D.Double();

	private boolean visible =true;

	private String name;

	private java.util.List layerListeners =new ArrayList();

	private boolean dynamic =false;

	/** gets the extent of the layer
	 * @return the extent of the layer.
	 */
	public Rectangle2D getExtent() {
		return extent;
	}

	/** returns true if the layer is visible
	 * @return returns true if the layer is visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/** Shows or hides this layer depending on the value of parameter b
	 * @param b boolean determining if the layer is visible or not
	 */
	public void setVisible(boolean b) {
		visible = b;
		repaintLayer();
	}

	/**
	 * Returns a string contining the name of the layer.
	 * @return String containing the name of the layer
	 */
	public String getName() {
		return name;
	}

	/** Sets the name of the layer.
	 * @param s String containing the name of the layer
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * Adds a LayerListener to the layer that is notified when the layer's data is
	 * changed.
	 * @param l LayerListener to be added to the layer
	 */
	public void addLayerListener(LayerListener l) {
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

	/**
	 * Repaints the Layer.  Should we get rid of this method??????????
	 */
	public void repaintLayer() {
		ListIterator it = layerListeners.listIterator();
		while ( it.hasNext() ){
			(( LayerListener ) it.next() ).refresh( this );
		}
	}

	/**
	 * paints the selected objects of this layer
	 * @param g Graphics object to be painted on.
	 */
	public void paintSelections(Graphics2D g) {
	}

	/** Is this layer static?
	 * @return true if data is not dynamic
	 */
	public boolean isStatic() {
		return ! dynamic;
	}

	/** if set to false layer is static and the data will not change.  Static layers
	 * are painted behind non-static (dynamic) layers on a map.  If it is desired for
	 * a static layer to be higher in the map it setStatic() must be set to true.
	 * @param b true - layer is static and data will not be update; layer is painted behind any
	 * non-static layers.
	 * false - layer is dynamic and layer will change if the layer's data is changed;
	 * will be painted in front of any static layers
	 */
	public void setStatic(boolean b) {
		dynamic = ! b;
	}


}