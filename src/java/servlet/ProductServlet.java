/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import databaseConnection.DatabaseConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0641046
 */
@WebServlet("/Beryl")
public class ProductServlet extends HttpServlet {
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         response.setHeader("Content-Type", "text/plain-text");
        try (PrintWriter out = response.getWriter()) {
            if (!request.getParameterNames().hasMoreElements()) {
                // There are no parameters at all
                out.println(getResults("SELECT * FROM products"));
            } else {
                // There are some parameters
                int id = Integer.parseInt(request.getParameter("id"));
                out.println(getResults("SELECT * FROM products WHERE id = ?", String.valueOf(id)));
            }
        } catch (IOException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        if (keySet.contains("id") && keySet.contains("name")
                && keySet.contains("description") && keySet.contains("quantity")) {
            // There are some parameters
            String id = request.getParameter("id");
            int id_1 = Integer.parseInt(id);
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String quantity = request.getParameter("quantity");
            int quantity_1 = Integer.parseInt(quantity);
            
            doUpdate("INSERT INTO sample (id, name, description, quantity) VALUES (?, ?, ?, ?)", id, name, description, quantity);
        } else {
            // There are no parameters at all
            response.setStatus(500);
        }
    }
    
    /**
     * 
     * @param query
     * @param params
     * @return 
     */
    
 private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    
 private String getResults(String query, String... params) {
//     ArrayList prod = new ArrayList();
//     prod.add(id);
//     prod.add(name);
//     prod.add(description);
//     prod.add(quantity);
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i-1]);
            }
            ResultSet rs = pstmt.executeQuery();
            sb.append("[");
            while (rs.next()) {
                sb.append(String.format("{ \"productId\" : %d, \"name\" : %s, \"description\" : %s, \"quantity\" : %d },", 
                        rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
            }
            sb.substring(0, sb.length()-1);
            sb.append("]");
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    

}
