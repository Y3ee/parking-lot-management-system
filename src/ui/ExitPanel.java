package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import model.ExitBill;
import model.Receipt;
import service.ParkingService;

public class ExitPanel extends JPanel {

    private JTextField plateField;
    private JTextArea output;

    public ExitPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TOP: controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBorder(new TitledBorder("Vehicle Exit / Payment"));

        top.add(new JLabel("Plate Number:"));
        plateField = new JTextField(12);
        top.add(plateField);

        JButton previewBtn = new JButton("Preview Bill");
        JButton payParkingBtn = new JButton("Pay Parking Only");
        JButton payAllBtn = new JButton("Pay All (Fee + Fines)");

        top.add(previewBtn);
        top.add(payParkingBtn);
        top.add(payAllBtn);

        previewBtn.setBackground(new Color(144, 238, 144)); 
        payParkingBtn.setBackground(new Color(255, 102, 102)); 
        payAllBtn.setBackground(new Color(255, 204, 153)); 

        add(top, BorderLayout.NORTH);

        // center: output area
        output = new JTextArea(18, 55);
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(output), BorderLayout.CENTER);

        previewBtn.addActionListener(e -> preview());
        payParkingBtn.addActionListener(e -> pay(false));
        payAllBtn.addActionListener(e -> pay(true));
    }

    private void preview() {
        String plate = plateField.getText().trim().toUpperCase();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter plate number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ExitBill bill = ParkingService.getInstance().getExitService().previewBill(plate);
            output.setText(bill.prettyBill());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pay(boolean payAll) {
        String plate = plateField.getText().trim().toUpperCase();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter plate number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] methods = {"Cash", "Card"};
        String method = (String) JOptionPane.showInputDialog(
                this,
                "Select payment method:",
                "Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                methods,
                methods[0]
        );
        if (method == null) return; // cancelled

        try {
            Receipt receipt;
            if (payAll) {
                receipt = ParkingService.getInstance().getExitService().payAllAndExit(plate, method);
            } else {
                receipt = ParkingService.getInstance().getExitService().payParkingOnlyAndExit(plate, method);
            }

            output.setText(receipt.prettyReceipt());
            JOptionPane.showMessageDialog(this, "Payment successful ✅\nThank you! Drive safe and see you again 😊", "Done",
                    JOptionPane.INFORMATION_MESSAGE);

            plateField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Payment Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
