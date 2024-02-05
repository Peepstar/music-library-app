package MusicLibrary;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.List;

public class Menu {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        //If tables are created and initial data has been populated we can continue with menu
        if (PopulateData.populateInitialData()) {
            int choice = -1;
            DatabaseManage crudDB = new DatabaseManage();

            while (choice != 0) {
                showMainMenu();
                choice = askForValidNumber();
                switch (choice) {
                    case 1:
                        showAddMenu();
                        choice = askForValidNumber();
                        addToDatabase(choice, crudDB); //Depending on choice, a different method will be executed to add data into the database
                        break;

                    case 2:
                        showDeleteMenu();
                        choice = askForValidNumber();
                        deleteFromDataBase(choice, crudDB); //Depending on choice, a different method will be executed to delete data from the database
                        break;

                    case 3:
                        showUpdateMenu();
                        choice = askForValidNumber();
                        updateDatabase(choice, crudDB);
                        break;

                    case 4:
                        showInformation();
                        choice = askForValidNumber();
                        showInformationFromDataBase(choice, crudDB);
                        break;

                    case 5:
                        System.out.println("\nDo you want to completely delete the database and all it's associated data?\n");
                        boolean deleteChoice = askForYesOrNo();
                        if(deleteChoice){
                            crudDB.cleanDatabase();
                        }else{
                            System.out.println("\nNo changed made");
                        }
                        break;

                    case 0:
                        System.out.println("\nThank you for using the Music Library app");
                        System.out.println("\n--EXIT--\n");
                        break;

                }

            }
            //Closing reader to release system resources.
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Error closing reader: " + e.getMessage());
                }
        //In case that database is not populated we inform the user and don't make any change.
        }else{
            System.out.println("There was an issue populating database");
        }
    }

    //Show main menu and menus for each option
    private static void showMainMenu() {
        System.out.println("\n----MUSIC LIBRARY----\n");
        System.out.println("\n1- Agregar");
        System.out.println("2- Borrar");
        System.out.println("3- Actualizar");
        System.out.println("4- Consultar");
        System.out.println("5- Clean all  database");
        System.out.println("0- Exit");
        System.out.println("\nIngrese una opcion\n");

    }

    private static void showAddMenu() {
        System.out.println("\n1- Agregar un genero");
        System.out.println("2- Agregar un artista");
        System.out.println("3- Agregar un album");
        System.out.println("4- Agregar una canción");
        System.out.println("5- Agregar una canción con duración, popularidad y explicit");
        System.out.println("\nIngrese una opcion\n");
    }

    private static void showDeleteMenu() {
        System.out.println("\n1- Delete a genre");
        System.out.println("2- Delete an artist");
        System.out.println("3- Delete an album");
        System.out.println("4- Delete a track");
        System.out.println("\nChoose an option\n");
    }

    private static void showUpdateMenu() {
        System.out.println("\n1- Update a genre");
        System.out.println("2- Update an artist");
        System.out.println("3- Update an album");
        System.out.println("4- Update a track");
        System.out.println("\nChoose an option\n");
    }

    private static void showInformation() {
        System.out.println("\n1- Show genre info");
        System.out.println("2- Show artist info");
        System.out.println("3- Show album info");
        System.out.println("4- Show track info by name");
        System.out.println("5- Show track info by ID");
        System.out.println("6- Show all tracks and albums of an artist");
        System.out.println("7- Show songs by genre");
        System.out.println("8- Show songs by popularity");
        System.out.println("9- Show songs by duration");
        System.out.println("10- Show songs by explicity");
    }

    //Depending on user choice, we are going to use different add methods from DataBaseManage class.
    private static void addToDatabase(int option, DatabaseManage db) {
        int insertionId; //Id returned by insertion on database
        String input = ""; //input by user
        String albumInput = ""; //In case extra required input is needed from user
        String genreInput = ""; //In case extra required input is needed from user

        switch (option) {
            case 1: //Adding a genre to genre table
                System.out.println("Enter genre name: \n");
                input = askForValidName();

                insertionId = db.addGenre(input);
                System.out.println("ID: " + insertionId);
                if (insertionId == -1) {
                    System.out.println("\nInsertion failed. Please verify that genre name is correct and that genre doesn't exist in database\n");
                } else {
                    System.out.println("\nInserted " + input + " into database\n");
                    db.showGenreInfo(insertionId);
                }
                break;

            case 2: //Adding an artist to artist table
                System.out.println("Enter artist name: \n");
                input = askForValidName();

                insertionId = db.addArtist(input);
                if (insertionId == -1) {
                    System.out.println("\nInsertion failed. Please verify that artist name is correct and that artist doesn't exist in database\n");
                } else {
                    System.out.println("\nInserted " + input + " into database\n");
                    db.showArtistInfo(insertionId);
                }
                break;

            case 3: //Adding an album to album table
                System.out.println("Enter album name: \n");
                input = askForValidName();

                String artistInput = ""; //Artist name that is mandatory to add an album
                System.out.println("Enter artist name: \n");
                artistInput = askForValidName();

                insertionId = db.addAlbum(input, artistInput);
                if (insertionId == -1) {
                    System.out.println("\nInsertion failed Please verify that album name is correct and that album doesn't exist in database\n");
                } else {
                    System.out.println("\nInserted " + input + " into database\n");
                    db.showAlbumInfo(insertionId);
                }
                break;

            case 4: //Adding a track to tracks table
                System.out.println("Enter track name: \n");
                input = askForValidName();

                System.out.println("Enter album name: \n");
                albumInput = askForValidName();

                System.out.println("Enter genre name: \n");
                genreInput = askForValidName();

                insertionId = db.addTrack(input, albumInput, genreInput);
                if (insertionId != -1) {
                    System.out.println("\nInserted " + input + " into database\n");
                    db.showSingleTrack(insertionId);
                } else {
                    System.out.println("\nInsertion failed Please verify that information is correct and that track doesn't exist in database\n");
                }
                break;

            case 5: //Adding a track to tracks table with extra data
                Integer popularity = null;
                Integer duration;
                Boolean explicit;
                System.out.println("Track, genre and album name must be correct to continue!\n");

                System.out.println("Enter track name: \n");
                input = askForValidName();

                System.out.println("Enter album name: \n");
                albumInput = askForValidName();

                System.out.println("Enter genre name: \n");
                genreInput = askForValidName();

                System.out.println("Enter popularity (0-100): \n");
                //Asking for popularity and if it is a valid number, assign it to popularity
                Integer tempInput = askForValidNumber();
                if (tempInput > 0 && tempInput <= 100) {
                    popularity = tempInput;
                } else {
                    System.out.println("\nNo valid number was entered\n");
                }
                //Asking for duration
                System.out.println("Enter song duration (in seconds): \n");
                duration = askForValidNumber();
                if (duration == -1) { //If duration is null means that the user didn't enter a valid number
                    System.out.println("\nNo valid number was entered\n");
                    duration = null;
                }
                //Asking for explicity
                System.out.println("Is the song explicit?\n");
                explicit = askForYesOrNo();

                insertionId = db.addTrack(input, albumInput, genreInput, popularity, duration, explicit);
                if(insertionId != -1){
                    System.out.println("\nInserted " + input + " into database\n");
                    db.showSingleTrack(insertionId);
                } else {
                    System.out.println("\nInsertion failed Please verify that information is correct and that track doesn't exist in database\n");
                }
                break;

            default:
                System.out.println("\nInvalid option\n");
        }
    }

    //Depending on user choice, we are going to use different delete methods from DatabaseManage class
    private static void deleteFromDataBase(int option, DatabaseManage db){
        String input = ""; //input by user
        Boolean deleteChoice;

        switch(option){
            case 1:
                System.out.println("Enter genre name to be deleted: \n");
                input = askForValidName(); //Ask for genre name
                //Get genre ID for user's input. Using genre as tableName parameter to check information in right table
                int genreId = db.checkId("genre", input);
                //Check if genre exist, if so, show information for id
                if(db.showGenreInfo(genreId)) {
                    System.out.println("\nDo you want to delete this genre?");
                    deleteChoice = askForYesOrNo();

                    if (deleteChoice) {
                        if (db.deleteGenre(genreId)) {
                            System.out.println(input + " was deleted from database and all related tracks");
                        } else {
                            System.out.println("Error deleting genre");
                        }
                    }
                    if(!deleteChoice){
                        System.out.println(input + " was not deleted from database");
                    }
                }

                break;

            case 2:
                System.out.println("Enter artist name to be deleted: \n");
                input = askForValidName(); //Ask for artist name
                //Get artist ID for user's input. Using artist as tableName parameter to check information in right table
                int artistId = db.checkId("artist", input);
                //Check if artist exist, if so, show information for id
                if(db.showArtistInfo(artistId)) {
                    System.out.println("\nDo you want to delete this artist?");
                    deleteChoice = askForYesOrNo();

                    if (deleteChoice) {
                        if (db.deleteArtist(artistId)) {
                            System.out.println(input + " was deleted from database and all related tracks");
                        } else {
                            System.out.println("Error deleting artist");
                        }
                    }
                    if(!deleteChoice){
                        System.out.println(input + " was not deleted from database");
                    }
                }

                break;

            case 3:
                System.out.println("Enter album name to be deleted: \n");
                input = askForValidName(); //Ask for album name
                //Get album ID for user's input. Using album as tableName parameter to check information in right table
                int albumId = db.checkId("album", input);
                //Check if album ID exists, if so, show information for id
                if(db.showAlbumInfo(albumId)) {
                    System.out.println("\nDo you want to delete this album: \n");
                    deleteChoice = askForYesOrNo();

                    if (deleteChoice) {
                        if (db.deleteAlbum(albumId)) {
                            System.out.println(input + " was deleted from database and all related tracks\n");
                        } else {
                            System.out.println("Error deleting album\n");
                        }
                    }
                    if (!deleteChoice) {
                        System.out.println(input + " was not deleted from database\n");
                    }
                }

                break;

            case 4:
                System.out.println("Enter track name to be deleted\n");
                input = askForValidName();
                //Get track ID for user's input. Using tracks as tableName parameter to check information in right table
                if(db.showTracksInfo(input)) {
                    System.out.println("\nEnter ID for the track that you want to delete");
                    int idToDelete = askForValidNumber();
                    if(db.showSingleTrack(idToDelete)) {
                        System.out.println("\nDo you want to delete this track?\n");
                        deleteChoice = askForYesOrNo();
                        if (deleteChoice) {
                            if (db.deleteTrack(idToDelete)) {
                                System.out.println(input + " was deleted from database\n");
                            } else {
                                System.out.println("Error deleting track\n");
                            }
                        }
                        if (!deleteChoice) {
                            System.out.println(input + " was not deleted from database\n");
                        }
                    }
                }

                break;

            default:
                System.out.println("Invalid option\n");
        }
    }
    //Depending on user choice, we are going to use different update methods from DatabaseManage class
    private static void updateDatabase(int option, DatabaseManage db) {
        String input = ""; //input by user
        Boolean updateChoice;

        switch (option) {
            //Update genre
            case 1:
                System.out.println("Enter genre name to be updated: \n");
                input = askForValidName(); //Ask for genre name
                //Get genre ID for user's input. Using genre as tableName parameter to check information in right table
                int genreId = db.checkId("genre", input);
                //Check if genre exist, if so, show information for given id
                if (db.showGenreInfo(genreId)) {
                    System.out.println("\nDo you want to update this genre?");
                    updateChoice = askForYesOrNo();
                    if (updateChoice) {
                        System.out.println("Type new genre name: \n");
                        input = askForValidName();
                        //Updating genre name
                        if (db.updateGenre(genreId, input)) {
                            System.out.println("\nGenre ID " + genreId + " was updated");
                        } else {
                            System.out.println("\nError updating genre\n");
                        }
                    }
                    if (!updateChoice) {
                        System.out.println(input + " was not updated\n");
                    }
                }

                break;

            //Update artist
            case 2:
                System.out.println("Enter artist name to be updated: \n");
                input = askForValidName(); //Ask for artist name
                //Get artist ID for user's input. Using artist as tableName parameter to check information in right table
                int artistId = db.checkId("artist", input);
                //Check if artist exists, if so, show information for given id
                if (db.showArtistInfo(artistId)) {
                    System.out.println("\nDo you want to update this artist?");
                    updateChoice = askForYesOrNo();
                    if (updateChoice) {
                        System.out.println("\nType new artist name: \n");
                        input = askForValidName();
                        //Updating artist name
                        if (db.updateArtist(artistId, input)) {
                            System.out.println("Artist ID " + artistId + " was updated");
                            db.showArtistInfo(artistId);
                        }else{
                            System.out.println("\nError updating artist\n");
                        }
                    }
                    if (!updateChoice) {
                        System.out.println(input + " was not updated");
                    }
                }

                break;

            //Update album
            case 3:
                System.out.println("Enter album name to be updated: \n");
                input = askForValidName();
                //Get album ID for user's input. Using album as tableName parameter to check information in right table
                int albumId = db.checkId("album", input);
                //Check if album exists, if so, show information for given id
                if (db.showAlbumInfo(albumId)) {
                    System.out.println("\nDo you want to update this album?");
                    updateChoice = askForYesOrNo();
                    //If user type yes, proceed to update desired information
                    if (updateChoice) {
                        System.out.println("What field do you want to update?\n");
                        System.out.println("1- Album name\n2- Artist name");
                        int choice = askForValidNumber();
                        //Updating album name
                        if (choice == 1) {
                            System.out.println("Enter new album name: ");
                            input = askForValidName();
                            //Updating album name
                            if (db.updateAlbum(albumId, input)) {
                                System.out.println("\nAlbum ID " + albumId + " was updated\n");
                            } else {
                                System.out.println("\nError updating album name\n");
                            }
                        }
                        //Updating artist name
                        if (choice == 2) {
                            //Getting new artist name
                            System.out.println("Enter new artist name: \n");
                            input = askForValidName();
                            //Getting artistId for new artist associated to this album.
                            int artist_id = db.checkId("artist", input);
                            //Updating artist name and associate new artistID to album that is being updated. -1 means that new artist was not found
                            if (artist_id != -1){
                                db.updateArtistIdOnAlbum(albumId, artist_id);
                                System.out.println("\nUpdated successfully!\n");
                            } else {
                                System.out.println("\nError updating artist name\n");
                            }
                        } else {
                            System.out.println("\nInvalid option\n");
                        }
                    //User didn't want to update this album
                    } else {
                        System.out.println(input + " was not updated\n");
                    }
                }
                break;

            //Update track
            case 4:
                System.out.println("Enter track name to be updated: \n");
                input = askForValidName();
                //Check if track name exists, if so, show all tracks associated to that name
                if (db.showTracksInfo(input)) {
                    //Ask for the ID that user wants to update
                    System.out.println("\nEnter the ID for the track you want to update: \n");
                    int idToUpdate = askForValidNumber();
                    //If ID exists, show it and ask if this is the one that needs to be updated
                    if (db.showSingleTrack(idToUpdate)) {
                        System.out.println("\nDo you want to update this track?\n");
                        updateChoice = askForYesOrNo();
                        //If user type yes, proceed to update desired information
                        if (updateChoice) {
                            System.out.println("What field do you want to update?\n");
                            //Asking what is the field that has to be updated in track
                            System.out.println("\n1- Track name\n2- Album name\n3- Artist name\n4- Genre\n5- Duration\n6- Popularity\n7- Explicit");
                            int choice = askForValidNumber();
                            //Use helper method to ask for user input and update track in database
                            updateTrack(choice, idToUpdate, db);
                        }
                    }else{
                        System.out.println("\ntrack ID doesn't exist\n");
                    }
                }else{
                    System.out.println("\nNo tracks found with that name\n");
                }
                break;
            }
        }

    //Update a field on a track depending on user's choice
    //Helper method for updateDataBase
    private static boolean updateTrack(int choice, int trackId, DatabaseManage db){
        String input;

            switch (choice) {
                //Updating track name. Asking for new track name and then using DB manage class to update it.
                case 1:
                    System.out.println("Enter new track name: \n");
                    input = askForValidName();
                    //Updating trackName, if update was completed successfully, return true
                    return db.updateTrackName(trackId, input);

                //Updating album name. Getting album ID, then asking for new album name and then using DB manage class to update it.
                case 2:
                    //Getting new album name
                    System.out.println("Enter new album name: \n");
                    input = askForValidName();
                    //Getting albumId for new album associated to this track using album as tableName
                    int albumId = db.checkId("album", input);
                    //If albumId is not -1 it means that exists on database so we can update it on track
                    if (albumId != -1){
                        //Return true if update was completed, otherwise false
                        return db.updateAlbumIdOnTrack(trackId, albumId);
                    } else {
                        //new albumID doesn't exist so we return false
                        System.out.println("\nAlbum ID was not found\n");
                        return false;
                    }

                case 3:
                    //Getting new artist name
                    System.out.println("Enter new artist name: \n");
                    input = askForValidName();
                    //Getting artistId for new artist associated to this track using artist as tableName
                    int artistId = db.checkId("artist", input);
                    //If artistID is not -1 it means that exists on database so we can update it on track
                    if(artistId != -1){
                        //Return true if update was completed, otherwise false
                        return db.updateArtistIdOnTrack(trackId, artistId);
                    }else{
                        //new ArtistID doesn't exist so we return false
                        System.out.println("\nArtist ID was not found\n");
                        return false;
                    }

                case 4:
                    //Getting genre name
                    System.out.println("Enter new genre name: \n");
                    input = askForValidName();
                    //Getting genreId for new genre associated to this track using genre as tableName
                    int genreId = db.checkId("genre", input);
                    //If genreId is not -1 it means that exists on database so we can update it on track
                    if(genreId != -1){
                        //Return true if update was completed, otherwise false
                        return db.updateGenreIdOnTrack(trackId, genreId);
                    }else{
                        //new genreId doesn't exist so we return false
                        System.out.println("\nGenre ID was not found\n");
                        return false;
                    }

                case 5:
                    //Getting new duration
                    System.out.println("Enter new duration: \n");
                    int duration = askForValidNumber();
                    //If update was completed, return true otherwise false
                    if(db.updateDuration(trackId, duration)) {
                        System.out.println("\nDuration updated!\n");
                        return true;
                    }else{
                        System.out.println("\nUpdate failed\n");
                        return false;
                    }

                case 6:
                    //Getting new popularity
                    System.out.println("Enter new popularity: \n");
                    int popularity = askForValidNumber();
                    //If update was completed, return true otherwise false
                    if(db.updatePopularity(trackId, popularity)) {
                        System.out.println("\nPopularity updated!\n");
                        return true;
                    }else{
                        System.out.println("\nUpdate failed\n");
                        return false;
                    }

                case 7:
                    //Getting explicity
                    System.out.println("\nIs the song explicit?\n");
                    boolean explicity = askForYesOrNo();
                    //If update was completed, return true otherwise false
                    if(db.updateExplicity(trackId, explicity)) {
                        System.out.println("\nExplcity updated!\n");
                        return true;
                    }else{
                        System.out.println("\nUpdate failed\n");
                        return false;
                    }

                default:
                    System.out.println("\nInvalid option\n");
            }
        //No update
        return false;
    }

    //Depending on user choice, we are going to use different methods to show information from DatabaseManage class
    private static void showInformationFromDataBase(int option, DatabaseManage db){
        int order;
        String input = ""; //input by user
        Boolean showChoice;
        int tracksLimit;

        switch(option){

            case 1:
                System.out.println("Enter genre name to be shown");
                input = askForValidName();
                //Get genre ID for user's input. Using genre as tableName parameter to check information in right table
                int genreId = db.checkId("genre", input);
                //Show information for given id
                db.showGenreInfo(genreId);
                break;

            case 2:
                System.out.println("Enter artist name to be shown");
                input = askForValidName();
                //Get artist ID for user's input. Using artist as tableName parameter to check information in right table
                int artistId = db.checkId("artist", input);
                //Show information for given id
                db.showArtistInfo(artistId);
                break;

            case 3:
                System.out.println("Enter album name to be shown");
                input = askForValidName();
                //Get album ID for user's input. Using album as tableName parameter to check information in right table
                int albumId = db.checkId("album", input);
                //Show information for given id
                db.showAlbumInfo(albumId);
                break;

            case 4:
                System.out.println("Enter track name to be shown");
                input = askForValidName();
                //Check if track name exists, if so, show all tracks associated to that name
                db.showTracksInfo(input);
                break;

            case 5:
                System.out.println("Enter track ID to be shown");
                int trackId = askForValidNumber();
                //Check if ID exist, if so, show track associated to that ID
                db.showSingleTrack(trackId);
                break;

            case 6:
                System.out.println("Enter artist name to show all albums and tracks associated");
                input = askForValidName();
                //Get artist ID for user's input. Using artist as tableName parameter to check information in right table
                int artist = db.checkId("artist", input);
                db.showTracksAlbums_Artist(artist);
                break;

            case 7:
                System.out.println("Enter genre name to show all tracks associated");
                input = askForValidName();
                //Get genre ID for user's input. Using genre table as tableName parameter to check informmation in right table
                int genre = db.checkId("genre", input);
                db.showTracksGenre(genre);
                break;

            case 8:
                System.out.println("Show songs in:\n1-ASCENDING ORDER\n2-DESCENDING ORDER");
                order = askForValidNumber();
                System.out.println("How many songs do you want to see?");
                tracksLimit = askForValidNumber();
                //Depending on user choice, show tracks ordered by popularity either in ascending or descending order.
                db.showTracksByPopularity(order, tracksLimit);
                break;

            case 9:
                System.out.println("Show songs in:\n1-ASCENDING ORDER\n2-DESCENDING ORDER");
                order = askForValidNumber();
                System.out.println("How many songs do you want to see?");
                tracksLimit = askForValidNumber();
                //Depending on user choice, show tracks ordered by duration either in ascending or descending order.
                db.showTracksByDuration(order, tracksLimit);
                break;

            case 10:
                System.out.println("Show explicit songs?\n");
                boolean explicit = askForYesOrNo();
                System.out.println("Show songs in:\n1-ASCENDING ORDER\n2-DESCENDING ORDER");
                order = askForValidNumber();
                System.out.println("How many songs do you want to see?");
                tracksLimit = askForValidNumber();
                //Depending on user choice, show tracks ordered by explicity either in ascending or descending order.
                db.showTracksByExplicity(order, tracksLimit, explicit);
                break;

            default:
                System.out.println("\nInvalid option\n");
        }
    }

    //Ask for a valid string to be inserted into database
    private static String askForValidName() {
        String result = "";
        try {
            reader.mark(0); // mark the current position
            while (result.trim().isEmpty() || !result.matches("[a-zA-Z ]+")) {
                result = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("\nError reading data for valid name:" + e.getMessage());
        //Closing reader to avoid any issue on next readings
        }
        return result;
    }
    //Asking for a valid number to be inserted into database
    private static int askForValidNumber() {
        int result = -1;
        try {
            reader.mark(0); // mark the current position
            String temp = reader.readLine();
            if (temp.matches("[0-9]+")) {
                result = Integer.parseInt(temp);
            }
        } catch (IOException e) {
            System.out.println("\nError reading data for valid number: " + e.getMessage());
        }
        return result;
    }
    //Asking if a song is explicit
    private static boolean askForYesOrNo() {
        String temp = "";
        System.out.println("Type YES or NO\n");
        try {
            reader.mark(0); // mark the current position
            temp = reader.readLine();
            if(temp.equalsIgnoreCase("yes")){
                return true;
            }else if (temp.equalsIgnoreCase("no")){
                return false;
            }else{
                System.out.println("\nInvalid option");
                return false;
            }
        } catch (IOException e) {
            System.out.println("\nError reading data for yes or no:" + e.getMessage());
            return false;
        }
    }

}

