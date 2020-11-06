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
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "check_Dashboard", urlPatterns = "/api/checkdb", loadOnStartup = 1)
public class CheckDashboard extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        System.out.println("check Dashboard");

       String employee = (String)request.getSession().getAttribute("employee");
       JsonObject jsonObject = new JsonObject();
       System.out.println(employee);
       if(employee.equals("yes"))
       {
          jsonObject.addProperty("status","yes");
       }
       else{
           jsonObject.addProperty("status","no");
       }
        System.out.println("ok here");
        out.write(jsonObject.toString());
        System.out.println("ok here");
        response.setStatus(200);
        out.close();
    }




}

