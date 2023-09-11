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
import oracle.jdbc.internal.Monitor;
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
 * Oracle JDBC Probe Connection implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraWrappedConnection extends OraForwardWrapper implements OracleConnection, Monitor {

	private final OracleConnection shadow;
	private final OraForwardTranslateOrRecord translator;
	private final CloseableLock monitorLock;

	public OraWrappedConnection(Connection shadow, OraForwardTranslateOrRecord translator) {
		super(shadow);
		this.shadow = (OracleConnection) shadow;
		this.translator = translator;
		this.monitorLock = this.newDefaultLock();
	}

	@Override
	public CloseableLock getMonitorLock() {
		return monitorLock;
	}

	@Override
	public Statement createStatement() throws SQLException {
		return new OraWrappedStatement(
				this, translator, shadow.createStatement()); 
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return new OraWrappedStatement(this, translator,
				shadow.createStatement(resultSetType, resultSetConcurrency));
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new OraWrappedStatement(this, translator,
				shadow.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return new OraWrappedCallableStatement(this, translator,
				shadow.prepareCall(translator.translate(sql)));
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return new OraWrappedCallableStatement(this, translator,
				shadow.prepareCall(translator.translate(sql), resultSetType, resultSetConcurrency));
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return new OraWrappedCallableStatement(this, translator,
				shadow.prepareCall(translator.translate(sql), resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return new OraWrappedPreparedStatement(this, translator,
				shadow.prepareStatement(translator.translate(sql)));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return new OraWrappedPreparedStatement(this, translator,
				shadow.prepareStatement(translator.translate(sql), resultSetType, resultSetConcurrency));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return new OraWrappedPreparedStatement(this, translator,
				shadow.prepareStatement(translator.translate(sql), resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return new OraWrappedPreparedStatement(
				this, translator, shadow.prepareStatement(translator.translate(sql), autoGeneratedKeys));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return new OraWrappedPreparedStatement(
				this, translator, shadow.prepareStatement(translator.translate(sql), columnIndexes));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return new OraWrappedPreparedStatement(
				this, translator, shadow.prepareStatement(translator.translate(sql), columnNames));
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return shadow.nativeSQL(translator.translate(sql));
	}

	@Override
	public PreparedStatement getStatementWithKey(String key) throws SQLException {
		return new OraWrappedPreparedStatement(this, translator,
				shadow.getStatementWithKey(key));
	}

	@Deprecated
	@Override
	public CallableStatement prepareCallWithKey(String key) throws SQLException {
		return new OraWrappedCallableStatement(
				this, translator, shadow.prepareCallWithKey(key));
	}

	@Deprecated
	@Override
	public PreparedStatement prepareStatementWithKey(String key) throws SQLException {
		return new OraWrappedPreparedStatement(this, translator,
				shadow.prepareStatementWithKey(key));
	}

	@Override
	public boolean equals(Object obj) {
System.err.println("equals!!!");
		return shadow.equals(obj);
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
	public boolean isValid(ConnectionValidation effort, int timeout) throws SQLException {
		return shadow.isValid(effort, timeout);
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

	@Override
	public Connection _getPC() {
		return shadow._getPC();
	}

	@Override
	public void abort() throws SQLException {
		shadow.abort();
	}

	@Deprecated
	@Override
	public void applyConnectionAttributes(Properties connAttr) throws SQLException {
		shadow.applyConnectionAttributes(connAttr);
	}

	@Deprecated
	@Override
	public void archive(int mode, int aseq, String acstext) throws SQLException {
		shadow.archive(mode, aseq, acstext);
	}

	@Override
	public void cancel() throws SQLException {
		shadow.cancel();
	}

	@Deprecated
	@Override
	public void clearAllApplicationContext(String nameSpace) throws SQLException {
		shadow.clearAllApplicationContext(nameSpace);
	}

	@Deprecated
	@Override
	public void close(Properties connAttr) throws SQLException {
		shadow.close(connAttr);
	}

	@Override
	public void close(int opt) throws SQLException {
		shadow.close(opt);
	}

	@Override
	public void commit(EnumSet<CommitOption> options) throws SQLException {
		shadow.commit(options);
	}

	@Override
	public ARRAY createARRAY(String typeName, Object elements) throws SQLException {
		return shadow.createARRAY(typeName, elements);
	}

	@Override
	public BINARY_DOUBLE createBINARY_DOUBLE(double value) throws SQLException {
		return shadow.createBINARY_DOUBLE(value);
	}

	@Override
	public BINARY_FLOAT createBINARY_FLOAT(float value) throws SQLException {
		return shadow.createBINARY_FLOAT(value);
	}

	@Override
	public DATE createDATE(Date value) throws SQLException {
		return shadow.createDATE(value);
	}

	@Override
	public DATE createDATE(Time value) throws SQLException {
		return shadow.createDATE(value);
	}

	@Override
	public DATE createDATE(Timestamp value) throws SQLException {
		return shadow.createDATE(value);
	}

	@Override
	public DATE createDATE(String value) throws SQLException {
		return shadow.createDATE(value);
	}

	@Override
	public DATE createDATE(Date value, Calendar cal) throws SQLException {
		return shadow.createDATE(value, cal);
	}

	@Override
	public DATE createDATE(Time value, Calendar cal) throws SQLException {
		return shadow.createDATE(value, cal);
	}

	@Override
	public DATE createDATE(Timestamp value, Calendar cal) throws SQLException {
		return shadow.createDATE(value, cal);
	}

	@Override
	public INTERVALDS createINTERVALDS(String value) throws SQLException {
		return shadow.createINTERVALDS(value);
	}

	@Override
	public INTERVALYM createINTERVALYM(String value) throws SQLException {
		return shadow.createINTERVALYM(value);
	}

	@Override
	public NUMBER createNUMBER(boolean value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(byte value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(short value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(int value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(long value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(float value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(double value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(BigDecimal value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(BigInteger value) throws SQLException {
		return shadow.createNUMBER(value);
	}

	@Override
	public NUMBER createNUMBER(String value, int scale) throws SQLException {
		return shadow.createNUMBER(value, scale);
	}

	@Override
	public Array createOracleArray(String arrayTypeName, Object elements) throws SQLException {
		return shadow.createOracleArray(arrayTypeName, elements);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Date value) throws SQLException {
		return shadow.createTIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(DATE value) throws SQLException {
		return shadow.createTIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Time value) throws SQLException {
		return shadow.createTIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Timestamp value) throws SQLException {
		return shadow.createTIMESTAMP(value);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(String value) throws SQLException {
		return shadow.createTIMESTAMP(value);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Date value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPLTZ(value, cal);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Time value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPLTZ(value, cal);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Timestamp value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPLTZ(value, cal);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(String value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPLTZ(value, cal);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(DATE value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPLTZ(value, cal);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Date value) throws SQLException {
		return shadow.createTIMESTAMPTZ(value);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Time value) throws SQLException {
		return shadow.createTIMESTAMPTZ(value);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp value) throws SQLException {
		return shadow.createTIMESTAMPTZ(value);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(String value) throws SQLException {
		return shadow.createTIMESTAMPTZ(value);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(DATE value) throws SQLException {
		return shadow.createTIMESTAMPTZ(value);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Date value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPTZ(value, cal);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Time value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPTZ(value, cal);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPTZ(value, cal);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(String value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMPTZ(value, cal);
	}

	@Override
	public AQMessage dequeue(String queueName, AQDequeueOptions opt, byte[] tdo) throws SQLException {
		return shadow.dequeue(queueName, opt, tdo);
	}

	@Override
	public AQMessage dequeue(String queueName, AQDequeueOptions opt, byte[] tdo, int version) throws SQLException {
		return shadow.dequeue(queueName, opt, tdo, version);
	}

	@Override
	public AQMessage[] dequeue(String queueName, AQDequeueOptions opt, byte[] tdo, int version, int deqSize)
			throws SQLException {
		return shadow.dequeue(queueName, opt, tdo, version, deqSize);
	}

	@Override
	public AQMessage dequeue(String queueName, AQDequeueOptions opt, String typeName) throws SQLException {
		return shadow.dequeue(queueName, opt, typeName);
	}

	@Override
	public AQMessage[] dequeue(String queueName, AQDequeueOptions opt, String typeName, int deqSize) throws SQLException {
		return shadow.dequeue(queueName, opt, typeName, deqSize);
	}

	@Override
	public void enqueue(String queueName, AQEnqueueOptions opt, AQMessage mesg) throws SQLException {
		shadow.enqueue(queueName, opt, mesg);
	}

	@Override
	public int enqueue(String queueName, AQEnqueueOptions opt, AQMessage[] mesgs) throws SQLException {
		return shadow.enqueue(queueName, opt, mesgs);
	}

	@Override
	public TypeDescriptor[] getAllTypeDescriptorsInCurrentSchema() throws SQLException {
		return shadow.getAllTypeDescriptorsInCurrentSchema();
	}

	@Override
	public String getAuthenticationAdaptorName() throws SQLException {
		return shadow.getAuthenticationAdaptorName();
	}

	@Override
	public boolean getAutoClose() throws SQLException {
		return shadow.getAutoClose();
	}

	@Override
	public CallableStatement getCallWithKey(String key) throws SQLException {
		return shadow.getCallWithKey(key);
	}

	@Deprecated
	@Override
	public Properties getConnectionAttributes() throws SQLException {
		return shadow.getConnectionAttributes();
	}

	@Deprecated
	@Override
	public int getConnectionReleasePriority() throws SQLException {
		return shadow.getConnectionReleasePriority();
	}

	@Override
	public boolean getCreateStatementAsRefCursor() {
		return shadow.getCreateStatementAsRefCursor();
	}

	@Override
	public String getCurrentSchema() throws SQLException {
		return shadow.getCurrentSchema();
	}

	@Override
	public String getDataIntegrityAlgorithmName() throws SQLException {
		return shadow.getDataIntegrityAlgorithmName();
	}

	@Override
	public DatabaseChangeRegistration getDatabaseChangeRegistration(int regid) throws SQLException {
		return shadow.getDatabaseChangeRegistration(regid);
	}

	@Deprecated
	@Override
	public int getDefaultExecuteBatch() {
		return shadow.getDefaultExecuteBatch();
	}

	@Override
	public int getDefaultRowPrefetch() {
		return shadow.getDefaultRowPrefetch();
	}

	@Override
	public TimeZone getDefaultTimeZone() throws SQLException {
		return shadow.getDefaultTimeZone();
	}

	@Override
	public Object getDescriptor(String sqlName) {
		return shadow.getDescriptor(sqlName);
	}

	@Override
	public String getEncryptionAlgorithmName() throws SQLException {
		return shadow.getEncryptionAlgorithmName();
	}

	@Deprecated
	@Override
	public short getEndToEndECIDSequenceNumber() throws SQLException {
		return shadow.getEndToEndECIDSequenceNumber();
	}

	@Deprecated
	@Override
	public String[] getEndToEndMetrics() throws SQLException {
		return shadow.getEndToEndMetrics();
	}

	@Override
	public boolean getExplicitCachingEnabled() throws SQLException {
		return shadow.getExplicitCachingEnabled();
	}

	@Override
	public boolean getImplicitCachingEnabled() throws SQLException {
		return shadow.getImplicitCachingEnabled();
	}

	@Override
	public boolean getIncludeSynonyms() {
		return shadow.getIncludeSynonyms();
	}

	@Deprecated
	@Override
	public Object getJavaObject(String sqlName) throws SQLException {
		return shadow.getJavaObject(sqlName);
	}

	@Override
	public Properties getProperties() {
		return shadow.getProperties();
	}

	@Override
	public boolean getRemarksReporting() {
		return shadow.getRemarksReporting();
	}

	@Override
	public boolean getRestrictGetTables() {
		return shadow.getRestrictGetTables();
	}

	@Deprecated
	@Override
	public String getSQLType(Object obj) throws SQLException {
		return shadow.getSQLType(obj);
	}

	@Override
	public String getSessionTimeZone() {
		return shadow.getSessionTimeZone();
	}

	@Override
	public String getSessionTimeZoneOffset() throws SQLException {
		return shadow.getSessionTimeZoneOffset();
	}

	@Override
	public int getStatementCacheSize() throws SQLException {
		return shadow.getStatementCacheSize();
	}

	@Deprecated
	@Override
	public int getStmtCacheSize() {
		return shadow.getStmtCacheSize();
	}

	@Override
	public short getStructAttrCsId() throws SQLException {
		return shadow.getStructAttrCsId();
	}

	@Override
	public TypeDescriptor[] getTypeDescriptorsFromList(String[][] schemaAndTypeNamePairs) throws SQLException {
		return shadow.getTypeDescriptorsFromList(schemaAndTypeNamePairs);
	}

	@Override
	public TypeDescriptor[] getTypeDescriptorsFromListInCurrentSchema(String[] typeNames) throws SQLException {
		return shadow.getTypeDescriptorsFromListInCurrentSchema(typeNames);
	}

	@Deprecated
	@Override
	public Properties getUnMatchedConnectionAttributes() throws SQLException {
		return shadow.getUnMatchedConnectionAttributes();
	}

	@Override
	public String getUserName() throws SQLException {
		return shadow.getUserName();
	}

	@Deprecated
	@Override
	public boolean getUsingXAFlag() {
		return shadow.getUsingXAFlag();
	}

	@Deprecated
	@Override
	public boolean getXAErrorFlag() {
		return shadow.getXAErrorFlag();
	}

	@Override
	public boolean isLogicalConnection() {
		return shadow.isLogicalConnection();
	}

	@Override
	public boolean isProxySession() {
		return shadow.isProxySession();
	}

	@Override
	public boolean isUsable() {
		return shadow.isUsable();
	}

	@Override
	public void openProxySession(int type, Properties prop) throws SQLException {
		shadow.openProxySession(type, prop);
	}

	@Override
	public void oracleReleaseSavepoint(OracleSavepoint savepoint) throws SQLException {
		shadow.oracleReleaseSavepoint(savepoint);
	}

	@Override
	public void oracleRollback(OracleSavepoint savepoint) throws SQLException {
		shadow.oracleRollback(savepoint);
	}

	@Override
	public OracleSavepoint oracleSetSavepoint() throws SQLException {
		return shadow.oracleSetSavepoint();
	}

	@Override
	public OracleSavepoint oracleSetSavepoint(String name) throws SQLException {
		return shadow.oracleSetSavepoint(name);
	}

	@Deprecated
	@Override
	public OracleConnection physicalConnectionWithin() {
		return shadow.physicalConnectionWithin();
	}

	@Override
	public int pingDatabase() throws SQLException {
		return shadow.pingDatabase();
	}

	@Deprecated
	@Override
	public int pingDatabase(int timeOut) throws SQLException {
		return shadow.pingDatabase(timeOut);
	}

	@Override
	public void purgeExplicitCache() throws SQLException {
		shadow.purgeExplicitCache();
	}

	@Override
	public void purgeImplicitCache() throws SQLException {
		shadow.purgeImplicitCache();
	}

	@Override
	public void putDescriptor(String sqlName, Object desc) throws SQLException {
		shadow.putDescriptor(sqlName, desc);
	}

	@Override
	public AQNotificationRegistration[] registerAQNotification(String[] name, Properties[] options, Properties globalOptions)
			throws SQLException {
		return shadow.registerAQNotification(name, options, globalOptions);
	}

	@Deprecated
	@Override
	public void registerConnectionCacheCallback(OracleConnectionCacheCallback occc, Object userObj, int cbkFlag)
			throws SQLException {
		shadow.registerConnectionCacheCallback(occc, userObj, cbkFlag);
	}

	@Override
	public DatabaseChangeRegistration registerDatabaseChangeNotification(Properties options) throws SQLException {
		return shadow.registerDatabaseChangeNotification(options);
	}

	@Deprecated
	@Override
	public void registerSQLType(String sqlName, Class<?> javaClass) throws SQLException {
		shadow.registerSQLType(sqlName, javaClass);
	}

	@Deprecated
	@Override
	public void registerSQLType(String sqlName, String javaClassName) throws SQLException {
		shadow.registerSQLType(sqlName, javaClassName);
	}

	@Override
	public void registerTAFCallback(OracleOCIFailover cbk, Object obj) throws SQLException {
		shadow.registerTAFCallback(cbk, obj);
	}

	@Deprecated
	@Override
	public void setApplicationContext(String nameSpace, String attribute, String value) throws SQLException {
		shadow.setApplicationContext(nameSpace, attribute, value);
	}

	@Override
	public void setAutoClose(boolean autoClose) throws SQLException {
		shadow.setAutoClose(autoClose);
	}

	@Deprecated
	@Override
	public void setConnectionReleasePriority(int priority) throws SQLException {
		shadow.setConnectionReleasePriority(priority);
	}

	@Override
	public void setCreateStatementAsRefCursor(boolean value) {
		shadow.setCreateStatementAsRefCursor(value);
	}

	@Deprecated
	@Override
	public void setDefaultExecuteBatch(int batch) throws SQLException {
		shadow.setDefaultExecuteBatch(batch);
	}

	@Override
	public void setDefaultRowPrefetch(int value) throws SQLException {
		shadow.setDefaultRowPrefetch(value);
	}

	@Override
	public void setDefaultTimeZone(TimeZone tz) throws SQLException {
		shadow.setDefaultTimeZone(tz);
	}

	@Deprecated
	@Override
	public void setEndToEndMetrics(String[] metrics, short sequenceNumber) throws SQLException {
		shadow.setEndToEndMetrics(metrics, sequenceNumber);
	}

	@Override
	public void setExplicitCachingEnabled(boolean cache) throws SQLException {
		shadow.setExplicitCachingEnabled(cache);
	}

	@Override
	public void setImplicitCachingEnabled(boolean cache) throws SQLException {
		shadow.setImplicitCachingEnabled(cache);
	}

	@Override
	public void setIncludeSynonyms(boolean synonyms) {
		shadow.setIncludeSynonyms(synonyms);
	}

	@Override
	public void setPlsqlWarnings(String setting) throws SQLException {
		shadow.setPlsqlWarnings(setting);
	}

	@Override
	public void setRemarksReporting(boolean reportRemarks) {
		shadow.setRemarksReporting(reportRemarks);
	}

	@Override
	public void setRestrictGetTables(boolean restrict) {
		shadow.setRestrictGetTables(restrict);
	}

	@Override
	public void setSessionTimeZone(String regionName) throws SQLException {
		shadow.setSessionTimeZone(regionName);
	}

	@Override
	public void setStatementCacheSize(int size) throws SQLException {
		shadow.setStatementCacheSize(size);
	}

	@Deprecated
	@Override
	public void setStmtCacheSize(int size) throws SQLException {
		shadow.setStmtCacheSize(size);
	}

	@Deprecated
	@Override
	public void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
		shadow.setStmtCacheSize(size, clearMetaData);
	}

	@Deprecated
	@Override
	public void setUsingXAFlag(boolean value) {
		shadow.setUsingXAFlag(value);
	}

	@Override
	public void setWrapper(oracle.jdbc.OracleConnection wrapper) {
		shadow.setWrapper(wrapper);
	}

	@Deprecated
	@Override
	public void setXAErrorFlag(boolean value) {
		shadow.setXAErrorFlag(value);
	}

	@Override
	public void shutdown(DatabaseShutdownMode mode) throws SQLException {
		shadow.shutdown(mode);
	}

	@Override
	public void startup(DatabaseStartupMode mode) throws SQLException {
		shadow.startup(mode);
	}

	@Deprecated
	@Override
	public void startup(String startupString, int mode) throws SQLException {
		shadow.startup(startupString, mode);
	}

	@Override
	public void unregisterAQNotification(AQNotificationRegistration registration) throws SQLException {
		shadow.unregisterAQNotification(registration);
	}

	@Override
	public void unregisterDatabaseChangeNotification(DatabaseChangeRegistration registration) throws SQLException {
		shadow.unregisterDatabaseChangeNotification(registration);
	}

	@Deprecated
	@Override
	public void unregisterDatabaseChangeNotification(int registrationId) throws SQLException {
		shadow.unregisterDatabaseChangeNotification(registrationId);
	}

	@Override
	public void unregisterDatabaseChangeNotification(long registrationId, String callback) throws SQLException {
		shadow.unregisterDatabaseChangeNotification(registrationId, callback);
	}

	@Deprecated
	@Override
	public void unregisterDatabaseChangeNotification(int registrationId, String host, int tcpPort) throws SQLException {
		shadow.unregisterDatabaseChangeNotification(registrationId, host, tcpPort);
	}

	@Override
	public oracle.jdbc.OracleConnection unwrap() {
		return shadow.unwrap();
	}

	@Override
	public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener listener) throws SQLException {
		shadow.addLogicalTransactionIdEventListener(listener);
	}

	@Override
	public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener listener, Executor executor)
			throws SQLException {
		shadow.addLogicalTransactionIdEventListener(listener, executor);
	}

	@Override
	public boolean attachServerConnection() throws SQLException {
		return shadow.attachServerConnection();
	}

	@Override
	public void beginRequest() throws SQLException {
		shadow.beginRequest();
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Timestamp value, Calendar cal) throws SQLException {
		return shadow.createTIMESTAMP(value, cal);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp value, ZoneId tzid) throws SQLException {
		return shadow.createTIMESTAMPTZ(value, tzid);
	}

	@Override
	public void detachServerConnection(String tag) throws SQLException {
		shadow.detachServerConnection(tag);
	}

	@Override
	public void endRequest() throws SQLException {
		shadow.endRequest();
	}

	@Override
	public String getDRCPPLSQLCallbackName() throws SQLException {
		return shadow.getDRCPPLSQLCallbackName();
	}

	@Override
	public String getDRCPReturnTag() throws SQLException {
		return shadow.getDRCPReturnTag();
	}

	@Override
	public DRCPState getDRCPState() throws SQLException {
		return shadow.getDRCPState();
	}

	@Override
	public LogicalTransactionId getLogicalTransactionId() throws SQLException {
		return shadow.getLogicalTransactionId();
	}

	@Override
	public boolean isDRCPEnabled() throws SQLException {
		return shadow.isDRCPEnabled();
	}

	@Override
	public boolean isDRCPMultitagEnabled() throws SQLException {
		return shadow.isDRCPMultitagEnabled();
	}

	@Override
	public boolean needToPurgeStatementCache() throws SQLException {
		return shadow.needToPurgeStatementCache();
	}

	@Override
	public void removeLogicalTransactionIdEventListener(LogicalTransactionIdEventListener listener) throws SQLException {
		shadow.removeLogicalTransactionIdEventListener(listener);
	}

	@Override
	public void setShardingKey(OracleShardingKey shardingKey, OracleShardingKey superShardingKey) throws SQLException {
		shadow.setShardingKey(shardingKey, superShardingKey);
	}

	@Override
	public void setShardingKey(OracleShardingKey shardingKey) throws SQLException {
		shadow.setShardingKey(shardingKey);
	}

	@Override
	public boolean setShardingKeyIfValid(OracleShardingKey shardingKey, OracleShardingKey superShardingKey, int timeout) throws SQLException {
		return shadow.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
	}

	@Override
	public boolean setShardingKeyIfValid(OracleShardingKey shardingKey, int timeout) throws SQLException {
		return shadow.setShardingKeyIfValid(shardingKey, timeout);
	}

	@Override
	public void startup(DatabaseStartupMode mode, String pfileName) throws SQLException {
		shadow.startup(mode, pfileName);
	}

	@Override
	public String getChecksumProviderName() throws SQLException {
		return shadow.getChecksumProviderName();
	}

	@Override
	public String getEncryptionProviderName() throws SQLException {
		return shadow.getEncryptionProviderName();
	}

	@Override
	public void disableLogging() throws SQLException {
		shadow.disableLogging();
	}

	@Override
	public void dumpLog() throws SQLException {
		shadow.dumpLog();
	}

	@Override
	public void enableLogging() throws SQLException {
		shadow.enableLogging();
	}

	@Override
	public SecuredLogger getLogger() throws SQLException {
		return shadow.getLogger();
	}

	@Override
	public String getNetConnectionId() throws SQLException {
		return shadow.getNetConnectionId();
	}

	@Override
	public Properties getServerSessionInfo() throws SQLException {
		return shadow.getServerSessionInfo();
	}

	//
	// oracle.jdbc.internal.OracleConnection
	//

	@Override
	public Object getACProxy() {
		return shadow.getACProxy();
	}

	@Override
	public void setACProxy(Object aCProxy) {
		shadow.setACProxy(aCProxy);
	}

	@Override
	public int CHARBytesToJavaChars(byte[] arg0, int arg1, char[] arg2) throws SQLException {
		return shadow.CHARBytesToJavaChars(arg0, arg1, arg2);
	}

	@Override
	public boolean IsNCharFixedWith() {
		return shadow.IsNCharFixedWith();
	}

	@Override
	public int NCHARBytesToJavaChars(byte[] arg0, int arg1, char[] arg2) throws SQLException {
		return shadow.NCHARBytesToJavaChars(arg0, arg1, arg2);
	}

	@Override
	public void ackJMSNotification(JMSNotificationRegistration arg0, byte[] arg1, Directive arg2) throws SQLException {
		shadow.ackJMSNotification(arg0, arg1, arg2);
	}

	@Override
	public void ackJMSNotification(ArrayList<JMSNotificationRegistration> arg0, byte[][] arg1, Directive arg2)
			throws SQLException {
		shadow.ackJMSNotification(arg0, arg1, arg2);
	}

	@Override
	public void addBfile(OracleBfile arg0) throws SQLException {
		shadow.addBfile(arg0);
	}

	@Override
	public void addFeature(ClientFeature arg0) throws SQLException {
		shadow.addFeature(arg0);
	}

	@Override
	public void addLargeObject(OracleLargeObject arg0) throws SQLException {
		shadow.addLargeObject(arg0);
	}

	@Override
	public void addXSEventListener(XSEventListener arg0) throws SQLException {
		shadow.addXSEventListener(arg0);
	}

	@Override
	public void addXSEventListener(XSEventListener arg0, Executor arg1) throws SQLException {
		shadow.addXSEventListener(arg0, arg1);
	}

	@Override
	public void beginNonRequestCalls() throws SQLException {
		shadow.beginNonRequestCalls();
	}

	@Override
	public Class<?> classForNameAndSchema(String arg0, String arg1) throws ClassNotFoundException {
		return shadow.classForNameAndSchema(arg0, arg1);
	}

	@Override
	public void cleanupAndClose() throws SQLException {
		shadow.cleanupAndClose();
	}

	@Override
	public void cleanupAndClose(boolean arg0) throws SQLException {
		shadow.cleanupAndClose(arg0);
	}

	@Override
	public void clearClientIdentifier(String arg0) throws SQLException {
		shadow.clearClientIdentifier(arg0);
	}

	@Override
	public void clearDrcpTagName() throws SQLException {
		shadow.clearDrcpTagName();
	}

	@Override
	public void closeInternal(boolean arg0) throws SQLException {
		shadow.closeInternal(arg0);
	}

	@Override
	public void closeLogicalConnection() throws SQLException {
		shadow.closeLogicalConnection();
	}

	@Override
	public BFILE createBfile(byte[] arg0) throws SQLException {
		return shadow.createBfile(arg0);
	}

	@Override
	public BfileDBAccess createBfileDBAccess() throws SQLException {
		return shadow.createBfileDBAccess();
	}

	@Override
	public BLOB createBlob(byte[] arg0) throws SQLException {
		return shadow.createBlob(arg0);
	}

	@Override
	public BlobDBAccess createBlobDBAccess() throws SQLException {
		return shadow.createBlobDBAccess();
	}

	@Override
	public BLOB createBlobWithUnpickledBytes(byte[] arg0) throws SQLException {
		return shadow.createBlobWithUnpickledBytes(arg0);
	}

	@Override
	public CLOB createClob(byte[] arg0) throws SQLException {
		return shadow.createClob(arg0);
	}

	@Override
	public CLOB createClob(byte[] arg0, short arg1) throws SQLException {
		return shadow.createClob(arg0, arg1);
	}

	@Override
	public ClobDBAccess createClobDBAccess() throws SQLException {
		return shadow.createClobDBAccess();
	}

	@Override
	public CLOB createClobWithUnpickledBytes(byte[] arg0) throws SQLException {
		return shadow.createClobWithUnpickledBytes(arg0);
	}

	@Override
	public byte[] createLightweightSession(String arg0, KeywordValueLong[] arg1, int arg2, KeywordValueLong[][] arg3,
			int[] arg4) throws SQLException {
		return shadow.createLightweightSession(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public Enumeration<String> descriptorCacheKeys() {
		return shadow.descriptorCacheKeys();
	}

	@Override
	public void doXSNamespaceOp(XSOperationCode arg0, byte[] arg1, XSNamespace[] arg2, XSSecureId arg3)
			throws SQLException {
		shadow.doXSNamespaceOp(arg0, arg1, arg2, arg3);
	}

	@Override
	public void doXSNamespaceOp(XSOperationCode arg0, byte[] arg1, XSNamespace[] arg2, XSNamespace[][] arg3,
			XSSecureId arg4) throws SQLException {
		shadow.doXSNamespaceOp(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public void doXSSessionAttachOp(int arg0, byte[] arg1, XSSecureId arg2, byte[] arg3, XSPrincipal arg4,
			String[] arg5, String[] arg6, String[] arg7, XSNamespace[] arg8, XSNamespace[] arg9, XSNamespace[] arg10,
			TIMESTAMPTZ arg11, TIMESTAMPTZ arg12, int arg13, long arg14, XSKeyval arg15, int[] arg16)
			throws SQLException {
		shadow.doXSSessionAttachOp(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16);
	}

	@Override
	public void doXSSessionChangeOp(XSSessionSetOperationCode arg0, byte[] arg1, XSSecureId arg2,
			XSSessionParameters arg3) throws SQLException {
		shadow.doXSSessionChangeOp(arg0, arg1, arg2, arg3);
	}

	@Override
	public byte[] doXSSessionCreateOp(XSSessionOperationCode arg0, XSSecureId arg1, byte[] arg2, XSPrincipal arg3,
			String arg4, XSNamespace[] arg5, XSSessionModeFlag arg6, XSKeyval arg7) throws SQLException {
		return shadow.doXSSessionCreateOp(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}

	@Override
	public void doXSSessionDestroyOp(byte[] arg0, XSSecureId arg1, byte[] arg2) throws SQLException {
		shadow.doXSSessionDestroyOp(arg0, arg1, arg2);
	}

	@Override
	public void doXSSessionDetachOp(int arg0, byte[] arg1, XSSecureId arg2, boolean arg3) throws SQLException {
		shadow.doXSSessionDetachOp(arg0, arg1, arg2, arg3);
	}

	@Override
	public void endNonRequestCalls() throws SQLException {
		shadow.endNonRequestCalls();
	}

	@Override
	public void endRequest(boolean arg0) throws SQLException {
		shadow.endRequest(arg0);
	}

	@Override
	public void executeLightweightSessionPiggyback(int arg0, byte[] arg1, KeywordValueLong[] arg2, int arg3)
			throws SQLException {
		shadow.executeLightweightSessionPiggyback(arg0, arg1, arg2, arg3);
	}

	@Override
	public int freeTemporaryBlobsAndClobs() throws SQLException {
		return shadow.freeTemporaryBlobsAndClobs();
	}

	@Override
	public boolean getAutoCommitInternal() throws SQLException {
		return shadow.getAutoCommitInternal();
	}

	@Override
	public boolean getBigEndian() throws SQLException {
		return shadow.getBigEndian();
	}

	@Override
	public BufferCacheStatistics getByteBufferCacheStatistics() {
		return shadow.getByteBufferCacheStatistics();
	}

	@Override
	public int getC2SNlsRatio() {
		return shadow.getC2SNlsRatio();
	}

	@Override
	public BufferCacheStatistics getCharBufferCacheStatistics() {
		return shadow.getCharBufferCacheStatistics();
	}

	@Override
	public Class<?> getClassForType(String arg0, Map<String, Class<?>> arg1) {
		return shadow.getClassForType(arg0, arg1);
	}

	@Override
	public Properties getClientInfoInternal() throws SQLException {
		return shadow.getClientInfoInternal();
	}

	@Override
	public long getCurrentSCN() throws SQLException {
		return shadow.getCurrentSCN();
	}

	@Override
	public Properties getDBAccessProperties() throws SQLException {
		return shadow.getDBAccessProperties();
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return shadow.getDatabaseProductVersion();
	}

	@Override
	public DatabaseSessionState getDatabaseSessionState() throws SQLException {
		return shadow.getDatabaseSessionState();
	}

	@Override
	public String getDatabaseTimeZone() throws SQLException {
		return shadow.getDatabaseTimeZone();
	}

	@Override
	public short getDbCsId() throws SQLException {
		return shadow.getDbCsId();
	}

	@Override
	public boolean getDefaultFixedString() {
		return shadow.getDefaultFixedString();
	}

	@Override
	public String getDefaultSchemaNameForNamedTypes() throws SQLException {
		return shadow.getDefaultSchemaNameForNamedTypes();
	}

	@Override
	public byte[] getDerivedKeyInternal(byte[] arg0, int arg1)
			throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
		return shadow.getDerivedKeyInternal(arg0, arg1);
	}

	@Override
	public Object getDescriptor(byte[] arg0) {
		return shadow.getDescriptor(arg0);
	}

	@Override
	public short getDriverCharSet() {
		return shadow.getDriverCharSet();
	}

	@Override
	public int getEOC() throws SQLException {
		return shadow.getEOC();
	}

	@Override
	public short getExecutingRPCFunctionCode() {
		return shadow.getExecutingRPCFunctionCode();
	}

	@Override
	public String getExecutingRPCSQL() {
		return shadow.getExecutingRPCSQL();
	}

	@Override
	public byte[] getFDO(boolean arg0) throws SQLException {
		return shadow.getFDO(arg0);
	}

	@Override
	public void getForm(OracleTypeADT arg0, OracleTypeCLOB arg1, int arg2) throws SQLException {
		shadow.getForm(arg0, arg1, arg2);
	}

	@Override
	public HAManager getHAManager() {
		return shadow.getHAManager();
	}

	@Override
	public int getHeapAllocSize() throws SQLException {
		return shadow.getHeapAllocSize();
	}

	@Override
	public byte getInstanceProperty(InstanceProperty arg0) throws SQLException {
		return shadow.getInstanceProperty(arg0);
	}

	@Override
	public boolean getJDBCStandardBehavior() {
		return shadow.getJDBCStandardBehavior();
	}

	@Override
	public Properties getJavaNetProperties() throws SQLException {
		return shadow.getJavaNetProperties();
	}

	@Override
	public Map<String, Class<?>> getJavaObjectTypeMap() {
		return shadow.getJavaObjectTypeMap();
	}

	@Override
	public short getJdbcCsId() throws SQLException {
		return shadow.getJdbcCsId();
	}

	@Override
	public ReplayContext getLastReplayContext() throws SQLException {
		return shadow.getLastReplayContext();
	}

	@Override
	public Connection getLogicalConnection(OraclePooledConnection arg0, boolean arg1) throws SQLException {
		return shadow.getLogicalConnection(arg0, arg1);
	}

	@Override
	public boolean getMapDateToTimestamp() {
		return shadow.getMapDateToTimestamp();
	}

	@Override
	public int getMaxCharSize() throws SQLException {
		return shadow.getMaxCharSize();
	}

	@Override
	public int getMaxCharbyteSize() {
		return shadow.getMaxCharbyteSize();
	}

	@Override
	public int getMaxNCharbyteSize() {
		return shadow.getMaxNCharbyteSize();
	}

	@Override
	public short getNCharSet() {
		return shadow.getNCharSet();
	}

	@Override
	public int getNegotiatedSDU() throws SQLException {
		return shadow.getNegotiatedSDU();
	}

	@Override
	public byte getNegotiatedTTCVersion() throws SQLException {
		return shadow.getNegotiatedTTCVersion();
	}

	@Override
	public NetStat getNetworkStat() {
		return shadow.getNetworkStat();
	}

	@Override
	public int getOCIEnvHeapAllocSize() throws SQLException {
		return shadow.getOCIEnvHeapAllocSize();
	}

	@Override
	public Properties getOCIHandles() throws SQLException {
		return shadow.getOCIHandles();
	}

	@Override
	public int getOutboundConnectTimeout() {
		return shadow.getOutboundConnectTimeout();
	}

	@Override
	public double getPercentageQueryExecutionOnDirectShard() {
		return shadow.getPercentageQueryExecutionOnDirectShard();
	}

	@Override
	public OracleConnection getPhysicalConnection() {
		return shadow.getPhysicalConnection();
	}

	@Override
	public void getPropertyForPooledConnection(OraclePooledConnection arg0) throws SQLException {
		shadow.getPropertyForPooledConnection(arg0);
	}

	@Override
	public String getProtocolType() {
		return shadow.getProtocolType();
	}

	@Override
	public ReplayContext[] getReplayContext() throws SQLException {
		return shadow.getReplayContext();
	}

	@Override
	public ResultSetCache getResultSetCache() throws SQLException {
		return shadow.getResultSetCache();
	}

	@Override
	public short getStructAttrNCsId() throws SQLException {
		return shadow.getStructAttrNCsId();
	}

	@Override
	public TIMEZONETAB getTIMEZONETAB() throws SQLException {
		return shadow.getTIMEZONETAB();
	}

	@Override
	public long getTdoCState(String arg0) throws SQLException {
		return shadow.getTdoCState(arg0);
	}

	@Override
	public long getTdoCState(String arg0, String arg1) throws SQLException {
		return shadow.getTdoCState(arg0, arg1);
	}

	@Override
	public boolean getTimestamptzInGmt() {
		return shadow.getTimestamptzInGmt();
	}

	@Override
	public int getTimezoneVersionNumber() throws SQLException {
		return shadow.getTimezoneVersionNumber();
	}

	@Override
	public EnumSet<TransactionState> getTransactionState() throws SQLException {
		return shadow.getTransactionState();
	}

	@Override
	public int getTxnMode() {
		return shadow.getTxnMode();
	}

	@Override
	public String getURL() throws SQLException {
		return shadow.getURL();
	}

	@Override
	public boolean getUse1900AsYearForTime() {
		return shadow.getUse1900AsYearForTime();
	}

	@Override
	public int getVarTypeMaxLenCompat() throws SQLException {
		return shadow.getVarTypeMaxLenCompat();
	}

	@Override
	public short getVersionNumber() throws SQLException {
		return shadow.getVersionNumber();
	}

	@Override
	public oracle.jdbc.OracleConnection getWrapper() {
		return shadow.getWrapper();
	}

	@Override
	public XAResource getXAResource() throws SQLException {
		return shadow.getXAResource();
	}

	@Override
	public boolean hasNoOpenHandles() throws SQLException {
		return shadow.hasNoOpenHandles();
	}

	@Override
	public boolean isCharSetMultibyte(short arg0) {
		return shadow.isCharSetMultibyte(arg0);
	}

	@Override
	public boolean isConnectionBigTZTC() throws SQLException {
		return shadow.isConnectionBigTZTC();
	}

	@Override
	public boolean isConnectionSocketKeepAlive() throws SocketException, SQLException {
		return shadow.isConnectionSocketKeepAlive();
	}

	@Override
	public boolean isDataInLocatorEnabled() throws SQLException {
		return shadow.isDataInLocatorEnabled();
	}

	@Override
	public boolean isDescriptorSharable(OracleConnection arg0) throws SQLException {
		return shadow.isDescriptorSharable(arg0);
	}

	@Override
	public boolean isLifecycleOpen() throws SQLException {
		return shadow.isLifecycleOpen();
	}

	@Override
	public boolean isLobStreamPosStandardCompliant() throws SQLException {
		return shadow.isLobStreamPosStandardCompliant();
	}

	@Override
	public boolean isNetworkCompressionEnabled() {
		return shadow.isNetworkCompressionEnabled();
	}

	@Override
	public boolean isSafelyClosed() throws SQLException {
		return shadow.isSafelyClosed();
	}

	@Override
	public boolean isStatementCacheInitialized() {
		return shadow.isStatementCacheInitialized();
	}

	@Override
	public boolean isUsable(boolean arg0) {
		return shadow.isUsable(arg0);
	}

	@Deprecated
	@Override
	public boolean isV8Compatible() throws SQLException {
		return shadow.isV8Compatible();
	}

	@Override
	public int javaCharsToCHARBytes(char[] arg0, int arg1, byte[] arg2) throws SQLException {
		return shadow.javaCharsToCHARBytes(arg0, arg1, arg2);
	}

	@Override
	public int javaCharsToNCHARBytes(char[] arg0, int arg1, byte[] arg2) throws SQLException {
		return shadow.javaCharsToNCHARBytes(arg0, arg1, arg2);
	}

	@Override
	public JMSMessage jmsDequeue(String arg0, JMSDequeueOptions arg1) throws SQLException {
		return shadow.jmsDequeue(arg0, arg1);
	}

	@Override
	public JMSMessage jmsDequeue(String arg0, JMSDequeueOptions arg1, OutputStream arg2) throws SQLException {
		return shadow.jmsDequeue(arg0, arg1, arg2);
	}

	@Deprecated
	@Override
	public JMSMessage jmsDequeue(String arg0, JMSDequeueOptions arg1, String arg2) throws SQLException {
		return shadow.jmsDequeue(arg0, arg1, arg2);
	}

	@Override
	public JMSMessage[] jmsDequeue(String arg0, JMSDequeueOptions arg1, int arg2) throws SQLException {
		return shadow.jmsDequeue(arg0, arg1, arg2);
	}

	@Override
	public void jmsEnqueue(String arg0, JMSEnqueueOptions arg1, JMSMessage arg2, AQMessageProperties arg3)
			throws SQLException {
		shadow.jmsEnqueue(arg0, arg1, arg2, arg3);
	}

	@Override
	public void jmsEnqueue(String arg0, JMSEnqueueOptions arg1, JMSMessage[] arg2, AQMessageProperties[] arg3)
			throws SQLException {
		shadow.jmsEnqueue(arg0, arg1, arg2, arg3);
	}

	@Override
	public ResultSet newArrayDataResultSet(Datum[] arg0, long arg1, int arg2, Map<String, Class<?>> arg3)
			throws SQLException {
		return shadow.newArrayDataResultSet(arg0, arg1, arg2, arg3);
	}

	@Override
	public ResultSet newArrayDataResultSet(OracleArray arg0, long arg1, int arg2, Map<String, Class<?>> arg3)
			throws SQLException {
		return shadow.newArrayDataResultSet(arg0, arg1, arg2, arg3);
	}

	@Override
	public ResultSet newArrayLocatorResultSet(ArrayDescriptor arg0, byte[] arg1, long arg2, int arg3,
			Map<String, Class<?>> arg4) throws SQLException {
		return shadow.newArrayLocatorResultSet(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public ResultSetMetaData newStructMetaData(StructDescriptor arg0) throws SQLException {
		return shadow.newStructMetaData(arg0);
	}

	@Override
	public int numberOfDescriptorCacheEntries() {
		return shadow.numberOfDescriptorCacheEntries();
	}

	@Override
	public CallableStatement prepareCall(String arg0, Properties arg1) throws SQLException {
		return shadow.prepareCall(arg0, arg1);
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2) throws SQLException {
		return shadow.prepareDirectPath(arg0, arg1, arg2);
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2, Properties arg3)
			throws SQLException {
		return shadow.prepareDirectPath(arg0, arg1, arg2, arg3);
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2, String arg3)
			throws SQLException {
		return shadow.prepareDirectPath(arg0, arg1, arg2, arg3);
	}

	@Override
	public PreparedStatement prepareDirectPath(String arg0, String arg1, String[] arg2, String arg3, Properties arg4)
			throws SQLException {
		return shadow.prepareDirectPath(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, Properties arg1) throws SQLException {
		return shadow.prepareStatement(arg0, arg1);
	}

	@Override
	public void putDescriptor(byte[] arg0, Object arg1) throws SQLException {
		shadow.putDescriptor(arg0, arg1);
	}

	@Override
	public OracleStatement refCursorCursorToStatement(int arg0) throws SQLException {
		return shadow.refCursorCursorToStatement(arg0);
	}

	@Override
	public void registerEndReplayCallback(EndReplayCallback arg0) throws SQLException {
		shadow.registerEndReplayCallback(arg0);
	}

	@Override
	public Map<String, JMSNotificationRegistration> registerJMSNotification(String[] arg0, Map<String, Properties> arg1)
			throws SQLException {
		return shadow.registerJMSNotification(arg0, arg1);
	}

	@Override
	public Map<String, JMSNotificationRegistration> registerJMSNotification(String[] arg0, Map<String, Properties> arg1,
			String arg2) throws SQLException {
		return shadow.registerJMSNotification(arg0, arg1, arg2);
	}

	@Override
	public void removeAllDescriptor() {
		shadow.removeAllDescriptor();
	}

	@Override
	public void removeAllXSEventListener() throws SQLException {
		shadow.removeAllXSEventListener();
	}

	@Override
	public void removeBfile(OracleBfile arg0) throws SQLException {
		shadow.removeBfile(arg0);
	}

	@Override
	public void removeDescriptor(String arg0) {
		shadow.removeDescriptor(arg0);
	}

	@Override
	public void removeLargeObject(OracleLargeObject arg0) throws SQLException {
		shadow.removeLargeObject(arg0);
	}

	@Override
	public void removeXSEventListener(XSEventListener arg0) throws SQLException {
		shadow.removeXSEventListener(arg0);
	}

	@Override
	public void sendRequestFlags() throws SQLException {
		shadow.sendRequestFlags();
	}

	@Override
	public boolean serverSupportsExplicitBoundaryBit() throws SQLException {
		return shadow.serverSupportsExplicitBoundaryBit();
	}

	@Override
	public boolean serverSupportsRequestBoundaries() throws SQLException {
		return shadow.serverSupportsRequestBoundaries();
	}

	@Override
	public void setChecksumMode(ChecksumMode arg0) throws SQLException {
		shadow.setChecksumMode(arg0);
	}

	@Override
	public void setChunkInfo(OracleShardingKey arg0, OracleShardingKey arg1, String arg2) throws SQLException {
		shadow.setChunkInfo(arg0, arg1, arg2);
	}

	@Override
	public void setClientIdentifier(String arg0) throws SQLException {
		shadow.setClientIdentifier(arg0);
	}

	@Override
	public void setDatabaseSessionState(DatabaseSessionState arg0) throws SQLException {
		shadow.setDatabaseSessionState(arg0);
	}

	@Override
	public void setDefaultFixedString(boolean arg0) {
		shadow.setDefaultFixedString(arg0);
	}

	@Override
	public void setFDO(byte[] arg0) throws SQLException {
		shadow.setFDO(arg0);
	}

	@Override
	public void setHAManager(HAManager arg0) throws SQLException {
		shadow.setHAManager(arg0);
	}

	@Override
	public void setJavaObjectTypeMap(Map<String, Class<?>> arg0) {
		shadow.setJavaObjectTypeMap(arg0);
	}

	@Override
	public void setLastReplayContext(ReplayContext arg0) throws SQLException {
		shadow.setLastReplayContext(arg0);
	}

	@Override
	public void setPDBChangeEventListener(PDBChangeEventListener arg0) throws SQLException {
		shadow.setPDBChangeEventListener(arg0);
	}

	@Override
	public void setPDBChangeEventListener(PDBChangeEventListener arg0, Executor arg1) throws SQLException {
		shadow.setPDBChangeEventListener(arg0, arg1);
	}

	@Override
	public void setReplayContext(ReplayContext[] arg0) throws SQLException {
		shadow.setReplayContext(arg0);
	}

	@Override
	public void setReplayOperations(EnumSet<ReplayOperation> arg0) throws SQLException {
		shadow.setReplayOperations(arg0);
	}

	@Override
	public void setReplayingMode(boolean arg0) throws SQLException {
		shadow.setReplayingMode(arg0);
	}

	@Override
	public void setSafelyClosed(boolean arg0) throws SQLException {
		shadow.setSafelyClosed(arg0);
	}

	@Override
	public void setTxnMode(int arg0) {
		shadow.setTxnMode(arg0);
	}

	@Override
	public void setUsable(boolean arg0) {
		shadow.setUsable(arg0);
	}

	@Override
	public void startJMSNotification(JMSNotificationRegistration arg0) throws SQLException {
		shadow.startJMSNotification(arg0);
	}

	@Override
	public void stopJMSNotification(JMSNotificationRegistration arg0) throws SQLException {
		shadow.stopJMSNotification(arg0);
	}

	@Override
	public Datum toDatum(CustomDatum arg0) throws SQLException {
		return shadow.toDatum(arg0);
	}

	@Override
	public void unregisterJMSNotification(JMSNotificationRegistration arg0) throws SQLException {
		shadow.unregisterJMSNotification(arg0);
	}

}
