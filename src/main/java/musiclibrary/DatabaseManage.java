package musiclibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManage {
    private static Connection DBconnection;

    public DatabaseManage() {
        DBconnection = ConnectToDB.initialConnection();
    }

    //Add methods ---
    public int addGenre(String genreName) {
        int resultKey = -1;
        try {
            PreparedStatement statement = DBconnection.prepareStatement("INSERT INTO genre (name) VALUES (?) ON CONFLICT DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, genreName);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet set = statement.getGeneratedKeys();
                set.next();
                resultKey = set.getInt(1); //Return generated key by insert
            }
        } catch (SQLException e) {
            System.out.println("\nError inserting data into database: " + e.getMessage());
        }
        return resultKey;
    }

    public int addArtist(String artistName) {
        int resultKey = -1;
        try {
            PreparedStatement statement = DBconnection.prepareStatement("INSERT INTO artist (name) VALUES (?) ON CONFLICT DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, artistName);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet set = statement.getGeneratedKeys();
                set.next();
                resultKey = set.getInt(1); //Return generated key by insert
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        return resultKey;
    }

    public int addAlbum(String albumName, String artistName) {
        int resultKey = -1;
        try {
            // Check if artist already exists in database and get its ID
            int artistId = checkArtist(artistName);
            //Insert album into the database
            PreparedStatement statement = DBconnection.prepareStatement("INSERT INTO album (name, artist_id) VALUES (?, ?) ON CONFLICT DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, albumName);
            statement.setInt(2, artistId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet set = statement.getGeneratedKeys();
                set.next();
                resultKey = set.getInt(1); //Return generated key by insert
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        return resultKey;
    }

    public int addTrack(String trackName, String albumName, String genre) {
        //track ID to add it to tracks_artist table and to return
        int trackId = -1;
        try {
            //Check if album exist in database
            List<Integer> albumInfo = checkAlbum(albumName); //Getting album id and artist id or add it if necessary
            int albumId = 0;
            int artistId = 0;
            if (albumInfo.size() > 0) {
                albumId = albumInfo.get(0); //Getting album ID
                artistId = albumInfo.get(1); //Getting artist ID
            }
            //Check if genre exist in database and get its ID
            int genreId = checkGenre(genre);
            //Insert track into database and get its ID
            PreparedStatement statement = DBconnection.prepareStatement("INSERT INTO tracks (name, album_id, genre_id) VALUES (?,?,?) ON CONFLICT DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, trackName);
            statement.setInt(2, albumId);
            statement.setInt(3, genreId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet set = statement.getGeneratedKeys();
                set.next();
                trackId = set.getInt(1); //getting new generated key to add to junction table and return it
                if (addArtist_Track(trackId, artistId)) {
                    //Track added to DB and also added to tracks_artist table so we can return true
                    return trackId;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        //Unable to complete insertion so trackId will be -1
        return trackId;
    }

    public int addTrack(String trackName, String albumName, String genre, int duration, int popularity, boolean explicit) {
        //track ID to add it to tracks_artist table and return it
        int trackId = -1;
        try {
            //Check if album exist in database
            List<Integer> albumInfo = checkAlbum(albumName); //Getting album id and artist id or add it if necessary
            int albumId = -1;
            int artistId = -1;
            if (albumInfo.size() > 0) {
                albumId = albumInfo.get(0); //Getting album ID
                artistId = albumInfo.get(1); //Getting artist ID
            }
            //Check if genre exist in database and get its ID
            int genreId = checkGenre(genre);
            //Insert track into database and get its ID
            PreparedStatement statement = DBconnection.prepareStatement("INSERT INTO tracks (name, album_id, genre_id, popularity, duration, explicit) VALUES (?,?,?,?,?,?) ON CONFLICT DO NOTHING", PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, trackName);
            statement.setInt(2, albumId);
            statement.setInt(3, genreId);
            statement.setInt(4, popularity);
            statement.setInt(5, duration);
            statement.setBoolean(6, explicit);
            int rowsAffected = statement.executeUpdate();


            if (rowsAffected > 0) {
                ResultSet set = statement.getGeneratedKeys();
                set.next();
                trackId = set.getInt(1); //getting new generated key
                if (addArtist_Track(trackId, artistId)) {
                    //Track added to DB and also added to tracks_artist table so we can return true
                    return trackId;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        //Unable to complete insertion so trackId will be -1
        return trackId;
    }

    //Delete methods ---
    //it will return true if deletion was completed, otherwise it will return false
    public boolean deleteGenre(int genreId) {
        String tableName = "genre";
        return deleteRow(tableName, genreId); //Deleting genreName from genre table
    }

    public boolean deleteArtist(int artistId) {
        String tableName = "artist";
        return deleteRow(tableName, artistId); //Deleting genreName from genre table
    }

    public boolean deleteAlbum(int albumId) {
        String tableName = "album";
        return deleteRow(tableName, albumId); //Deleting genreName from genre table
    }

    public boolean deleteTrack(int trackId) {
        String tableName = "tracks";
        return deleteRow(tableName, trackId); //Deleting genreName from genre table
    }

    //Update methods
    public boolean updateGenre(int genreId, String newGenre) {
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE genre SET name = ? WHERE id = ?")) {
            statement.setString(1, newGenre);
            statement.setInt(2, genreId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Genre updated on database\n");
                showGenreInfo(genreId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //Genre wasn't updated
        return false;
    }

    //Update methods
    public boolean updateArtist(int artistId, String newArtist) {
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE artist SET name = ? WHERE id = ?")) {
            statement.setString(1, newArtist);
            statement.setInt(2, artistId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist updated on database\n");
                showArtistInfo(artistId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //Artist wasn't updated
        return false;
    }

    public boolean updateAlbum(int albumId, String newAlbum) {
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE album SET name = ? WHERE id = ?")) {
            statement.setString(1, newAlbum);
            statement.setInt(2, albumId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album updated on database\n");
                showAlbumInfo(albumId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //Album wasn't updated
        return false;
    }

    //Update artist_id associated to album
    public boolean updateArtistIdOnAlbum(int albumId, int newArtistId) {
        //Updating artist_id for albumId
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE album SET artist_id = ? WHERE id = ?")) {
            statement.setInt(1, newArtistId);
            statement.setInt(2, albumId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then update worked so we return true
            if (rowsAffected > 0) {
                System.out.println("\nArtist updated on album\n");
                showAlbumInfo(albumId);
                return true;
            }

        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;
    }

    public boolean updateTrackName(int trackId, String newTrackName) {
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks SET name = ? WHERE id = ?")) {
            statement.setString(1, newTrackName);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nTrack updated on database\n");
                showSingleTrack(trackId);
                return true;
            }
        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //Track wasn't updated
        return false;
    }

    public boolean updateAlbumIdOnTrack(int trackId, int newAlbumId) {
        //Updating album_id for trackId
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks SET album_id = ? WHERE id = ?")) {
            statement.setInt(1, newAlbumId);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then updated worked so we return true
            if (rowsAffected > 0) {
                System.out.println("\nAlbum updated on track\n");
                showSingleTrack(trackId);
                return true;
            }

        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;
    }

    public boolean updateArtistIdOnTrack(int trackId, int newArtistId) {
        //Updating artist_id for track_id on junction table tracks_artist
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks_artist SET artist_id = ? WHERE track_id = ?")) {
            statement.setInt(1, newArtistId);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then update worked so we return true
            if (rowsAffected > 0) {
                System.out.println("\nArtist updated on track\n");
                System.out.println("ADVISE: It is recommended to also update album associated to song to match artist\n");
                showSingleTrack(trackId);
                return true;
            }

        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;

    }

    public boolean updateGenreIdOnTrack(int trackId, int newGenreId) {
        //Updating genre_id for track_id on tracks table
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks SET genre_id = ? WHERE id = ?")) {
            statement.setInt(1, newGenreId);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then update worked so we return true
            if (rowsAffected > 0) {
                System.out.println("\nGenre updated on track\n");
                showSingleTrack(trackId);
                return true;
            }

        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;
    }

    public boolean updateDuration(int trackId, int duration) {
        //Updating duration for trackId on tracks table
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks SET duration = ? WHERE id = ?")) {
            statement.setInt(1, duration);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then update worked so we return true
            if (rowsAffected > 0) {
                showSingleTrack(trackId);
                return true;
            }

        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;
    }

    public boolean updatePopularity(int trackId, int popularity) {
        //Updating popularity for trackId on tracks table
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks SET popularity = ? WHERE id = ?")) {
            statement.setInt(1, popularity);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then update worked so we return true
            if (rowsAffected > 0) {
                showSingleTrack(trackId);
                return true;
            }
        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;
    }

    public boolean updateExplicity(int trackId, boolean explicity) {
        //Updating explicity for trackId on track table
        try (PreparedStatement statement = DBconnection.prepareStatement("UPDATE tracks SET explicit = ? WHERE id = ?")) {
            statement.setBoolean(1, explicity);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();
            //If rows affected is more than 0, then update worked so we return true
            if (rowsAffected > 0) {
                showSingleTrack(trackId);
                return true;
            }
        } catch (SQLException e) {
            //No update completed
            System.out.println("\nError updating database: " + e.getMessage());
            return false;
        }
        //No update completed
        return false;
    }

    ///Methods to show rows information
    //Show genre information given its id
    public boolean showGenreInfo(int id) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("SELECT genre.id, genre.name, COUNT(tracks.id) AS total_tracks" +
                    " FROM genre LEFT JOIN tracks ON tracks.genre_id = genre.id" +
                    " WHERE genre.id = ?" +
                    " GROUP BY genre.id, genre.name");

            statement.setInt(1, id);
            ResultSet genreResult = statement.executeQuery();

            if (genreResult.next()) {
                System.out.println("\nGenre ID: " + genreResult.getInt("id"));
                System.out.println("Genre name: " + genreResult.getString("name"));
                System.out.println("Total number of tracks that belongs to this genre: " + genreResult.getInt("total_tracks"));
                return true;
            } else {
                System.out.println("No results for specified genre");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error showing genre:" + e.getMessage());
            return false;
        }
    }

    //Show artist information given its id
    public boolean showArtistInfo(int id) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("SELECT artist.id, artist.name, COUNT(tracks_artist.track_id) AS total_tracks" +
                    " FROM artist LEFT JOIN tracks_artist ON tracks_artist.artist_id = artist.id" +
                    " WHERE artist.id = ?" +
                    " GROUP BY artist.id, artist.name");
            statement.setInt(1, id);
            ResultSet artistResult = statement.executeQuery();

            if (artistResult.next()) {
                System.out.println("\nArtist ID: " + artistResult.getInt("id"));
                System.out.println("Artist name: " + artistResult.getString("name"));
                System.out.println("Total number of tracks that belongs to this artist: " + artistResult.getInt("total_tracks"));
                return true;
            } else {
                System.out.println("No result for specified artist");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error showing artist: " + e.getMessage());
            return false;
        }
    }

    //Show album information given its id
    public boolean showAlbumInfo(int id) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("SELECT album.id, album.name, artist.name AS artist_name" +
                    " FROM album JOIN artist ON album.artist_id = artist.id" +
                    " WHERE album.id = ?");
            statement.setInt(1, id);
            ResultSet albumResult = statement.executeQuery();

            if (albumResult.next()) {
                System.out.println("\nAlbum ID: " + albumResult.getInt("id"));
                System.out.println("Album name: " + albumResult.getString("name"));
                System.out.println("Artist name: " + albumResult.getString("artist_name"));

                statement = DBconnection.prepareStatement("SELECT name FROM tracks WHERE tracks.album_id = ?");
                statement.setInt(1, id);
                ResultSet tracksResult = statement.executeQuery();
                System.out.println("\nTracks: ");
                while (tracksResult.next()) {
                    String trackName = tracksResult.getString("name");
                    System.out.println("- " + trackName);
                }
                return true;

            } else {
                System.out.println("No result for specified album");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error showing album: " + e.getMessage());
            return false;

        }
    }

    //Show single track information given its id
    public boolean showSingleTrack(int id) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("SELECT tracks.id, tracks.name, genre.name AS genre_name, album.name AS album_name, artist.name AS artist_name, tracks.duration, tracks.popularity, tracks.explicit" +
                    " FROM tracks JOIN album ON tracks.album_id = album.id" +
                    " JOIN genre ON tracks.genre_id = genre.id JOIN artist ON album.artist_id = artist.id" +
                    " WHERE tracks.id = ?");
            statement.setInt(1, id);
            ResultSet trackResult = statement.executeQuery();

            if (trackResult.next()) {
                System.out.println("\nTrack ID: " + trackResult.getInt("id"));
                System.out.println("Track name: " + trackResult.getString("name"));
                System.out.println("Album: " + trackResult.getString("album_name"));
                System.out.println("Artist: " + trackResult.getString("artist_name"));
                System.out.println("Genre: " + trackResult.getString("genre_name"));
                System.out.print("Duration: " + trackResult.getInt("duration") + "  ");
                System.out.print("Popularity " + trackResult.getInt("popularity") + "  ");
                System.out.println("Explicit: " + trackResult.getBoolean("explicit"));

                return true;
            } else {
                System.out.println("Track ID not found");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error showing track: " + e.getMessage());
            return false;
        }
    }

    public boolean showTracksAlbums_Artist(int id) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("SELECT album.id AS album_id FROM album JOIN artist ON album.artist_id = artist.id WHERE artist.id = ?");
            statement.setInt(1, id);
            ResultSet tracks_albumsResult = statement.executeQuery();

            while (tracks_albumsResult.next()) {
                showAlbumInfo(tracks_albumsResult.getInt("album_id"));
            }

            return true;


        } catch (SQLException e) {
            System.out.println("Error showing track: " + e.getMessage());
            return false;
        }
    }

    public boolean showTracksGenre(int id) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("SELECT tracks.name AS tracks_names FROM tracks JOIN genre ON tracks.genre_id = genre.id WHERE genre.id = ?");
            statement.setInt(1, id);
            ResultSet tracks_genreResult = statement.executeQuery();

            while (tracks_genreResult.next()) {
                System.out.println("- " + tracks_genreResult.getString("tracks_names"));
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error showing track: " + e.getMessage());
            return false;
        }
    }

    public boolean showTracksByPopularity(int order, int limit) {
        try {
            PreparedStatement statement;
            if (order == 1) {
                statement = DBconnection.prepareStatement("SELECT tracks.name AS track_Name, tracks.popularity AS track_Popularity FROM tracks WHERE popularity IS NOT NULL ORDER BY popularity ASC LIMIT ?");
                statement.setInt(1, limit);
                ResultSet tracks_popularityResult = statement.executeQuery();

                while (tracks_popularityResult.next()) {
                    System.out.println(tracks_popularityResult.getString("track_Name") + " - " + tracks_popularityResult.getInt("track_Popularity"));
                }

            } else {
                statement = DBconnection.prepareStatement("SELECT tracks.name AS track_Name, tracks.popularity AS track_Popularity FROM tracks WHERE popularity IS NOT NULL ORDER BY popularity DESC LIMIT ?");
                statement.setInt(1, limit);
                ResultSet tracks_popularityResult = statement.executeQuery();

                while (tracks_popularityResult.next()) {
                    System.out.println(tracks_popularityResult.getString("track_Name") + " - " + tracks_popularityResult.getInt("track_Popularity"));
                }

            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error showing track: " + e.getMessage());
            return false;
        }
    }

    public boolean showTracksByDuration(int order, int limit) {
        try {
            PreparedStatement statement;
            if (order == 1) {
                statement = DBconnection.prepareStatement("SELECT tracks.name AS track_Name, tracks.duration AS track_Duration FROM tracks WHERE duration IS NOT NULL ORDER BY duration ASC LIMIT ?");
                statement.setInt(1, limit);
                ResultSet tracks_popularityResult = statement.executeQuery();

                while (tracks_popularityResult.next()) {
                    System.out.println(tracks_popularityResult.getString("track_Name") + " - " + tracks_popularityResult.getInt("track_Duration"));
                }

            } else {
                statement = DBconnection.prepareStatement("SELECT tracks.name AS track_Name, tracks.duration AS track_Duration FROM tracks WHERE duration IS NOT NULL ORDER BY duration DESC LIMIT ?");
                statement.setInt(1, limit);
                ResultSet tracks_popularityResult = statement.executeQuery();

                while (tracks_popularityResult.next()) {
                    System.out.println(tracks_popularityResult.getString("track_Name") + " - " + tracks_popularityResult.getInt("track_Duration"));
                }

            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error showing track: " + e.getMessage());
            return false;
        }
    }

    public boolean showTracksByExplicity(int order, int limit, boolean explicity) {
        try {
            PreparedStatement statement;
            if (order == 1) {
                statement = DBconnection.prepareStatement("SELECT tracks.name AS track_Name, tracks.duration AS track_Duration, tracks.explicit AS track_Explicitly FROM tracks JOIN  WHERE tracks.explicit = ? AND tracks.explicit IS NOT NULL ORDER BY tracks.duration ASC LIMIT ?");
                statement.setBoolean(1, explicity);
                statement.setInt(2, limit);
                ResultSet tracks_explicityResult = statement.executeQuery();

                while (tracks_explicityResult.next()) {
                    System.out.println(tracks_explicityResult.getString("track_Name") + " - " + tracks_explicityResult.getInt("track_Duration") + " Seconds - " + "Explicit: " + tracks_explicityResult.getBoolean("track_Explicitly"));
                }

            } else {
                statement = DBconnection.prepareStatement("SELECT tracks.name AS track_Name, tracks.duration AS track_Duration, tracks.explicit AS track_Explicitly FROM tracks WHERE tracks.explicit = ? AND tracks.explicit IS NOT NULL ORDER BY tracks.duration DESC LIMIT ?");
                statement.setBoolean(1, explicity);
                statement.setInt(2, limit);
                ResultSet tracks_explicityResult = statement.executeQuery();

                while (tracks_explicityResult.next()) {
                    System.out.println(tracks_explicityResult.getString("track_Name") + " - " + tracks_explicityResult.getInt("track_Duration") + " Seconds - " + "Explicit: " + tracks_explicityResult.getBoolean("track_Explicitly"));
                }

            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error showing track: " + e.getMessage());
            return false;
        }
    }

    public void cleanDatabase() {
        try {
            //Delete all tables in database
            Statement statement = DBconnection.createStatement();
            statement.executeUpdate("DROP TABLE tracks_artist CASCADE;");
            statement.executeUpdate("DROP TABLE tracks CASCADE;");
            statement.executeUpdate("DROP TABLE album CASCADE;");
            statement.executeUpdate("DROP TABLE artist CASCADE;");
            statement.executeUpdate("DROP TABLE genre CASCADE;");

            System.out.println("Database cleaned successfully.");
            System.out.println("\n(Start the program again to fill database with initial data)\n");
        }catch(SQLException e) {
            System.out.println("Error cleaning the database: " + e.getMessage());
        }
    }

    //Helper methods

    //Delete rowToBeDeleted from tableName. Returns true if deletion was completed, otherwise false.
    private boolean deleteRow(String tableName, int rowToBeDeleted) {
        try {
            //Delete item from desired table
            PreparedStatement statement = DBconnection.prepareStatement("DELETE CASCADE FROM " + tableName + " WHERE id = ?");
            statement.setInt(1, rowToBeDeleted);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                //Deletion completed successfully
                return true;
            }
        } catch (SQLException e) {
            System.out.println("\nError deleting from database:" + e.getMessage());
        }
        //Deletion wasn't completed
        return false;

    }

    //Check if an album exists on database, otherwise create a new album (and artist if necessary),and add it to album table. Returns artist ID and album ID in a List ordered respectively
    private List<Integer> checkAlbum(String albumName) {
        List<Integer> result = new ArrayList<>();
        try {
            PreparedStatement checkAlbumStatement = DBconnection.prepareStatement("SELECT id, artist_id FROM album WHERE (LOWER(name)) = (LOWER(?))");
            checkAlbumStatement.setString(1, albumName);
            ResultSet albumResult = checkAlbumStatement.executeQuery();

            if (albumResult.next()) {
                // If album already exists, get its id and get artistId
                result.add(albumResult.getInt("id")); //Adding album ID to list
                result.add(albumResult.getInt("artist_id")); //Adding artist ID to list
            } else {
                //If album does not exist, insert it into the database
                String artistName = askForArtist(); //Album can not be created without artist. Asking for new artist name
                int artistId = addArtist(artistName); //get artistId and add artist to Database
                int albumId = addAlbum(albumName, artistName); //Adding album to DB and get its ID
                result.add(albumId);
                result.add(artistId);
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }

        return result;
    }

    //Check if a genre exists on database, otherwise create a new genre and add it to genre table. Returns genreName ID
    private int checkGenre(String genreName) {
        int genreId = -1;
        try {
            PreparedStatement checkGenreStatement = DBconnection.prepareStatement("SELECT id FROM genre WHERE (LOWER(name)) = (LOWER(?))");
            checkGenreStatement.setString(1, genreName);
            ResultSet genreResult = checkGenreStatement.executeQuery();

            if (genreResult.next()) {
                //If genre already exists, get its id
                genreId = genreResult.getInt("id");
            } else {
                //If genre does not exist, insert it into the database and get its ID
                genreId = addGenre(genreName);
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        return genreId;
    }

    //Check if an artist exists on database, otherwise create a new artist and add it to artist table. Returns artistName ID (POSSIBLE REDUNDANT METHOD, IMPROVEMENT NEEDED)
    private int checkArtist(String artistName) {
        int artistId = -1;
        try {
            PreparedStatement checkArtistStatement = DBconnection.prepareStatement("SELECT id FROM artist WHERE (LOWER(name)) = (LOWER(?))");
            checkArtistStatement.setString(1, artistName);
            ResultSet artistResult = checkArtistStatement.executeQuery();

            if (artistResult.next()) {
                // If artist already exists, get its id
                artistId = artistResult.getInt("id");
            } else {
                // If artist does not exist, insert it into the database
                artistId = addArtist(artistName);
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        return artistId;
    }

    //Check id for logicalKey parameter in table called tableName. Return ID of specified name (Also used in Menu class)
    public int checkId(String tableName, String logicalKey) {
        int resultId = -1;
        try {
            PreparedStatement checkStatement = DBconnection.prepareStatement("SELECT id FROM " + tableName + " WHERE (LOWER(name)) = (LOWER(?))");
            checkStatement.setString(1, logicalKey);
            ResultSet checkResult = checkStatement.executeQuery();

            if (checkResult.next()) {
                resultId = checkResult.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving information from Database: " + e.getMessage());
        }
        return resultId;
    }

    //Add artist.id and track.id to tracks_artist table. Return true if insertion was completed, otherwise false
    private boolean addArtist_Track(int trackId, int artistId) {
        try {
            PreparedStatement statement = DBconnection.prepareStatement("INSERT INTO tracks_artist (artist_id, track_id) VALUES (?,?) ON CONFLICT DO NOTHING");
            statement.setInt(1, artistId);
            statement.setInt(2, trackId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }

        return false; //Inserting failed

    }

    //Ask for an artist name until user gives a valid input
    private String askForArtist() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String artistInput = "";

        try {
            while (artistInput.trim().isEmpty() || !artistInput.matches("[a-zA-Z ]+")) {
                System.out.println("Enter a valid artist name");
                artistInput = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
        //We don't close the reader since it may generate issues with BufferedReader in Menu class.
        return artistInput;
    }


    //Show all tracks associated with a trackName
    public boolean showTracksInfo(String trackName){
        List<Integer> listIds = checkTracks(trackName);
        if(listIds.size() > 0) {
            for (int id : listIds) {
                showSingleTrack(id);
                System.out.println();
            }
            //At least one track was found
            return true;
        }
        //No tracks found
        System.out.println("No tracks found with given input\n");
        return false;
    }

    //Check if trackName exists in tracks table and return all IDs associated. Return a list of IDs
    private List<Integer> checkTracks(String trackName) {
        List<Integer> listIds = new ArrayList<>(); //List of all IDs associated with this track name
        try {
            PreparedStatement checkArtistStatement = DBconnection.prepareStatement("SELECT id FROM tracks WHERE LOWER(name) = LOWER(?)");
            checkArtistStatement.setString(1, trackName);
            ResultSet resultId = checkArtistStatement.executeQuery();
            if (resultId.next()) {
                do {
                    // If track name exists, get all IDs that have that logical key and add it to list
                    listIds.add(resultId.getInt("id"));
                } while (resultId.next());
            }

        } catch (SQLException e) {
            System.out.println("Error connecting to database:" + e.getMessage());
        }
        //If there is no record with trackName, an empty list will be returned
        return listIds;
    }
}










