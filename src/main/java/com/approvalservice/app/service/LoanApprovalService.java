package com.approvalservice.app.service;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.model.approval.PendingLoan;
import com.approvalservice.app.model.response.BasicResponse;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.Loan;
import com.approvalservice.app.storage.LoanContractsStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service which process approval request
 */

@Service
public class LoanApprovalService
{
    private LoanContractsStorage loanContractsStorage;

    @Autowired
    private void setLoanContractsStorage(LoanContractsStorage loanContractsStorage)
    {
        this.loanContractsStorage = loanContractsStorage;
    }

    /**
     * Here we prepare a loan contract for further approval
     */
    public BasicResponse createLoanApprovalRequest(PendingLoan request)
    {
        String customerId = request.getCustomerId();

        Loan preparedContract;

        if (isNew(customerId))
        {
            preparedContract = createPendingContract(request);

            loanContractsStorage.createNewContract(customerId, preparedContract);
        } else
            {
                if (loanContractsStorage.isProcessing(customerId))
                {
                    return new BasicResponse(BusinessMessages.CUSTOMER_CONTRACT_PENDING);
                }

                preparedContract = createPendingContract(request);

                loanContractsStorage.addLoanToContract(customerId, preparedContract);
            }

        loanContractsStorage.setProcessing(customerId, true);

        return new BasicResponse(BusinessMessages.CONTRACT_SENT_TO_APPROVAL);
    }

    /**
     * Process incoming approval requests for loan managers. If request was approved, we assume that is was sent
     * to the customer, but this functionality is out of scope.
     */
    public BasicResponse processPendingContract(Approval request)
    {
        LocalDateTime approvalTime = LocalDateTime.now();
        String customerId = request.getCustomerId();

        if (loanContractsStorage.isContractExists(customerId))
        {
            if (loanContractsStorage.isPendingContracts(customerId))
            {
                if (!loanContractsStorage.isApproveAllowed(customerId, request.getLoanApprover()))
                {
                    return new BasicResponse(BusinessMessages.INAPPROPRIATE_APPROVER);
                }

                loanContractsStorage.setLoanApproval(customerId, request.isApproved(), approvalTime);
                loanContractsStorage.setProcessing(customerId, false);

                if (request.isApproved())
                {
                    return new BasicResponse(BusinessMessages.SUCCESSFULLY_SENT);
                }
                else
                {
                    return new BasicResponse(BusinessMessages.NOT_APPROVED);
                }
            }
            else
            {
                return new BasicResponse(BusinessMessages.NO_PENDING_CONTRACTS);
            }
        }
        else
        {
            return new BasicResponse(BusinessMessages.CUSTOMER_NOT_FOUND);
        }
    }

    private boolean isNew(String customerId)
    {
        return !loanContractsStorage.isContractExists(customerId);
    }

    private Loan createPendingContract(PendingLoan request)
    {
        return new Loan(request.getCustomerId(), false, request.getApprovers(), request.getLoanAmount());
    }
}
