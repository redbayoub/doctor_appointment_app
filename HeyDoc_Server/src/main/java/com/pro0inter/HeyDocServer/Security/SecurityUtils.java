package com.pro0inter.HeyDocServer.Security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class SecurityUtils {
    private static final String[] FIREBASE_CLAIMS_RESERVERD_KEYS = {
            "acr", "amr", "at_hash", "aud", "auth_time", "azp", "cnf", "c_hash",
            "exp", "firebase", "iat", "iss", "jti", "nbf", "nonce", "sub"
    };

    public boolean isLoogedIn(String idToken) {
        // idToken comes from the client app (shown above)
        FirebaseToken decodedToken = null;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return true;
        } catch (FirebaseAuthException e) {
            return false;
            //e.printStackTrace();
        }

    }

    public FirebaseToken getFirebaseToken(String idToken) throws FirebaseAuthException {
        // idToken comes from the client app (shown above)
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public Map<String, Object> getClaims(FirebaseToken firebaseToken) {
        Map<String, Object> claims=firebaseToken.getClaims();
        claims.keySet().removeAll(Arrays.asList(FIREBASE_CLAIMS_RESERVERD_KEYS));
        return claims;
    }

    public void setClaims(String uid, Map<String, Object> claims) throws FirebaseAuthException {

        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);

    }


}
