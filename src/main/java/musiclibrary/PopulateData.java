package musiclibrary;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;



public class PopulateData {

    public static boolean populateInitialData() {

        //CSV file with music as initial database
        // --->>> Configure this path to your project path (DO NOT CHANGE AFTER "//music-library-app"<<<---
        String csvFile = "path//to//your//project//music-library-app//src//main//resources//dataset.csv";
        //Map to keep track of primary keys for each data inserted on the DB and then use it to include foreign keys
        Map<String, Integer> keysMap = new HashMap<>();
        Instant startTime = Instant.now(); //To calculate time that takes to populate the data

        try {
            //Connecting to database
            Connection connection = ConnectToDB.initialConnection();
            //Creating database and tables in case that they don't exist
            createTablesAndIndexes();
            //Checking if th e database is already populated with the CSV file, if so the CSV file will not be read again
            PreparedStatement checkStatement = connection.prepareStatement("SELECT EXISTS(SELECT 1 FROM tracks)");
            ResultSet checkSet = checkStatement.executeQuery();
            checkSet.next();
            //If this value is true, it means that the "tracks" table has at least one row so we just return true to exit method
            if (checkSet.getBoolean(1)) {
                System.out.println("DATABASE IS ALREADY POPULATED");
                return true;
            }
            //Reading CSV file
            try (Reader reader = new FileReader(csvFile)) {
                CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',')
                        .withQuote('"')
                        .withHeader();
                Iterable<CSVRecord> records = format.parse(reader);
                for (CSVRecord record : records) {
                    //Getting needed data from CSV file
                    String[] artists = record.get("artists").split(";"); //We are using an array in case there are different artists for the same track
                    String albumName = record.get("album_name");
                    String trackName = record.get("track_name");
                    int popularity = Integer.parseInt(record.get("popularity"));
                    int duration = Integer.parseInt(record.get("duration_ms")) / 1000;
                    boolean explicit = Boolean.parseBoolean(record.get("explicit"));
                    String genre = record.get("track_genre");
                    //Insert data on database for each artist related to this song
                    for (String currArtist : artists) {
                        //Artist name as lowerCase to avoid duplication on database
                        currArtist = currArtist.toLowerCase();
                        //Inserting artist to artist table or getting its ID if already exists on the database
                        Integer artistID = keysMap.get(currArtist + "_artist");
                        if (artistID == null) {
                            PreparedStatement artistStatement = connection.prepareStatement("INSERT INTO artist (name) VALUES (?) ON CONFLICT (name) DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
                            artistStatement.setString(1, currArtist);
                            artistStatement.executeUpdate();

                            ResultSet artistSet = artistStatement.getGeneratedKeys();
                            if (artistSet.next()) {
                                artistID = artistSet.getInt(1);
                                //Adding inserted row key in map to avoid duplication and set foreign keys
                                keysMap.put(currArtist + "_artist", artistID);
                            }
                        }
                        //Inserting genre to genre table or getting its ID if already exists on the database
                        Integer genreID = keysMap.get(genre + "_genre");
                        if (genreID == null) {
                            PreparedStatement genreStatement = connection.prepareStatement("INSERT INTO genre (name) VALUES (?) ON CONFLICT (name) DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
                            genreStatement.setString(1, genre);
                            genreStatement.executeUpdate();

                            ResultSet genreSet = genreStatement.getGeneratedKeys();
                            if (genreSet.next()) {
                                genreID = genreSet.getInt(1);
                                //Adding inserted row key in map to avoid duplication and set foreign keys
                                keysMap.put(genre + "_genre", genreID);
                            }
                        }
                        //Inserting album to album table or getting its ID if already exists on the database
                        Integer albumID = keysMap.get(albumName + "_album");
                        if (albumID == null) {
                            PreparedStatement albumStatement = connection.prepareStatement("INSERT INTO album (name, artist_id) VALUES (?,?) ON CONFLICT (name) DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
                            albumStatement.setString(1, albumName);
                            albumStatement.setInt(2, artistID);
                            albumStatement.executeUpdate();

                            ResultSet albumSet = albumStatement.getGeneratedKeys();
                            if (albumSet.next()) {
                                albumID = albumSet.getInt(1);
                                //Adding inserted row key in map to avoid duplication and set foreign keys
                                keysMap.put(albumName + "_album", albumID);
                            }
                        }
                        //Inserting track to track table or getting its ID if already exists on the database
                        Integer trackID = keysMap.get(trackName + "_" + albumName);
                        if (trackID == null) {
                            PreparedStatement trackStatement = connection.prepareStatement("INSERT INTO tracks (name, popularity, duration, explicit, album_id, genre_id) " +
                                    "VALUES (?,?,?,?,?,?) ON CONFLICT (name, album_id) DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
                            trackStatement.setString(1, trackName);
                            trackStatement.setInt(2, popularity);
                            trackStatement.setInt(3, duration);
                            trackStatement.setBoolean(4, explicit);
                            trackStatement.setInt(5, albumID);
                            trackStatement.setInt(6, genreID);
                            trackStatement.executeUpdate();

                            ResultSet trackSet = trackStatement.getGeneratedKeys();
                            if (trackSet.next()) {
                                trackID = trackSet.getInt(1);
                                //Adding inserted row key in map to avoid duplication and set foreign keys
                                keysMap.put(trackName + "_" + albumName, trackID);
                            }
                        }
                        //Inserting artist_id and track_id to junction table (tracks_artist)
                        if (artistID != null && trackID != null) {
                            PreparedStatement artist_trackStatement = connection.prepareStatement("INSERT INTO tracks_artist (artist_id, track_id) VALUES (?,?)");
                            artist_trackStatement.setInt(1, artistID);
                            artist_trackStatement.setInt(2, trackID);
                            artist_trackStatement.executeUpdate();
                        }

                    }
                }
                //Database was populated without issue so we return true
                Instant endTime = Instant.now(); ////To calculate time that takes to populate the data
                Duration elapsedTime = Duration.between(startTime, endTime);
                long elapsedSeconds = elapsedTime.toSeconds();

                System.out.println("\nTime taken to fill the database: " + elapsedSeconds + " seconds\n");
                System.out.println("Database has been populated with initial file");
                return true;

            } catch (IOException e) {
                //If the code reach this point it means that there was an issue filling out database, so we return false
                System.out.println(e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            //If the code reach this point it means that there was an issue filling out database, so we return false
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void createTablesAndIndexes() {

        //Creating SQL statements to create tables on database.
        String genreTable = "CREATE TABLE IF NOT EXISTS genre (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT UNIQUE NOT NULL);";

        String artistTable = "CREATE TABLE IF NOT EXISTS artist (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT UNIQUE NOT NULL);";

        String albumTable = "CREATE TABLE IF NOT EXISTS album (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT UNIQUE NOT NULL, " +
                "artist_id INTEGER REFERENCES artist(id) ON DELETE CASCADE NOT NULL);";

        String tracksTable = "CREATE TABLE IF NOT EXISTS tracks (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "popularity INTEGER, " +
                "duration INTEGER, " +
                "explicit BOOLEAN, " +
                "album_id INTEGER REFERENCES album(id) ON DELETE CASCADE NOT NULL, " +
                "genre_id INTEGER REFERENCES genre(id) ON DELETE CASCADE NOT NULL, " +
                "UNIQUE (name, album_id));";

        String tracks_artistTable = "CREATE TABLE IF NOT EXISTS tracks_artist (" +
                "artist_id INTEGER REFERENCES artist(id) ON DELETE CASCADE NOT NULL, " +
                "track_id INTEGER REFERENCES tracks(id) ON DELETE CASCADE NOT NULL);";

        String[] createIndexStatements = {
                "CREATE INDEX idx_genre_name ON genre (name)",
                "CREATE INDEX idx_artist_name ON artist (name)",
                "CREATE INDEX idx_album_name ON album (name)",
                "CREATE INDEX idx_track_name ON tracks (name)",
        };


        try {
            //Connecting to database
            Connection connection = ConnectToDB.initialConnection();


            //Run statements to create tables and indexes.
            Statement genreStatement = connection.createStatement();
            genreStatement.execute(genreTable);

            Statement artistStatement = connection.createStatement();
            artistStatement.execute(artistTable);

            Statement albumStatement = connection.createStatement();
            albumStatement.execute(albumTable);

            Statement tracksStatement = connection.createStatement();
            tracksStatement.execute(tracksTable);

            Statement tracks_artistStatement = connection.createStatement();
            tracks_artistStatement.execute(tracks_artistTable);

            Statement indexStatement = connection.createStatement();
            for (String sql : createIndexStatements) {
                indexStatement.execute(sql);
            }

        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }
}

