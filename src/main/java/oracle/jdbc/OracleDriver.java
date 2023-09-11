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

package oracle.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * Oracle JDBC Redirect Driver implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OracleDriver extends oracle.jdbc.driver.OracleDriver  {

	public static final boolean isDMS() {
		return false;
	}

	public static final boolean isInServer() {
		return false;
	}

	@Deprecated
	public static final boolean isJDK14() {
		return true;
	}

	public static final boolean isDebug() {
		return false;
	}

	public static final boolean isPrivateDebug() {
		return false;
	}

	public static final String getJDBCVersion() {
		return "JDBC 4.2";
	}

	public static final String getDriverVersion() {
		return "21.10.a2";
	}

	public static final String getBuildDate() {
		return "20230901";
	}

	static {
		try {
			DriverManager.registerDriver(new OracleDriver());
		} catch (SQLException sqle) {
			throw new RuntimeException("Unable to register orajdbc-redirect driver!");
		}
	}
}
