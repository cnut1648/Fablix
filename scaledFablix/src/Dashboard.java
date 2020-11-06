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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "Dashboard", urlPatterns = "/api/dashboard", loadOnStartup = 1)
public class Dashboard extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedbMaster")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type


        // Retrieve parameter id from url request.


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            Connection dbcon = dataSource.getConnection();

            DatabaseMetaData dbmd = dbcon.getMetaData();
            Map<String, Map<String, String>> output = new HashMap<String, Map<String, String>>();

            ResultSet tables = dbmd.getTables(null, null, null, null);
            JsonArray jsonArray = new JsonArray();
            while (tables.next()) {
                JsonObject jsonObject = new JsonObject();
                String table_name = tables.getString(3);
                HashMap<String, String> currentTable = new HashMap<String, String>();
                output.put(table_name, currentTable);
                jsonObject.addProperty("tablename",table_name);
                ResultSet cols = dbmd.getColumns(null, null, table_name, null);
                String name="";
                String type="";
                while (cols.next()) {

                    name+=cols.getString("COLUMN_NAME");
                    name+=",";
                    type+=cols.getString("TYPE_NAME");
                    type+=",";

                }
                System.out.println(name);
                System.out.println("");
                jsonObject.addProperty("name",name);
                jsonObject.addProperty("type",type);
                jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());
            response.setStatus(200);
            tables.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        } {

        }

    }




    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        System.out.println("Dashboard servelet");
        String out_string = "";
        try {
            Connection dbcon = dataSource.getConnection();
            String status= request.getParameter("movie");
            String status2= request.getParameter("starstatus");
            if(status==null)
            {
                status="no";
            }
            if(status2==null)
            {
                status2="yes";
            }
            System.out.println(status);
            System.out.println(status2);
            if (status.equals("yes"))
            {
                String genre = request.getParameter("genre");
                String title = request.getParameter("title");
                System.out.println(title);
                String year = request.getParameter("year");
                Integer years=Integer.parseInt(year);
                System.out.println(years);
                String director = request.getParameter("director");
                String star = request.getParameter("star");
                CallableStatement cs = dbcon.prepareCall("{call add_movie(?,?,?,?,?,?)}");
                cs.setString("title",title);
                cs.setInt("year", years);
                cs.setString("director",director);
                cs.setString("star_name",star);
                cs.setString("genre",genre);
                cs.registerOutParameter(6, Types.VARCHAR);

                cs.execute();

                out_string = cs.getString(6);
                cs.close();
                System.out.println(out_string);
            }
            if (status2.equals("no")) {
                System.out.println("ok here");
                String star = request.getParameter("starname");
                String star_dob = request.getParameter("stardob");
                System.out.println(star);
                System.out.println(star_dob);
                Integer star_dobs=Integer.parseInt(star_dob);
                CallableStatement cs = dbcon.prepareCall("{call add_star(?,?,?)}");
                cs.setString("star_name",star);
                cs.setInt("star_dob",star_dobs);
                cs.registerOutParameter(3, Types.VARCHAR);
                cs.execute();
                out_string = cs.getString(3);
                cs.close();
                System.out.println(out_string);


            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", out_string);
            out.write(jsonObject.toString());
            response.setStatus(200);
            dbcon.close();

        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());


            response.setStatus(500);

        }
        out.close();

    }
}
