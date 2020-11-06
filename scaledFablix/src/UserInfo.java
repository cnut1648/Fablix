import com.google.gson.JsonObject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "UserInfo", urlPatterns = "/api/user")
public class UserInfo extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // write to response
        PrintWriter out = response.getWriter();
        HttpSession s = request.getSession();
        Map<String, String> userinfo = (Map<String, String>) s.getAttribute("user");
        if (userinfo != null) {
            System.out.println("Welcome to user page.......");
            System.out.println("\thello " + userinfo.get("firstName") + ", " + userinfo.get("lastName"));
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("name", (userinfo.get("firstName") + ","+ userinfo.get("lastName")));
            responseJsonObject.addProperty("address", userinfo.get("address"));
            responseJsonObject.addProperty("email", userinfo.get("email"));
            out.write(responseJsonObject.toString());
        }
        response.setStatus(200);
    }
}