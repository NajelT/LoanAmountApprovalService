package com.approvalservice.app.service;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import com.approvalservice.app.model.response.BasicResponse;
import com.approvalservice.app.storage.LoanContractsStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApprovalServiceTest
{
    private static final String CUSTOMER_ID = "AA-1234-817";
    private static final ArrayList<String> APPROVERS = new ArrayList<>(
            Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene", "Dionizas Ulinskas"));
    private static final String APPROVER = "Mamertas Juronis";
    private static final long LOAN_AMOUNT = 5000;

    @InjectMocks
    private LoanApprovalService loanApprovalService;

    @Mock
    private LoanContractsStorage loanContractsStorage;

    @Test
    void createLoanApprovalRequestFoNewTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, APPROVERS);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(false);

        BasicResponse response = loanApprovalService.createLoanApprovalRequest(loan);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.CONTRACT_SENT_TO_APPROVAL.getResult());
    }

    @Test
    void createLoanApprovalRequestForExistTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, APPROVERS);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(true);

        BasicResponse response = loanApprovalService.createLoanApprovalRequest(loan);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.CONTRACT_SENT_TO_APPROVAL.getResult());
    }

    @Test
    void createLoanApprovalRequestForProcessingLoanTest()
    {
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, APPROVERS);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(true);
        when(loanContractsStorage.isProcessing(anyString())).thenReturn(true);

        BasicResponse response = loanApprovalService.createLoanApprovalRequest(loan);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.CUSTOMER_CONTRACT_PENDING.getResult());
    }

    @Test
    void processPendingContractOKTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, APPROVER, true);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(true);
        when(loanContractsStorage.isPendingContracts(any(String.class))).thenReturn(true);
        when(loanContractsStorage.isApproveAllowed(any(String.class), any(String.class))).thenReturn(true);

        BasicResponse response = loanApprovalService.processPendingContract(approval);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.SUCCESSFULLY_SENT.getResult());
    }

    @Test
    void processPendingContractNotExistTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, APPROVER, true);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(false);

        BasicResponse response = loanApprovalService.processPendingContract(approval);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.CUSTOMER_NOT_FOUND.getResult());
    }

    @Test
    void processPendingContractNoPendingTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, APPROVER, true);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(true);
        when(loanContractsStorage.isPendingContracts(anyString())).thenReturn(false);

        BasicResponse response = loanApprovalService.processPendingContract(approval);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.NO_PENDING_CONTRACTS.getResult());
    }

    @Test
    void processPendingContractApproverNotAllowedTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, APPROVER, true);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(true);
        when(loanContractsStorage.isPendingContracts(any(String.class))).thenReturn(true);
        when(loanContractsStorage.isApproveAllowed(any(String.class), any(String.class))).thenReturn(false);

        BasicResponse response = loanApprovalService.processPendingContract(approval);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.INAPPROPRIATE_APPROVER.getResult());
    }

    @Test
    void processPendingContractNotApprovedTest()
    {
        Approval approval = new Approval(CUSTOMER_ID, APPROVER, false);

        when(loanContractsStorage.isContractExists(anyString())).thenReturn(true);
        when(loanContractsStorage.isPendingContracts(any(String.class))).thenReturn(true);
        when(loanContractsStorage.isApproveAllowed(any(String.class), any(String.class))).thenReturn(true);

        BasicResponse response = loanApprovalService.processPendingContract(approval);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getMessage()).isEqualTo(BusinessMessages.NOT_APPROVED.getResult());
    }
}
