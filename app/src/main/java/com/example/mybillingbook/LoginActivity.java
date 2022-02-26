package com.example.mybillingbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.annotations.Nullable;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

public class LoginActivity extends AppCompatActivity {

    //Following are the objects on the login screen

    private TextView countryCodeTextView;

    //Following is the button for the gogole login
    private LinearLayout googleLogin;


    //The register user link
    private TextView registerLink;



    //THe object for progress dialog
    private ProgressDialog progressDialog;

    //Following is the sign in client for ggole
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private int RESULT_CODE_SINGIN=999;



    //Following are the ofur objets for the dynamix login cretaion
    private EditText emailEditTxt;
    private  EditText passwordEdittxt;
    private TextView forgotPassword;
    private TextView loginBtn;






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Following will be the code, to hide the action abr from the splash screen activity
        //Objects.requireNonNull(getSupportActionBar()).hide();

        //Following is the code to change the color of the status bar for the splash screen activityy
        Window window=LoginActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimaryDark));

        initialize();  //Following is the method, to intialize the objects on the app with their respeective ids

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        mAuth=FirebaseAuth.getInstance();

        //COnfiguring the google sign in options
        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleSignInClient = GoogleSignIn.getClient(this,gso);


        //Now, the on click listener for login with google
        //Attach a onClickListener
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInM();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////




        /////////////////////////////////////////////////////////////////////////////////////////////////
        //Now following is the code for the register user bottom sheet dialog
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(LoginActivity.this);
                View dialog=LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottomsheet_registeruser,null);

                EditText emailRegister=dialog.findViewById(R.id.email_register);
                EditText passwordRegister=dialog.findViewById(R.id.password_register);
                EditText confirmEditTextRegister=dialog.findViewById(R.id.confirmPassword_register);
                TextView createAccount=dialog.findViewById(R.id.createAccount_register);


                createAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(emailRegister.getText().toString()))
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Email is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                        else if (TextUtils.isEmpty(passwordRegister.getText().toString()) )
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Password is empty",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                        else if (TextUtils.isEmpty(confirmEditTextRegister.getText().toString()))
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Please confirm password",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                        else if ((passwordRegister.getText().length())<6)
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Weak Password. Provide more than 6 characters",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                            mdToast.show();
                        }
                        else if (!((passwordRegister.getText().toString()).equals(confirmEditTextRegister.getText().toString())))
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Password Mismatch",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                            mdToast.show();
                            passwordRegister.setText("");
                            confirmEditTextRegister.setText("");
                        }
                        else
                        {

                            progressDialog=new ProgressDialog(LoginActivity.this);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show(); //Starting the progress dialog
                            progressDialog.setContentView(R.layout.dialog_loading);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                            //Now, we will register the user and log in to the home page
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailRegister.getText().toString(),passwordRegister.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful())
                                            {
                                                //Following means that the user is registered sucessfully
                                                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailRegister.getText().toString(),passwordRegister.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                             if (task.isSuccessful())
                                                             {
                                                                 progressDialog.dismiss();

                                                                 Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                                                                 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                 startActivity(i);
                                                                 finish();
                                                                 MDToast mdToast=MDToast.makeText(getApplicationContext(),"Logged in",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                                                                 mdToast.show();
                                                             }
                                                             else
                                                             {
                                                                 progressDialog.dismiss();
                                                                 MDToast mdToast=MDToast.makeText(getApplicationContext(),"Failed to log in",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                                                 mdToast.show();
                                                                 bottomSheetDialog.cancel();

                                                             }
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                //Following means that the user failed to register an account
                                                MDToast mdToast=MDToast.makeText(getApplicationContext(),"Failed to create a account",MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR);
                                                mdToast.show();
                                                progressDialog.dismiss();
                                                bottomSheetDialog.cancel();
                                            }
                                        }
                                    });

                        }
                    }
                });


                bottomSheetDialog.setContentView(dialog);
                bottomSheetDialog.show();
            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //The functioning code for the login button////////////////////////////////////////////////////

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailEditTxt.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Email can not be empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else if (TextUtils.isEmpty(passwordEdittxt.getText().toString()))
                {
                    MDToast mdToast=MDToast.makeText(getApplicationContext(),"Password can not be empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                    mdToast.show();
                }
                else
                {
                    progressDialog=new ProgressDialog(LoginActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show(); //Starting the progress dialog
                    progressDialog.setContentView(R.layout.dialog_loading);
                    progressDialog.setCancelable(false);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditTxt.getText().toString(),passwordEdittxt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        //Following is the code for the sucessfull login
                                        progressDialog.dismiss();
                                        Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                        MDToast mdToast=MDToast.makeText(getApplicationContext(),"Logged in",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO);
                                        mdToast.show();
                                    }
                                    else
                                    {
                                        //Following is the code if login fails
                                        progressDialog.dismiss();
                                        MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_WARNING);
                                        mdToast.show();
                                    }
                                }
                            });
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////



        ////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////Following is the code for the forgot password bottom sheet//////////////////////////

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(LoginActivity.this);
                View dialog=LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottomsheet_forgotpassword,null);

                //The objects on the bottom sheet
                EditText emailForgetPassword=dialog.findViewById(R.id.email_forgotPassword);
                TextView sendLink=dialog.findViewById(R.id.sendLink_forgotPassword);

                sendLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(emailForgetPassword.getText().toString()))
                        {
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),"Email can not be empty",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING);
                            mdToast.show();
                        }
                        else
                        {
                            progressDialog=new ProgressDialog(LoginActivity.this);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show(); //Starting the progress dialog
                            progressDialog.setContentView(R.layout.dialog_loading);
                            progressDialog.setCancelable(false);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                            FirebaseAuth.getInstance().sendPasswordResetEmail(emailForgetPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                MDToast mdToast=MDToast.makeText(getApplicationContext(),"Password recovery mail sent sucessfully",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
                                                mdToast.show();
                                                bottomSheetDialog.cancel();

                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                                                mdToast.show();
                                                bottomSheetDialog.cancel();
                                            }

                                        }
                                    });
                        }
                    }
                });


                bottomSheetDialog.setContentView(dialog);
                bottomSheetDialog.show();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////




















    }











    ///////////////////////////////////////////////////////////////////////////////////////////////
    //Following is the method, we will use to initialize all the ids//////////////////////////////
    private void initialize() {

        googleLogin=findViewById(R.id.googleLogin);

        registerLink=findViewById(R.id.registerLink_loginPage);

        emailEditTxt=findViewById(R.id.email_login);
        passwordEdittxt=findViewById(R.id.password_login);
        forgotPassword=findViewById(R.id.forgotPasswordBtn_login);
        loginBtn=findViewById(R.id.loginBtn_login);



    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void signInM() {
        Intent singInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent,RESULT_CODE_SINGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CODE_SINGIN) {        //just to verify the code
            //create a Task object and use GoogleSignInAccount from Intent and write a separate method to handle singIn Result.

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RESULT_CODE_SINGIN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately

                    MDToast mdToast=MDToast.makeText(getApplicationContext(),e.toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                    mdToast.show();
                    // ...
                }
            }

        }
    }


    private void firebaseAuthWithGoogle(String idToken) {

        progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); //Starting the progress dialog
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();

                            Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(i);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.

                            progressDialog.dismiss();
                            MDToast mdToast=MDToast.makeText(getApplicationContext(),task.getException().toString(),MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                            mdToast.show();

                        }

                        // ...
                    }
                });
    }




}