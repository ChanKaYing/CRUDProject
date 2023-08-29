import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;

public class HomePage extends JPanel {
    private Connection connection;
    private JPanel itemPanel;
    private JButton calculateButton;
    private JLabel totalPriceLabel;
    private JTextArea selectedItemTextArea;
    private double totalPrice = 0.0;
    private JTextField searchField;
    private JButton searchButton;
    private JButton buyButton;

    public HomePage(final JPanel cards, final CardLayout c1) {
        this.revalidate();
        this.validate();
        this.repaint();
        setSize(800, 600);
        this.setLayout(new BorderLayout(10, 10));
        initializeDatabase();
        createSearchField();
        createSearchButton();
        createItemPanel();
        createCalculateButton();
        createTotalPriceLabel();
        createSelectedItemTextArea();

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        leftPanel.add(topPanel, BorderLayout.NORTH);

        JScrollPane itemScrollPane = new JScrollPane(itemPanel);
        leftPanel.add(itemScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(calculateButton);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JScrollPane textScrollPane = new JScrollPane(selectedItemTextArea);
        rightPanel.add(textScrollPane, BorderLayout.CENTER);

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        totalPanel.add(totalPriceLabel);
        rightPanel.add(totalPanel, BorderLayout.NORTH);

        this.add(rightPanel, BorderLayout.EAST);
        buyButton = new JButton("Total");
        bottomPanel.add(buyButton);
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c1.show(cards,"Total");
            }
        });

        this.add(calculateButton, BorderLayout.SOUTH);

        loadProductsFromDatabase();
    }


    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/item", "root", "chankaying0222");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createItemPanel() {
        itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(0, 2, 8, 8));
        this.add(itemPanel, BorderLayout.CENTER);
    }

    private void createCalculateButton() {
        calculateButton = new JButton("Calculate Total");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotalPrice();
            }
        });

        this.add(calculateButton, BorderLayout.SOUTH);
    }

    private void createTotalPriceLabel() {
        totalPriceLabel = new JLabel("Cart                   \n Total Price: $0.00");


        this.add(totalPriceLabel, BorderLayout.NORTH);
    }

    private void createSelectedItemTextArea() {
        selectedItemTextArea = new JTextArea(10, 30);
        selectedItemTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(selectedItemTextArea);
        this.add(scrollPane, BorderLayout.EAST);
    }

    private void createSearchField() {
        searchField = new JTextField(20);
        this.add(searchField, BorderLayout.NORTH);
    }

    private void createSearchButton() {
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                searchProducts(searchTerm);
            }
        });

        this.add(searchButton, BorderLayout.NORTH);
    }

    private void loadProductsFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM item");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                int productQuantity = resultSet.getInt("quantity");
                double productPrice = resultSet.getDouble("price");

                JLabel nameLabel = new JLabel("Name: " + productName);
                JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", productPrice));

                SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, productQuantity, 1);
                JSpinner spinner = new JSpinner(spinnerModel);

                itemPanel.add(nameLabel);
                itemPanel.add(priceLabel);
                itemPanel.add(new JLabel("Select Quantity: "+productQuantity ));
                itemPanel.add(spinner);

            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchProducts(String searchTerm) {
        itemPanel.removeAll();
        itemPanel.revalidate();
        itemPanel.repaint();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM item WHERE name LIKE '%" + searchTerm + "%'";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                int productQuantity = resultSet.getInt("quantity");
                double productPrice = resultSet.getDouble("price");

                JLabel nameLabel = new JLabel("Name: " + productName);
                JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", productPrice));

                SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, productQuantity, 1);
                JSpinner spinner = new JSpinner(spinnerModel);

                itemPanel.add(nameLabel);
                itemPanel.add(priceLabel);
                itemPanel.add(new JLabel("Select Quantity:"));
                itemPanel.add(spinner);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTotalPrice() {
        totalPrice = 0.0;
        selectedItemTextArea.setText(""); // Clear previous selections
        Component[] components = itemPanel.getComponents();
        for (int i = 0; i < components.length; i += 4) {
            JSpinner spinner = (JSpinner) components[i + 3];
            int selectedQuantity = (int) spinner.getValue();
            if (selectedQuantity > 0) {
                JLabel nameLabel = (JLabel) components[i];
                String itemName = nameLabel.getText().replace("Name: ", "");

                JLabel priceLabel = (JLabel) components[i + 1];
                String priceString = priceLabel.getText().replace("Price: $", "");
                double productPrice = Double.parseDouble(priceString);

                double itemTotalPrice = selectedQuantity * productPrice;
                totalPrice += itemTotalPrice;
                selectedItemTextArea.append(
                        "Item: " + itemName + "\n" +
                                "Quantity: " + selectedQuantity + "\n" +
                                "Price: $" + String.format("%.2f", productPrice) + "\n" +
                                "Total Price: $" + String.format("%.2f", itemTotalPrice) + "\n\n"
                );
            }
        }

        selectedItemTextArea.append("Total Price: $" + String.format("%.2f", totalPrice) + "\n");
        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
    }

}