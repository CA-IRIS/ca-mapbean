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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import us.mn.state.dot.dds.client.DataListener;
import us.mn.state.dot.dds.client.Station;
import us.mn.state.dot.dds.client.StationClient;
import us.mn.state.dot.dds.client.StationListener;
import us.mn.state.dot.shape.event.MapMouseListener;
import us.mn.state.dot.shape.event.SelectMouseMode;
import us.mn.state.dot.shape.shapefile.ShapeLayer;
import us.mn.state.dot.shape.shapefile.ShapeObject;
import us.mn.state.dot.shape.shapefile.ShapeRenderer;

/**
 * A StationLayer displays detector station data represented as a gpoly.shp
 * file.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.33 $ $Date: 2003/08/14 15:00:43 $ 
 */
public final class StationLayer extends ShapeLayer implements
		StationListener, DataListener {
	/**
	 * Constructs a StationLayer.
	 * @throws IOException Will throw an IOException if the gpoly.dbf or
	 * gpoly.shp files can not be found
	 */
	public StationLayer() throws IOException {
		super( "gpoly/gpoly", "gpoly" );
		this.setStatic( false );
	}

	public StationLayer( StationClient stationClient ) throws IOException {
		this();
		stationClient.addStationListener( this );
	}

	/**
	 * updates the data of this layer
	 * @param volume an array containing the new volume values
	 * @param occupancy an array containing the new occupancy values
	 * @param status an array containing the new status values
	 */
	public final synchronized void update( int[] volume, int[] occupancy,
			int[] status ) {
		for ( int i = shapes.length - 1; i >= 0; i-- ) {
			ShapeObject shape = shapes[ i ];
			int station = ( ( Integer ) 
				shape.getValue( "STATION2" ) ).intValue() - 1;
			if ( station > 0 ) {
				if ( station < volume.length ) {
					shape.addField( "VOLUME",
						new Integer( volume[ station ] ) );
					shape.addField( "OCCUPANCY",
						new Integer( occupancy[ station ] ) );
					shape.addField( "STATUS",
						new Integer( status[ station ] ) );
				}
			}	
		}
		SwingUtilities.invokeLater( new NotifyThread( this,
			LayerChangedEvent.DATA ) );
	}
	
	private final class NotifyThread implements Runnable {
		private final StationLayer layer;
		private final int reason;
		
		public NotifyThread( StationLayer layer, int reason ) {
			this.layer = layer;
			this.reason = reason;
		}
		
		public void run() {
			layer.notifyLayerChangedListeners( new LayerChangedEvent(
				layer, reason ) );
		}
	}
	
	public final Theme getTheme() {
		StationMapTip mapTip = new StationMapTip();
		ShapeRenderer renderer = new OccupancyRenderer(
			"OCCUPANCY", mapTip );
		Theme result = new StationTheme( this, renderer );
		result.setTip( mapTip );
		return result;
	}
	
	/**
	 * Paint selected objects on this layer.
	 */
	public void paintSelections( Graphics2D g, LayerRenderer renderer,
			MapObject[] selections) {
		for ( int i = 0; i < selections.length; i++ ) {
			renderer.render( g, selections[ i ] );
		}
	}
	
	private final class StationTheme extends Theme implements 
			MapMouseListener {
		
		private final JMenu rightClickMenu = new JMenu();
		private final JMenu statusMenu = new JMenu( "Status" );
		private final JMenuItem idMenuItem = new JMenuItem( "ID" );
		private final JMenuItem volumeMenuItem = new JMenuItem( "Volume" );
		private final JMenuItem occMenuItem = new JMenuItem( "occ" );
		private final ShapeLayer layer;
		
		public StationTheme( ShapeLayer layer, LayerRenderer renderer ){
			super( layer, renderer );
			this.layer = layer;
			Symbol symbol = new FillSymbol();
			symbol.getOutLineSymbol().setColor( Color.magenta );
			symbol.getOutLineSymbol().setSize( 50 );
			symbol.setOutLined( true );
			symbol.setFilled( false );
			setSelectionRenderer( new DefaultRenderer( symbol ) );
			rightClickMenu.add( statusMenu );
			statusMenu.add( idMenuItem );
			statusMenu.add( volumeMenuItem );
			statusMenu.add( occMenuItem );
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
			MapObject searchResult = getMapObject( event );
			if ( searchResult == null ) {
				return false;
			} else {
				MapObject[] selections = { searchResult };
				setSelections( selections );
				notifyThemeChangedListeners( new ThemeChangedEvent( this,
					ThemeChangedEvent.SELECTION ) );
				if ( SwingUtilities.isRightMouseButton( event ) ) {
					ShapeObject shapeObject = ( ShapeObject ) searchResult;
					idMenuItem.setText( "Station " + shapeObject.getValue( 
						"STATION2" ) + ": " + shapeObject.getValue( "NAME" ) );
					volumeMenuItem.setText( " Volume = " + 
						shapeObject.getValue( "VOLUME" ) );
					occMenuItem.setText( " Occupancy = " + 
						shapeObject.getValue( "OCCUPANCY" ) );
					JPopupMenu menu = rightClickMenu.getPopupMenu();
					menu.show( event.getComponent(), event.getX(),
						event.getY() );
				}
				return true;
			}
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
		
		private MapObject getMapObject( final java.awt.event.MouseEvent event ) {
			MapBean map = ( MapBean ) event.getSource();
			AffineTransform at = null;
			try {
				at = map.getTransform().createInverse();
			} catch ( NoninvertibleTransformException ex ){
				return null;
			}
			Graphics2D g = ( Graphics2D ) map.getGraphics();
			g.setTransform( map.getTransform() );
			Point2D point = null;
			point = at.transform( event.getPoint(), point );
			return layer.search( point, renderer );
		}
		
		public boolean mouseClicked( final java.awt.event.MouseEvent event ) {
			return false;
		}
	}
	
	/**
	 * @see us.mn.state.dot.dds.client.DataListener#update(List)
	 */
	public void update( List stations ) {
		for ( int i = shapes.length - 1; i >= 0; i-- ) {
			ShapeObject shape = shapes[ i ];
			int station = ( ( Integer ) 
				shape.getValue( "STATION2" ) ).intValue() - 1;
			if ( station > 0 ) {
				Station stat = ( Station ) stations.get( station );
				shape.addField( "VOLUME",
					new Integer( (int) stat.getVolume() ) );
				shape.addField( "OCCUPANCY",
					new Integer( (int) stat.getOccupancy() ) );
				shape.addField( "STATUS",
					new Integer( stat.getStatus() ) );
			}	
		}
		SwingUtilities.invokeLater( new NotifyThread( this,
			LayerChangedEvent.DATA ) );
	}

}
