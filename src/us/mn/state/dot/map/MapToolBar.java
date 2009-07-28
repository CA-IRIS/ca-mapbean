/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2009  Minnesota Department of Transportation
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import us.mn.state.dot.map.event.LayerChange;
import us.mn.state.dot.map.event.LayerChangedEvent;
import us.mn.state.dot.map.event.LayerChangedListener;

/**
 * Toolbar used for MapBean.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class MapToolBar extends NavigationBar implements LayerChangedListener {

	/** Menu bar */
	protected final JMenuBar menu = new JMenuBar();

	/** Legend menu */
	protected final JMenu legend = new JMenu("Legend");

	/** Theme selection combo box */
	protected final JComboBox themes = new JComboBox();

	/** Create a new MapToolBar */
	public MapToolBar(MapBean m) {
		super(m);
		menu.setBorderPainted(false);
		menu.setAlignmentY(.5f);
		menu.add(new LayerMenu(map.getLayers()));
		menu.add(legend);
		menu.add(themes);
		add(menu, 0);
		map.addLayerChangedListener(this);
	}

	/** Called by the Layer when the layers data is changed */
	public void layerChanged(LayerChangedEvent ev) {
		if(ev.getReason() == LayerChange.model) {
			legend.removeAll();
			for(LayerState ls: map.getLayers())
				addThemeLegend(ls);
		}
	}

	/** Add a theme legend to the tool bar */
	protected void addThemeLegend(LayerState ls) {
		String name = ls.getLayer().getName();
		LegendMenu lm = new LegendMenu(name, ls.getTheme());
		if(lm.getItemCount() > 1) {
			legend.add(lm);
			createThemeModel(ls, lm);
		}
	}

	/** Create the theme combo box model */
	protected void createThemeModel(final LayerState ls,
		final LegendMenu legend)
	{
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		for(Theme t: ls.getThemes())
			model.addElement(t);
		if(model.getSize() < 2)
			return;
		themes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = themes.getSelectedItem();
				if(obj instanceof Theme) {
					Theme t = (Theme)obj;
					ls.setTheme(t);
					legend.setTheme(t);
				}
			}
		});
		themes.setModel(model);
	}
}
