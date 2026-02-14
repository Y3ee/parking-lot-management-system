import javax.swing.*;
import java.awt.*;
import service.ParkingService;
import ui.AdminPanel;
import ui.EntryPanel;
import ui.ExitPanel;
import ui.ReportingPanel;

public class Main {
    public static void main(String[] args) {
        // Initialize the Singleton ParkingService to ensure database is ready
        ParkingService.getInstance(); 

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Parking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 900); 

            // Main Tabbed Pane structure
            JTabbedPane tabs = new JTabbedPane();
            
            // 1. Customer-Facing Tabs (Always Visible)
            tabs.addTab("Entry / Park", new EntryPanel());
            tabs.addTab("Exit / Payment", new ExitPanel());

            // 2. Admin-Only Login Button
            JButton adminLoginBtn = new JButton("🔒 Admin Access");
            adminLoginBtn.setFocusPainted(false);
            adminLoginBtn.setBackground(new Color(240, 240, 240));

            adminLoginBtn.addActionListener(e -> {
                // Use JPasswordField to hide the characters as the admin types
                JPasswordField pwdField = new JPasswordField();
                Object[] message = {"Enter Admin Password:", pwdField};
                
                // ✅ FIXED: Changed PROTECTED_MESSAGE to QUESTION_MESSAGE
                int action = JOptionPane.showConfirmDialog(
                        frame, 
                        message, 
                        "Admin Authentication", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE
                );

                if (action == JOptionPane.OK_OPTION) {
                    String password = new String(pwdField.getPassword());
                    
                    // Simple authentication check
                    if ("admin123".equals(password)) {
                        // Dynamically add the restricted panels
                        tabs.addTab("Admin / View Status", new AdminPanel());
                        tabs.addTab("Reports", new ReportingPanel());
                        
                        // Switch to the Admin tab automatically
                        tabs.setSelectedIndex(2);
                        
                        // Hide the login button once authenticated
                        adminLoginBtn.setVisible(false);
                        JOptionPane.showMessageDialog(frame, "Access Granted", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid Password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Layout Management
            JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            topBar.add(adminLoginBtn);

            frame.setLayout(new BorderLayout());
            frame.add(topBar, BorderLayout.NORTH);
            frame.add(tabs, BorderLayout.CENTER);
            
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}