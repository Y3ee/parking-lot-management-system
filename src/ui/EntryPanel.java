package ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import model.ParkingSpot;
import model.SpotType;
import model.Vehicle;
import service.ParkingService;

public class EntryPanel extends JPanel {

    private JTextField plateField;
    private JComboBox<String> typeCombo;
    private JPanel mainGridPanel;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");

    public EntryPanel() {
        setLayout(new BorderLayout());

        // top panel: inputs
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(new TitledBorder("Step 1: Enter Details & Select Type"));

        topPanel.add(new JLabel("Plate Number:"));
        plateField = new JTextField(10);
        topPanel.add(plateField);

        plateField.addActionListener(e -> refreshGrid());

        topPanel.add(new JLabel("Vehicle Type:"));

        String[] types = {"Car", "Motorcycle", "SUV or Truck", "Handicapped"};
        typeCombo = new JComboBox<>(types);
        topPanel.add(typeCombo);

        // refresh when vehicle type change
        typeCombo.addActionListener(e -> refreshGrid());

        JLabel helpLabel = new JLabel("(Step 2: Click a suitable spot below to park)");
        helpLabel.setForeground(Color.BLUE);
        topPanel.add(helpLabel);

        JButton registerBtn = new JButton("Register as VIP");
        registerBtn.setBackground(new Color(173, 216, 230));
        registerBtn.addActionListener(e -> showRegistrationDialog());
        topPanel.add(registerBtn);

        add(topPanel, BorderLayout.NORTH);

        // center panel: the interactive grid 
        mainGridPanel = new JPanel();
        mainGridPanel.setLayout(new BoxLayout(mainGridPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(mainGridPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refreshGrid();
    }

    private void refreshGrid() {
        mainGridPanel.removeAll();

        List<ParkingSpot> spots = ParkingService.getInstance().getParkingLotStatus();

        for (int floor = 5; floor >= 1; floor--) {

            JPanel floorPanel = new JPanel(new GridLayout(0, 6, 10, 10));

            TitledBorder border = BorderFactory.createTitledBorder("Floor " + floor + " (Click a spot to park)");
            border.setTitleFont(new Font("Arial", Font.BOLD, 14));
            floorPanel.setBorder(border);

            String floorPrefix = "F" + floor + "-";

            for (ParkingSpot spot : spots) {
                if (spot.getSpotId().startsWith(floorPrefix)) {
                    JButton spotBtn = createInteractionButton(spot);
                    floorPanel.add(spotBtn);
                }
            }

            mainGridPanel.add(floorPanel);
            mainGridPanel.add(Box.createVerticalStrut(10));
        }

        mainGridPanel.revalidate();
        mainGridPanel.repaint();
    }

    // Polymorphism: build correct Vehicle subclass
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

        String priceText = String.format("RM %.2f/hr", spot.getType().getRate());
        String statusText = spot.isOccupied() ? "OCCUPIED" : "AVAILABLE";

        btn.setText("<html><center><b>" + spot.getSpotId() + "</b><br>"
                + spot.getType() + "<br>"
                + priceText + "<br>"
                + statusText + "</center></html>");

        if (spot.isOccupied()) {
            btn.setBackground(Color.RED);
            btn.setEnabled(false);
            return btn;
        }

        // AVAILABLE:
        String currentPlate = plateField.getText().trim().toUpperCase();
        Vehicle preview = buildVehicleFromUI("TEMP");
        boolean isVip = !currentPlate.isEmpty() && new dao.ParkingSpotDAO().isVip(currentPlate);

        // VIP Logic for RESERVED
        if (spot.getType() == SpotType.RESERVED) {
            if (isVip) {
                btn.setEnabled(true); // always enabled for VIP regardless of vehicle type
                btn.setBackground(new Color(173, 216, 230)); // blue
                btn.setText("<html><center><b>" + spot.getSpotId()
                        + "</b><br>RESERVED<br><b>VIP ACCESS</b></center></html>");
            } else {
                btn.setEnabled(false);
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setText("<html><center><b>" + spot.getSpotId()
                        + "</b><br>RESERVED<br>(VIP ONLY)</center></html>");
            }

        } else {
            // standard suitability check for non-reserved spots
            boolean suitable = preview.canParkIn(spot.getType());
            if (!suitable) {
                btn.setEnabled(false);
                btn.setBackground(Color.LIGHT_GRAY);
            } else {
                btn.setEnabled(true);
                btn.setBackground(new Color(144, 238, 144)); // Green

                if (spot.getType() == SpotType.HANDICAPPED) {
                    btn.setBackground(new Color(255, 255, 153)); // Yellow
                }
            }
        }

        btn.addActionListener(e -> handleSpotClick(spot));
        return btn;
    }

    private void handleSpotClick(ParkingSpot spot) {

        String plate = plateField.getText().trim().toUpperCase();

        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Plate Number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean isVip = new dao.ParkingSpotDAO().isVip(plate);

        if (spot.getType() == SpotType.RESERVED && !isVip) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Access Denied!\nPlate " + plate + " is NOT registered as VIP.\n\nWould you like to register now?",
                    "VIP Only",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) showRegistrationDialog();
            return;
        }

        Vehicle v = buildVehicleFromUI(plate);

        // if user selected Handicapped type, ask card for ANY spot
        boolean isOkuCardholder = false;
        String selectedType = ((String) typeCombo.getSelectedItem());

        if ("Handicapped".equalsIgnoreCase(selectedType)) {
            int ok = JOptionPane.showConfirmDialog(
                    this,
                    "You selected HANDICAPPED vehicle.\nDo you have an OKU card?",
                    "OKU Verification",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            isOkuCardholder = (ok == JOptionPane.YES_OPTION);
        }

        // VIP reserved bypass suitability
        if (!(spot.getType() == SpotType.RESERVED && isVip)) {
            if (!v.canParkIn(spot.getType())) {
                JOptionPane.showMessageDialog(this,
                        "This vehicle type cannot park in " + spot.getType() + " spot.",
                        "Invalid Spot",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // pass the popup result into ParkingService so it saves is_oku_cardholder
        model.Ticket ticket = ParkingService.getInstance()
                .parkVehicleAt(v, spot.getSpotId(), isOkuCardholder);

        if (ticket != null) {
            JOptionPane.showMessageDialog(this,
                    "Parked Successfully!\n\n"
                            + "Ticket: " + ticket.getTicketId() + "\n"
                            + "Spot: " + ticket.getSpotId() + "\n"
                            + "Entry Time: " + ticket.getEntryTime().format(DT_FMT),
                    "Ticket Generated",
                    JOptionPane.INFORMATION_MESSAGE);

            plateField.setText("");
            refreshGrid();
        } else {
            JOptionPane.showMessageDialog(this, "Parking Failed: Spot mismatch or occupied.");
        }
    }

    // VIP Registration Popup
    private void showRegistrationDialog() {
        JTextField plateInput = new JTextField();
        JTextField nameInput = new JTextField();

        Object[] message = {
                "VIP Plate Number:", plateInput,
                "Owner Name:", nameInput
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Register New VIP", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String plate = plateInput.getText().trim().toUpperCase();
            String name = nameInput.getText().trim();

            if (!plate.isEmpty() && !name.isEmpty()) {
                boolean saved = new dao.ParkingSpotDAO().registerVip(plate, name);
                if (saved) {
                    JOptionPane.showMessageDialog(this, "Success! " + plate + " is now a VIP.");
                    // put plate back into main field 
                    plateField.setText(plate);
                    refreshGrid(); //unlock reserved instantly
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Plate already registered!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}