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
package us.mn.state.dot.shape.symbol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A FillSymbol is used to paint a polygon on a Map only SOLID_FILL is implemented.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class FillSymbol extends AbstractSymbol {

	/** fill constants */
	public static final int SOLID_FILL = 0;
	//public static final int TRANSPARENT_FILL = 1;
	//public static final int HORIZONTAL_FILL = 2;
	//public static final int VERTICAL_FILL = 3;
	//public static final int UPWARD_DIAGONAL_FILL = 4;
	//public static final int DOWNWARD_DIAGONAL_FILL = 5;
	//public static final int CROSS_FILL = 6;
	//public static final int DIAGONAL_CROSS_FILL = 7;
	//public static final int LIGHT_GRAY_FILL = 8;
	//public static final int GRAY_FILL = 9;
	//public static final int DARK_GRAY_FILL = 10;

	public FillSymbol() {
		super();
	}

	public FillSymbol( Color c ){
		super( c );
	}

	public FillSymbol( Color c, String label ){
		super( c, label );
	}

	public FillSymbol( Color c, String label, boolean outlined ){
		super( c, label, outlined );
	}

	/** Draw symbol on map */
	public void draw(Graphics2D g, Shape shape) {
		if(isFilled()) {
			g.setColor(color);
			g.fill(shape);
		}
		if(isOutlined()) {
			outlineSymbol.draw(g, shape);
		}
	}

	/** Get the legend component for this symbol */
	public Component getLegend() {
		String l = getLabel();
		JLabel label = new JLabel();
		if(l != null && (!l.equals(""))) {
			label.setText(l);
			FillSymbolIcon icon = new FillSymbolIcon(this);
			label.setIcon(icon);
		}
		return label;
	}

	class FillSymbolIcon implements Icon {

		private final FillSymbol symbol;
		private int width;
		private int height;

		public FillSymbolIcon(FillSymbol symbol) {
			this(symbol, 25, 15);
		}

		public FillSymbolIcon(FillSymbol symbol, int width, int height){
			this.symbol = symbol;
			this.width = width;
			this.height = height;
		}

		public void paintIcon( Component c, Graphics g, int x, int y ) {
			Graphics2D g2 = ( Graphics2D ) g;
			if(symbol.isOutlined()) {
				g2.setColor(symbol.getOutlineColor());
				g2.drawRect(x, y, width - 1, height - 1);
			}
			if(symbol.isFilled()) {
				g.setColor(symbol.getColor());
				g.fillRect(x + 1, y + 1, width - 2, height - 2);
			}
		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight(){
			return height;
		}
	}
}
