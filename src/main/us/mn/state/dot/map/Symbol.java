
//Title:        Symbol
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Your description

package us.mn.state.dot.shape;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public abstract class Symbol implements LegendItem {

	protected Color color = Color.black;
	protected Color outlineColor = Color.black;
	protected int size;
	protected boolean outline = false;
	protected boolean filled = true;
	private String label = "";

	public abstract void draw( Graphics2D g, GeneralPath path );

	public Symbol(){
		this( Color.black );
	}

	public Symbol( Color c ){
		this( c, "", false );
	}

	public Symbol( Color c, String label ){
		this( c, label, false );
	}

	public Symbol( Color color, String label, boolean outline ){
		this.color = color;
		this.outline = outline;
		this.label = label;
	}

	public final void setColor ( Color color ){
		this.color = color;
	}

	public Color getColor(){
		return color;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled( boolean f ) {
		filled = f;
	}

	public void setSize ( int size ){
		if ( size < 0 ) {
			throw new IllegalArgumentException( "Size can't be less than 0: " +
				size );
		} else {
			this.size = size;
		}
	}

	public int getSize(){
		return size;
	}

	public void setOutLine( boolean outline ){
		this.outline = outline;
	}

	public boolean getOutLine(){
		return outline;
	}

	public void setOutLineColor( Color c ){
		outlineColor = c;
	}

	public Color getOutLineColor(){
		return outlineColor;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel( String l ){
		label = l;
	}

	public Component getLegend(){
		JLabel label = new JLabel();
		if ( ( getLabel() != null ) && ( ! getLabel().equals( "" ) ) ) {
			label.setText( getLabel() );
			ColorIcon icon = new ColorIcon( getColor() );
			label.setIcon( icon );
		}
		return label;
	}

	class ColorIcon implements Icon {
		private Color color;
		private int w, h;

		public ColorIcon(){
			this( Color.gray, 25, 15 );
		}

		public ColorIcon( Color color ){
			this( color, 25, 15 );
		}

		public ColorIcon( Color color, int w, int h ){
			this.color = color;
			this.w = w;
			this.h = h;
		}

		public void paintIcon( Component c, Graphics g, int x, int y ) {
			g.setColor( Color.black );
			g.drawRect( x, y, w - 1, h - 1 );
			g.setColor( color );
			g.fillRect( x + 1, y + 1, w - 2, h - 2 );
		}
		public Color getColor() {
			return color;
		}

		public void setColor( Color color ){
			this.color = color;
		}

		public int getIconWidth() {
			return w;
		}

		public int getIconHeight(){
			return h;
		}
	}
}
