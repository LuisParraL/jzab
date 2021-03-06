/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.jzab.agent.net.codecs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <p>Title: ZabbixConstants</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.jzab.agent.net.codecs.ZabbixConstants</code></p>
 */

public class ZabbixConstants {
	/** The protocol version of the zabbix passive response processor */
	public static final byte ZABBIX_PROTOCOL = 1;

	/** The zabbix response header in bytes */
	public static final byte[] ZABBIX_HEADER =  "ZBXD".getBytes();
	/** The zabbix response baseline size for creating the downstream channel buffer */
	public static final int BASELINE_SIZE = ZABBIX_HEADER.length + 9;  // one byte for protocol, 8 bytes for length
	
	
	public static final byte[] AGENT_DATA_HEADER = "{ \"request\":\"agent data\", \"data\":[".getBytes(); 

	/** The default buffer size for collection buffers */
	public static final int DEFAULT_COLLECTION_BUFFER_SIZE = 4096 * 10;
	/** The default direct flag for collection buffers which, when true, will create direct memory buffers */
	public static final boolean DEFAULT_COLLECTION_BUFFER_DIRECT = true;
	
	/** The byte buffer that collection executing threads write their results into */
	public static final ThreadLocal<ByteBuffer> collectionBuffer = new ThreadLocal<ByteBuffer>() {
		/**
		 * Creates the default size buffer
		 * {@inheritDoc}
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		@Override
		protected ByteBuffer initialValue() {		
				return DEFAULT_COLLECTION_BUFFER_DIRECT 
						? 
						ByteBuffer.allocateDirect(DEFAULT_COLLECTION_BUFFER_SIZE)
						:
						ByteBuffer.allocate(DEFAULT_COLLECTION_BUFFER_SIZE);
		}
	};
	/** The schedule delay that collection executing threads use to determine which checks to execute */
	public static final ThreadLocal<Long> currentScheduleWindow = new ThreadLocal<Long>() {
		/**
		 * Sets the default schedule window as -1
		 * {@inheritDoc}
		 * @see java.lang.ThreadLocal#initialValue()
		 */
		@Override
		protected Long initialValue() {
			return -1L;
		}
	};
	
	/**
	 * Returns the passed long in the form of a little endian formatted byte array 
	 * @param payloadLength The long value to encode
	 * @return an byte array
	 */
	public static byte[] encodeLittleEndianLongBytes(long payloadLength) {
		return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(payloadLength).array();
	}
	
	/**
	 * Decodes the little endian encoded bytes to a long
	 * @param bytes The bytes to decode
	 * @return the decoded long value
	 */
	public static long decodeLittleEndianLongBytes(byte[] bytes) {
		return ((ByteBuffer) ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).put(bytes).flip()).getLong();
	}
	

}
