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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "confirm", urlPatterns = "/api/confirm")

public class Confirm extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedbMaster")
    private DataSource dataSource;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // write to response
        PrintWriter out = response.getWriter();
        System.out.println("ok to confirm");
        Map<String, Item> map = (Map<String, Item>) request.getSession().getAttribute("cart");
        Map<String, String> userInfo=(Map<String,String>) request.getSession().getAttribute("user");

        try {
            JsonArray jsonArray = new JsonArray();
            Connection dbcon = dataSource.getConnection();
            java.util.Date date = new java.util.Date();
            String query = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?,?,?)";
            PreparedStatement statement = dbcon.prepareStatement(query);
            Statement statement2 = dbcon.createStatement();
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
            jsonArray.add(jsonObject1);
            for(String item:map.keySet()){
                statement.setString(1, userInfo.get("id"));
                statement.setString(2, map.get(item).id);
                statement.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(date));

                // Perform the query
                statement.executeUpdate();
                String query2="Select max(id) as id from sales";
                ResultSet rs=statement2.executeQuery(query2);
                while (rs.next()) {
                    String sale_id = rs.getString("id");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("sale_id", sale_id);
                    jsonObject.addProperty("title", item);
                    jsonObject.addProperty("qty", map.get(item).qty);
                    jsonObject.addProperty("price", map.get(item).price());
                    jsonObject.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
                    jsonArray.add(jsonObject);
                }
            }
            request.getSession().setAttribute("cart",null);
            Map<String,Item> newmap = (Map<String,Item>)request.getSession().getAttribute("cart");

            if (newmap == null) {

                newmap = new HashMap<>();
                request.getSession().setAttribute("cart", newmap);
            }

            out.write(jsonArray.toString());
            statement.close();
            statement2.close();
            dbcon.close();

        }
        catch (Exception e) {

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