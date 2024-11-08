// base code for student budget assessment
// Students do not need to use this code in their assessment, fine to junk it and do something different!
//
// Your submission must be a maven project, and must be submitted via Codio, and run in Codio
//
// user can enter in wages and loans and calculate total income
//
// run in Codio 
// To see GUI, run with java and select Box Url from Codio top line menu
//
// Layout - Uses GridBag layout in a straightforward way, every component has a (column, row) position in the UI grid
// Not the prettiest layout, but relatively straightforward
// Students who use IntelliJ or Eclipse may want to use the UI designers in these IDEs , instead of GridBagLayout
package Budget;

// Swing imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

// class definition
public class BudgetBase extends JPanel {    // based on Swing JPanel

    // high level UI stuff
    JFrame topLevelFrame;  // top-level JFrame
    GridBagConstraints layoutConstraints = new GridBagConstraints(); // used to control layout

    // widgets which may have listeners and/or values
    private JButton calculateButton;   // Calculate button
    private JButton exitButton;        // Exit button
    private JTextField wagesField;     // Wages text field
    private JTextField loansField;     // Loans text field
    private JTextField totalIncomeField; // Total Income field
    private JTextField InvestmentsField; //Total

    private JTextField foodField;
    private JTextField rentField;
    private JTextField insuranceField;
    private JTextField totalSpendingField;
    private JTextField totalDifferenceField;

    // constructor - create UI  (dont need to change this)
    public BudgetBase(JFrame frame) {
        topLevelFrame = frame; // keep track of top-level frame
        setLayout(new GridBagLayout());  // use GridBag layout
        initComponents();  // initalise components
    }

    // initialise componenents
    // Note that this method is quite long.  Can be shortened by putting Action Listener stuff in a separate method
    // will be generated automatically by IntelliJ, Eclipse, etc
    private void initComponents() { 

        // Top row (0) - "INCOME" label
        JLabel incomeLabel = new JLabel("INCOME");
        addComponent(incomeLabel, 0, 0);

        // Row 1 - Wages label followed by wages textbox
        JLabel wagesLabel = new JLabel("Wages");
        addComponent(wagesLabel, 1, 0);

        // set up text field for entering wages
        // Could create method to do below (since this is done several times)
        wagesField = new JTextField("", 10);   // blank initially, with 10 columns
        wagesField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
        addComponent(wagesField, 1, 1);   

        // Row 2 - Loans label followed by loans textbox
        JLabel loansLabel = new JLabel("Loans");
        addComponent(loansLabel, 2, 0);

        // set up text box for entering loans
        loansField = new JTextField("", 10);   // blank initially, with 10 columns
        loansField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
        addComponent(loansField, 2, 1); 


        //Row 3
        JLabel InvestmentsLabel = new JLabel("Investments");
        addComponent(InvestmentsLabel, 3,0);

        InvestmentsField = new JTextField("", 10);   // blank initially, with 10 columns
        InvestmentsField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
        addComponent(InvestmentsField, 3, 1); 

        // Row 4 - Total Income label followed by total income field
        JLabel totalIncomeLabel = new JLabel("Total Income");
        addComponent(totalIncomeLabel, 4, 0);

        // set up text box for displaying total income.  Users cam view, but cannot directly edit it
        totalIncomeField = new JTextField("0", 10);   // 0 initially, with 10 columns
        totalIncomeField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
        totalIncomeField.setEditable(false);    // user cannot directly edit this field (ie, it is read-only)
        addComponent(totalIncomeField, 4, 1);  

        // Row 4 - Calculate Button
        calculateButton = new JButton("Calculate");
        addComponent(calculateButton, 6, 0);  

        // Row 5 - Exit Button
        exitButton = new JButton("Exit");
        addComponent(exitButton, 7, 0);  

        // set up  listeners (in a spearate method)
        initListeners();
//////////////////////////////////////////////////////// second coloumb
        //Row 1
         // Top row (0) - "INCOME" label
         JLabel spendingLabel = new JLabel("SPENDING");
         addComponent(spendingLabel, 0, 2);
 
         // Row 1 - Wages label followed by wages textbox
         JLabel foodLabel = new JLabel("Food");
         addComponent(foodLabel, 1, 2);
 
         // set up text field for entering wages
         // Could create method to do below (since this is done several times)
         foodField = new JTextField("", 10);   // blank initially, with 10 columns
         foodField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
         addComponent(foodField, 1, 3);   
 
         // Row 2 - Loans label followed by loans textbox
         JLabel rentLabel = new JLabel("Rent");
         addComponent(rentLabel, 2, 2);
 
         // set up text box for entering loans
         rentField = new JTextField("", 10);   // blank initially, with 10 columns
         rentField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
         addComponent(rentField, 2, 3); 
 
 
         //Row 3
         JLabel insuranceLabel = new JLabel("Insurance");
         addComponent(insuranceLabel, 3,2);
 
         insuranceField = new JTextField("", 10);   // blank initially, with 10 columns
         insuranceField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
         addComponent(insuranceField, 3, 3); 
 
         // Row 4 - Total Income label followed by total income field
         JLabel totalSpendingLabel = new JLabel("Total Spending");
         addComponent(totalSpendingLabel, 4, 2);
 
         // set up text box for displaying total income.  Users cam view, but cannot directly edit it
         totalSpendingField = new JTextField("0", 10);   // 0 initially, with 10 columns
         totalSpendingField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
         totalSpendingField.setEditable(false);    // user cannot directly edit this field (ie, it is read-only)
         addComponent(totalSpendingField, 4, 3);  
 


         JLabel totalDifferenceLabel = new JLabel("Total Difference");
         addComponent (totalDifferenceLabel,5,0);



          // set up text box for displaying totalDifference
          totalDifferenceField = new JTextField("0", 10);   // 0 initially, with 10 columns
          totalDifferenceField.setHorizontalAlignment(JTextField.RIGHT) ;    // number is at right end of field
          totalDifferenceField.setEditable(false);    // user cannot directly edit this field (ie, it is read-only)
          addComponent(totalDifferenceField, 5, 1);
    }

    // set up listeners
    // initially just for buttons, can add listeners for text fields
    private void initListeners() {

        // exitButton - exit program when pressed
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // calculateButton - call calculateTotalIncome() when pressed
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTotalIncome();
                calculateTotalSpending();
                calculateTotalDifference();
                
            }
        });

    }

    // add a component at specified row and column in UI.  (0,0) is top-left corner
    private void addComponent(Component component, int gridrow, int gridcol) {
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;   // always use horixontsl filll
        layoutConstraints.gridx = gridcol;
        layoutConstraints.gridy = gridrow;
        add(component, layoutConstraints);

    }

    // update totalIncomeField (eg, when Calculate is pressed)
    // use double to hold numbers, so user can type fractional amounts such as 134.50
    public double calculateTotalIncome() {

        // get values from income text fields.  valie is NaN if an error occurs
        double wages = getTextFieldValue(wagesField);
        double loans = getTextFieldValue(loansField);
        double Investments = getTextFieldValue(InvestmentsField);


        // clear total field and return if any value is NaN (error)
        if (Double.isNaN(wages) || Double.isNaN(loans) || Double.isNaN(Investments)) {
            totalIncomeField.setText("");  // clear total income field
            wages = 0.0;
            return wages;   // exit method and do nothing
        }

        // otherwise calculate total income and update text field
        double totalIncome = wages + loans + Investments;
        totalIncomeField.setText(String.format("%.2f",totalIncome));  // format with 2 digits after the .
        return totalIncome;
        

    }

    public double calculateTotalSpending(){

          // get values from income text fields.  valie is NaN if an error occurs
          double food = getTextFieldValue(foodField);
          double rent = getTextFieldValue(rentField);
          double insurance = getTextFieldValue(insuranceField);
  
          // clear total field and return if any value is NaN (error)
          if (Double.isNaN(food) || Double.isNaN(rent) || Double.isNaN(insurance)){
              totalSpendingField.setText("");  // clear total income field
              rent = 0.0;
              return rent;   // exit method and do nothing
          }
  
          // otherwise calculate total income and update text field
          double totalSpending = food + rent + insurance;
          totalSpendingField.setText(String.format("%.2f",totalSpending));  // format with 2 digits after the .
          return totalSpending;
          
          
         

    }

    public double calculateTotalDifference(){
        double totalIncome = getTextFieldValue(totalIncomeField);
        double totalSpending = getTextFieldValue(totalSpendingField);

      double totalDifference = totalIncome - totalSpending;
      totalDifferenceField.setText(String.format("%.2f",totalDifference)); 
      return totalDifference;

    }

    // return the value if a text field as a double
    // --return 0 if field is blank
    // --return NaN if field is not a number
    private double getTextFieldValue(JTextField field) {

        // get value as String from field
        String fieldString = field.getText();  // get text from text field

        if (fieldString.isBlank()) {   // if text field is blank, return 0
            return 0;
        }

        else {  // if text field is not blank, parse it into a double
            try {
                return Double.parseDouble(fieldString);  // parse field number into a double
             } catch (java.lang.NumberFormatException ex) {  // catch invalid number exception
                JOptionPane.showMessageDialog(topLevelFrame, "Please enter a valid number");  // show error message
                return Double.NaN;  // return NaN to show that field is not a number
            }
        }
    }


// below is standard code to set up Swing, which students shouldnt need to edit much
    // standard mathod to show UI
    private static void createAndShowGUI() {
 
        //Create and set up the window.
        JFrame frame = new JFrame("Budget Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        BudgetBase newContentPane = new BudgetBase(frame);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    // standard main class to set up Swing UI
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
    }


}