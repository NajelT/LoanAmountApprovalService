package com.approvalservice.app.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Frequent API error responses, which makes further API request process not possible. Mostly 4xx.
 */

@Getter
@AllArgsConstructor
public enum RequestCheckMessages
{
    WRONG_CUSTOMER_ID("Wrong customer id. Should be in 'XX-XXXX-XXX' pattern", false),
    EMPTY_CUSTOMER_ID("Customer ID cannot be empty", false),
    WRONG_LOAN_AMOUNT("Empty or wrong loan amount field.", false),
    WRONG_LOAN_APPROVERS("Loan approvers: should be no more than three persons", false),
    APPROVERS_NOT_FOUND("One or more approvers was not found in the database", false),
    APPROVER_NOT_FOUND("Approver not found or has no permission to work with this loan contract", false),
    APPROVER_EMPTY("Approver value is empty or null", false),
    APPROVERS_EMPTY("Approvers value is empty or null", false),
    REQUEST_OK("Success", true);

    private final String result;
    private final boolean success;
}
