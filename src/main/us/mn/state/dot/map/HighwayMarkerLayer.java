/*
 * HighwayMarkerLayer.java
 *
 * Created on April 18, 2000, 5:25 PM
 */

package us.mn.state.dot.shape;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import us.mn.state.dot.tms.toast.Icons;
import java.net.*;
import java.util.*;

/** 
 *
 * @author  engs1eri
 * @version 
 */
public final class HighwayMarkerLayer extends ShapeLayer {
	
	private final Field highways;
	
  	/** Creates new HighwayMarkerLayer */
  	public HighwayMarkerLayer() throws IOException {
		super( "gpoly/markers", "markers" );
		highways = this.getField( "highway" );
	}
	
	public void paint( Graphics2D g, LayerRenderer renderer ) {
		for ( int i = ( paths.length - 1 ) ; i >= 0; i-- ){
			//Point2D point = paths[ i ];
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
		String resource = "/images/HighWayMarkers/" + key + ".png";
		URL url = this.getClass().getResource( resource );
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