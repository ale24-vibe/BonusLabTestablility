package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SAP_BasedInvoiceSenderTest {

    // Mocks used in tests
    FilterInvoice filter;
    SAP sap; // mock the real SAP type from your production code
    SAP_BasedInvoiceSender sender;

    @BeforeEach
    void setUp() {
        // Create Mockito mocks for both dependencies.
        // filter is stubbed to return controlled lists in each test.
        // sap is verified to ensure send(...) is invoked (or not).
        filter = mock(FilterInvoice.class);
        sap = mock(SAP.class);

        // Construct the sender with injected mocks (constructor-injection).
        // This assumes SAP_BasedInvoiceSender has a constructor like:
        //   public SAP_BasedInvoiceSender(FilterInvoice filter, SAP sap) { ... }
        sender = new SAP_BasedInvoiceSender(filter, sap);
    }

    @Test
    void testWhenLowInvoicesSent() {
        // Arrange: stub filter.lowValueInvoices() to return a controlled list of invoices
        List<Invoice> invoices = Arrays.asList(
                new Invoice("cust1", 50),
                new Invoice("cust2", 75)
        );
        when(filter.lowValueInvoices()).thenReturn(invoices);

        // Act: call the method under test
        sender.sendLowValuedInvoices();

        // Assert: verify sap.send(...) is called once for each invoice returned by filter.
        // We verify exact calls with the specific Invoice instances returned by the stub.
        verify(sap, times(1)).send(invoices.get(0));
        verify(sap, times(1)).send(invoices.get(1));

        // Ensure no other interactions occurred with the SAP mock.
        verifyNoMoreInteractions(sap);

        // Also verify that the filter was queried exactly once.
        verify(filter, times(1)).lowValueInvoices();
    }

    @Test
    void testWhenNoInvoices() {
        // Arrange: stub filter.lowValueInvoices() to return an empty list
        when(filter.lowValueInvoices()).thenReturn(Collections.emptyList());

        // Act: call the method under test
        sender.sendLowValuedInvoices();

        // Assert: verify that sap.send(...) is never called when there are no invoices
        verify(sap, never()).send(any(Invoice.class));

        // And verify filter.lowValueInvoices() was called once to check for invoices
        verify(filter, times(1)).lowValueInvoices();

        // No interactions with sap beyond the expectation above
        verifyNoMoreInteractions(sap);
    }
}