//Tests using 7 different tests including Calculate totalincome,Calculate totalspending,Totaldifference,Undo,Redo(which also uses undo) and validility checks. All test pass on my current machine meaning it works fine.
package src.test.java.Budget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.main.java.Budget.BudgetBase;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetBaseTest {

    private BudgetBase bb;

    @BeforeEach
    public void setUp() {
        bb = new BudgetBase(new JFrame());
    }
    @Test
    public void testInitialValues() {
        assertEquals("0", bb.wagesField.getText(), "Initial wages should be 0");
        assertEquals("0", bb.loansField.getText(), "Initial loans should be 0");
        assertEquals("0", bb.investmentsField.getText(), "Initial investments should be 0");
        assertEquals("0", bb.foodField.getText(), "Initial food should be 0");
        assertEquals("0", bb.rentField.getText(), "Initial rent should be 0");
        assertEquals("0", bb.insuranceField.getText(), "Initial insurance should be 0");
        assertEquals("0.00", bb.totalIncomeField.getText(), "Initial total income should be 0");
        assertEquals("0.00", bb.totalSpendingField.getText(), "Initial total spending should be 0");
        assertEquals("0.00", bb.totalDifferenceField.getText(), "Initial total difference should be 0");}
    
    @Test
    public void testCalculateTotalIncome() {
        bb.wagesField.setText("1000");
        bb.loansField.setText("700");
        bb.investmentsField.setText("500");
        bb.wagesComboBox.setSelectedItem("Per Year");
        bb.loansComboBox.setSelectedItem("Per Year");
        bb.investmentsComboBox.setSelectedItem("Per Year");
        bb.calculateAll();
        assertEquals("2200.00", bb.totalIncomeField.getText(), "Total income calculation failed");}
    

    @Test
    public void testCalculateTotalSpending() {
        bb.foodField.setText("300");
        bb.rentField.setText("500");
        bb.insuranceField.setText("650");
        bb.foodComboBox.setSelectedItem("Per Year");
        bb.rentComboBox.setSelectedItem("Per Year");
        bb.insuranceComboBox.setSelectedItem("Per Year");
        bb.calculateAll();
        assertEquals("1450.00", bb.totalSpendingField.getText(), "Total spending calculation failed");}
    

    @Test
    public void testTotalDifference() {
        bb.wagesField.setText("1000");
        bb.foodField.setText("300");
        bb.wagesComboBox.setSelectedItem("Per Year");
        bb.foodComboBox.setSelectedItem("Per Year");
        bb.calculateAll();
        assertEquals("700.00", bb.totalDifferenceField.getText(), "Total difference calculation failed");}
    

    @Test
    public void testUndoFunctionality() {
        bb.wagesField.setText("1000");
        bb.wagesComboBox.setSelectedItem("Per Year");
        bb.saveState();
        bb.calculateAll();

        bb.wagesField.setText("1500");
        bb.saveState();
        bb.calculateAll();

        bb.undo();
        assertEquals("1000.00", bb.totalIncomeField.getText(), "Undo functionality failed");}
    

    @Test
    public void testRedoFunctionality() {
        bb.wagesField.setText("1000");
        bb.wagesComboBox.setSelectedItem("Per Year");
        bb.calculateAll();
        bb.saveState();
        bb.wagesField.setText("1500");
        bb.calculateAll();
        bb.saveState();
        bb.undo();
        assertEquals("1000.00", bb.totalIncomeField.getText(), "Undo functionality failed");
        bb.redo();
        assertEquals("1500.00", bb.totalIncomeField.getText(), "Redo functionality failed");}
    

    @Test
    public void testValidityOfInputs() {
        
        bb.wagesField.setText("100a");  
        bb.calculateAll();
        assertEquals("0", bb.wagesField.getText(), "Wages field should be reset to 0 when an invalid character is inputted");

        
        bb.wagesField.setText("1500");
        bb.calculateAll();
        assertEquals("1500", bb.wagesField.getText(), "Wages field should accept valid input");

        
        bb.loansField.setText("abc");
        bb.calculateAll();
        assertEquals("0", bb.loansField.getText(), "Loans field should be reset to 0 when an invalid character is inputted");

         
        bb.loansField.setText("500");
        bb.calculateAll();
        assertEquals("500", bb.loansField.getText(), "Loans field should accept valid input");}
    
}
