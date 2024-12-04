package src.main.java.Budget;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Stack;

public class BudgetBase extends JPanel {

    public JFrame topLevelFrame;
    public GridBagConstraints layoutConstraints = new GridBagConstraints();

    public JButton undoButton;
    public JButton redoButton;
    public JButton exitButton;

    public JTextField wagesField;
    public JComboBox<String> wagesComboBox;

    public JTextField loansField;
    public JComboBox<String> loansComboBox;

    public JTextField investmentsField;
    public JComboBox<String> investmentsComboBox;

    public JTextField foodField;
    public JComboBox<String> foodComboBox;

    public JTextField rentField;
    public JComboBox<String> rentComboBox;

    public JTextField insuranceField;
    public JComboBox<String> insuranceComboBox;

    public JTextField totalIncomeField;
    public JTextField totalSpendingField;
    public JTextField totalDifferenceField;

    public Stack<State> undoStack; // Tracks undo states
    public Stack<State> redoStack; // Tracks redo states
    public boolean isPerformingUndoRedo = false; // Tracks if undo/redo is in progress

    public BudgetBase(JFrame frame) {
        this.topLevelFrame = frame;
        setLayout(new GridBagLayout());
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        initComponents();
        saveState(); // Save the initial state
    }

    public void initComponents() {
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

        redoButton = new JButton("Redo");
        addComponent(redoButton, 6, 4);

        exitButton = new JButton("Exit");
        addComponent(exitButton, 7, 0);

        initListeners();
    }

    public JTextField createInputRow(String label, int row, int colOffset, String[] comboBoxOptions, java.util.function.Consumer<JComboBox<String>> comboBoxSetter) {
        JLabel fieldLabel = new JLabel(label);
        addComponent(fieldLabel, row, colOffset);

        JTextField textField = new JTextField("", 10);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setName(label);
        addComponent(textField, row, colOffset + 1);

        JComboBox<String> comboBox = new JComboBox<>(comboBoxOptions);
        addComponent(comboBox, row, colOffset + 2);
        comboBoxSetter.accept(comboBox);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saveState();
                calculateAll();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saveState();
                calculateAll();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saveState();
                calculateAll();
            }
        });

        comboBox.addActionListener(e -> {
            saveState();
            calculateAll();
        });

        return textField;
    }

     void initListeners() {
        undoButton.addActionListener(e -> {
            System.out.println("[DEBUG] Undo button pressed");
            undo();
            debugStacks();
        });
        redoButton.addActionListener(e -> {
            System.out.println("[DEBUG] Redo button pressed");
            redo();
            debugStacks();
        });
        exitButton.addActionListener(e -> System.exit(0));
    }

    public void addComponent(Component component, int gridrow, int gridcol) {
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = gridcol;
        layoutConstraints.gridy = gridrow;
        add(component, layoutConstraints);
    }

    public void saveState() {
        if (isPerformingUndoRedo) return; // Prevent saving state during undo/redo operations
        State currentState = new State(this);

        // Save to undo stack only if it's a new state
        if (undoStack.isEmpty() || !undoStack.peek().equals(currentState)) {
            undoStack.push(currentState);
            redoStack.clear(); // Clear redo stack on a new action
        }

        System.out.println("[DEBUG] State saved to undo stack");
        debugStacks();
    }

    public void undo() {
        if (undoStack.size() <= 1) {
            JOptionPane.showMessageDialog(this, "No more undo steps available.");
            return;
        }

        isPerformingUndoRedo = true; // Mark undo in progress
        redoStack.push(undoStack.pop()); // Move the current state to redo stack
        State previousState = undoStack.peek(); // Peek the previous state
        previousState.restore(this);
        calculateAll();
        isPerformingUndoRedo = false; // Undo complete

        System.out.println("[DEBUG] Undo performed");
        debugStacks();
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No more redo steps available.");
            return;
        }

        isPerformingUndoRedo = true; // Mark redo in progress
        State nextState = redoStack.pop(); // Get the next state from redo stack
        undoStack.push(nextState); // Save it back to the undo stack
        nextState.restore(this);
        calculateAll();
        isPerformingUndoRedo = false; // Redo complete

        System.out.println("[DEBUG] Redo performed");
        debugStacks();
    }

    public void debugStacks() {
        System.out.println("==== [DEBUG] Undo Stack ====");
        for (int i = undoStack.size() - 1; i >= 0; i--) {
            System.out.println(undoStack.get(i));
        }
        System.out.println("==== [DEBUG] Redo Stack ====");
        for (int i = redoStack.size() - 1; i >= 0; i--) {
            System.out.println(redoStack.get(i));
        }
    }

    public void calculateAll() {
        boolean[] errorFlag = {false};
        double totalIncome = calculateTotalIncome(errorFlag);
        double totalSpending = calculateTotalSpending(errorFlag);
        calculateTotalDifference(totalIncome, totalSpending);
    }

    public double calculateTotalIncome(boolean[] errorFlag) {
        double wages = convertToYearly(getFieldValue(wagesField, errorFlag), wagesComboBox);
        double loans = convertToYearly(getFieldValue(loansField, errorFlag), loansComboBox);
        double investments = convertToYearly(getFieldValue(investmentsField, errorFlag), investmentsComboBox);
        double totalIncome = wages + loans + investments;
        totalIncomeField.setText(String.format("%.2f", totalIncome));
        return totalIncome;
    }

    public double calculateTotalSpending(boolean[] errorFlag) {
        double food = convertToYearly(getFieldValue(foodField, errorFlag), foodComboBox);
        double rent = convertToYearly(getFieldValue(rentField, errorFlag), rentComboBox);
        double insurance = convertToYearly(getFieldValue(insuranceField, errorFlag), insuranceComboBox);
        double totalSpending = food + rent + insurance;
        totalSpendingField.setText(String.format("%.2f", totalSpending));
        return totalSpending;
    }

    public void calculateTotalDifference(double totalIncome, double totalSpending) {
        double totalDifference = totalIncome - totalSpending;
        totalDifferenceField.setText(String.format("%.2f", totalDifference));
        totalDifferenceField.setForeground(totalDifference < 0 ? Color.RED : Color.BLACK);
    }

    public double getFieldValue(JTextField field, boolean[] errorFlag) {
        try {
            return Double.parseDouble(field.getText().isEmpty() ? "0" : field.getText());
        } catch (NumberFormatException e) {
            errorFlag[0] = true;
            field.setText("0");
            return 0;
        }
    }

    public double convertToYearly(double value, JComboBox<String> comboBox) {
        if (comboBox == null) return value;
        switch ((String) comboBox.getSelectedItem()) {
            case "Per Week":
                return value * 52;
            case "Per Month":
                return value * 12;
            default:
                return value;
        }
    }

    public static class State {
        public final String wages, loans, investments, food, rent, insurance;
        public final String wagesFreq, loansFreq, investmentsFreq, foodFreq, rentFreq, insuranceFreq;

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
            return wages.equals(other.wages) &&
                   loans.equals(other.loans) &&
                   investments.equals(other.investments) &&
                   food.equals(other.food) &&
                   rent.equals(other.rent) &&
                   insurance.equals(other.insurance) &&
                   wagesFreq.equals(other.wagesFreq) &&
                   loansFreq.equals(other.loansFreq) &&
                   investmentsFreq.equals(other.investmentsFreq) &&
                   foodFreq.equals(other.foodFreq) &&
                   rentFreq.equals(other.rentFreq) &&
                   insuranceFreq.equals(other.insuranceFreq);
        }

        @Override
        public String toString() {
            return "State{" +
                    "wages='" + wages + '\'' +
                    ", loans='" + loans + '\'' +
                    ", investments='" + investments + '\'' +
                    ", food='" + food + '\'' +
                    ", rent='" + rent + '\'' +
                    ", insurance='" + insurance + '\'' +
                    '}';
        }
    }

 // Getters for testing
// Add getter methods to expose public fields for testing
public JTextField getWagesField() {
    return wagesField;
}

public JTextField getLoansField() {
    return loansField;
}

public JTextField getInvestmentsField() {
    return investmentsField;
}

public JTextField getFoodField() {
    return foodField;
}

public JTextField getRentField() {
    return rentField;
}

public JTextField getInsuranceField() {
    return insuranceField;
}

public JTextField getTotalIncomeField() {
    return totalIncomeField;
}

public JTextField getTotalSpendingField() {
    return totalSpendingField;
}

public JTextField getTotalDifferenceField() {
    return totalDifferenceField;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Budget Calculator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new BudgetBase(frame));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
