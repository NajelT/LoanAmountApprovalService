package com.approvalservice.app.util;

import com.approvalservice.app.enums.RequestCheckMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RequestPropertyCheckerTest
{
    private static final String CUSTOMER_ID = "AA-1234-817";
    private static final long LOAN_AMOUNT = 5000;
    private static final ArrayList<String> LOAN_APPROVERS =
            new ArrayList<>(Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene"));

    @Test
    void checkPendingLoanBodyOKTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, LOAN_APPROVERS);

        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.REQUEST_OK);
    }

    @Test
    void checkPendingLoanBodyEmptyIdTest()
    {
        PendingLoan loan = new PendingLoan("", LOAN_AMOUNT, LOAN_APPROVERS);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.EMPTY_CUSTOMER_ID);
    }

    @Test
    void checkPendingLoanBodyWrongIdTest()
    {
        String customerId = "111-1111xxx";
        PendingLoan loan = new PendingLoan(customerId, LOAN_AMOUNT, LOAN_APPROVERS);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.WRONG_CUSTOMER_ID);
    }

    @Test
    void checkPendingLoanBodyWrongApproversQtyTest()
    {
        ArrayList<String> approvers = new ArrayList<>(
                        Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene", "Dionizas Ulinskas"));
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, approvers);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.WRONG_LOAN_APPROVERS);
    }

    @Test
    void checkPendingLoanBodyEmptyApproversQtyTest()
    {
        ArrayList<String> approvers = new ArrayList<>();
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, approvers);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.APPROVERS_EMPTY);
    }

    @Test
    void checkPendingLoanBodyNullApproversTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, null);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.APPROVERS_EMPTY);
    }

    @Test
    void checkPendingLoanBodyEmptyLoanAmountTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, 0, LOAN_APPROVERS);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.WRONG_LOAN_AMOUNT);
    }

    @Test
    void checkPendingLoanBodyNegativeLoanAmountTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, -1000, LOAN_APPROVERS);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(loan);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.WRONG_LOAN_AMOUNT);
    }

    @Test
    void checkApprovalRequestTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, "Mamertas Juronis", true);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkApprovalRequest(approval);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.REQUEST_OK);
    }

    @Test
    void checkApprovalRequestEmptyApproverTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, "", true);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkApprovalRequest(approval);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.APPROVER_EMPTY);
    }

    @Test
    void checkApprovalRequestNullApproverTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, null, true);
        RequestCheckMessages checkResult = RequestPropertyChecker.checkApprovalRequest(approval);

        assertThat(checkResult).isEqualTo(RequestCheckMessages.APPROVER_EMPTY);
    }
}


