/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2004  Minnesota Department of Transportation
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
package us.mn.state.dot.map.symbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Symbol;

/**
 * Symol for painting MapObjects as images.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class ImageSymbol implements Symbol {

	protected final String label;

	private ImageIcon icon;

	private Dimension size;

	protected boolean drawFullSize = true;

	/** Create a new ImageSymbol */
	public ImageSymbol(ImageIcon icon) {
		this(icon, "");
	}

	public ImageSymbol(ImageIcon icon, String label) {
		this(icon, label, null);
	}

	public ImageSymbol(ImageIcon icon, String label, Dimension size) {
		this.icon = icon;
		this.label = label;
		this.size = size;
		if(size != null) {
			drawFullSize = false;
		}
	}

	public void setIcon( ImageIcon icon ) {
		this.icon = icon;
	}

	public ImageIcon getIcon( ImageIcon icon ) {
		return icon;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setColor(Color color) {
	}

	public Color getColor() {
		return null;
	}

	public void setOutlineColor(Color color) {
	}

	public Color getOutlineColor() {
		return null;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Draw the ImageSymbol.
	 * If size == null then the image is drawn at full size.
	 */
	public void draw(Graphics2D g, MapObject o) {
		Shape shape = o.getShape();
		Rectangle2D rect = shape.getBounds2D();
		double xCoord = rect.getX();
		double yCoord = rect.getY();
		int width = 0;
		int height = 0;
		if ( !drawFullSize ) {
			width = ( int ) size.getWidth();
			height = ( int ) size.getHeight();
		} else { //draw image to full size.
			width = icon.getIconWidth();
			height = icon.getIconHeight();
			try {
				AffineTransform transform = g.getTransform();
				Point2D pointStart = new Point2D.Double( 0, 0 );
				Point2D pointEnd = new Point2D.Double( width, height );
				pointStart = transform.inverseTransform( pointStart,
					pointStart );
				pointEnd = transform.inverseTransform( pointEnd, pointEnd );
				width = ( int ) ( pointEnd.getX() - pointStart.getX() );
				height = ( int ) ( pointEnd.getY() - pointStart.getY() );
			} catch ( java.awt.geom.NoninvertibleTransformException nte ) {
				nte.printStackTrace();
				return;
			}
		}

		g.drawImage( icon.getImage(), ( ( int ) xCoord - width / 2 ),
			( ( int ) yCoord - height / 2 ), width, height,
			icon.getImageObserver() );
	}

	public Dimension getSize() {
		return size;
	}

	public Component getLegend() {
		return new JLabel( icon );
	}
}
