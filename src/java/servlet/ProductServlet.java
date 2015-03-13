/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import databaseConnection.DatabaseConnection;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


/**
 *
 * @author c0641046
 */
//@WebServlet("/Beryl")
@Path("/products")
public class ProductServlet {

    @GET
    @Produces("application/json")
    public String doGet() throws IOException, SQLException {
        JsonArrayBuilder jArray = Json.createArrayBuilder();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM products;";
        PreparedStatement pstm = conn.prepareStatement(query);
        ResultSet rs = pstm.executeQuery();
        while (rs.next()) {
            JsonObjectBuilder jObject = Json.createObjectBuilder()
                    .add("productID", rs.getInt("id"))
                    .add("name", rs.getString("name"))
                    .add("description", rs.getString("description"))
                    .add("quantity", rs.getInt("quantity"));
            jArray.add(jObject);
        }
        return jArray.build().toString();
    }

    @GET
    @Produces("application/json")
    @Path("{productId}")
    public String doGet(@PathParam("productId") int id) throws IOException, SQLException {
        JsonObjectBuilder jObject = Json.createObjectBuilder();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM products where id = " + id + ";";
        PreparedStatement pstm = conn.prepareStatement(query);
        ResultSet rs = pstm.executeQuery();
        while (rs.next()) {
            jObject.add("productID", rs.getInt("id"))
                    .add("name", rs.getString("name"))
                    .add("description", rs.getString("description"))
                    .add("quantity", rs.getInt("quantity"));

        }
        return jObject.build().toString();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param prod
     * @throws org.json.simple.parser.ParseException
     */
    //  @Override
    @POST
    @Consumes("application/json")
    public void doPost(String prod) {
        JsonParser jObject = Json.createParser(new StringReader(prod));
        Map<String, String> map = new HashMap<>();
        String key = "";
        String val = "";
        while (jObject.hasNext()) {
            JsonParser.Event evt = jObject.next();
            switch (evt) {
                case KEY_NAME:
                    key = jObject.getString();
                    break;
                case VALUE_STRING:
                    val = jObject.getString();
                    map.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = Integer.toString(jObject.getInt());
                    map.put(key, val);
                    break;
            }
        }
        doInsert("INSERT INTO products (name, description, quantity) VALUES (?, ?, ?)",
                map.get("name"), map.get("description"), map.get("quantity"));
    }

    private int doInsert(String query, String name, String description, String quantity) {
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
    @Consumes("application/json")
    public void doPut(@PathParam("productId") String id, String prod){
        JsonParser jObject = Json.createParser(new StringReader(prod));
        Map<String, String> map = new HashMap<>();
        String key = "";
        String val = "";
        while (jObject.hasNext()) {
            JsonParser.Event evt = jObject.next();
            switch (evt) {
                case KEY_NAME:
                    key = jObject.getString();
                    break;
                case VALUE_STRING:
                    val = jObject.getString();
                    map.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = jObject.toString();
                    map.put(key,val);
                    map.put(key, val);
                    break;
            }
        }
        doInsert("INSERT INTO products (name, description, quantity) VALUES (?, ?, ?)",
                map.get("name"), map.get("description"), map.get("quantity"));
    }
    

    private int doUpdate(String query, String name, String description, String quantity) {
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
    public void doDelete(@PathParam("productId") int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "DELETE from products where id =" + id;
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
            sb.setLength(sb.length() - 2);
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
