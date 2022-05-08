package com.approvalservice.app.model.reports;

import com.approvalservice.app.enums.BusinessMessages;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasicContractsReport
{
    private String message;

    public BasicContractsReport(BusinessMessages message)
    {
        this.message = message.getResult();
    }
}
