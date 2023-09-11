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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import oracle.jdbc.OracleDataFactory;
import oracle.jdbc.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

/**
 * 
 * Oracle JDBC Forward ResultSet/OracleResultSet implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardResultSet extends OraForwardWrapper implements OracleResultSet {

	final Statement statement;
	final ResultSet proxy;

	OraForwardResultSet(Statement statement, ResultSet proxy) {
		super(proxy);
		this.statement = statement;
		this.proxy = proxy;
	}

	@Override
	public Statement getStatement() throws SQLException {
		return statement;
	}

	@Override
	public boolean next() throws SQLException {
		return proxy.next();
	}

	@Override
	public void close() throws SQLException {
		proxy.close();
	}

	@Override
	public boolean wasNull() throws SQLException {
		return proxy.wasNull();
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		return proxy.getString(columnIndex);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return proxy.getBoolean(columnIndex);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return proxy.getByte(columnIndex);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return proxy.getShort(columnIndex);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return proxy.getInt(columnIndex);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return proxy.getLong(columnIndex);
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return proxy.getFloat(columnIndex);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return proxy.getDouble(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return proxy.getBigDecimal(columnIndex);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return proxy.getBytes(columnIndex);
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return proxy.getDate(columnIndex);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return proxy.getTime(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return proxy.getTimestamp(columnIndex);
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return proxy.getAsciiStream(columnIndex);
	}

	@Deprecated
	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return proxy.getUnicodeStream(columnIndex);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return proxy.getBinaryStream(columnIndex);
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		return proxy.getString(columnLabel);
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return proxy.getBoolean(columnLabel);
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return proxy.getByte(columnLabel);
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return proxy.getShort(columnLabel);
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return proxy.getInt(columnLabel);
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return proxy.getLong(columnLabel);
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return proxy.getFloat(columnLabel);
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return proxy.getDouble(columnLabel);
	}

	@Deprecated
	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return proxy.getBigDecimal(columnLabel, scale);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return proxy.getBytes(columnLabel);
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return proxy.getDate(columnLabel);
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return proxy.getTime(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return proxy.getTimestamp(columnLabel);
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return proxy.getAsciiStream(columnLabel);
	}

	@Deprecated
	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return proxy.getUnicodeStream(columnLabel);
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return proxy.getBinaryStream(columnLabel);
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return proxy.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		proxy.clearWarnings();
	}

	@Override
	public String getCursorName() throws SQLException {
		return proxy.getCursorName();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return proxy.getMetaData();
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return proxy.getObject(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return proxy.getObject(columnLabel);
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return proxy.findColumn(columnLabel);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return proxy.getCharacterStream(columnIndex);
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return proxy.getCharacterStream(columnLabel);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return proxy.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return proxy.getBigDecimal(columnLabel);
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return proxy.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return proxy.isAfterLast();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return proxy.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		return proxy.isLast();
	}

	@Override
	public void beforeFirst() throws SQLException {
		proxy.beforeFirst();
	}

	@Override
	public void afterLast() throws SQLException {
		proxy.afterLast();
	}

	@Override
	public boolean first() throws SQLException {
		return proxy.first();
	}

	@Override
	public boolean last() throws SQLException {
		return proxy.last();
	}

	@Override
	public int getRow() throws SQLException {
		return proxy.getRow();
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		return proxy.absolute(row);
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		return proxy.relative(rows);
	}

	@Override
	public boolean previous() throws SQLException {
		return proxy.previous();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		proxy.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return proxy.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		proxy.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return proxy.getFetchSize();
	}

	@Override
	public int getType() throws SQLException {
		return proxy.getType();
	}

	@Override
	public int getConcurrency() throws SQLException {
		return proxy.getConcurrency();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return proxy.rowUpdated();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return proxy.rowInserted();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return proxy.rowDeleted();
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		proxy.updateNull(columnIndex);
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		proxy.updateBoolean(columnIndex, x);
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		proxy.updateByte(columnIndex, x);
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		proxy.updateShort(columnIndex, x);
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		proxy.updateInt(columnIndex, x);
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		proxy.updateLong(columnIndex, x);
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		proxy.updateFloat(columnIndex, x);
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		proxy.updateDouble(columnIndex, x);
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		proxy.updateBigDecimal(columnIndex, x);
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		proxy.updateString(columnIndex, x);
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		proxy.updateBytes(columnIndex, x);
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		proxy.updateDate(columnIndex, x);
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		proxy.updateTime(columnIndex, x);
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		proxy.updateTimestamp(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		proxy.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		proxy.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		proxy.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		proxy.updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		proxy.updateObject(columnIndex, x);
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		proxy.updateNull(columnLabel);
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		proxy.updateBoolean(columnLabel, x);
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		proxy.updateByte(columnLabel, x);
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		proxy.updateShort(columnLabel, x);
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		proxy.updateInt(columnLabel, x);
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		proxy.updateLong(columnLabel, x);
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		proxy.updateFloat(columnLabel, x);
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		proxy.updateDouble(columnLabel, x);
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		proxy.updateBigDecimal(columnLabel, x);
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		proxy.updateString(columnLabel, x);
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		proxy.updateBytes(columnLabel, x);
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		proxy.updateDate(columnLabel, x);
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		proxy.updateTime(columnLabel, x);
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		proxy.updateTimestamp(columnLabel, x);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		proxy.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		proxy.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		proxy.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		proxy.updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		proxy.updateObject(columnLabel, x);
	}

	@Override
	public void insertRow() throws SQLException {
		proxy.insertRow();
	}

	@Override
	public void updateRow() throws SQLException {
		proxy.updateRow();
	}

	@Override
	public void deleteRow() throws SQLException {
		proxy.deleteRow();
	}

	@Override
	public void refreshRow() throws SQLException {
		proxy.refreshRow();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		proxy.cancelRowUpdates();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		proxy.moveToInsertRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		proxy.moveToCurrentRow();
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return proxy.getObject(columnIndex, map);
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		return proxy.getRef(columnIndex);
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		return proxy.getBlob(columnIndex);
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return proxy.getClob(columnIndex);
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		return proxy.getArray(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return proxy.getObject(columnLabel, map);
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		return proxy.getRef(columnLabel);
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		return proxy.getBlob(columnLabel);
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return proxy.getClob(columnLabel);
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		return proxy.getArray(columnLabel);
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return proxy.getDate(columnIndex, cal);
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return proxy.getDate(columnLabel, cal);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return proxy.getTime(columnIndex, cal);
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return proxy.getTime(columnLabel, cal);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return proxy.getTimestamp(columnIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return proxy.getTimestamp(columnLabel, cal);
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return proxy.getURL(columnIndex);
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return proxy.getURL(columnLabel);
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		proxy.updateRef(columnIndex, x);
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		proxy.updateRef(columnLabel, x);
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		proxy.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		proxy.updateBlob(columnLabel, x);
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		proxy.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		proxy.updateClob(columnLabel, x);
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		proxy.updateArray(columnIndex, x);
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		proxy.updateArray(columnLabel, x);
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return proxy.getRowId(columnIndex);
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return proxy.getRowId(columnLabel);
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		proxy.updateRowId(columnIndex, x);
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		proxy.updateRowId(columnLabel, x);
	}

	@Override
	public int getHoldability() throws SQLException {
		return proxy.getHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return proxy.isClosed();
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		proxy.updateNString(columnIndex, nString);
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		proxy.updateNString(columnLabel, nString);
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		proxy.updateNClob(columnIndex, nClob);
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		proxy.updateNClob(columnLabel, nClob);
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return proxy.getNClob(columnIndex);
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return proxy.getNClob(columnLabel);
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return proxy.getSQLXML(columnIndex);
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return proxy.getSQLXML(columnLabel);
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		proxy.updateSQLXML(columnIndex, xmlObject);
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		proxy.updateSQLXML(columnLabel, xmlObject);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return proxy.getNString(columnIndex);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return proxy.getNString(columnLabel);
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return proxy.getNCharacterStream(columnIndex);
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return proxy.getNCharacterStream(columnLabel);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		proxy.updateNCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		proxy.updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		proxy.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		proxy.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		proxy.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		proxy.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		proxy.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		proxy.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		proxy.updateBlob(columnIndex, inputStream, length);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		proxy.updateBlob(columnLabel, inputStream, length);
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		proxy.updateClob(columnIndex, reader, length);
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		proxy.updateClob(columnLabel, reader, length);
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		proxy.updateNClob(columnIndex, reader, length);
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		proxy.updateNClob(columnLabel, reader, length);
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		proxy.updateNCharacterStream(columnIndex, x);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		proxy.updateNCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		proxy.updateAsciiStream(columnIndex, x);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		proxy.updateBinaryStream(columnIndex, x);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		proxy.updateCharacterStream(columnIndex, x);
		
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		proxy.updateAsciiStream(columnLabel, x);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		proxy.updateBinaryStream(columnLabel, x);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		proxy.updateCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		proxy.updateBlob(columnIndex, inputStream);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		proxy.updateBlob(columnLabel, inputStream);
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		proxy.updateClob(columnIndex, reader);
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		proxy.updateClob(columnLabel, reader);
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		proxy.updateNClob(columnIndex, reader);
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		proxy.updateNClob(columnLabel, reader);
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return proxy.getObject(columnIndex, type);
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return proxy.getObject(columnLabel, type);
	}

	//
	// Oracle extension redefinition...
	//

	@Override
	public ARRAY getARRAY(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getARRAY");
	}

	@Override
	public ARRAY getARRAY(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getARRAY");
	}

	@Override
	public AuthorizationIndicator getAuthorizationIndicator(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getAuthorizationIndicator");
	}

	@Override
	public AuthorizationIndicator getAuthorizationIndicator(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getAuthorizationIndicator");
	}

	@Override
	public BFILE getBFILE(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBFILE");
	}

	@Override
	public BFILE getBFILE(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBFILE");
	}

	@Override
	public BLOB getBLOB(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBLOB");
	}

	@Override
	public BLOB getBLOB(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBLOB");
	}

	@Override
	public BFILE getBfile(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBfile");
	}

	@Override
	public BFILE getBfile(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBfile");
	}

	@Override
	public CHAR getCHAR(int columnIndex) throws SQLException {
		return new CHAR(getString(columnIndex), CharacterSet.make(CharacterSet.AL32UTF8_CHARSET));
	}

	@Override
	public CHAR getCHAR(String columnLabel) throws SQLException {
		return new CHAR(getString(columnLabel), CharacterSet.make(CharacterSet.AL32UTF8_CHARSET));
	}

	@Override
	public CLOB getCLOB(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCLOB");
	}

	@Override
	public CLOB getCLOB(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCLOB");
	}

	@Override
	public ResultSet getCursor(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCursor");
	}

	@Override
	public ResultSet getCursor(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCursor");
	}

	@Deprecated
	@Override
	public CustomDatum getCustomDatum(int columnIndex, CustomDatumFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCustomDatum");
	}

	@Deprecated
	@Override
	public CustomDatum getCustomDatum(String columnLabel, CustomDatumFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCustomDatum");
	}

	@Override
	public DATE getDATE(int columnIndex) throws SQLException {
		return new DATE(getDate(columnIndex));
	}

	@Override
	public DATE getDATE(String columnLabel) throws SQLException {
		return new DATE(getDate(columnLabel));
	}

	@Override
	public INTERVALDS getINTERVALDS(int columnIndex) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public INTERVALDS getINTERVALDS(String columnLabel) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public INTERVALYM getINTERVALYM(int columnIndex) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public INTERVALYM getINTERVALYM(String columnLabel) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public NUMBER getNUMBER(int columnIndex) throws SQLException {
		return new NUMBER(getBigDecimal(columnIndex));
	}

	@Override
	public NUMBER getNUMBER(String columnLabel) throws SQLException {
		return new NUMBER(getBigDecimal(columnLabel));
	}

	@Override
	public OPAQUE getOPAQUE(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOPAQUE");
	}

	@Override
	public OPAQUE getOPAQUE(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOPAQUE");
	}

	@Override
	public ORAData getORAData(int columnIndex, ORADataFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getORAData");
	}

	@Override
	public ORAData getORAData(String columnLabel, ORADataFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getORAData");
	}

	@Override
	public Object getObject(int columnIndex, OracleDataFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getObject");
	}

	@Override
	public Object getObject(String columnLabel, OracleDataFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getObject");
	}

	@Override
	public Datum getOracleObject(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOracleObject");
	}

	@Override
	public Datum getOracleObject(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOracleObject");
	}

	@Override
	public RAW getRAW(int columnIndex) throws SQLException {
		return new RAW(getBytes(columnIndex));
	}

	@Override
	public RAW getRAW(String columnLabel) throws SQLException {
		return new RAW(getBytes(columnLabel));
	}

	@Override
	public REF getREF(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getREF");
	}

	@Override
	public REF getREF(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getREF");
	}

	@Override
	public ROWID getROWID(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getROWID");
	}

	@Override
	public ROWID getROWID(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getROWID");
	}

	@Override
	public STRUCT getSTRUCT(int columnIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getSTRUCT");
	}

	@Override
	public STRUCT getSTRUCT(String columnLabel) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getSTRUCT");
	}

	@Override
	public TIMESTAMP getTIMESTAMP(int columnIndex) throws SQLException {
		return new TIMESTAMP(getTimestamp(columnIndex));
	}

	@Override
	public TIMESTAMP getTIMESTAMP(String columnLabel) throws SQLException {
		return new TIMESTAMP(getTimestamp(columnLabel));
	}

	@Override
	public TIMESTAMPLTZ getTIMESTAMPLTZ(int columnIndex) throws SQLException {
		//TODO
		//TODO
		//TODO
		//TODO We need server timezone here........
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("getTIMESTAMPLTZ");
	}

	@Override
	public TIMESTAMPLTZ getTIMESTAMPLTZ(String columnLabel) throws SQLException {
		//TODO
		//TODO
		//TODO
		//TODO We need server timezone here........
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("getTIMESTAMPLTZ");
	}

	@Override
	public TIMESTAMPTZ getTIMESTAMPTZ(int columnIndex) throws SQLException {
		//TODO
		//TODO
		//TODO
		//TODO More work here is required........
		//TODO For PostgreSQL we can read java.time.OffsetDateTime.class
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("getTIMESTAMPTZ");
	}

	@Override
	public TIMESTAMPTZ getTIMESTAMPTZ(String columnLabel) throws SQLException {
		//TODO
		//TODO
		//TODO
		//TODO More work here is required........
		//TODO For PostgreSQL we can read java.time.OffsetDateTime.class
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("getTIMESTAMPTZ");
	}

	@Override
	public void updateARRAY(int columnIndex, ARRAY array) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateARRAY");
	}

	@Override
	public void updateARRAY(String columnLabel, ARRAY array) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateARRAY");
	}

	@Override
	public void updateBFILE(int columnIndex, BFILE x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateBFILE");
	}

	@Override
	public void updateBFILE(String columnLabel, BFILE x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateBFILE");
	}

	@Override
	public void updateBLOB(int columnIndex, BLOB x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateBLOB");
	}

	@Override
	public void updateBLOB(String columnLabel, BLOB x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateBLOB");
	}

	@Override
	public void updateBfile(int columnIndex, BFILE x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateBfile");
	}

	@Override
	public void updateBfile(String columnLabel, BFILE x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateBfile");
	}

	@Override
	public void updateCHAR(int columnIndex, CHAR x) throws SQLException {
		proxy.updateString(columnIndex, x.stringValue());
	}

	@Override
	public void updateCHAR(String columnLabel, CHAR x) throws SQLException {
		proxy.updateString(columnLabel, x.stringValue());
	}

	@Override
	public void updateCLOB(int columnIndex, CLOB x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateCLOB");
	}

	@Override
	public void updateCLOB(String columnLabel, CLOB x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateCLOB");
	}

	@Deprecated
	@Override
	public void updateCustomDatum(int columnIndex, CustomDatum x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateCustomDatum");
	}

	@Deprecated
	@Override
	public void updateCustomDatum(String columnLabel, CustomDatum x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateCustomDatum");
	}

	@Override
	public void updateDATE(int columnIndex, DATE x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateDATE(String columnLabel, DATE x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateINTERVALDS(int columnIndex, INTERVALDS x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateINTERVALDS(String columnLabel, INTERVALDS x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateINTERVALYM(int columnIndex, INTERVALYM x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateINTERVALYM(String columnLabel, INTERVALYM x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateNUMBER(int columnIndex, NUMBER x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateNUMBER(String columnLabel, NUMBER x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateORAData(int columnIndex, ORAData x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateORAData");
	}

	@Override
	public void updateORAData(String columnLabel, ORAData x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateORAData");
	}

	@Override
	public void updateOracleObject(int columnIndex, Datum x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateOracleObject");
	}

	@Override
	public void updateOracleObject(String columnLabel, Datum x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateOracleObject");
	}

	@Override
	public void updateRAW(int columnIndex, RAW x) throws SQLException {
		proxy.updateBytes(columnIndex, x.getBytes());
	}

	@Override
	public void updateRAW(String columnLabel, RAW x) throws SQLException {
		proxy.updateBytes(columnLabel, x.getBytes());
	}

	@Override
	public void updateREF(int columnIndex, REF x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateREF");
	}

	@Override
	public void updateREF(String columnLabel, REF x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateREF");
	}

	@Override
	public void updateROWID(int columnIndex, ROWID x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateROWID");
	}

	@Override
	public void updateROWID(String columnLabel, ROWID x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateROWID");
	}

	@Override
	public void updateSTRUCT(int columnIndex, STRUCT x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateSTRUCT");
	}

	@Override
	public void updateSTRUCT(String columnLabel, STRUCT x) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("updateSTRUCT");
	}

	@Override
	public void updateTIMESTAMP(int columnIndex, TIMESTAMP x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateTIMESTAMP(String columnLabel, TIMESTAMP x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateTIMESTAMPLTZ(int columnIndex, TIMESTAMPLTZ x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateTIMESTAMPLTZ(String columnLabel, TIMESTAMPLTZ x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateTIMESTAMPTZ(int columnIndex, TIMESTAMPTZ x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void updateTIMESTAMPTZ(String columnLabel, TIMESTAMPTZ x) throws SQLException {
		//TODO
		//TODO
		//TODO
	}

	@Override
	public byte[] getCompileKey() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCompileKey");
	}

	@Override
	public byte[] getRuntimeKey() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getRuntimeKey");
	}

	@Override
	public boolean isFromResultSetCache() throws SQLException {
		return false;
	}

}
