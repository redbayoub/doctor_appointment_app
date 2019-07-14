package com.pro0inter.heydoc.api;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by root on 4/27/19.
 */

public class ServiceGenerator {



    //private static final String BASE_URL = "http://10.0.2.2:8080/";
    //private static final String BASE_URL = "http://192.168.43.38:8080/";
    private static final String BASE_URL = "http://192.168.1.33:8080/";


    private static Retrofit.Builder builder
            = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.addConverterFactory(GsonConverterFactory.create());
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create());
    public static Retrofit retrofit = builder.build();
    private static OkHttpClient.Builder httpClient
            = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor logging
            = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);



    public static <S> S createService(Class<S> serviceClass) {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceSecured(Class<S> serviceClass) {

            httpClient.interceptors().clear();
            httpClient.addInterceptor(new FirebaseUserIdTokenInterceptor(false));

            httpClient.addInterceptor(logging);

            builder.client(httpClient.build());
            retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    public static String getFileUrl(String filename) {
        String file_url=BASE_URL+"upload/files/"+filename;
        System.out.println("########"+file_url);
        return file_url;
    }



/*    public static <S> S createServiceSecured(Class<S> serviceClass,boolean force_refresh_token) {

        mAuth.getAccessToken(force_refresh_token)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if(task.isComplete()) {
                            String token = task.getResult().getToken();
                        }else{
                            //handel auth failed
                        }

                    }
                });
            httpClient.interceptors().clear();
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder builder1 = original.newBuilder()
                        .header(FIREBASE_AUTH_HEADER, current_id_token);
                Request request = builder1.build();
                return chain.proceed(request);
            });

            httpClient.addInterceptor(logging);

            builder.client(httpClient.build());
            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }*/

   /* public void ddd(){
        new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = json.getAsJsonPrimitive().getAsString();
                        try {
                            return format.parse(date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
                        return null;
                    }
                })
                .create()

    }*/

}
