/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package us.mn.state.dot.shape;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This interface should be used to add data to a MapBean object. 
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 0.23 $ $Date: 2003/05/07 15:28:57 $ 
 */
public interface Layer {
	
	/**
	 * Get the extent of this layer.
	 */
    public Rectangle2D getExtent();
	
	/**
	 * Paint this layer.
	 */
	public void paint( Graphics2D g, LayerRenderer r );
	
	/**
	 * Paint selected objects on this layer.
	 */
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
		MapObject[] selections );
	
	/**
	 * Get the name of this layer.
	 */
	public String getName();
	
	/**
	 * Register a LayerChangedListener with this layer.
	 */
	public void addLayerChangedListener( LayerChangedListener listener );
	
	/**
	 * Remove a layerChangedListener from this layer.
	 */
	public void removeLayerChangedListener( LayerChangedListener listener );
	
	/**
	 * Notify all LayerChangedListeners that this layer has changed.
	 */
	public void notifyLayerChangedListeners( LayerChangedEvent event );
	
	/**
	 * Is this a static layer?
	 */
	public boolean isStatic();
	
	/**
	 * Set this layer as a static layer. 
	 */
	public void setStatic( boolean b );
	
	/**
	 * Gets all of the paths present at the Point2D p in this layer.
	 * @param p The Point2D at which to search.
	 * @return a java.util.List containing all of the paths found.
	 */
	public java.util.List getPaths( Point2D p, LayerRenderer renderer );
	
	/**
	 * Returns an int representing the index of the first path found that
	 * is contained by, intersects, or contains the Rectangle2D searchArea.
	 * @param searchArea Rectangle2D representing the area to search.
	 * @return an int representing the index of the first found path, returns
	 * -1 if nothing found.
	 */
	public MapObject search( Rectangle2D searchArea, LayerRenderer renderer );
	
	/**
	 * Returns an int representing the index of the first path found that
	 * is contained by, intersects, or contains the Point2D searchArea.
	 * @param p Point2D representing the point to search.
	 * @return an int representing the index of the first found path, returns
	 * -1 if nothing found.
	 */
	public MapObject search( Point2D p, LayerRenderer renderer );
	
	/**
	 * Get the theme for this layer.
	 */
	public Theme getTheme();
}
