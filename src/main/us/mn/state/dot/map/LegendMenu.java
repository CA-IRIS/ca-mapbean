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
package us.mn.state.dot.map;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JMenu;

/**
 * JMenu for displaying the Legend for a LayerRenderer.
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @author Douglas Lau
 */
public class LegendMenu extends JMenu {

  	/** Create a new LegendMenu */
 	public LegendMenu(LayerRenderer r) {
		super("Legend");
		setBorder(BorderFactory.createEtchedBorder());
		setBorderPainted(true);
		setMapRenderer(r);
	}

	/** Set the LayerRenderer that this menu displays */
	public void setMapRenderer(LayerRenderer r) {
		removeAll();
		Component[] legend = r.getLegend();
		if(legend == null) return;
		for(int i = 0; i < legend.length; i++) {
			add(legend[i]);
		}
	}
}
