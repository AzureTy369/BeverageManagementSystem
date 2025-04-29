
package GUI;

import BUS.CreateImage;
import DTO.*;
import BUS.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends javax.swing.JPanel {

    /**
     * Creates new form JPanelQuanLyNhanVien
     */
    private ListCustomer listCtm;
    private CustomerBUS ctmBUS;
    private int selectedRowIndex;
    private boolean flat;

    public CustomerPanel() {
        initComponents();
        init();
        editDisplay();
        this.setSize(960, 700);
        this.setVisible(true);
    }

    private void init() {
        listCtm = new ListCustomer();
        ctmBUS = new CustomerBUS(listCtm, this);
        buttonAdd.addActionListener(ctmBUS);
        buttonFix.addActionListener(ctmBUS);
        buttonShowAll.addActionListener(ctmBUS);
        buttonSearch.addActionListener(ctmBUS);

        setCustomer();
        showList(listCtm.getList());
    }

    public boolean isFlat() {
        return flat;
    }

    public javax.swing.JButton getButtonAdd() {
        return buttonAdd;
    }

    public javax.swing.JButton getButtonFix() {
        return buttonFix;
    }

    public javax.swing.JButton getButtonSearch() {
        return buttonSearch;
    }

    public javax.swing.JButton getButtonShowAll() {
        return buttonShowAll;
    }

    public javax.swing.JTable getJTableCustomer() {
        return jTableCustomer;
    }

    public javax.swing.JLabel getLabelPictureCustomer() {
        return labelPictureCustomer;
    }

    public javax.swing.JTextField getTextFieldCustomerAddress() {
        return textFieldCustomerAddress;
    }

    public javax.swing.JTextField getTextFieldCustomerFirstname() {
        return textFieldCustomerFirstname;
    }

    public javax.swing.JTextField getTextFieldCustomerID() {
        return textFieldCustomerID;
    }

    public javax.swing.JTextField getTextFieldCustomerLastname() {
        return textFieldCustomerLastname;
    }

    public javax.swing.JTextField getTextFieldCustomerPhone() {
        return textFieldCustomerPhone;
    }

    public javax.swing.JTextField getTextFieldSearch() {
        return textFieldSearch;
    }

    public final void editDisplay() {
        CreateImage cre = new CreateImage();
        cre.changeColorButton(buttonAdd);
        cre.changeColorButton(buttonFix);
        cre.changeColorButton(buttonSearch);
        cre.changeColorButton(buttonShowAll);
        cre.setIconForButton(buttonAdd, "add.png");
        cre.setIconForButton(buttonFix, "fix.png");
        cre.setIconForButton(buttonSearch, "search.png");
        cre.setIconForButton(buttonShowAll, "refesh.png");
        labelPictureCustomer.setSize(300, 200);
        cre.setIconForLabel(labelPictureCustomer, "labelPictureCustomer.png");
    }

    private void setCustomer() {
        jTableCustomer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRowIndex = jTableCustomer.getSelectedRow();
                showInfo();
            }
        });
    }

    private void showInfo() {
        ctmBUS.setEnabled(false);
        textFieldCustomerID.setText(jTableCustomer.getValueAt(selectedRowIndex, 0).toString());
        textFieldCustomerFirstname.setText(jTableCustomer.getValueAt(selectedRowIndex, 1).toString());
        textFieldCustomerLastname.setText(jTableCustomer.getValueAt(selectedRowIndex, 2).toString());
        textFieldCustomerAddress.setText(jTableCustomer.getValueAt(selectedRowIndex, 3).toString());
        textFieldCustomerPhone.setText(jTableCustomer.getValueAt(selectedRowIndex, 4).toString());
    }

    public void showList(ArrayList<Customer> list) {
        DefaultTableModel customerTable = (DefaultTableModel) jTableCustomer.getModel();
        customerTable.setRowCount(0);
        if (list == null) {
            return;
        }
        for (Customer ctmDTO : list) {
            customerTable.addRow(new Object[]{ctmDTO.getCustomerId(), ctmDTO.getCustomerFirstname(), ctmDTO.getCustomerLastname(), ctmDTO.getCustomerAddress(), ctmDTO.getCustomerPhone()});
        }
    }


    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        textFieldCustomerFirstname = new javax.swing.JTextField();
        textFieldCustomerAddress = new javax.swing.JTextField();
        textFieldCustomerPhone = new javax.swing.JTextField();
        textFieldCustomerLastname = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        textFieldCustomerID = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        buttonAdd = new javax.swing.JButton();
        buttonFix = new javax.swing.JButton();
        buttonShowAll = new javax.swing.JButton();
        labelPictureCustomer = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        textFieldSearch = new javax.swing.JTextField();
        buttonSearch = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        scrollPane2 = new java.awt.ScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableCustomer = new javax.swing.JTable();

        jLabel3.setText("Họ khách hàng");

        jLabel4.setText("Địa chỉ");

        jLabel5.setText("Số điện thoại");

        jLabel6.setText("Tên khách hàng");

        textFieldCustomerFirstname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCustomerFirstnameActionPerformed(evt);
            }
        });
        textFieldCustomerFirstname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldCustomerFirstnameKeyReleased(evt);
            }
        });

        textFieldCustomerAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCustomerAddressActionPerformed(evt);
            }
        });
        textFieldCustomerAddress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldCustomerAddressKeyReleased(evt);
            }
        });

        textFieldCustomerPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldCustomerPhoneFocusLost(evt);
            }
        });
        textFieldCustomerPhone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCustomerPhoneActionPerformed(evt);
            }
        });
        textFieldCustomerPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldCustomerPhoneKeyReleased(evt);
            }
        });

        textFieldCustomerLastname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldCustomerLastnameActionPerformed(evt);
            }
        });
        textFieldCustomerLastname.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldCustomerLastnameKeyReleased(evt);
            }
        });

        jLabel1.setText("Mã khách hàng");

        textFieldCustomerID.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldCustomerFirstname, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                    .addComponent(textFieldCustomerAddress)
                    .addComponent(textFieldCustomerPhone)
                    .addComponent(textFieldCustomerLastname)
                    .addComponent(textFieldCustomerID)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textFieldCustomerID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldCustomerFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldCustomerLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(textFieldCustomerAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(textFieldCustomerPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
        );

        buttonAdd.setForeground(new java.awt.Color(0, 0, 0));
        buttonAdd.setText("Thêm");
        buttonAdd.setBorderPainted(false);

        buttonFix.setForeground(new java.awt.Color(0, 0, 0));
        buttonFix.setText("Sửa");
        buttonFix.setBorderPainted(false);

        buttonShowAll.setForeground(new java.awt.Color(0, 0, 0));
        buttonShowAll.setText("Làm mới");
        buttonShowAll.setBorderPainted(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(buttonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonFix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonFix, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Tìm kiếm");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 0, -1, -1));

        textFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldSearchActionPerformed(evt);
            }
        });
        jPanel3.add(textFieldSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, 710, 30));

        buttonSearch.setForeground(new java.awt.Color(0, 0, 0));
        buttonSearch.setText("Tìm kiếm");
        buttonSearch.setBorderPainted(false);
        buttonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSearchActionPerformed(evt);
            }
        });
        jPanel3.add(buttonSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 20, 150, 46));

        jLabel12.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Quản lý khách hàng");

        jTableCustomer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Mã khách hàng", "Họ", "Tên", "Địa chỉ", "Số điện thoại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableCustomerKeyPressed(evt);
            }
        });
        jScrollPane6.setViewportView(jTableCustomer);

        scrollPane2.add(jScrollPane6);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 960, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelPictureCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(labelPictureCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldCustomerFirstnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCustomerFirstnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldCustomerFirstnameActionPerformed

    private void textFieldCustomerAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCustomerAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldCustomerAddressActionPerformed

    private void textFieldCustomerPhoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCustomerPhoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldCustomerPhoneActionPerformed

    private void textFieldCustomerLastnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldCustomerLastnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldCustomerLastnameActionPerformed

    private void textFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldSearchActionPerformed

    private void buttonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonSearchActionPerformed

    private void textFieldCustomerFirstnameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldCustomerFirstnameKeyReleased
        String firstname = textFieldCustomerFirstname.getText().trim();

        if (firstname.isEmpty()) {
            return;
        }

        if (!Tool.isName(firstname)) {
            flat = true;
            JOptionPane.showMessageDialog(this, "Họ của khách hàng không hợp lệ");
            textFieldCustomerFirstname.setText("");
            textFieldCustomerFirstname.requestFocus();
            flat = false;
        }
    }//GEN-LAST:event_textFieldCustomerFirstnameKeyReleased

    private void textFieldCustomerLastnameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldCustomerLastnameKeyReleased
        String lastname = textFieldCustomerLastname.getText().trim();

        if (lastname.isEmpty()) {
            return;
        }

        if (!Tool.isName(lastname)) {
            flat = true;
            JOptionPane.showMessageDialog(this, "Tên của khách hàng không hợp lệ");
            textFieldCustomerLastname.setText("");
            textFieldCustomerLastname.requestFocus();
            flat = false;
        }
    }//GEN-LAST:event_textFieldCustomerLastnameKeyReleased

    private void textFieldCustomerAddressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldCustomerAddressKeyReleased
        String address = textFieldCustomerAddress.getText().trim();

        if (address.isEmpty()) {
            return;
        }

        if (!Tool.isAdress(address)) {
            flat = true;
            JOptionPane.showMessageDialog(this, "Địa chỉ của khách hàng không hợp lệ");
            textFieldCustomerAddress.setText("");
            textFieldCustomerAddress.requestFocus();
            flat = false;
        }
    }//GEN-LAST:event_textFieldCustomerAddressKeyReleased

    private void textFieldCustomerPhoneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldCustomerPhoneKeyReleased

    }//GEN-LAST:event_textFieldCustomerPhoneKeyReleased

    private void textFieldCustomerPhoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldCustomerPhoneFocusLost
        String phone = textFieldCustomerPhone.getText().trim();

        if (phone.isEmpty()) {
            return;
        }

        if (!Tool.checkPhone(phone)) {
            flat = true;
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số điện thoại của khách hàng.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            flat = false;
            textFieldCustomerPhone.setText("");
            textFieldCustomerPhone.requestFocus();
        } else if (!listCtm.checkPhoneExist(phone)) {
            flat = true;
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được sử dụng.", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            flat = false;
            textFieldCustomerPhone.setText("");
            textFieldCustomerPhone.requestFocus();
        }

    }//GEN-LAST:event_textFieldCustomerPhoneFocusLost

    private void jTableCustomerKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableCustomerKeyPressed
        selectedRowIndex = jTableCustomer.getSelectedRow();
        int rowCount = jTableCustomer.getRowCount();

        switch (evt.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                if (selectedRowIndex > 0) {
                    jTableCustomer.changeSelection(selectedRowIndex--, 0, false, false);
                } else {
                }
            }
            case KeyEvent.VK_DOWN -> {
                if (selectedRowIndex == rowCount - 1) {
                } else {
                    jTableCustomer.changeSelection(selectedRowIndex++, 0, false, false);
                }
            }
        }
        showInfo();
    }//GEN-LAST:event_jTableCustomerKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonFix;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JButton buttonShowAll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTableCustomer;
    private javax.swing.JLabel labelPictureCustomer;
    private java.awt.ScrollPane scrollPane2;
    private javax.swing.JTextField textFieldCustomerAddress;
    private javax.swing.JTextField textFieldCustomerFirstname;
    private javax.swing.JTextField textFieldCustomerID;
    private javax.swing.JTextField textFieldCustomerLastname;
    private javax.swing.JTextField textFieldCustomerPhone;
    private javax.swing.JTextField textFieldSearch;
    // End of variables declaration//GEN-END:variables
}
