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

import org.postgresql.util.PGInterval;

import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;

/**
 * 
 * Datatype conversion for Oracle JDBC Redirect package
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 * 
 */
public class OraForwardPgUtils {

	private static final int ORA_OFFSET = 60;

	/**
	 * Converts Oracle INTERVALDS to PostgreSQL PGInterval
	 *     For Oracle format description please see <a href="https://www.orafaq.com/wiki/Interval">Interval</a>
	 * 
	 * @param value
	 * @return
	 */
	public static PGInterval pgInterval(INTERVALDS value) {
		final byte[] array = value.getBytes();
		double seconds = unsigned(array[6]) - ORA_OFFSET;
		int nanos = decodeOraBytes(array, 7);
		if (nanos > 0) {
			seconds += ((double) nanos) / 1_000_000_000;
		}
		return new PGInterval(0, 0, 
				decodeOraBytes(array, 0),
				unsigned(array[4]) - ORA_OFFSET,
				unsigned(array[5]) - ORA_OFFSET,
				seconds);
	}

	/**
	 * Converts Oracle INTERVALYM to PostgreSQL PGInterval
	 *     For Oracle format description please see <a href="https://www.orafaq.com/wiki/Interval">Interval</a>
	 * 
	 * @param value
	 * @return
	 */
	public static PGInterval pgInterval(INTERVALYM value) {
		final byte[] array = value.getBytes();
		return new PGInterval(
				decodeOraBytes(array, 0),
				unsigned(array[4]) - ORA_OFFSET, 
				0, 0, 0, 0);
	}

	private static int unsigned(byte value) {
		return value & 0xFF;
	}

	private static int decodeOraBytes(byte[] array, int msb) {
		int value =
				unsigned(array[msb + 3]) | unsigned(array[msb + 2]) << 8 | unsigned(array[msb + 1]) << 16 | unsigned(array[msb]) << 24;
		return -(Integer.MIN_VALUE - value);
	}

	public static void main(String[] argv) {
		System.out.println("!");
		byte[] msb = new byte[11];
		msb[0] = (byte) 128;
		msb[1] = 0;
		msb[2] = 2;
		msb[3] = (byte) 253;
		msb[4] = 71;
		msb[5] = 117;
		msb[6] = 119;
		msb[7] = (byte) 128;
		msb[8] = 0;
		msb[9] = 0;
		msb[10] = 0;
		INTERVALDS ids = new INTERVALDS(msb);
		String strIds = ids.stringValue();
		System.out.println(strIds);
		INTERVALDS fromString = new INTERVALDS(strIds);
		System.out.println(fromString.stringValue());
	}
}
