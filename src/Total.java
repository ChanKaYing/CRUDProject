import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Total extends JPanel {
    private Connection connection;
    private JPanel itemPanel;
    private JButton calculateButton;
    private JTextArea selectedItemTextArea;
    private double totalPrice = 0.0;
    private Label totalPriceLabel;

    public Total(final JPanel cards, final CardLayout c1) {
        setLayout(new BorderLayout(10, 10));
        initializeDatabase();
        createItemPanel();
        createCalculateButton();
        createSelectedItemTextArea();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(calculateButton);
        add(bottomPanel, BorderLayout.SOUTH);

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
        add(itemPanel, BorderLayout.CENTER);
    }

    private void createCalculateButton() {
        calculateButton = new JButton("Calculate Total");
    }

    private void createSelectedItemTextArea() {
        selectedItemTextArea = new JTextArea(10, 30);
        selectedItemTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(selectedItemTextArea);
        add(scrollPane, BorderLayout.EAST);
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


        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to purchase?\nTotal Price: $" + String.format("%.2f", totalPrice),
                "Confirm Purchase",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Perform purchase logic here
            JOptionPane.showMessageDialog(null, "Purchase confirmed! Thank you for shopping.");
            selectedItemTextArea.setText("");
            totalPrice = 0.0;
        }
    }

}

