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
import java.sql.Wrapper;

public abstract class OraForwardWrapper implements Wrapper, OraForwardUnWrap {

	private final Object shadow;

	protected OraForwardWrapper(final Object shadow) {
		this.shadow = shadow;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		final Object result;
		if (iface.isAssignableFrom(this.getClass())) {
			result = this;
		} else if (iface.isAssignableFrom(shadow.getClass())) {
			result = unwrapProxy();
		} else if (Wrapper.class.isAssignableFrom(shadow.getClass())) {
			result = ((Wrapper) unwrapProxy()).unwrap(iface);
		} else {
			throw new SQLException("Unable to unwrap " + iface.getName());
		}
		return iface.cast(result);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if (iface.isAssignableFrom(getClass())) {
			return true;
		} else if (iface.isAssignableFrom(shadow.getClass())) {
			return true;
		} else if (Wrapper.class.isAssignableFrom(shadow.getClass())) {
			((Wrapper) unwrapProxy()).isWrapperFor(iface);
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OraForwardUnWrap) {
			return shadow.equals(((OraForwardUnWrap) obj).unwrapProxy());
		} else {
			return shadow.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return shadow.hashCode();
	}

	@Override
	public Object unwrapProxy() {
		return shadow;
	}

}
