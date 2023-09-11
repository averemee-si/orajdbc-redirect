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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleParameterMetaData;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
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
 * Oracle JDBC Forward PreparedStatement implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardPreparedStatement extends OraForwardStatement implements OraclePreparedStatement {

	OraForwardPreparedStatement(OracleConnection proxy, PreparedStatement statement, OraForwardTranslator translator,
			int dbType, OraForwardTranslator.Holder holder) throws SQLException {
		super(proxy, statement, translator, dbType);
		this.holder = holder;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		//TODO - Achtung!!!
		return new OraForwardResultSet(this, ((PreparedStatement) statement).executeQuery());
	}

	@Override
	public int executeUpdate() throws SQLException {
		return ((PreparedStatement) statement).executeUpdate();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		((PreparedStatement) statement).setNull(parameterIndex, sqlType);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean value) throws SQLException {
		((PreparedStatement) statement).setBoolean(parameterIndex, value);
	}

	@Override
	public void setByte(int parameterIndex, byte value) throws SQLException {
		((PreparedStatement) statement).setByte(parameterIndex, value);
	}

	@Override
	public void setShort(int parameterIndex, short value) throws SQLException {
		((PreparedStatement) statement).setShort(parameterIndex, value);
	}

	@Override
	public void setInt(int parameterIndex, int value) throws SQLException {
		((PreparedStatement) statement).setInt(parameterIndex, value);
	}

	@Override
	public void setLong(int parameterIndex, long value) throws SQLException {
		((PreparedStatement) statement).setLong(parameterIndex, value);
	}

	@Override
	public void setFloat(int parameterIndex, float value) throws SQLException {
		((PreparedStatement) statement).setFloat(parameterIndex, value);
	}

	@Override
	public void setDouble(int parameterIndex, double value) throws SQLException {
		((PreparedStatement) statement).setDouble(parameterIndex, value);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal value) throws SQLException {
		((PreparedStatement) statement).setBigDecimal(parameterIndex, value);
	}

	@Override
	public void setString(int parameterIndex, String value) throws SQLException {
		((PreparedStatement) statement).setString(parameterIndex, value);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] value) throws SQLException {
		((PreparedStatement) statement).setBytes(parameterIndex, value);
	}

	@Override
	public void setDate(int parameterIndex, Date value) throws SQLException {
		//TODO
		//TODO
		//TODO Date in Oracle is Timestamp...
		//TODO
		//TODO
		((PreparedStatement) statement).setDate(parameterIndex, value);
	}

	@Override
	public void setTime(int parameterIndex, Time value) throws SQLException {
		((PreparedStatement) statement).setTime(parameterIndex, value);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp value) throws SQLException {
		((PreparedStatement) statement).setTimestamp(parameterIndex, value);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream stream, int length) throws SQLException {
		((PreparedStatement) statement).setAsciiStream(parameterIndex, stream, length);
	}

	@Deprecated
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream stream, int length) throws SQLException {
		((PreparedStatement) statement).setUnicodeStream(parameterIndex, stream, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream stream, int length) throws SQLException {
		((PreparedStatement) statement).setBinaryStream(parameterIndex, stream, length);
	}

	@Override
	public void clearParameters() throws SQLException {
		((PreparedStatement) statement).clearParameters();
		
	}

	@Override
	public void setObject(int parameterIndex, Object value, int targetSqlType) throws SQLException {
		((PreparedStatement) statement).setObject(parameterIndex, value, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object value) throws SQLException {
		((PreparedStatement) statement).setObject(parameterIndex, value);
	}

	@Override
	public boolean execute() throws SQLException {
		return ((PreparedStatement) statement).execute();
	}

	@Override
	public void addBatch() throws SQLException {
		((PreparedStatement) statement).addBatch();
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		((PreparedStatement) statement).setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setRef(int parameterIndex, Ref value) throws SQLException {
		((PreparedStatement) statement).setRef(parameterIndex, value);
	}

	@Override
	public void setBlob(int parameterIndex, Blob value) throws SQLException {
		((PreparedStatement) statement).setBlob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Clob value) throws SQLException {
		((PreparedStatement) statement).setClob(parameterIndex, value);
	}

	@Override
	public void setArray(int parameterIndex, Array value) throws SQLException {
		((PreparedStatement) statement).setArray(parameterIndex, value);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return ((PreparedStatement) statement).getMetaData();
	}

	@Override
	public void setDate(int parameterIndex, Date value, Calendar cal) throws SQLException {
		((PreparedStatement) statement).setDate(parameterIndex, value, cal);
	}

	@Override
	public void setTime(int parameterIndex, Time value, Calendar cal) throws SQLException {
		((PreparedStatement) statement).setTime(parameterIndex, value, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp value, Calendar cal) throws SQLException {
		((PreparedStatement) statement).setTimestamp(parameterIndex, value, cal);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		((PreparedStatement) statement).setNull(parameterIndex, sqlType, typeName);
	}

	@Override
	public void setURL(int parameterIndex, URL value) throws SQLException {
		((PreparedStatement) statement).setURL(parameterIndex, value);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return ((PreparedStatement) statement).getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId value) throws SQLException {
		((PreparedStatement) statement).setRowId(parameterIndex, value);
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		((PreparedStatement) statement).setNString(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		((PreparedStatement) statement).setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		((PreparedStatement) statement).setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		((PreparedStatement) statement).setClob(parameterIndex, reader, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		((PreparedStatement) statement).setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		((PreparedStatement) statement).setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		((PreparedStatement) statement).setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setObject(int parameterIndex, Object value, int targetSqlType, int scaleOrLength) throws SQLException {
		((PreparedStatement) statement).setObject(parameterIndex, value, scaleOrLength);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream stream, long length) throws SQLException {
		((PreparedStatement) statement).setAsciiStream(parameterIndex, stream, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream stream, long length) throws SQLException {
		((PreparedStatement) statement).setBinaryStream(parameterIndex, stream, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		((PreparedStatement) statement).setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream stream) throws SQLException {
		((PreparedStatement) statement).setAsciiStream(parameterIndex, stream);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream stream) throws SQLException {
		((PreparedStatement) statement).setBinaryStream(parameterIndex, stream);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement) statement).setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement) statement).setNCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement) statement).setClob(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream stream) throws SQLException {
		((PreparedStatement) statement).setBlob(parameterIndex, stream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		((PreparedStatement) statement).setNClob(parameterIndex, reader);
	}

	//
	// Oracle extension redefinition...
	//

	@Override
	public ResultSet getReturnResultSet() throws SQLException {
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("getReturnResultSet");
	}

	@Override
	public OracleParameterMetaData OracleGetParameterMetaData() throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("OracleGetParameterMetaData");
	}

	@Override
	public void defineParameterType(int parameterIndex, int type, int maxSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineParameterType");
	}

	@Override
	public void defineParameterTypeBytes(int parameterIndex, int type, int maxSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineParameterTypeBytes");
	}

	@Override
	public void defineParameterTypeChars(int parameterIndex, int type, int maxSize) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("defineParameterTypeChars");
	}

	@Deprecated
	@Override
	public int getExecuteBatch() {
		return 1;
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

	@Deprecated
	@Override
	public int sendBatch() throws SQLException {
		return 0;
	}

	@Override
	public void setARRAY(int parameterIndex, ARRAY value) throws SQLException {
		((PreparedStatement) statement).setArray(parameterIndex, (Array) value.getArray());
	}

	@Override
	public void setARRAYAtName(String parameterName, ARRAY value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setArray(parameterIndex, (Array) value.getArray());
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setArrayAtName(String parameterName, Array value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setArray(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setAsciiStreamAtName(String parameterName, InputStream stream) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setAsciiStream(parameterIndex, stream);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setAsciiStreamAtName(String parameterName, InputStream stream, int length) throws SQLException {
		setAsciiStreamAtName(parameterName, stream, (long) length); 
	}

	@Override
	public void setAsciiStreamAtName(String parameterName, InputStream stream, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setAsciiStream(parameterIndex, stream, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBFILE(int parameterIndex, BFILE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBFILE");
	}

	@Override
	public void setBFILEAtName(String parameterName, BFILE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBFILEAtName");
	}

	@Override
	public void setBLOB(int parameterIndex, BLOB value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBLOB");
	}

	@Override
	public void setBLOBAtName(String parameterName, BLOB value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBLOBAtName");
	}

	@Override
	public void setBfile(int parameterIndex, BFILE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBfile");
	}

	@Override
	public void setBfileAtName(String parameterName, BFILE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setBfileAtName");
	}

	@Override
	public void setBigDecimalAtName(String parameterName, BigDecimal value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBigDecimal(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBinaryDouble(int parameterIndex, double value) throws SQLException {
		setDouble(parameterIndex, value);
	}

	@Override
	public void setBinaryDouble(int parameterIndex, BINARY_DOUBLE value) throws SQLException {
		setDouble(parameterIndex, value.doubleValue());
	}

	@Override
	public void setBinaryDoubleAtName(String parameterName, double value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setDouble(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBinaryDoubleAtName(String parameterName, BINARY_DOUBLE value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBinaryDouble(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBinaryFloat(int parameterIndex, float value) throws SQLException {
		setFloat(parameterIndex, value);
	}

	@Override
	public void setBinaryFloat(int parameterIndex, BINARY_FLOAT value) throws SQLException {
		setFloat(parameterIndex, value.floatValue());
	}

	@Override
	public void setBinaryFloatAtName(String parameterName, float value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setFloat(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBinaryFloatAtName(String parameterName, BINARY_FLOAT value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBinaryFloat(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBinaryStreamAtName(String parameterName, InputStream stream) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBinaryStream(parameterIndex, stream);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBinaryStreamAtName(String parameterName, InputStream stream, int length) throws SQLException {
		setBinaryStreamAtName(parameterName, stream, (long) length);
	}

	@Override
	public void setBinaryStreamAtName(String parameterName, InputStream stream, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBinaryStream(parameterIndex, stream, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBlobAtName(String parameterName, Blob value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBlob(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBlobAtName(String parameterName, InputStream stream) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBlob(parameterIndex, stream);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBlobAtName(String parameterName, InputStream stream, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBlob(parameterIndex, stream, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBooleanAtName(String parameterName, boolean value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBoolean(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setByteAtName(String parameterName, byte value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setByte(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBytesAtName(String parameterName, byte[] value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBytes(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setBytesForBlob(int parameterIndex, byte[] value) throws SQLException {
		((PreparedStatement) statement).setBlob(parameterIndex, new ByteArrayInputStream(value));
	}

	@Override
	public void setBytesForBlobAtName(String parameterName, byte[] value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setBytesForBlob(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setCHAR(int parameterIndex, CHAR value) throws SQLException {
		setString(parameterIndex, value.stringValue());
	}

	@Override
	public void setCHARAtName(String parameterName, CHAR value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setCHAR(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setCLOB(int parameterIndex, CLOB value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCLOB");
	}

	@Override
	public void setCLOBAtName(String parameterName, CLOB value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCLOBAtName");
	}

	@Override
	public void setCharacterStreamAtName(String parameterName, Reader reader) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setCharacterStream(parameterIndex, reader);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setCharacterStreamAtName(String parameterName, Reader reader, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setCharacterStream(parameterIndex, reader, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setCheckBindTypes(boolean flag) {
		//TODO
		//TODO
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void setClobAtName(String parameterName, Clob value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setClob(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setClobAtName(String parameterName, Reader reader) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setClob(parameterIndex, reader);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setClobAtName(String parameterName, Reader reader, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setClob(parameterIndex, reader, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Deprecated
	@Override
	public void setCursor(int parameterIndex, ResultSet value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCursor");
	}

	@Override
	public void setCursorAtName(String parameterName, ResultSet value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCursorAtName");
	}

	@Deprecated
	@Override
	public void setCustomDatum(int parameterIndex, CustomDatum value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCustomDatum");
	}

	@Override
	public void setCustomDatumAtName(String parameterName, CustomDatum value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setCustomDatumAtName");
	}

	@Override
	public void setDATE(int parameterIndex, DATE value) throws SQLException {
		((PreparedStatement) statement).setDate(parameterIndex, value.dateValue());
	}

	@Override
	public void setDATEAtName(String parameterName, DATE value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setDATE(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setDateAtName(String parameterName, Date value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setDate(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setDateAtName(String parameterName, Date value, Calendar cal) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setDate(parameterIndex, value, cal);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setDisableStmtCaching(boolean cache) {
		//TODO
		//TODO
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void setDoubleAtName(String parameterName, double value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setDouble(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Deprecated
	@Override
	public void setExecuteBatch(int batchValue) throws SQLException {
	}

	@Override
	public void setFixedCHAR(int parameterIndex, String value) throws SQLException {
		setString(parameterIndex, value);
	}

	@Override
	public void setFixedCHARAtName(String parameterName, String value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setString(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setFloatAtName(String parameterName, float value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setFloat(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setFormOfUse(int parameterIndex, short formOfUse) {
		//TODO
		//TODO
		//TODO
		//TODO
		//TODO
	}

	@Override
	public void setINTERVALDS(int parameterIndex, INTERVALDS value) throws SQLException {
		if (dbType == OraForwardUtils.POSTGRESQL) {
			setObject(parameterIndex, OraForwardPgUtils.pgInterval(value));
		} else {
			// OraRedirectUtils.MARIADB
			setString(parameterIndex, value.stringValue());
		}
	}

	@Override
	public void setINTERVALDSAtName(String parameterName, INTERVALDS value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setINTERVALDS(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setINTERVALYM(int parameterIndex, INTERVALYM value) throws SQLException {
		if (dbType == OraForwardUtils.POSTGRESQL) {
			setObject(parameterIndex, OraForwardPgUtils.pgInterval(value));
		} else {
			// OraRedirectUtils.MARIADB
			setString(parameterIndex, value.stringValue());
		}
	}

	@Override
	public void setINTERVALYMAtName(String parameterName, INTERVALYM value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setINTERVALYM(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setIntAtName(String parameterName, int value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setInt(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setLongAtName(String parameterName, long value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setLong(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNCharacterStreamAtName(String parameterName, Reader reader) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNCharacterStream(parameterIndex, reader);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNCharacterStreamAtName(String parameterName, Reader reader, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNCharacterStream(parameterIndex, reader, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNClobAtName(String parameterName, NClob value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNClob(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNClobAtName(String parameterName, Reader reader) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNClob(parameterIndex, reader);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNClobAtName(String parameterName, Reader reader, long length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNClob(parameterIndex, reader, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNStringAtName(String parameterName, String value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNString(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNUMBER(int parameterIndex, NUMBER value) throws SQLException {
		setBigDecimal(parameterIndex, value.bigDecimalValue());
	}

	@Override
	public void setNUMBERAtName(String parameterName, NUMBER value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNUMBER(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNullAtName(String parameterName, int value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNull(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setNullAtName(String parameterName, int sqlType, String sqlName) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setNull(parameterIndex, sqlType, sqlName);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setOPAQUE(int parameterIndex, OPAQUE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setOPAQUE");
	}

	@Override
	public void setOPAQUEAtName(String parameterName, OPAQUE value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setOPAQUEAtName");
	}

	@Override
	public void setORAData(int parameterIndex, ORAData value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setORAData");
	}

	@Override
	public void setORADataAtName(String parameterName, ORAData value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setORADataAtName");
	}

	@Override
	public void setObjectAtName(String parameterName, Object value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setObject(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setObjectAtName(String parameterName, Object value, int targetSqlType) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setObject(parameterIndex, value, targetSqlType);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setObjectAtName(String parameterName, Object value, int targetSqlType, int scale) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setObject(parameterIndex, value, targetSqlType, scale);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setOracleObject(int parameterIndex, Datum value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setOracleObject");
	}

	@Override
	public void setOracleObjectAtName(String parameterName, Datum value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setOracleObjectAtName");
	}

	@Override
	public void setPlsqlIndexTable(int parameterIndex, Object arrayData, int maxLen, int curLen, int elemSqlType, int elemMaxLen) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setPlsqlIndexTable");
	}

	@Override
	public void setRAW(int parameterIndex, RAW value) throws SQLException {
		setBytes(parameterIndex, value.getBytes());
	}

	@Override
	public void setRAWAtName(String parameterName, RAW value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setRAW(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setREF(int parameterIndex, REF value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setREF");
	}

	@Override
	public void setREFAtName(String parameterName, REF value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setREFAtName");
	}

	@Override
	public void setROWID(int parameterIndex, ROWID value) throws SQLException {
		setRowId(parameterIndex, (RowId) value.toJdbc());
	}

	@Override
	public void setROWIDAtName(String parameterName, ROWID value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setROWID(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setRefAtName(String parameterName, Ref value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setRef(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setRefType(int parameterIndex, REF value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setRefType");
	}

	@Override
	public void setRefTypeAtName(String parameterName, REF value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setRefTypeAtName");
	}

	@Override
	public void setRowIdAtName(String parameterName, RowId value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setRowId(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setSQLXMLAtName(String parameterName, SQLXML value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setSQLXML(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setSTRUCT(int parameterIndex, STRUCT value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setSTRUCT");
	}

	@Override
	public void setSTRUCTAtName(String parameterName, STRUCT value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setSTRUCTAtName");
	}

	@Override
	public void setShortAtName(String parameterName, short value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setShort(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setStringAtName(String parameterName, String value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setString(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setStringForClob(int parameterIndex, String value) throws SQLException {
		setClob(parameterIndex, new StringReader(value));
	}

	@Override
	public void setStringForClobAtName(String parameterName, String value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setStringForClob(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setStructDescriptor(int parameterIndex, StructDescriptor value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setStructDescriptor");
	}

	@Override
	public void setStructDescriptorAtName(String parameterName, StructDescriptor value) throws SQLException {
		throw OraForwardUtils.sqlFeatureNotSupportedException("setStructDescriptorAtName");
	}

	@Override
	public void setTIMESTAMP(int parameterIndex, TIMESTAMP value) throws SQLException {
		setTimestamp(parameterIndex, value.timestampValue());
	}

	@Override
	public void setTIMESTAMPAtName(String parameterName, TIMESTAMP value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setTIMESTAMP(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setTIMESTAMPLTZ(int parameterIndex, TIMESTAMPLTZ value) throws SQLException {
		//TODO
		//TODO
		//TODO
		//TODO We need server timezone here........
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("setTIMESTAMPLTZ");
	}

	@Override
	public void setTIMESTAMPLTZAtName(String parameterName, TIMESTAMPLTZ value) throws SQLException {
		//TODO
		//TODO
		//TODO
		//TODO We need server timezone here........
		//TODO
		//TODO
		//TODO
		throw OraForwardUtils.sqlFeatureNotSupportedException("setTIMESTAMPLTZAtName");
	}

	@Override
	public void setTIMESTAMPTZ(int parameterIndex, TIMESTAMPTZ value) throws SQLException {
		if (dbType == OraForwardUtils.POSTGRESQL) {
			// java.time.OffsetDateTime.class for timestamp with time zone	
			setObject(parameterIndex, value.toOffsetDateTime());
		} else {
			// OraRedirectUtils.MARIADB
			// MySQL/MariaDB always store UTC value...
			setTimestamp(parameterIndex, value.timestampValue());
		}
	}

	@Override
	public void setTIMESTAMPTZAtName(String parameterName, TIMESTAMPTZ value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setTIMESTAMPTZ(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setTimeAtName(String parameterName, Time value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setTime(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setTimeAtName(String parameterName, Time value, Calendar cal) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setTime(parameterIndex, value, cal);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setTimestampAtName(String parameterName, Timestamp value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setTimestamp(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setTimestampAtName(String parameterName, Timestamp value, Calendar cal) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setTimestamp(parameterIndex, value, cal);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setURLAtName(String parameterName, URL value) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setURL(parameterIndex, value);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

	@Override
	public void setUnicodeStreamAtName(String parameterName, InputStream stream, int length) throws SQLException {
		if (params != null && params.size() > 0 && params.containsKey(parameterName)) {
			for (int parameterIndex : params.get(parameterName)) {
				setUnicodeStream(parameterIndex, stream, length);
			}
		} else {
			throw OraForwardUtils.namedParameterNotFound(parameterName, original, translated);
		}
	}

}
