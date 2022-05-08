package com.approvalservice.app;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.enums.RequestCheckMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import com.approvalservice.app.util.ObjectToJsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * API tests, for loan statistics endpoint. Should run on a working and configured application.
 */

public class LoanStatisticsTest
{
    private static final String CREATE_REQUEST = "/api/loan/request";
    private static final String APPROVE_REQUEST = "/api/loan/approval";
    private static final String REPORT_ENDPOINT = "/api/loan/report";

    public LoanStatisticsTest()
    {
        RestAssured.baseURI = "http://localhost:8080";
    }

    private static final String CUSTOMER_ID = "21-Q2A1-XX9";
    private static final long LOAN_AMOUNT = 1000;
    private static final ArrayList<String> LOAN_APPROVERS =
            new ArrayList<>(Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene"));
    private static final Header API_HEADER = new Header("Content-Type", "application/json");
    private static final int STAT_CLEAN_TIME = 61;

    @BeforeAll
    static void waitForStatCleaned() throws InterruptedException
    {
        Thread.sleep(STAT_CLEAN_TIME * 1000);
    }

    @BeforeEach
    void createAndApproveLoanAmountRequest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, LOAN_APPROVERS);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(loan);

        reqSpec.body(json);
        reqSpec.post(CREATE_REQUEST);

        Approval approval = new Approval(CUSTOMER_ID, "Mamertas Juronis", true);

        ObjectToJsonConverter.formRequestJson(reqSpec, approval);
        reqSpec.post(APPROVE_REQUEST);
    }

    @Test
    public void approveLoanRequestOKTest()
    {
        RequestSpecification reqSpec = given().header(API_HEADER);

        Response response = reqSpec.get(REPORT_ENDPOINT);

        response.then().assertThat().statusCode(200);
        response.then().assertThat().body("contractsCount", equalTo(1));
        response.then().assertThat().body("loanAmountsSum", equalTo(1000));
        response.then().assertThat().body("avgLoanAmount", equalTo(1000));
        response.then().assertThat().body("maxLoanAmount", equalTo(1000));
        response.then().assertThat().body("minLoanAmount", equalTo(1000));
    }
}
