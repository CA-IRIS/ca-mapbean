
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
		super("C:\\gpoly\\gpoly", "gpoly");
    }

    public void update(int[] volume, int[] occupancy, int[] status){
		Field [] fields = super.getFields();
        IntegerField v = null;
        IntegerField o = null;
        IntegerField s = null;
		int [] station = null;
		for (int i = 0; i < fields.length; i++) {
			if ((fields[i].getName()).equals("VOLUME")) {
				v = ((IntegerField) fields[i]);
			} else if ((fields[i].getName()).equals("OCCUPANCY")) {
				o = ((IntegerField) fields[i]);
			} else if ((fields[i].getName()).equals("STATUS")) {
				s = ((IntegerField) fields[i]);
			} else if ((fields[i].getName()).equals("STATION2")) {
				station = ((IntegerField) fields[i]).getData();
			}
		}
		for (int i = 0; i < station.length; i++){
			if (station[i] > 0) {
				v.setValue(i, volume[station[i] - 1]);
				o.setValue(i, occupancy[station[i] - 1]);
				s.setValue(i, status[station[i] - 1]);
			}
		}
		updateLayer(this);
	}
}
