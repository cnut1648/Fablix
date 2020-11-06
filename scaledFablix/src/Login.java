import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
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
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // write to response
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        if (phone==null) {
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid grecapycha verify");
                out.println(responseJsonObject.toString());
                out.close();
                return;
            }
        }

        try {
            Connection dbcon = dataSource.getConnection();

            String query = "select *\n" +
                    "from customers\n" +
                    "where email = ?";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, username);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            System.out.println("password: " + password);

            boolean success = false;

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                System.out.println("after encrypt: the password is " + success);
            }
            
            if (!success){
                // Login fail
                System.out.println("\tlogin fail");
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid email or password. Please try again.");
            }
            else {
                do {
                    // login success
                    System.out.println("\tlogin success!!");
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("id", rs.getString("id"));
                    userInfo.put("firstName", rs.getString("firstName"));
                    userInfo.put("lastName", rs.getString("lastName"));
                    userInfo.put("address", rs.getString("address"));
                    userInfo.put("email", rs.getString("email"));

                    System.out.println("hello there! \n");
                    for (String k: userInfo.keySet()){
                        System.out.println("\t " + k + userInfo.get(k));
                    }

                    request.getSession().setAttribute("user", userInfo);
                    request.getSession().setAttribute("employee", "no");

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