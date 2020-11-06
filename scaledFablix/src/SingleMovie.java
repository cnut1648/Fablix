import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovie", urlPatterns = "/api/single-movie")
public class SingleMovie extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String movieid = request.getParameter("movieid");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "select m.id, title, year, director,\n" +
                    "       group_concat(distinct s.name order by s.ct desc, s.name asc) as star_list,\n" +
                    "       group_concat(distinct s.id order by s.ct desc, s.name asc) as star_id,\n" +
                    "       group_concat(distinct g.name order by g.name) as genre_list, rating\n" +
                    "from (select * from movies where id= ? ) m\n" +
                    "         left join ratings R on R.movieId= m.id\n" +
                    "         left join (select name, movieId from genres_in_movies join genres on genres_in_movies.genreId = id) g on m.id=g.movieId\n" +
                    "         left join (select id, name, movieId, ct\n" +
                    "                    from stars_in_movies join stars on stars_in_movies.starId = stars.id join starCT on sid = starId) s on m.id = s.movieId\n" +
                    "group by R.movieId,R.rating\n" +
                    "order by rating desc;";


            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, movieid);

            System.out.println("query for single-movie");
            System.out.println("::::::::::");
            System.out.println(statement.toString());
            System.out.println("::::::::::");

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            JsonObject url = new JsonObject();
            url.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
            System.out.println(url.toString());
            jsonArray.add(url);

            // Iterate through each row of rs
            while (rs.next()) {
                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", rs.getString("title"));
                jsonObject.addProperty("id", rs.getString("id"));
                jsonObject.addProperty("year", rs.getString("year"));
                jsonObject.addProperty("director", rs.getString("director"));
                jsonObject.addProperty("star_list", rs.getString("star_list"));
                jsonObject.addProperty("star_id", rs.getString("star_id"));
                jsonObject.addProperty("genre_list", rs.getString("genre_list"));
                jsonObject.addProperty("rating", rs.getString("rating"));

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }
}

