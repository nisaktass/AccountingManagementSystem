



import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import static java.time.LocalTime.now;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author nisaaktas
 */
public class Transaction extends javax.swing.JFrame { //sadece tur degısınce update etmıyor 

    /**
     * Creates new form Transaction
     */
    public  DefaultTableModel transactionmodel;   //transactıon table default modelı verı tutup guncellemke için tabloya satır sutun ekleyıp guncelleme için gerekli
    public  DefaultTableModel viewmodel; //view table default modelı
    public  Connection baglanti; //
    public  PreparedStatement komut; //sql sorgusu ıcın hazırlanıyor
    String fileName; //txt conver ıcın gerektı 
    
        float totalincome=0f;   //toplam gelır gıder tutan değişkenler 
        float totalexpense=0f;
        float balance=0f;
    
    public Transaction() {
        initComponents();
    }
    
    public Transaction(int selectedTabIndex) throws SQLException {
        initComponents(); 
       
        
        transactionmodel = (DefaultTableModel) UpdateTable.getModel(); //tabloya default modelı atadık
        viewmodel = (DefaultTableModel) viewTable.getModel(); //tabloya default modelı atadık
        
        jTabbedPane1.setSelectedIndex(selectedTabIndex);//parametreye gore tabbed pane acacak 
      
       DltBttn.setEnabled(false); //ilk baslarda butonlar inaktif 
       UpdtBttn.setEnabled(false);
   
    String username = Login.username; //kullanıcıın adını alıyoruz logınde statıc oldugu ıcın sınıf ıle alıyoır 
 //kulanıcı ısımlerını labellere yazdırıyor       
 jLabel8.setText(username);
 jLabel12.setText(username);
 jLabel13.setText(username);
//suankı tarıh ve saatı alıyor
LocalDateTime now=LocalDateTime.now();
//tarıh ve saatı strıng hale getırıyor t yı boslukla degıstırıyor.
String formattedDate = now.toString().replace("T", " "); 
//tarıh bılgısını farklı labellere yazdırıyor
date.setText(formattedDate);
date1.setText(formattedDate);
jLabel14.setText(formattedDate);

//sekme açıldıgında tablo dolu olsun. 
//baslangıcta tabloları doldur  secılen sekmeye gore    
if (selectedTabIndex == 1) {
        tabloDoldurma(transactionmodel);
    } else if (selectedTabIndex == 2) {
        tabloDoldurma(viewmodel);
    }

        
       //update gelıp ıslemsız gecınce vıew tablısu bos kaldı o yyuzden ekledım. 
        //sekmeler arası gecıste tabloları doldur 
        jTabbedPane1.addChangeListener(e -> {
            int index = jTabbedPane1.getSelectedIndex();
    
    try {
        if (index == 1) { // Update sekmesi
            tabloDoldurma(transactionmodel); //update sekmesındekı tablosunu dolduruyor 
        } else if (index== 2) { // View sekmesi
            tabloDoldurma(viewmodel);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
});
        
  

        
        //update kısmında secım yapıp yapılmadıgına bakan formu dolduran kısım. 
        // Sekmeyi seç
    jTabbedPane1.setSelectedIndex(selectedTabIndex);
    //secılen satır degıstıkçe tetıklenecek 
    UpdateTable.getSelectionModel().addListSelectionListener(e -> {
        //satır secım kontrol
            boolean isSelected = UpdateTable.getSelectedRow() != -1; //butonlar ıcın. 
            
            int selectedRow = UpdateTable.getSelectedRow(); //tablonun secılen satırı alıyor
            if (selectedRow == -1) { //secım yoksa 
                DltBttn.setEnabled(false); //butonlar ınaktıf 
                UpdtBttn.setEnabled(false);
                return; // Hiçbir işlem yapma
            }
           
            //secım varsa butonlar aktif
         DltBttn.setEnabled(isSelected);
         UpdtBttn.setEnabled(isSelected);
         //secım varsa tablodandoldurma metodu cagrılıyıor 
         if (isSelected) tablodanDoldurma();
 });

}  
   //databaseen verı cekıp tabloyu dolduruyor 
    private void tabloDoldurma(DefaultTableModel model) throws SQLException{  
    model.setRowCount(0); //önce tabloyu sıfırla her verı gelıste tekrar tekrar eklenmesın 
    //verıtabanına baglan
    baglanti = DatabaseConnection.connect();
    String sorgu = "SELECT*FROM transactions";//tablodakı verılerı alıyor 
    komut = baglanti.prepareCall(sorgu);
    ResultSet sonuc = komut.executeQuery(); //sorguyu calıstırıp sonucları alıyır.
        //butun kayıtlar ıcın döngüye gırıyor
        while(sonuc.next()){ //her satırı sırayla alır. 
            //tablodakı sutunların bılgısını cekıp satıra eklıyor 
            //
          model.addRow(new Object[]{
          sonuc.getInt("id"),//transactıons tablosunun sutun isimleri  ben kendı tablomun sırasına gore verıyı cektım. 
          sonuc.getString("type"),
          sonuc.getString("category"),
          sonuc.getString("currency"),
          sonuc.getFloat("amount"),
          sonuc.getTimestamp("created_at"),
          sonuc.getString("created_by"),
          sonuc.getTimestamp("updated_at"),
          sonuc.getString("updated_by"),
          sonuc.getString("description")
               
                
                
        });
           
        }
        
        
    }
        //update kısmında yan taraftakı formu doldurmak ıcın kullandım 
         private void tablodanDoldurma(){ //tostrıng obje strınf donemez vsvs dıye bır uyarı cıktı 
         // tablodan secılen satırı alır 
        int row=UpdateTable.getSelectedRow();//seçilen satır 
        if (row == -1) { //secılen satır yoksa donduruyor 
            return;
        }
        
        //Tipin ne oldugunu tabloya gecırme 
        String type=transactionmodel.getValueAt(row, 1).toString();//secılen satırdakı 1. sutundakı verıyı aldık strınge cevırdık
        //ıcerıdekı ıncome ıse o butonu secılı hale getırdık
        if(type.equals("Income"))income1.setSelected(true);
         else expense1.setSelected(true);//degılse dıgerını secılı hale getırıdk
         
         //Kategorinin ne oldugunu tabloya gecırme  satrın 2. verısını aldık
         categorybox1.setSelectedItem(transactionmodel.getValueAt(row, 2));
         //currency ne tabloya gecırme //satırın 3. verısını aldık
         currencybox1.setSelectedItem(transactionmodel.getValueAt(row, 3));
         //amount tabloya gecırme //satırın 4. verısını aldık
         amountfıeld1.setText(transactionmodel.getValueAt(row, 4).toString());
         //descripytion taboya gecırme  //satırın 5. verısını aldık
         descriptionfield1.setText(transactionmodel.getValueAt(row, 9).toString());
         
}
         private void exportReport(float income,float expense,float balance,int year,int month) throws IOException{
        
             
          fileName="MonthlyReport_"+year+"_"+month+".txt"; //dosya adı belırledık
          //filewriter nesnesı olusturduk. 
        FileWriter writer=new FileWriter(fileName);//dosyada yazı yazmaya yarar dosyaadıtxt adında dosya olusturur yazı yazmaya hazır hale getıtır. 
        //rapor baslık ve ayarları
        writer.write("MONTHLY FINANCIAL REPORT\n");
        writer.write("--------------------------\n");
        writer.write("Month:"+month+"\n");
        writer.write("Year:"+year+"\n");
        //total gelır gıder bakıye vs ayarlıyorz
        writer.write("Total income:"+String.format("%.2f TL", income)+"\n"); //2 ondalık basamak ile format
        writer.write("Total expense:"+String.format("%.2f TL", expense)+"\n");
        writer.write("Total balance:"+String.format("%.2f TL", balance)+"\n");//sayıyı vırgulde sonra 2 basamak ıle yazdırır. 
        //dosyayı kapattık
        writer.close();
        JOptionPane.showMessageDialog(this, "Report successfully saved to TXT file:\n" + fileName);
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
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        income = new javax.swing.JRadioButton();
        expense = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        amountfıeld = new javax.swing.JTextField();
        categorybox = new javax.swing.JComboBox<>();
        currencybox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionfield = new javax.swing.JTextArea();
        AddBttn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel7 = new javax.swing.JPanel();
        income1 = new javax.swing.JRadioButton();
        expense1 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        amountfıeld1 = new javax.swing.JTextField();
        categorybox1 = new javax.swing.JComboBox<>();
        currencybox1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionfield1 = new javax.swing.JTextArea();
        UpdtBttn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        date1 = new javax.swing.JLabel();
        DltBttn = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        UpdateTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        viewTable = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        month = new javax.swing.JComboBox<>();
        yearbox = new javax.swing.JComboBox<>();
        calculatebttn = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        income2 = new javax.swing.JLabel();
        expense2 = new javax.swing.JLabel();
        balancelbl1 = new javax.swing.JLabel();
        incomelbl = new javax.swing.JLabel();
        expenselbl = new javax.swing.JLabel();
        balancelbl = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        txtbttn = new javax.swing.JButton();
        cancelBttn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        buttonGroup1.add(income);
        income.setText("Income");

        buttonGroup1.add(expense);
        expense.setText("Expense");
        expense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expenseActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel1.setText("Type:");

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel2.setText("Category:");

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel3.setText("Amount:");

        amountfıeld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountfıeldActionPerformed(evt);
            }
        });

        categorybox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Rent", "Salary", "Utilities", "Food", "Transportation" }));
        categorybox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryboxActionPerformed(evt);
            }
        });

        currencybox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "TL", "USD", "EURO", " ", " " }));
        currencybox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currencyboxActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel4.setText("Currency:");

        descriptionfield.setColumns(20);
        descriptionfield.setRows(5);
        jScrollPane1.setViewportView(descriptionfield);

        AddBttn.setText("Add");
        AddBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddBttnActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel5.setText("Description:");

        jLabel8.setText("jLabel8");

        date.setText("jLabel1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(date, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(AddBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(categorybox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(currencybox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(amountfıeld, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(income)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(expense))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(date)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(income)
                    .addComponent(expense))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categorybox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currencybox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amountfıeld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(AddBttn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1134, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Add", jPanel2);

        buttonGroup2.add(income1);
        income1.setText("Income");

        buttonGroup2.add(expense1);
        expense1.setText("Expense");
        expense1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expense1ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel6.setText("Type:");

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel7.setText("Category:");

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel9.setText("Amount:");

        amountfıeld1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountfıeld1ActionPerformed(evt);
            }
        });

        categorybox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Rent", "Salary", "Utilities", "Food", "Transportation" }));
        categorybox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categorybox1ActionPerformed(evt);
            }
        });

        currencybox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "TL", "USD", "EURO", " ", " " }));
        currencybox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currencybox1ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel10.setText("Currency:");

        descriptionfield1.setColumns(20);
        descriptionfield1.setRows(5);
        jScrollPane2.setViewportView(descriptionfield1);

        UpdtBttn.setText("UPDATE");
        UpdtBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdtBttnActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel11.setText("Description:");

        jLabel12.setText("jLabel8");

        date1.setText("jLabel1");

        DltBttn.setText("DELETE");
        DltBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DltBttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(date1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(categorybox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(currencybox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(amountfıeld1, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(income1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(expense1))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(UpdtBttn, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(DltBttn)))
                .addContainerGap(164, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(date1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addGap(8, 8, 8)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(income1)
                    .addComponent(expense1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categorybox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currencybox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(amountfıeld1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UpdtBttn)
                    .addComponent(DltBttn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel7);

        UpdateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Type", "Category", "Currency", "Amount", "Created at", "Created by", "Updated at", "Updated by", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        UpdateTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        jScrollPane7.setViewportView(UpdateTable);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 753, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 20, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        jSplitPane1.setLeftComponent(jPanel8);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Delete/Update", jPanel3);

        viewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Type", "Category", "Currency", "Amount", "Created at", "Created by", "Updated at", "Updated by", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(viewTable);

        jLabel13.setText("jLabel13");

        jLabel14.setText("jLabel14");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 753, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 773, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 492, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        month.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MONTH", "January ", "February", "March", "April", "May", "June ", "July ", "August", "September", "Octaber", "November ", "December" }));

        yearbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "YEAR", "2023", "2024", "2025" }));

        calculatebttn.setText("CALCULATE");
        calculatebttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculatebttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(month, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(yearbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(calculatebttn)
                .addContainerGap(98, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(month, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calculatebttn)))
        );

        jLabel18.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("FINANCIAL REPORTS");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        income2.setText("income");

        expense2.setText("expense");

        balancelbl1.setText("balance");

        incomelbl.setText("jLabel1");

        expenselbl.setText("jLabel2");

        balancelbl.setText("jLabel3");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(income2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(incomelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(expense2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(balancelbl1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 78, Short.MAX_VALUE)))
                        .addGap(2, 2, 2)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(expenselbl, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(balancelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(income2)
                    .addComponent(incomelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expenselbl, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expense2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(balancelbl1)
                    .addComponent(balancelbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        txtbttn.setText("Export TXT");
        txtbttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtbttn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtbttn)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 31, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(138, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(90, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("View", jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelBttn.setText("Cancel");
        cancelBttn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBttnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cancelBttn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelBttn)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBttnActionPerformed
        // TODO add your handling code here:
        //cancel butonu secılıyken role gore ayrım yapıyor 
        //logın sınıfından rol bılgısıı alıyor 
        if (Login.rol.equalsIgnoreCase("admin")) {
            Admin a = new Admin(); //admın nesnesı olusturuyor ve acıyor 
            a.setVisible(true);
            
// Grafik paneline grafik bileşeni yeniden eklenmeli //tekrar gıdınce grafık gorunsun dıye yazdım 
            BalanceGraph grafik = new BalanceGraph(); // Grafik çizim sınıfın buysa
            a.getDrawingPanel().setLayout(new BorderLayout());
            a.getDrawingPanel().add(grafik, BorderLayout.CENTER);
            a.getDrawingPanel().revalidate();
            a.getDrawingPanel().repaint();

// CardLayout ile card5'e geç //butona basınca yenıde welcome panelıne atsın
            CardLayout cl = (CardLayout) a.getMainPnl().getLayout();
            cl.show(a.getMainPnl(), "card5");


         a.getMainPnl().repaint(); //main panelı yenıledım. 
         //user ıse user frame ını acsın. 
        } else if (Login.rol.equalsIgnoreCase("user")) {
            User u = new User();
            u.setVisible(true);
            //gerı donunce tekrar grafıgımı cızsın dıye 
            
       MiniBalanceGraph bg=new MiniBalanceGraph();//grafık sınıfıfnın nesnesını olusturduk
       bg.setPreferredSize(jPanel2.getSize());//grafık sınıfının sıze ını bpanelle eşitledij 
       jPanel2.setLayout(new BorderLayout()); //tüm alanı kaplasın
       jPanel2.add(bg,BorderLayout.CENTER);//panelin ortasına ekledık
       jPanel2.revalidate(); //yerlesim (yenı layout)güncelle
       jPanel2.repaint();

        }

        
    }//GEN-LAST:event_cancelBttnActionPerformed

    private void expenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expenseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expenseActionPerformed

    private void amountfıeldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountfıeldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amountfıeldActionPerformed

    private void categoryboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryboxActionPerformed

    private void currencyboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currencyboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currencyboxActionPerformed

    private void AddBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBttnActionPerformed
        // TODO add your handling code here:
        //username ı aldım 
String username = Login.username;
//kullanıcı adını yazdırdım
jLabel8.setText(username);
//tarıh saat bılgısını aldım

LocalDateTime now = LocalDateTime.now();
//sqle uygun tarıh objesı
Timestamp createdAt = Timestamp.valueOf(now);
//tarıhı kullanıcı okuyabılsın dıye strıng yaptık
String formattedDate = now.toString().replace("T", " ");  //tarıh ve saat arasında bır tane T var onu boslukla degıstırıyor 
//labele tarıhı yazdrdık. 
date.setText(formattedDate);

        //radıobuttonlara actıon command atayıp butona tıklayınca donecek deger belırlenıyor. 
             income.setActionCommand("income");
             expense.setActionCommand("expense");
             
             //seçili butonun actıon command ını alıyoruz gelır gıder vs donutyıor 
             String type = buttonGroup1.getSelection().getActionCommand(); // gelır gıder
             //dıger bılesenlerı alıyoruz 
             String category=categorybox.getSelectedItem().toString();
             String currency=currencybox.getSelectedItem().toString();
             String amountStr=amountfıeld.getText().trim();
             String description=descriptionfield.getText().trim();
             //herhangı bır alan bossa uyarı verıyır 
             if(type.isEmpty()||category.isEmpty()||currency.isEmpty()||amountStr.isEmpty()){
                 JOptionPane.showMessageDialog(this,"fields can not be empty.");
                 return;    
             }
             
             float amount;
             try{
                 //kulanıcının gırdıfgı miktarı (textfıledde strıng formda ) sayıya cevırıyor
                 amount=Float.parseFloat(amountStr);
                 if(amount<=0) throw new NumberFormatException();   //negatıfse hata fırlatıyor              
             }catch(NumberFormatException e){
                 JOptionPane.showMessageDialog(this, "Please enter a valid positive number..");
                 return;
             }
           
try {
    
    //verıtabanı baglantısı acıyor 
        Connection conn = DatabaseConnection.connect();
        //sql sorgusu hazırlanıyor 7b alan 7 tane ? var 
        String sql = "INSERT INTO transactions (type, category, amount, description, currency,created_at,created_by) VALUES (?,?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
//? lerıne sırasıyla degerler atanıyır 
        pst.setString(1, type);
        pst.setString(2, category);
        pst.setFloat(3, amount);
        pst.setString(4, description);
        pst.setString(5, currency);
        pst.setString(6, formattedDate);
        pst.setString(7, username);
       //sorgu calıstırılıyor ve verıtabanına eklenıyor transactıons tablosuna 
        pst.executeUpdate();
        JOptionPane.showMessageDialog(this, "The transaction has been added successfully.");
        
        //guncel tabloyla javadakı tablomuzu dolduruyor
        tabloDoldurma(transactionmodel); // Update tablosu içn 
         tabloDoldurma(viewmodel); // View tablosu için
        conn.close();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "ERROR: " + e.getMessage());
    }
             
        
    }//GEN-LAST:event_AddBttnActionPerformed

    private void expense1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expense1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expense1ActionPerformed

    private void amountfıeld1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amountfıeld1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amountfıeld1ActionPerformed

    private void categorybox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categorybox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categorybox1ActionPerformed

    private void currencybox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currencybox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currencybox1ActionPerformed

    private void UpdtBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdtBttnActionPerformed
        // TODO add your handling code here:
        //secilen satırı alıyor 
        int selectedRow = UpdateTable.getSelectedRow();
        if (selectedRow == -1) {//secım yoksa işlem yapmıyor 
            return;
        } 
        //KULLANICININN YENI DEGERLERI ICIN BUNU YAPIYOR. 
        //actıon command eklıyor butona deger atıyor  
        income1.setActionCommand("income");
        expense1.setActionCommand("expense");
        //tablonun secılen satırının 0.sutunundakı degerı to strıng yapıyır 
        int id = Integer.parseInt(transactionmodel.getValueAt(selectedRow, 0).toString());
        //buton grupta secılenın degerını donduruyor 
        String newtype = buttonGroup2.getSelection().getActionCommand(); // income expense 
        //diğer bileşenleri alıyor. kullanıcını yaptıgı yenı secımler 
             String newcategory=categorybox1.getSelectedItem().toString();
             String newcurrency=currencybox1.getSelectedItem().toString();
             Float newamount=Float.parseFloat(amountfıeld1.getText().trim());
             String newdescription=descriptionfield1.getText().trim();
             //ESKİ DEĞERLERİn ne oldugunu belırleme 
             //Tipin ne oldugunu tablodan secme 
        String oldtype=transactionmodel.getValueAt(selectedRow, 1).toString();
         if(oldtype.equals("Income"))income1.setSelected(true);
         else expense1.setSelected(true);// ona gore buton aktıf yapıyor 
         
         //Kategorinin ne oldugunu tablodan alma
         String oldCategory=(String) transactionmodel.getValueAt(selectedRow, 2);
         //currency ne tablodan alma
        String oldCurrency= (String) transactionmodel.getValueAt(selectedRow, 3);
         //amount tablodan alma
        Float oldAmount= Float.parseFloat(transactionmodel.getValueAt(selectedRow, 4).toString());
         //descripytion tabodan alma 
         String olddescription=transactionmodel.getValueAt(selectedRow, 9).toString();
         
if ((newtype.trim().equals(oldtype))//yenı eskı kıyaslama yapıyır 
                && newcategory.trim().equals(oldCategory)
                && newcurrency.trim().equals(oldCurrency)
                && newamount.equals(oldAmount)
                &&newdescription.trim().equals(olddescription)) {
                JOptionPane.showMessageDialog(null, "No changes were made.");//yenı eskı fark yoksa uyarı 
                return; //işlem iptal  gereksız update engelledı 
            }
try{ 
    //db baglantı sagladı 
         baglanti = DatabaseConnection.connect();
         //sql sorgusu 
            String query = "UPDATE transactions SET type=?,category=?,currency=?,amount=?,description=?,updated_at=?,updated_by=? WHERE id=?";
            //sorguya parametrelerı yerlestırıyoryz 
            komut = baglanti.prepareStatement(query);
            komut.setString(1, newtype);
            komut.setString(2, newcategory);
            komut.setString(3, newcurrency);
            komut.setFloat(4, newamount);
            komut.setString(5, newdescription);
            //guncelleme bılgısı alıyoruz saat ve kullanıcı olarak 
             String updatedAt = java.time.LocalDateTime.now().toString();
             String updatedBy = Login.username;
             //sql sorgusuna gonderıyoruz 
            komut.setString(6, updatedAt );
            komut.setString(7, updatedBy);
            komut.setInt(8,id); //id ye gore bu degerlerı guncellıyoruz 

            int updated = komut.executeUpdate(); //sorgu calıstır 
           //update yapıldıysa basarı mesajı 
            if (updated > 0) {
                JOptionPane.showMessageDialog(null, "The transaction was updated successfully.");
              //tablo secımlerımızı temızle
               UpdateTable.clearSelection();
                //guncel verılerle ıkı tabloyu da doldur 
                tabloDoldurma(transactionmodel);
                 tabloDoldurma(viewmodel);
             //formdakı alanları sıdfırtla 
              categorybox1.setSelectedIndex(0);
              currencybox1.setSelectedIndex(0);
              amountfıeld1.setText("");
               descriptionfield1.setText("");
               
                buttonGroup2.clearSelection(); 

                UpdtBttn.setEnabled(false); // Butonları kapat
                DltBttn.setEnabled(false);

            }
            
            
        } catch (SQLException ex) { //hata olursa uyarı
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Update error: " + ex.getMessage());
        }


        try { //tabloyu yenıden doldurmak ıcın her ıhtımale karsı. 
            tabloDoldurma(transactionmodel);
        } catch (SQLException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            tabloDoldurma(viewmodel);
        } catch (SQLException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        }



             
        
        
    }//GEN-LAST:event_UpdtBttnActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        // TODO add your handling code here:
        
        
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void DltBttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DltBttnActionPerformed
        // TODO add your handling code here:
        //satır secım kontrol 
        int selectedRow = UpdateTable.getSelectedRow();
        if (selectedRow != -1) {
//id al 
            int id = (int) UpdateTable.getValueAt(selectedRow, 0);
            //kullanıcı silme onayı 
            int confirm = JOptionPane.showConfirmDialog(this, "ARE YOU SURE", "CONFİRM DELETE", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                try { //db baglan
                    baglanti = DatabaseConnection.connect();
                    //sorgu calıstır ıd ye gore sılme işlemi 
                    String query = "DELETE FROM transactions WHERE id=?";
                    komut = baglanti.prepareStatement(query);
                    //sorguyu ıd ye yerlestır. 
                    komut.setInt(1, id);//* ne 1. yer tutucu id yerlestırıyor 
                    komut.executeUpdate();//silmeyı gercekleştır 
                    tabloDoldurma(transactionmodel); //guncel tabloyu doldur tekrar 
                    tabloDoldurma(viewmodel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "eror while deleting user:" + ex.getMessage());

                }
                JOptionPane.showMessageDialog(this, "transaction deleted ");
            }
            //form alanlarını sıfırla 
            categorybox1.setSelectedItem(0);
                currencybox1.setSelectedItem(0);
                amountfıeld1.setText("");
               descriptionfield1.setText("");
               
                buttonGroup2.clearSelection(); // role

                UpdtBttn.setEnabled(false); // Butonları kapat
                DltBttn.setEnabled(false);
        }
        try {
            tabloDoldurma(transactionmodel);
            tabloDoldurma(viewmodel);
        } catch (SQLException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_DltBttnActionPerformed

    private void calculatebttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculatebttnActionPerformed
        // TODO add your handling code here:
        //comboboxda secımı yaptık ve bunları aldı 
        int selectedMonthIndex=month.getSelectedIndex();
        int selectedYearIndex=yearbox.getSelectedIndex();
        //secım yapmadıysa uyaro 
        if(selectedMonthIndex==0){
            JOptionPane.showMessageDialog(this, "Please select a valid month!");
        return;
        }
        if(selectedYearIndex==0){
            JOptionPane.showMessageDialog(this, "Please select a valid year!");
        return;
        }
        //secılen ay ve yıl degerını al secılen ındekstekı degerı alıyoruz ocak=1 subat=2 vs 
        int selectedMonth=selectedMonthIndex;
        //burada yılı ıntegere cevırıyor cunku strıng gelıyor 
        int selectedYear=Integer.parseInt(yearbox.getItemAt(selectedYearIndex));
       
        //her satırı tek tek ıncelıoyor 
        for(int i=0;i<viewmodel.getRowCount();i++){
            //satırdakı verılerı alıyor 
           String type=viewmodel.getValueAt(i,1).toString();
           float amount=Float.parseFloat(viewmodel.getValueAt(i, 4).toString());
           String currency=viewmodel.getValueAt(i, 3).toString();
           String dateStr=viewmodel.getValueAt(i, 5).toString();
           String justDate=dateStr.split(" ")[0];//saat ıle alınca hata aldık sadece tarıh ıcın 
           //split bosluknkarakterıne gore ayırır [] ılk parcayı al demektır 
           
           //sadece tarıhı aldım
           LocalDate date=LocalDate.parse(justDate); //string tarıhı localdate nesnesıne cevırdı 
           //secılene aıt degılse gec 
           if(date.getMonthValue()!=selectedMonth||date.getYear()!=selectedYear){
               continue;
           }
           //kur farkı sabut kur kullandım. 
           if(currency.equalsIgnoreCase("USD")){
               amount*=40f;
               
           }else if(currency.equalsIgnoreCase("EURO")){
               amount*=45f;
           }
           //gelır gıder hesapladı
           if(type.equalsIgnoreCase("income")){
               totalincome+=amount;
           }else if(type.equalsIgnoreCase("expense")){ //iki string kıyaslar buyuk kucuk harf farkını yok sayar.
               totalexpense+=amount;
           }
           
        }
        //sonucları yazdırma virgulden sonra 2 bas 
         balance=totalincome-totalexpense;
         incomelbl.setText(String.format("%.2f TL", totalincome));//hata verdı. virgulden sonra 2 bas 
         expenselbl.setText(String.format("%.2f TL", totalexpense));
         balancelbl.setText(String.format("%.2f TL", balance));
        
        
        
        
    }//GEN-LAST:event_calculatebttnActionPerformed

    private void txtbttnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbttnActionPerformed
        // TODO add your handling code here:
        //ay yıl bılgısını aldı 
        int selectedMonth=month.getSelectedIndex(); //1. indeks ocak 2. index subat vsvs...
        int selectedYear=Integer.parseInt(yearbox.getSelectedItem().toString());
        //rapor ıcın degerlerı aldo 
        float income=this.totalincome;
        float expense=this.totalexpense;
        float balance=this.balance;
        
        try { //export raporu calıstırdı 
            exportReport(income,expense,balance,selectedMonth,selectedYear);
        } catch (IOException ex) {
            Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int choice = JOptionPane.showConfirmDialog(this, "The report has been created. Would you like to open the file?", "Report", JOptionPane.YES_NO_OPTION);
//dosya olusunca sorutyıor kullancıı ısterse dosyayı acıyor 
if (choice == JOptionPane.YES_OPTION) {
            try { // desktop sınıfının ornegı program baslatmaya vs yarar. filenmae ısımlı dosyamızı acacak File nesnesı olusturuyor. 
                Desktop.getDesktop().open(new File(fileName)); //masaustu ile baglantı saglar 
            } catch (IOException ex) {
                Logger.getLogger(Transaction.class.getName()).log(Level.SEVERE, null, ex);
            }
}
        
        

        
        
        
        
        
        
        
        
        
       
        
    }//GEN-LAST:event_txtbttnActionPerformed

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
            java.util.logging.Logger.getLogger(Transaction.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Transaction.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Transaction.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Transaction.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Transaction().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddBttn;
    private javax.swing.JButton DltBttn;
    private javax.swing.JTable UpdateTable;
    private javax.swing.JButton UpdtBttn;
    private javax.swing.JTextField amountfıeld;
    private javax.swing.JTextField amountfıeld1;
    private javax.swing.JLabel balancelbl;
    private javax.swing.JLabel balancelbl1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton calculatebttn;
    private javax.swing.JButton cancelBttn;
    private javax.swing.JComboBox<String> categorybox;
    private javax.swing.JComboBox<String> categorybox1;
    private javax.swing.JComboBox<String> currencybox;
    private javax.swing.JComboBox<String> currencybox1;
    private javax.swing.JLabel date;
    private javax.swing.JLabel date1;
    private javax.swing.JTextArea descriptionfield;
    private javax.swing.JTextArea descriptionfield1;
    private javax.swing.JRadioButton expense;
    private javax.swing.JRadioButton expense1;
    private javax.swing.JLabel expense2;
    private javax.swing.JLabel expenselbl;
    private javax.swing.JRadioButton income;
    private javax.swing.JRadioButton income1;
    private javax.swing.JLabel income2;
    private javax.swing.JLabel incomelbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> month;
    private javax.swing.JButton txtbttn;
    private javax.swing.JTable viewTable;
    private javax.swing.JComboBox<String> yearbox;
    // End of variables declaration//GEN-END:variables
}
