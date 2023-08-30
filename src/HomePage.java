import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomePage extends JPanel {
    private Connection connection;
    private JPanel itemPanel;
    private JButton calculateButton;
    private JLabel totalPriceLabel;
    private JTextArea selectedItemTextArea;
    private JPanel cartField;
    private double totalPrice = 0.0;
    private JTextField searchField;
    private JButton searchButton;
    private JButton buyButton;
    ArrayList<String> CartText=new ArrayList<String>();

    public HomePage(final JPanel cards, final CardLayout c1) {
        this.setBackground(new Color(255,255,204));
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
        itemScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(itemScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(calculateButton);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        totalPanel.add(totalPriceLabel);
        rightPanel.add(totalPanel, BorderLayout.NORTH);

        JScrollPane textScrollPane = new JScrollPane(cartField);
        textScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(textScrollPane, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(300,80));

        this.add(rightPanel, BorderLayout.EAST);
        buyButton = new JButton("Total");
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileWriter myWriter = new FileWriter("Receipt.txt");
                    for(int m=0;m<CartText.size();m++){
                        myWriter.write(CartText.get(m)+"\n");
                    }
                    myWriter.close();
                    System.out.println("Receipt Printed");
                } catch (IOException var6) {
                    System.out.println("Cannot Print");
                    var6.printStackTrace();
                }

            }
        });
        //buyButton.setSize(leftPanel.getSize().width,2);
        rightPanel.add(buyButton, BorderLayout.SOUTH);

        //this.add(calculateButton, BorderLayout.SOUTH);

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
        itemPanel.setLayout(new GridLayout(0, 5, 8, 8));
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
        totalPriceLabel = new JLabel("Cart                   \n Total Price: RM0.00");


        this.add(totalPriceLabel, BorderLayout.NORTH);
    }

    private void createSelectedItemTextArea() {
        cartField = new JPanel();
        cartField.setLayout(new GridLayout(0,1,5,5));
        this.add(cartField, BorderLayout.EAST);
        //selectedItemTextArea = new JTextArea(10, 30);
        //selectedItemTextArea.setEditable(false);

        //JScrollPane scrollPane = new JScrollPane(selectedItemTextArea);
        //this.add(scrollPane, BorderLayout.EAST);
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
                JLabel priceLabel = new JLabel("Price: RM" + String.format("%.2f", productPrice));

                SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, productQuantity, 1);
                JSpinner spinner = new JSpinner(spinnerModel);
                ImageIcon IMAGE = new ImageIcon((new ImageIcon(productName+".png")).getImage().getScaledInstance(80, 80, 1));
                JLabel picture = new JLabel(IMAGE);
                itemPanel.add(picture);
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

    private void createItemGroup(String productName,int productQuantity,double productPrice){

        JLabel nameLabel = new JLabel("Name: " + productName);
        JLabel priceLabel = new JLabel("Price: RM" + String.format("%.2f", productPrice));

        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, productQuantity, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        ImageIcon IMAGE = new ImageIcon((new ImageIcon(productName+".png")).getImage().getScaledInstance(80, 80, 1));
        JLabel picture = new JLabel(IMAGE);
        itemPanel.add(picture);
        itemPanel.add(nameLabel);
        itemPanel.add(priceLabel);
        itemPanel.add(new JLabel("Select Quantity: "+productQuantity ));
        itemPanel.add(spinner);
        for(int i=0;i<CartText.size();i++){
            if(productName.equals(CartText.get(i).replace("Name: ",""))){
                System.out.println("HELLO");
                spinner.setValue(Integer.parseInt(CartText.get(i+2)));
            }
        }
        /*
        JPanel IPanel= new JPanel(new BorderLayout());
        JPanel TPanel = new JPanel(new BorderLayout());
        JPanel TPanel2 = new JPanel();
        JLabel nameLabel = new JLabel("Name: " + productName);
        JLabel priceLabel = new JLabel("Price: $" + String.format("%.2f", productPrice));

        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, productQuantity, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        ImageIcon IMAGE = new ImageIcon((new ImageIcon("Shoplogo.png")).getImage().getScaledInstance(80, 80, 1));
        JLabel picture = new JLabel(IMAGE);
        spinner.setPreferredSize(new Dimension(40,40));
        TPanel2.setPreferredSize(new Dimension(100,80));
        TPanel2.add(spinner);
        IPanel.add(picture, BorderLayout.WEST);
        TPanel.add(nameLabel, BorderLayout.NORTH);
        TPanel.add(priceLabel, BorderLayout.CENTER);
        TPanel.add(new JLabel("Select Quantity: " + productQuantity ), BorderLayout.SOUTH);
        IPanel.add(TPanel, BorderLayout.CENTER);
        IPanel.add(TPanel2, BorderLayout.EAST);
        itemPanel.add(IPanel);
        IPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        for(int i=0;i<CartText.size();i++){
            if(productName.equals(CartText.get(i).replace("Name: ",""))){
                System.out.println("HELLO");
                spinner.setValue(Integer.parseInt(CartText.get(i+2)));
            }
        }*/
    }

    private void searchProducts(String searchTerm) {
        int i=0;
        boolean check;
        String a = "";
        String b = "";
        String c = "";
        for(Component jc:itemPanel.getComponents()){
            c="0";
            check=false;
            if(jc instanceof JLabel && i%5==1){
                a = ((JLabel) jc).getText();
                System.out.println(a);
            }
            if(jc instanceof JLabel && i%5==2){
                b = ((JLabel) jc).getText();
                System.out.println(b);
            }
            if(jc instanceof JSpinner) {
                c = ((JSpinner) jc).getValue() + "";
                System.out.println(c);
            }
            if(!c.equals("0")){
                for(int l=0;l<CartText.size();l++){
                    if(a.equals(CartText.get(l))){
                        CartText.set(l+2,c);
                        check=true;
                        break;
                    }
                }
                if (!check){
                    CartText.add(a);CartText.add(b);CartText.add(c);
                }
            }
            i++;
        }
        System.out.println("------------------");
        for(int d=0;d<CartText.size();d++){
            System.out.println(CartText.get(d));
        }



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
                createItemGroup(productName,productQuantity, productPrice);

            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCartGroup(String name, String text){
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new GridLayout(0,2,8,8));
        cartPanel.setPreferredSize(new Dimension(80,100));
        ImageIcon IMAGE = new ImageIcon((new ImageIcon(name+".png")).getImage().getScaledInstance(80, 80, 1));
        JLabel picture = new JLabel(IMAGE);
        JLabel cartlabel = new JLabel("<html>"+text.replace("\n","<br/>")+"<html>");
        cartPanel.add(picture);
        cartPanel.add(cartlabel);
        cartField.add(cartPanel, BorderLayout.CENTER);
        cartPanel.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    private void calculateTotalPrice() {
        int i=0;
        boolean check;
        String a = "";
        String b = "";
        String c = "";
        for(Component jc:itemPanel.getComponents()){
            c="0";
            check=false;
            if(jc instanceof JLabel && i%5==1){
                a = ((JLabel) jc).getText();
                System.out.println(a);
            }
            if(jc instanceof JLabel && i%5==2){
                b = ((JLabel) jc).getText();
                System.out.println(b);
            }
            if(jc instanceof JSpinner) {
                c = ((JSpinner) jc).getValue() + "";
                System.out.println(c);
            }
            if(!c.equals("0")){
                for(int l=0;l<CartText.size();l++){
                    if(a.equals(CartText.get(l))){
                        CartText.set(l+2,c);
                        check=true;
                        break;
                    }
                }
                if (!check){
                    CartText.add(a);CartText.add(b);CartText.add(c);
                }
            }
            i++;
        }
        System.out.println("------------------");
        for(int d=0;d<CartText.size();d++){
            System.out.println(CartText.get(d));
        }

        itemPanel.removeAll();
        itemPanel.revalidate();
        itemPanel.repaint();
        cartField.removeAll();
        cartField.revalidate();
        cartField.repaint();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM item";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                int productQuantity = resultSet.getInt("quantity");
                double productPrice = resultSet.getDouble("price");
                createItemGroup(productName,productQuantity, productPrice);

            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///////////////////////
        totalPrice = 0.0;
        //selectedItemTextArea.setText(""); // Clear previous selections
        String text="";
        double calc=0;
        String name="";
        for(int j=0; j<CartText.size();j++){
            if(j%3==2){
                text=text+"\n"+"Quantity = "+CartText.get(j)+"\n";
                totalPrice+=calc*Integer.parseInt(CartText.get(j));
                createCartGroup(name, text);
                text="";name="";
            } else if (j%3==1) {
                text=text+"\n"+CartText.get(j);
                calc=Double.parseDouble(CartText.get(j).replace("Price: RM",""));
            } else{
                name=CartText.get(j).replace("Name: ","");
                text=text+"\n"+CartText.get(j);
            }
        }
        //selectedItemTextArea.setText(text);
        totalPriceLabel.setText("Total Price: RM" + String.format("%.2f", totalPrice));



        /*
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
                                "Price: RM" + String.format("%.2f", productPrice) + "\n" +
                                "Total Price: RM" + String.format("%.2f", itemTotalPrice) + "\n\n"
                );
            }
        }

        selectedItemTextArea.append("Total Price: RM" + String.format("%.2f", totalPrice) + "\n");
        totalPriceLabel.setText("Total Price: RM" + String.format("%.2f", totalPrice));*/
    }

}