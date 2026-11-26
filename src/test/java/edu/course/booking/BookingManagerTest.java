package edu.course.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class BookingManagerTest {

    @Test
    void bookSuccess() {
        IPaymentGateway pg = mock(IPaymentGateway.class);
        INotificationService ns = mock(INotificationService.class);
        IEventRepository er = mock(IEventRepository.class);
        BookingManager bm = new BookingManager(pg, ns, er);
        BookingRequest req = new BookingRequest("C101", "E201", 2, 1500);

        when(er.isSoldOut("E201")).thenReturn(false);
        when(pg.processPayment("C101", 3000)).thenReturn("TXN-001");

        String res = bm.book(req);
        assertEquals("TXN-001", res);

        verify(er).isSoldOut("E201");
        verify(pg).processPayment("C101", 3000);
        verify(er).saveBooking(any());
        verify(ns).sendConfirmation(any());
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void invalidRequest(BookingRequest request) {

        IPaymentGateway pg = mock(IPaymentGateway.class);
        INotificationService ns = mock(INotificationService.class);
        IEventRepository er = mock(IEventRepository.class);

        BookingManager bm = new BookingManager(pg, ns, er);

        assertThrows(IllegalArgumentException.class, () -> bm.book(request));

        verifyNoInteractions(pg, ns, er);
    }

    static Stream<BookingRequest> invalidRequests() {
        return Stream.of(
                new BookingRequest(null, "E201", 2, 1500),
                new BookingRequest("", "E201", 2, 1500),
                new BookingRequest("C101", null, 2, 1500),
                new BookingRequest("C101", "", 2, 1500),
                new BookingRequest("C101", "E201", 0, 1500),
                new BookingRequest("C101", "E201", 6, 1500),
                new BookingRequest("C101", "E201", 2, 0),
                new BookingRequest("C101", "E201", 2, -100)
        );
    }

    @Test
    void soldOut() {

        IPaymentGateway pg = mock(IPaymentGateway.class);
        INotificationService ns = mock(INotificationService.class);
        IEventRepository er = mock(IEventRepository.class);
        BookingManager bm = new BookingManager(pg, ns, er);
        BookingRequest request = new BookingRequest("C101", "E201", 2, 1500);

        when(er.isSoldOut("E201")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> bm.book(request));

        verify(er).isSoldOut("E201");
        verifyNoInteractions(pg, ns);
        verify(er, never()).saveBooking(any());
    }
    @Test
    void paymentFailed() {
        IPaymentGateway pg = mock(IPaymentGateway.class);
        INotificationService ns = mock(INotificationService.class);
        IEventRepository er = mock(IEventRepository.class);

        BookingManager bm = new BookingManager(pg, ns, er);

        BookingRequest request = new BookingRequest("C101", "E201", 2, 1500);

        when(er.isSoldOut("E201")).thenReturn(false);
        when(pg.processPayment("C101", 3000)).thenThrow(new RuntimeException("Payment failed"));


        assertThrows(RuntimeException.class, () -> bm.book(request));

        verify(pg).processPayment("C101", 3000);
        verify(er, never()).saveBooking(any());
        verifyNoInteractions(ns);
    }
}