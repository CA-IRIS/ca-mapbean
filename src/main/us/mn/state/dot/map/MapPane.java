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
 *
 * Created on October 24, 2000, 2:02 PM
 */

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

/**
 * This class can be used to generate map graphics when access to the graphics
 * subsystem is not available.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.41 $ $Date: 2001/04/20 17:17:54 $
 */
public final class MapPane implements ThemeChangedListener {
	
	/** buffer for map */
	private transient BufferedImage screenBuffer;
	
	/** buffer for static themes in map */
	private transient BufferedImage staticBuffer;
	
	/** List of dynamic themes */
	private final ArrayList themes = new ArrayList();
	
	/** List of static themes */
	private final ArrayList staticThemes = new ArrayList();
	
	/** Transformation to draw shapes on the map */
	private final AffineTransform screenTransform = new AffineTransform();
	
	/** Bounding box */
	private Rectangle2D extent = new Rectangle2D.Double();
	
	/** does the buffer need to be updated? */
	private boolean bufferDirty = true;
	
	/** does the static buffer need to be updated? */
	private boolean staticBufferDirty = false;
	
	/** Background color of map */
	private Color backgroundColor = Color.gray;
	
	/** height of map image */
	private int height = 0;
	
	/** width of map image */
	private int width = 0;
	
	private ArrayList listeners = new ArrayList();
	
	private boolean transparent = false;
	
	/** Creates new MapPane without any themes. */
	public MapPane() {
		this( new ArrayList() );
	}
	
	/**
	 * Constructs a Map using the themes contained in the themes parameter.
	 * @param themes a list of themes to be used in the map
	 */
	public MapPane( java.util.List themes ) {
		for ( ListIterator li = themes.listIterator(); li.hasNext(); ){
			Object ob = li.next();
			Theme theme;
			if ( ob instanceof Layer ) {
				theme = ( ( Layer ) ob ).getTheme();
			} else if ( ob instanceof Theme ) {
				theme = ( Theme ) ob;
			} else {
				throw new IllegalArgumentException( "Must be Layer or Theme" );
			}
			addTheme( theme );
		}
	}
	
	/**
	 * Add a MapChangedListener to the listeners of this MapPane.
	 * @param l, the listener to add.
	 */
	public void addMapChangedListener( MapChangedListener l ) {
		listeners.add( l );
	}
	
	/**
	 * Gets the list of themes contained in the MapPane.
	 * @returns the list of themes contained by the MapPane
	 */
	public ArrayList getThemes() {
		ArrayList result = new ArrayList( staticThemes );
		result.addAll( themes );
		return result;
	}
	
	/**
	 * Set the size of the MapImage.
	 * @param d, the new dimension of the image.
	 */
	public void setSize( Dimension d ) {
		int height = d.height;
		int width = d.width;
		if ( height < 0 ) {
			throw new IllegalArgumentException(
			"Height must be greater than 0: height= " + height );
		}
		if ( width < 0 ) {
			throw new IllegalArgumentException(
			"Width must be greater than 0: width= " + width );
		}
		if ( height == 0 ) {
			height = 1;
		}
		if ( width == 0 ) {
			width = 1;
		}
		screenBuffer = createImage( width, height );
        staticBuffer = createImage( width, height );
		rescale();
	}
	
    private BufferedImage createImage( int width, int height ) {
        if ( transparent ) {
            return new BufferedImage( width, height,
                BufferedImage.TYPE_4BYTE_ABGR );
        } else {
			return new BufferedImage( width, height, 
				BufferedImage.TYPE_3BYTE_BGR );
		   // return new BufferedImage( width, height,
           //     BufferedImage.TYPE_INT_RGB );
           //return GraphicsEnvironment.getLocalGraphicsEnvironment()
           //     .getDefaultScreenDevice().getDefaultConfiguration()
           //     .createCompatibleImage( width, height );
           //FIXME there has got to be a better way!
        }
        
    }
	/**
	 * Get the size of the MapImage.
	 */
	public Dimension getSize(){
		if ( screenBuffer == null ) {
			return null;
		} else {
			return new Dimension( screenBuffer.getWidth(),
			screenBuffer.getHeight() );
		}
	}
	
	/**
	 * Add a new Theme to the Map.
	 * @param theme Theme to be added to the Map
	 */
	public void addTheme( Theme theme ) {
		if ( theme.isStatic() ) {
			staticThemes.add( theme );
		} else {
			themes.add( theme );
		}
		theme.addThemeChangedListener( this );
		setExtent( theme.getExtent() );
	}
	
	/**
	 * Add a new theme to the Map at the specified index.
	 * @param theme Theme to be added to the Map
	 * @param index int specifying the index at which the theme should be added
	 */
	public void addTheme( Theme theme, int index ) {
		themes.add( index, theme );
		theme.addThemeChangedListener( this );
	}
	
	/**
	 * Called when the MapBean is resized or the extent is changed.
	 */
	private synchronized void rescale() {
		if ( screenBuffer == null ) {
			return;
		}
		height = screenBuffer.getHeight();
		width = screenBuffer.getWidth();
		if ( height == 0 || width == 0 || extent == null ) {
			return;
		}
		double mapWidth = extent.getWidth();
		double mapHeight = extent.getHeight();
		double scale = 0;
		double shiftX = 0;
		double shiftY = 0;
		double scaleX = width / mapWidth;
		double scaleY = height / mapHeight;
		if ( scaleX > scaleY ) {
			scale = scaleY;
			shiftX = ( width - ( mapWidth * scale ) ) / 2;
		} else {
			scale = scaleX;
			shiftY = ( height - ( mapHeight * scale ) ) / 2;
		}
		screenTransform.setToTranslation( - ( extent.getMinX() * scale )
			+ shiftX, ( extent.getMaxY() * scale ) + shiftY );
		screenTransform.scale( scale, -scale );
		bufferDirty = true;
		staticBufferDirty = true;
	}
	
	/**
	 * Sets the background color of the map.
	 * @param color, new color for the backgound.
	 */
	public void setBackground( Color color ) {
		backgroundColor = color;
	}
	
	/**
	 * Updates staticBuffer
	 */
	private void updateStaticBuffer() {
		if ( staticBuffer == null ) return;
		synchronized ( staticBuffer ) {
			Graphics2D g = staticBuffer.createGraphics();
			g.clearRect( 0, 0, staticBuffer.getWidth(),
				staticBuffer.getHeight() );
			g.setColor( backgroundColor );
			g.fillRect( 0, 0, staticBuffer.getWidth(),
				staticBuffer.getHeight() );
			g.transform( screenTransform );
			staticBufferDirty = false;
			ListIterator li = staticThemes.listIterator();
			while ( li.hasNext() ) {
				( ( Theme ) li.next() ).paint( g );
			}
			g.dispose();
		}
	}
	
	/**
	 * Updates screenBuffer.
	 */
	public void updateScreenBuffer() {
		if ( screenBuffer == null ) return;
		synchronized ( screenBuffer ) {
			Graphics2D g = screenBuffer.createGraphics();
				if ( staticBufferDirty ) {
				updateStaticBuffer();
			}
			g.drawImage( staticBuffer, 0, 0, null );
			g.transform( screenTransform );
			bufferDirty = false;
			ListIterator li = themes.listIterator();
			while ( li.hasNext() ) {
				( ( Theme ) li.next() ).paint( g );
			}
			g.dispose();
		}
	}
	
	public BufferedImage getImage() {
		if ( bufferDirty || staticBufferDirty ) {
			updateScreenBuffer();
		}
		return screenBuffer;
	}
	
	public void themeChanged( final ThemeChangedEvent event ) {
		switch ( event.getReason() ) {
			case ThemeChangedEvent.DATA: case ThemeChangedEvent.SHADE:
				Theme theme = ( Theme ) event.getSource();
				if ( theme.isStatic() ) {
                    staticBufferDirty = true;
				}
                bufferDirty = true;
				break;
			case ThemeChangedEvent.SELECTION:
				break;
				default:
					break;
		}
		notifyMapChangedListeners();
	}
	
	/**
	 * Gets the theme with the name name from the Map.
	 * @param name the string containing the Name of layer to return.
	 * @return Theme or null if not found.
	 */
	public Theme getTheme( String name ) {
		Theme result = null;
		for ( ListIterator li = themes.listIterator(); li.hasNext(); ){
			Theme temp = ( Theme ) li.next();
			if ( name.equals( temp.getName() ) ) {
				result = temp;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Notifies all registered MapChangedListeners that the map image has
	 * changed.
	 */
	private void notifyMapChangedListeners(){
		ListIterator it = listeners.listIterator();
		while ( it.hasNext() ) {
			MapChangedListener listener = ( MapChangedListener ) it.next();
			listener.mapChanged();
		}
	}
	
	/**
	 * Get the AffineTransform of the map.
	 */
	public AffineTransform getTransform() {
		return screenTransform;
	}
	
	/**
	 * Get the extent of the MapImage.
	 */
	public Rectangle2D getExtent(){
		return extent;
	}
	
	/**
	 * Set the bounding box for display
	 * @param r The rectangle which describes the new bounding box for the
	 *	display.
	 */
	public void setExtent( Rectangle2D r ) {
		setExtent( r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight() );
	}
	
	/**
	 * Set the bounding box for display
	 * @param x, the new x-coordinate for the map.
	 * @param y, the new y-coordinate for the map.
	 * @param width, the new width for the map.
	 * @param height, the new height for the map.
	 */
	public void setExtent( double x, double y, double width, double height ) {
		setExtentFrame( x, y, width, height );
	}
	
	/**
	 * Set the bounding box for display
	 * @param x, the new x-coordinate for the map.
	 * @param y, the new y-coordinate for the map.
	 * @param width, the new width for the map.
	 * @param height, the new height for the map.
	 */
	protected void setExtentFrame( double x, double y, double width,
            double height ) {
		extent.setFrame( x, y, width, height );
		rescale();
	}
}