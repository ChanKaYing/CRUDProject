import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CRUDAppWithId extends JFrame {
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

    public CRUDAppWithId() {
        setTitle("CRUD App");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        productModel = new DefaultListModel<>();
        productList = new JList<>(productModel);

        JScrollPane scrollPane = new JScrollPane(productList);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 8));
        idField = new JTextField();
        nameField = new JTextField();
        quantityField = new JTextField();
        priceField = new JTextField();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Product Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Product Price:"));
        inputPanel.add(priceField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        getContentPane().add(inputPanel, BorderLayout.SOUTH);

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
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                int productQuantity = resultSet.getInt("quantity");
                double productPrice = resultSet.getDouble("price");
                productModel.addElement("ID: " + id + ", Name: " + productName + " - Quantity: " + productQuantity + ", Price: RM" + productPrice);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        String idStr = idField.getText().trim();
        String name = nameField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (idStr.isEmpty() || name.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }

        try {
            int id = Integer.parseInt(idStr);
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            if (id < 0 || quantity < 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "ID, quantity, and price must be non-negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID, quantity, or price format.");
            return false;
        }

        return true;
    }

    private void addProduct() {
        if (!validateInput()) {
            return;
        }

        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO item (id, name, quantity, price) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, id);
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

        if (!validateInput()) {
            return;
        }

        int id = Integer.parseInt(idField.getText());
        String newName = nameField.getText();
        int newQuantity = Integer.parseInt(quantityField.getText());
        double newPrice = Double.parseDouble(priceField.getText());

        try {
            String selectedProduct = productList.getSelectedValue();
            String[] parts = selectedProduct.split(", ");
            int productId = Integer.parseInt(parts[0].split(": ")[1]);

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE item SET id = ?, name = ?, quantity = ?, price = ? WHERE id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, newName);
            preparedStatement.setInt(3, newQuantity);
            preparedStatement.setDouble(4, newPrice);
            preparedStatement.setInt(5, productId);
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

    private void deleteProduct() {
        int selectedIndex = productList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }

        try {
            String selectedProduct = productList.getSelectedValue();
            int productId = Integer.parseInt(selectedProduct.split(": ")[1].split(",")[0]);

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM item WHERE id = ?");
            preparedStatement.setInt(1, productId);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            loadProductsFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CRUDAppWithId app = new CRUDAppWithId();
            app.setVisible(true);
        });
    }
}
