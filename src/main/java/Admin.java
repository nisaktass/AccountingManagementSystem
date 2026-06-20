

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author nisaaktas
 */
public class Admin extends javax.swing.JFrame {

    /**
     * Creates new form admin
     */
    CardLayout cl;
    public static DefaultTableModel usertable;  //main kodda hata olunca static tanımladım 
    public static Connection baglanti;
    public static PreparedStatement komut;
    
    
    
   
    /// search cancel regex ///
    public Admin() {
        initComponents();
       
    }
    
    public Admin(String username) throws SQLException {
        initComponents();
        
        //grafık ıcın 
    BalanceGraph bg=new BalanceGraph();
    bg.setPreferredSize(drawingPanel.getSize());
    drawingPanel.setLayout(new BorderLayout()); //tüm alanı kaplasın
    drawingPanel.add(bg,BorderLayout.CENTER);
    drawingPanel.revalidate(); //yerlesim güncelle
    drawingPanel.repaint();//yeniden çiz
        

        
       
      //username ıle labelı gunceledı 
        WlcLb.setText("Welcome "+username);
        
        //cardlayout olusturup welcome panelı acılısta gosterdık 
       cl = (CardLayout) MainPnl.getLayout();
       cl.show(MainPnl, "card5");
      
  
///// edt delete ıcın baslangıc tabloda secım var mı bunu kontrol edıyor secılınce formu dolduruyor butonları aktıve eıyor.       

//basta butonlar ınaktıf 
        dltBttn.setEnabled(false);
        edtBttn.setEnabled(false);
        
        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean isSelected = userTable.getSelectedRow() != -1;
            int selectedRow = userTable.getSelectedRow();//hangı satır secıldı kontrol 
            if (selectedRow == -1) { //secım yapılmadıysa butonlar ınaktıf kalsın
                dltBttn.setEnabled(false);
                edtBttn.setEnabled(false);
                return; // Hiçbir işlem yapma
            }
           //secım varsa butonlar aktıf 
         dltBttn.setEnabled(isSelected);//isSelected true donerse butonlar aktıf 
         edtBttn.setEnabled(isSelected);
         
         //form doldurma
         //tablodan alınanı forma yerlestırme
         idtxt1.setText(userTable.getValueAt(selectedRow, 0).toString()); //id //secılen satırın 0. ındeksınndekı elemanı al 
         untxt1.setText(userTable.getValueAt(selectedRow, 1).toString());//username
         mailtxt1.setText(userTable.getValueAt(selectedRow, 2).toString());//mail
         passtxt1.setText(userTable.getValueAt(selectedRow, 3).toString());//sifre
         
            String gender = userTable.getValueAt(selectedRow, 4).toString(); //radıobuttonlara döndürme 
            if (gender.equalsIgnoreCase("women")) {
                women.setSelected(true);
            } else if (gender.equalsIgnoreCase("men")) {
                men.setSelected(true);
            }
            
            String role = userTable.getValueAt(selectedRow, 5).toString();
            if (role.equalsIgnoreCase("admin")) {
                admin.setSelected(true);
            } else if (role.equalsIgnoreCase("user")) {
                user.setSelected(true);
            }
         
         
         
});
        //tabloya default model atama ve verileri tabloya yazma 
        usertable = (DefaultTableModel) userTable.getModel(); //jtable yı defaukttable yaptı satır ekleme sılme vs ıcın 
        tabloDoldurma();  ////verileri tabloya yazma. database ıle baglantı saglayıp verı cekme 


    }

    public JPanel getDrawingPanel() { //cancel butonları ıcın bunları olusturudm 
        return drawingPanel;
    }

    public JPanel getMainPnl() {
        return MainPnl;
    }
        
    
   //userları  database e gonderme 
    private void addUserToDatabase(String username, String sifre, String email, String cinsiyet, String rol) {
    
        Connection conn = null;
    PreparedStatement stmt = null;
//sorgu baslattı 
    String query = "INSERT INTO personel (username, sifre, email, cinsiyet, rol) VALUES (?, ?, ?, ?, ?)";
//? lerı sonradan gelecek degerler ıcın yer tutucu 
    try {
        // sorgu baslattı baglandı 
        conn = DatabaseConnection.connect();  // kendi bağlantı sınıfını kullandım
        stmt = conn.prepareStatement(query);

        stmt.setString(1, username); //? lerıne parametrelerı yerlestırdı 
        stmt.setString(2, sifre);
        stmt.setString(3, email);   
        stmt.setString(4, cinsiyet);
        stmt.setString(5, rol);

        // sorgu calistırmak ve ekleme yapılmıs mı kontrol eklemek 
        int rowsInserted = stmt.executeUpdate();//sorgu calıstrı işlem basarılı ise 0 dan buyuk deger doner 

        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(this, "User added successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "User could not be added.");
        }

    } catch (SQLException e) { //burada bazı hatalae dondururyır 
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
    } finally { //baglantıyı kapat her zaman 
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }
    }
    
// verılen username ve password ile eşlesen jayıt var mı sorgu 
    public boolean isUserExists(String username, String password) { //un pass kontrol 
    boolean exists = false;
    Connection conn = null;

    PreparedStatement stmt = null;

    ResultSet rs = null;

    try {
        conn = DatabaseConnection.connect(); // burada kendi sınıfımı kullanıyrum 
        String query = "SELECT * FROM personel WHERE username = ? AND sifre = ?"; 
        stmt = conn.prepareStatement(query); // username ve sifreyle eşleşen kayıt var mı ona bakar 
        stmt.setString(1, username); //? kullanıcıdan gelen verılerı koyar 
        stmt.setString(2, password);
        rs = stmt.executeQuery();

        if (rs.next()) { //calıstı satır varsa  true 
            exists = true;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally { //kapat 
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        try { if (conn != null) conn.close(); } catch (Exception e) {}
    }

    return exists; //kullanıcı var mı yok mu döndür 
}
   
    
    public static void tabloDoldurma() throws SQLException {
        
        usertable.setRowCount(0);//tum satırlar temızlık 
       // db baglantı. 
        baglanti = DatabaseConnection.connect();
        String sorgu = "SELECT*FROM personel";//personel tablosunu aldık. 
        komut = baglanti.prepareCall(sorgu);
        ResultSet sonuc = komut.executeQuery(); //dönen sonuclar sonuc ta tutulur.select oldugu ıcın executequery dıgerlerı  execute update. 

        while (sonuc.next()) { //her satıra sırayla gecer satır varsa true doner verılerı al 
            Object[] satir = { //her kayıt ıcın obje dızı olusturuluyor ıcıne sayı metin nesne girebilir.
                sonuc.getInt("id"),
                sonuc.getString("username"),
                sonuc.getString("email"),
                sonuc.getString("sifre"),
                sonuc.getString("cinsiyet"),
                sonuc.getString("rol")
            };
            usertable.addRow(satir); // diziyi tabloya ekle 

        }
        sonuc.close();
        komut.close();
        baglanti.close();
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        MainPnl = new javax.swing.JPanel();
        userPnl = new javax.swing.JPanel();
        UsrTP = new javax.swing.JTabbedPane();
        addUserPnl = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        usernametxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        passtxt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        adminBttn = new javax.swing.JRadioButton();
        userBttn = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        confirmtxt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        emailtxt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        womenBttn = new javax.swing.JRadioButton();
        menBttn = new javax.swing.JRadioButton();
        addBttn = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        untxt1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        passtxt1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        admin = new javax.swing.JRadioButton();
        user = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        women = new javax.swing.JRadioButton();
        men = new javax.swing.JRadioButton();
        edtBttn = new javax.swing.JButton();
        mailtxt1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        idtxt1 = new javax.swing.JTextField();
        dltBttn = new javax.swing.JButton();
        cancelbttn = new javax.swing.JButton();
        wlcPnl = new javax.swing.JPanel();
        WlcLb = new javax.swing.JLabel();
        drawingPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        operationsItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        viewItem = new javax.swing.JMenuItem();
        userItem = new javax.swing.JMenu();
        useropmenuıtem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MainPnl.setLayout(new java.awt.CardLayout());

        jLabel1.setText("Username:");

        usernametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernametxtActionPerformed(evt);
            }
        });

        jLabel2.setText("Password:");

        jLabel3.setText("Role:");

        buttonGroup2.add(adminBttn);
        adminBttn.setText("Admin");

        buttonGroup2.add(userBttn);
        userBttn.setText("User");

        jLabel4.setText("Confirm Password:");

        confirmtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmtxtActionPerformed(evt);
            }
        });

        jLabel5.setText("email:");

        emailtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailtxtActionPerformed(evt);
            }
        });

        jLabel6.setText("Gender");

        buttonGroup1.add(womenBttn);
        womenBttn.setText("Women");

        buttonGroup1.add(menBttn);
        menBttn.setText("Men");

        addBttn.setText("Add User");
        addBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addUserPnlLayout = new javax.swing.GroupLayout(addUserPnl);
        addUserPnl.setLayout(addUserPnlLayout);
        addUserPnlLayout.setHorizontalGroup(
            addUserPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addUserPnlLayout.createSequentialGroup()
                .addGroup(addUserPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(usernametxt)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(confirmtxt)
                    .addComponent(passtxt)
                    .addComponent(emailtxt, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(addUserPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addUserPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3)
                    .addGroup(addUserPnlLayout.createSequentialGroup()
                        .addComponent(womenBttn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(menBttn))
                    .addGroup(addUserPnlLayout.createSequentialGroup()
                        .addComponent(adminBttn)
                        .addGap(18, 18, 18)
                        .addComponent(userBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(addUserPnlLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(addBttn)))
                .addContainerGap(655, Short.MAX_VALUE))
        );
        addUserPnlLayout.setVerticalGroup(
            addUserPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addUserPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernametxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addUserPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(womenBttn)
                    .addComponent(menBttn))
                .addGap(23, 23, 23)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(addUserPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userBttn)
                    .addComponent(adminBttn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addComponent(addBttn)
                .addContainerGap())
        );

        UsrTP.addTab("Add", addUserPnl);

        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Username", "email", "Password", "Role", "Gender"
            }
        ));
        jScrollPane3.setViewportView(userTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 526, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel4);

        jLabel8.setText("Username:");

        untxt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                untxt1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Password:");

        jLabel10.setText("Role:");

        buttonGroup4.add(admin);
        admin.setText("Admin");

        buttonGroup4.add(user);
        user.setText("User");

        jLabel11.setText("email:");

        jLabel12.setText("Gender");

        buttonGroup3.add(women);
        women.setText("Women");

        buttonGroup3.add(men);
        men.setText("Men");

        edtBttn.setText("EDİT USER");
        edtBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edtBttnActionPerformed(evt);
            }
        });

        mailtxt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailtxt1ActionPerformed(evt);
            }
        });

        jLabel13.setText("id");

        idtxt1.setEditable(false);
        idtxt1.setToolTipText("");

        dltBttn.setText("DELETE USER");
        dltBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dltBttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(admin)
                                    .addComponent(women)
                                    .addComponent(jLabel10))
                                .addGap(45, 45, 45)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(men)
                                    .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11)
                                .addComponent(mailtxt1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                                .addComponent(jLabel12)
                                .addComponent(passtxt1)
                                .addComponent(untxt1)
                                .addComponent(idtxt1)))
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(edtBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dltBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(idtxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(untxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(passtxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(mailtxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(women)
                    .addComponent(men))
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(admin)
                    .addComponent(user))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edtBttn)
                    .addComponent(dltBttn))
                .addGap(128, 128, 128))
        );

        jSplitPane2.setRightComponent(jPanel5);

        UsrTP.addTab("Edit/Delete", jSplitPane2);

        cancelbttn.setText("Cancel");
        cancelbttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelbttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout userPnlLayout = new javax.swing.GroupLayout(userPnl);
        userPnl.setLayout(userPnlLayout);
        userPnlLayout.setHorizontalGroup(
            userPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(userPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UsrTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelbttn))
                .addContainerGap(235, Short.MAX_VALUE))
        );
        userPnlLayout.setVerticalGroup(
            userPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userPnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(UsrTP, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cancelbttn)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        MainPnl.add(userPnl, "card3");

        WlcLb.setText("welcome");

        drawingPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        drawingPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drawingPanelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout drawingPanelLayout = new javax.swing.GroupLayout(drawingPanel);
        drawingPanel.setLayout(drawingPanelLayout);
        drawingPanelLayout.setHorizontalGroup(
            drawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );
        drawingPanelLayout.setVerticalGroup(
            drawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 154, Short.MAX_VALUE)
        );

        jLabel7.setText("Click on the graph to access the financial report");

        javax.swing.GroupLayout wlcPnlLayout = new javax.swing.GroupLayout(wlcPnl);
        wlcPnl.setLayout(wlcPnlLayout);
        wlcPnlLayout.setHorizontalGroup(
            wlcPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wlcPnlLayout.createSequentialGroup()
                .addGroup(wlcPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wlcPnlLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(WlcLb))
                    .addGroup(wlcPnlLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(wlcPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(drawingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(776, Short.MAX_VALUE))
        );
        wlcPnlLayout.setVerticalGroup(
            wlcPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wlcPnlLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(WlcLb)
                .addGap(25, 25, 25)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(drawingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(424, Short.MAX_VALUE))
        );

        MainPnl.add(wlcPnl, "card5");

        jMenu1.setText("Transaction");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenu1.add(jSeparator1);

        operationsItem.setText("Operations");
        operationsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operationsItemActionPerformed(evt);
            }
        });
        jMenu1.add(operationsItem);
        jMenu1.add(jSeparator2);

        viewItem.setText("View");
        viewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewItemActionPerformed(evt);
            }
        });
        jMenu1.add(viewItem);

        jMenuBar1.add(jMenu1);

        userItem.setText("Users");
        userItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userItemMouseClicked(evt);
            }
        });

        useropmenuıtem.setText("User Operations");
        useropmenuıtem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useropmenuıtemActionPerformed(evt);
            }
        });
        userItem.add(useropmenuıtem);

        jMenuBar1.add(userItem);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1031, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(MainPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(136, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void usernametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernametxtActionPerformed

    private void confirmtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_confirmtxtActionPerformed

    private void emailtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailtxtActionPerformed

    private void addBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBttnActionPerformed
        // TODO add your handling code here:
        
        //bos alan var mı kontrol
        if (usernametxt.getText().trim().isEmpty()
            ||passtxt.getText().trim().isEmpty()
            || confirmtxt.getText().trim().isEmpty()
            || emailtxt.getText().trim().isEmpty()
            || buttonGroup1.getSelection() == null
            || buttonGroup2.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields");
            return;
        }
        ///////regex kontrol 
       String usernameRegex = "^[a-z]{3,}"; //kucuk harf mın 3 karakter
            String passRegex ="^\\w{8,}$"; //buyuk kucuk harf rakam _ mın 8 tekrar 
            String emailRegex = "^[A-Za-z0-9+_.-]{1,64}@([A-Za-z0-9]+){2,255}\\.[a-z]{2,}$";

        boolean isValid=true;//gecerli mi 
        boolean hasError=false;//butu hataları tek seferde sırayla gostersın dıye 
        
        if (!usernametxt.getText().matches(usernameRegex)) {
            JOptionPane.showMessageDialog(this, "username can not include uppercase character or digit.");

            isValid=false;
            hasError=true;
        }
        if (!passtxt.getText().matches(passRegex)) {
            JOptionPane.showMessageDialog(this, "Password must be strong!");
            
            isValid=false;
            hasError=true;

        }
        if (!emailtxt.getText().matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Enter valid email.");
            
            isValid=false;
            hasError=true;

        }
        if(hasError){//bunlarda hata varsa ileri gitmeyecek. hataları gosterecek. 
            return;
        }
        if (!passtxt.getText().equals(confirmtxt.getText())) {
            JOptionPane.showMessageDialog(this, "password do not match");
           
            isValid=false;
            return;

        }
        if (isUserExists(usernametxt.getText(), passtxt.getText())) {
            JOptionPane.showMessageDialog(this, "This user already exists.");
            
            isValid=false;
            return;
        }
        
        
        //dstabase e ve tabloya ekleme 
        if(isValid){ //true donunce 
        
            //butonlata tıklandıgında deger dondursun dıye keledım 
            womenBttn.setActionCommand("women");
            menBttn.setActionCommand("men");
            
            adminBttn.setActionCommand("admin");
            userBttn.setActionCommand("user");
             
            String gender = buttonGroup1.getSelection().getActionCommand(); // men / women
            String role = buttonGroup2.getSelection().getActionCommand();   // admin / user
            
            addUserToDatabase(usernametxt.getText(),
                passtxt.getText(),
                emailtxt.getText(),
                gender,
                role);

            usernametxt.setText("");
            passtxt.setText("");
            confirmtxt.setText("");
            emailtxt.setText("");
            buttonGroup1.clearSelection();
            buttonGroup2.clearSelection();

        }
        try { 
            tabloDoldurma();
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addBttnActionPerformed

    private void cancelbttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelbttnActionPerformed
        // TODO add your handling code here://///////////////////////////////////////////
        //kullanıcı welcome panelıne gıder. 
        int option = JOptionPane.showConfirmDialog(this, "Are you sure","Cancel", JOptionPane.YES_NO_OPTION);
        if(option==JOptionPane.YES_OPTION){
            cl.show(MainPnl, "card5");
        }
        
    }//GEN-LAST:event_cancelbttnActionPerformed

    private void untxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_untxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_untxt1ActionPerformed

    private void edtBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edtBttnActionPerformed
        // TODO add your handling code here:
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        try {
            
            //regex
            String usernameRegex = "^[a-z]{3,}"; //kucuk harf mın 3 karakter 
            String passRegex = "^\\w{8,}$";
            String emailRegex = "^[A-Za-z0-9+_.-]{1,64}@([A-Za-z0-9]+){2,255}\\.[a-z]{2,}$";
            
            //yeı degerlerı okuma 
            int id = Integer.parseInt(idtxt1.getText());
            String newUsername = untxt1.getText();
            String newEmail = mailtxt1.getText();
            String newPass = passtxt1.getText();
            String newGender = women.isSelected() ? "women" : "men"; //women butonu secılıyse women degılse men alır kosullu kısa if ternary
            String newRole = admin.isSelected() ? "admin" : "user"; //admin secılıyse admın degılse user alır. 

            //regex karsılastırması
            if(!newUsername.trim().matches(usernameRegex)){
                JOptionPane.showMessageDialog(this, "username can not include uppercase letters or digits. must be at least 3 characters long");
                return;
            }
            if(!newEmail.trim().matches(emailRegex)){
                JOptionPane.showMessageDialog(this, "Enter valid email.");
                return;
            }
            if(!newPass.trim().matches(passRegex)){
                JOptionPane.showMessageDialog(this, "Password must be strong!");
                return;
            }

           //tablodan eskı degerlerı alma
            String oldUn = userTable.getValueAt(selectedRow, 1).toString();
            String oldmail = userTable.getValueAt(selectedRow, 2).toString();
            String oldpass = userTable.getValueAt(selectedRow, 3).toString();
            String oldgender = userTable.getValueAt(selectedRow, 4).toString();
            String oldrole = userTable.getValueAt(selectedRow, 5).toString();

            //eskı verı yenı verıye esıtse 
            if ((newUsername.trim().equals(oldUn))
                && newEmail.trim().equals(oldmail)
                && newPass.trim().equals(oldpass)
                && newGender.trim().equals(oldgender)
                && newRole.trim().equals(oldrole)) {
                JOptionPane.showMessageDialog(null, "No changes were made");
                return;
            }
            
            baglanti = DatabaseConnection.connect(); // ıd ye gore sutu guncelleme
            String query = "UPDATE personel SET username=?,email=?,sifre=?,cinsiyet=?,rol=? WHERE id=?";//set hangı sutun guncellencek 
            komut = baglanti.prepareStatement(query);
            komut.setString(1, newUsername); //1. soru ısaretıne newUsername atacak username sutunu guncellenmıs olacak. 
            komut.setString(2, newEmail);
            komut.setString(3, newPass);
            komut.setString(4, newGender);
            komut.setString(5, newRole);
            komut.setInt(6, id); //hangi satır olduguna idye gore  kaar verecek 

            int updated = komut.executeUpdate(); //guncelleme olduysa sıfırdan buyuk deger doner 
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "User updated successfully.");

                userTable.clearSelection(); //tabloda secımı temızledık. 

                tabloDoldurma();//sql den verıyı cekıp tabloyu guncelledık. 

                //secımlerı ve yazıları sıfırladık. 
                idtxt1.setText("");
                untxt1.setText("");
                mailtxt1.setText("");
                passtxt1.setText("");
                buttonGroup1.clearSelection(); // gender
                buttonGroup2.clearSelection(); // role

                edtBttn.setEnabled(false); // Butonları kapat
                dltBttn.setEnabled(false);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Update error: " + ex.getMessage());
        }
        

    }//GEN-LAST:event_edtBttnActionPerformed

    private void mailtxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailtxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mailtxt1ActionPerformed

    private void dltBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dltBttnActionPerformed
        // TODO add your handling code here:
        int selectedRow = userTable.getSelectedRow(); //secılen satırın ındeksını verır 
        if (selectedRow != -1) { //index eksı ır ıse hıc secım yok demektır 

            int id = (int) userTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "ARE YOU SURE", "CONFİRM DELETE", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                try {
                    baglanti = DatabaseConnection.connect();
                    String query = "DELETE FROM personel WHERE id=?"; //id ile eşleşen satırı sil. 
                    komut = baglanti.prepareStatement(query);
                    komut.setInt(1, id); //1. soru ısareıne id attık. 
                    komut.executeUpdate();
                    
                    //tabloyu guncelledim 
                    tabloDoldurma();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "eror while deleting user:" + ex.getMessage());

                }
                JOptionPane.showConfirmDialog(this, "user deleted ");
                

            }
            //alan buton temızlık 
            idtxt1.setText("");
            untxt1.setText("");
            mailtxt1.setText("");
            passtxt1.setText("");
            buttonGroup1.clearSelection(); // gender
            buttonGroup2.clearSelection(); // role

            edtBttn.setEnabled(false); // Butonları kapat
            dltBttn.setEnabled(false);

        }
        try { 
            tabloDoldurma();
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_dltBttnActionPerformed

    private void userItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userItemMouseClicked
        // TODO add your handling code here:
       
        
    }//GEN-LAST:event_userItemMouseClicked

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        // TODO add your handling code here:
        

    }//GEN-LAST:event_jMenu1MouseClicked

    private void operationsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operationsItemActionPerformed
        // TODO add your handling code here:
         User u=new User(); //user frame acıyor 
        u.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_operationsItemActionPerformed

    private void viewItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewItemActionPerformed
        // TODO add your handling code here:
         Transaction t = null; //try catch lerı kendı eklettı 
        try {
            t = new Transaction(2); //ransactıon tabbed pane ıcın 
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }

        t.setVisible(true);
    }//GEN-LAST:event_viewItemActionPerformed

    private void drawingPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawingPanelMouseClicked
        // TODO add your handling code here:
        Transaction t = null; //try catch lerı kendı eklettı 
        try {
            t = new Transaction(2); //transactıon a 2 parametresını yollar view actırır. 
    
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }

        t.setVisible(true);
        
        
    }//GEN-LAST:event_drawingPanelMouseClicked

    private void useropmenuıtemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useropmenuıtemActionPerformed
        // TODO add your handling code here:
        if (cl == null) {
        cl = (CardLayout) MainPnl.getLayout(); 
    }
    cl.show(MainPnl, "card3");//user ıle alakalı cardlayout acar
    }//GEN-LAST:event_useropmenuıtemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Admin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MainPnl;
    private javax.swing.JTabbedPane UsrTP;
    private javax.swing.JLabel WlcLb;
    private javax.swing.JButton addBttn;
    private javax.swing.JPanel addUserPnl;
    private javax.swing.JRadioButton admin;
    private javax.swing.JRadioButton adminBttn;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JButton cancelbttn;
    private javax.swing.JTextField confirmtxt;
    private javax.swing.JButton dltBttn;
    private javax.swing.JPanel drawingPanel;
    private javax.swing.JButton edtBttn;
    private javax.swing.JTextField emailtxt;
    private javax.swing.JTextField idtxt1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextField mailtxt1;
    private javax.swing.JRadioButton men;
    private javax.swing.JRadioButton menBttn;
    private javax.swing.JMenuItem operationsItem;
    private javax.swing.JTextField passtxt;
    private javax.swing.JTextField passtxt1;
    private javax.swing.JTextField untxt1;
    private javax.swing.JRadioButton user;
    private javax.swing.JRadioButton userBttn;
    private javax.swing.JMenu userItem;
    private javax.swing.JPanel userPnl;
    private javax.swing.JTable userTable;
    private javax.swing.JTextField usernametxt;
    private javax.swing.JMenuItem useropmenuıtem;
    private javax.swing.JMenuItem viewItem;
    private javax.swing.JPanel wlcPnl;
    private javax.swing.JRadioButton women;
    private javax.swing.JRadioButton womenBttn;
    // End of variables declaration//GEN-END:variables

}

