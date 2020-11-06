import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieList", urlPatterns = "/api/movie", loadOnStartup = 1)
public class MovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;

    LinkedHashMap<String, Movie> movies;
    private final Object lock = new Object();
    List<Long> TJs;
    long TS_start = 0;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private class Movie{
        public String id = null;
        public String title = null;
        public String year = null;
        public String director = null;
        public String genre_list = null;
        public String sid_list = null;
        public String sname_list = null;
        public String rating = null;

        Movie(String id, String title, String year, String director, String genre_list, String rating){
            this.id= id;
            this.title = title;
            this.year = year;
            this.director = director;
            this.genre_list = genre_list;
            this.rating = rating;
        }

        Movie(String sid_list, String sname_list) {
            this.sid_list = sid_list;
            this.sname_list = sname_list;
        }

        boolean ready(){
            return sid_list != null;
        }

        @Override
        public String toString() {
            return this.id + this.title;
        }

        public boolean starReady() {
            return this.title != null;
        }
    }

    private String queryAppend(String N, String page, String first, String second){
        String query = "";
        if (first != null && !first.isEmpty() && second != null && !second.isEmpty()) {
            String order1 = first.substring(first.length() - 1);
            String order2 = second.substring(second.length() - 1);
            first = first.substring(0, first.length()-1);
            second = second.substring(0, second.length()-1);
            query += "order by " + first + (order1.equals("0")?" asc ,": " desc ,");
            query += second + (order2.equals("0")?" asc ": " desc\n");
        } else {
            query += "order by title\n";
        }
        if (N != null && !N.isEmpty()) {
            int n = Integer.parseInt(N);
            query += "limit " + (n + 1) + "\n";
            if (page !=null && !page.isEmpty()){
                int p = Integer.parseInt(page);
                query += "offset " + ((p-1) * n) + "\n";
            }
        } else {
            // default N = 25
            query += "limit 26\n";
        }
        return query;
    };


    // to get movie data but no star info
    private void getMovieData(PreparedStatement stmt) {
        try{
            long start = System.nanoTime();
            ResultSet rs = stmt.executeQuery();
            synchronized (lock){
                TJs.add(System.nanoTime() - start);
            }

            while (rs.next()) {
                Movie m = new Movie(rs.getString("id"), rs.getString("title"),
                        rs.getString("year"), rs.getString("director"), rs.getString("genre_list"),
                        rs.getString("rating"));
                movies.put(rs.getString("id"), m);
            }

            rs.close();
            stmt.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // if movies data already init, then add star info
    private void getStarInfo(Connection dbcon){
        // find all stars
        String query="select movieId,\n" +
                "       group_concat(distinct sid order by ct desc) sid,\n" +
                "       group_concat(distinct name order by ct desc) name\n" +
                "from superstar\n" +
                "where movieId in (";

        for (Map.Entry<String, Movie>kv: movies.entrySet()){
            query += "'" + kv.getKey() + "',";
        }
        query = query.substring(0, query.length()-1);
        query+=")\ngroup by movieId;";

//        System.out.println("getStarInfo :::::::::::");
//        System.out.println(query);
//        System.out.println("::::::::::::::::::::");

        try {
            PreparedStatement statement = dbcon.prepareStatement(query);
            long start = System.nanoTime();
            ResultSet rs = statement.executeQuery();
            synchronized (lock){
                TJs.add(System.nanoTime() - start);
            }
            while (rs.next()) {
                Movie data = movies.get(rs.getString("movieId"));
                if (data == null) continue;
                data.sid_list = rs.getString("sid");
                data.sname_list = rs.getString("name");
            }
            rs.close();
            statement.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    synchronized protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized (lock){
            TS_start = System.nanoTime();
        }

//        System.out.println("callling api/movie");
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            request.getSession().setAttribute("curURL", request.getQueryString());
//            System.out.println("set session " + request.getSession().getAttribute("curURL"));

            Connection dbcon = dataSource.getConnection();

            // browse by genre
            String genre = request.getParameter("genre");
            // browse by begin with
            String begin = request.getParameter("begin");

            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");

//            System.out.println(title);

            String N = request.getParameter("N");
            String page = request.getParameter("page");

            String first = request.getParameter("first");
            String second = request.getParameter("second");

            String query;

            PreparedStatement statement = null;

            synchronized (lock){
                movies = new LinkedHashMap<>();
                TJs = new ArrayList<>();
            }

            //  each if get movie data and get star of these movies

            // search by genre
            if (genre != null && !genre.isEmpty()) {

                query = "select id, title, year, director,\n" +
                        "       group_concat(distinct g.name order by g.name) as genre_list,\n" +
                        "       rating\n" +
                        "from movies left join ratings on id=ratings.movieId\n" +
                        "            left join (select name, movieId from genres_in_movies join genres on genres_in_movies.genreId = id ) g on id=g.movieId\n" +
                        "group by id, rating\n" +
                        "having find_in_set( ? , genre_list)\n";

                query += queryAppend(N, page, first, second);
                statement = dbcon.prepareStatement(query);

                statement.setString(1, genre);

                getMovieData(statement);
                getStarInfo(dbcon);

                // search by beginning character
            }
            else if (begin != null && !begin.isEmpty()) {
                query = "select id, title, year, director,\n" +
                        "       group_concat(distinct g.name order by g.name) as genre_list,\n" +
                        "       rating\n" +
                        "from movies left join ratings on id=ratings.movieId\n" +
                        "            left join (select name, movieId from genres_in_movies join genres on genres_in_movies.genreId = id ) g on id=g.movieId\n" +
                        "where title like ? \n" +
                        "group by id, rating\n";

                String append = begin.equals("*") ? "regexp '^[^0-9a-z]'\n" :  begin + "%";
                query += queryAppend(N, page, first, second);

                statement = dbcon.prepareStatement(query);
                statement.setString(1, append);

//                System.out.println(statement);

                getMovieData(statement);
                getStarInfo(dbcon);

            }
            // search by condition
            else{

                // two if below to get movie data

                // search by condition: with one of three conditions
                if (title != null || year != null || director != null){

                    query = "select id, title, year, director,\n" +
                            "       group_concat(distinct g.name order by g.name) as genre_list,\n" +
                            "       rating\n" +
                            "from movies left join ratings on id=ratings.movieId\n" +
                            "            left join (select name, movieId from genres_in_movies join genres on genres_in_movies.genreId = id ) g on id=g.movieId\n" +
                            "where  1=1\n";

                    List<String> title_split = null;
                    List<String> director_split= null;

                    if (title != null && !title.isEmpty()) {
                        title_split = Arrays.asList(title.split(" "));
                        query += "\tand (title like ? or (";
                        for (int i = 0; i < title_split.size(); i++){
                            query += " edSubstr( ? , lower(title)) and ";
                        }
                        query = query.substring(0, query.length() -4);
                        query += "))\n";
                    }
                    if (year != null && !year.isEmpty()) {
                        query += "\tand year = ? \n";
                    }
                    if (director != null && !director.isEmpty()) {
                        director_split = Arrays.asList(director.split(" "));
                        query += "\tand (director like ? or (";
                        for (int i = 0; i < director_split.size(); i++){
                            query += " edSubstr( ? , lower(director)) and ";
                        }
                        query = query.substring(0, query.length() -4);
                        query += "))\n";
                    }

                    query += "group by id, rating\n";
                    query += queryAppend(N, page, first, second);
                    statement = dbcon.prepareStatement(query);

                    int ct = 1;
                    if (title != null && !title.isEmpty()) {
                        statement.setString(ct, "%" + title + "%");
                        ct++;
                        for (String t: title_split){
                            statement.setString(ct, t.toLowerCase());
                            ct++;
                        }
                    }
                    if (year != null && !year.isEmpty()) {
                        statement.setInt(ct, Integer.parseInt(year));
                        ct++;
                    }
                    if (director != null && !director.isEmpty()) {
                        statement.setString(ct, "%" + director + "%");
                        ct++;
                        for (String t: director_split){
                            statement.setString(ct, t.toLowerCase());
                            ct++;
                        }
                    }

//                    System.out.println("getting movie ids ::::::::::::::::::::");
//                    System.out.println(statement);
//                    System.out.println("::::::::::::::::::::");

                    getMovieData(statement);

                    // intersection of star and movies
                    if (star != null && !star.isEmpty()) {
                        query = "select movieId,\n" +
                                "       group_concat(distinct sid order by ct desc) sid,\n" +
                                "       group_concat(distinct name order by ct desc) name\n" +
                                "from superstar\n" +
                                "where movieId in (";
                        for (int i = 0; i < movies.size(); i++){
                            query += " ? ,";
                        }
                        query = query.substring(0, query.length()-1);
                        query += ")\n" +
                                "   and (name like ? ) or (";

                        List<String> star_split = Arrays.asList(star.split(" "));

                        int max = star_split.size();
                        for (int i = 0; i<max; i++){
                            query += " edSubstr( ? , lower(name)) and";
                        }
                        query = query.substring(0, query.length() - 4);
                        query += ")\n "+
                                "group by movieId;";

                        statement = dbcon.prepareStatement(query);
                        ct = 1;
                        for (String id: movies.keySet()){
                            statement.setString(ct, id);
                            ct++;
                        }
                        statement.setString(ct, "%" + star + "%");
                        ct++;
                        for (String s: star_split){
                            statement.setString(ct, s.toLowerCase());
                            ct++;
                        }

//                        System.out.println("intersection of star is :::::::::::");
//                        System.out.println(statement);
//                        System.out.println(":::::::::::::::::::::");
                        // Perform the query
                        long start = System.nanoTime();;
                        ResultSet rs = statement.executeQuery();
                        synchronized (lock){
                            TJs.add(System.nanoTime() - start);
                        }
                        while (rs.next()){
                            Movie m =  movies.get(rs.getString("movieId"));
                            if (m == null) continue;
                            m.sid_list= rs.getString("sid");
                            m.sname_list= rs.getString("name");
                        }
                        rs.close();
                        statement.close();
                        // removal unnecessary
                        movies.entrySet().removeIf(e-> !e.getValue().ready());
                    }
                }

                // search by condition: with only star
                else{
                    // get movie id
                    query = "select movieId,\n" +
                            "       group_concat(distinct sid order by ct desc) sid,\n" +
                            "       group_concat(distinct name order by ct desc) name\n" +
                            "from superstar\n" +
                            "where (name like ? or (";
                    List<String> star_split = Arrays.asList(star.split(" "));

                    int max = star_split.size();
                    for (int i = 0; i<max; i++){
                        query += " edSubstr( ? , lower(name)) and";
                    }
                    query = query.substring(0, query.length() - 4);
                    query += "))\n "+
                            "group by movieId;";

                    statement = dbcon.prepareStatement(query);
                    statement.setString(1, "%" + star + "%");
                    for (int ct = 2; ct <= max+1; ct++){
                        statement.setString(ct, star_split.get(ct-2).toLowerCase());
                    }

//                    System.out.println("find all stars :::::::::::");
//                    System.out.println(statement);
//                    System.out.println(":::::::::::::::::::::");
                    // Perform the query
                    long start = System.nanoTime();
                    ResultSet rs = statement.executeQuery();
                    synchronized (lock){
                        TJs.add(System.nanoTime() - start);
                    }
                    while (rs.next()){
                        Movie m = new Movie(rs.getString("sid"), rs.getString("name"));
                        movies.put(rs.getString("movieId"), m);
                    }
                    rs.close();
                    statement.close();

                    query = "select id, title, year, director,\n" +
                            "       group_concat(distinct g.name order by g.name) as genre_list,\n" +
                            "       rating\n" +
                            "from movies left join ratings on id=ratings.movieId\n" +
                            "            left join (select name, movieId from genres_in_movies join genres on genres_in_movies.genreId = id ) g on id=g.movieId\n" +
                            "where id in (";
                    for (String id: movies.keySet()){
                        query += "'" + id + "',";
                    }
                    query = query.substring(0, query.length()-1);
                    query += ")\ngroup by id, rating\n";
//                    System.out.println("fixxixngkjla\n" + query);
                    query += queryAppend(N, page, first, second);
                    statement = dbcon.prepareStatement(query);

                    start = System.nanoTime();
                    rs = statement.executeQuery();
                    TJs.add(System.nanoTime() - start);
                    while(rs.next()){
                        Movie m = movies.get(rs.getString("id"));
                        m.id = rs.getString("id");
                        m.title = rs.getString("title");
                        m.year = rs.getString("year");
                        m.director = rs.getString("director");
                        m.genre_list = rs.getString("genre_list");
                        m.rating = rs.getString("rating");
//                        System.out.println(m.toString());
                    }
                    rs.close();
                    statement.close();
                    movies.entrySet().removeIf(e -> !e.getValue().starReady());
                }

                // unless there are no movies
                if (movies.size() != 0){
                    getStarInfo(dbcon);
                }
            }

            // after if-else movies are ready
            // parse and return
            JsonArray jsonArray = new JsonArray();
            for (Map.Entry<String, Movie>kv: movies.entrySet()){
                Movie data = kv.getValue();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", kv.getKey());
                jsonObject.addProperty("movie_name", data.title);
                jsonObject.addProperty("movie_dob", data.year);
                jsonObject.addProperty("movie_director", data.director);
                jsonObject.addProperty("genre_list", data.genre_list);
                jsonObject.addProperty("movie_rate", data.rating);
                jsonObject.addProperty("star_id",data.sid_list);
                jsonObject.addProperty("star_list",data.sname_list);
                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);

            statement.close();
            dbcon.close();
            synchronized (lock){
                FileWriter logFile = new FileWriter("queryLog", true);
                logFile.write("TS," + (System.nanoTime()-TS_start) + ",");
                long sum = 0L;
                for (long i: TJs){
                    sum += i;
                }
                logFile.write("TJ," +  sum + "\n");
                logFile.close();
            }

        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());


            response.setStatus(500);

        }
        out.close();
    }
}