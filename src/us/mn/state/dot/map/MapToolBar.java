/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

/**
 * Toolbar used for MapBean.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class MapToolBar extends NavigationBar {

	/** Menu bar */
	protected final JMenuBar menu = new JMenuBar();

	/** Legend menu */
	protected final JMenu legend = new JMenu("Legend");

	/** Create a new MapToolBar */
	public MapToolBar(MapBean m) {
		super(m);
		menu.setBorderPainted(false);
		menu.setAlignmentY(.5f);
		menu.add(new LayerMenu(map.getLayers()));
		menu.add(legend);
		add(menu, 0);
	}

	/** Add a theme legend to the tool bar */
	public void addThemeLegend(LayerState lstate) {
		String name = lstate.getLayer().getName();
		LayerRenderer r = lstate.getCurrentLayerRenderer();
		LegendMenu l = new LegendMenu(name, r);
		JComboBox combo = createRendererCombo(lstate, l);
		if(combo != null) {
			menu.add(combo);
			legend.add(l);
		} else if(l.getItemCount() > 1)
			legend.add(l);
	}

	/** Get the renderer selector combo box */
	protected JComboBox createRendererCombo(final LayerState lstate,
		final LegendMenu legend)
	{
		final JComboBox combo = new JComboBox();
		for(LayerRenderer r: lstate.getLayerRenderers())
			combo.addItem(r);
		if(combo.getItemCount() < 2)
			return null;
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LayerRenderer r =
					(LayerRenderer)combo.getSelectedItem();
				lstate.setCurrentLayerRenderer(r);
				legend.setMapRenderer(r);
			}
		});
		return combo;
	}
}
