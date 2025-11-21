package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBHelper {
	/**
	 * Simple JDBC helper used by tests to execute SQL and fetch results.
	 *
	 * What you need to configure:
	 * - jdbcUrl: Points to your database and schema.
	 *   Examples:
	 *     MySQL:     jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
	 *     PostgreSQL:jdbc:postgresql://localhost:5432/mydb
	 *     Oracle:    jdbc:oracle:thin:@localhost:1521/xe
	 *
	 * - username / password: Valid DB credentials with SELECT permission on your tables.
	 *
	 * Where to set these:
	 * - Option A (recommended): Pass system properties when running Maven:
	 *     mvn clean test -Ddb.url="jdbc:mysql://localhost:3306/mydb" -Ddb.user="root" -Ddb.pass="secret"
	 * - Option B: Edit defaults in tests (see DataValidationTest#setUp).
	 *
	 * Drivers:
	 * - For MySQL, this project already includes mysql-connector-java.
	 * - For PostgreSQL/Oracle, add the appropriate driver to pom.xml and switch the JDBC URL.
	 */
	private final String jdbcUrl;
	private final String username;
	private final String password;

	public DBHelper(String jdbcUrl, String username, String password) {
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(jdbcUrl, username, password);
	}

	public ResultSet executeQuery(String query) throws SQLException {
		Connection connection = getConnection();
		Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.closeOnCompletion();
		return statement.executeQuery(query);
	}

	public String getValueFromDB(String query) throws SQLException {
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(query)) {
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
			return null;
		}
	}

	public String getColumnValue(String query, String columnName) throws SQLException {
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement();
		     ResultSet resultSet = statement.executeQuery(query)) {
			if (resultSet.next()) {
				return resultSet.getString(columnName);
			}
			return null;
		}
	}

	public boolean recordExists(String query, Object... params) throws SQLException {
		try (Connection connection = getConnection();
		     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	/**
	 * Execute multiple independent queries and concatenate the first-column result
	 * from the first row of each query into a single string with the chosen delimiter.
	 *
	 * Usage:
	 *   List<String> queries = Arrays.asList(
	 *     "SELECT name FROM users WHERE id=1",
	 *     "SELECT email FROM users WHERE id=1",
	 *     "SELECT age FROM users WHERE id=1"
	 *   );
	 *   String dbBlob = db.getConcatenatedValues(queries, "|"); // e.g. "John|john@x.com|32"
	 *
	 * Notes:
	 * - If a query returns no rows, null is appended (as an empty token).
	 * - If you need multiple columns per query, write one SQL that selects the
	 *   concatenation on the DB side (e.g., CONCAT in MySQL) or create separate queries.
	 */
	public String getConcatenatedValues(List<String> queries, String delimiter) throws SQLException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < queries.size(); i++) {
			String value = getValueFromDB(queries.get(i));
			if (value != null) {
				builder.append(value);
			}
			if (i < queries.size() - 1) {
				builder.append(delimiter);
			}
		}
		return builder.toString();
	}
}


