package com.example.maintena.Api;

import com.example.maintena.Model.Details;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

// This class is responsible for calling the VES API to fetch the Vehicle Details of a number plate and parse the response.
public interface DVLAService {

    // The Post annotation indicates that this api call is a POST method
    // The headers annotation sets up the header of the Api post method
    // This Method is to be used for the Live Environment (The real Endpoint)
    // The header contains the Api Key for the real environment
    @Headers("x-api-key: ko9aWON3PeaNSRwlRY35c9jTyveBoi7m68dTFMpk")
    @POST("/vehicle-enquiry/v1/vehicles")
    Call<Details> getVehicleDetails(@Body RequestBody requestBody);

    // The Post annotation indicates that this api call is a POST method
    // The headers annotation sets up the header of the Api post method
    // This Method is to be used for the UAT Environment
    // The header contains the Api Key for the UAT environment
    @Headers("x-api-key: lRIJapHhLS6UYPKbJqTKi4jeXXbhs0IU3qYOOlSf")
    @POST("/vehicle-enquiry/v1/vehicles")
    Call<Details> getUATVehicleDetails(@Body RequestBody requestBody);

}