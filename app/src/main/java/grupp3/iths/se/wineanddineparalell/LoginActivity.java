package grupp3.iths.se.wineanddineparalell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.email_et);
        mPassword = findViewById(R.id.password_et);

        // If current user isn't null. Switch to MainActivity.
        // If current user is null. Stay on LoginFragment.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // TODO: Change so it switches to SearchFragment. Then delete setFragment call in MainActivity.
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };

        // Login to user account.
        mLogin = findViewById(R.id.login_btn);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

    }

    /**
     * Check if user is already logged in or not.
     * If not. Prompt LoginFragment. Otherwise prompt SearchFragment.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Check if email or password field is left empty
     * Check if email or password is wrong. Checking against user database.
     */
    private void startSignIn() {

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(LoginActivity.this, "Leave no fields empty.", Toast.LENGTH_LONG).show();

        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Wrong email or password. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
