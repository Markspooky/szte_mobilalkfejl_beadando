package com.example.beadando;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 99;
    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordagainET;
    CheckBox aszfCB;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        if (bundle.getInt("SECRET_KEY") != 99) {
            finish();
        }

        usernameET = findViewById(R.id.user);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        passwordagainET = findViewById(R.id.password_again);
        aszfCB = findViewById(R.id.aszf);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String userName = preferences.getString("user","");
        String password = preferences.getString("password","");

        usernameET.setText(userName);
        passwordET.setText(password);

        mAuth = FirebaseAuth.getInstance();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void register(View view) {
        String userName = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordagain = passwordagainET.getText().toString();
        String email = emailET.getText().toString();

        if (userName.isEmpty() || password.isEmpty() || email.isEmpty() || passwordagain.isEmpty()) {
            Log.e(LOG_TAG,"Minden mezőt tölts ki!");
            return;
        }

        if (!aszfCB.isChecked()) {
            Log.e(LOG_TAG,"Fogadd el a feltételeket!");
            return;
        }


        if (!password.equals(passwordagain)) {
            Log.e(LOG_TAG,"A két jelszónak egyeznie kell");
            return;
        }
        if (password.length() < 6) {
            Log.e(LOG_TAG,"A jelszónak legalább 6 karakternek kell lennie");
            return;
        }



        Log.i(LOG_TAG,"Regisztrált: "+userName + ", jelszó: " + password + ", email: " + email);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG,"Regisztráció sikeres");
                    atMain();
                } else {
                    Log.w(LOG_TAG,"Regisztráció sikertelen",task.getException());
                    finish();
                }
            }
        });


    }


    private void atMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SECRET_KEY",SECRET_KEY);
        startActivity(intent);
    }

    public void megse(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}