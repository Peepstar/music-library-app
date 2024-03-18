package MusicLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToDB {
    //Database login and database information

    //Set up "user" and "password" to your configurations
    private static final String  user = "your_user";
    private static final String password = "your_password";
    private static final String port = "5432";
    private static final String hosting = "localhost";
    private static final String baseDeDatos = "music_library";

    public static Connection initialConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://" + hosting + ":" + port + "/"
                    + baseDeDatos + "?charSet=UTF-8", user, password);
        } catch (SQLException e) {
            System.out.println("Unable to connect to Database: " + e.getMessage());
        }
        return connection;
    }
}



