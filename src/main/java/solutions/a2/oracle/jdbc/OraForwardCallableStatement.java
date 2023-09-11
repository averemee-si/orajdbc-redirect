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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDataFactory;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
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
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

/**
 * 
 * Oracle JDBC Forward CallableStatement implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardCallableStatement extends OraForwardPreparedStatement implements OracleCallableStatement  {

	OraForwardCallableStatement(OracleConnection proxy, CallableStatement statement, OraForwardTranslator translator,
			int dbType, OraForwardTranslator.Holder holder) throws SQLException {
		super(proxy, statement, translator, dbType, holder);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		((CallableStatement) statement).registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		((CallableStatement) statement).registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public boolean wasNull() throws SQLException {
		return ((CallableStatement) statement).wasNull();
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getString(parameterIndex);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getBoolean(parameterIndex);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getByte(parameterIndex);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getShort(parameterIndex);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getInt(parameterIndex);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getLong(parameterIndex);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getFloat(parameterIndex);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getDouble(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return ((CallableStatement) statement).getBigDecimal(parameterIndex);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getBytes(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getDate(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getTime(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getTimestamp(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getObject(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getBigDecimal(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return ((CallableStatement) statement).getObject(parameterIndex, map);
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getRef(parameterIndex);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getBlob(parameterIndex);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getClob(parameterIndex);
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getArray(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return ((CallableStatement) statement).getDate(parameterIndex, cal);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return ((CallableStatement) statement).getTime(parameterIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return ((CallableStatement) statement).getTimestamp(parameterIndex, cal);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		((CallableStatement) statement).registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		((CallableStatement) statement).registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		((CallableStatement) statement).registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		((CallableStatement) statement).registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getURL(parameterIndex);
	}

	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		((CallableStatement) statement).setURL(parameterName, val);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		((CallableStatement) statement).setNull(parameterName, sqlType);
	}

	@Override
	public void setBoolean(String parameterName, boolean value) throws SQLException {
		((CallableStatement) statement).setBoolean(parameterName, value);
	}

	@Override
	public void setByte(String parameterName, byte value) throws SQLException {
		((CallableStatement) statement).setByte(parameterName, value);
	}

	@Override
	public void setShort(String parameterName, short value) throws SQLException {
		((CallableStatement) statement).setShort(parameterName, value);
	}

	@Override
	public void setInt(String parameterName, int value) throws SQLException {
		((CallableStatement) statement).setInt(parameterName, value);
	}

	@Override
	public void setLong(String parameterName, long value) throws SQLException {
		((CallableStatement) statement).setLong(parameterName, value);
	}

	@Override
	public void setFloat(String parameterName, float value) throws SQLException {
		((CallableStatement) statement).setFloat(parameterName, value);
	}

	@Override
	public void setDouble(String parameterName, double value) throws SQLException {
		((CallableStatement) statement).setDouble(parameterName, value);
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal value) throws SQLException {
		((CallableStatement) statement).setBigDecimal(parameterName, value);
	}

	@Override
	public void setString(String parameterName, String value) throws SQLException {
		((CallableStatement) statement).setString(parameterName, value);
	}

	@Override
	public void setBytes(String parameterName, byte[] value) throws SQLException {
		((CallableStatement) statement).setBytes(parameterName, value);
	}

	@Override
	public void setDate(String parameterName, Date value) throws SQLException {
		((CallableStatement) statement).setDate(parameterName, value);
	}

	@Override
	public void setTime(String parameterName, Time value) throws SQLException {
		((CallableStatement) statement).setTime(parameterName, value);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp value) throws SQLException {
		((CallableStatement) statement).setTimestamp(parameterName, value);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream stream, int length) throws SQLException {
		((CallableStatement) statement).setAsciiStream(parameterName, stream, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream stream, int length) throws SQLException {
		((CallableStatement) statement).setBinaryStream(parameterName, stream, length);
	}

	@Override
	public void setObject(String parameterName, Object value, int targetSqlType, int scale) throws SQLException {
		((CallableStatement) statement).setObject(parameterName, value, targetSqlType, scale);
	}

	@Override
	public void setObject(String parameterName, Object value, int targetSqlType) throws SQLException {
		((CallableStatement) statement).setObject(parameterName, value, targetSqlType);
	}

	@Override
	public void setObject(String parameterName, Object value) throws SQLException {
		((CallableStatement) statement).setObject(parameterName, value);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		((CallableStatement) statement).setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setDate(String parameterName, Date value, Calendar cal) throws SQLException {
		((CallableStatement) statement).setDate(parameterName, value, cal);
	}

	@Override
	public void setTime(String parameterName, Time value, Calendar cal) throws SQLException {
		((CallableStatement) statement).setTime(parameterName, value, cal);		
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp value, Calendar cal) throws SQLException {
		((CallableStatement) statement).setTimestamp(parameterName, value, cal);
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		((CallableStatement) statement).setNull(parameterName, sqlType, typeName);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getString(parameterName);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getBoolean(parameterName);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getByte(parameterName);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getShort(parameterName);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getInt(parameterName);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getLong(parameterName);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getFloat(parameterName);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getDouble(parameterName);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getBytes(parameterName);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getDate(parameterName);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getTime(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getTimestamp(parameterName);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getObject(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getBigDecimal(parameterName);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return ((CallableStatement) statement).getObject(parameterName, map);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getRef(parameterName);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getBlob(parameterName);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getClob(parameterName);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getArray(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return ((CallableStatement) statement).getDate(parameterName, cal);
	}

	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return ((CallableStatement) statement).getTime(parameterName, cal);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return ((CallableStatement) statement).getTimestamp(parameterName, cal);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getURL(parameterName);
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getRowId(parameterName);
	}

	@Override
	public void setRowId(String parameterName, RowId value) throws SQLException {
		((CallableStatement) statement).setRowId(parameterName, value);
	}

	@Override
	public void setNString(String parameterName, String value) throws SQLException {
		((CallableStatement) statement).setNString(parameterName, value);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		((CallableStatement) statement).setNCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		((CallableStatement) statement).setNClob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		((CallableStatement) statement).setClob(parameterName, reader, length);
	}

	@Override
	public void setBlob(String parameterName, InputStream stream, long length) throws SQLException {
		((CallableStatement) statement).setBlob(parameterName, stream, length);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		((CallableStatement) statement).setNClob(parameterName, reader, length);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getNClob(parameterName);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		((CallableStatement) statement).setSQLXML(parameterName, xmlObject);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getSQLXML(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getNString(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getNCharacterStream(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return ((CallableStatement) statement).getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return ((CallableStatement) statement).getCharacterStream(parameterName);
	}

	@Override
	public void setBlob(String parameterName, Blob value) throws SQLException {
		((CallableStatement) statement).setBlob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Clob value) throws SQLException {
		((CallableStatement) statement).setClob(parameterName, value);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream inputStream, long length) throws SQLException {
		((CallableStatement) statement).setAsciiStream(parameterName, inputStream, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream inputStream, long length) throws SQLException {
		((CallableStatement) statement).setBinaryStream(parameterName, inputStream, length);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		((CallableStatement) statement).setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream stream) throws SQLException {
		((CallableStatement) statement).setAsciiStream(parameterName, stream);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream stream) throws SQLException {
		((CallableStatement) statement).setBinaryStream(parameterName, stream);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		((CallableStatement) statement).setCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
		((CallableStatement) statement).setNCharacterStream(parameterName, reader);
	}

	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		((CallableStatement) statement).setClob(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream stream) throws SQLException {
		((CallableStatement) statement).setBlob(parameterName, stream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		((CallableStatement) statement).setNClob(parameterName, reader);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return ((CallableStatement) statement).getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return ((CallableStatement) statement).getObject(parameterName, type);
	}

	//
	// Oracle extension redefinition...
	//

	@Override
	public ResultSet getCursor(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCursor");
	}

	@Override
	public void registerReturnParameter(int parameterIndex, int externalType) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerReturnParameter");
	}

	@Override
	public void registerReturnParameter(int parameterIndex, int externalType, int maxSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerReturnParameter");
	}

	@Override
	public void registerReturnParameter(int parameterIndex, int externalType, String typeName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerReturnParameter");
	}

	@Override
	public ARRAY getARRAY(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getARRAY");
	}

	@Deprecated
	@Override
	public Object getAnyDataEmbeddedObject(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getAnyDataEmbeddedObject");
	}

	@Override
	public InputStream getAsciiStream(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getAsciiStream");
	}

	@Override
	public BFILE getBFILE(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBFILE");
	}

	@Override
	public BLOB getBLOB(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBLOB");
	}

	@Override
	public BFILE getBfile(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBfile");
	}

	@Override
	public InputStream getBinaryStream(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBinaryStream");
	}

	@Override
	public InputStream getBinaryStream(String parameterName) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getBinaryStream");
	}

	@Override
	public CLOB getCLOB(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCLOB");
	}

	@Deprecated
	@Override
	public Object getCustomDatum(int parameterIndex, CustomDatumFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getCustomDatum");
	}

	@Override
	public CHAR getCHAR(int parameterIndex) throws SQLException {
		return new CHAR(getString(parameterIndex), CharacterSet.make(CharacterSet.AL32UTF8_CHARSET));
	}

	@Override
	public DATE getDATE(int parameterIndex) throws SQLException {
		return new DATE(getDate(parameterIndex));
	}

	@Override
	public INTERVALDS getINTERVALDS(int parameterIndex) throws SQLException {
		if (dbType == OraForwardUtils.POSTGRESQL) {
			//TODO
			//TODO
			//TODO
			throw OraForwardUtils.sqlFeatureNotSupportedException("getINTERVALDS");
		} else {
			// OraRedirectUtils.MARIADB
			return new INTERVALDS(getString(parameterIndex));
		}
	}

	@Override
	public INTERVALYM getINTERVALYM(int parameterIndex) throws SQLException {
		if (dbType == OraForwardUtils.POSTGRESQL) {
			//TODO
			//TODO
			//TODO
			throw OraForwardUtils.sqlFeatureNotSupportedException("getINTERVALYM");
		} else {
			// OraRedirectUtils.MARIADB
			return new INTERVALYM(getString(parameterIndex));
		}
	}

	@Override
	public NUMBER getNUMBER(int parameterIndex) throws SQLException {
		return new NUMBER(getBigDecimal(parameterIndex));
	}

	@Override
	public OPAQUE getOPAQUE(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOPAQUE");
	}

	@Override
	public Object getORAData(int parameterIndex, ORADataFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getORAData");
	}

	@Override
	public Object getObject(int parameterIndex, OracleDataFactory factory) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getObject");
	}

	@Override
	public Datum getOracleObject(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOracleObject");
	}

	@Override
	public Datum[] getOraclePlsqlIndexTable(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getOraclePlsqlIndexTable");
	}

	@Override
	public Object getPlsqlIndexTable(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getPlsqlIndexTable");
	}

	@Override
	public Object getPlsqlIndexTable(int parameterIndex, Class<?> primitiveType) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getPlsqlIndexTable");
	}

	@Override
	public RAW getRAW(int parameterIndex) throws SQLException {
		return new RAW(getBytes(parameterIndex));
	}

	@Override
	public REF getREF(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getREF");
	}

	@Override
	public ROWID getROWID(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getROWID");
	}

	@Override
	public STRUCT getSTRUCT(int parameterIndex) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("getSTRUCT");
	}

	@Override
	public TIMESTAMP getTIMESTAMP(int parameterIndex) throws SQLException {
		return new TIMESTAMP(getTimestamp(parameterIndex));
	}

	@Override
	public TIMESTAMPLTZ getTIMESTAMPLTZ(int parameterIndex) throws SQLException {
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
	public TIMESTAMPTZ getTIMESTAMPTZ(int parameterIndex) throws SQLException {
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
	public InputStream getUnicodeStream(int parameterIndex) throws SQLException {
		try {
			Reader reader = getCharacterStream(parameterIndex);
			char[] charBuffer = new char[8 * 1024];
			int numCharsRead;
			final StringBuilder sb = new StringBuilder();
			while ((numCharsRead = reader.read(charBuffer, 0, charBuffer.length)) != -1) {
				sb.append(charBuffer, 0, numCharsRead);
			}
			reader.close();
			return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException ioe) {
			throw new SQLException(ioe);
		}
	}

	@Override
	public InputStream getUnicodeStream(String parameterName) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			return getUnicodeStream(params.get(parameterName).get(0));
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void registerIndexTableOutParameter(int parameterIndex, int maxLen, int elemSqlType, int elemMaxLen) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerIndexTableOutParameter");
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale, int maxLength) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerOutParameter");
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale, int maxLength) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerOutParameter");
	}

	@Deprecated
	@Override
	public void registerOutParameterBytes(int parameterIndex, int sqlType, int scale, int maxLength) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerOutParameterBytes");
	}

	@Deprecated
	@Override
	public void registerOutParameterChars(int parameterIndex, int sqlType, int scale, int maxLength) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("registerOutParameterChars");
	}

	@Override
	public void setARRAY(String parameterName, ARRAY value) throws SQLException {
		setARRAYAtName(parameterName, value);
	}

	@Override
	public void setArray(String parameterName, Array value) throws SQLException {
		setArrayAtName(parameterName, value);
	}

	@Override
	public void setBFILE(String parameterName, BFILE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBFILE");
	}

	@Override
	public void setBLOB(String parameterName, BLOB value) throws SQLException {
		setBLOBAtName(parameterName, value);
	}

	@Override
	public void setBfile(String parameterName, BFILE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBFILE");
	}

	@Override
	public void setBinaryDouble(String parameterName, BINARY_DOUBLE value) throws SQLException {
		setBinaryDoubleAtName(parameterName, value);
	}

	@Override
	public void setBinaryDouble(String parameterName, double value) throws SQLException {
		setBinaryDoubleAtName(parameterName, value);
	}

	@Override
	public void setBinaryFloat(String parameterName, BINARY_FLOAT value) throws SQLException {
		setBinaryFloatAtName(parameterName, value);
	}

	@Override
	public void setBinaryFloat(String parameterName, float value) throws SQLException {
		setBinaryFloatAtName(parameterName, value);
	}

	@Override
	public void setBytesForBlob(String parameterName, byte[] value) throws SQLException {
		setBytesForBlobAtName(parameterName, value);
	}

	@Override
	public void setCHAR(String parameterName, CHAR value) throws SQLException {
		setCHARAtName(parameterName, value);
	}

	@Override
	public void setCLOB(String parameterName, CLOB value) throws SQLException {
		setCLOBAtName(parameterName, value);
	}

	@Override
	public void setCursor(String parameterName, ResultSet value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCursor");
	}

	@Override
	public void setCustomDatum(String parameterName, CustomDatum value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCustomDatum");
	}

	@Override
	public void setDATE(String parameterName, DATE value) throws SQLException {
		setDATEAtName(parameterName, value);
	}

	@Override
	public void setFixedCHAR(String parameterName, String value) throws SQLException {
		setFixedCHARAtName(parameterName, value);
	}

	@Override
	public void setINTERVALDS(String parameterName, INTERVALDS value) throws SQLException {
		setINTERVALDSAtName(parameterName, value);
	}

	@Override
	public void setINTERVALYM(String parameterName, INTERVALYM value) throws SQLException {
		setINTERVALYMAtName(parameterName, value);
	}

	@Override
	public void setNUMBER(String parameterName, NUMBER value) throws SQLException {
		setNUMBERAtName(parameterName, value);
	}

	@Override
	public void setOPAQUE(String parameterName, OPAQUE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setOPAQUE");
	}

	@Override
	public void setORAData(String parameterName, ORAData value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setORAData");
	}

	@Override
	public void setOracleObject(String parameterName, Datum value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setOracleObject");
	}

	@Override
	public void setRAW(String parameterName, RAW value) throws SQLException {
		setRAWAtName(parameterName, value);
	}

	@Override
	public void setREF(String parameterName, REF value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setREF");
	}

	@Override
	public void setROWID(String parameterName, ROWID value) throws SQLException {
		setROWIDAtName(parameterName, value);
	}

	@Override
	public void setRef(String parameterName, Ref value) throws SQLException {
		setRefAtName(parameterName, value);
	}

	@Override
	public void setRefType(String parameterName, REF value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setRefType");
	}

	@Override
	public void setSTRUCT(String parameterName, STRUCT value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setSTRUCT");
	}

	@Override
	public void setStringForClob(String parameterName, String value) throws SQLException {
		setStringForClobAtName(parameterName, value);
	}

	@Override
	public void setStructDescriptor(String parameterName, StructDescriptor value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setStructDescriptor");
	}

	@Override
	public void setTIMESTAMP(String parameterName, TIMESTAMP value) throws SQLException {
		setTIMESTAMPAtName(parameterName, value);
	}

	@Override
	public void setTIMESTAMPLTZ(String parameterName, TIMESTAMPLTZ value) throws SQLException {
		setTIMESTAMPLTZAtName(parameterName, value);
	}

	@Override
	public void setTIMESTAMPTZ(String parameterName, TIMESTAMPTZ value) throws SQLException {
		setTIMESTAMPTZAtName(parameterName, value);
	}

	@Override
	public void setUnicodeStream(String parameterName, InputStream stream, int length) throws SQLException {
		setUnicodeStreamAtName(parameterName, stream, length);
	}

	@Override
	public void registerOutParameterAtName(String parameterMarkerName, int sqlType) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterMarkerName)) {
			for (int parameterIndex : params.get(parameterMarkerName)) {
				registerOutParameter(parameterIndex, sqlType);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterMarkerName, original, translated);
		}
	}

	@Override
	public void registerOutParameterAtName(String parameterMarkerName, int sqlType, int scale) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterMarkerName)) {
			for (int parameterIndex : params.get(parameterMarkerName)) {
				registerOutParameter(parameterIndex, sqlType, scale);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterMarkerName, original, translated);
		}
	}

	@Override
	public void registerOutParameterAtName(String parameterMarkerName, int sqlType, String typeName) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterMarkerName)) {
			for (int parameterIndex : params.get(parameterMarkerName)) {
				registerOutParameter(parameterIndex, sqlType, typeName);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterMarkerName, original, translated);
		}
	}

}
