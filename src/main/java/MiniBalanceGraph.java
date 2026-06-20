
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
public class MiniBalanceGraph extends JPanel{
    public MiniBalanceGraph() {
        setPreferredSize(new Dimension(60, 44)); // Sabit boyut
        setBackground(Color.WHITE);
    }
    @Override

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //panelin ılk halını getırdım. 
        Graphics2D g2 = (Graphics2D) g;

        // Arka plan
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 60, 44); // Sabit boyut sol ustten baslayıp 60,44 panel 

        // Çerçeve
        g2.setColor(Color.GRAY);
        g2.drawRect(3, 9, 54, 25); // x=3, y=9, genişlik=54, yükseklik=25

        // Gelir çizgisi (yeşil)
        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(5, 25, 20, 20);
        g2.drawLine(20, 20, 35, 23);
        g2.drawLine(35, 23, 55, 15);

        // Gider çizgisi (kırmızı)
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(5, 15, 20, 17);
        g2.drawLine(20, 17, 35, 20);
        g2.drawLine(35, 20, 55, 18);
    }

    
}

