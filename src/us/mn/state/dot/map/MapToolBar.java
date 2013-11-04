/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JToolBar;

/**
 * Toolbar for legend, themes and zoom buttons for MapBean.
 *
 * @author Douglas Lau
 * @author Erik Engstrom
 */
public class MapToolBar extends JToolBar implements LayerChangedListener {

	/** Map associated with the tool bar */
	private final MapBean map;

	/** Menu bar */
	private final JMenuBar menu = new JMenuBar();

	/** Layer menu */
	private final LayerMenu layers = new LayerMenu();

	/** Legend menu */
	private final JMenu legend = new JMenu("Legend");

	/** Theme selection combo box */
	private final JComboBox themes = new JComboBox();

	/** Create a new map tool bar */
	public MapToolBar(MapBean m) {
		map = m;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		menu.setAlignmentY(.5f);
		menu.add(layers);
		menu.add(legend);
		menu.add(themes);
		add(menu);
		add(Box.createGlue());
		addZoomButtons();
		map.addLayerChangedListener(this);
	}

	/** Called by the Layer when the layers data is changed */
	public void layerChanged(LayerChangedEvent ev) {
		if(ev.getReason() == LayerChange.model) {
			layers.removeAll();
			legend.removeAll();
			for(LayerState ls: map.getLayers()) {
				layers.addLayer(ls);
				addThemeLegend(ls);
			}
		}
	}

	/** Add a theme legend to the tool bar */
	private void addThemeLegend(LayerState ls) {
		String name = ls.getLayer().getName();
		LegendMenu lm = new LegendMenu(name, ls.getTheme());
		if(lm.getItemCount() > 1) {
			legend.add(lm);
			createThemeModel(ls, lm);
		}
	}

	/** Create the theme combo box model */
	private void createThemeModel(final LayerState ls,
		final LegendMenu lm)
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
					lm.setTheme(t);
				}
			}
		});
		themes.setModel(model);
	}

	/** Add the zoom buttons */
	private void addZoomButtons() {
		JButton b = new JButton(" + ");
		b.setToolTipText("Zoom map view in");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.zoom(true);
			}
		});
		addButton(b);
		b = new JButton(" - ");
		b.setToolTipText("Zoom map view out");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				map.zoom(false);
			}
		});
		addButton(b);
	}

	/** Add a button to the toolbar */
	public void addButton(AbstractButton b) {
		b.setMargin(new Insets(2, 2, 2, 2));
		add(b);
		add(Box.createHorizontalStrut(4));
	}
}
