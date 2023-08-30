import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminPage extends JPanel {
    private DefaultListModel<String> productModel;
    private JList<String> productList;
    private JTextField idField;
    private JTextField nameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;

    private Connection connection;

    public AdminPage(final JPanel cards, final CardLayout c1) {
        //setTitle("CRUD App");
        this.setBackground(new Color(255,255,204));
        this.setLayout(new GridLayout(2,2,10,10));
        this.setSize(200, 400);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);
        //this.setMaximumSize(new Dimension(10,10));

        productModel = new DefaultListModel<>();
        productList = new JList<>(productModel);

        JScrollPane scrollPane = new JScrollPane(productList);
        this.add(scrollPane, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(8, 4)); // Updated for the ID field
        idField = new JTextField(); // New field for product ID
        nameField = new JTextField();
        nameField.setSize(4,5);
        quantityField = new JTextField();
        priceField = new JTextField();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");


        productList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int J=productList.getSelectedIndex();
                String dbUrl = "jdbc:mysql://localhost:3306/item";
                String dbUser = "root";
                String dbPassword = "chankaying0222";
                try {
                    Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM item");
                    int count=0;
                    while (resultSet.next()) {
                        int id = Integer.parseInt(resultSet.getString("id"));
                        String name = resultSet.getString("name");
                        int quantity = Integer.parseInt(resultSet.getString("quantity"));
                        double price = Double.parseDouble(resultSet.getString("price"));
                        if(count==J){
                            idField.setText(id+"");
                            nameField.setText(name);
                            quantityField.setText(quantity+"");
                            priceField.setText(price+"");
                            break;
                        }
                        count++;
                    }

                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        inputPanel.add(new JLabel("Product ID:")); // Added for the ID field
        inputPanel.add(idField); // Added for the ID field
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Product Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Product Price:"));
        inputPanel.add(priceField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        this.add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        initializeDatabase();
        loadProductsFromDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/item", "root", "chankaying0222");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProductsFromDatabase() {
        productModel.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM item");

            while (resultSet.next()) {
                String productId = resultSet.getString("id");
                String productName = resultSet.getString("name");
                int productQuantity = resultSet.getInt("quantity");
                double productPrice = resultSet.getDouble("price");
                productModel.addElement("ID: " + productId + " " + productName + " - Quantity: " + productQuantity + ", Price: RM" + productPrice);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        String id = idField.getText(); // Get ID from the text field
        String name = nameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO item (id, name, quantity, price) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setDouble(4, price);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            idField.setText("");
            nameField.setText("");
            quantityField.setText("");
            priceField.setText("");
            loadProductsFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        int selectedIndex = productList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }

        String newName = nameField.getText();
        int newQuantity = Integer.parseInt(quantityField.getText());
        double newPrice = Double.parseDouble(priceField.getText());
        int newID = Integer.parseInt(idField.getText());

        try {
            String selectedProduct = productList.getSelectedValue();
            String productName = selectedProduct.split(" - ")[0];

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE item SET quantity = ?, price = ?, name = ? WHERE id = ?");
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setDouble(2, newPrice);
            preparedStatement.setString(3, newName);
            preparedStatement.setInt(4, newID);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            nameField.setText("");
            quantityField.setText("");
            priceField.setText("");
            loadProductsFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteProduct() {
        int selectedIndex = productList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }
        int J=productList.getSelectedIndex();
        String dbUrl = "jdbc:mysql://localhost:3306/item";
        String dbUser = "root";
        String dbPassword = "chankaying0222";
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM item");
            int count=0;
            while (resultSet.next()) {
                int id = Integer.parseInt(resultSet.getString("id"));
                if(count==J){
                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM item WHERE id = ?");
                    preparedStatement.setString(1, ""+id);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();

                    loadProductsFromDatabase();
                    break;
                }
                count++;
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //////////////////////////////////////////
        /*
        try {
            String selectedProduct = productList.getSelectedValue();
            String productName = selectedProduct.split(" - ")[0];
            System.out.println(productName);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM item WHERE id = ?");
            preparedStatement.setString(1, productName);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            loadProductsFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
         */
    }
}
