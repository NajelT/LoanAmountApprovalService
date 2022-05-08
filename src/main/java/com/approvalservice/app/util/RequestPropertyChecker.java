package com.approvalservice.app.util;

import com.approvalservice.app.enums.RequestCheckMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import org.springframework.stereotype.Component;

/**
 * Check body request properties. Should be done before going to a service level - i.e. we just check here
 * field correctness without any business logic.
 */

@Component
public class RequestPropertyChecker
{
    private static final int NUMBER_OF_APPROVERS = 3;
    private static final String ID_PATTERN = "[a-zA-Z0-9]{2}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{3}";

    private RequestPropertyChecker()
    {
    }

    /**
     * Basic checks on loan initial request body
     */
    public static RequestCheckMessages checkPendingLoanBody(PendingLoan request)
    {
        RequestCheckMessages customerIdStatus = checkCustomerId(request.getCustomerId());
        if (!customerIdStatus.isSuccess())
        {
            return customerIdStatus;
        }
        if (request.getApprovers() == null)
        {
            return RequestCheckMessages.APPROVERS_EMPTY;
        }
        if (request.getApprovers().size() > NUMBER_OF_APPROVERS)
        {
            return RequestCheckMessages.WRONG_LOAN_APPROVERS;
        }
        if (request.getApprovers().isEmpty())
        {
            return RequestCheckMessages.APPROVERS_EMPTY;
        }
        if (request.getLoanAmount() <= 0)
        {
            return RequestCheckMessages.WRONG_LOAN_AMOUNT;
        }

        return RequestCheckMessages.REQUEST_OK;
    }

    /**
     * Basic checks on approval request
     */
    public static RequestCheckMessages checkApprovalRequest(Approval request)
    {
        RequestCheckMessages customerIdStatus = checkCustomerId(request.getCustomerId());
        if (!customerIdStatus.isSuccess())
        {
            return customerIdStatus;
        }
        if (request.getLoanApprover() == null)
        {
            return RequestCheckMessages.APPROVER_EMPTY;
        }
        if (request.getLoanApprover().isEmpty())
        {
            return RequestCheckMessages.APPROVER_EMPTY;
        }

        return RequestCheckMessages.REQUEST_OK;
    }

    /**
     * Check customer id
     */
    private static RequestCheckMessages checkCustomerId(String customerId)
    {
        if (customerId == null || customerId.isEmpty())
        {
            return RequestCheckMessages.EMPTY_CUSTOMER_ID;
        }
        if (!isValid(customerId))
        {
            return RequestCheckMessages.WRONG_CUSTOMER_ID;
        }

        return RequestCheckMessages.REQUEST_OK;
    }

    private static boolean isValid(String input)
    {
        return input.matches(ID_PATTERN);
    }
}
