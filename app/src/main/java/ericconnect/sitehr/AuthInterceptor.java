package ericconnect.sitehr;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Eric on 3/4/18.
 */


public class AuthInterceptor implements Interceptor {

    String token;
    AuthInterceptor(String t){
        super();
        token = t;
    }

    @Override
    public Response intercept(Chain chain)
            throws IOException {
        Request request = chain.request();
        if(token!=null){
            request = request.newBuilder()
                    .addHeader("Authenticator", token)
                    .build();
        }
        Response response = chain.proceed(request);
        return response;
    }
}