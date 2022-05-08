package com.approvalservice.app.model.approval;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request we fill when send to approval
 */

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PendingLoan
{
    private String customerId;
    private long loanAmount;
    private List<String> approvers;
}
