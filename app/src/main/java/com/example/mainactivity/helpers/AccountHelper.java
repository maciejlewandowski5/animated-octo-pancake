package com.example.mainactivity.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mainactivity.MainActivity;
import com.example.mainactivity.R;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import modelv2.User;
import modelv2.UserSession;

public class AccountHelper {
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private Activity activity;
    private static final int RC_SIGN_IN = 1001;
    private SignInSuccessful signInSuccessful;

    public AccountHelper(Activity activity) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.googleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
        this.activity = activity;
        this.signInSuccessful = null;
    }

    public void setSignInSuccessful(SignInSuccessful signInSuccessful) {
        this.signInSuccessful = signInSuccessful;
    }

    public void signOut(String tag) {
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w(tag, "Signed out of google");
                // Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                Toast.makeText(activity.getApplicationContext(), "You Signed out", Toast.LENGTH_LONG).show();
                Utils.toastMessage("You Signed out", activity);
                activity.finish();
                //   activity.startActivity(intent);
            }
        });
    }

    public void configureGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signInUsingGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public int getRCSGININCode() {
        return RC_SIGN_IN;
    }

    public void verifySignInResults(String tag, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Utils.toastMessage(activity.getString(R.string.you_logged_in) + task.getResult().getDisplayName(), activity);
            assert account != null;
            firebaseAuthWithGoogle(account, tag);
        } catch (ApiException e) {
            Log.w(tag, "Google sign in failed", e);
            Utils.toastMessage(activity.getString(R.string.login_failed) + task.getException(), activity);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account, String tag) {

        Log.d(tag, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Log.d(tag, "signInWithCredential:success: currentUser: " + user.getEmail());
                    Utils.toastMessage("Firebase Authentication failed:" + task.getException(), activity);
                    if (user != null) {
                        if (signInSuccessful != null) {

                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                FirebaseFirestore.getInstance().collection("Users").
                                        document(user.getUid()).
                                        set(UserSession.CreateNewUser(user.getDisplayName(), user.getUid()));

                            }

                            signInSuccessful.signInSuccessful(user);//launchStartActivity(user);
                        }
                    }

                } else {
                    Log.w(tag, "signInWithCredential:failure", task.getException());
                    Utils.toastMessage("Firebase Authentication failed:" + task.getException(), activity);
                }
            }
        });


    }

    public interface SignInSuccessful {
        void signInSuccessful(FirebaseUser user);
    }
}


