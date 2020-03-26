package com.hcljp.googleoauthandopeniddemo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class MyIntentService extends JobIntentService {
    public static final int JOB_ID=1000;

    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context,MyIntentService.class,JOB_ID,work);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String idToken = intent.getStringExtra("id_token");
        String clientId = intent.getStringExtra("client_id");
        authenticateIdToken(idToken,clientId);
    }

    private void authenticateIdToken(String idToken,String client_id) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(client_id))
                .build();
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if(googleIdToken!=null){
                GoogleIdToken.Payload payload = googleIdToken.getPayload();
                String msg = "authenticated email: "+ payload.getEmail()+"\n user id: "+payload.getSubject()+"\n email verified: "+payload.getEmailVerified();
                showToast(msg);
            }else{
                showToast("id token authentication failed");
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    final Handler mHandler = new Handler();
    private void showToast(final CharSequence text){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyIntentService.this,text,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
