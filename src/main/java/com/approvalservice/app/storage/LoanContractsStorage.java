package com.approvalservice.app.storage;

import com.approvalservice.app.model.BankAccount;
import com.approvalservice.app.model.Loan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Access to a contracts and statistics data.
 */

@Component
public class LoanContractsStorage
{
    @Value("${service.loan.sent-contracts-period}")
    private int sentContractsPeriod;

    private static final ConcurrentHashMap<String, BankAccount> customerContracts = new ConcurrentHashMap<>();

    /**
     * Check if there is contracts for provided customer Id
     */
    public boolean isContractExists(String customerId)
    {
        return customerContracts.containsKey(customerId);
    }

    /**
     * Add new customer account with approved loan
     */
    public void createNewContract(String customerId, Loan response)
    {
        customerContracts.computeIfAbsent(customerId,
                k -> new BankAccount(new ArrayList<>(Arrays.asList(response))));
    }

    /**
     * Update existing customer account with new loan contract
     */
    public void addLoanToContract(String customerId, Loan response)
    {
        if (customerContracts.containsKey(customerId)) {
            customerContracts.get(customerId).getLoans().add(response);
        }
    }

    /**
     * Check if the customer has pending loans on its account.
     */
    public boolean isProcessing(String cusomerId)
    {
        if (customerContracts.containsKey(cusomerId))
        {
            return customerContracts.get(cusomerId).isProcessing();
        }
        else
        {
            return false;
        }
    }

    /**
     * Set processing flag which on customer's account. Will make further loan approval request not possible till
     * current unapproved loan will be processed by a loan manager.
     */
    public void setProcessing(String customerId, boolean processing)
    {
        if (customerContracts.containsKey(customerId))
        {
            customerContracts.get(customerId).setProcessing(processing);
        }
    }

    /**
     * Check if there is any pending unapproved loans
     */
    public boolean isPendingContracts(String customerId)
    {
        List<Loan> contracts = customerContracts.get(customerId).getLoans();

        return contracts.stream().anyMatch(contract -> !contract.isApproved());
    }

    /**
     * Check if a loan manager has a permission to work with pending loan
     */
    public boolean isApproveAllowed(String customerId, String approver)
    {
        List<Loan> loans = customerContracts.get(customerId).getLoans();

        for (Loan loan : loans)
        {
            if (loan.getApproverNames().contains(approver))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Process loan prepared contract approval by a manager
     */
    public void setLoanApproval(String customerId, boolean approved, LocalDateTime approvalTime)
    {
        if (customerContracts.containsKey(customerId))
        {
            if (approved)
            {
                customerContracts.get(customerId).getLoans().forEach(c ->
                {
                    if (!c.isApproved())
                    {
                        c.setApprovalTime(approvalTime);
                        c.setApproved(true);
                    }
                });
            }
            else
            {
                customerContracts.get(customerId).getLoans().removeIf(c -> !c.isApproved());
            }
        }
    }

    /**
     * Get list of approved loans (contracts) sent to the customer during amount of time configured in a config.
     */
    public List<Loan> getFilteredContracts(LocalDateTime currentTime)
    {
        List<Loan> filteredContracts = new ArrayList<>();

        customerContracts.forEach((key, customerProfile) -> filteredContracts.addAll(customerProfile
                .getLoans().stream()
                .filter(loan -> isAppropriate(loan, currentTime))
                .collect(Collectors.toList())));

        return filteredContracts;
    }

    private boolean isAppropriate(Loan loan, LocalDateTime currentTime)
    {
        return loan.getApprovalTime() != null && loan.isApproved()
                && loan.getApprovalTime().isAfter(currentTime.minusSeconds(sentContractsPeriod));
    }

    /**
     * Remove client from the map
     */
    public void removeContract(String customerId)
    {
        customerContracts.remove(customerId);
    }
}
