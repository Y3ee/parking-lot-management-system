package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import model.ParkingSpot;
import model.SpotType;
import model.Vehicle;
import service.ParkingService;

public class EntryPanel extends JPanel {

    private JTextField plateField;
    private JComboBox<String> typeCombo;
    private JPanel mainGridPanel; // The container for the floors

    public EntryPanel() {
        setLayout(new BorderLayout());

        // --- TOP PANEL: Inputs ---
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(new TitledBorder("Step 1: Enter Details & Select Type"));

        topPanel.add(new JLabel("Plate Number:"));
        plateField = new JTextField(10);
        topPanel.add(plateField);

        topPanel.add(new JLabel("Vehicle Type:"));

        // ✅ FIX: Remove "Reserved" from vehicle choices (Reserved is a spot type, not a vehicle type)
        String[] types = {"Car", "Motorcycle", "SUV or Truck", "Handicapped"};
        typeCombo = new JComboBox<>(types);
        topPanel.add(typeCombo);

        // ✅ IMPORTANT: When change type, refresh to re-enable/disable suitable spots
        typeCombo.addActionListener(e -> refreshGrid());

        // Add a "Help" label
        JLabel helpLabel = new JLabel("(Step 2: Click a suitable spot below to park)");
        helpLabel.setForeground(Color.BLUE);
        topPanel.add(helpLabel);

        add(topPanel, BorderLayout.NORTH);

        JButton registerBtn = new JButton("Register as VIP");
        registerBtn.setBackground(new Color(173, 216, 230)); // Light Blue
        registerBtn.addActionListener(e -> showRegistrationDialog());
        topPanel.add(registerBtn);

        // --- CENTER PANEL: The Interactive Grid ---
        mainGridPanel = new JPanel();
        mainGridPanel.setLayout(new BoxLayout(mainGridPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(mainGridPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Load the grid initially
        refreshGrid();
    }

    private void refreshGrid() {
        mainGridPanel.removeAll();

        // Fetch all spots from the database
        java.util.List<ParkingSpot> spots = ParkingService.getInstance().getParkingLotStatus();

        // Loop from Floor 5 down to 1 (Descending)
        for (int floor = 5; floor >= 1; floor--) {

            // Create the panel for this floor
            JPanel floorPanel = new JPanel(new GridLayout(0, 6, 10, 10)); // 6 Columns

            TitledBorder border = BorderFactory.createTitledBorder("Floor " + floor + " (Click a spot to park)");
            border.setTitleFont(new Font("Arial", Font.BOLD, 14));
            floorPanel.setBorder(border);

            String floorPrefix = "F" + floor + "-";

            // Add only spots belonging to this floor
            for (ParkingSpot spot : spots) {
                if (spot.getSpotId().startsWith(floorPrefix)) {
                    JButton spotBtn = createInteractionButton(spot);
                    floorPanel.add(spotBtn);
                }
            }

            // Add the floor panel to the main scrollable area
            mainGridPanel.add(floorPanel);

            // Add a spacer between floors
            mainGridPanel.add(Box.createVerticalStrut(10));
        }

        mainGridPanel.revalidate();
        mainGridPanel.repaint();
    }

    // ✅ Helper: build correct Vehicle object based on dropdown selection (polymorphism)
    private Vehicle buildVehicleFromUI(String plate) {
        String selectedType = (String) typeCombo.getSelectedItem();

        if ("Motorcycle".equalsIgnoreCase(selectedType)) return new model.Motorcycle(plate);
        if ("SUV or Truck".equalsIgnoreCase(selectedType)) return new model.SUVTruck(plate);
        if ("Handicapped".equalsIgnoreCase(selectedType)) return new model.HandicappedVehicle(plate);
        return new model.Car(plate);
    }

    private JButton createInteractionButton(ParkingSpot spot) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(100, 75));
        btn.setFont(new Font("Arial", Font.PLAIN, 10));

        // Display Info
        String priceText = String.format("RM %.2f/hr", spot.getType().getRate());
        String statusText = spot.isOccupied() ? "OCCUPIED" : "AVAILABLE";

        btn.setText("<html><center><b>" + spot.getSpotId() + "</b><br>"
                + spot.getType() + "<br>"
                + priceText + "<br>"
                + statusText + "</center></html>");

        // Color Logic
        if (spot.isOccupied()) {
            btn.setBackground(Color.RED);
            btn.setEnabled(false); // Cannot click occupied spots
        } else {
            // ✅ Suitability check (grey out unsuitable spots)
            Vehicle preview = buildVehicleFromUI("TEMP");
            boolean suitable = preview.canParkIn(spot.getType());

            if (!suitable) {
                btn.setEnabled(false);
                btn.setBackground(Color.LIGHT_GRAY);
            } else {
                btn.setBackground(new Color(144, 238, 144)); // Green for suitable available spots

                // SPECIAL COLORS for Special Types
                if (spot.getType() == SpotType.HANDICAPPED) btn.setBackground(new Color(255, 255, 153)); // Yellow
                if (spot.getType() == SpotType.RESERVED) btn.setBackground(new Color(173, 216, 230));   // Blue
            }
        }

        // --- CLICK LOGIC ---
        btn.addActionListener(e -> handleSpotClick(spot));

        return btn;
    }

    private void handleSpotClick(ParkingSpot spot) {
        String plate = plateField.getText().trim().toUpperCase();

        // 1. Basic Validation
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Plate Number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. VIP CHECK (The Guard at the Gate)
        if (spot.getType() == SpotType.RESERVED) {

            // Check the database whitelist
            boolean isRegistered = new dao.ParkingSpotDAO().isVip(plate);

            if (!isRegistered) {
                // DENIED!
                int choice = JOptionPane.showConfirmDialog(this,
                        "Access Denied!\nPlate " + plate + " is NOT registered as VIP.\n\nWould you like to register now?",
                        "VIP Only",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    showRegistrationDialog(); // Open the form immediately
                }
                return; // Stop here. Do not park.
            }

            // If registered, show a nice welcome message
            JOptionPane.showMessageDialog(this, "Welcome, VIP Member! Gate opening...", "Access Granted", JOptionPane.INFORMATION_MESSAGE);
        }

        // 3. Normal Parking Logic (✅ create correct subclass)
        Vehicle v = buildVehicleFromUI(plate);

        // ✅ Extra safety check (in case someone bypasses UI)
        if (!v.canParkIn(spot.getType())) {
            JOptionPane.showMessageDialog(this,
                    "This vehicle type cannot park in " + spot.getType() + " spot.",
                    "Invalid Spot",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.Ticket ticket = ParkingService.getInstance().parkVehicleAt(v, spot.getSpotId());

        if (ticket != null) {
        JOptionPane.showMessageDialog(this,
                "Parked Successfully!\n\n"
                + "Ticket: " + ticket.getTicketId() + "\n"
                + "Spot: " + ticket.getSpotId() + "\n"
                + "Entry Time: " + ticket.getEntryTime(),
                "Ticket Generated",
                JOptionPane.INFORMATION_MESSAGE);

        plateField.setText("");
        refreshGrid();
        } else {
        JOptionPane.showMessageDialog(this, "Parking Failed: Spot mismatch or occupied.");
        }

    }

    // The Popup Form for Registration
    private void showRegistrationDialog() {
        JTextField plateInput = new JTextField();
        JTextField nameInput = new JTextField();

        Object[] message = {
                "VIP Plate Number:", plateInput,
                "Owner Name:", nameInput
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Register New VIP", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String plate = plateInput.getText().trim();
            String name = nameInput.getText().trim();

            if (!plate.isEmpty() && !name.isEmpty()) {
                // Save to Database
                boolean saved = new dao.ParkingSpotDAO().registerVip(plate, name);
                if (saved) {
                    JOptionPane.showMessageDialog(this, "Success! " + plate + " is now a VIP.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Plate already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
