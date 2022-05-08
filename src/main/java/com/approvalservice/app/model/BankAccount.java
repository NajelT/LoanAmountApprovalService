package com.approvalservice.app.model;

import lombok.*;

import java.util.List;

/**
 * Client account object. Will be stored for further statistics and analysis once a loan approved
 */

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class BankAccount
{
    private final List<Loan> loans;
    private boolean processing;
}
