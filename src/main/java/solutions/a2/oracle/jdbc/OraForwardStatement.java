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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeRegistration;

/**
 * 
 * Oracle JDBC Forward Statement implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardStatement extends OraForwardWrapper implements OracleStatement {

	protected final OracleConnection proxy;
	protected final OraForwardTranslator translator;
	protected final int dbType;
	protected Statement statement;
	protected Map<String, List<Integer>> params = null;
	protected String original;
	protected String translated;
	protected OraForwardTranslator.Holder holder;

	private int dummyLobPrefetchSize = 0;


	OraForwardStatement(OracleConnection proxy, Statement statement, OraForwardTranslator translator, int dbType) {
		super(statement);
		this.proxy = proxy;
		this.translator = translator;
		this.dbType = dbType;
		this.statement = statement;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return new OraForwardResultSet(this, statement.executeQuery(holder.translated));
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.executeUpdate(holder.translated);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.execute(holder.translated);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return proxy;
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.executeUpdate(holder.translated, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.executeUpdate(holder.translated, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.executeUpdate(holder.translated, columnNames);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.execute(holder.translated, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.execute(holder.translated, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		return statement.execute(holder.translated, columnNames);
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return new OraForwardResultSet(this, statement.getResultSet());
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return new OraForwardResultSet(this, statement.getGeneratedKeys());
	}

	@Override
	public void close() throws SQLException {
		statement.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return statement.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		statement.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {
		return statement.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		statement.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		statement.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return statement.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		statement.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {
		statement.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return statement.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		statement.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		statement.setCursorName(name);
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return statement.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return statement.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		statement.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return statement.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		statement.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return statement.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return statement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return statement.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		holder = translator.translateAndConvertParams(sql);
		statement.addBatch(holder.translated);
	}

	@Override
	public void clearBatch() throws SQLException {
		statement.clearBatch();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return statement.executeBatch();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return statement.getMoreResults(current);
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return statement.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return statement.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		statement.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return statement.isPoolable();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		statement.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return statement.isCloseOnCompletion();
	}

	//
	// Oracle extension redefinition...
	//

	@Override
	public boolean isNCHAR(int index) throws SQLException {
		// There is no equivalent of NCHAR in MariaDB or PostgreSQL
		return false;
	}

	@Deprecated
	@Override
	public int creationState() {
		return 0;
	}

	@Override
	public int getLobPrefetchSize() throws SQLException {
		return dummyLobPrefetchSize;
	}

	@Override
	public void setLobPrefetchSize(int size) throws SQLException {
		dummyLobPrefetchSize = size;
	}

	@Override
	public int getRowPrefetch() {
		return proxy.getDefaultRowPrefetch();
	}

	@Override
	public void setRowPrefetch(int size) throws SQLException {
		proxy.setDefaultRowPrefetch(size);
	}

	@Override
	public void closeWithKey(String key) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("closeWithKey");
	}

	@Override
	public void setDatabaseChangeRegistration(DatabaseChangeRegistration registration) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("oracle.jdbc.dcn.DatabaseChangeRegistration");
	}

	@Override
	public long getRegisteredQueryId() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getRegisteredQueryId");
	}

	@Override
	public String[] getRegisteredTableNames() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getRegisteredTableNames");
	}

	@Override
	public void defineColumnType(int columnIndex, int type) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineColumnType");
	}

	@Override
	public void defineColumnType(int columnIndex, int type, int lobPrefetchSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineColumnType");
	}

	@Override
	public void defineColumnType(int columnIndex, int type, String typeName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineColumnType");
	}

	@Deprecated
	@Override
	public void defineColumnType(int columnIndex, int type, int lobPrefetchSize, short formOfUse) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineColumnType");
	}

	@Deprecated
	@Override
	public void defineColumnTypeBytes(int columnIndex, int type, int lobPrefetchSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineColumnTypeBytes");
	}

	@Deprecated
	@Override
	public void defineColumnTypeChars(int columnIndex, int type, int lobPrefetchSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineColumnTypeChars");
	}

	@Override
	public void clearDefines() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("clearDefines");
	}

}