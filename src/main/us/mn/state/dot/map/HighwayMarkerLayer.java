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

/*
 * HighwayMarkerLayer.java
 *
 * Created on April 18, 2000, 5:25 PM
 */

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
//import us.mn.state.dot.tms.toast.Icons;

/** 
 *
 * @author  erik.engstrom@dot.state.mn.us
 * @version 
 */
public final class HighwayMarkerLayer extends ShapeLayer {
	
	private final Field highways;
	
	private static final String FILE_LOCATION = "/gpoly/markers.shp";
	private static final String LAYER_NAME = "markers";
	private static final String LOOKUP_FIELD = "highway";
	private static final String IMAGE_LOCATION = "/images/HighWayMarkers/";
	
  	/** Creates new HighwayMarkerLayer */
  	public HighwayMarkerLayer() throws IOException {
		super( FILE_LOCATION, LAYER_NAME );
		highways = this.getField( LOOKUP_FIELD );
	}
	
	public HighwayMarkerLayer( URL fileLocation ) throws IOException {
		super( URL fileLocation, LAYER_NAME );
		highways = this.getField( LOOKUP_FIELD );
	}
	
	public void paint( Graphics2D g, LayerRenderer renderer ) {
		for ( int i = ( paths.length - 1 ) ; i >= 0; i-- ){
			double xCoord = paths[ i ].getBounds().getX();
			double yCoord = paths[ i ].getBounds().getY();
			ImageIcon icon = getIcon( highways.getStringValue( i ) );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF );
			g.drawImage( icon.getImage(), ( ( int ) xCoord - 1000 ),
				( ( int ) yCoord + 1000 ), 2000, -2000,
				icon.getImageObserver() );
		}
	}
	
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			ArrayList selections ) {
	}

	/** Cache of ImageIcons */
	private final HashMap map = new HashMap();

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
		return icon;
	}

	/** Get an icon from a string name */
	private final ImageIcon getIcon( String key ) {
		return getImageIcon( key );
	}

	/** Get an image from a string name */
	private final Image getImage( String key ) {
		ImageIcon icon = getImageIcon( key );
		if( icon == null ) return null;
		return icon.getImage();
	}
}