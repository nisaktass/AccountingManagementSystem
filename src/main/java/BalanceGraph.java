
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nisaaktas
 */
class BalanceGraph extends JPanel{  //çizime uygun alan ıcın 
    public BalanceGraph() {
        setPreferredSize(new Dimension(295, 154)); // panelin görünür boyutu //cancelda donunce cok kucuk geldı onu engellemek ıcın kopydum
        setBackground(Color.WHITE); // arka plan görünür olsun
    }
    @Override
    protected void paintComponent(Graphics g ){
        super.paintComponent(g);  //paneli varsayıla halıyle alır 
        Graphics2D g2=(Graphics2D) g; 
         
        //panel
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,295,154);//panel boyutu

        //baslık
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String title="FİNANCİAL REPORTS";
        g2.drawString(title,80,25); //title ı 80,25 noktasından baslattık. 
        
        // Çerçeve
        g2.setColor(Color.GRAY);
        g2.drawRect(10, 30, 275, 110);//x=10,y=30,genıslık 275 yukseklık 110

        // Gelir çizgisi
        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(15, 100, 80, 80); //baslangıc x/baslangıc y/bitiş x/bitiş y sol ust kose baslangıc. 
        g2.drawLine(80, 80, 160, 90);
        g2.drawLine(160, 90, 250, 70);
        
        // Gider çizgisi
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(15, 115, 80, 125);
        g2.drawLine(80, 125, 160, 135);
        g2.drawLine(160, 135, 250, 120);
        
        
    }
    
}
