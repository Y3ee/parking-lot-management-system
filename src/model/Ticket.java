package model;

import java.time.LocalDateTime;

public class Ticket {

    private String ticketId;
    private String spotId;
    private LocalDateTime entryTime;

    public Ticket(String ticketId, String spotId, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.spotId = spotId;
        this.entryTime = entryTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}
