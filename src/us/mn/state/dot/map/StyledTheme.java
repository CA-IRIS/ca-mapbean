/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2009  Minnesota Department of Transportation
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
 */
package us.mn.state.dot.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

/**
 * A styled theme is a theme which contains a list of styles.
 *
 * @author Douglas Lau
 */
abstract public class StyledTheme extends Theme {

	/** List of all styles */
	protected final LinkedList<Style> styles = new LinkedList<Style>();

	/** Shape of legend icon */
	protected final Shape lshape;

	/** Create a new styled theme */
	protected StyledTheme(String n, Shape ls) {
		super(n);
		lshape = ls;
	}

	/** Add a new style to the theme */
	protected void addStyle(Style sty) {
		styles.add(sty);
		addSymbol(new VectorSymbol(sty, lshape));
	}

	/** Get the style to draw a given map object */
	abstract public Style getStyle(MapObject mo);

	/** Get a list of all symbols */
	public List<Symbol> getSymbols() {
		LinkedList<Symbol> symbols = new LinkedList<Symbol>();
		for(Style s: styles)
			symbols.add(getSymbol(s.getLabel()));
		return symbols;
	}

	/** Get a symbol for the given map object */
	public Symbol getSymbol(MapObject mo) {
		return getSymbol(getStyle(mo).getLabel());
	}

	/** Get the width to use for the selected outline */
	protected float getSelectedWidth(MapObject mo) {
		Style style = getStyle(mo);
		if(style.outline != null)
			return 9 * style.outline.width / 10;
		return 25;
	}

	/** Get the thickness of the ellipse */
	protected float getThickness(Shape s) {
		Rectangle2D r = s.getBounds2D();
		return (float)Math.min(r.getHeight(), r.getWidth()) / 2;
	}

	/** Create an ellipse around the given shape */
	protected Shape createEllipse(Shape s) {
		Rectangle2D r = s.getBounds2D();
		return new Ellipse2D.Double(r.getCenterX() - r.getWidth(),
			r.getCenterY() - r.getHeight(), r.getWidth() * 2,
			r.getHeight() * 2);
	}

	/** Draw a selected map object */
	public void drawSelected(Graphics2D g, MapObject mo) {
		Shape shape = mo.getShape();
		g.transform(mo.getTransform());
		Outline outline = Outline.createDashed(Color.WHITE,
			getSelectedWidth(mo));
		g.setColor(outline.color);
		g.setStroke(outline.stroke);
		g.draw(shape);
		outline = Outline.createSolid(Color.WHITE, getThickness(shape));
		Shape ellipse = createEllipse(shape);
		g.setStroke(outline.stroke);
		g.draw(ellipse);
	}
}
