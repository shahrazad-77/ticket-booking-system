package edu.course.booking;

public class BookingManager {

    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;
    private final IEventRepository eventRepository;

    public BookingManager(IPaymentGateway paymentGateway, INotificationService notificationService, IEventRepository eventRepository) {

        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
    }

    public String book(BookingRequest request) {

        if (request == null || request.getCustomerId() == null || request.getCustomerId().isBlank() || request.getEventId() == null ||
                request.getEventId().isBlank() || request.getTickets() < 1 || request.getTickets() > 5 || request.getPricePerTicket() <= 0) {
            throw new IllegalArgumentException("Invalid booking request");}

        if (eventRepository.isSoldOut(request.getEventId())) {
                throw new IllegalStateException("Event is sold out");
            }


        int amount = request.getTickets() * request.getPricePerTicket();
        String transactionId = paymentGateway.processPayment(request.getCustomerId(), amount);
        eventRepository.saveBooking(request);
        notificationService.sendConfirmation(request);

        return transactionId;
    }
}