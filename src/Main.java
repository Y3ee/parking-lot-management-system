import javax.swing.*; // Import this
import service.ParkingService;
import ui.AdminPanel;
import ui.EntryPanel;

public class Main {
    public static void main(String[] args) {
        ParkingService.getInstance(); // Ensure DB is loaded

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Parking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 1000); // Make it wider

            // Create a Tabbed Pane (Professional Look)
            JTabbedPane tabs = new JTabbedPane();
            
            tabs.addTab("Entry / Park", new EntryPanel());
            tabs.addTab("Admin / View Status", new AdminPanel());

            frame.add(tabs);
            frame.setVisible(true);
        });
    }
}