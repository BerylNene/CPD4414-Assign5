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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
     * @param prod
     */
  //  @Override
    @POST
      @Path ("{productId}")
    public void doPost(String prod) throws ParseException{
        JSONObject jObject = (JSONObject) new JSONParser().parse(prod);
        String name = (String) jObject.get("name");
        String description = (String) jObject.get("description");
        int quantity =(int) jObject.get("quantity");
        doPostPut("INSERT INTO products (name, description, quantity) VALUES (?, ?, ?)", name, description, quantity);
    }
    
    private int doPostPut(String query, String name, String description, int quantity){
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
  
     @PUT
    @Path("{productId}")
 // @Override
    public void doPut(@PathParam("productId")int id, String prod)throws IOException, SQLException{
        JSONObject jObject = new JSONObject();
        String name = (String) jObject.get("name");
        String description = (String) jObject.get("description");
        int quantity =(int) jObject.get("quantity");
        doPostPut("INSERT INTO products (name, description, quantity) VALUES (?, ?, ?, ?)", name, description, quantity);
         Connection conn = DatabaseConnection.getConnection();
        String query = "UPDATE products where SET name =\'" + name +"\', description =\'" + description + "\', quantity =\'" + quantity;
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.executeUpdate();
    }


    private int doPutUpdate(String query, String name, String description, int quantity) {
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
    
    @DELETE
    @Path("{productId}")
    //@Override
        public void doDelete(@PathParam("productId")int id) throws SQLException{
        Connection conn = DatabaseConnection.getConnection();
        String query = "DELETE from products where productId =" + id;
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.execute();
            
    }

    private int delete(String query, int id) {
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
}
  