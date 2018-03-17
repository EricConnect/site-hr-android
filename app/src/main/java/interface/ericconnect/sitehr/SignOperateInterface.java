package ericconnect.sitehr;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by Eric on 11/17/17.
 */

public interface SignOperateInterface {


    @GET("api/clockinout/{public_id}/{operator_id}/in")
    Call<ResponseBody> signIn(
            @Path("public_id") String publicID,
            @Path("operator_id")String operatorID
    );


    @GET("api/clockinout/{public_id}/{operator_id}/out")
    Call<ResponseBody> signOut(
            @Path("public_id") String publicID,
            @Path("operator_id") String operatorID
    );


    //Person getPersonInfo(Integer id);
    @GET("api/user/{public_id}")
    Call<Person> getPersonInfo(
            @Path("public_id") String publicID
    );


    @GET("api/users")
    Call<List<Person>> getAllPersonInfo();


    @GET("api/login/{username}/{password}")
    Call<ResponseBody> login(
            @Path("username") String name,
            @Path("password") String pwd
    );


    @GET("api/get_history/{public_id}/{start_date}/{end_date}")
    Call<ResponseBody> getHistory(
            @Path("public_id") String publicID,
            @Path("start_date") String startDate,
            @Path("end_date") String endDate
    );

}

