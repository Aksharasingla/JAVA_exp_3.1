<!doctype html>
<html>
<head><meta charset="utf-8"><title>Login</title></head>
<body>
  <h2>Login</h2>
  <form action="LoginServlet" method="post">
    Username: <input name="username" required><br>
    Password: <input name="password" type="password" required><br>
    <button type="submit">Login</button>
  </form>
</body>
</html>

package com.example.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    // Example hardcoded validation; replace with DB lookup if required.
    private static final String USER = "admin";
    private static final String PASS = "admin123";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (PrintWriter out = response.getWriter()) {
            if (username != null && password != null && username.equals(USER) && password.equals(PASS)) {
                out.println("<!doctype html><html><body>");
                out.printf("<h2>Welcome, %s!</h2>", escapeHtml(username));
                out.println("<p>Login successful.</p>");
                out.println("</body></html>");
            } else {
                out.println("<!doctype html><html><body>");
                out.println("<h2>Login failed</h2>");
                out.println("<p>Invalid username or password. <a href='login.html'>Try again</a></p>");
                out.println("</body></html>");
            }
        }
    }

    // minimal HTML escape
    private String escapeHtml(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
