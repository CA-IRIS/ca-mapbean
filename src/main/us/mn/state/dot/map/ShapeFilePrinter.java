/*
 * ShapeFilePrinter.java
 *
 * Created on November 14, 2000, 2:41 PM
 */

package us.mn.state.dot.shape;

import java.io.*;

/**
 *
 * @author  engs1eri
 * @version 
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