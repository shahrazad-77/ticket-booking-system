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

        int amount = request.getTickets() * request.getPricePerTicket();
        String transactionId = paymentGateway.processPayment(request.getCustomerId(), amount);

        return transactionId;
    }
}