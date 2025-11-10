package com.example.web;

import com.example.web.util.DBUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.*;

@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String studentId = request.getParameter("studentId");
        String date = request.getParameter("date");
        String status = request.getParameter("status");
        String remark = request.getParameter("remark");

        String insertSql = "INSERT INTO Attendance (StudentID, AttDate, Status, Remark) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, studentId);
            ps.setDate(2, Date.valueOf(date)); // expects yyyy-MM-dd
            ps.setString(3, status);
            ps.setString(4, remark);
            int rows = ps.executeUpdate();

            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                if (rows > 0) {
                    out.println("<!doctype html><html><body>");
                    out.println("<h2>Attendance Saved</h2>");
                    out.printf("<p>Student: %s<br>Date: %s<br>Status: %s</p>", escapeHtml(studentId), escapeHtml(date), escapeHtml(status));
                    out.println("<p><a href='attendance.jsp'>Back</a></p>");
                    out.println("</body></html>");
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to insert attendance.");
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String escapeHtml(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
