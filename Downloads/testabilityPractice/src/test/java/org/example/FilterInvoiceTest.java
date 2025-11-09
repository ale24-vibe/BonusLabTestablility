package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FilterInvoiceTest {

    @Test
    void filterInvoiceTest() {
        // Integration test using the real Database and QueryInvoicesDAO (no stubbing).
        // 1) Create FilterInvoice with the no-arg constructor which creates a Database and a DAO internally.
        // 2) Use that Database to ensure the invoice table exists, clear prior data, and insert controlled rows.
        // 3) Call lowValueInvoices() and assert that only invoices with value < 100 are returned.
        FilterInvoice filter = new FilterInvoice();

        // Use the Database instance created inside FilterInvoice to prepare data
        Database db = filter.db;

        // Ensure the invoice table exists (idempotent) and commit
        db.withSql(() -> {
            try (var ps = db.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS invoice (name VARCHAR(255), value INT)")) {
                ps.execute();
                db.getConnection().commit();
            }
            return null;
        });

        // Use a DAO on the same DB to manage test data
        QueryInvoicesDAO dao = new QueryInvoicesDAO(db);
        dao.clear(); // remove any previous data so test is deterministic

        // Insert one low-value and one high-value invoice
        dao.save(new Invoice("customer-low", 50));   // low value -> should be returned
        dao.save(new Invoice("customer-high", 150)); // high value -> should NOT be returned

        // Call the method under test (uses internal DAO that queries the same DB)
        List<Invoice> low = filter.lowValueInvoices();

        // Verify: only invoices with value < 100 are returned
        assertThat(low)
                .isNotNull()
                .hasSize(1)
                .allMatch(i -> i.getValue() < 100);
    }
}