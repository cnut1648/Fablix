import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "Addcart", urlPatterns = "/api/Addcart")

public class AddToCart extends HttpServlet {
    private static final long serialVersionUID = 2L;
    // Create a dataSource which registered in web.xml

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve parameter id from url request.
        String name = request.getParameter("title");
        String id = request.getParameter("id");
        System.out.println("::::::::::");
        System.out.println("add to cart\n \t" + name + "\n\t" + id);
        System.out.println("::::::::::");
        Item additem=new Item(name,id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        Map<String,Item> map = (Map<String,Item>)request.getSession().getAttribute("cart");

        // additem.qty init null
        if (map == null){
//            if not exist map then create one
            map = new HashMap<>();
//            request.getSession().setAttribute("cart", map);
            additem.qty = 1;
        }
        else {
            Item existItem = map.get(name);
            if (existItem == null){
                additem.qty = 1;
            }
            else{
                additem.qty = existItem.qty + 1;
            }
        }
        map.put(name, additem);
        request.getSession().setAttribute("cart", map);
        System.out.println("add item qty = " + additem.qty);
        response.setStatus(200);
    }
}

