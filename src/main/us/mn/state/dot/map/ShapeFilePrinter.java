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

import java.io.*;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.3 $ $Date: 2001/04/19 16:49:31 $ 
 */
public class ShapeFilePrinter extends java.lang.Object {

	/** Creates new ShapeFilePrinter */
    public ShapeFilePrinter() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main( String args[] ) {
		if ( args.length < 1 ) { 
			System.out.println( "must include name of shape file to read" );
		}
		String inFileName = args[ 0 ];
		try {
			File file = new File( inFileName );
			ShapeLayer layer = new ShapeLayer( file.toURL(), inFileName );
			if ( args.length > 1 ) {
				File outFile = new File( args[ 1 ] );
				OutputStream out = new FileOutputStream( outFile );
				layer.printData( out );
			} else {
				layer.printData( System.out );
			}
		} catch ( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}
    }
}