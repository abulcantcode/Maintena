package com.example.maintena.Api;

import com.example.maintena.Model.Details;

import junit.framework.TestCase;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DVLAServiceTest extends TestCase {

    // This is my JUnit test to check that an API connection can be established and returns the correct numberplate
    // Below is a link to see the acceptable number plates for the UAT environment:
    // https://developer-portal.driver-vehicle-licensing.api.gov.uk/apis/vehicle-enquiry-service/mock-responses.html
    public void testAPIShouldReturnCorrectNumberPlate() throws IOException {
        //This numberplate is compatible with the UAT API
        String numPlate = "AA19AAA";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                //This is the end point for the UAT environment
                .baseUrl("https://uat.driver-vehicle-licensing.api.gov.uk")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        DVLAService service = retrofit.create(DVLAService.class);

        // This is the request body
        String requestBody = "{ \"registrationNumber\":\"" + numPlate + "\" }";
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));

        // getUATVehicleDetails is part of the DVLAService class, which has the correct API Key for the UAT environment
        Call<Details> call = service.getUATVehicleDetails(body);
        Response<Details> response = call.execute();
        if (response.isSuccessful()) {
            Details details = response.body();
            assertEquals(numPlate, details.getRegistrationNumber());
        } else {
            fail();
        }
    }
}