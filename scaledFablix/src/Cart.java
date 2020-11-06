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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "cart", urlPatterns = "/api/cart")
public class Cart extends HttpServlet {
    private static final long serialVersionUID = 2L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        Map<String, Item> map = (Map<String, Item>) request.getSession().getAttribute("cart");

        JsonArray jsonArray = new JsonArray();
        JsonObject cur = new JsonObject();
        cur.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
        jsonArray.add(cur);
        System.out.println("::::::::::");
        System.out.println("prev url = " + cur.get("curURL"));
        for (String item : map.keySet())
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", item);
            Item additem = map.get(item);
            jsonObject.addProperty("qty", additem.qty);
            jsonObject.addProperty("price", additem.price());
            jsonArray.add(jsonObject);
            System.out.println("Cart: \t"+ additem.qty + " " + item);
        };
        System.out.println("::::::::::");

        // write JSON string to output
        response.getWriter().write(jsonArray.toString());
        // set response status to 200 (OK)
        response.setStatus(200);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String title = request.getParameter("data");
        String command = request.getParameter("command");
        String qty = request.getParameter("qty");
        System.out.println("::::::::::");
        System.out.println("Cart doPost, \n \t setting " + title + " with command " + command);
        System.out.println("::::::::::");
        PrintWriter out = response.getWriter();
        Map<String, Item> map = (Map<String, Item>) request.getSession().getAttribute("cart");
        if (command.equals("add")){
            Item movie = map.get(title);
            movie.qty += 1;
            System.out.println(movie.qty);
        }
        if (command.equals("decrease")){
            Item movie = map.get(title);
            movie.qty -= 1;
            System.out.println(movie.qty);
            if(Integer.valueOf(movie.qty )==0){
                map.remove(title);
            }
        }
        if (command.equals("delete")){
            map.remove(title);
        }
        if (command.equals("update") && qty != null){
            Item movie = map.get(title);
            movie.qty = Integer.parseInt(qty);
            System.out.println(movie.qty);
        }
        JsonArray jsonArray = new JsonArray();
        for (String item : map.keySet())
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", item);
            jsonObject.addProperty("qty", map.get(item).qty);
            jsonObject.addProperty("price",  map.get(item).price());
            jsonObject.addProperty("curURL", (String) request.getSession().getAttribute("curURL"));
            jsonArray.add(jsonObject);
        };
        out.write(jsonArray.toString());
        response.setStatus(200);
    }
}


