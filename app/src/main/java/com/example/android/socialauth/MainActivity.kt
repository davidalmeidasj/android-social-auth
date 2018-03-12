package com.example.android.socialauth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Callable
import com.facebook.appevents.AppEventsLogger;

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001

    // UI references.
    private var mGoogleSignInButton: SignInButton? = null
    private var mFacebookSignInButton: LoginButton? = null


    private var mFacebookCallbackManager: CallbackManager? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)
        mFacebookCallbackManager = CallbackManager.Factory.create()


        setContentView(R.layout.activity_main)

        mGoogleSignInButton = google_sign_in_button
        mGoogleSignInButton?.let {
            it.setOnClickListener({ signInWithGoogle() })
        }

        mFacebookSignInButton = facebook_sign_in_button
        mFacebookSignInButton?.registerCallback(mFacebookCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        handleSignInResult(Callable<Void> {
                            LoginManager.getInstance().logOut()
                            null
                        })
                    }

                    override fun onCancel() {
                        handleSignInResult(null)
                    }

                    override fun onError(error: FacebookException) {
                        Log.d(MainActivity::class.java.canonicalName, error.message)
                        handleSignInResult(null)
                    }
                }
        )



        val button = button2
        button.setOnClickListener {
            newActivity()
        }
    }

    private fun signInWithGoogle() {
        mGoogleApiClient?.disconnect()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun newActivity() {
        val logger = AppEventsLogger.newLogger(this)
        logger.logEvent("Fazendo login")

        val intent = Intent(this, NewsArticles::class.java)
        startActivity(intent)
    }


    private fun handleSignInResult(logout: Callable<Void>?) {
        if (logout == null) {
            /* Login error */
            Toast.makeText(applicationContext, R.string.login_error, Toast.LENGTH_SHORT).show()
        } else {
            /* Login success */
            newActivity()
        }
    }
}
