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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.1 $ $Date: 2001/04/19 16:52:59 $ 
 */
public class Viewer extends JFrame {

	private final MapBean map = new MapBean();
		
	/** Creates new Viewer */
    public Viewer() {
		super( "Shapefile Viewer" );
		this.setJMenuBar( buildMenus() );
		this.getContentPane().setLayout( new BorderLayout() );
		this.getContentPane().add( map, BorderLayout.CENTER );
		NavigationBar toolbar = new NavigationBar( map );
		this.getContentPane().add( toolbar, BorderLayout.NORTH );
		try {
			map.addTheme( loadLayer( "" ).getTheme() );
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
    }
	
	private JMenuBar buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( createFileMenu() );
		menuBar.add( createViewMenu() );
		return menuBar;
	}
	
	private JMenu createFileMenu(){
		JMenu file = new JMenu( "File" );
		JMenuItem newItem = new JMenuItem( "New" );
		newItem.setEnabled( false );
		file.add( newItem );
		JMenuItem openItem = new JMenuItem( "Open" );
		openItem.setEnabled( false );
		file.add( openItem );
		JMenuItem saveItem = new JMenuItem( "Save" );
		saveItem.setEnabled( false );
		file.add( saveItem );
		file.addSeparator();
		JMenuItem exitItem = new JMenuItem( "Exit" );
		file.add( exitItem );
		return file;
	}
	
	private JMenu createViewMenu() {
		JMenu viewMenu = new JMenu( "View" );
		JMenuItem addLayer = new JMenuItem( "Add Layer" );
		final JFrame frame = this;
		addLayer.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				JFileChooser dialog = new JFileChooser();
				dialog.setDialogTitle( "Load ShapeFile" );
				dialog.setDialogType( JFileChooser.OPEN_DIALOG );
				javax.swing.filechooser.FileFilter shp = new ShapeFileFilter( );
				dialog.setFileFilter( shp );
				int returnVal = dialog.showOpenDialog( frame );
				if ( returnVal == JFileChooser.APPROVE_OPTION ) {
					File file = dialog.getSelectedFile();
					addLayer( file );
				}
			}
		});
		viewMenu.add( addLayer );
		JMenuItem removeLayer = new JMenuItem( "Remove Layer" );
		viewMenu.add( removeLayer );
		return viewMenu;
	}
	
	private void addLayer( File file ) {
		try {

			Layer layer = new ShapeLayer( file.toURL(), getName( file ) );
			map.addTheme( layer.getTheme() );
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	private String getName( File file ) {
		String fileName = file.getName();
		int index = fileName.lastIndexOf( '.' );
		return fileName.substring( 0, index );
	}

	public static void main( String[] args ) {
		Viewer viewer = new Viewer();
		viewer.setSize( 100, 100 );
		viewer.setVisible( true );
	}
	
	private Layer loadLayer( String name ) throws IOException {
		return new ShapeLayer( "gpoly/gpoly", "gpoly" );
	}
}