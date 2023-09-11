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

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import javax.transaction.xa.XAResource;

import oracle.jdbc.LogicalTransactionId;
import oracle.jdbc.LogicalTransactionIdEventListener;
import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.OracleShardingKey;
import oracle.jdbc.aq.AQDequeueOptions;
import oracle.jdbc.aq.AQEnqueueOptions;
import oracle.jdbc.aq.AQMessage;
import oracle.jdbc.aq.AQMessageProperties;
import oracle.jdbc.aq.AQNotificationRegistration;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.diagnostics.SecuredLogger;
import oracle.jdbc.driver.HAManager;
import oracle.jdbc.internal.DatabaseSessionState;
import oracle.jdbc.internal.JMSDequeueOptions;
import oracle.jdbc.internal.JMSEnqueueOptions;
import oracle.jdbc.internal.JMSMessage;
import oracle.jdbc.internal.JMSNotificationRegistration;
import oracle.jdbc.internal.JMSNotificationRegistration.Directive;
import oracle.jdbc.internal.KeywordValueLong;
import oracle.jdbc.internal.NetStat;
import oracle.jdbc.internal.OracleArray;
import oracle.jdbc.internal.OracleBfile;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.internal.OracleLargeObject;
import oracle.jdbc.internal.OracleStatement;
import oracle.jdbc.internal.PDBChangeEventListener;
import oracle.jdbc.internal.ReplayContext;
import oracle.jdbc.internal.ResultSetCache;
import oracle.jdbc.internal.XSEventListener;
import oracle.jdbc.internal.XSKeyval;
import oracle.jdbc.internal.XSNamespace;
import oracle.jdbc.internal.XSPrincipal;
import oracle.jdbc.internal.XSSecureId;
import oracle.jdbc.internal.XSSessionParameters;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCLOB;
import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.BfileDBAccess;
import oracle.sql.BlobDBAccess;
import oracle.sql.CLOB;
import oracle.sql.ClobDBAccess;
import oracle.sql.CustomDatum;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TIMEZONETAB;
import oracle.sql.TypeDescriptor;

/**
 * 
 * Oracle JDBC Forward Connection implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardConnection extends OraForwardWrapper implements OracleConnection {

	private final Connection shadow;
	private final OraForwardTranslator translator;
	private final int dbType;

	private int dummyRowPrefetchSize = 10;
	private TimeZone defaultTimeZone;
	private boolean includeSynonyms;
	private boolean remarksReporting;
	private boolean restrictGetTables;
	private boolean usingXA;
	private boolean xaErrorFlag;
	//TODO
	private boolean logging;
	private boolean explicitCachingEnabled;
	private boolean implicitCachingEnabled;
	private String userName;
	private String currentSchema;
	private Object aCProxy;

	public OraForwardConnection(
			Connection connection, Properties info, OraForwardTranslator translator, int dbType)
					throws SQLException {
		super(connection);
		this.shadow = connection;
		this.translator = translator;
		this.dbType = dbType;
		// Parse some Oracle specific properties....
		if (info.containsKey(OracleConnection.CONNECTION_PROPERTY_AUTOCOMMIT) && 
				"false".equalsIgnoreCase(info.getProperty(OracleConnection.CONNECTION_PROPERTY_AUTOCOMMIT))) {
			shadow.setAutoCommit(false);
		}
		//oracle.jdbc.commitSelectOnAutocommit - ?
		//defaultRowPrefetch - ?
	}

	@Override
	public Statement createStatement() throws SQLException {
		return new OraForwardStatement(this,
				shadow.createStatement(), translator, dbType); 
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return new OraForwardStatement(this,
				shadow.createStatement(resultSetType, resultSetConcurrency),
				translator, dbType);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new OraForwardStatement(this,
				shadow.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability),
				translator, dbType);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardCallableStatement(this, shadow.prepareCall(holder.translated),
				translator, dbType, holder);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardCallableStatement(this,
				shadow.prepareCall(holder.translated, resultSetType, resultSetConcurrency),
				translator, dbType, holder);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardCallableStatement(this,
				shadow.prepareCall(holder.translated, resultSetType, resultSetConcurrency, resultSetHoldability),
				translator, dbType, holder);
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardPreparedStatement(this, shadow.prepareStatement(holder.translated),
				translator, dbType, holder);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardPreparedStatement(this,
				shadow.prepareStatement(holder.translated, resultSetType, resultSetConcurrency),
				translator, dbType, holder);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardPreparedStatement(this,
				shadow.prepareStatement(holder.translated, resultSetType, resultSetConcurrency, resultSetHoldability),
				translator, dbType, holder);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardPreparedStatement(this,
				shadow.prepareStatement(holder.translated, autoGeneratedKeys),
				translator, dbType, holder);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardPreparedStatement(this,
				shadow.prepareStatement(holder.translated, columnIndexes),
				translator, dbType, holder);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		final OraForwardTranslator.Holder holder = translator.translateAndConvertParams(sql);
		return new OraForwardPreparedStatement(this,
				shadow.prepareStatement(holder.translated, columnNames),
				translator, dbType, holder);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return shadow.nativeSQL(translator.translate(sql));
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		shadow.setAutoCommit(autoCommit);
		
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return shadow.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		shadow.commit();
	}

	@Override
	public void rollback() throws SQLException {
		shadow.rollback();
	}

	@Override
	public void close() throws SQLException {
		shadow.close();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return shadow.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return shadow.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		shadow.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return shadow.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		shadow.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return shadow.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		shadow.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return shadow.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return shadow.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		shadow.clearWarnings();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return shadow.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		shadow.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		shadow.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return shadow.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return shadow.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return shadow.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		shadow.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		shadow.releaseSavepoint(savepoint);
	}

	@Override
	public Clob createClob() throws SQLException {
		return shadow.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return shadow.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return shadow.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return shadow.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return shadow.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		shadow.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		shadow.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return shadow.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return shadow.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return shadow.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return shadow.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		shadow.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return shadow.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		shadow.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		shadow.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return shadow.getNetworkTimeout();
	}

	//
	// Oracle extension redefinition...
	//

	@Override
	public Connection _getPC() {
		return shadow;
	}

	@Override
	public int getDefaultRowPrefetch() {
		return dummyRowPrefetchSize;
	}

	@Override
	public void setDefaultRowPrefetch(int size) throws SQLException {
		dummyRowPrefetchSize = size;
	}

	@Override
	public TimeZone getDefaultTimeZone() throws SQLException {
		return defaultTimeZone;
	}

	@Override
	public void setDefaultTimeZone(TimeZone tz) throws SQLException {
		defaultTimeZone = tz;
	}

	@Override
	public boolean getIncludeSynonyms() {
		return includeSynonyms;
	}

	@Override
	public void setIncludeSynonyms(boolean includeSynonyms) {
		this.includeSynonyms = includeSynonyms;
	}

	@Override
	public boolean getRemarksReporting() {
		return remarksReporting;
	}

	@Override
	public void setRemarksReporting(boolean remarksReporting) {
		this.remarksReporting = remarksReporting;
	}

	@Override
	public boolean getRestrictGetTables() {
		return restrictGetTables;
	}

	@Override
	public void setRestrictGetTables(boolean restrictGetTables) {
		this.restrictGetTables = restrictGetTables;
	}

	@Deprecated
	@Override
	public boolean getUsingXAFlag() {
		return usingXA;
	}

	@Deprecated
	@Override
	public void setUsingXAFlag(boolean usingXA) {
		this.usingXA = usingXA;
	}

	@Deprecated
	@Override
	public boolean getXAErrorFlag() {
		return xaErrorFlag;
	}

	@Deprecated
	@Override
	public void setXAErrorFlag(boolean xaErrorFlag) {
		this.xaErrorFlag = xaErrorFlag;
	}

	@Override
	public void disableLogging() throws SQLException {
		logging = false;
	}

	@Override
	public void enableLogging() throws SQLException {
		logging = true;
	}

	@Deprecated
	@Override
	public CallableStatement prepareCallWithKey(String key) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("prepareCallWithKey");
	}

	@Deprecated
	@Override
	public PreparedStatement prepareStatementWithKey(String key) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("prepareStatementWithKey");
	}

	@Override
	public boolean isValid(ConnectionValidation effort, int timeout) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("isValid");
	}

	@Override
	public PreparedStatement getStatementWithKey(String key) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getStatementWithKey");
	}

	@Override
	public void abort() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("abort");
	}

	@Deprecated
	@Override
	public void applyConnectionAttributes(Properties connAttr) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("applyConnectionAttributes");
	}

	@Deprecated
	@Override
	public void archive(int mode, int aseq, String acstext) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("archive");
	}

	@Override
	public void cancel() throws SQLException {
		close();
	}

	@Deprecated
	@Override
	public void clearAllApplicationContext(String nameSpace) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("clearAllApplicationContext");
	}

	@Deprecated
	@Override
	public void close(Properties connAttr) throws SQLException {
		close();
	}

	@Override
	public void close(int opt) throws SQLException {
		close();
	}

	@Override
	public void commit(EnumSet<CommitOption> options) throws SQLException {
		commit();
	}

	@Override
	public OracleConnection unwrap() {
		return this;
	}

	@Override
	public boolean getExplicitCachingEnabled() throws SQLException {
		return explicitCachingEnabled;
	}

	@Override
	public void setExplicitCachingEnabled(boolean explicitCachingEnabled) throws SQLException {
		this.explicitCachingEnabled = explicitCachingEnabled;
	}

	@Override
	public boolean getImplicitCachingEnabled() throws SQLException {
		return implicitCachingEnabled;
	}

	@Override
	public void setImplicitCachingEnabled(boolean implicitCachingEnabled) throws SQLException {
		this.implicitCachingEnabled = implicitCachingEnabled;
	}

	@Override
	public String getUserName() throws SQLException {
		if (userName == null) {
			final String query;
			if (dbType == OraForwardUtils.POSTGRESQL) {
				query = "SELECT session_user";
			} else {
				// OraRedirectUtils.MARIADB
				query = "SELECT USER()";
			}
			userName = getFirstColumnFromFirstRowDb(query);
		}
		return userName;
	}

	@Override
	public String getCurrentSchema() throws SQLException {
		if (currentSchema == null) {
			final String query;
			if (dbType == OraForwardUtils.POSTGRESQL) {
				query = "SELECT current_schema";
			} else {
				// OraRedirectUtils.MARIADB
				query = "SELECT USER()";
			}
			currentSchema = getFirstColumnFromFirstRowDb(query);
		}
		return currentSchema;
	}

	private String getFirstColumnFromFirstRowDb(final String query) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String result = null;
		try {
			statement = shadow.prepareStatement(query);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getString(1);
			} else {
				throw new SQLException("Unable to execute + '" + query + "'!");
			}
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			resultSet = null;
			statement = null;
		}
		return result;
	}

	@Override
	public boolean getAutoClose() throws SQLException {
		return true;
	}

	@Override
	public void setAutoClose(boolean autoClose) throws SQLException {
		if (!autoClose) {
			throw new SQLException("Unable to disable connection auto close!");
		}
	}

	@Override
	public boolean isLogicalConnection() {
		return true;
	}

	@Override
	public boolean isProxySession() {
		return false;
	}

	@Override
	public boolean getCreateStatementAsRefCursor() {
		//TODO
		return false;
	}

	@Override
	public void setCreateStatementAsRefCursor(boolean value) {
	}

	@Override
	public void setWrapper(oracle.jdbc.OracleConnection wrapper) {
		//TODO
	}

	@Override
	public ARRAY createARRAY(String typeName, Object elements) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("createARRAY");
	}

	@Override
	public BINARY_DOUBLE createBINARY_DOUBLE(double value) throws SQLException {
		return new BINARY_DOUBLE(value);
	}

	@Override
	public BINARY_FLOAT createBINARY_FLOAT(float value) throws SQLException {
		return new BINARY_FLOAT(value);
	}

	@Override
	public DATE createDATE(Date value) throws SQLException {
		return new DATE(value);
	}

	@Override
	public DATE createDATE(Time value) throws SQLException {
		return new DATE(value);
	}

	@Override
	public DATE createDATE(Timestamp value) throws SQLException {
		return new DATE(value);
	}

	@Override
	public DATE createDATE(String value) throws SQLException {
		return new DATE(value);
	}

	@Override
	public DATE createDATE(Date value, Calendar cal) throws SQLException {
		return new DATE(value, cal);
	}

	@Override
	public DATE createDATE(Time value, Calendar cal) throws SQLException {
		return new DATE(value, cal);
	}

	@Override
	public DATE createDATE(Timestamp value, Calendar cal) throws SQLException {
		return new DATE(value, cal);
	}

	@Override
	public INTERVALDS createINTERVALDS(String value) throws SQLException {
		return new INTERVALDS(value);
	}

	@Override
	public INTERVALYM createINTERVALYM(String value) throws SQLException {
		return new INTERVALYM(value);
	}

	@Override
	public NUMBER createNUMBER(boolean value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(byte value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(short value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(int value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(long value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(float value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(double value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(BigDecimal value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(BigInteger value) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(String value, int scale) throws SQLException {
		return new NUMBER(value);
	}

	@Override
	public Array createOracleArray(String arrayTypeName, Object elements) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("createOracleArray");
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Date value) throws SQLException {
		return new TIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(DATE value) throws SQLException {
		return new TIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Time value) throws SQLException {
		return new TIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Timestamp value) throws SQLException {
		return new TIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(String value) throws SQLException {
		return new TIMESTAMP(value);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Date value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Time value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Timestamp value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(String value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(DATE value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Date value) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Time value) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp value) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(String value) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(DATE value) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Date value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Time value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(String value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Timestamp value, Calendar cal) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp value, ZoneId tzid) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public AQMessage dequeue(String queueName, AQDequeueOptions opt, byte[] tdo) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("dequeue");
	}

	@Override
	public AQMessage dequeue(String queueName, AQDequeueOptions opt, byte[] tdo, int version) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("dequeue");
	}

	@Override
	public AQMessage[] dequeue(String queueName, AQDequeueOptions opt, byte[] tdo, int version, int deqSize)
			throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("dequeue");
	}

	@Override
	public AQMessage dequeue(String queueName, AQDequeueOptions opt, String typeName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("dequeue");
	}

	@Override
	public AQMessage[] dequeue(String queueName, AQDequeueOptions opt, String typeName, int deqSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("dequeue");
	}

	@Override
	public void enqueue(String queueName, AQEnqueueOptions opt, AQMessage mesg) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("enqueue");
	}

	@Override
	public int enqueue(String queueName, AQEnqueueOptions opt, AQMessage[] mesgs) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("enqueue");
	}

	@Override
	public TypeDescriptor[] getAllTypeDescriptorsInCurrentSchema() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getAllTypeDescriptorsInCurrentSchema");
	}

	@Override
	public String getAuthenticationAdaptorName() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getAuthenticationAdaptorName");
	}

	@Override
	public CallableStatement getCallWithKey(String key) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCallWithKey");
	}

	@Deprecated
	@Override
	public Properties getConnectionAttributes() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getConnectionAttributes");
	}

	@Deprecated
	@Override
	public int getConnectionReleasePriority() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getConnectionReleasePriority");
	}

	@Override
	public String getDataIntegrityAlgorithmName() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getDataIntegrityAlgorithmName");
	}

	@Override
	public DatabaseChangeRegistration getDatabaseChangeRegistration(int regid) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getDatabaseChangeRegistration");
	}

	@Deprecated
	@Override
	public int getDefaultExecuteBatch() {
		//TODO
		return -1;
	}

	@Override
	public Object getDescriptor(String sqlName) {
		//TODO
		return null;
	}

	@Override
	public String getEncryptionAlgorithmName() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getEncryptionAlgorithmName");
	}

	@Deprecated
	@Override
	public short getEndToEndECIDSequenceNumber() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getEndToEndECIDSequenceNumber");
	}

	@Deprecated
	@Override
	public String[] getEndToEndMetrics() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getEndToEndMetrics");
	}

	@Deprecated
	@Override
	public Object getJavaObject(String sqlName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getJavaObject");
	}

	@Override
	public Properties getProperties() {
		//TODO
		return null;
	}

	@Deprecated
	@Override
	public String getSQLType(Object obj) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getSQLType");
	}

	@Override
	public String getSessionTimeZone() {
		//TODO
		return null;
	}

	@Override
	public void setSessionTimeZone(String regionName) throws SQLException {
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("setSessionTimeZone");
	}

	@Override
	public String getSessionTimeZoneOffset() throws SQLException {
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("getSessionTimeZoneOffset");
	}

	@Override
	public int getStatementCacheSize() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getStatementCacheSize");
	}

	@Deprecated
	@Override
	public int getStmtCacheSize() {
		return 0;
	}

	@Override
	public void setStatementCacheSize(int size) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setStatementCacheSize");
	}

	@Deprecated
	@Override
	public void setStmtCacheSize(int size) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setStmtCacheSize");
	}

	@Deprecated
	@Override
	public void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setStmtCacheSize");
	}

	@Override
	public short getStructAttrCsId() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getStructAttrCsId");
	}

	@Override
	public TypeDescriptor[] getTypeDescriptorsFromList(String[][] schemaAndTypeNamePairs) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getTypeDescriptorsFromList");
	}

	@Override
	public TypeDescriptor[] getTypeDescriptorsFromListInCurrentSchema(String[] typeNames) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getTypeDescriptorsFromListInCurrentSchema");
	}

	@Deprecated
	@Override
	public Properties getUnMatchedConnectionAttributes() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getUnMatchedConnectionAttributes");
	}

	@Override
	public boolean isUsable() {
		//TODO
		//TODO return !shadow.isClosed();
		return (shadow != null);
	}

	@Override
	public void openProxySession(int type, Properties prop) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("openProxySession");
	}

	@Override
	public void oracleReleaseSavepoint(OracleSavepoint savepoint) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("oracleReleaseSavepoint");
	}

	@Override
	public void oracleRollback(OracleSavepoint savepoint) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("oracleRollback");
	}

	@Override
	public OracleSavepoint oracleSetSavepoint() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("oracleSetSavepoint");
	}

	@Override
	public OracleSavepoint oracleSetSavepoint(String name) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("oracleSetSavepoint");
	}

	@Deprecated
	@Override
	public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
		return null;
	}

	@Override
	public int pingDatabase() throws SQLException {
		//TODO
		return 0;
	}

	@Deprecated
	@Override
	public int pingDatabase(int timeOut) throws SQLException {
		//TODO
		return pingDatabase();
	}

	@Override
	public void purgeExplicitCache() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("purgeExplicitCache");
	}

	@Override
	public void purgeImplicitCache() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("purgeImplicitCache");
	}

	@Override
	public void putDescriptor(String sqlName, Object desc) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("putDescriptor");
	}

	@Override
	public AQNotificationRegistration[] registerAQNotification(String[] name, Properties[] options, Properties globalOptions)
			throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerAQNotification");
	}

	@Deprecated
	@Override
	public void registerConnectionCacheCallback(OracleConnectionCacheCallback occc, Object userObj, int cbkFlag)
			throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerConnectionCacheCallback");
	}

	@Override
	public DatabaseChangeRegistration registerDatabaseChangeNotification(Properties options) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerDatabaseChangeNotification");
	}

	@Deprecated
	@Override
	public void registerSQLType(String sqlName, Class<?> javaClass) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerSQLType");
	}

	@Deprecated
	@Override
	public void registerSQLType(String sqlName, String javaClassName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerSQLType");
	}

	@Override
	public void registerTAFCallback(OracleOCIFailover cbk, Object obj) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerTAFCallback");
	}

	@Deprecated
	@Override
	public void setApplicationContext(String nameSpace, String attribute, String value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setApplicationContext");
	}

	@Deprecated
	@Override
	public void setConnectionReleasePriority(int priority) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setConnectionReleasePriority");
	}

	@Deprecated
	@Override
	public void setDefaultExecuteBatch(int batch) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setDefaultExecuteBatch");
	}

	@Deprecated
	@Override
	public void setEndToEndMetrics(String[] metrics, short sequenceNumber) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setEndToEndMetrics");
	}

	@Override
	public void setPlsqlWarnings(String setting) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setPlsqlWarnings");
	}

	@Override
	public void shutdown(DatabaseShutdownMode mode) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("shutdown");
	}

	@Override
	public void startup(DatabaseStartupMode mode) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("startup");
	}

	@Deprecated
	@Override
	public void startup(String startupString, int mode) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("startup");
	}

	@Override
	public void unregisterAQNotification(AQNotificationRegistration registration) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("unregisterAQNotification");
	}

	@Override
	public void unregisterDatabaseChangeNotification(DatabaseChangeRegistration registration) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("unregisterDatabaseChangeNotification");
	}

	@Deprecated
	@Override
	public void unregisterDatabaseChangeNotification(int registrationId) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("unregisterDatabaseChangeNotification");
	}

	@Override
	public void unregisterDatabaseChangeNotification(long registrationId, String callback) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("unregisterDatabaseChangeNotification");
	}

	@Deprecated
	@Override
	public void unregisterDatabaseChangeNotification(int registrationId, String host, int tcpPort) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("unregisterDatabaseChangeNotification");
	}

	@Override
	public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener listener) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("addLogicalTransactionIdEventListener");
	}

	@Override
	public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener listener, Executor executor)
			throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("addLogicalTransactionIdEventListener");
	}

	@Override
	public void beginRequest() throws SQLException {
		shadow.beginRequest();
	}

	@Override
	public void endRequest() throws SQLException {
		shadow.endRequest();
	}

	@Override
	public boolean attachServerConnection() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("attachServerConnection");
	}

	@Override
	public void detachServerConnection(String tag) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("detachServerConnection");
	}

	@Override
	public String getDRCPPLSQLCallbackName() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getDRCPPLSQLCallbackName");
	}

	@Override
	public String getDRCPReturnTag() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getDRCPReturnTag");
	}

	@Override
	public DRCPState getDRCPState() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getDRCPState");
	}

	@Override
	public LogicalTransactionId getLogicalTransactionId() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("LogicalTransactionId");
	}

	@Override
	public boolean isDRCPEnabled() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("isDRCPEnabled");
	}

	@Override
	public boolean isDRCPMultitagEnabled() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("isDRCPMultitagEnabled");
	}

	@Override
	public boolean needToPurgeStatementCache() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("needToPurgeStatementCache");
	}

	@Override
	public void removeLogicalTransactionIdEventListener(LogicalTransactionIdEventListener listener) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("removeLogicalTransactionIdEventListener");
	}

	@Override
	public void setShardingKey(OracleShardingKey shardingKey, OracleShardingKey superShardingKey) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setShardingKey");
	}

	@Override
	public void setShardingKey(OracleShardingKey shardingKey) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setShardingKey");
	}

	@Override
	public boolean setShardingKeyIfValid(OracleShardingKey shardingKey, OracleShardingKey superShardingKey, int timeout) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setShardingKeyIfValid");
	}

	@Override
	public boolean setShardingKeyIfValid(OracleShardingKey shardingKey, int timeout) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setShardingKeyIfValid");
	}

	@Override
	public void startup(DatabaseStartupMode mode, String pfileName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("startup");
	}

	@Override
	public String getChecksumProviderName() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getChecksumProviderName");
	}

	@Override
	public String getEncryptionProviderName() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getEncryptionProviderName");
	}

	@Override
	public void dumpLog() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("dumpLog");
	}

	@Override
	public SecuredLogger getLogger() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getLogger");
	}

	@Override
	public String getNetConnectionId() throws SQLException {
		//TODO - ?
		throw OraForwardUtils.sqlFeatureNotSupportedException("getServerSessionInfo");
	}

	@Override
	public Properties getServerSessionInfo() throws SQLException {
		//TODO - ?
		throw OraForwardUtils.sqlFeatureNotSupportedException("getServerSessionInfo");
	}

	//
	// oracle.jdbc.internal.OracleConnection
	//

	@Override
	public Object getACProxy() {
		return aCProxy;
	}

	@Override
	public void setACProxy(Object aCProxy) {
		this.aCProxy = aCProxy;
	}

	@Override
	public CloseableLock getMonitorLock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int CHARBytesToJavaChars(byte[] arg0, int arg1, char[] arg2) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean IsNCharFixedWith() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int NCHARBytesToJavaChars(byte[] arg0, int arg1, char[] arg2) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ackJMSNotification(JMSNotificationRegistration arg0, byte[] arg1, Directive arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ackJMSNotification(ArrayList<JMSNotificationRegistration> arg0, byte[][] arg1, Directive arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBfile(OracleBfile arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFeature(ClientFeature arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLargeObject(OracleLargeObject arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addXSEventListener(XSEventListener arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addXSEventListener(XSEventListener arg0, Executor arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginNonRequestCalls() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> classForNameAndSchema(String arg0, String arg1) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanupAndClose() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanupAndClose(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearClientIdentifier(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearDrcpTagName() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInternal(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeLogicalConnection() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BFILE createBfile(byte[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BfileDBAccess createBfileDBAccess() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BLOB createBlob(byte[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlobDBAccess createBlobDBAccess() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BLOB createBlobWithUnpickledBytes(byte[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CLOB createClob(byte[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CLOB createClob(byte[] arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClobDBAccess createClobDBAccess() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CLOB createClobWithUnpickledBytes(byte[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] createLightweightSession(String arg0, KeywordValueLong[] arg1, int arg2, KeywordValueLong[][] arg3,
			int[] arg4) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> descriptorCacheKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doXSNamespaceOp(XSOperationCode arg0, byte[] arg1, XSNamespace[] arg2, XSSecureId arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doXSNamespaceOp(XSOperationCode arg0, byte[] arg1, XSNamespace[] arg2, XSNamespace[][] arg3,
			XSSecureId arg4) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doXSSessionAttachOp(int arg0, byte[] arg1, XSSecureId arg2, byte[] arg3, XSPrincipal arg4,
			String[] arg5, String[] arg6, String[] arg7, XSNamespace[] arg8, XSNamespace[] arg9, XSNamespace[] arg10,
			TIMESTAMPTZ arg11, TIMESTAMPTZ arg12, int arg13, long arg14, XSKeyval arg15, int[] arg16)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doXSSessionChangeOp(XSSessionSetOperationCode arg0, byte[] arg1, XSSecureId arg2,
			XSSessionParameters arg3) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] doXSSessionCreateOp(XSSessionOperationCode arg0, XSSecureId arg1, byte[] arg2, XSPrincipal arg3,
			String arg4, XSNamespace[] arg5, XSSessionModeFlag arg6, XSKeyval arg7) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doXSSessionDestroyOp(byte[] arg0, XSSecureId arg1, byte[] arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doXSSessionDetachOp(int arg0, byte[] arg1, XSSecureId arg2, boolean arg3) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endNonRequestCalls() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endRequest(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeLightweightSessionPiggyback(int arg0, byte[] arg1, KeywordValueLong[] arg2, int arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int freeTemporaryBlobsAndClobs() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getAutoCommitInternal() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBigEndian() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BufferCacheStatistics getByteBufferCacheStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getC2SNlsRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BufferCacheStatistics getCharBufferCacheStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getClassForType(String arg0, Map<String, Class<?>> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getClientInfoInternal() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getCurrentSCN() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Properties getDBAccessProperties() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatabaseSessionState getDatabaseSessionState() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseTimeZone() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getDbCsId() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDefaultFixedString() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDefaultSchemaNameForNamedTypes() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getDerivedKeyInternal(byte[] arg0, int arg1)
			throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDescriptor(byte[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getDriverCharSet() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEOC() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getExecutingRPCFunctionCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getExecutingRPCSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getFDO(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getForm(OracleTypeADT arg0, OracleTypeCLOB arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HAManager getHAManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHeapAllocSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getInstanceProperty(InstanceProperty arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getJDBCStandardBehavior() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Properties getJavaNetProperties() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class<?>> getJavaObjectTypeMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getJdbcCsId() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ReplayContext getLastReplayContext() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getLogicalConnection(OraclePooledConnection arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getMapDateToTimestamp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMaxCharSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxCharbyteSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxNCharbyteSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getNCharSet() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNegotiatedSDU() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getNegotiatedTTCVersion() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NetStat getNetworkStat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOCIEnvHeapAllocSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Properties getOCIHandles() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOutboundConnectTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPercentageQueryExecutionOnDirectShard() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public OracleConnection getPhysicalConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getPropertyForPooledConnection(OraclePooledConnection arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getProtocolType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReplayContext[] getReplayContext() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetCache getResultSetCache() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getStructAttrNCsId() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TIMEZONETAB getTIMEZONETAB() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTdoCState(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTdoCState(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getTimestamptzInGmt() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTimezoneVersionNumber() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EnumSet<TransactionState> getTransactionState() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTxnMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getURL() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getUse1900AsYearForTime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getVarTypeMaxLenCompat() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getVersionNumber() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public oracle.jdbc.OracleConnection getWrapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XAResource getXAResource() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNoOpenHandles() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCharSetMultibyte(short arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnectionBigTZTC() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnectionSocketKeepAlive() throws SocketException, SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDataInLocatorEnabled() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDescriptorSharable(OracleConnection arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLifecycleOpen() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLobStreamPosStandardCompliant() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNetworkCompressionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSafelyClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStatementCacheInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUsable(boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isV8Compatible() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int javaCharsToCHARBytes(char[] arg0, int arg1, byte[] arg2) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int javaCharsToNCHARBytes(char[] arg0, int arg1, byte[] arg2) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JMSMessage jmsDequeue(String arg0, JMSDequeueOptions arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSMessage jmsDequeue(String arg0, JMSDequeueOptions arg1, OutputStream arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSMessage jmsDequeue(String arg0, JMSDequeueOptions arg1, String arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JMSMessage[] jmsDequeue(String arg0, JMSDequeueOptions arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jmsEnqueue(String arg0, JMSEnqueueOptions arg1, JMSMessage arg2, AQMessageProperties arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jmsEnqueue(String arg0, JMSEnqueueOptions arg1, JMSMessage[] arg2, AQMessageProperties[] arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ResultSet newArrayDataResultSet(Datum[] arg0, long arg1, int arg2, Map<String, Class<?>> arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet newArrayDataResultSet(OracleArray arg0, long arg1, int arg2, Map<String, Class<?>> arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet newArrayLocatorResultSet(ArrayDescriptor arg0, byte[] arg1, long arg2, int arg3,
			Map<String, Class<?>> arg4) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSetMetaData newStructMetaData(StructDescriptor arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numberOfDescriptorCacheEntries() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CallableStatement prepareCall(String arg0, Properties arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2, Properties arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2, String arg3)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2, String arg3, Properties arg4)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, Properties arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putDescriptor(byte[] arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OracleStatement refCursorCursorToStatement(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerEndReplayCallback(EndReplayCallback arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, JMSNotificationRegistration> registerJMSNotification(String[] arg0, Map<String, Properties> arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, JMSNotificationRegistration> registerJMSNotification(String[] arg0, Map<String, Properties> arg1,
			String arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAllDescriptor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllXSEventListener() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeBfile(OracleBfile arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeDescriptor(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLargeObject(OracleLargeObject arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeXSEventListener(XSEventListener arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRequestFlags() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean serverSupportsExplicitBoundaryBit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean serverSupportsRequestBoundaries() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setChecksumMode(ChecksumMode arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChunkInfo(OracleShardingKey arg0, OracleShardingKey arg1, String arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClientIdentifier(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDatabaseSessionState(DatabaseSessionState arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultFixedString(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFDO(byte[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHAManager(HAManager arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setJavaObjectTypeMap(Map<String, Class<?>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLastReplayContext(ReplayContext arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPDBChangeEventListener(PDBChangeEventListener arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPDBChangeEventListener(PDBChangeEventListener arg0, Executor arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReplayContext(ReplayContext[] arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReplayOperations(EnumSet<ReplayOperation> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReplayingMode(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSafelyClosed(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTxnMode(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUsable(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startJMSNotification(JMSNotificationRegistration arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopJMSNotification(JMSNotificationRegistration arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Datum toDatum(CustomDatum arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregisterJMSNotification(JMSNotificationRegistration arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
