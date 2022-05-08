package com.approvalservice.app.service;

import com.approvalservice.app.model.Loan;
import com.approvalservice.app.model.reports.ContractsReport;
import com.approvalservice.app.storage.LoanContractsStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanStatisticsServiceTest
{
    private static final ArrayList<String> LOAN_APPROVERS =
            new ArrayList<>(Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene"));

    @InjectMocks
    private LoanStatisticsService loanStatisticsService;

    @Mock
    private LoanContractsStorage loanContractsStorage;

    @Test
    void getStatsOnLoansTest()
    {
        when(loanContractsStorage.getFilteredContracts(any())).thenReturn(loansForReport());

        ContractsReport report = loanStatisticsService.getStatsOnLoans();

        assertThat(report.getContractsCount()).isEqualTo(3);
        assertThat(report.getLoanAmountsSum()).isEqualTo(63000);
        assertThat(report.getAvgLoanAmount()).isEqualTo(21000);
        assertThat(report.getMaxLoanAmount()).isEqualTo(50000);
        assertThat(report.getMinLoanAmount()).isEqualTo(5000);
    }

    List<Loan> loansForReport()
    {
        List<Loan> testLoanList = new ArrayList<>();

        testLoanList.add(new Loan("AA-1234-817", true, LOAN_APPROVERS, 5000));
        testLoanList.add(new Loan("AA-1234-818", true, LOAN_APPROVERS, 8000));
        testLoanList.add(new Loan("AA-1234-819", true, LOAN_APPROVERS, 50000));

        return testLoanList;
    }
}
