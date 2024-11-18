package Budget;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Stack;

public class BudgetBase extends JPanel {

    JFrame topLevelFrame;
    GridBagConstraints layoutConstraints = new GridBagConstraints();

    ;
    private JButton undoButton;
    private JButton exitButton;

    private JTextField wagesField;
    private JComboBox<String> wagesComboBox;

    private JTextField loansField;
    private JComboBox<String> loansComboBox;

    private JTextField investmentsField;
    private JComboBox<String> investmentsComboBox;

    private JTextField foodField;
    private JComboBox<String> foodComboBox;

    private JTextField rentField;
    private JComboBox<String> rentComboBox;

    private JTextField insuranceField;
    private JComboBox<String> insuranceComboBox;

    private JTextField totalIncomeField;
    private JTextField totalSpendingField;
    private JTextField totalDifferenceField;

    private Stack<State> stateHistory;
    private State lastSavedState;

    private Timer validationTimer;

    public BudgetBase(JFrame frame) {
        topLevelFrame = frame;
        setLayout(new GridBagLayout());
        stateHistory = new Stack<>();
        initComponents();
    }

    private void initComponents() {
        String[] comboBoxOptions = {"Per Week", "Per Month", "Per Year"};

        JLabel incomeLabel = new JLabel("INCOME");
        addComponent(incomeLabel, 0, 0);

        wagesField = createInputRow("Wages", 1, 0, comboBoxOptions, ref -> wagesComboBox = ref);
        loansField = createInputRow("Loans", 2, 0, comboBoxOptions, ref -> loansComboBox = ref);
        investmentsField = createInputRow("Investments", 3, 0, comboBoxOptions, ref -> investmentsComboBox = ref);

        JLabel totalIncomeLabel = new JLabel("Total Income");
        addComponent(totalIncomeLabel, 4, 0);

        totalIncomeField = new JTextField("0", 10);
        totalIncomeField.setHorizontalAlignment(JTextField.RIGHT);
        totalIncomeField.setEditable(false);
        addComponent(totalIncomeField, 4, 1);

        JLabel spendingLabel = new JLabel("SPENDING");
        addComponent(spendingLabel, 0, 3);

        foodField = createInputRow("Food", 1, 3, comboBoxOptions, ref -> foodComboBox = ref);
        rentField = createInputRow("Rent", 2, 3, comboBoxOptions, ref -> rentComboBox = ref);
        insuranceField = createInputRow("Insurance", 3, 3, comboBoxOptions, ref -> insuranceComboBox = ref);

        JLabel totalSpendingLabel = new JLabel("Total Spending");
        addComponent(totalSpendingLabel, 4, 3);

        totalSpendingField = new JTextField("0", 10);
        totalSpendingField.setHorizontalAlignment(JTextField.RIGHT);
        totalSpendingField.setEditable(false);
        addComponent(totalSpendingField, 4, 4);

        JLabel totalDifferenceLabel = new JLabel("Total Difference");
        addComponent(totalDifferenceLabel, 5, 0);

        totalDifferenceField = new JTextField("0", 10);
        totalDifferenceField.setHorizontalAlignment(JTextField.RIGHT);
        totalDifferenceField.setEditable(false);
        addComponent(totalDifferenceField, 5, 1);

        undoButton = new JButton("Undo");
        addComponent(undoButton, 6, 3);

        exitButton = new JButton("Exit");
        addComponent(exitButton, 7, 0);

        initListeners();
    }

    private JTextField createInputRow(String label, int row, int colOffset, String[] comboBoxOptions, java.util.function.Consumer<JComboBox<String>> comboBoxSetter) {
        JLabel fieldLabel = new JLabel(label);
        addComponent(fieldLabel, row, colOffset);

        JTextField textField = new JTextField("", 10);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setName(label); // Set field name for better error messages
        addComponent(textField, row, colOffset + 1);

        JComboBox<String> comboBox = new JComboBox<>(comboBoxOptions);
        addComponent(comboBox, row, colOffset + 2);

        comboBoxSetter.accept(comboBox);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer = new Timer(300, e -> {
                saveState();
                calculateAll();
            });

            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }

            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }

            public void changedUpdate(DocumentEvent e) {
                timer.restart();
            }
        });

        comboBox.addActionListener(e -> {
            saveState();
            calculateAll();
        });

        return textField;
    }

    private void initListeners() {
        

        undoButton.addActionListener(e -> undo());

        exitButton.addActionListener(e -> System.exit(0));
    }

    private void addComponent(Component component, int gridrow, int gridcol) {
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = gridcol;
        layoutConstraints.gridy = gridrow;
        add(component, layoutConstraints);
    }

    private void saveState() {
        State currentState = new State(this);
        if (lastSavedState == null || !lastSavedState.equals(currentState)) {
            stateHistory.push(currentState);
            lastSavedState = currentState;
        }
    }

    private void undo() {
        if (!stateHistory.isEmpty()) {
            stateHistory.pop(); // Remove current state
            if (!stateHistory.isEmpty()) {
                State previousState = stateHistory.peek(); // Peek the next state to restore
                previousState.restore(this);
                lastSavedState = previousState;
                calculateAll();
            } else {
                JOptionPane.showMessageDialog(this, "No more undo steps available.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No more undo steps available.");
        }
    }

    private void calculateAll() {
        boolean[] errorShownFlag = {false};
        double totalIncome = calculateTotalIncome(errorShownFlag);
        double totalSpending = calculateTotalSpending(errorShownFlag);
        calculateTotalDifference(totalIncome, totalSpending);
    }

    private double calculateTotalIncome(boolean[] errorShownFlag) {
        double wages = convertToYearly(getTextFieldValue(wagesField, errorShownFlag), wagesComboBox);
        double loans = convertToYearly(getTextFieldValue(loansField, errorShownFlag), loansComboBox);
        double investments = convertToYearly(getTextFieldValue(investmentsField, errorShownFlag), investmentsComboBox);

        double totalIncome = wages + loans + investments;
        totalIncomeField.setText(String.format("%.2f", totalIncome));
        return totalIncome;
    }

    private double calculateTotalSpending(boolean[] errorShownFlag) {
        double food = convertToYearly(getTextFieldValue(foodField, errorShownFlag), foodComboBox);
        double rent = convertToYearly(getTextFieldValue(rentField, errorShownFlag), rentComboBox);
        double insurance = convertToYearly(getTextFieldValue(insuranceField, errorShownFlag), insuranceComboBox);

        double totalSpending = food + rent + insurance;
        totalSpendingField.setText(String.format("%.2f", totalSpending));
        return totalSpending;
    }

    private void calculateTotalDifference(double totalIncome, double totalSpending) {
        double totalDifference = totalIncome - totalSpending;
        totalDifferenceField.setText(String.format("%.2f", totalDifference));
        totalDifferenceField.setForeground(totalDifference < 0 ? Color.RED : Color.BLACK);
    }

    private final HashMap<JTextField, Timer> errorTimers = new HashMap<>();

private double getTextFieldValue(JTextField field, boolean[] globalErrorFlag) {
    String text = field.getText();

    // If text is blank, reset to 0 immediately
    if (text.isBlank()) {
        field.setText("");
        cancelErrorTimer(field); // Cancel any active error timer
        return 0;
    }

    try {
        // Try parsing the input
        double value = Double.parseDouble(text);
        cancelErrorTimer(field); // Cancel any active error timer
        return value;
    } catch (NumberFormatException e) {
        // Start or restart the error timer for this field
        startErrorTimer(field);
        globalErrorFlag[0] = true; // Mark global error flag
        field.setText(""); // Reset field to 0
        return 0;
    }
}

private void startErrorTimer(JTextField field) {
    // Cancel any existing timer for this field
    cancelErrorTimer(field);

    // Create a new timer for debounced error message display
    Timer timer = new Timer(300, e -> {
        JOptionPane.showMessageDialog(this, "Invalid input in " + field.getName() + ". Resetting to 0.");
        cancelErrorTimer(field); // Stop the timer after the message
    });
    timer.setRepeats(false); // Ensure it only runs once
    timer.start();

    // Store the timer in the map
    errorTimers.put(field, timer);
}

private void cancelErrorTimer(JTextField field) {
    // Cancel and remove the timer if it exists
    Timer timer = errorTimers.get(field);
    if (timer != null) {
        timer.stop();
        errorTimers.remove(field);
    }
}


    

    private double convertToYearly(double value, JComboBox<String> comboBox) {
        if (Double.isNaN(value) || comboBox == null) return 0;
        String period = comboBox.getSelectedItem() != null ? comboBox.getSelectedItem().toString() : "Per Year";
        switch (period) {
            case "Per Week":
                return value * 52;
            case "Per Month":
                return value * 12;
            default:
                return value;
        }
    }

    private static class State {
        String wages, loans, investments, food, rent, insurance;
        String wagesFreq, loansFreq, investmentsFreq, foodFreq, rentFreq, insuranceFreq;

        State(BudgetBase base) {
            this.wages = base.wagesField.getText();
            this.loans = base.loansField.getText();
            this.investments = base.investmentsField.getText();
            this.food = base.foodField.getText();
            this.rent = base.rentField.getText();
            this.insurance = base.insuranceField.getText();
            this.wagesFreq = (String) base.wagesComboBox.getSelectedItem();
            this.loansFreq = (String) base.loansComboBox.getSelectedItem();
            this.investmentsFreq = (String) base.investmentsComboBox.getSelectedItem();
            this.foodFreq = (String) base.foodComboBox.getSelectedItem();
            this.rentFreq = (String) base.rentComboBox.getSelectedItem();
            this.insuranceFreq = (String) base.insuranceComboBox.getSelectedItem();
        }

        void restore(BudgetBase base) {
            base.wagesField.setText(wages);
            base.loansField.setText(loans);
            base.investmentsField.setText(investments);
            base.foodField.setText(food);
            base.rentField.setText(rent);
            base.insuranceField.setText(insurance);
            base.wagesComboBox.setSelectedItem(wagesFreq);
            base.loansComboBox.setSelectedItem(loansFreq);
            base.investmentsComboBox.setSelectedItem(investmentsFreq);
            base.foodComboBox.setSelectedItem(foodFreq);
            base.rentComboBox.setSelectedItem(rentFreq);
            base.insuranceComboBox.setSelectedItem(insuranceFreq);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof State)) return false;
            State other = (State) obj;
            return wages.equals(other.wages)
                    && loans.equals(other.loans)
                    && investments.equals(other.investments)
                    && food.equals(other.food)
                    && rent.equals(other.rent)
                    && insurance.equals(other.insurance)
                    && wagesFreq.equals(other.wagesFreq)
                    && loansFreq.equals(other.loansFreq)
                    && investmentsFreq.equals(other.investmentsFreq)
                    && foodFreq.equals(other.foodFreq)
                    && rentFreq.equals(other.rentFreq)
                    && insuranceFreq.equals(other.insuranceFreq);
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Budget Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BudgetBase newContentPane = new BudgetBase(frame);
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BudgetBase::createAndShowGUI);
    }
}
