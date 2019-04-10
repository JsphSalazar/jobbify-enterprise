package mx.tec.jobbify_enterprise.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import mx.tec.jobbify_enterprise.R;
import mx.tec.jobbify_enterprise.beans.Enterprise;
import mx.tec.jobbify_enterprise.enums.EnterpriseCategory;
import mx.tec.jobbify_enterprise.validators.FormValidator;

/*
 * This class is meant to handle the login and registration
 *
 * It creates a SharedPreference file to store the token for keep logged in
 * It creates two dialog for each, login and registration
 *
 * It also create a new entry in the DB for each new registration using UID(Firebase)
 *       it saves a User object with fullName and phone
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private ConstraintLayout root;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Token to login automatically
        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE);
        String token = prefs.getString("token", "");

        // If there is a token use it to log in immediately
        // otherwise continue with login or register
        if (!TextUtils.isEmpty(token)){
            mAuth.signInWithCustomToken(token).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Intent intent = new Intent(WelcomeActivity.this, EnterpriseDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(root, "Error while logging automatically", Snackbar.LENGTH_LONG).show();
                }
            });
        }
        setContentView(R.layout.activity_welcome);
        root = findViewById(R.id.welcomeConstrainLayout);

    }

    public void onRegisterClick(View v){
        // Creates a dialog above the screen to enter information fields.
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Registration");

        LayoutInflater inflater = LayoutInflater.from(this);
        View cardview_register  = inflater.inflate(R.layout.cardview_register, null);
        final MaterialEditText edtMail, edtPass, edtName, edtPhone;
        final Spinner dropdown;
        edtMail  = cardview_register.findViewById(R.id.edtMail);
        edtPass  = cardview_register.findViewById(R.id.edtPassword);
        edtName  = cardview_register.findViewById(R.id.edtName);
        edtPhone = cardview_register.findViewById(R.id.edtPhone);

        dropdown = cardview_register.findViewById(R.id.categorySpinner);
        dropdown.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, EnterpriseCategory.values()));
        dialog.setView(cardview_register);

        /* If the user press 'DONE':
         * 1) Validates all the fields
         * 2) Try to create a new account
         */
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String email, pass, name, phone, address;
                email = edtMail.getText().toString();
                pass  = edtPass.getText().toString();
                name  = edtName.getText().toString();
                phone = edtPhone.getText().toString();

                if (TextUtils.isEmpty(email) || !FormValidator.isValidEmail(email)){
                    Snackbar.make(root, "Invalid email", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pass) || pass.length() < 6){
                    Snackbar.make(root, "Invalid password (6 char, min)", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(name) || !FormValidator.isValidName(name)){
                    Snackbar.make(root, "Invalid name", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(phone) || !FormValidator.isValidPhone(phone)){
                    Snackbar.make(root, "Invalid phone", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(dropdown.getSelectedItem().toString())){
                    Snackbar.make(root, "Invalid category", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(WelcomeActivity.this, "Registering...", Toast.LENGTH_LONG).show();
                createAccount(email, pass, name, phone, dropdown.getSelectedItem().toString());
            }
        });

        // If press 'CANCEL', just quit the dialog
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void onLoginClick(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Log In");

        LayoutInflater inflater = LayoutInflater.from(this);
        View cardview_login  = inflater.inflate(R.layout.cardview_login, null);
        final MaterialEditText edtMail, edtPass;
        edtMail  = cardview_login.findViewById(R.id.edtMail);
        edtPass  = cardview_login.findViewById(R.id.edtPassword);

        dialog.setView(cardview_login);

        dialog.setPositiveButton("Log in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tryLogIn(edtMail.getText().toString(), edtPass.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void createAccount(String email, String password, final String name, final String phone, final String category){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Save token to future login
                    prefs.edit().putString("token", FirebaseInstanceId.getInstance().getToken()).apply();

                    // Create new user [more info could be added in the future]
                    Enterprise newEnterprise = new Enterprise(name, "", category, "");
                    newEnterprise.addPhone(phone);

                    // Insert into DB, in /users/{uid}          Where {uid} is a number generated by Firebase
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("enterprise").child(firebaseUser.getUid()).setValue(newEnterprise);

                    // Create a new Task, and finish this stack!
                    Intent intent = new Intent(WelcomeActivity.this, EnterpriseDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Send info to user and dev about where it failed
                Log.wtf(TAG, "onFailure: ERROR CREATING ACCOUNT");
                Snackbar.make(root, "Something bad happened, try again!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void tryLogIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                prefs.edit().putString("token", FirebaseInstanceId.getInstance().getToken()).apply();
                Intent intent = new Intent(WelcomeActivity.this, EnterpriseDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                prefs.edit().putString("token", "").apply();
                Snackbar.make(root, "Error, try again!", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
