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
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import us.mn.state.dot.shape.shapefile.ShapeLayer;
import us.mn.state.dot.shape.shapefile.ShapeObject;

/** 
 * This layer is used for displaying highway markers on the map.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.12 $ $Date: 2003/05/19 21:23:41 $
 */
public final class HighwayMarkerLayer extends ShapeLayer {
	
	/** Location of the shape file */
	private static final String FILE_LOCATION = "gpoly/markers";
	
	/** Name of the layer */
	private static final String LAYER_NAME = "markers";
	
	/** Field that contains the name of the highway. */
	private static final String LOOKUP_FIELD = "HIGHWAY";
	
	/** Location of the image files.*/
	private static final String IMAGE_LOCATION = "/images/HighWayMarkers/";
	
  	/** Creates new HighwayMarkerLayer */
  	public HighwayMarkerLayer() throws IOException {
		super( FILE_LOCATION, LAYER_NAME );
	}
	
	/** Create a new Highway MarkerLayer */
	public HighwayMarkerLayer( URL fileLocation ) throws IOException {
		super( fileLocation, LAYER_NAME );
	}
	
	public void paint( Graphics2D g, LayerRenderer renderer ) {
		for ( int i = ( shapes.length - 1 ) ; i >= 0; i-- ){
			ShapeObject shape = shapes[ i ];
			double xCoord = shape.getShape().getBounds().getX();
			double yCoord = shape.getShape().getBounds().getY();
			ImageIcon icon = getIcon( ( String ) shape.getValue( 
				LOOKUP_FIELD ) );
			if ( icon == null ) {
				continue;
			}
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF );
			g.drawImage( icon.getImage(), ( ( int ) xCoord - 1000 ),
				( ( int ) yCoord + 1000 ), 2000, -2000,
				icon.getImageObserver() );
		}
	}
	
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			MapObject[] selections ) {
	}

	/** Cache of ImageIcons */
	private final Map map = new HashMap();

	/** Get a requested ImageIcon resource */
	private final ImageIcon getImageIcon( String key ) {
		ImageIcon icon = ( ImageIcon ) map.get( key );
		if ( icon != null ) return icon;
		String resource = IMAGE_LOCATION + key + ".gif";
		URL url = this.getClass().getResource( resource );
		if ( url == null ) {
			resource = IMAGE_LOCATION + key + ".png";
			url = this.getClass().getResource( resource );
		}
		if ( url != null ) {
			icon = new ImageIcon( url );
			map.put( key, icon );
		}
		if ( icon == null ) {
			System.err.println( "Can't find image " + resource );
		}
		return icon;
	}

	/** Get an icon from a string name */
	private final ImageIcon getIcon( String key ) {
		return getImageIcon( key );
	}

}
