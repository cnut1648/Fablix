import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class dataQuery {
    String username;
    String passwd;
    String conStr;
    Connection connection;
    int starMax;
    int movieMax;
    HashMap<String, List<Integer>> stars;
//    HashMap<String, List<String>> starInMovie;
//    HashMap<String, List<String>> genreInMovie;
    HashMap<String, List<String>> dir;

    public dataQuery(String username, String passwd){
        this.username = username;
        this.passwd = passwd;
        this.conStr = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
    }

    public void init() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        this.connection = DriverManager.getConnection(this.conStr, this.username, this.passwd);
        if (this.connection != null){
            getMovieMax();
            getStar();
//            getStarInMovie();
//            getGenreInMovie();
            getDirector();
            this.connection.close();
        }
    }

    public void getMovieMax() throws  Exception{
        Statement stmt;
        ResultSet rs;
        stmt = connection.createStatement();
        rs = stmt.executeQuery("select max(substr(id,3)) as i from movies;");
        while(rs.next()){
            this.movieMax = rs.getInt("i");
        }
    }

    public void getStar() throws Exception {
        Statement stmt;
        ResultSet rs;
        stmt = connection.createStatement();
        rs = stmt.executeQuery("select max(substr(id,3)) as i from stars;");
        this.stars = new HashMap<>();

        while(rs.next()){
            this.starMax = rs.getInt("i");
        }
        rs = stmt.executeQuery("select name, group_concat(case\n" +
                "   when birthYear is null then 0\n" +
                "    else birthYear end\n" +
                "    ) b\n" +
                "from stars\n" +
                "group by name;");
        while(rs.next()){
            String bstr = rs.getString("b");
            String starname = rs.getString("name");
            List<Integer> bs = new ArrayList<>();
            for (String dobstr: bstr.split(",")){
                bs.add(Integer.parseInt(dobstr));
            }
            this.stars.put(starname, bs);
        }

        rs.close();
        stmt.close();
    }

//    public void getStarInMovie() throws Exception {
//        Statement stmt;
//        ResultSet rs;
//        stmt = connection.createStatement();
//        this.starInMovie = new HashMap<>();
//
//        rs = stmt.executeQuery("select name, group_concat(case\n" +
//                "   when birthYear is null then 0\n" +
//                "    else birthYear end\n" +
//                "    ) b\n" +
//                "from stars\n" +
//                "group by name;");
//
//        while(rs.next()){
//            String stars = rs.getString("starId");
//            List<String> s = Arrays.asList(stars.split(","));
//            this.starInMovie.put(rs.getString("movieId"), s);
//        }
//
//        rs.close();
//        stmt.close();
//    }

//    public void getGenreInMovie() throws Exception {
//        Statement stmt;
//        ResultSet rs;
//        stmt = connection.createStatement();
//        this.genreInMovie = new HashMap<>();
//
//        rs = stmt.executeQuery("select movieId, group_concat(genreId) as gid\n" +
//                "from genres_in_movies\n" +
//                "group by movieId;");
//
//        while(rs.next()){
//            String gs = rs.getString("gid");
//            List<String> g = Arrays.asList(gs.split(","));
//            this.genreInMovie.put(rs.getString("movieId"), g);
//        }
//
//        rs.close();
//        stmt.close();
//    }

    public void getDirector() throws Exception{
        Statement stmt;
        ResultSet rs;
        stmt = connection.createStatement();
        this.dir = new HashMap<>();

        rs = stmt.executeQuery("select director, group_concat(title) as title\n" +
                "from movies\n" +
                "group by director");
        while(rs.next()){
            String gs = rs.getString("title");
            List<String> g = Arrays.asList(gs.split(","));
            this.dir.put(rs.getString("director"), g);
        }
        rs.close();
        stmt.close();
    }
}
