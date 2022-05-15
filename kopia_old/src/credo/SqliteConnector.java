/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 *
 * @author Tomek
 */
public class SqliteConnector {
    public static Connection connection = null;
    public static String pathtodatabase = null;
    public static void initConnection() throws SQLException{
        pathtodatabase = "d:/Projects/Python/PycharmProjects/CREDO/sqlite/credo.db";
        connection = DriverManager.getConnection("jdbc:sqlite:" + pathtodatabase);
    }
    
    public static ArrayList getHittypes() throws SQLException{
        ArrayList hittypes = new ArrayList();
        if (connection == null)
            initConnection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from hittype");
        while(rs.next()) {
          // read the result set
          System.out.println("name = " + rs.getString("name"));
          System.out.println("id = " + rs.getInt("id"));
          hittypes.add(rs.getString("name"));
        }
        return hittypes;
    }
    
    public static boolean insertHits(double[][]hits) throws SQLException{
        if (connection == null)
            initConnection();
        Statement statement = connection.createStatement();
        
        
        
        for (int a = 0; a < hits.length; a++){
            if (a % 1000 == 0)
                System.err.println(a);
            String statemntSQL = "INSERT INTO hit (x, y, type) VALUES";
            statemntSQL += "(" + Double.toString(hits[a][0]) + "," + Double.toString(hits[a][1]) + ",1)";
            statement.execute(statemntSQL);
        }
        /*
        String statemntSQL = "INSERT INTO hit (x, y, type) VALUES";
        
        for (int a = 0; a < hits.length; a++){
            if (a > 0)
                statemntSQL += ",";
            statemntSQL += "\n";
            statemntSQL += "(" + Double.toString(hits[a][0]) + "," + Double.toString(hits[a][1]) + ",1)";
        }*/
        
        //return statement.execute(statemntSQL);
        return true;
    }
    
    
}
