package com.approvalservice.app.model.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.beans.Transient;

/**
 * Loan report according to the task
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContractsReport extends BasicContractsReport
{
    private long contractsCount;
    private long loanAmountsSum;
    private long avgLoanAmount;
    private long maxLoanAmount;
    private long minLoanAmount;

    @Transient
    public boolean isEmpty()
    {
        return !(contractsCount != 0 && loanAmountsSum != 0
                && avgLoanAmount != 0 && maxLoanAmount != 0 && minLoanAmount != 0);
    }

    @Transient
    @Override
    public String getMessage()
    {
        return super.getMessage();
    }

    @Override
    public String toString()
    {
        return "ContractsReport{" +
                "contractsCount=" + contractsCount +
                ", loanAmountsSum=" + loanAmountsSum +
                ", avgLoanAmount=" + avgLoanAmount +
                ", maxLoanAmount=" + maxLoanAmount +
                ", minLoanAmount=" + minLoanAmount +
                '}';
    }
}
