package ericconnect.sitehr;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by Eric on 11/30/17.
 */
public class ServiceHelper {

    public static void setENDPOINT(String ENDPOINT) {
        ServiceHelper.ENDPOINT = ENDPOINT;
    }

    private static String ENDPOINT = "http://192.168.1.8:5000/";
    protected static String TOKEN;

    public static void setToken(String token) {
        ServiceHelper.TOKEN = token;
    }


    private static ServiceHelper instance;

    private SignOperateInterface service;


    public ServiceHelper() {

        Retrofit retrofit = createAdapter().build();
        service = retrofit.create(SignOperateInterface.class);



    }
    /**
     * return a  service instance
     * @param service_url the service endpoint.
     */
    public static ServiceHelper getInstance(String service_url, String token) {

        if(service_url != null)
            setENDPOINT(service_url);

        if(token != null)
            setToken(token);

        if(instance != null) {
            return instance;
        } else {
            instance = new ServiceHelper();
            return instance;
        }

    }

    private Retrofit.Builder createAdapter() {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //logging restful connection info
        OkHttpClient.Builder mClient = new OkHttpClient.Builder();
        mClient.addInterceptor(new AuthInterceptor(TOKEN));
        mClient.addInterceptor(interceptor); //add  logging interceptor to log out ;

        return new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(mClient.build())
                .addConverterFactory(GsonConverterFactory.create());
    }

    // define all operations below.

    public Call<List<Person>> getAllCategory() {
        return service.getAllPersonInfo();
    }

    public Call<Person> getPersonInfo(String public_id){
        return service.getPersonInfo(public_id);
    }

    public Call<ResponseBody> signIn(String public_id, String operator_id){
        return service.signIn(public_id, operator_id);
    }

    public Call<ResponseBody> signOut(String public_id, String operator_id){
        return service.signOut(public_id, operator_id);
    }

    public Call<ResponseBody> login(String name, String pwd){
        return service.login(name, pwd);
    }

    public Call<ResponseBody> getHistory(String public_id, String start_date, String end_date){
        return service.getHistory(public_id,start_date,end_date);
    }
}
