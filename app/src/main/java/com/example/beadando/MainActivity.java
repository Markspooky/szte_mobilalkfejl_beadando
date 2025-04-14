package com.example.beadando;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private static final int RC_SIGN_IN = 999;
    private static final int SECRET_KEY = 99;

    EditText userNameET;
    EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        userNameET = findViewById(R.id.Edit_Text_Username);
        passwordET = findViewById(R.id.Edit_Text_Password);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCore, Intent data) {

        super.onActivityResult(requestCode, resultCore, data);

        if (requestCode == RC_SIGN_IN) {
            Task< GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG,"Google bejelentkezés sikeres: "+account.getId());
               firebaseAuthWGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(LOG_TAG,"Google bejelentkezés sikertelen",e);
            }

        }

    }

    private void firebaseAuthWGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG,"Autentikáció sikeres");
                    atHome();
                } else {
                    Log.w(LOG_TAG,"Autentikáció sikertelen",task.getException());
                }
            }
        });
    }

    private void atHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }

    public void login(View view) {

        String userNameStr = userNameET.getText().toString();
        String passwordStr = passwordET.getText().toString();

        //Log.i(LOG_TAG,"Bejelentkezett: "+userNameStr + ", jelszó: " + passwordStr);

        mAuth.signInWithEmailAndPassword(userNameStr,passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG,"Bejelentkezés sikeres");
                    atHome();
                } else {
                    Log.w(LOG_TAG,"Bejelentkezés sikertelen",task.getException());
                }
            }
        });

    }

    public void loginWithGoogle(View view) {
        Intent GSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(GSignInIntent,RC_SIGN_IN);
    }
    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG,"Sikeres névtelen bejelentkezés");
                    atHome();
                } else {
                    Log.w(LOG_TAG,"Sikertelen névtelen bejelentkezés",task.getException());
                }
            }
        });
    }



    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);

        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName",userNameET.getText().toString());
        editor.putString("password",passwordET.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}