package ui;

import java.awt.*;
import javax.swing.*;
import model.Car;
import model.Motorcycle;
import model.SpotType;
import model.Vehicle; // Import SpotType
import service.ParkingService;

public class EntryPanel extends JPanel {

    public EntryPanel() {
        setLayout(new FlowLayout()); // Use FlowLayout for simple left-to-right

        // UI Components
        JLabel plateLabel = new JLabel("Plate Number:");
        JTextField plateField = new JTextField(10);
        
        JLabel typeLabel = new JLabel("Vehicle Type:");
        // Matches your Spot Types logic
        String[] vehicleTypes = {"Car", "Motorcycle", "SUV or Truck", "Handicapped", "Reserved"};
        JComboBox<String> typeCombo = new JComboBox<>(vehicleTypes);

        JButton parkButton = new JButton("Park Vehicle");

        // The Logic when button is clicked
        parkButton.addActionListener(e -> {
            String plate = plateField.getText().trim();
            String typeStr = (String) typeCombo.getSelectedItem();
            
            if(plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a plate number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- 1. VIP PASSWORD CHECK ---
            if ("Reserved".equals(typeStr)) {
                JPasswordField pf = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(null, pf, "Enter VIP Password (Hint: VIP123)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String password = new String(pf.getPassword());
                    if (!password.equals("VIP123")) {
                        JOptionPane.showMessageDialog(this, "Invalid VIP Password!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                        return; // Stop here, do not park
                    }
                } else {
                    return; // User clicked Cancel
                }
            }

            // --- 2. CREATE CORRECT VEHICLE TYPE ---
            Vehicle v = null;
            
            // We use anonymous classes for Reserved/Handicapped to force the correct SpotType
            // because 'Car' is hardcoded to REGULAR.
            switch (typeStr) {
                case "Car":
                    v = new Car(plate); // Enters as REGULAR
                    break;
                case "SUV or Truck":
                    v = new Car(plate); // enter regular
                    break;
                case "Motorcycle":
                    v = new Motorcycle(plate); // Enters as COMPACT
                    break;
                case "Reserved":
                    // Force Type to RESERVED
                    v = new Vehicle(plate, SpotType.RESERVED) {}; 
                    break;
                case "Handicapped":
                    // Force Type to HANDICAPPED
                    v = new Vehicle(plate, SpotType.HANDICAPPED) {}; 
                    break;
            }

            // --- 3. CALL SERVICE ---
            // This sends the vehicle to the Service, which finds the matching spot in DAO
            boolean success = ParkingService.getInstance().parkVehicle(v);

            if (success) {
                JOptionPane.showMessageDialog(this, "Vehicle Parked Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                plateField.setText(""); // Clear the field
            } else {
                JOptionPane.showMessageDialog(this, "Parking Failed: No spots available for " + typeStr, "Full", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add components to the panel
        add(plateLabel);
        add(plateField);
        add(typeLabel);
        add(typeCombo);
        add(parkButton);
    }
}