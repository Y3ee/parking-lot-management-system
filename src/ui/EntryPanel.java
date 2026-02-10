package ui;

import model.Vehicle;
import service.ParkingService;

import javax.swing.*;

public class EntryPanel extends JPanel {

    public EntryPanel() {
        JTextField plateField = new JTextField(10);
        JButton parkButton = new JButton("Park Vehicle");

        parkButton.addActionListener(e -> {
            Vehicle v = new Vehicle(plateField.getText(), "Car");
            new ParkingService().parkVehicle(v, "F1-R1-S1");
            JOptionPane.showMessageDialog(this, "Vehicle parked");
        });

        add(new JLabel("Plate Number:"));
        add(plateField);
        add(parkButton);
    }
}
