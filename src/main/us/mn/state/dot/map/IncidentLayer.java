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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import us.mn.state.dot.dds.client.*;
import us.mn.state.dot.shape.event.*;

/**
 * Displays incidents as icons on map.
 *
 * @author erik.engstrom@dot.state.mn.us
 * @version $Revision: 1.34 $ $Date: 2001/04/20 15:52:03 $
 */
public class IncidentLayer extends AbstractLayer implements
		IncidentListener {

	protected Incident [] incidents = null;
	protected ListSelectionModel selectionModel =
		new DefaultListSelectionModel();
	private boolean directional = true;

	/* the number of map units per mile */
	private static final int MAP_UNITS_PER_MILE = 3218;
	
	private static final int[] RING_DEFAULTS = { 5, 10, 15 };

	public IncidentLayer(){
		setStatic( false );
		setName( "incidents" );

	}

	public void setSelectionModel( ListSelectionModel selectionModel ){
		if ( selectionModel == null ) {
			throw new NullPointerException( "selectionModel can not be null" );
		} else {
			this.selectionModel = selectionModel;
		}
	}

	public synchronized void update( Incident[] incidents ){
		this.incidents = incidents;
		if ( incidents.length > 0 ){
			double maxX = incidents[ 0 ].getX();
			double maxY = incidents[ 0 ].getY();
			double minX = maxX;
			double minY = maxY;
			for ( int i = 1; i < incidents.length; i++ ) {
				if ( incidents[ i ].getX() < minX ) {
					minX = incidents[ i ].getX();
				} else if ( incidents[ i ].getX() > maxX ) {
					maxX = incidents[ i ].getX();
				}
				if ( incidents[ i ].getY() < minY ) {
					minY = incidents[ i ].getY();
				} else if ( incidents[ i ].getY() > maxY ) {
					maxY = incidents[ i ].getY();
				}
			}
			extent = new Rectangle2D.Double( minX, minY, ( maxX - minX ),
				( maxY - minY ) );
			//SwingUtilities.invokeLater( new NotifyThread( this,
			//	LayerChangedEvent.DATA ) );
            notifyLayerChangedListeners( new LayerChangedEvent( this,
				LayerChangedEvent.DATA ) );
		}
	}
	
	/**
	 * Class for notifying gui of changes in the eventdispatch thread.
	 */
	private final class NotifyThread implements Runnable {
		
		private final IncidentLayer layer;
		private final int type;
		
		public NotifyThread( IncidentLayer layer, int type ) {
			this.layer = layer;
			this.type = type;
		}
		
		public void run() {
			layer.notifyLayerChangedListeners( new LayerChangedEvent( layer,
				type ) );
		}
	}

	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			int[] selection ) {
		selectionModel.clearSelection();
		int[] diameters = new int[ 3 ];
		diameters = getDefaultDiameters();
	}
	
	private int[] getDefaultDiameters(){
		int[] result = new int[ 3 ];
		for ( int i = 0; i < 3; i++ ){
			result[ i ] = RING_DEFAULTS[ i ] * MAP_UNITS_PER_MILE;
		}
		return result;
	}
	
	private void drawEllipses( Graphics2D g, Incident incident,
		int[] diameters) {
		for ( int i = 0; i < 3; i++ ){
			g.draw( new Ellipse2D.Double( ( incident.getX() -
				( diameters[ i ] / 2 ) ), ( incident.getY() -
				( diameters[ i ] / 2 ) ), diameters[ i ],
				diameters[ i ] ) );
		}
	}
	
	public void paint( Graphics2D g, LayerRenderer renderer ){
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ){
				if ( directional ) {
					incidents[ i ].paintDirectionalIcon( g );
				} else {
					incidents[ i ].paintIncidentIcon( g );
				}
			}
		}
	}

	public java.util.List getPaths( Point2D p ){
		ArrayList result = new ArrayList();
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( p ) ) {
					result.add( incidents[ i ] );
				}
			}
		}
		return result;
	}
	
	private int getIncident( Point2D p ) {
		int result = -1;
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( p ) ) {
					result = i;
					break;
				}
			}
		}
		return result;
	}
	
	public final int search( Rectangle2D searchArea ) {
		int result = -1;
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( searchArea ) || r.intersects( searchArea ) ||
						searchArea.contains( r ) ) {
					result = i;
					break;
				}
			}
		}
		return result;
	}

	public void setDirectional( boolean b ) {
		directional = b;
	}
	
	public Theme getTheme() {
		Theme result = new IncidentTheme( this );
		result.setTip( new MapTip() {
			public String getTip( Layer layer, int i ) {
				String result = null;
				if ( i >= 0 && i < incidents.length ) {
					result = incidents[ i ].toString();
				}
				return result;
			}
		});
		return result;
	}
	
	private class IncidentTheme extends Theme implements MapMouseListener {
		
		public IncidentTheme( Layer layer ) {
			super( layer );
		}
		
		public MapMouseListener getMapMouseListener() {
			return this;
		}
		
		public boolean mouseMoved( final java.awt.event.MouseEvent p1 ) {
			return false;
		}
		
		public java.lang.String[] getMouseModeServiceList() {
			String[] result = { "Select" };
			return result;
		}
		
		public boolean listensToMouseMode( String modeName ) {
			boolean result = false;
			if ( modeName.equals( SelectMouseMode.MODE_ID ) ) {
				result = true;
			}
			return result;
		}
		
		public void mouseExited( final java.awt.event.MouseEvent event ) {
		}
		
		public boolean mousePressed( final java.awt.event.MouseEvent event ) {
			return false;
		}
		
		public boolean mouseDragged( final java.awt.event.MouseEvent event ) {
			return false;
		}
		
		public void mouseMoved() {
		}
		
		public void mouseEntered( final java.awt.event.MouseEvent event ) {
		}
		
		public boolean mouseReleased( final java.awt.event.MouseEvent event ) {
			return false;
		}
		
		public boolean mouseClicked( final java.awt.event.MouseEvent event ) {
			if ( ! this.isVisible() ) {
				return false;
			}
			MapBean map = ( MapBean ) event.getSource();
			AffineTransform at = null;
			try {
				at = map.getTransform().createInverse();
			} catch ( NoninvertibleTransformException ex ){
				return false;
			}
			Graphics2D g = ( Graphics2D ) map.getGraphics();
			g.setTransform( map.getTransform() );
			Point2D point = null;
			point = at.transform( event.getPoint(), point );
			IncidentLayer layer = ( IncidentLayer ) getLayer();
			int index = getIncident( point );
			if ( index == -1 ) {
				setSelections( new int[ 0 ] );
				notifyThemeChangedListeners( new ThemeChangedEvent( this,
					ThemeChangedEvent.SELECTION ) );
				return false;
			} else {
				int[] oldSelections = getSelections();
				if ( oldSelections.length > 0 && index == oldSelections[ 0 ] ) {
					clearSelections();
				} else {
					int[] selections = { index };
					setSelections( selections );
				}
				notifyThemeChangedListeners( new ThemeChangedEvent( this,
					ThemeChangedEvent.SELECTION ) );
				return true;
			}
		}
	}
}