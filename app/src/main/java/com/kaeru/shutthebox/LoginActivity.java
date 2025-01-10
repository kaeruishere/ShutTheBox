package com.kaeru.shutthebox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "FirebaseAuth";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private com.google.android.gms.common.SignInButton btnGoogleSignIn;

    private EditText rFullname , rUsername, rEmail, rPassword, rPasswordConfirm , sUsername , sPassword;
    private Button rRegisterButton, sSignInButton ;
    private LinearLayout registerLayout, signInLayout , GLayout;
    private TextView rToS, sToR;

    private EditText gUsernameEditText;
    private Button gUsernameSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Google Sign-In Ayarları
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        setupListeners();
    }

    private void initViews() {
        rFullname = findViewById(R.id.r_FullName);
        rUsername = findViewById(R.id.r_Username);
        rEmail = findViewById(R.id.r_Email);
        rPassword = findViewById(R.id.r_Password);
        rPasswordConfirm = findViewById(R.id.r_PasswordConfirm);
        rRegisterButton = findViewById(R.id.r_RegisterButton);
        sSignInButton = findViewById(R.id.s_SignInButton);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);

        sUsername = findViewById(R.id.s_Username);
        sPassword = findViewById(R.id.s_Password);
        registerLayout = findViewById(R.id.RegisterLayout);
        signInLayout = findViewById(R.id.SignInLayout);
        GLayout = findViewById(R.id.GLayout);
        gUsernameEditText = findViewById(R.id.g_Username);
        gUsernameSaveButton = findViewById(R.id.g_UsernameSaveButton);
        rToS = findViewById(R.id.r_to_s);
        sToR = findViewById(R.id.s_to_r);
    }

    private void setupListeners() {
        // Kayıt Olma
        rRegisterButton.setOnClickListener(view -> {
            String fullname = rFullname.getText().toString().trim();
            String username = rUsername.getText().toString().trim();
            String email = rEmail.getText().toString().trim();
            String password = rPassword.getText().toString().trim();
            String passwordConfirm = rPasswordConfirm.getText().toString().trim();

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            registerWithEmail(fullname , username, email, password);
        });

        sSignInButton.setOnClickListener(view -> {
            String username = sUsername.getText().toString().trim();
            String password = sPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                signInWithUsername(username, password);
            }
        });

        gUsernameSaveButton.setOnClickListener(view -> {
            String googleUsername = gUsernameEditText.getText().toString().trim();
            if (!googleUsername.isEmpty()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    saveUserToFirestore(user.getUid(), googleUsername, user.getEmail(), user.getDisplayName());
                }
            } else {
                Toast.makeText(LoginActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
            }
        });


        // Google ile Giriş
        btnGoogleSignIn.setOnClickListener(view -> signInWithGoogle());

        // Giriş/Kayıt Arası Geçiş
        rToS.setOnClickListener(view -> switchLayout(false));
        sToR.setOnClickListener(view -> switchLayout(true));
    }

    private void signInWithUsername(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    
                    Toast.makeText(LoginActivity.this, "Sign-In Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Sign-In Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerWithEmail(String fullname ,String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    saveUserToFirestore(user.getUid(), username, email, fullname);
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                   
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(user.getUid()).get()
                            .addOnCompleteListener(documentTask -> {
                                if (documentTask.isSuccessful()) {
                                    if (documentTask.getResult().exists()) {
                                       
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                      
                                        GLayout.setVisibility(View.VISIBLE);
                                        btnGoogleSignIn.setVisibility(View.GONE);
                                        registerLayout.setVisibility(View.GONE);
                                        signInLayout.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Google Sign-In Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserToFirestore(String userId, String username, String email, String displayName) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("displayName", displayName);
        user.put("registrationDate", System.currentTimeMillis());

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                  
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error Saving User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




    private void switchLayout(boolean toRegister) {
        registerLayout.setVisibility(toRegister ? View.VISIBLE : View.GONE);
        signInLayout.setVisibility(toRegister ? View.GONE : View.VISIBLE);
        GLayout.setVisibility(View.GONE);  // Hide Google username input by default
    }

}
