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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
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
 * Oracle JDBC Wrapped PreparedStatement implementation
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraWrappedPreparedStatement extends OraWrappedStatement implements OraclePreparedStatement {

	OraWrappedPreparedStatement(OracleConnection proxy, OraForwardTranslateOrRecord translator, Statement statement) throws SQLException {
		super(proxy, translator, statement);
	}


	@Override
	public ResultSet executeQuery() throws SQLException {
		return new OraWrappedResultSet(this, ((PreparedStatement) statement).executeQuery());
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
		return ((OraclePreparedStatement) statement).getReturnResultSet();
	}

	@Override
	public OracleParameterMetaData OracleGetParameterMetaData() throws SQLException {
		return ((OraclePreparedStatement) statement).OracleGetParameterMetaData();
	}

	@Override
	public void defineParameterType(int parameterIndex, int type, int maxSize) throws SQLException {
		((OraclePreparedStatement) statement).defineParameterType(parameterIndex, type, maxSize);
	}

	@Override
	public void defineParameterTypeBytes(int parameterIndex, int type, int maxSize) throws SQLException {
		((OraclePreparedStatement) statement).defineParameterTypeBytes(parameterIndex, type, maxSize);
	}

	@Override
	public void defineParameterTypeChars(int parameterIndex, int type, int maxSize) throws SQLException {
		((OraclePreparedStatement) statement).defineParameterTypeChars(parameterIndex, type, maxSize);
	}

	@Deprecated
	@Override
	public int getExecuteBatch() {
		return ((OraclePreparedStatement) statement).getExecuteBatch();
	}

	@Override
	public void registerReturnParameter(int parameterIndex, int externalType) throws SQLException {
		((OraclePreparedStatement) statement).registerReturnParameter(parameterIndex, externalType);
	}

	@Override
	public void registerReturnParameter(int parameterIndex, int externalType, int maxSize) throws SQLException {
		((OraclePreparedStatement) statement).registerReturnParameter(parameterIndex, externalType, maxSize);
	}

	@Override
	public void registerReturnParameter(int parameterIndex, int externalType, String typeName) throws SQLException {
		((OraclePreparedStatement) statement).registerReturnParameter(parameterIndex, externalType, typeName);
	}

	@Deprecated
	@Override
	public int sendBatch() throws SQLException {
		return ((OraclePreparedStatement) statement).sendBatch();
	}

	@Override
	public void setARRAY(int parameterIndex, ARRAY value) throws SQLException {
		((OraclePreparedStatement) statement).setARRAY(parameterIndex, value);
	}

	@Override
	public void setARRAYAtName(String parameterName, ARRAY value) throws SQLException {
		((OraclePreparedStatement) statement).setARRAYAtName(parameterName, value);
	}

	@Override
	public void setArrayAtName(String parameterName, Array value) throws SQLException {
		((OraclePreparedStatement) statement).setArrayAtName(parameterName, value);
	}

	@Override
	public void setAsciiStreamAtName(String parameterName, InputStream stream) throws SQLException {
		((OraclePreparedStatement) statement).setAsciiStreamAtName(parameterName, stream);
	}

	@Override
	public void setAsciiStreamAtName(String parameterName, InputStream stream, int length) throws SQLException {
		((OraclePreparedStatement) statement).setAsciiStreamAtName(parameterName, stream, length);
	}

	@Override
	public void setAsciiStreamAtName(String parameterName, InputStream stream, long length) throws SQLException {
		((OraclePreparedStatement) statement).setAsciiStreamAtName(parameterName, stream, length);
	}

	@Override
	public void setBFILE(int parameterIndex, BFILE value) throws SQLException {
		((OraclePreparedStatement) statement).setBFILE(parameterIndex, value);
	}

	@Override
	public void setBFILEAtName(String parameterName, BFILE value) throws SQLException {
		((OraclePreparedStatement) statement).setBFILEAtName(parameterName, value);
	}

	@Override
	public void setBLOB(int parameterIndex, BLOB value) throws SQLException {
		((OraclePreparedStatement) statement).setBLOB(parameterIndex, value);
	}

	@Override
	public void setBLOBAtName(String parameterName, BLOB value) throws SQLException {
		((OraclePreparedStatement) statement).setBLOBAtName(parameterName, value);
	}

	@Override
	public void setBfile(int parameterIndex, BFILE value) throws SQLException {
		((OraclePreparedStatement) statement).setBfile(parameterIndex, value);
	}

	@Override
	public void setBfileAtName(String parameterName, BFILE value) throws SQLException {
		((OraclePreparedStatement) statement).setBfileAtName(parameterName, value);
	}

	@Override
	public void setBigDecimalAtName(String parameterName, BigDecimal value) throws SQLException {
		((OraclePreparedStatement) statement).setBigDecimalAtName(parameterName, value);
	}

	@Override
	public void setBinaryDouble(int parameterIndex, double value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryDouble(parameterIndex, value);
	}

	@Override
	public void setBinaryDouble(int parameterIndex, BINARY_DOUBLE value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryDouble(parameterIndex, value);
	}

	@Override
	public void setBinaryDoubleAtName(String parameterName, double value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryDoubleAtName(parameterName, value);
	}

	@Override
	public void setBinaryDoubleAtName(String parameterName, BINARY_DOUBLE value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryDoubleAtName(parameterName, value);
	}

	@Override
	public void setBinaryFloat(int parameterIndex, float value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryFloat(parameterIndex, value);
	}

	@Override
	public void setBinaryFloat(int parameterIndex, BINARY_FLOAT value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryFloat(parameterIndex, value);
	}

	@Override
	public void setBinaryFloatAtName(String parameterName, float value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryFloatAtName(parameterName, value);
	}

	@Override
	public void setBinaryFloatAtName(String parameterName, BINARY_FLOAT value) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryFloatAtName(parameterName, value);
	}

	@Override
	public void setBinaryStreamAtName(String parameterName, InputStream stream) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryStreamAtName(parameterName, stream);
	}

	@Override
	public void setBinaryStreamAtName(String parameterName, InputStream stream, int length) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryStreamAtName(parameterName, stream, length);
	}

	@Override
	public void setBinaryStreamAtName(String parameterName, InputStream stream, long length) throws SQLException {
		((OraclePreparedStatement) statement).setBinaryStreamAtName(parameterName, stream, length);
	}

	@Override
	public void setBlobAtName(String parameterName, Blob value) throws SQLException {
		((OraclePreparedStatement) statement).setBlobAtName(parameterName, value);
	}

	@Override
	public void setBlobAtName(String parameterName, InputStream stream) throws SQLException {
		((OraclePreparedStatement) statement).setBlobAtName(parameterName, stream);
	}

	@Override
	public void setBlobAtName(String parameterName, InputStream stream, long length) throws SQLException {
		((OraclePreparedStatement) statement).setBlobAtName(parameterName, stream, length);
	}

	@Override
	public void setBooleanAtName(String parameterName, boolean value) throws SQLException {
		((OraclePreparedStatement) statement).setBooleanAtName(parameterName, value);
	}

	@Override
	public void setByteAtName(String parameterName, byte value) throws SQLException {
		((OraclePreparedStatement) statement).setByteAtName(parameterName, value);
	}

	@Override
	public void setBytesAtName(String parameterName, byte[] value) throws SQLException {
		((OraclePreparedStatement) statement).setBytesAtName(parameterName, value);
	}

	@Override
	public void setBytesForBlob(int parameterIndex, byte[] value) throws SQLException {
		((OraclePreparedStatement) statement).setBytesForBlob(parameterIndex, value);
	}

	@Override
	public void setBytesForBlobAtName(String parameterName, byte[] value) throws SQLException {
		((OraclePreparedStatement) statement).setBytesForBlobAtName(parameterName, value);
	}

	@Override
	public void setCHAR(int parameterIndex, CHAR value) throws SQLException {
		((OraclePreparedStatement) statement).setCHAR(parameterIndex, value);
	}

	@Override
	public void setCHARAtName(String parameterName, CHAR value) throws SQLException {
		((OraclePreparedStatement) statement).setCHARAtName(parameterName, value);
	}

	@Override
	public void setCLOB(int parameterIndex, CLOB value) throws SQLException {
		((OraclePreparedStatement) statement).setCLOB(parameterIndex, value);
	}

	@Override
	public void setCLOBAtName(String parameterName, CLOB value) throws SQLException {
		((OraclePreparedStatement) statement).setCLOBAtName(parameterName, value);
	}

	@Override
	public void setCharacterStreamAtName(String parameterName, Reader reader) throws SQLException {
		((OraclePreparedStatement) statement).setCharacterStreamAtName(parameterName, reader);
	}

	@Override
	public void setCharacterStreamAtName(String parameterName, Reader reader, long length) throws SQLException {
		((OraclePreparedStatement) statement).setCharacterStreamAtName(parameterName, reader, length);
	}

	@Override
	public void setCheckBindTypes(boolean flag) {
		((OraclePreparedStatement) statement).setCheckBindTypes(flag);
	}

	@Override
	public void setClobAtName(String parameterName, Clob value) throws SQLException {
		((OraclePreparedStatement) statement).setClobAtName(parameterName, value);
	}

	@Override
	public void setClobAtName(String parameterName, Reader reader) throws SQLException {
		((OraclePreparedStatement) statement).setClobAtName(parameterName, reader);
	}

	@Override
	public void setClobAtName(String parameterName, Reader reader, long length) throws SQLException {
		((OraclePreparedStatement) statement).setClobAtName(parameterName, reader, length);
	}

	@Deprecated
	@Override
	public void setCursor(int parameterIndex, ResultSet value) throws SQLException {
		((OraclePreparedStatement) statement).setCursor(parameterIndex, value);
	}

	@Override
	public void setCursorAtName(String parameterName, ResultSet value) throws SQLException {
		((OraclePreparedStatement) statement).setCursorAtName(parameterName, value);
	}

	@Deprecated
	@Override
	public void setCustomDatum(int parameterIndex, CustomDatum value) throws SQLException {
		((OraclePreparedStatement) statement).setCustomDatum(parameterIndex, value);
	}

	@Override
	public void setCustomDatumAtName(String parameterName, CustomDatum value) throws SQLException {
		((OraclePreparedStatement) statement).setCustomDatumAtName(parameterName, value);
	}

	@Override
	public void setDATE(int parameterIndex, DATE value) throws SQLException {
		((OraclePreparedStatement) statement).setDATE(parameterIndex, value);
	}

	@Override
	public void setDATEAtName(String parameterName, DATE value) throws SQLException {
		((OraclePreparedStatement) statement).setDATEAtName(parameterName, value);
	}

	@Override
	public void setDateAtName(String parameterName, Date value) throws SQLException {
		((OraclePreparedStatement) statement).setDateAtName(parameterName, value);
	}

	@Override
	public void setDateAtName(String parameterName, Date value, Calendar cal) throws SQLException {
		((OraclePreparedStatement) statement).setDateAtName(parameterName, value, cal);
	}

	@Override
	public void setDisableStmtCaching(boolean cache) {
		((OraclePreparedStatement) statement).setDisableStmtCaching(cache);
	}

	@Override
	public void setDoubleAtName(String parameterName, double value) throws SQLException {
		((OraclePreparedStatement) statement).setDoubleAtName(parameterName, value);
	}

	@Deprecated
	@Override
	public void setExecuteBatch(int batchValue) throws SQLException {
		((OraclePreparedStatement) statement).setExecuteBatch(batchValue);
	}

	@Override
	public void setFixedCHAR(int parameterIndex, String value) throws SQLException {
		((OraclePreparedStatement) statement).setFixedCHAR(parameterIndex, value);
	}

	@Override
	public void setFixedCHARAtName(String parameterName, String value) throws SQLException {
		((OraclePreparedStatement) statement).setFixedCHARAtName(parameterName, value);
	}

	@Override
	public void setFloatAtName(String parameterName, float value) throws SQLException {
		((OraclePreparedStatement) statement).setFloatAtName(parameterName, value);
	}

	@Override
	public void setFormOfUse(int parameterIndex, short formOfUse) {
		((OraclePreparedStatement) statement).setFormOfUse(parameterIndex, formOfUse);
	}

	@Override
	public void setINTERVALDS(int parameterIndex, INTERVALDS value) throws SQLException {
		((OraclePreparedStatement) statement).setINTERVALDS(parameterIndex, value);
	}

	@Override
	public void setINTERVALDSAtName(String parameterName, INTERVALDS value) throws SQLException {
		((OraclePreparedStatement) statement).setINTERVALDSAtName(parameterName, value);
	}

	@Override
	public void setINTERVALYM(int parameterIndex, INTERVALYM value) throws SQLException {
		((OraclePreparedStatement) statement).setINTERVALYM(parameterIndex, value);
	}

	@Override
	public void setINTERVALYMAtName(String parameterName, INTERVALYM value) throws SQLException {
		((OraclePreparedStatement) statement).setINTERVALYMAtName(parameterName, value);
	}

	@Override
	public void setIntAtName(String parameterName, int value) throws SQLException {
		((OraclePreparedStatement) statement).setIntAtName(parameterName, value);
	}

	@Override
	public void setLongAtName(String parameterName, long value) throws SQLException {
		((OraclePreparedStatement) statement).setLongAtName(parameterName, value);
	}

	@Override
	public void setNCharacterStreamAtName(String parameterName, Reader reader) throws SQLException {
		((OraclePreparedStatement) statement).setNCharacterStreamAtName(parameterName, reader);
	}

	@Override
	public void setNCharacterStreamAtName(String parameterName, Reader reader, long length) throws SQLException {
		((OraclePreparedStatement) statement).setNCharacterStreamAtName(parameterName, reader, length);
	}

	@Override
	public void setNClobAtName(String parameterName, NClob value) throws SQLException {
		((OraclePreparedStatement) statement).setNClobAtName(parameterName, value);
	}

	@Override
	public void setNClobAtName(String parameterName, Reader reader) throws SQLException {
		((OraclePreparedStatement) statement).setNClobAtName(parameterName, reader);
	}

	@Override
	public void setNClobAtName(String parameterName, Reader reader, long length) throws SQLException {
		((OraclePreparedStatement) statement).setNClobAtName(parameterName, reader, length);
	}

	@Override
	public void setNStringAtName(String parameterName, String value) throws SQLException {
		((OraclePreparedStatement) statement).setNStringAtName(parameterName, value);
	}

	@Override
	public void setNUMBER(int parameterIndex, NUMBER value) throws SQLException {
		((OraclePreparedStatement) statement).setNUMBER(parameterIndex, value);
	}

	@Override
	public void setNUMBERAtName(String parameterName, NUMBER value) throws SQLException {
		((OraclePreparedStatement) statement).setNUMBERAtName(parameterName, value);
	}

	@Override
	public void setNullAtName(String parameterName, int value) throws SQLException {
		((OraclePreparedStatement) statement).setNullAtName(parameterName, value);
	}

	@Override
	public void setNullAtName(String parameterName, int sqlType, String sqlName) throws SQLException {
		((OraclePreparedStatement) statement).setNullAtName(parameterName, sqlType, sqlName);
	}

	@Override
	public void setOPAQUE(int parameterIndex, OPAQUE value) throws SQLException {
		((OraclePreparedStatement) statement).setOPAQUE(parameterIndex, value);
	}

	@Override
	public void setOPAQUEAtName(String parameterName, OPAQUE value) throws SQLException {
		((OraclePreparedStatement) statement).setOPAQUEAtName(parameterName, value);
	}

	@Override
	public void setORAData(int parameterIndex, ORAData value) throws SQLException {
		((OraclePreparedStatement) statement).setORAData(parameterIndex, value);
	}

	@Override
	public void setORADataAtName(String parameterName, ORAData value) throws SQLException {
		((OraclePreparedStatement) statement).setORADataAtName(parameterName, value);
	}

	@Override
	public void setObjectAtName(String parameterName, Object value) throws SQLException {
		((OraclePreparedStatement) statement).setObjectAtName(parameterName, value);
	}

	@Override
	public void setObjectAtName(String parameterName, Object value, int targetSqlType) throws SQLException {
		((OraclePreparedStatement) statement).setObjectAtName(parameterName, value, targetSqlType);
	}

	@Override
	public void setObjectAtName(String parameterName, Object value, int targetSqlType, int scale) throws SQLException {
		((OraclePreparedStatement) statement).setObjectAtName(parameterName, value, targetSqlType, scale);
	}

	@Override
	public void setOracleObject(int parameterIndex, Datum value) throws SQLException {
		((OraclePreparedStatement) statement).setOracleObject(parameterIndex, value);
	}

	@Override
	public void setOracleObjectAtName(String parameterName, Datum value) throws SQLException {
		((OraclePreparedStatement) statement).setOracleObjectAtName(parameterName, value);
	}

	@Override
	public void setPlsqlIndexTable(int parameterIndex, Object arrayData, int maxLen, int curLen, int elemSqlType, int elemMaxLen) throws SQLException {
		((OraclePreparedStatement) statement).setPlsqlIndexTable(parameterIndex, arrayData, maxLen, curLen, elemSqlType, elemMaxLen);
	}

	@Override
	public void setRAW(int parameterIndex, RAW value) throws SQLException {
		((OraclePreparedStatement) statement).setRAW(parameterIndex, value);
	}

	@Override
	public void setRAWAtName(String parameterName, RAW value) throws SQLException {
		((OraclePreparedStatement) statement).setRAWAtName(parameterName, value);
	}

	@Override
	public void setREF(int parameterIndex, REF value) throws SQLException {
		((OraclePreparedStatement) statement).setREF(parameterIndex, value);
	}

	@Override
	public void setREFAtName(String parameterName, REF value) throws SQLException {
		((OraclePreparedStatement) statement).setREFAtName(parameterName, value);
	}

	@Override
	public void setROWID(int parameterIndex, ROWID value) throws SQLException {
		((OraclePreparedStatement) statement).setROWID(parameterIndex, value);
	}

	@Override
	public void setROWIDAtName(String parameterName, ROWID value) throws SQLException {
		((OraclePreparedStatement) statement).setROWIDAtName(parameterName, value);
	}

	@Override
	public void setRefAtName(String parameterName, Ref value) throws SQLException {
		((OraclePreparedStatement) statement).setRefAtName(parameterName, value);
	}

	@Override
	public void setRefType(int parameterIndex, REF value) throws SQLException {
		((OraclePreparedStatement) statement).setRefType(parameterIndex, value);
	}

	@Override
	public void setRefTypeAtName(String parameterName, REF value) throws SQLException {
		((OraclePreparedStatement) statement).setRefTypeAtName(parameterName, value);
	}

	@Override
	public void setRowIdAtName(String parameterName, RowId value) throws SQLException {
		((OraclePreparedStatement) statement).setRowIdAtName(parameterName, value);
	}

	@Override
	public void setSQLXMLAtName(String parameterName, SQLXML value) throws SQLException {
		((OraclePreparedStatement) statement).setSQLXMLAtName(parameterName, value);
	}

	@Override
	public void setSTRUCT(int parameterIndex, STRUCT value) throws SQLException {
		((OraclePreparedStatement) statement).setSTRUCT(parameterIndex, value);
	}

	@Override
	public void setSTRUCTAtName(String parameterName, STRUCT value) throws SQLException {
		((OraclePreparedStatement) statement).setSTRUCTAtName(parameterName, value);
	}

	@Override
	public void setShortAtName(String parameterName, short value) throws SQLException {
		((OraclePreparedStatement) statement).setShortAtName(parameterName, value);
	}

	@Override
	public void setStringAtName(String parameterName, String value) throws SQLException {
		((OraclePreparedStatement) statement).setStringAtName(parameterName, value);
	}

	@Override
	public void setStringForClob(int parameterIndex, String value) throws SQLException {
		((OraclePreparedStatement) statement).setStringForClob(parameterIndex, value);
	}

	@Override
	public void setStringForClobAtName(String parameterName, String value) throws SQLException {
		((OraclePreparedStatement) statement).setStringForClobAtName(parameterName, value);
	}

	@Override
	public void setStructDescriptor(int parameterIndex, StructDescriptor value) throws SQLException {
		((OraclePreparedStatement) statement).setStructDescriptor(parameterIndex, value);
	}

	@Override
	public void setStructDescriptorAtName(String parameterName, StructDescriptor value) throws SQLException {
		((OraclePreparedStatement) statement).setStructDescriptorAtName(parameterName, value);
	}

	@Override
	public void setTIMESTAMP(int parameterIndex, TIMESTAMP value) throws SQLException {
		setTimestamp(parameterIndex, value.timestampValue());
	}

	@Override
	public void setTIMESTAMPAtName(String parameterName, TIMESTAMP value) throws SQLException {
		((OraclePreparedStatement) statement).setTIMESTAMPAtName(parameterName, value);
	}

	@Override
	public void setTIMESTAMPLTZ(int parameterIndex, TIMESTAMPLTZ value) throws SQLException {
		((OraclePreparedStatement) statement).setTIMESTAMPLTZ(parameterIndex, value);
	}

	@Override
	public void setTIMESTAMPLTZAtName(String parameterName, TIMESTAMPLTZ value) throws SQLException {
		((OraclePreparedStatement) statement).setTIMESTAMPLTZAtName(parameterName, value);
	}

	@Override
	public void setTIMESTAMPTZ(int parameterIndex, TIMESTAMPTZ value) throws SQLException {
		((OraclePreparedStatement) statement).setTIMESTAMPTZ(parameterIndex, value);
	}

	@Override
	public void setTIMESTAMPTZAtName(String parameterName, TIMESTAMPTZ value) throws SQLException {
		((OraclePreparedStatement) statement).setTIMESTAMPTZAtName(parameterName, value);
	}

	@Override
	public void setTimeAtName(String parameterName, Time value) throws SQLException {
		((OraclePreparedStatement) statement).setTimeAtName(parameterName, value);
	}

	@Override
	public void setTimeAtName(String parameterName, Time value, Calendar cal) throws SQLException {
		((OraclePreparedStatement) statement).setTimeAtName(parameterName, value, cal);
	}

	@Override
	public void setTimestampAtName(String parameterName, Timestamp value) throws SQLException {
		((OraclePreparedStatement) statement).setTimestampAtName(parameterName, value);
	}

	@Override
	public void setTimestampAtName(String parameterName, Timestamp value, Calendar cal) throws SQLException {
		((OraclePreparedStatement) statement).setTimestampAtName(parameterName, value, cal);
	}

	@Override
	public void setURLAtName(String parameterName, URL value) throws SQLException {
		((OraclePreparedStatement) statement).setURLAtName(parameterName, value);
	}

	@Override
	public void setUnicodeStreamAtName(String parameterName, InputStream stream, int length) throws SQLException {
		((OraclePreparedStatement) statement).setUnicodeStreamAtName(parameterName, stream, length);;
	}

}
