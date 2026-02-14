package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.*;
import model.FineReport.OutstandingFine;
import model.OccupancyReport.FloorOccupancy;
import model.OccupancyReport.TypeOccupancy;
import service.ReportService;
import java.time.format.DateTimeFormatter;

public class ReportingPanel extends JPanel{
    private final ReportService reportService;
    private JTabbedPane reportTabs;

    public ReportingPanel() {
        this.reportService = ReportService.getInstance();
        setLayout(new BorderLayout());
        
        JLabel header = new JLabel("Reports & Analytics", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);
        
        // pane for different reports
        reportTabs = new JTabbedPane();
        reportTabs.addTab("Occupancy", createOccupancyPanel());
        reportTabs.addTab("Revenue", createRevenuePanel());
        reportTabs.addTab("Fines", createFinePanel());
        reportTabs.addTab("Current Vehicles", createVehicleListPanel());
        
        add(reportTabs, BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton(" Refresh All Reports");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
        refreshBtn.addActionListener(e -> refreshAllReports());
        add(refreshBtn, BorderLayout.SOUTH);
    }

    //occupancy report
    private JPanel createOccupancyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea summaryArea = new JTextArea(5, 40);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(summaryArea), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // floor occupancy table
        JTable floorTable = new JTable();
        splitPane.setLeftComponent(new JScrollPane(floorTable));
        
        // type occupancy table
        JTable typeTable = new JTable();
        splitPane.setRightComponent(new JScrollPane(typeTable));
        
        splitPane.setDividerLocation(300);
        panel.add(splitPane, BorderLayout.CENTER);
        

        JButton loadBtn = new JButton("Load Occupancy Report");
        loadBtn.addActionListener(e -> {
            OccupancyReport report = reportService.generateOccupancyReport();
            if (report != null) {
                summaryArea.setText(report.getSummary());
                populateFloorTable(floorTable, report);
                populateTypeTable(typeTable, report);
            }
        });
        panel.add(loadBtn, BorderLayout.SOUTH);
        

        loadBtn.doClick();
        
        return panel;
    }
    
    private void populateFloorTable(JTable table, OccupancyReport report) {
        String[] columns = {"Floor", "Total", "Occupied", "Available", "Rate %"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (FloorOccupancy floor : report.getFloorOccupancy().values()) {
            model.addRow(new Object[]{
                "Floor " + floor.getFloorNumber(),
                floor.getTotal(),
                floor.getOccupied(),
                floor.getAvailable(),
                String.format("%.1f%%", floor.getOccupancyRate())
            });
        }
        
        table.setModel(model);
    }
    
    private void populateTypeTable(JTable table, OccupancyReport report) {
        String[] columns = {"Spot Type", "Total", "Occupied", "Available", "Rate %"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (TypeOccupancy type : report.getTypeOccupancy().values()) {
            model.addRow(new Object[]{
                type.getType().toString(),
                type.getTotal(),
                type.getOccupied(),
                type.getAvailable(),
                String.format("%.1f%%", type.getOccupancyRate())
            });
        }
        
        table.setModel(model);
    }


    //revenue report
    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextArea revenueArea = new JTextArea(20, 50);
        revenueArea.setEditable(false);
        revenueArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panel.add(new JScrollPane(revenueArea), BorderLayout.CENTER);
        
        JPanel breakdownPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        breakdownPanel.setBorder(new TitledBorder("Quick Stats"));
        
        JLabel totalLabel = new JLabel("Total: RM 0.00", SwingConstants.CENTER);
        JLabel parkingLabel = new JLabel("Parking: RM 0.00", SwingConstants.CENTER);
        JLabel fineLabel = new JLabel("Fines: RM 0.00", SwingConstants.CENTER);
        JLabel avgLabel = new JLabel("Avg/Trans: RM 0.00", SwingConstants.CENTER);
        
        JLabel todayLabel = new JLabel("Today: RM 0.00", SwingConstants.CENTER);
        JLabel weekLabel = new JLabel("Week: RM 0.00", SwingConstants.CENTER);
        JLabel monthLabel = new JLabel("Month: RM 0.00", SwingConstants.CENTER);
        JLabel transLabel = new JLabel("Trans: 0", SwingConstants.CENTER);
        
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        parkingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fineLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        avgLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        breakdownPanel.add(totalLabel);
        breakdownPanel.add(parkingLabel);
        breakdownPanel.add(fineLabel);
        breakdownPanel.add(avgLabel);
        breakdownPanel.add(todayLabel);
        breakdownPanel.add(weekLabel);
        breakdownPanel.add(monthLabel);
        breakdownPanel.add(transLabel);
        
        panel.add(breakdownPanel, BorderLayout.NORTH);
        
    
        JButton loadBtn = new JButton("Generate Revenue Report");
        loadBtn.addActionListener(e -> {
            RevenueReport report = reportService.generateRevenueReport();
            if (report != null) {
                revenueArea.setText(report.getSummary());
                
                totalLabel.setText(String.format("Total: RM %.2f", report.getTotalRevenue()));
                parkingLabel.setText(String.format("Parking: RM %.2f", report.getTotalParkingRevenue()));
                fineLabel.setText(String.format("Fines: RM %.2f", report.getTotalFineRevenue()));
                avgLabel.setText(String.format("Avg: RM %.2f", report.getAverageRevenuePerTransaction()));
                
                todayLabel.setText(String.format("Today: RM %.2f", report.getTodayRevenue()));
                weekLabel.setText(String.format("Week: RM %.2f", report.getWeekRevenue()));
                monthLabel.setText(String.format("Month: RM %.2f", report.getMonthRevenue()));
                transLabel.setText(String.format("Trans: %d", report.getTotalTransactions()));
            }
        });
        panel.add(loadBtn, BorderLayout.SOUTH);
        
        loadBtn.doClick();
        
        return panel;
    }

    //fine report
    private JPanel createFinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        summaryPanel.setBorder(new TitledBorder("Fine Statistics"));
        
        JLabel unpaidLabel = new JLabel("Unpaid: RM 0.00", SwingConstants.CENTER);
        JLabel paidLabel = new JLabel("Paid: RM 0.00", SwingConstants.CENTER);
        JLabel totalLabel = new JLabel("Total: RM 0.00", SwingConstants.CENTER);
        JLabel vehiclesLabel = new JLabel("Vehicles: 0", SwingConstants.CENTER);
        JLabel rateLabel = new JLabel("Collection: 0%", SwingConstants.CENTER);
        
        unpaidLabel.setFont(new Font("Arial", Font.BOLD, 14));
        unpaidLabel.setForeground(Color.RED);
        paidLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        paidLabel.setForeground(new Color(0, 128, 0));
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        vehiclesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        summaryPanel.add(unpaidLabel);
        summaryPanel.add(paidLabel);
        summaryPanel.add(totalLabel);
        summaryPanel.add(vehiclesLabel);
        summaryPanel.add(rateLabel);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Outstanding fines table
        JTable fineTable = new JTable();
        fineTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(fineTable), BorderLayout.CENTER);
        
        JButton loadBtn = new JButton("Load Fine Report");
        loadBtn.addActionListener(e -> {
            FineReport report = reportService.generateFineReport();
            if (report != null) {
                
                unpaidLabel.setText(String.format("Unpaid: RM %.2f", report.getTotalUnpaidFines()));
                paidLabel.setText(String.format("Paid: RM %.2f", report.getTotalPaidFines()));
                totalLabel.setText(String.format("Total: RM %.2f", report.getTotalFines()));
                vehiclesLabel.setText(String.format("Vehicles: %d", report.getNumberOfVehiclesWithFines()));
                rateLabel.setText(String.format("Collection: %.1f%%", report.getCollectionRate()));
                
                populateFineTable(fineTable, report);
            }
        });
        panel.add(loadBtn, BorderLayout.SOUTH);
        

        loadBtn.doClick();
        
        return panel;
    }
    
    private void populateFineTable(JTable table, FineReport report) {
        String[] columns = {"Rank", "Plate Number", "Fine Count", "Total Amount (RM)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        int rank = 1;
        for (OutstandingFine fine : report.getOutstandingFines()) {
            model.addRow(new Object[]{
                rank++,
                fine.getPlateNumber(),
                fine.getFineCount(),
                String.format("%.2f", fine.getAmount())
            });
        }
        
        table.setModel(model);
    }

    //vehicle list report
    private JPanel createVehicleListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        

        JLabel countLabel = new JLabel("Currently Parked: 0 vehicles", SwingConstants.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(countLabel, BorderLayout.NORTH);
        
        // vehicle table
        JTable vehicleTable = new JTable();
        vehicleTable.setFont(new Font("Monospaced", Font.PLAIN, 11));
        panel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);
        
        
        JButton loadBtn = new JButton("Refresh Vehicle List");
        loadBtn.addActionListener(e -> {
            VehicleListReport report = reportService.generateVehicleListReport();
            if (report != null) {
                countLabel.setText("Currently Parked: " + report.getTotalCount() + " vehicles");
                populateVehicleTable(vehicleTable, report);
            }
        });
        panel.add(loadBtn, BorderLayout.SOUTH);
        
        loadBtn.doClick();
        
        return panel;
    }
    
    private void populateVehicleTable(JTable table, VehicleListReport report) {

        DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");

        String[] columns = {"Plate", "Type", "Spot ID", "Spot Type", "Entry Time", "Hours", "Current Fee (RM)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        for (VehicleListReport.ParkedVehicle vehicle : report.getParkedVehicles()) {
            model.addRow(new Object[]{
                vehicle.getPlateNumber(),
                vehicle.getVehicleType(),
                vehicle.getSpotId(),
                vehicle.getSpotType(),
                vehicle.getEntryTime().format(DT_FMT),
                vehicle.getHoursParked(),
                String.format("%.2f", vehicle.getCurrentFee())
            });
        }
        
        table.setModel(model);
    }

    //refresh all reports
    private void refreshAllReports() {
        int currentTab = reportTabs.getSelectedIndex();
        Component selectedComp = reportTabs.getComponentAt(currentTab);
        
        if (selectedComp instanceof JPanel) {
            JPanel panel = (JPanel) selectedComp;
            for (Component child : getAllComponents(panel)) {
                if (child instanceof JButton) {
                    JButton btn = (JButton) child;
                    // Only click the button if it's a "Load" or "Generate" button
                    if (btn.getText().contains("Load") || btn.getText().contains("Generate") || btn.getText().contains("Refresh")) {
                        btn.doClick();
                        break; 
                    }
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, "refresh complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    

    
    private java.util.List<Component> getAllComponents(Container container) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        for (Component comp : container.getComponents()) {
            list.add(comp);
            if (comp instanceof Container) {
                list.addAll(getAllComponents((Container) comp));
            }
        }
        return list;
    }

}
