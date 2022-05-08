package com.approvalservice.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.restassured.specification.RequestSpecification;

public class ObjectToJsonConverter
{

    public static void formRequestJson(RequestSpecification reqSpec, Object loan) throws JsonProcessingException
    {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(loan);

        reqSpec.body(json);
    }
}
