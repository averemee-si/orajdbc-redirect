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

import java.sql.SQLException;

/**
 * 
 * URL Parser for Oracle JDBC Redirect package
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardUrlParser {

	/**
	 * 'a2.mapping.file.url' - URL to SQL mapping file.
	 *   File must be yaml in format:
	 *  SQL-ID:
	 *     SQL-TEXT 
	 *  or <a href="https://chronicle.software/">Chronicle Map</a>
	 *  Default - file:///opt/a2/mapping.yaml
	 */
	public static final String A2_MAPPING_FILE_URL = "a2.mapping.file.url";
	/**
	 * 'a2.mapping.file.type' - file type of SQL mapping file.
	 *   Allowed values 'yaml' or 'chronicle'
	 */
	public static final String A2_MAPPING_FILE_TYPE = "a2.mapping.file.type";
	/**
	 * 'a2.backing.driver' - type of backing driver.
	 *   Allowed values 'PostgreSQL' or 'MariaDB'
	 */
	public static final String A2_BACKING_DRIVER = "a2.backing.driver";
	/**
	 * 'a2.driver.mode' - forward/probe/proxy
	 *   If forward - driver redirects all SQLs to 3rd driver and replaces it if needed
	 *   If probe - driver records all Oracle commands to file
	 *   If proxy - driver just replaces SQL statements and uses Oracle driver
	 * Default - forward.
	 */
	public static final String A2_DRIVER_MODE = "a2.driver.mode";
	public static final String A2_DRIVER_MODE_FORWARD = "forward";
	public static final String A2_DRIVER_MODE_PROBE = "probe";
	public static final String A2_DRIVER_MODE_PROXY = "proxy";
	/**
	 * 'a2.probe.file' - filename for writing SQL statements used only when a2.probe.mode=true
	 *  Default - /opt/a2/probe.yaml
	 */
	public static final String A2_PROBE_FILE = "a2.probe.file";
	/**
	 *  Oracle JDBC Prefix
	 */
	public static final String ORA_PREFIX = "jdbc:oracle:thin:";

	private static final String PG_JDBC = "jdbc:postgresql://";
	private static final String MD_JDBC = "jdbc:mariadb://";

	private String url;
	private int dbType = OraForwardUtils.POSTGRESQL;
	private String mappingUrl = null;
	private boolean yamlStore = true;
	private String driverMode = A2_DRIVER_MODE_FORWARD;
	private String probeFileName;

	public OraForwardUrlParser(final String sourceUrl) throws SQLException {
		if (sourceUrl.substring(ORA_PREFIX.length()).contains("TNS_ADMIN=")) {
			throw new SQLException("TNS Alias Format for JDBC URL format is not supported!\n" +
					"Please use EZConnect format and not " + sourceUrl + "!");
		} else if (sourceUrl.substring(ORA_PREFIX.length()).startsWith("@(DESCRIPTION")) {
			throw new SQLException("TNS URL Format for JDBC URL format is not supported!\n" +
					"Please use EZConnect format and not " + sourceUrl + "!");
		} else {
			final int paramsPos = sourceUrl.indexOf('?');
			String driverParams = null;
			String hostPortDb;
			if (paramsPos > -1) {
				for (final String param : sourceUrl.substring(paramsPos + 1).split("&")) {
					if (param.startsWith(A2_MAPPING_FILE_URL)) {
						mappingUrl = parseParamValue(A2_MAPPING_FILE_URL, param);
					} else if (param.startsWith(A2_MAPPING_FILE_TYPE)) {
						final String mappingFileType = parseParamValue(A2_MAPPING_FILE_TYPE, param);
						if ("yaml".equals(mappingFileType)) {
							yamlStore = true;
						} else if ("chronicle".equals(mappingFileType)) {
							yamlStore = false;
						} else {
							throw new SQLException(
									"For the '" + A2_MAPPING_FILE_TYPE + "' only yaml and chronicle are allowed!");
						}
					} else if (param.startsWith(A2_BACKING_DRIVER)) {
						final String backingFileType = parseParamValue(A2_BACKING_DRIVER, param);
						if ("postgres".equals(backingFileType)) {
							dbType = OraForwardUtils.POSTGRESQL;
						} else if ("mariadb".equals(backingFileType)) {
							dbType = OraForwardUtils.MARIADB;
						} else {
							throw new SQLException(
									"For the '" + A2_BACKING_DRIVER + "' only postgres and mariadb are allowed!");
						}
					} else if (param.startsWith(A2_DRIVER_MODE)) {
						final String probeModeAsString = parseParamValue(A2_DRIVER_MODE, param);
						if (A2_DRIVER_MODE_FORWARD.equals(probeModeAsString)) {
							driverMode = A2_DRIVER_MODE_FORWARD;
						} else if (A2_DRIVER_MODE_PROBE.equals(probeModeAsString)) {
							driverMode = A2_DRIVER_MODE_PROBE;
						} else if (A2_DRIVER_MODE_PROXY.equals(probeModeAsString)) {
							driverMode = A2_DRIVER_MODE_PROXY;
						} else {
							throw new SQLException(
									"For the '" + A2_DRIVER_MODE + "' only forward, probe, and proxy are allowed!");
						}
					} else if (param.startsWith(A2_PROBE_FILE)) {
						probeFileName = parseParamValue(A2_PROBE_FILE, param);
					} else {
						if (driverParams == null) {
							driverParams = new String(param);
						} else {
							driverParams = driverParams + "&" + param;
						}
					}
				}
				if (A2_DRIVER_MODE_PROBE.equals(driverMode) ||
						A2_DRIVER_MODE_PROXY.equals(driverMode)) {
					hostPortDb = sourceUrl.substring(0, paramsPos);
				} else {
					// A2_DRIVER_MODE_FORWARD
					hostPortDb = sourceUrl.substring(ORA_PREFIX.length(), paramsPos);
				}
			} else {
				//No additional parameters are passed, default values will be used...
				hostPortDb = sourceUrl.substring(ORA_PREFIX.length());
			}

			// We need this parameter only when (driverMode == forward || proxy)
			if (A2_DRIVER_MODE_FORWARD.equals(driverMode) ||
					A2_DRIVER_MODE_PROXY.equals(driverMode)) {
				if (mappingUrl == null) {
					//TODO - detect OS and use different path for Windows!
					mappingUrl = "file:///tmp/mapping.yaml";
				} else {
					if (!mappingUrl.startsWith("file://") || !mappingUrl.startsWith("s3://")) {
						mappingUrl = "file://" + mappingUrl;
					}
				}
			}

			if (A2_DRIVER_MODE_PROBE.equals(driverMode) ||
					A2_DRIVER_MODE_PROXY.equals(driverMode)) {
				url = hostPortDb;
				if (A2_DRIVER_MODE_PROBE.equals(driverMode) && probeFileName == null) {
					//TODO - detect OS and use different path for Windows!
					probeFileName = "/tmp/probe.yaml";
				}
			} else {
				// A2_DRIVER_MODE_FORWARD
				if (hostPortDb.startsWith("@//")) {
					hostPortDb = hostPortDb.substring(3);
				} else if (hostPortDb.startsWith("@")) {
					hostPortDb = hostPortDb.substring(1);
				} else {
					//TODO
					//TODO Need to parse more...
					//TODO
					throw new SQLException("This URL Format for JDBC URL format is not supported!\n" +
							"Please use EZConnect format and not " + sourceUrl + "!");
				}
				if (dbType == OraForwardUtils.POSTGRESQL) {
					url = PG_JDBC + hostPortDb;
				} else {
					// dbType == OraForwardUtils.MARIADB
					url = MD_JDBC + hostPortDb;
				}
			}

			if (driverParams != null) {
				url = url + "?" + driverParams;
			}
		}
	}

	private String parseParamValue(final String paramName, final String nameValuePair) throws SQLException {
		final int pos = nameValuePair.indexOf('=');
		if (pos == -1) {
			throw new SQLException(
					String.format("Unable to parse '%s' parameter! " +
							"Value '%s' is wrong!", paramName, nameValuePair));
		} else {
			return nameValuePair.substring(pos + 1);
		}
	}

	public String getUrl() {
		return url;
	}

	public int getDbType() {
		return dbType;
	}

	public String getMappingUrl() {
		return mappingUrl;
	}

	public boolean isYamlStore() {
		return yamlStore;
	}

	public String getDriverMode() {
		return driverMode;
	}

	public String getProbeFileName() {
		return probeFileName;
	}

}
