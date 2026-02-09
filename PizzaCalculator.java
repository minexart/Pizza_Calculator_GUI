import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple pizza calculator GUI application.
 *
 * This program allows the user to enter the number of people and select
 * a hunger level (light, medium or ravenous). It then calculates how
 * many pizzas are needed for each selected pizza type based on the
 * number of slices per pizza and displays the results in a table. The
 * user can add multiple pizza types to a single order, see the running
 * total cost and save the order history to a text file.
 */
public class PizzaCalculator extends JFrame implements ActionListener {

    /**
     * Represents a pizza option with a name, number of slices and price.
     */
    private static class Pizza {
        private final String name;
        private final int slices;
        private final double price;

        public Pizza(String name, int slices, double price) {
            this.name = name;
            this.slices = slices;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("%s (%d slices, RM %.2f)", name, slices, price);
        }
    }

    // Array of available pizzas
    private final Pizza[] pizzas = new Pizza[] {
            new Pizza("Margherita", 8, 18.00),
            new Pizza("Pepperoni", 8, 20.00),
            new Pizza("Hawaiian", 8, 21.00),
            new Pizza("BBQ Chicken", 8, 23.50),
            new Pizza("Veggie Delight", 8, 19.50),
            new Pizza("Meat Lovers", 10, 24.00),
            new Pizza("Seafood", 10, 26.00),
            new Pizza("Supreme Deluxe", 12, 28.50)
    };

    // GUI components
    private final JTextField tfPeople = new JTextField(5);
    private final JRadioButton rbLight = new JRadioButton("Light (1 slice/person)", true);
    private final JRadioButton rbMedium = new JRadioButton("Medium (2 slices/person)");
    private final JRadioButton rbRavenous = new JRadioButton("Ravenous (4 slices/person)");
    private final ButtonGroup hungerGroup = new ButtonGroup();
    private final JComboBox<Pizza> cbPizza = new JComboBox<>();
    private final JButton btnAdd = new JButton("Add to Order");
    private final JButton btnSave = new JButton("Save Order & New");
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel lblTotal = new JLabel("Total: RM 0.00");

    /**
     * Constructs the pizza calculator GUI.
     */
    public PizzaCalculator() {
        super("Pizza Calculator");

        // Initialize hunger level radio buttons
        hungerGroup.add(rbLight);
        hungerGroup.add(rbMedium);
        hungerGroup.add(rbRavenous);

        // Initialize pizza combo box
        for (int i = 0; i < pizzas.length; i++) {
            cbPizza.addItem(pizzas[i]);
        }

        // Setup table model and table
        String[] columnNames = {
                "Pizza",
                "Slices per Pizza",
                "Price (RM)",
                "People",
                "Hunger Level",
                "Slices Needed",
                "Pizzas Needed",
                "Cost (RM)"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make table non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Adjust column widths for readability
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Layout panels
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        // First row: number of people
        gc.gridx = 0;
        gc.gridy = 0;
        inputPanel.add(new JLabel("Number of people:"), gc);

        gc.gridx = 1;
        inputPanel.add(tfPeople, gc);

        // Second row: hunger level radio buttons
        gc.gridx = 0;
        gc.gridy = 1;
        inputPanel.add(new JLabel("Hunger level:"), gc);

        gc.gridx = 1;
        JPanel hungerPanel = new JPanel();
        hungerPanel.add(rbLight);
        hungerPanel.add(rbMedium);
        hungerPanel.add(rbRavenous);
        inputPanel.add(hungerPanel, gc);

        // Third row: pizza selection
        gc.gridx = 0;
        gc.gridy = 2;
        inputPanel.add(new JLabel("Select pizza:"), gc);

        gc.gridx = 1;
        inputPanel.add(cbPizza, gc);

        // Fourth row: Add button
        gc.gridx = 1;
        gc.gridy = 3;
        inputPanel.add(btnAdd, gc);

        // Add action listeners
        btnAdd.addActionListener(this);
        btnSave.addActionListener(this);

        // Setup bottom panel for total and save button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(lblTotal, BorderLayout.WEST);
        bottomPanel.add(btnSave, BorderLayout.EAST);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
    }

    /**
     * Handles button clicks for adding pizza to the order or saving the order.
     *
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnAdd) {
            addPizzaToOrder();
        } else if (source == btnSave) {
            saveOrderToFile();
        }
    }

    /**
     * Validates inputs and calculates the number of pizzas needed for the
     * selected pizza type. Adds a row to the table and updates the total.
     */
    private void addPizzaToOrder() {
        String peopleStr = tfPeople.getText().trim();

        if (peopleStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the number of people.");
            return;
        }

        int people;

        try {
            people = Integer.parseInt(peopleStr);
            if (people <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Number of people must be a positive integer.");
            return;
        }

        // Determine slices per person based on hunger level
        int slicesPerPerson;
        String hungerLabel;

        if (rbLight.isSelected()) {
            slicesPerPerson = 1;
            hungerLabel = "Light";
        } else if (rbMedium.isSelected()) {
            slicesPerPerson = 2;
            hungerLabel = "Medium";
        } else {
            slicesPerPerson = 4;
            hungerLabel = "Ravenous";
        }

        Pizza selectedPizza = (Pizza) cbPizza.getSelectedItem();

        if (selectedPizza == null) {
            JOptionPane.showMessageDialog(this, "Please select a pizza.");
            return;
        }

        int totalSlicesNeeded = people * slicesPerPerson;
        int pizzasNeeded = (int) Math.ceil((double) totalSlicesNeeded / selectedPizza.slices);
        double cost = pizzasNeeded * selectedPizza.price;

        // Add row to table
        Object[] row = {
                selectedPizza.name,
                selectedPizza.slices,
                String.format("%.2f", selectedPizza.price),
                people,
                hungerLabel,
                totalSlicesNeeded,
                pizzasNeeded,
                String.format("%.2f", cost)
        };

        tableModel.addRow(row);

        // Update total cost
        updateTotalCost();
    }

    /**
     * Updates the total cost label based on the current rows in the table.
     */
    private void updateTotalCost() {
        double total = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String costStr = tableModel.getValueAt(i, 7).toString();
            try {
                total += Double.parseDouble(costStr);
            } catch (NumberFormatException e) {
                // ignore invalid numbers
            }
        }

        lblTotal.setText(String.format("Total: RM %.2f", total));
    }

    /**
     * Saves the current order to a text file named {@code order_history.txt}.
     * After saving, the table is cleared and the form is reset.
     */
    private void saveOrderToFile() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items in the order to save.");
            return;
        }

        try (FileWriter writer = new FileWriter("order_history.txt", true)) {
            // Write timestamp
            DateTimeFormatter dtf =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            writer.write("Order placed at: "
                    + LocalDateTime.now().format(dtf)
                    + System.lineSeparator());

            // Write table header
            writer.write("Pizza\tSlices per Pizza\tPrice (RM)\tPeople\tHunger Level\tSlices Needed\tPizzas Needed\tCost (RM)"
                    + System.lineSeparator());

            // Write each row
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object value = tableModel.getValueAt(i, j);
                    writer.write(value + "\t");
                }
                writer.write(System.lineSeparator());
            }

            // Write total
            writer.write(lblTotal.getText() + System.lineSeparator());
            writer.write(System.lineSeparator());

            JOptionPane.showMessageDialog(this, "Order saved to order_history.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage());
        }

        // Reset table and inputs for a new order
        tableModel.setRowCount(0);
        lblTotal.setText("Total: RM 0.00");
        tfPeople.setText("");
        rbLight.setSelected(true);
        cbPizza.setSelectedIndex(0);
    }

    /**
     * Main entry point. Creates and shows the GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PizzaCalculator app = new PizzaCalculator();
            app.setVisible(true);
        });
    }
}
