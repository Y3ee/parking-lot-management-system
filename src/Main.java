import java.awt.*;
import javax.swing.*;
import service.ParkingService;
import ui.AdminPanel;
import ui.EntryPanel;
import ui.ExitPanel;
import ui.ReportingPanel;

public class Main {
    public static void main(String[] args) {
        // initialize the Singleton ParkingService to ensure database is ready
        ParkingService.getInstance(); 

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Parking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 900); 

            // main tabbed pane structure
            JTabbedPane tabs = new JTabbedPane();
            
            // 1. customer-facing tabs
            tabs.addTab("Entry / Park", new EntryPanel());
            tabs.addTab("Exit / Payment", new ExitPanel());

            // 2. admin-only login button
            JButton adminLoginBtn = new JButton("🔒 Admin Access");
            adminLoginBtn.setFocusPainted(false);
            adminLoginBtn.setBackground(new Color(240, 240, 240));

            adminLoginBtn.addActionListener(e -> {
                JPasswordField pwdField = new JPasswordField();
                Object[] message = {"Enter Admin Password:", pwdField};
                
                int action = JOptionPane.showConfirmDialog(
                        frame, 
                        message, 
                        "Admin Authentication", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.QUESTION_MESSAGE
                );

                if (action == JOptionPane.OK_OPTION) {
                    String password = new String(pwdField.getPassword());
                    
                    // simple authentication check
                    if ("admin123".equals(password)) {
                        // dynamically add the restricted panels
                        tabs.addTab("Admin / View Status", new AdminPanel());
                        tabs.addTab("Reports", new ReportingPanel());
                        
                        // switch to the Admin tab automatically
                        tabs.setSelectedIndex(2);
                        
                        // hide the login button once authenticated
                        adminLoginBtn.setVisible(false);
                        JOptionPane.showMessageDialog(frame, "Access Granted", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid Password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // layout management
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