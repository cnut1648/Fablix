import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "Payment", urlPatterns = "/api/Payment")

public class Payment extends HttpServlet {
    private static final long serialVersionUID = 2L;
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        System.out.println("ok to process");
        Map<String, Item> map = (Map<String, Item>) request.getSession().getAttribute("cart");
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
        jsonArray.add(jsonObject1);
        for (String item : map.keySet())
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", item);
            jsonObject.addProperty("qty", map.get(item).qty);
            jsonObject.addProperty("price", map.get(item).price());
            jsonObject.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
            jsonArray.add(jsonObject);
        }

        response.getWriter().write(jsonArray.toString());
        response.setStatus(200);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // write to response
        PrintWriter out = response.getWriter();
        System.out.println("ok to payment");
        String cardnumber = request.getParameter("CreditCard");
        String data = request.getParameter("data");

        try {
            Connection dbcon = dataSource.getConnection();

            String query = "select *\n" +
                    "from creditcards\n" +
                    "where id = ? \n" +
                    "and  expiration= ?";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, cardnumber);
            statement.setString(2, data);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            System.out.println("cardnumber: " + cardnumber);
            if (!rs.first()){
                // Login fail
                System.out.println("\tpay fail");
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid CardNumber or Expiration Data. Please try again.");
            }
            else {
                do {
                    // login success
                    System.out.println("\tPay success!!");
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success!");

                } while (rs.next());
                response.setStatus(200);
            }
            out.println(responseJsonObject.toString());
            rs.close();
            statement.close();
            dbcon.close();
        }
        catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
}


