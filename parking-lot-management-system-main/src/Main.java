import javax.swing.*; 
import service.ParkingService;
import ui.AdminPanel;
import ui.EntryPanel;
import ui.ExitPanel;
import ui.ReportingPanel;

public class Main {
    public static void main(String[] args) {
        ParkingService.getInstance(); // Ensure DB is loaded

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Parking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 1000); // Make it wider

            //Tabbed Pane
            JTabbedPane tabs = new JTabbedPane();
            
            tabs.addTab("Entry / Park", new EntryPanel());
            tabs.addTab("Exit / Payment", new ExitPanel());
            tabs.addTab("Admin / View Status", new AdminPanel());
            tabs.addTab("Reports", new ReportingPanel());

            frame.add(tabs);
            frame.setVisible(true);
        });
    }
}
