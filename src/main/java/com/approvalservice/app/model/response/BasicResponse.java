package com.approvalservice.app.model.response;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.enums.RequestCheckMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.beans.Transient;

/**
 * Basic response body
 */

@Getter
@Setter
@AllArgsConstructor
public class BasicResponse
{
    private String message;
    private HttpStatus status;

    public BasicResponse(BusinessMessages message)
    {
        this.message = message.getResult();
        this.status = message.getStatus();
    }

    public BasicResponse(RequestCheckMessages requestCheckMessages)
    {
        this.message = requestCheckMessages.getResult();

        if (!requestCheckMessages.isSuccess())
        {
            this.status = HttpStatus.BAD_REQUEST;
        }
    }

    @Transient
    public HttpStatus getStatus()
    {
        return status;
    }
}
