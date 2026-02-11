package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import model.ParkingSpot;
import service.ParkingService;

public class AdminPanel extends JPanel {

    private JPanel mainListPanel; // Changed from 'gridPanel' to 'mainListPanel'

    public AdminPanel() {
        setLayout(new BorderLayout());

        // 1. Header
        JLabel header = new JLabel("Parking Lot Status", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        // 2. The Main Container (Holds the 5 Floor Panels vertically)
        mainListPanel = new JPanel();
        mainListPanel.setLayout(new BoxLayout(mainListPanel, BoxLayout.Y_AXIS));
        
        // Add scrolling because 5 floors might not fit on one screen
        JScrollPane scrollPane = new JScrollPane(mainListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling
        add(scrollPane, BorderLayout.CENTER);

        // 3. Refresh Button
        JButton refreshBtn = new JButton("Refresh Status");
        refreshBtn.addActionListener(e -> loadParkingStatus());
        add(refreshBtn, BorderLayout.SOUTH);

        // Load data immediately
        loadParkingStatus();
    }

    private void loadParkingStatus() {
        mainListPanel.removeAll(); 

        List<ParkingSpot> allSpots = ParkingService.getInstance().getParkingLotStatus();

        // Loop through floors 5 down to 1 (To match the visual of Top Floor on Top)
        for (int floor = 5; floor >= 1; floor--) {
            
            // A. Create a Panel for THIS Floor
            JPanel floorPanel = new JPanel();
            // 2 rows, 10 columns (fits 20 spots nicely)
            floorPanel.setLayout(new GridLayout(0, 6, 10, 10));   

            // Add a Border with Title (e.g., "Floor 5")
            TitledBorder border = BorderFactory.createTitledBorder("Floor " + floor);
            border.setTitleFont(new Font("Arial", Font.BOLD, 14));
            floorPanel.setBorder(border);

            // B. Filter spots just for this floor
            String floorPrefix = "F" + floor + "-"; // e.g., "F1-"
            
            for (ParkingSpot spot : allSpots) {
                if (spot.getSpotId().startsWith(floorPrefix)) {
                    JButton spotBtn = createSpotButton(spot);
                    floorPanel.add(spotBtn);
                }
            }

            // C. Add this floor to the main list
            mainListPanel.add(floorPanel);
            
            // D. Add a spacer/separator between floors
            mainListPanel.add(Box.createVerticalStrut(20));
        }

        mainListPanel.revalidate();
        mainListPanel.repaint();
    }

    // Helper method to create the button design
    private JButton createSpotButton(ParkingSpot spot) {
        JButton spotBtn = new JButton();
        spotBtn.setPreferredSize(new Dimension(100, 85)); // Slightly taller for extra text
        spotBtn.setOpaque(true);
        spotBtn.setBorderPainted(true);
        spotBtn.setFont(new Font("Arial", Font.PLAIN, 10));

        // 1. Format the Price
        // "RM 2.00/hr"
        String priceText = String.format("RM %.2f/hr", spot.getType().getRate());

        // 2. Build the HTML Text
        String statusText = spot.isOccupied() ? "Occupied" : "Available";
        String plateText = spot.isOccupied() ? "<br><b style='color:yellow'>" + spot.getVehiclePlate() + "</b>" : "";
        
        spotBtn.setText("<html><center>" + 
            "<b>" + spot.getSpotId() + "</b><br>" + 
            spot.getType() + "<br>" + 
            "<b>" + priceText + "</b><br>" + // <--- Added Price Here
            statusText + 
            plateText + 
            "</center></html>");

        // 3. Color Logic (Same as before)
        if (spot.isOccupied()) {
            spotBtn.setBackground(new Color(255, 102, 102)); // Red
        } else {
            switch (spot.getType()) {
                case COMPACT:
                    spotBtn.setBackground(new Color(144, 238, 144)); // Green
                    break;
                case RESERVED:
                    spotBtn.setBackground(new Color(173, 216, 230)); // Blue
                    break;
                case HANDICAPPED:
                    spotBtn.setBackground(new Color(255, 255, 153)); // Yellow (distinct for Handicapped)
                    break;
                default: 
                    spotBtn.setBackground(Color.WHITE);
            }
        }
        return spotBtn;
    }
}