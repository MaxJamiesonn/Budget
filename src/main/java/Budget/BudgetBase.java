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
    private Timer saveStateTimer;
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
    public Stack<State> undoStack;
    public Stack<State> redoStack;
    public boolean isPerformingUndoRedo = false;

    public BudgetBase(JFrame frame) {
        this.topLevelFrame = frame;
        setLayout(new GridBagLayout());
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        initComponents();
        setDefaultValues();
        saveState();
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
                handleTextChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTextChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTextChange();
            }

            private void handleTextChange() {
                if (saveStateTimer != null && saveStateTimer.isRunning()) {
                    saveStateTimer.restart(); 
                } else {
                    saveStateTimer = new Timer(500, event -> {
                        saveState(); 
                        calculateAll(); 
                    });
                    saveStateTimer.setRepeats(false); 
                    saveStateTimer.start();
                }
            }
        });

        comboBox.addActionListener(e -> {
            saveState();
            calculateAll();
        });return textField;}
    
    
    public void setDefaultValues() {
        wagesField.setText("0");
        loansField.setText("0");
        investmentsField.setText("0");
        foodField.setText("0");
        rentField.setText("0");
        insuranceField.setText("0");
        totalIncomeField.setText("0.00");
        totalSpendingField.setText("0.00");
        totalDifferenceField.setText("0.00");
        wagesComboBox.setSelectedItem("Per Year");
        loansComboBox.setSelectedItem("Per Year");
        investmentsComboBox.setSelectedItem("Per Year");
        foodComboBox.setSelectedItem("Per Year");
        rentComboBox.setSelectedItem("Per Year");
        insuranceComboBox.setSelectedItem("Per Year");
    }

    public void addComponent(Component component, int gridrow, int gridcol) {
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = gridcol;
        layoutConstraints.gridy = gridrow;
        add(component, layoutConstraints);
    }

    public void saveState() {
        if (isPerformingUndoRedo) return; 
        State currentState = new State(this);
        if (undoStack.isEmpty() || !undoStack.peek().equals(currentState)) {
        undoStack.push(currentState);
        redoStack.clear(); 
        System.out.println("state saved - Wages: " + wagesField.getText());
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
        System.out.println("total Income: " + totalIncome); 
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
            JOptionPane.showMessageDialog(null, "Invalid input in field: " + field.getName());
            field.setText("0"); 
            calculateAll(); 
            return 0;}}
    public double convertToYearly(double value, JComboBox<String> comboBox) {
        if (comboBox == null) return value;
        switch ((String) comboBox.getSelectedItem()) {
            case "Per Week":
                return value * 52;
            case "Per Month":
                return value * 12;
            default:
                return value;}}
        
    
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
    
        boolean equals(State other) {
            return wages.equals(other.wages) && loans.equals(other.loans) &&
                   investments.equals(other.investments) && food.equals(other.food) &&
                   rent.equals(other.rent) && insurance.equals(other.insurance) &&
                   wagesFreq.equals(other.wagesFreq) && loansFreq.equals(other.loansFreq) &&
                   investmentsFreq.equals(other.investmentsFreq) && foodFreq.equals(other.foodFreq) &&
                   rentFreq.equals(other.rentFreq) && insuranceFreq.equals(other.insuranceFreq);}}
        
    public void undo() {
        if (!undoStack.isEmpty()) {
            System.out.println("undo called - current undo stack: " + undoStack);
            isPerformingUndoRedo = true;
            redoStack.push(undoStack.pop());
            if (!undoStack.isEmpty()) {
                applyState(undoStack.peek());}
            isPerformingUndoRedo = false;}}

    public void redo() {
        if (!redoStack.isEmpty()) {
            isPerformingUndoRedo = true;
            State nextState = redoStack.pop();
            undoStack.push(nextState);
            applyState(nextState);
            isPerformingUndoRedo = false;}}
        
    public void applyState(State state) {
        wagesField.setText(state.wages);
        loansField.setText(state.loans);
        investmentsField.setText(state.investments);
        foodField.setText(state.food);
        rentField.setText(state.rent);
        insuranceField.setText(state.insurance);
    
        wagesComboBox.setSelectedItem(state.wagesFreq);
        loansComboBox.setSelectedItem(state.loansFreq);
        investmentsComboBox.setSelectedItem(state.investmentsFreq);
        foodComboBox.setSelectedItem(state.foodFreq);
        rentComboBox.setSelectedItem(state.rentFreq);
        insuranceComboBox.setSelectedItem(state.insuranceFreq);
    
        calculateAll();}
    
    private void initListeners() {
        undoButton.addActionListener(e -> undo());
        redoButton.addActionListener(e -> redo());
        exitButton.addActionListener(e -> System.exit(0));
    }

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Budget Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new BudgetBase(frame));
        frame.pack();
        frame.setVisible(true);
    });}
}
