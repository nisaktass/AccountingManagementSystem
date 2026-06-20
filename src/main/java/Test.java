
import java.sql.Connection;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nisaaktas
 */
public class Test {
    public static void main(String[] args) {
        //veritabanına baglantı yapılırken orun var mı dıye kontrol 
        try {
    Connection conn = DatabaseConnection.connect(); //databaseconnection sınıfının connect metodunu cagırır 
    System.out.println("Connection to database successful!");//sifre vs dogr ise baglantıyı kurar 
    conn.close(); // işlem bittiğinde kapat
} catch (SQLException e) {
    System.out.println("An error occurred while connecting to the database:");//sıfre vs yanlıssa hata fırlatır 
    e.printStackTrace();//hatanın ne oldugunu yazar 
}
Login loginFrame=new Login();//login frameini actık.
loginFrame.setVisible(true);
    }  
    
}