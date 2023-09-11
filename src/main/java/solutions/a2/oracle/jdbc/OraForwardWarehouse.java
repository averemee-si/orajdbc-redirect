/**
 * Copyright (c) 2018-present, A2 Rešitve d.o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package solutions.a2.oracle.jdbc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * SQL statement warehouse
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardWarehouse implements OraForwardTranslateOrRecord {

	private final static int QUEUE_SIZE = 16384;

	private static OraForwardWarehouse instance;
	private final ConcurrentHashMap<String, String> warehouse;
	private final String probeFileName;
	private final ThreadPoolExecutor execWrite;
	private final BufferedWriter writer;

	private OraForwardWarehouse(final String probeFileName) throws SQLException {
		this.probeFileName = probeFileName;
		this.warehouse = new ConcurrentHashMap<>();
		this.execWrite = new ThreadPoolExecutor(
				1, 1, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_SIZE));
		execWrite.setThreadFactory(new DaemonThreadFactory());
		execWrite.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		try {
			writer = new BufferedWriter(new FileWriter(probeFileName));
		} catch (IOException ioe) {
			throw new SQLException(ioe);
		}
	}

	public static OraForwardWarehouse getInstance(final String probeFileName) throws SQLException {
		if (instance == null) {
			synchronized (OraForwardWarehouse.class) {
				instance = new OraForwardWarehouse(probeFileName);
			}
		}
		return instance;
	}

	@Override
	public String translate(final String sql) throws SQLException {
		final String sql_id = OraForwardUtils.sql_id(sql);
		if (warehouse.put(sql_id, sql) == null) {
			final WriterJob writerJob = new WriterJob(writer, sql_id, sql);
			execWrite.submit(writerJob);
		}
		return sql;
	}

	private static class WriterJob implements Runnable {

		private final BufferedWriter writer;
		private final String sql_id;
		private final String sql;
		
		WriterJob(final BufferedWriter writer, final String sql_id, final String sql) {
			this.writer = writer;
			this.sql_id = sql_id;
			this.sql = sql;
		}

		@Override
		public void run() {
			try {
				OraForwardUtils.write2Yaml(writer, sql_id, sql);
				writer.flush();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}

	/**
	 * Hands out threads from the wrapped ThreadFactory with setDeamon(true), so the
	 * threads won't keep the JVM alive when it should otherwise exit.
	 */
	private static class DaemonThreadFactory implements ThreadFactory {
		private final ThreadFactory factory;

		/**
		 * Construct a ThreadFactory with setDeamon(true) using
		 * Executors.defaultThreadFactory()
		 */
		public DaemonThreadFactory() {
			this(Executors.defaultThreadFactory());
		}

		/**
		 * Construct a ThreadFactory with setDeamon(true) wrapping the given factory
		 *
		 * @param thread
		 *              factory to wrap
		 */
		public DaemonThreadFactory(ThreadFactory factory) {
			if (factory == null) {
				throw new NullPointerException("factory cannot be null");
			}
			this.factory = factory;
		}

		@Override
		public Thread newThread(Runnable r) {
			final Thread t = factory.newThread(r);
			t.setDaemon(true);
			return t;
		}
	}
    
}
