package MusicLibrary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToDB {
    //Database login and database information
    private static final String  user = "postgres";
    private static final String password = "5A74008816j";
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



