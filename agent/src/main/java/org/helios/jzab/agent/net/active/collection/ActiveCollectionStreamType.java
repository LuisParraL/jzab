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
package org.helios.jzab.agent.net.active.collection;

import java.nio.ByteOrder;

import org.helios.jzab.agent.util.AbstractReadableWritableByteChannelBufferFactory;
import org.helios.jzab.agent.util.IReadableWritableByteChannelBufferFactory;
import org.helios.jzab.agent.util.ReadableWritableByteChannelBuffer;

/**
 * <p>Title: ActiveCollectionStreamType</p>
 * <p>Description: Defines the buffering mechanisms available for accumulating active collection results.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.jzab.agent.net.active.ActiveCollectionStreamType</code></p>
 */
public enum ActiveCollectionStreamType implements IReadableWritableByteChannelBufferFactory {
	/** Uses a dynamic heap buffer to accumulate results */
	MEMORY(false, new AbstractReadableWritableByteChannelBufferFactory(){
	public ReadableWritableByteChannelBuffer newInstance(ByteOrder order, int size) {
		return ReadableWritableByteChannelBuffer.newDynamic(order, size);
	}}),
	/** Uses a dynamic direct (non-heap) buffer to accumulate results */
	DIRECTMEMORY(false, new AbstractReadableWritableByteChannelBufferFactory(){
	public ReadableWritableByteChannelBuffer newInstance(ByteOrder order, int size) {
		return ReadableWritableByteChannelBuffer.newDirectDynamic(order, size);
	}}),
	/** Uses a temporary file buffer to accumulate results */
	DISK(true, new AbstractReadableWritableByteChannelBufferFactory(){
	public ReadableWritableByteChannelBuffer newInstance(ByteOrder order, int size) {
		return ReadableWritableByteChannelBuffer.newDynamic(order, size);
	}}),
	/** Uses a temporary memory mapped file buffer to accumulate results */
	DIRECTDISK(true, new AbstractReadableWritableByteChannelBufferFactory(){
	public ReadableWritableByteChannelBuffer newInstance(ByteOrder order, int size) {
		return ReadableWritableByteChannelBuffer.newDirectDynamic(order, size);
	}});
	

	

	
	/**
	 * Creates a new ActiveCollectionStreamType
	 * @param boolean true if the streamer is disk based, false if it is memory based 
	 * @param factory The buffer factory
	 */
	private ActiveCollectionStreamType(boolean diskBased, IReadableWritableByteChannelBufferFactory factory) {
		this.factory = factory;
		this.diskBased = diskBased;
	}
	
	/** The factory used to create {@link ReadableWritableByteChannelBuffer} instances */
	private final IReadableWritableByteChannelBufferFactory factory;
	/** Indicates if the streamer is disk based */
	private final boolean diskBased;

	/**
	 * {@inheritDoc}
	 * @see org.helios.jzab.agent.util.IReadableWritableByteChannelBufferFactory#newInstance(java.nio.ByteOrder, int)
	 */
	@Override
	public ReadableWritableByteChannelBuffer newInstance(ByteOrder order, int size) {
		return factory.newInstance(order, size);
	}

	/**
	 * {@inheritDoc}
	 * @see org.helios.jzab.agent.util.IReadableWritableByteChannelBufferFactory#newInstance(int)
	 */
	@Override
	public ReadableWritableByteChannelBuffer newInstance(int size) {		
		return factory.newInstance(size);
	}

	/**
	 * Creates a new IActiveCollectionStream
	 * @param order The byte order of the underlying buffer
	 * @param size The size of the underlying buffer
	 * @return a new IActiveCollectionStream
	 */
	public IActiveCollectionStream newCollectionStream(ByteOrder order, int size) {
		if(isDiskBased()) {
			return new FileActiveCollectionStream(factory.newInstance(order, size));
		}
		return new ActiveCollectionStream(factory.newInstance(order, size));
	}
	
	/**
	 * Creates a new IActiveCollectionStream usng the native byte order
	 * @param size The size of the underlying buffer
	 * @return a new IActiveCollectionStream
	 */
	public IActiveCollectionStream newCollectionStream(int size) {
		return newCollectionStream(size);
	}
	

	/**
	 * Returns the buffer factory for this type
	 * @return the buffer factory for this type
	 */
	public IReadableWritableByteChannelBufferFactory getFactory() {
		return factory;
	}

	/**
	 * Indicates if this type buffers to disk
	 * @return true if this type buffers to disk, false if it buffers in memory
	 */
	public boolean isDiskBased() {
		return diskBased;
	}

}