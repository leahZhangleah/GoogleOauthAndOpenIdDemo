package com.hcljp.googleoauthandopeniddemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE=100;
    // web application client id on google developer console
    public static final String CLIENT_ID="";
    private static final String TAG = "MainActivity";
    GoogleSignInClient mGoogleSignInClient;
    TextView googleSignInAccountName,authenticatedAccountInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        /*GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestEmail()
                .build();*/
        //if we want to enable back end server to access google api, we need to get server auth code
        //while requesting drive scope access token, we also get ID Token
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                //.requestServerAuthCode(CLIENT_ID) //.requestIdToken(CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        SignInButton signInButton = findViewById(R.id.google_sign_in_btn);
        signInButton.setOnClickListener(this);
        Button signOutBtn = findViewById(R.id.google_sign_out_btn);
        signOutBtn.setOnClickListener(this);
        googleSignInAccountName = findViewById(R.id.google_sign_in_account_name);
        authenticatedAccountInfo = findViewById(R.id.authenticated_account_info);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            //show sign out button if the user has signed in
            updateUI(account);
        }else{
            //show sign in button if the user hasn't signed in
            //todo
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.google_sign_in_btn:
                signIn();
                break;
            case R.id.google_sign_out_btn:
                signOut();
                break;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            googleSignInAccountName.setText("");
                            Toast.makeText(MainActivity.this,"sign out",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"sign out failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void signIn() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GOOGLE_SIGN_IN_REQUEST_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String authCode = account.getServerAuthCode();
            String idToken = account.getIdToken();
            Set<Scope> scopes = account.getGrantedScopes();
            while(scopes.iterator().hasNext()){
                Log.i(TAG, "scope: "+scopes.iterator().next().toString());
            }
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "sign in result: failed code="+e.getStatusCode() );
            e.printStackTrace();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        String accountInfo = "signed in email: "+ account.getEmail()+"\n google id: "+account.getId();
        googleSignInAccountName.setText(accountInfo);

        /*Intent intent = new Intent(this,MyIntentService.class);
        intent.putExtra("id_token",account.getIdToken());
        intent.putExtra("client_id",CLIENT_ID);
        MyIntentService.enqueueWork(this,intent);*/
    }

}




































