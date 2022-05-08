package com.approvalservice.app.model;

import lombok.Getter;
import lombok.Setter;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Positive loan request response
 */

@Getter
@Setter
public class Loan
{
    private String userId;
    private boolean approved;
    private List<String> approverNames;
    private long loanAmount;
    private LocalDateTime createdTime;
    private LocalDateTime approvalTime;

    public Loan(String userId, boolean approved, List<String> approverNames, long loanAmount)
    {
        this.userId = userId;
        this.approved = approved;
        this.approverNames = approverNames;
        this.loanAmount = loanAmount;
        this.createdTime = LocalDateTime.now();
    }

    @Transient
    public LocalDateTime getCreatedTime()
    {
        return createdTime;
    }

    @Transient
    public LocalDateTime getApprovalTime()
    {
        return approvalTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan contract = (Loan) o;
        return approved == contract.approved &&
                loanAmount == contract.loanAmount &&
                userId.equals(contract.userId) &&
                approverNames.equals(contract.approverNames) &&
                Objects.equals(createdTime, contract.createdTime) &&
                Objects.equals(approvalTime, contract.approvalTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(userId, approved, approverNames, loanAmount, createdTime, approvalTime);
    }
}
