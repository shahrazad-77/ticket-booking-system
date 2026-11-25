package edu.course.booking;

public class BookingRequest {

    private final String customerId;
    private final String eventId;
    private final int tickets;
    private final int pricePerTicket;

    public BookingRequest(String customerId, String eventId, int tickets, int pricePerTicket) {
        this.customerId = customerId;
        this.eventId = eventId;
        this.tickets = tickets;
        this.pricePerTicket = pricePerTicket;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEventId() {
        return eventId;
    }

    public int getTickets() {
        return tickets;
    }

    public int getPricePerTicket() {
        return pricePerTicket;
    }
}