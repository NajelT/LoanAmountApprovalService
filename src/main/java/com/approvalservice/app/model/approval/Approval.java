package com.approvalservice.app.model.approval;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Approval
{
    private final String customerId;
    private final String loanApprover;
    private final boolean approved;
}
