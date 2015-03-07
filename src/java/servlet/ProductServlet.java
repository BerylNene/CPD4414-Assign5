/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import databaseConnection.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author c0641046
 */
//@WebServlet("/Beryl")

@Path("/products")
public class ProductServlet {
    
    @GET
    @Produces("application/json")
    public String doGet()throws IOException, SQLException {
        JSONArray jArray = new JSONArray();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM products";
         PreparedStatement preparedStatement = conn.prepareStatement(query);
        
         ResultSet resultSet  = preparedStatement.executeQuery();
         while (resultSet.next()){
             int num_columns = resultSet.getMetaData().getColumnCount();
             JSONObject jObject = new JSONObject();
             for (int i = 0; i < num_columns; i++){
                 String columnName = resultSet.getMetaData().getColumnLabel(i+1);
                 Object columnValue = resultSet.getObject(i+1);
                 jObject.put(columnName, columnValue);
             }
             jArray.add(jObject);
         }
         return jArray.toJSONString();
    }

    @GET
    @Produces("application/json")
    @Path ("{productId}")
    public String doGet(@PathParam("productId")int id)throws IOException, SQLException {
        JSONObject jObject = new JSONObject();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM products where productId =" + id;
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        
         ResultSet resultSet  = preparedStatement.executeQuery();
         while (resultSet.next()){
             int num_columns = resultSet.getMetaData().getColumnCount();
             for (int i = 0; i < num_columns; i++){
                 String columnName = resultSet.getMetaData().getColumnLabel(i+1);
                 Object columnValue = resultSet.getObject(i+1);
                 jObject.put(columnName, columnValue);
             }
             
         }
         return jObject.toJSONString();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     */
  //  @Override
    public void doPost(String prod){
        JSONObject jObject = new JSONObject();
        String name = (String) jObject.get("name");
        int description = (int) jObject.get("description");
        int quantity =(int) jObject.get("quantity");
        doPostPut("INSERT INTO products (name, description, quantity) VALUES (?, ?, ?, ?)", name, description, quantity);
    }
    
    private int doPostPut(String query, String name, int description, int quantity){
        int numChanges = 0;
        ArrayList prod = new ArrayList();
        prod.add(name);
        prod.add(description);
        prod.add(quantity);
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= prod.size(); i++) {
                pstmt.setString(i, prod.get(i - 1).toString());
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    
    }
         
            

    

    /**
     *
     * @param query
     * @param params
     * @return
     */
    private int doUpdate(String query, int id, String name, int description, int quantity) {
        int numChanges = 0;
        ArrayList prod = new ArrayList();
        prod.add(id);
        prod.add(name);
        prod.add(description);
        prod.add(quantity);
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= prod.size(); i++) {
                pstmt.setString(i, prod.get(i - 1).toString());
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
   // @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String getResults(String query, String... params) {

        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            sb.append("[ ");
            while (rs.next()) {
                sb.append(String.format("{ \"productId\" : %d, \"name\" : \"%s\", \"description\" : \"%s\", \"quantity\" : %d },\n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
            }
            sb.setLength(sb.length()-2);
            sb.append(" ]");
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
    private String getSingleResult(String query, String... params) {

        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sb.append(String.format("{ \"productId\" : %d, \"name\" : %s, \"description\" : %s, \"quantity\" : %d }",
                        rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getInt("quantity")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
   // @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
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

            doPutUpdate("UPDATE products SET id = ?, name = ?, description = ?, quantity = ?", id_1, name, description, quantity_1);
        } else {
            // There are no parameters at all
            response.setStatus(500);
        }
    }


    private int doPutUpdate(String query, int id, String name, String description, int quantity) {
        int numChanges = 0;
        ArrayList prod = new ArrayList();
        prod.add(id);
        prod.add(name);
        prod.add(description);
        prod.add(quantity);
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= prod.size(); i++) {
                pstmt.setString(i, prod.get(i - 1).toString());
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }
    
    //@Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Set<String> keySet = request.getParameterMap().keySet();
        if (request.getParameterNames().hasMoreElements()) {
            // There are some parameters
            int id = Integer.parseInt(request.getParameter("id"));
           doUpdate("DELETE from products where id = ?", id);
        } else {
            // There are no parameters at all
            response.setStatus(500);
        }
    }

    private int doUpdate(String query, int id) {
        int numChanges = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, id);
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }


}
