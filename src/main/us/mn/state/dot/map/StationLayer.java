package us.mn.state.dot.shape;

import us.mn.state.dot.dds.client.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

/**
 * A StationLayer displays detector station data represented as a gpoly.shp file.
 *
 * @author Erik Engstrom
 * @since 1.0
 */
public final class StationLayer extends ShapeLayer implements StationListener {
	/**
	 * Constructs a StationLayer.
	 * @throws IOException Will throw an IOException if the gpoly.dbf or gpoly.shp files can not be found
	 */
	public StationLayer() throws IOException {
		super( "gpoly/gpoly", "gpoly" );
		this.setStatic( false );
	}

	/**
	 * updates the data of this layer
	 * @param volume an array containing the new volume values
	 * @param occupancy an array containing the new occupancy values
	 * @param status an array containing the new status values
	 */
	public void update(int[] volume,int[] occupancy,int[] status) {
		IntegerField v = ( IntegerField ) super.getField( "VOLUME" );
		IntegerField o = ( IntegerField ) super.getField( "OCCUPANCY" );
		IntegerField s = ( IntegerField ) super.getField( "STATUS" );
		int [] station = (( IntegerField ) super.getField( "STATION2"
		) ).getData();
		for ( int i = ( station.length - 1 ); i >= 0; i-- ){
			if ( station[ i ] > 0 ) {
				if ( station[ i ] - 1 < volume.length ) {//i - 1 < volume.length ) {
					v.setValue( i, volume[ station[ i ] - 1 ] );
					o.setValue( i, occupancy[ station[ i ] - 1 ] );
					s.setValue( i, status[ station[ i ] - 1 ] );
				}
			}
		}
		updateLayer();
	}
}
