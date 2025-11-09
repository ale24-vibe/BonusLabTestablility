package org.example;

import java.util.ArrayList;
import java.util.List;

// Class responsible for sending low-valued invoices to the SAP system
public class SAP_BasedInvoiceSender {

    private final FilterInvoice filter;  // Dependency for filtering invoices
    private final SAP sap;  // Dependency for sending invoices to the SAP system

    // Constructor that uses dependency injection to initialize the filter and sap objects
    public SAP_BasedInvoiceSender(FilterInvoice filter, SAP sap) {
        this.filter = filter;
        this.sap = sap;
    }

    // Method to send all low-valued invoices to the SAP system
    public List<Invoice> sendLowValuedInvoices() {
        List<Invoice> low = filter.lowValueInvoices();
        List<Invoice> failed = new ArrayList<>();

        if (low == null || low.isEmpty()) {
            return failed;
        }

        for (Invoice inv : low) {
            try {
                sap.send(inv);
            } catch (Exception e) {
                // Collect failed invoice and continue processing remaining invoices.
                failed.add(inv);
                // Optionally log the failure - left out here to keep code simple.
            }
        }

        return failed;
    }
}
