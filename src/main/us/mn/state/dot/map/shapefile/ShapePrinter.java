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

package us.mn.state.dot.shape.shapefile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.4 $ $Date: 2003/05/19 21:23:41 $ 
 */
public class ShapePrinter {
	
	private ShapeLayer cameraLayer;

	public ShapePrinter( String source ) {
		try{
			cameraLayer = new ShapeLayer( new URL( source ), "cms");
		} catch (IOException ex){
			ex.printStackTrace();
		}

	}
	
	public void print( String dest ) throws IOException {
		OutputStream out = new FileOutputStream( dest );
		cameraLayer.printData( out );
		out.flush();
		out.close();
	}

	public static void main(String[] args) {
		ShapePrinter shapePrinter = new ShapePrinter( args[0] );
		try {
			shapePrinter.print( args[ 1 ]);
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
} 