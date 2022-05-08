package com.approvalservice.app.controller;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import com.approvalservice.app.model.reports.BasicContractsReport;
import com.approvalservice.app.model.reports.ContractsReport;
import com.approvalservice.app.model.response.BasicResponse;
import com.approvalservice.app.service.LoanApprovalService;
import com.approvalservice.app.service.LoanStatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApprovalControllerTest
{
    @InjectMocks
    private LoanApprovalController loanApprovalController;

    @Mock
    private LoanApprovalService loanApprovalService;

    @Mock
    private LoanStatisticsService loanStatisticsService;

    @Test
    void createLoanRequestTest()
    {
        when(loanApprovalService.createLoanApprovalRequest(any(PendingLoan.class)))
                .thenReturn(new BasicResponse(BusinessMessages.CONTRACT_SENT_TO_APPROVAL));

        ResponseEntity<BasicResponse> responseEntity = loanApprovalController.createLoanRequest(prepareLoanRequest());

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void approveLoanRequestTest()
    {
        when(loanApprovalService.processPendingContract(any(Approval.class)))
                .thenReturn(new BasicResponse(BusinessMessages.CONTRACT_SENT_TO_APPROVAL));

        ResponseEntity<BasicResponse> responseEntity = loanApprovalController.approveLoanRequest(prepareApprovalRequest());

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void getSentContractsReportTest()
    {
        when(loanStatisticsService.getStatsOnLoans()).thenReturn(new ContractsReport());

        ResponseEntity<BasicContractsReport> responseEntity = loanApprovalController.getSentContractsReport();

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
    }

    PendingLoan prepareLoanRequest() {
        String customerId = "21-Q2A1-XX9";
        long loanAmount = 5000;
        ArrayList<String> approvers = new ArrayList<>(
                Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene"));

        return new PendingLoan(customerId, loanAmount, approvers);
    }

    Approval prepareApprovalRequest() {
        String customerId = "21-Q2A1-XX9";
        String loanApprover = "Mamertas Juronis";
        boolean approved = true;

        return new Approval(customerId, loanApprover, approved);
    }
}
