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
package us.mn.state.dot.shape.shapefile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 */
public class ShapeFilePrinter {
		
	private ShapeLayer layer;

	/** Create a new ShapeFilePrinter */
	public ShapeFilePrinter(String fileName) throws IOException {
		File file = new File(fileName);
		layer = new ShapeLayer(file.toURL(), fileName);

	}
    
	public void print(OutputStream out) throws IOException {
		layer.printData(out);
	}

	static public void main(String args[]) {
		if(args.length < 1) { 
			System.err.println("must include name of shape file");
		}
		String inFileName = args[0];
		try {
			ShapeFilePrinter printer = new ShapeFilePrinter(
				inFileName);
			if(args.length > 1) {
				File outFile = new File(args[1]);
				OutputStream out = new FileOutputStream(
					outFile);
				printer.print(out);
			} else {
				printer.print(System.out);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
