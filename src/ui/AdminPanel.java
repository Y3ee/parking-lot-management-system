package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import model.FixedFineScheme;
import model.HourlyFineScheme;
import model.ParkingSpot;
import model.ProgressiveFineScheme;
import service.ParkingService;

public class AdminPanel extends JPanel {

    private JPanel mainListPanel; 

    public AdminPanel() {
        setLayout(new BorderLayout());

        // 1. header
        JLabel header = new JLabel("Parking Lot Status", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        // fine scheme selector (admin only)
        JPanel schemePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        schemePanel.add(new JLabel("Fine Scheme:"));

        String[] schemes = {"Fixed", "Progressive", "Hourly"};
        JComboBox<String> schemeCombo = new JComboBox<>(schemes);
        schemePanel.add(schemeCombo);

        JButton applyBtn = new JButton("Apply Scheme");
        applyBtn.setBackground(new Color(173, 216, 230)); 
        schemePanel.add(applyBtn);

        applyBtn.addActionListener(e -> {
            String selected = (String) schemeCombo.getSelectedItem();

            if ("Fixed".equals(selected)) {
                ParkingService.getInstance().getFineService().setScheme(new FixedFineScheme());
            }
            else if ("Progressive".equals(selected)) {
                ParkingService.getInstance().getFineService().setScheme(new ProgressiveFineScheme());
            }
            else if ("Hourly".equals(selected)) {
                ParkingService.getInstance().getFineService().setScheme(new HourlyFineScheme());
            }

            JOptionPane.showMessageDialog(this,
                    "Fine scheme set to: " + selected,
                    "Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        add(schemePanel, BorderLayout.BEFORE_FIRST_LINE);


        // 2. main container (5 floor panels vertically)
        mainListPanel = new JPanel();
        mainListPanel.setLayout(new BoxLayout(mainListPanel, BoxLayout.Y_AXIS));
        
        // add scrolling because 5 floors not fit on one screen
        JScrollPane scrollPane = new JScrollPane(mainListPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // faster scrolling
        add(scrollPane, BorderLayout.CENTER);

        // 3. refresh Button
        JButton refreshBtn = new JButton("Refresh Status");
        refreshBtn.addActionListener(e -> loadParkingStatus());
        add(refreshBtn, BorderLayout.SOUTH);

        // load data immediately
        loadParkingStatus();
    }

    private void loadParkingStatus() {
        mainListPanel.removeAll(); 

        List<ParkingSpot> allSpots = ParkingService.getInstance().getParkingLotStatus();

        // loop through floors 5 down to 1 (to match the visual of top floor on top)
        for (int floor = 5; floor >= 1; floor--) {
            
            // A. create a panel for THIS Floor
            JPanel floorPanel = new JPanel();
            // 2 rows, 10 columns (fits 20 spots nicely)
            floorPanel.setLayout(new GridLayout(0, 6, 10, 10));   

            // border with title ("Floor 5")
            TitledBorder border = BorderFactory.createTitledBorder("Floor " + floor);
            border.setTitleFont(new Font("Arial", Font.BOLD, 14));
            floorPanel.setBorder(border);

            // B. filter spots just for this floor
            String floorPrefix = "F" + floor + "-"; // "F1-"
            
            for (ParkingSpot spot : allSpots) {
                if (spot.getSpotId().startsWith(floorPrefix)) {
                    JButton spotBtn = createSpotButton(spot);
                    floorPanel.add(spotBtn);
                }
            }

            // C. add this floor to the main list
            mainListPanel.add(floorPanel);
            
            // D. add a spacer/separator between floors
            mainListPanel.add(Box.createVerticalStrut(20));
        }

        mainListPanel.revalidate();
        mainListPanel.repaint();
    }

    private JButton createSpotButton(ParkingSpot spot) {
        JButton spotBtn = new JButton();
        spotBtn.setPreferredSize(new Dimension(100, 85)); // Slightly taller for extra text
        spotBtn.setOpaque(true);
        spotBtn.setBorderPainted(true);
        spotBtn.setFont(new Font("Arial", Font.PLAIN, 10));

        // 1. format the Price
        // RM 2.00/hr
        String priceText = String.format("RM %.2f/hr", spot.getType().getRate());

        // 2. HTML Text
        String statusText = spot.isOccupied() ? "Occupied" : "Available";
        String plateText = spot.isOccupied() ? "<br><b style='color:yellow'>" + spot.getVehiclePlate() + "</b>" : "";
        
        spotBtn.setText("<html><center>" + 
            "<b>" + spot.getSpotId() + "</b><br>" + 
            spot.getType() + "<br>" + 
            "<b>" + priceText + "</b><br>" + 
            statusText + 
            plateText + 
            "</center></html>");

        // 3. color logic
        if (spot.isOccupied()) {
            spotBtn.setBackground(new Color(255, 102, 102)); // red
        } else {
            switch (spot.getType()) {
                case COMPACT:
                    spotBtn.setBackground(new Color(144, 238, 144)); // green
                    break;
                case RESERVED:
                    spotBtn.setBackground(new Color(173, 216, 230)); // blue
                    break;
                case HANDICAPPED:
                    spotBtn.setBackground(new Color(255, 255, 153)); // yellow (distinct for Handicapped)
                    break;
                default: 
                    spotBtn.setBackground(Color.WHITE);
            }
        }
        return spotBtn;
    }
}