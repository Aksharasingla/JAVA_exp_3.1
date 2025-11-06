CREATE DATABASE company;
USE company;

CREATE TABLE Employee (
  EmpID INT PRIMARY KEY,
  Name VARCHAR(100),
  Salary DECIMAL(10,2)
);

INSERT INTO Employee (EmpID, Name, Salary) VALUES
(101, 'Alice', 50000.00),
(102, 'Bob', 60000.00),
(103, 'Carol', 55000.00);

package com.example.web.util;

import java.sql.*;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/company?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } 
        catch (ClassNotFoundException e){ throw new RuntimeException(e); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}

package com.example.web;

import com.example.web.util.DBUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String idParam = request.getParameter("id");
        String all = request.getParameter("all");

        try (PrintWriter out = response.getWriter();
             Connection conn = DBUtil.getConnection()) {

            out.println("<!doctype html><html><body><h2>Employees</h2>");

            if (all != null && all.equals("true")) {
                String sql = "SELECT EmpID, Name, Salary FROM Employee";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {

                    out.println("<table border='1'><tr><th>EmpID</th><th>Name</th><th>Salary</th></tr>");
                    while (rs.next()) {
                        out.printf("<tr><td>%d</td><td>%s</td><td>%.2f</td></tr>",
                                   rs.getInt("EmpID"),
                                   escapeHtml(rs.getString("Name")),
                                   rs.getDouble("Salary"));
                    }
                    out.println("</table>");
                }
            } else if (idParam != null && !idParam.isBlank()) {
                String sql = "SELECT EmpID, Name, Salary FROM Employee WHERE EmpID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(idParam));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            out.println("<table border='1'><tr><th>EmpID</th><th>Name</th><th>Salary</th></tr>");
                            out.printf("<tr><td>%d</td><td>%s</td><td>%.2f</td></tr>",
                                       rs.getInt("EmpID"),
                                       escapeHtml(rs.getString("Name")),
                                       rs.getDouble("Salary"));
                            out.println("</table>");
                        } else {
                            out.println("<p>No employee found with ID: " + escapeHtml(idParam) + "</p>");
                        }
                    }
                }
            } else {
                out.println("<p>No action. Use the search form.</p>");
            }
            out.println("<p><a href='employee.html'>Back</a></p>");
            out.println("</body></html>");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String escapeHtml(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
