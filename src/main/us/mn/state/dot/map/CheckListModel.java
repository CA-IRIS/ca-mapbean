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

import javax.swing.*;
import java.util.*;
import java.awt.event.*;

/**
 * ListModel that contains check boxes instead of labels. 
 *
 * @author <a href="mailto:erik.engstrom@dot.state.mn.us">Erik Engstrom</a>
 * @version $Revision: 1.3 $ $Date: 2001/07/09 21:10:19 $ 
 */
public abstract class CheckListModel extends AbstractListModel implements
		ActionListener {

	protected Vector checks = new Vector();
	protected Vector items = new Vector();

	public int getSize(){
		return checks.size();
	}

	public Object getElementAt(int index){
		return checks.get(index);
	}
} 