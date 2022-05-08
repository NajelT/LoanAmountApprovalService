package com.approvalservice.app.controller;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.enums.RequestCheckMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import com.approvalservice.app.model.reports.BasicContractsReport;
import com.approvalservice.app.model.reports.ContractsReport;
import com.approvalservice.app.model.response.BasicResponse;
import com.approvalservice.app.service.LoanApprovalService;
import com.approvalservice.app.service.LoanStatisticsService;
import com.approvalservice.app.util.RequestPropertyChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Loan approval request rest controller
 */

@RestController
@RequestMapping("/api/loan")
public class LoanApprovalController
{
    private LoanApprovalService loanApprovalService;

    private LoanStatisticsService loanStatisticsService;

    @Autowired
    private void setLoanApprovalService(LoanApprovalService loanApprovalService)
    {
        this.loanApprovalService = loanApprovalService;
    }

    @Autowired
    public void setLoanStatisticsService(LoanStatisticsService loanStatisticsService)
    {
        this.loanStatisticsService = loanStatisticsService;
    }

    /**
     * Loan approval request endpoint
     */
    @PostMapping("/request")
    public ResponseEntity<BasicResponse> createLoanRequest(@RequestBody PendingLoan request)
    {
        RequestCheckMessages checkResult = RequestPropertyChecker.checkPendingLoanBody(request);
        if (!checkResult.isSuccess())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(checkResult));
        }

        BasicResponse result = loanApprovalService.createLoanApprovalRequest(request);

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    /**
     * Loan approval request endpoint
     */
    @PostMapping("/approval")
    public ResponseEntity<BasicResponse> approveLoanRequest(@RequestBody Approval request)
    {
        RequestCheckMessages checkResult = RequestPropertyChecker.checkApprovalRequest(request);
        if (!checkResult.isSuccess())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse(checkResult));
        }

        BasicResponse result = loanApprovalService.processPendingContract(request);

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    /**
     * Loan report endpoint
     */
    @GetMapping("/report")
    public ResponseEntity<BasicContractsReport> getSentContractsReport()
    {
        ContractsReport result = loanStatisticsService.getStatsOnLoans();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new BasicContractsReport(BusinessMessages.NO_CONTRACTS_FOR_STAT));
        }
        else
        {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }
}