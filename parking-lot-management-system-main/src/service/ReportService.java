package service;

import dao.ReportDAO;
import model.*;

//ui report
public class ReportService {
    private static ReportService instance;
    private final ReportDAO reportDAO;
    
    private ReportService() {
        this.reportDAO = new ReportDAO();
    }
    
    public static ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    //all reports
    public OccupancyReport generateOccupancyReport() {
        return reportDAO.getOccupancyReport();
    }
    
    public RevenueReport generateRevenueReport() {
        return reportDAO.getRevenueReport();
    }
    
    public FineReport generateFineReport() {
        return reportDAO.getFineReport();
    }
    
    public VehicleListReport generateVehicleListReport() {
        return reportDAO.getVehicleListReport();
    }
}
