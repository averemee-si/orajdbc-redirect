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

package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.jdbc.internal.AbstractConnectionBuilder;
import oracle.jdbc.internal.Monitor;
import solutions.a2.oracle.jdbc.OraForwardConnection;
import solutions.a2.oracle.jdbc.OraForwardTranslateOrRecord;
import solutions.a2.oracle.jdbc.OraForwardTranslator;
import solutions.a2.oracle.jdbc.OraForwardUrlParser;
import solutions.a2.oracle.jdbc.OraForwardUtils;
import solutions.a2.oracle.jdbc.OraForwardWarehouse;
import solutions.a2.oracle.jdbc.OraWrappedConnection;

/**
 * 
 * Oracle JDBC Redirect Driver implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OracleDriver implements Driver, Monitor  {

	private static final Logger LOGGER = Logger.getLogger("solutions.a2.oracle.jdbc.OracleDriver");
	protected static Properties DEFAULT_CONNECTION_PROPERTIES = null;
	private static ExecutorService threadPool = null;
	private Monitor.CloseableLock monitorLock = null;

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		return connect(url, info, AbstractConnectionBuilder.unconfigured());
	}

	public Connection connect(String url, Properties info, AbstractConnectionBuilder<?, ?> acb) throws SQLException {
		if (acceptsURL(url)) {
			final OraForwardUrlParser urlParser = new OraForwardUrlParser(url);
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "The friver will use '" + urlParser.getDriverMode() + "' mode.");
			}

			if (OraForwardUrlParser.A2_DRIVER_MODE_PROBE.equals(urlParser.getDriverMode()) ||
					OraForwardUrlParser.A2_DRIVER_MODE_PROXY.equals(urlParser.getDriverMode())) {
				if (monitorLock == null) {
					monitorLock = this.newDefaultLock();
				}
				if (DEFAULT_CONNECTION_PROPERTIES == null) {
					// Create required field and load resources
					DEFAULT_CONNECTION_PROPERTIES = new Properties();
					try {
						final InputStream resourceAsStream = OracleDriver.class.getResourceAsStream("/oracle/jdbc/defaultConnectionProperties.properties");
						if (resourceAsStream != null) {
							OracleDriver.DEFAULT_CONNECTION_PROPERTIES.load(resourceAsStream);
						}
					}
			        catch (IOException ioe) {}
				}
				if (threadPool == null) {
					threadPool = Executors.newCachedThreadPool(new ThreadFactory() {
						private final AtomicInteger numCreatedThreads = new AtomicInteger(0);
						private static final String THREAD_NAME_PREFIX = "OJDBC-WORKER-THREAD-";
						
						@Override
						public Thread newThread(final Runnable target) {
							final Thread thread = new Thread(null, target, THREAD_NAME_PREFIX + numCreatedThreads.incrementAndGet());
							thread.setPriority(5);
							thread.setDaemon(true);
							return thread;
						}
					});
				}

				final OraForwardTranslateOrRecord translator;
				if (OraForwardUrlParser.A2_DRIVER_MODE_PROBE.equals(urlParser.getDriverMode())) {
					translator = OraForwardWarehouse.getInstance(
							urlParser.getProbeFileName());
				} else {
					//A2_DRIVER_MODE_PROXY
					translator =  OraForwardTranslator.getInstance(
							urlParser.isYamlStore(),
							urlParser.getMappingUrl());
				}
				//Design is only for Type IV drivers.
				final OracleDriverExtension driverExtension = new T4CDriverExtension();
				final Connection connection = driverExtension.getConnection(urlParser.getUrl(), info, acb);
				// Set protocolId
				if (connection instanceof PhysicalConnection) {
					((PhysicalConnection)connection).protocolId = 0;
				}
				return new OraWrappedConnection(connection, translator);
			} else {
				final OraForwardTranslator translator =  OraForwardTranslator.getInstance(
						urlParser.isYamlStore(),
						urlParser.getMappingUrl());
				final Driver oraDriver = DriverManager.getDriver(urlParser.getUrl());
				return new OraForwardConnection(oraDriver.connect(urlParser.getUrl(), info),
						info, translator, urlParser.getDbType());
			}
		}
		return null;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url.startsWith(OraForwardUrlParser.ORA_PREFIX);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		//TODO
		//TODO
		return null;
	}

	public String processSqlEscapes(final String s) throws SQLException {
		//TODO
		//TODO
		return null;
	}

	@Override
	public int getMajorVersion() {
		return OraForwardUtils.getVersionMajor();
	}

	@Override
	public int getMinorVersion() {
		return OraForwardUtils.getVersionMinor();
	}

	@Override
	public boolean jdbcCompliant() {
		return true;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// return Oracle JDBC logger
		return Logger.getLogger("oracle.jdbc");
	}

	@Override
	public Monitor.CloseableLock getMonitorLock() {
		return monitorLock;
	}

	public static boolean getSystemPropertyDateZeroTime() {
		return GeneratedPhysicalConnection.getSystemPropertyDateZeroTime("false").equalsIgnoreCase("true");
	}

	public static boolean getSystemPropertyDateZeroTimeExtra() {
		return GeneratedPhysicalConnection.getSystemPropertyDateZeroTimeExtra("false").equalsIgnoreCase("true");
	}
    
	public static ExecutorService getExecutorService() throws SQLException {
		return threadPool;
	}

	public static void setExecutorService(final ExecutorService threadPool) throws SQLException {
		if (OracleDriver.threadPool != null) {
			OracleDriver.threadPool.shutdownNow();
		}
		OracleDriver.threadPool = threadPool;
	}

}
