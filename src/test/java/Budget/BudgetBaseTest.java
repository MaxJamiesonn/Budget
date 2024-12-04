package src.test.java.Budget;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.main.java.Budget.BudgetBase;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

public class BudgetBaseTest {

    private BudgetBase bb;

    @BeforeEach
    public void setUp() {
        bb = new BudgetBase(new JFrame()); // Make sure JFrame is correctly imported
    }

    @Test
    public void testInitialValues() {
        assertEquals("0", bb.getWagesField().getText(), "Initial wages should be 0");
        assertEquals("0", bb.getLoansField().getText(), "Initial loans should be 0");
        assertEquals("0", bb.getInvestmentsField().getText(), "Initial investments should be 0");
        assertEquals("0", bb.getFoodField().getText(), "Initial food should be 0");
        assertEquals("0", bb.getRentField().getText(), "Initial rent should be 0");
        assertEquals("0", bb.getInsuranceField().getText(), "Initial insurance should be 0");
        assertEquals("0.00", bb.getTotalIncomeField().getText(), "Initial total income should be 0");
        assertEquals("0.00", bb.getTotalSpendingField().getText(), "Initial total spending should be 0");
        assertEquals("0.00", bb.getTotalDifferenceField().getText(), "Initial total difference should be 0");
    }

    @Test
    public void testCalculateTotalIncome() {
        bb.getWagesField().setText("1000");
        bb.getLoansField().setText("700");
        bb.getInvestmentsField().setText("500");
        
        // Simulating the calculation
        bb.calculateAll(); // This method should update the fields after calculations

        assertEquals("2200.00", bb.getTotalIncomeField().getText(), "Total income calculation failed");
    }

    @Test
    public void testCalculateTotalSpending() {
        bb.getFoodField().setText("300");
        bb.getRentField().setText("500");
        bb.getInsuranceField().setText("650");
        
        // Simulating the calculation
        bb.calculateAll(); // This method should update the fields after calculations

        assertEquals("1450.00", bb.getTotalSpendingField().getText(), "Total spending calculation failed");
    }

    @Test
    public void testTotalDifference() {
        bb.getWagesField().setText("1000");
        bb.getFoodField().setText("300");

        // Simulating the calculation
        bb.calculateAll();

        assertEquals("700.00", bb.getTotalDifferenceField().getText(), "Total difference calculation failed");
    }

    @Test
    public void testUndoFunctionality() {
        bb.getWagesField().setText("1000");
        bb.getLoansField().setText("500");
        bb.calculateAll(); // Save the state

        bb.saveState(); // Save current state to undo stack
        bb.getWagesField().setText("1500");
        bb.calculateAll(); // Recalculate with new values

        assertEquals("1500.00", bb.getTotalIncomeField().getText(), "New total income should be calculated");

        // Now perform undo
        bb.undo();
        assertEquals("1000.00", bb.getTotalIncomeField().getText(), "Undo functionality failed");
    }

    @Test
    public void testRedoFunctionality() {
        bb.getWagesField().setText("1000");
        bb.getLoansField().setText("500");
        bb.calculateAll(); // Save the state

        bb.saveState(); // Save current state to undo stack
        bb.getWagesField().setText("1500");
        bb.calculateAll(); // Recalculate with new values

        bb.undo(); // Perform undo
        bb.redo(); // Perform redo

        assertEquals("1500.00", bb.getTotalIncomeField().getText(), "Redo functionality failed");
    }
}
