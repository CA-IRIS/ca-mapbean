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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

import us.mn.state.dot.dds.client.Incident;
import us.mn.state.dot.dds.client.IncidentDescription;
import us.mn.state.dot.dds.client.IncidentListener;
import us.mn.state.dot.shape.event.MapMouseListener;
import us.mn.state.dot.shape.event.SelectMouseMode;

/**
 * Displays incidents as icons on map.
 *
 * @author erik.engstrom@dot.state.mn.us
 * @version $Revision: 1.42 $ $Date: 2003/08/27 13:41:22 $
 */
public class IncidentLayer extends AbstractLayer implements
		IncidentListener {

	protected IncidentWrapper [] incidents = null;

	protected ListSelectionModel selectionModel =
		new DefaultListSelectionModel();

	//private boolean directional = true;

    private boolean dirty = true;

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

    /**
     * Called by the incident client every 30 seconds, will only call for a
     * repaint when there is at least one active incident or the first time
     * there are no incidents (to clear the layer).
     */
	public synchronized void update( Incident[] incidents ){
		this.incidents = new IncidentWrapper[ incidents.length ];
		if ( incidents.length > 0 ){
            dirty = true;
			double maxX = incidents[ 0 ].getX();
			double maxY = incidents[ 0 ].getY();
			double minX = maxX;
			double minY = maxY;
			for ( int i = 0; i < incidents.length; i++ ) {
				this.incidents[ i ] = new IncidentWrapper( incidents[ i ] );
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
            notifyLayerChangedListeners( new LayerChangedEvent( this,
				LayerChangedEvent.DATA ) );
		} else if ( dirty ) { //clear layer
            notifyLayerChangedListeners( new LayerChangedEvent( this,
				LayerChangedEvent.DATA ) );
            dirty = false;
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
			MapObject[] selection ) {
		for ( int i = 0; i < selection.length; i++ ) {
			renderer.render( g, selection[ i ] );
		}
	}


	public void paint( Graphics2D g, LayerRenderer renderer ){
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ){
				renderer.render( g, incidents[ i ] );
			}
		}
	}

	public java.util.List getPaths( Point2D p, LayerRenderer renderer ){
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

	public IncidentWrapper[] getIncidents() {
			return incidents;
	}

	private MapObject getIncident( Point2D p ) {
		MapObject result = null;
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( p ) ) {
					result = incidents[ i ];
					break;
				}
			}
		}
		return result;
	}

	public final MapObject search( Rectangle2D searchArea,
			LayerRenderer renderer ) {
		MapObject result = null;
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( searchArea ) || r.intersects( searchArea ) ||
						searchArea.contains( r ) ) {
					result = incidents[ i ];
					break;
				}
			}
		}
		return result;
	}

	public MapObject search( Point2D p, LayerRenderer renderer ) {
		MapObject result = null;
		if ( incidents != null ) {
			for ( int i = ( incidents.length - 1 ); i >= 0; i-- ) {
				double x = incidents[ i ].getX();
				double y = incidents[ i ].getY();
				Rectangle2D r = new Rectangle2D.Double( ( x - 500 ),
					( y - 500 ), 1000, 1000 );
				if ( r.contains( p ) ) {
					result = incidents[ i ];
					break;
				}
			}
		}
		return result;
	}

	public Theme getTheme() {
		Theme result = new IncidentTheme( this );
		result.setTip( new MapTip() {
			public String getTip( MapObject object ) {
				String result = null;
				if ( object != null ) {
					result = object.toString();
				}
				return result;
			}
		});
		return result;
	}

	public class IncidentWrapper implements MapObject {

		private final Incident incident;

		private final Shape shape;

		public IncidentWrapper( Incident incident ) {
			this.incident = incident;
			GeneralPath path = new GeneralPath();
			path.moveTo( ( float ) incident.getX(),
				( float ) incident.getY() );
			shape = path;
		}

		public Incident getIncident() {
			return incident;
		}

		public IncidentDescription getDescription() {
			return incident.getDescription();
		}

		public Shape getShape() {
			return shape;
		}

		public double getX() {
			return incident.getX();
		}

		public double getY() {
			return incident.getY();
		}

		public String toString() {
			return incident.toString();
		}

	}

	private class IncidentTheme extends Theme implements MapMouseListener {

		public IncidentTheme( Layer layer ) {
			super( layer );
			LayerRenderer renderer = new IncidentRenderer();
			this.addLayerRenderer( renderer );
			this.setCurrentLayerRenderer( renderer );
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
			MapObject incident = getIncident( point );
			if ( incident == null ) {
				setSelections( new MapObject[ 0 ] );
				notifyThemeChangedListeners( new ThemeChangedEvent( this,
					ThemeChangedEvent.SELECTION ) );
				return false;
			} else {
				MapObject[] oldSelections = getSelections();
				if ( oldSelections.length > 0 && incident ==
						oldSelections[ 0 ] ) {
					clearSelections();
				} else {
					MapObject[] selections = { incident };
					setSelections( selections );
				}
				notifyThemeChangedListeners( new ThemeChangedEvent( this,
					ThemeChangedEvent.SELECTION ) );
				return true;
			}
		}
	}
}