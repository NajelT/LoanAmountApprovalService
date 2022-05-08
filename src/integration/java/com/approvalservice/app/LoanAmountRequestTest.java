package com.approvalservice.app;

import com.approvalservice.app.enums.BusinessMessages;
import com.approvalservice.app.enums.RequestCheckMessages;
import com.approvalservice.app.model.approval.Approval;
import com.approvalservice.app.model.approval.PendingLoan;
import com.approvalservice.app.util.ObjectToJsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * API tests, for loan amount request endpoint. Should run on a working and configured application.
 */

public class LoanAmountRequestTest
{
    private static final String CREATE_REQUEST = "/api/loan/request";
    private static final String APPROVE_REQUEST = "/api/loan/approval";

    public LoanAmountRequestTest()
    {
        RestAssured.baseURI = "http://localhost:8080";
    }

    private static final String CUSTOMER_ID = "21-Q2A1-XX9";
    private static final long LOAN_AMOUNT = 1000;
    private static final ArrayList<String> LOAN_APPROVERS =
            new ArrayList<>(Arrays.asList("Mamertas Juronis", "Gaile Minderyte", "Nina Nugariene"));
    private static final String BODY_READ_PROPERTY = "message";
    private static final Header API_HEADER = new Header("Content-Type", "application/json");

    @Test
    void createLoanRequestEmptyCustomerIdTest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan("", LOAN_AMOUNT, LOAN_APPROVERS);

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(400);
        response.then().assertThat().body(BODY_READ_PROPERTY,equalTo(RequestCheckMessages.EMPTY_CUSTOMER_ID.getResult()));
    }

    @Test
    void createLoanRequestWrongCustomerIdTest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan("111-111-Q", LOAN_AMOUNT, LOAN_APPROVERS);

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(400);
        response.then().assertThat().body(BODY_READ_PROPERTY, equalTo(RequestCheckMessages.WRONG_CUSTOMER_ID.getResult()));
    }

    @Test
    void createLoanRequestEmptyApproversTest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, new ArrayList<>());

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(400);
        response.then().assertThat().body(BODY_READ_PROPERTY, equalTo(RequestCheckMessages.APPROVERS_EMPTY.getResult()));
    }

    @Test
    void createLoanRequestMoreThanThreeApproversTest() throws JsonProcessingException
    {
        ArrayList<String> approvers = new ArrayList<>(LOAN_APPROVERS);
        approvers.add("Sergejus Kazlauskas");
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, approvers);

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(400);
        response.then().assertThat().body(BODY_READ_PROPERTY, equalTo(RequestCheckMessages.WRONG_LOAN_APPROVERS.getResult()));
    }

    @Test
    void createLoanRequestZeroLoanAmountTest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, 0, LOAN_APPROVERS);

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(400);
        response.then().assertThat().body(BODY_READ_PROPERTY, equalTo(RequestCheckMessages.WRONG_LOAN_AMOUNT.getResult()));
    }

    @Test
    void createLoanRequestNegativeLoanAmountTest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, -2000, LOAN_APPROVERS);

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(400);
        response.then().assertThat().body(BODY_READ_PROPERTY, equalTo(RequestCheckMessages.WRONG_LOAN_AMOUNT.getResult()));
    }

    @Test
    void createLoanRequestOKTest() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        PendingLoan loan = new PendingLoan(CUSTOMER_ID, LOAN_AMOUNT, LOAN_APPROVERS);

        ObjectToJsonConverter.formRequestJson(reqSpec, loan);

        Response response = reqSpec.post(CREATE_REQUEST);

        response.then().assertThat().statusCode(201);
        response.then().assertThat().body(BODY_READ_PROPERTY,equalTo(BusinessMessages.CONTRACT_SENT_TO_APPROVAL.getResult()));
    }

    /**
     * In order to not keep anything in queue, accept all loan contracts .
     */
    @AfterAll
    static void approveAllLoans() throws JsonProcessingException
    {
        RequestSpecification reqSpec = given().header(API_HEADER);
        Approval approval = new Approval(CUSTOMER_ID, "Mamertas Juronis", true);

        ObjectToJsonConverter.formRequestJson(reqSpec, approval);
        reqSpec.post(APPROVE_REQUEST);
    }
}
