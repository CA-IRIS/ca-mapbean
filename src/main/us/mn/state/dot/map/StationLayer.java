
//Title:        StationLayer
//Version:      1.0
//Copyright:    Copyright (c) 1999
//Author:       Erik Engstrom
//Company:      MnDOT
//Description:  Layer that displays station data updated by DDS client.

package us.mn.state.dot.shape;

import us.mn.state.dot.dds.client.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;

public final class StationLayer extends ShapeLayer implements StationListener {

    public StationLayer() throws IOException {
		super( "gpoly/gpoly", "gpoly" );
		this.setStatic( false );
	}

	public void update( int[] volume, int[] occupancy, int[] status ){
		IntegerField v = ( IntegerField ) super.getField( "VOLUME" );
		IntegerField o = ( IntegerField ) super.getField( "OCCUPANCY" );
		IntegerField s = ( IntegerField ) super.getField( "STATUS" );
		int [] station = (( IntegerField ) super.getField( "STATION2"
			) ).getData();
		for ( int i = ( station.length - 1 ); i >= 0; i-- ){
			if ( station[ i ] > 0 ) {
				if ( i - 1 < volume.length ) {
					v.setValue( i, volume[ station[ i ] - 1 ] );
					o.setValue( i, occupancy[ station[ i ] - 1 ] );
					s.setValue( i, status[ station[ i ] - 1 ] );
				}
			}
		}
		updateLayer();
	}
}
