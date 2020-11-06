import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

public class dataUpdater {
    String username;
    String passwd;
    String conStr;
    Connection connection;
    HashMap<String, String> stars;
    HashMap<String, List<String>> starInMovie;
    HashMap<String, List<String>> genreInMovie;
    HashMap<String, List<String>> dir;

    public dataUpdater(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
        this.conStr = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
    }

    public void updateGenre() throws Exception{
        Statement stmt;
        stmt = connection.createStatement();
        stmt.executeUpdate("insert into genres\n" +
                "values (24, 'Porn'),\n" +
                "       (25, 'TV series'),\n" +
                "       (26, 'Noir'),\n" +
                "       (27, 'TV miniseries'),\n" +
                "       (28, 'Romance Comedy'),\n" +
                "       (29, 'Epic'),\n" +
                "       (30, 'Cartoon'),\n" +
                "       (31, 'History');\n");
    }

    public void loadData(String filename, String tableName) throws Exception{
        Statement stmt;
        String query =  "LOAD DATA LOCAL INFILE '" + filename + "'\n" +
                "INTO TABLE " + tableName + "\n" +
                "FIELDS TERMINATED BY '|'  \n" +
                "LINES TERMINATED BY '\\n';";
        stmt = connection.createStatement();
        stmt.executeUpdate(query);
    }

    public void update() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        this.connection = DriverManager.getConnection(this.conStr, this.username, this.passwd);
        if (this.connection != null){
            updateGenre();
            loadData("csvs/movie.csv", "movies");
            loadData("csvs/gim.csv", "genres_in_movies");
            loadData("csvs/star.csv", "stars");
            loadData("csvs/sim.csv", "stars_in_movies");
            this.connection.close();
        }

    }
}
