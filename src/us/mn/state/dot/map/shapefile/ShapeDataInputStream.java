/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2007  Minnesota Department of Transportation
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
 */
package us.mn.state.dot.map.shapefile;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Adds little endian double and integer reading to DataInputStream.
 *
 * @author Erik Engstrom
 */
public class ShapeDataInputStream extends DataInputStream {

	/** Create a new ShapeDataInputStream */
	public ShapeDataInputStream(InputStream in) {
		super(in);
	}

	/** Read a little endian short */
	public short readLittleShort() throws EOFException, IOException {
		short result = 0;
		result += (readByte() & 0xff);
		result += (readByte() & 0xff) << 8;
		return result;
	}

	/** Read a little endian integer */
	public int readLittleInt() throws EOFException, IOException {
		int result = 0;
		for(int i = 0; i < 4; i++)
			result += ((readByte() & 0xff) << (i * 8));
		return result;
	}

	/** Read a little endian double */
	public double readLittleDouble() throws EOFException, IOException {
		int first = readLittleInt();
		int second = readLittleInt();
		long temp = ((second & 0xFFFFFFFFL) << 32) +
			(first & 0xFFFFFFFFL);
		return Double.longBitsToDouble(temp);
	}

	/** Read a string */
	public String readString(int length) throws EOFException, IOException {
		byte[] buffer = new byte[length];
		for(int i = 0; i < length; i++)
			buffer[i] = readByte();
		return new String(buffer).trim();
	}
}
