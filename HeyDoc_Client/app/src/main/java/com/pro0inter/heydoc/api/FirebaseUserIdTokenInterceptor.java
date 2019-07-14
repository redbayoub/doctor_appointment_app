package com.pro0inter.heydoc.api;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseUserIdTokenInterceptor implements Interceptor {

    // Custom header for passing ID token in request.
    private static final String X_FIREBASE_ID_TOKEN = "Firebase-Auth";
    private boolean force_refresh_id_token;

    public FirebaseUserIdTokenInterceptor(boolean force_refresh_id_token) {
        this.force_refresh_id_token = force_refresh_id_token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                throw new Exception("User is not logged in.");
            } else {
                Task<GetTokenResult> task = user.getIdToken(true);
                GetTokenResult tokenResult = Tasks.await(task);
                String idToken = tokenResult.getToken();

                if (idToken == null) {
                    throw new Exception("idToken is null");
                } else {
                    Request modifiedRequest = request.newBuilder()
                            .addHeader(X_FIREBASE_ID_TOKEN, idToken)
                            .build();
                    return chain.proceed(modifiedRequest);
                }
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}