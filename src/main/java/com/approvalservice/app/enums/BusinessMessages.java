package com.approvalservice.app.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Frequent API responses (reactions) on requests. Appears when the request was accepted.
 */

@Getter
@AllArgsConstructor
public enum BusinessMessages
{
    CUSTOMER_CONTRACT_PENDING("This contract currently in loan approval process.", HttpStatus.CONFLICT),
    CONTRACT_SENT_TO_APPROVAL("The contract has been sent to a loan approve manager.", HttpStatus.CREATED),
    MAX_LOANS_PER_CUSTOMER("This customer already have unsecured loans. Can't approve.", HttpStatus.OK),
    NO_CONTRACTS_FOR_STAT("No recently approved contracts found, try again later.", HttpStatus.OK),
    NO_PENDING_CONTRACTS("No pending contracts found for this customer.", HttpStatus.OK),
    CUSTOMER_NOT_FOUND("Customer with provided ID is not found.", HttpStatus.NOT_FOUND),
    NOT_APPROVED("Loan contract request was declined.", HttpStatus.OK),
    INAPPROPRIATE_APPROVER("This Approver has no permission working with provided contract.", HttpStatus.OK),
    SUCCESSFULLY_SENT("Loan contract request was approved and successfully sent to the customer.", HttpStatus.OK);

    private final String result;
    private final HttpStatus status;
}
