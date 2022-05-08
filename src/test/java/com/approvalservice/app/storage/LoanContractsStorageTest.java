package com.approvalservice.app.storage;

import com.approvalservice.app.model.BankAccount;
import com.approvalservice.app.model.Loan;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LoanContractsStorageTest
{
    private static final ArrayList<String> LOAN_APPROVERS =
            new ArrayList<>(Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene"));
    String CLIENT_ID = "AA-1234-817";

    @InjectMocks
    private LoanContractsStorage loanContractsStorage;

    @BeforeEach
    void prepareData()
    {
        Loan loan = new Loan(CLIENT_ID, true, LOAN_APPROVERS, 5000);
        loan.setCreatedTime(LocalDateTime.now().minusSeconds(10));
        loan.setApprovalTime(LocalDateTime.now());

        loanContractsStorage.createNewContract(CLIENT_ID, loan);
    }

    @AfterEach
    void removeData()
    {
        loanContractsStorage.removeContract(CLIENT_ID);
    }

    @Test
    void isContractExistsTest()
    {
        boolean actual = loanContractsStorage.isContractExists(CLIENT_ID);

        assertThat(actual).isTrue();
    }

    @Test
    void addLoanToContractTest()
    {
        Loan loan = new Loan(CLIENT_ID, false, LOAN_APPROVERS, 8000);

        loanContractsStorage.addLoanToContract(CLIENT_ID, loan);

        boolean actual = loanContractsStorage.isPendingContracts(CLIENT_ID);

        assertThat(actual).isTrue();
    }

    @Test
    void setProcessingTest()
    {
        loanContractsStorage.setProcessing(CLIENT_ID, true);

        boolean actual = loanContractsStorage.isProcessing(CLIENT_ID);

        assertThat(actual).isTrue();
    }

    @Test
    void isPendingContractsTest()
    {
        Loan loan = new Loan(CLIENT_ID, false, LOAN_APPROVERS, 8000);

        loanContractsStorage.addLoanToContract(CLIENT_ID, loan);
        boolean actual = loanContractsStorage.isPendingContracts(CLIENT_ID);

        assertThat(actual).isTrue();
    }

    @Test
    void isApproveAllowedTest()
    {
        String approver = LOAN_APPROVERS.get(0);
        boolean actual = loanContractsStorage.isApproveAllowed(CLIENT_ID, approver);

        assertThat(actual).isTrue();
    }

    @Test
    void setLoanApprovalTest()
    {
        Loan loan = new Loan(CLIENT_ID, false, LOAN_APPROVERS, 8000);

        loanContractsStorage.addLoanToContract(CLIENT_ID, loan);

        boolean actualBefore = loanContractsStorage.isPendingContracts(CLIENT_ID);

        assertThat(actualBefore).isTrue();

        loanContractsStorage.setLoanApproval(CLIENT_ID, true, LocalDateTime.now());

        boolean actualAfter = loanContractsStorage.isPendingContracts(CLIENT_ID);

        assertThat(actualAfter).isFalse();
    }

    @Test
    void getFilteredContractsTest()
    {
        ReflectionTestUtils.setField(loanContractsStorage, "sentContractsPeriod", 60);

        LocalDateTime time = LocalDateTime.now();
        Loan loan = new Loan(CLIENT_ID, false, LOAN_APPROVERS, 8000);
        Loan loan2 = new Loan(CLIENT_ID, false, LOAN_APPROVERS, 8000);
        loan.setApprovalTime(time);
        loan2.setApprovalTime(time);

        loanContractsStorage.addLoanToContract(CLIENT_ID, loan);
        loanContractsStorage.addLoanToContract(CLIENT_ID, loan2);

        List<Loan> filteredLoans = loanContractsStorage.getFilteredContracts(LocalDateTime.now());

        assertThat(filteredLoans.size()).isEqualTo(1);
    }

    @Test
    void removeContractTest()
    {
        loanContractsStorage.removeContract(CLIENT_ID);

        boolean actual = loanContractsStorage.isContractExists(CLIENT_ID);

        assertThat(actual).isFalse();
    }
}
