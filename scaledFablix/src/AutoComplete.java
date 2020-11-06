import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL

@WebServlet(name = "autoComplete", urlPatterns = "/api/autoComplete", loadOnStartup = 1)
public class AutoComplete extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        // setup the response json arrray
        JsonArray jsonArray = new JsonArray();

        // get the query string from parameter
        String title = request.getParameter("title");

        // return the empty json array if query is null or empty
        if (title == null || title.trim().isEmpty()) {
            response.getWriter().write(jsonArray.toString());
            return;
        }

        try {
            Connection dbcon = dataSource.getConnection();
            String query = "select id, title\n" +
                    "from movies\n" +
                    "where match(title) against ( ? in boolean mode) or (  ";
            List<String> titleArgs = Arrays.asList(title.split(" "));
            int max = titleArgs.size();
            for (int i = 0; i < max; i++ ){
                query += " edSubstr( ? , lower(title)) and ";
            }
            query = query.substring(0, query.length() - 4);
            query += ")\n";
            System.out.println(query);
            PreparedStatement stmt = dbcon.prepareStatement(query);
            String arg = "";
            for (String a: titleArgs){
                arg += "+" + a + "* ";
            }
            stmt.setString(1, arg);
            for (int ct = 2; ct <= max+1; ct++){
                stmt.setString(ct, titleArgs.get(ct-2).toLowerCase());
            }
            System.out.println(stmt);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                JsonObject o = generateResponse(rs.getString("id"), rs.getString("title"));
                jsonArray.add(o);
            }
            rs.close();
            stmt.close();
            dbcon.close();

            response.getWriter().write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }


    }
    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "movie-title",
     *   "data": { "movieID": xxx }
     * }
     *
     */
    private static JsonObject generateResponse(String id, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", id);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}