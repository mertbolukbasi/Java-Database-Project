package database;

import model.User;

import java.sql.*;

public class Database {


    public static final String DATABASE_NAME = "Group16";
    public static final String DATABASE_USER = "myuser";
    public static final String DATABASE_PASSWORD = "1234";
    public static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;
    private static Connection connection;

    public static Connection openDatabase() throws SQLException {
        connection = DriverManager.getConnection(CONNECTION_STRING, DATABASE_USER, DATABASE_PASSWORD);
        return connection;
    }

    public static void closeDatabase() throws SQLException {
        if(connection != null) connection.close();
    }

    public static User login(String username, String password) throws SQLException {

        String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

        Connection dbConnection = Database.openDatabase();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            int id = resultSet.getInt("user_id");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            String role = resultSet.getString("role");
            Date created_at = resultSet.getDate("created_at");
            Date updated_at = resultSet.getDate("updated_at");

            User user = new User() {
                @Override
                public void showUserMenu() {

                }
            };
            user.setUserId(id);
            user.setName(name);
            user.setSurname(surname);
            user.setRole(role);
            user.setCreated_at(created_at);
            user.setUpdated_at(updated_at);
            dbConnection.close();
            return user;
        } else {
            return null;
        }
    }
}
