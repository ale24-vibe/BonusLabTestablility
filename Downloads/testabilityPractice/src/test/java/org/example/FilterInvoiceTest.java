package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FilterInvoiceTest {

    @Test void filterInvoiceTest() {

        FilterInvoice filter = new FilterInvoice();

        Database db = filter.db;

        db.withSql(() -> {
            try (var ps = db.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS invoice (name VARCHAR(255), value INT)")) {
                ps.execute();
                db.getConnection().commit();
            }
            return null;
        });

        QueryInvoicesDAO dao = new QueryInvoicesDAO(db);
        dao.clear();

        dao.save(new Invoice("customer-low", 50));   // low value -> should be returned
        dao.save(new Invoice("customer-high", 150)); // high value -> should NOT be returned

        List<Invoice> low = filter.lowValueInvoices();

        assertThat(low)
                .isNotNull()
                .hasSize(1)
                .allMatch(i -> i.getValue() < 100);
    }
}