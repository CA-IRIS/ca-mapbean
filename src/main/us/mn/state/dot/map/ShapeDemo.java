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
import javax.swing.*;
import java.io.*;


/**
  * ShapeDemo is a program to demonstrate the Shape package.
  *
  * @author Douglas Lau
  */
public final class ShapeDemo extends JFrame {


	// Private data
	private JMenuBar menuBar;
	private Map pane;


	/** Constructor */
	public ShapeDemo() {
		super( "ShapeFile Viewer" );
		final int inset = 150;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds( inset, inset, screenSize.width - inset * 2,
			screenSize.height - inset * 2 );
		//final StationPainter painter = new StationPainter();
		try {
			Layer layer = new Layer( "C:\\gpoly\\tvp" );
			//layer.setPainter( painter );
			pane = new Map( );
     pane.addLayer(layer);
/*			layer = new Layer( "water.shp" );
			layer.setColor( Color.blue );
			pane.addLayer( layer );
			layer = new Layer( "stateart.shp" );
			layer.setColor( Color.black );
			pane.addLayer( layer );
			layer = new Layer( "met-cr.shp" );
			layer.setColor( Color.black );
			pane.addLayer( layer );
			layer = new Layer( "met-csah.shp" );
			layer.setColor( Color.black );
			pane.addLayer( layer );
			layer = new Layer( "tvp.shp" );
			layer.setColor( Color.black );
			layer.setPainter( new CirclePainter() );
			pane.addLayer( layer ); */
			pane.setBackground( Color.lightGray );
			getContentPane().add( pane );
		}
		catch( IOException e ) {
			e.printStackTrace();
			pane = null;
		}

		/*Thread t = new Thread() {
			public void run() {
				try { sleep( 30000 ); }
				catch( InterruptedException e ) {}
				while( true ) {
					for( int j = 0; j < 3; j++ ) {
						painter.setOffset( j );
						pane.drawMainLayer();
						pane.repaint();
						try { sleep( 10000 ); }
						catch( InterruptedException e ) {}
					}
				}
			}
		};
		t.start();*/

		buildMenus();
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e ) { quit(); }
		});
	}


	/** Build the menus */
	private void buildMenus() {
		menuBar = new JMenuBar();
		menuBar.setOpaque( true );
		JMenu file = buildFileMenu();
		menuBar.add( file );
		setJMenuBar( menuBar );
	}


	/** Build the file menu */
	private JMenu buildFileMenu() {
		JMenu file = new JMenu( "File" );
		JMenuItem quit = new JMenuItem( "Quit" );
		quit.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) { quit(); }
		});
		file.add( quit );
		return file;
	}


	/** Quit the program */
	private void quit() {
		System.exit( 0 );
	}


	/** Main program method */
	static public void main( String args[] ) {
		new ShapeDemo().setVisible( true );
	}
}
