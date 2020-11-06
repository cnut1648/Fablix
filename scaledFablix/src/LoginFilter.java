import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
        if (true){
            // Keep default action: pass along the filter chain
            System.out.println("allowed");
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession().getAttribute("user") == null) {
            System.out.println("redirect---");
//            absolute path
            httpResponse.sendRedirect("/cs122B/login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */

//        either ends with allowedURIs or contains allowedURIs
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("api/elogin");
        allowedURIs.add("utils/login.js");
        allowedURIs.add("EmployeLogin.html");
        allowedURIs.add("EmployeLogin.js");
        allowedURIs.add("api/elogin");

        allowedURIs.add("utils/EmployeLogin.js");
        allowedURIs.add("utils/index.css");
        allowedURIs.add("utils/letter-f.png");
        allowedURIs.add("utils/movie1.jpg");
        allowedURIs.add("utils/movie2.jpg");
        allowedURIs.add("utils/movie3.jpg");
        allowedURIs.add("utils/tv.png");
    }

    public void destroy() {
        // ignored.
    }

}
