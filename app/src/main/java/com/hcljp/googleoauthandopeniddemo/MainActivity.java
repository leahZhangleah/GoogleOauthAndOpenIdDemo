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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE=100;
    private static final String TAG = "MainActivity";
    GoogleSignInClient mGoogleSignInClient;
    TextView googleSignInAccountName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        SignInButton signInButton = findViewById(R.id.google_sign_in_btn);
        signInButton.setOnClickListener(this);
        Button signOutBtn = findViewById(R.id.google_sign_out_btn);
        signOutBtn.setOnClickListener(this);
        googleSignInAccountName = findViewById(R.id.google_sign_in_account_name);
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
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "sign in result: failed code="+e.getStatusCode() );
            e.printStackTrace();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        String msg = "signed in email: "+ account.getEmail()+"\n google id: "+account.getId()+"\n id token: "+account.getIdToken();
        googleSignInAccountName.setText(msg);
    }
}




































