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

@WebServlet(name = "LogoutServlet", urlPatterns = "/api/logout")
public class Logout extends HttpServlet {
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // write to response
        PrintWriter out = response.getWriter();
        HttpSession s = request.getSession();
        Map<String, String> userinfo = (Map<String, String>) s.getAttribute("user");
        if (userinfo != null) {
            System.out.println("logout.......");
            System.out.println("\tbyebye " + userinfo.get("firstName") + ", " + userinfo.get("lastName"));
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("name", (userinfo.get("firstName") + ","+ userinfo.get("lastName")));
            out.write(responseJsonObject.toString());
            s.removeAttribute("user");
            response.setStatus(200);
        }
    }
}
