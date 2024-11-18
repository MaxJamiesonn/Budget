package Budget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BudgetBase extends JPanel {

    JFrame topLevelFrame;
    GridBagConstraints layoutConstraints = new GridBagConstraints();

    private JButton calculateButton;
    private JButton exitButton;
    private JTextField wagesField;
    private JTextField loansField;
    private JTextField totalIncomeField;
    private JTextField InvestmentsField;

    private JTextField foodField;
    private JTextField rentField;
    private JTextField insuranceField;
    private JTextField totalSpendingField;
    private JTextField totalDifferenceField;

    public BudgetBase(JFrame frame) {
        topLevelFrame = frame;
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents() {
        JLabel incomeLabel = new JLabel("INCOME");
        addComponent(incomeLabel, 0, 0);

        JLabel wagesLabel = new JLabel("Wages");
        addComponent(wagesLabel, 1, 0);

        wagesField = new JTextField("", 10);
        wagesField.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(wagesField, 1, 1);

        JLabel loansLabel = new JLabel("Loans");
        addComponent(loansLabel, 2, 0);

        loansField = new JTextField("", 10);
        loansField.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(loansField, 2, 1);

        JLabel InvestmentsLabel = new JLabel("Investments");
        addComponent(InvestmentsLabel, 3, 0);

        InvestmentsField = new JTextField("", 10);
        InvestmentsField.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(InvestmentsField, 3, 1);

        JLabel totalIncomeLabel = new JLabel("Total Income");
        addComponent(totalIncomeLabel, 4, 0);

        totalIncomeField = new JTextField("0", 10);
        totalIncomeField.setHorizontalAlignment(JTextField.RIGHT);
        totalIncomeField.setEditable(false);
        addComponent(totalIncomeField, 4, 1);

        calculateButton = new JButton("Calculate");
        addComponent(calculateButton, 6, 0);

        exitButton = new JButton("Exit");
        addComponent(exitButton, 7, 0);

        JLabel spendingLabel = new JLabel("SPENDING");
        addComponent(spendingLabel, 0, 2);

        JLabel foodLabel = new JLabel("Food");
        addComponent(foodLabel, 1, 2);

        foodField = new JTextField("", 10);
        foodField.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(foodField, 1, 3);

        JLabel rentLabel = new JLabel("Rent");
        addComponent(rentLabel, 2, 2);

        rentField = new JTextField("", 10);
        rentField.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(rentField, 2, 3);

        JLabel insuranceLabel = new JLabel("Insurance");
        addComponent(insuranceLabel, 3, 2);

        insuranceField = new JTextField("", 10);
        insuranceField.setHorizontalAlignment(JTextField.RIGHT);
        addComponent(insuranceField, 3, 3);

        JLabel totalSpendingLabel = new JLabel("Total Spending");
        addComponent(totalSpendingLabel, 4, 2);

        totalSpendingField = new JTextField("0", 10);
        totalSpendingField.setHorizontalAlignment(JTextField.RIGHT);
        totalSpendingField.setEditable(false);
        addComponent(totalSpendingField, 4, 3);

        JLabel totalDifferenceLabel = new JLabel("Total Difference");
        addComponent(totalDifferenceLabel, 5, 0);

        totalDifferenceField = new JTextField("0", 10);
        totalDifferenceField.setHorizontalAlignment(JTextField.RIGHT);
        totalDifferenceField.setEditable(false);
        addComponent(totalDifferenceField, 5, 1);

        initListeners();
    }

    private void initListeners() {
        exitButton.addActionListener(e -> System.exit(0));

        calculateButton.addActionListener(e -> {
            boolean[] errorShownFlag = {false};
            double totalIncome = calculateTotalIncome(errorShownFlag);
            double totalSpending = calculateTotalSpending(errorShownFlag);
            calculateTotalDifference(totalIncome, totalSpending, errorShownFlag);
        });
    }

    private void addComponent(Component component, int gridrow, int gridcol) {
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
        layoutConstraints.gridx = gridcol;
        layoutConstraints.gridy = gridrow;
        add(component, layoutConstraints);
    }

    public double calculateTotalIncome(boolean[] errorShownFlag) {
        double wages = getTextFieldValue(wagesField, errorShownFlag);
        double loans = getTextFieldValue(loansField, errorShownFlag);
        double investments = getTextFieldValue(InvestmentsField, errorShownFlag);

        if (Double.isNaN(wages) || Double.isNaN(loans) || Double.isNaN(investments)) {
            totalIncomeField.setText("0");
            return Double.NaN;
        }

        double totalIncome = wages + loans + investments;
        totalIncomeField.setText(String.format("%.2f", totalIncome));
        return totalIncome;
    }

    public double calculateTotalSpending(boolean[] errorShownFlag) {
        double food = getTextFieldValue(foodField, errorShownFlag);
        double rent = getTextFieldValue(rentField, errorShownFlag);
        double insurance = getTextFieldValue(insuranceField, errorShownFlag);

        if (Double.isNaN(food) || Double.isNaN(rent) || Double.isNaN(insurance)) {
            totalSpendingField.setText("0");
            return Double.NaN;
        }

        double totalSpending = food + rent + insurance;
        totalSpendingField.setText(String.format("%.2f", totalSpending));
        return totalSpending;
    }

    public double calculateTotalDifference(double totalIncome, double totalSpending, boolean[] errorShownFlag) {
        if (Double.isNaN(totalIncome) || Double.isNaN(totalSpending)) {
            totalDifferenceField.setText("0");
            return Double.NaN;
        }

        double totalDifference = totalIncome - totalSpending;
        totalDifferenceField.setText(String.format("%.2f", totalDifference));
        totalDifferenceField.setForeground(totalDifference < 0 ? Color.RED : Color.BLACK);
        return totalDifference;
    }

    private double getTextFieldValue(JTextField field, boolean[] errorShownFlag) {
        String fieldString = field.getText();

        if (fieldString.isBlank()) {
            return 0;
        } else {
            try {
                double value = Double.parseDouble(fieldString);
                if (value < 0) {
                    if (!errorShownFlag[0]) {
                        JOptionPane.showMessageDialog(null, "Please enter a non-negative number");
                        errorShownFlag[0] = true;
                    }
                    return Double.NaN;
                }
                return value;
            } catch (NumberFormatException ex) {
                if (!errorShownFlag[0]) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number");
                    errorShownFlag[0] = true;
                }
                return Double.NaN;
            }
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
