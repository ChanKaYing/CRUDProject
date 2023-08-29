import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;

public class Register extends JPanel {
    JLabel labelName;
    JLabel labelAddress;
    JLabel labelID;
    JLabel labelPassword;
    JLabel labelConfirmPassword;
    JLabel labelPhoneNumber;
    JButton buttonDone;
    JButton buttonBack;
    JButton buttonClear;
    JTextField textFieldRegisterName;
    JTextField textFieldRegisterAddress;
    JTextField textFieldRegisterID;
    JTextField textFieldPhoneNumber;
    JPasswordField passwordField;
    JPasswordField passwordFieldConfirm;
    JLabel logoImage;
    private Connection connection;

    public Register(final JPanel cards, final CardLayout c1) {
        this.setBackground(new Color(255,255,204));
        this.labelName = new JLabel("Name: ");
        this.labelAddress = new JLabel("Address: ");
        this.labelID = new JLabel("ID: ");
        this.labelPassword = new JLabel("Password: ");
        this.labelConfirmPassword = new JLabel(" Confirm password: ");
        this.labelPhoneNumber = new JLabel("Phone Number: ");
        this.buttonDone = new JButton("Done");
        this.buttonBack = new JButton("Back");
        this.buttonClear = new JButton("Clear");
        this.textFieldRegisterName = new JTextField();
        this.textFieldRegisterAddress = new JTextField();
        this.textFieldRegisterID = new JTextField();
        this.textFieldPhoneNumber = new JTextField();
        this.passwordField = new JPasswordField();
        this.passwordFieldConfirm = new JPasswordField();
        ImageIcon IMAGE = new ImageIcon((new ImageIcon("shoplogo1.png")).getImage().getScaledInstance(500, 400, 1));
        this.logoImage = new JLabel(IMAGE);
        this.logoImage.setBounds(50, 50, 1100, 250);
        this.labelName.setBounds(400, 350, 150, 40);
        this.labelAddress.setBounds(400, 400, 150, 40);
        this.labelID.setBounds(400, 450, 150, 40);
        this.labelPassword.setBounds(400, 500, 150, 40);
        this.labelConfirmPassword.setBounds(400, 550, 150, 40);
        this.labelPhoneNumber.setBounds(400, 600, 150, 40);
        this.textFieldRegisterName.setBounds(550, 350, 250, 40);
        this.textFieldRegisterAddress.setBounds(550, 400, 250, 40);
        this.textFieldRegisterID.setBounds(550, 450, 250, 40);
        this.passwordField.setBounds(550, 500, 250, 40);
        this.passwordFieldConfirm.setBounds(550, 550, 250, 40);
        this.textFieldPhoneNumber.setBounds(550, 600, 250, 40);
        this.buttonDone.setBounds(460, 670, 100, 40);
        this.buttonBack.setBounds(590, 670, 100, 40);
        this.buttonClear.setBounds(720, 670, 100, 40);
        this.add(this.labelName);
        this.add(this.labelPassword);
        this.add(this.labelAddress);
        this.add(this.labelID);
        this.add(this.labelConfirmPassword);
        this.add(this.labelPhoneNumber);
        this.add(this.textFieldRegisterName);
        this.add(this.textFieldRegisterAddress);
        this.add(this.textFieldRegisterID);
        this.add(this.textFieldPhoneNumber);
        this.add(this.passwordField);
        this.add(this.passwordFieldConfirm);
        this.add(this.buttonDone);
        this.add(this.buttonBack);
        this.add(this.logoImage);
        this.add(this.buttonClear);

        this.buttonDone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = "jdbc:mysql://localhost:3306/item";
                String username = "root";
                String password = "chankaying0222";

                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    boolean correct = false;
                    if (!textFieldRegisterName.getText().isEmpty() && !textFieldRegisterAddress.getText().isEmpty() &&
                            !textFieldRegisterID.getText().isEmpty() && !String.valueOf(passwordField.getPassword()).isEmpty() &&
                            !String.valueOf(passwordFieldConfirm.getPassword()).isEmpty() && !textFieldPhoneNumber.getText().isEmpty()) {
                        if (String.valueOf(passwordField.getPassword()).equals(String.valueOf(passwordFieldConfirm.getPassword()))) {
                            String query = "SELECT * FROM user WHERE id = ?";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                preparedStatement.setString(1, textFieldRegisterID.getText());
                                ResultSet resultSet = preparedStatement.executeQuery();
                                if (resultSet.next()) {
                                    correct = true;
                                }

                                if (correct) {
                                    JOptionPane.showMessageDialog(null, "The ID had been used. Please register with another ID.");
                                } else {
                                    String insertQuery = "INSERT INTO user (id, name, password, address, phone) VALUES (?, ?, ?, ?, ?)";
                                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                        insertStatement.setString(1, textFieldRegisterID.getText());
                                        insertStatement.setString(2, textFieldRegisterName.getText());
                                        insertStatement.setString(3, String.valueOf(passwordField.getPassword()));
                                        insertStatement.setString(4, textFieldRegisterAddress.getText());
                                        insertStatement.setString(5, textFieldPhoneNumber.getText());
                                        insertStatement.executeUpdate();

                                        JOptionPane.showMessageDialog(null, "Account created");

                                        // Update UI and other actions
                                        // ...

                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Password and confirmed password are not the same, please try again");
                            passwordField.setText("");
                            passwordFieldConfirm.setText("");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Somewhere is empty, please fill it.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.buttonBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c1.show(cards, "Login");
            }
        });


        this.buttonClear.addActionListener(new ActionListener() {
                                               public void actionPerformed(ActionEvent e) {
                                                   Register.this.textFieldRegisterID.setText("");
                                                   Register.this.textFieldRegisterName.setText("");
                                                   Register.this.textFieldRegisterAddress.setText("");

                                                   Register.this.passwordField.setText("");
                                                   Register.this.passwordFieldConfirm.setText("");
                                                   Register.this.textFieldPhoneNumber.setText("");
                                               }
                                           }

        );
        this.setSize(500, 500);
        this.setLayout((LayoutManager) null);
        this.setVisible(true);

    }
}

