
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nisaaktas
 */
public class DatabaseConnection {
    
    public static Connection connect() throws SQLException {//throws baglantıda hata olursa hata fırlatr
        String url = "jdbc:mysql://localhost:3306/project"; //baglanılacak verıtabanı adı 
        String user = "root";
        String password = "fsmblm";

        return DriverManager.getConnection(url, user, password);//verıtabanına baglantı connectıon nesnesı döndürme 
    }//drıvermanager sınıfının get connectıon metodu url user pass alarak verıtabanına baglanır 
}
