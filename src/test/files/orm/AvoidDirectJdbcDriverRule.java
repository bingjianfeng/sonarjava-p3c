import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseDao {

    public void getConnection () {
        String url = "jdbc:mysql://localhost:3306/your_database?useSSL=false&serverTimezone=UTC";
        String user = "your_username";
        String password = "your_password";
        Connection conn = DriverManager.getConnection(url, user, password); // Noncompliant
        return conn;
    }

    public ResultSet executeQuery (String sql) {
        PreparedStatement stmt = getConnection().prepareStatement(sql); // Noncompliant
        return stmt.executeQuery(); // Noncompliant
    }

    public void executeUpdate (String sql) {
        PreparedStatement stmt = getConnection().prepareStatement(sql); // Noncompliant
        return stmt.executeUpdate(); // Noncompliant
    }
}


public class UserDao {

    public BaseDao getBaseDao () {
        return new BaseDao();
    }

    public ResultSet getUserList () {
        return getBaseDao().executeQuery("select user_name from user"); // Noncompliant
    }

    public void updateUser () {
        return getBaseDao().executeUpdate("update user set user_name = 'jack' where user_id = 1"); // Noncompliant
    }
}