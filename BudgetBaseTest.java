package Budget;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

// Swing imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Unit test for simple App.
 */
public class BudgetBaseTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldTotal()
    {
        JFrame frame = new JFrame();
        BudgetBase bb = new BudgetBase(frame);
        double value = 0.0;

        assertEquals(value, bb.calculateTotalIncome() );
    }
}
