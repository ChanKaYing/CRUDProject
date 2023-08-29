import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JPanel {
    JLabel labelName;
    JLabel labelPassword;
    JButton buttonLogin;
    JButton buttonRegister;
    JTextField textFieldName;
    JPasswordField passwordField1;
    JLabel logoImage;

    private Connection connection;

    public Login(final JPanel cards, final CardLayout c1) {
        this.setBackground(new Color(255,255,204));
        this.labelName = new JLabel("Name: "); // Change labelID to labelName
        this.labelPassword = new JLabel("Password: ");
        this.buttonLogin = new JButton("Login");
        this.buttonRegister = new JButton("Register");
        this.textFieldName = new JTextField(); // Change textFieldID to textFieldName
        this.passwordField1 = new JPasswordField();
        ImageIcon IMAGE = new ImageIcon((new ImageIcon("shoplogo1.png")).getImage().getScaledInstance(500, 400, 1));
        this.logoImage = new JLabel(IMAGE);
        this.buttonLogin.setBounds(460, 565, 100, 40);
        this.buttonRegister.setBounds(590, 565, 100, 40);
        this.labelPassword.setBounds(400, 515, 100, 40);
        this.labelName.setBounds(400, 465, 100, 40); // Change labelID to labelName
        this.textFieldName.setBounds(500, 465, 250, 40); // Change textFieldID to textFieldName
        this.passwordField1.setBounds(500, 515, 250, 40);
        this.logoImage.setBounds(50, 50, 1100, 250);
        this.add(this.logoImage);
        this.add(this.buttonLogin);
        this.add(this.buttonRegister);
        this.add(this.labelName); // Change labelID to labelName
        this.add(this.labelPassword);
        this.add(this.textFieldName); // Change textFieldID to textFieldName
        this.add(this.passwordField1);
        textFieldName.setText("z");
        passwordField1.setText("z");

        this.buttonLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean correct = false;
                String dbUrl = "jdbc:mysql://localhost:3306/item";
                String dbUser = "root";
                String dbPassword = "chankaying0222";

                try {
                    Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM user");

                    while (resultSet.next()) {
                        String name = resultSet.getString("name"); // Change id to name
                        String password = resultSet.getString("password");

                        if (Login.this.textFieldName.getText().equals(name) && // Change textFieldID to textFieldName
                                new String(Login.this.passwordField1.getPassword()).equals(password)) {
                            JOptionPane.showMessageDialog(null, "Login successfully!");
                            correct = true;
                            c1.show(cards, "HomePage");
                            // Update UI and perform actions...
                            break;
                        }
                        else if(Login.this.textFieldName.getText().equals("admin") && // Change textFieldID to textFieldName
                                new String(Login.this.passwordField1.getPassword()).equals("0")){
                            c1.show(cards, "AdminPage");
                        }
                    }

                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                if (!correct) {
                    JOptionPane.showMessageDialog(null, "Name or password is incorrect, please try again."); // Change ID to Name
                }

                Login.this.textFieldName.setText(""); // Change textFieldID to textFieldName
                Login.this.passwordField1.setText("");
            }
        });

        this.buttonRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c1.show(cards,"Register");
                // Here you can implement the logic to switch to a registration panel or perform registration actions
            }
        });

        this.setLayout(null);
        this.setSize(1200, 700);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login Page");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel cards = new JPanel(new CardLayout());
            CardLayout card1 = (CardLayout)cards.getLayout();

            String purchasedString="";
            Login loginPanel = new Login(cards, card1);
            Register registerpanel = new Register(cards, card1);
            HomePage homePagepanel = new HomePage(cards,card1);
            AdminPage adminPagepanel = new AdminPage(cards, card1);
            Total totalPagepanel = new Total(cards, card1);


            cards.add(loginPanel, "Login");
            cards.add(registerpanel, "Register");
            cards.add(homePagepanel,"HomePage");
            cards.add(adminPagepanel, "AdminPage");
            cards.add(totalPagepanel, "Total");


            frame.add(cards);

            //       cards.add(homepanel, "Home");
            homePagepanel.revalidate();
            totalPagepanel.revalidate();



            frame.pack();
            frame.setSize(1200, 700);
            frame.setVisible(true);
        });
    }
}

