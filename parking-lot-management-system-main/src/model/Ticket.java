package model;

import java.time.LocalDateTime;

public class Ticket {
    private final String ticketId;   // T-PLATE-TIMESTAMP
    private final String spotId;
    private final LocalDateTime entryTime;

    public Ticket(String ticketId, String spotId, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.spotId = spotId;
        this.entryTime = entryTime;
    }

    public String getTicketId() { return ticketId; }
    public String getSpotId() { return spotId; }
    public LocalDateTime getEntryTime() { return entryTime; }
}
