package model;
import java.util.Map;

//this is for calclates occupancy by floor, type and overall.

public class OccupancyReport {
    private final int totalSpots;
    private final int occupiedSpots;
    private final double overallOccupancyRate;
    private final Map<Integer, FloorOccupancy> floorOccupancy;
    private final Map<SpotType, TypeOccupancy> typeOccupancy;

    public OccupancyReport(int totalSpots, int occupiedSpots,
                           Map<Integer, FloorOccupancy> floorOccupancy,
                           Map<SpotType, TypeOccupancy> typeOccupancy) {
        this.totalSpots = totalSpots;
        this.occupiedSpots = occupiedSpots;
        this.overallOccupancyRate = totalSpots == 0 ? 
            (double) occupiedSpots / totalSpots * 100 : 0.0;
        this.floorOccupancy = floorOccupancy;
        this.typeOccupancy = typeOccupancy;
    }

    public int getTotalSpots() { return totalSpots; }
    public int getOccupiedSpots() { return occupiedSpots; }
    public int getAvailableSpots() { return totalSpots - occupiedSpots; }
    public double getOverallOccupancyRate() { return overallOccupancyRate; }
    public Map<Integer, FloorOccupancy> getFloorOccupancy() { return floorOccupancy; }
    public Map<SpotType, TypeOccupancy> getTypeOccupancy() { return typeOccupancy; }

    public String getSummary() {
    return String.format("Overall: %d/%d spots occupied (%.1f%%)\nAvailable: %d spots",
        occupiedSpots, totalSpots, overallOccupancyRate, getAvailableSpots());
    } 

    //inner class of floor occupancy
    public static class FloorOccupancy {
        private final int floorNumber;
        private final int total;
        private final int occupied;

        public FloorOccupancy(int floorNumber, int total, int occupied) {
            this.floorNumber = floorNumber;
            this.total = total;
            this.occupied = occupied;
        }

        public int getFloorNumber() { return floorNumber; }
        public int getTotal() { return total; }
        public int getOccupied() { return occupied; }
        public int getAvailable() { return total - occupied; }
        public double getOccupancyRate() {
            return total > 0 ? (double) occupied / total * 100 : 0.0;
        }
    }

    //type-specific occupancy
    public static class TypeOccupancy {
        private final SpotType type;
        private final int total;
        private final int occupied;

        public TypeOccupancy(SpotType type, int total, int occupied) {
            this.type = type;
            this.total = total;
            this.occupied = occupied;
        }

        public SpotType getType() { return type; }
        public int getTotal() { return total; }
        public int getOccupied() { return occupied; }
        public int getAvailable() { return total - occupied; }
        public double getOccupancyRate() {
            return total > 0 ? (double) occupied / total * 100 : 0.0;
        }
    }
}
