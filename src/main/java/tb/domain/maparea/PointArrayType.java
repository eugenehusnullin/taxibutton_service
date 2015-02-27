package tb.domain.maparea;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

@SuppressWarnings("rawtypes")
public class PointArrayType implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	@Override
	public Class returnedClass() {
		return Point[].class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return false;
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return 0;
	}

	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		String value = (String) resultSet.getObject(names[0]);
		return this.fromSqlValue(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, 0);
		} else {
			statement.setObject(index, this.toSqlValue((List<Point>) value));
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return null;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return null;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return null;
	}

	private String toSqlValue(List<Point> value) {
		if (null == value || 0 == value.size()) {
			return null;
		}

		StringBuilder text = new StringBuilder();
		for (Point item : value) {
			text.append(item.getLatitude());
			text.append(":");
			text.append(item.getLongitude());
			text.append(";");
		}

		return text.toString();
	}

	private List<Point> fromSqlValue(String value) {
		List<Point> list = new ArrayList<Point>();
		if (null == value) {
			return list;
		}

		String[] points = value.split(";");
		for (int i = 0; i < points.length; i++) {
			String[] parts = points[i].split(":");
			Point item = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
			list.add(item);
		}

		return list;
	}
}
