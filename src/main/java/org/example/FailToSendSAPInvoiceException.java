package org.example;

/**
 * Made an unchecked exception (extends RuntimeException) so it can be used easily in tests
 * with Mockito.doThrow(...) without needing to change method signatures in existing interfaces.
 */
public class FailToSendSAPInvoiceException extends RuntimeException {
    public FailToSendSAPInvoiceException() {
        super();
    }

    public FailToSendSAPInvoiceException(String message) {
        super(message);
    }

    public FailToSendSAPInvoiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailToSendSAPInvoiceException(Throwable cause) {
        super(cause);
    }
}