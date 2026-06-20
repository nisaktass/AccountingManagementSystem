
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nisaaktas
 */
public class LoginService {
    public static String kullanıcıDoğrulama(String username,String pass){
        //verıtabanına baglanıp kullanıcı adı ve sıfre ıslesmesı kontrol 
         try (Connection conn = DatabaseConnection.connect()) {
            
           
            String query = "SELECT rol FROM personel WHERE username = ?  AND sifre = ?";
            //sorgu ıcın nesne olusturduj 
            PreparedStatement stmt = conn.prepareStatement(query);
            
            // Kullanıcıdan alınan verileri sorguya ekliyoruz
            //1. parametreyı sorgudakı ılk? yerıne 
            stmt.setString(1, username);
            //2. parametre 2. ? yerıne 
            stmt.setString(2, pass);  

            // Sorguyu çalıştırıyoruz
           //sonucu resutsete atıyoruz 
            ResultSet rs = stmt.executeQuery();
            
            // Eğer kullanıcı bulunduysa, rolünü döndür
            if (rs.next()) {
                return rs.getString("rol"); // admin ya da user döndürüyoruz
            } else {
                // Kullanıcı bulunamadıysa null döndür
                return null;
            }
       } catch (SQLException e) {
            e.printStackTrace();
            return null; // Hata durumunda null döndürüyoruz
        }
        
    }
    }
        


