package com.shoppinfever.fragments;

import android.accounts.Account;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.plus.Plus;
import com.shoppinfever.MyApplication;
import com.shoppinfever.R;
import com.shoppinfever.services.Util;
import com.shoppinfever.services.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private CallbackManager mCallbackManager;
    private static final String SERVER_CLIENT_ID = "222396816157-k4e4k9oi2b1cc4nnq3oksac4l9866rqa.apps.googleusercontent.com";
    /* Is there a ConnectionResult resolution in progress? */
   /* RequestCode for resolutions involving sign-in */
    private static final int RC_SIGN_IN = 1;

    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    private static final int RC_PERM_GET_ACCOUNTS = 2;

    /* Keys for persisting instance variables in savedInstanceState */
    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_SHOULD_RESOLVE = "should_resolve";

    /* Client for accessing Google APIs */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    public LoginFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        Util.setContext(getActivity()); // For Toast and Logs

        if (savedInstanceState != null)
        {
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
        }

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    // [START on_start_on_stop]
    @Override
    public void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    // [END on_start_on_stop]

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
        outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.findViewById(R.id.fbLoginButton).setOnClickListener(this);
        view.findViewById(R.id.gLoginButton).setOnClickListener(this);
        LoginManager.getInstance().registerCallback(mCallbackManager, mCallback);
        return view;
    }

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>()
    {
        @Override
        public void onSuccess(LoginResult loginResult)
        {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null)
            {
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response)
                            {
                                sendData(response);
                                Util.log(response.toString());
                                //sendData(profile);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }
            else
            {
                Util.toast("profile is null");
            }
        }

        @Override
        public void onCancel()
        {
            Util.toast("Login Canceled");

        }

        @Override
        public void onError(FacebookException error)
        {
            Util.toast("Something went wrong!");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Util.log("onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN)
        {
            /*// If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }*/

            Util.toast(resultCode + ": resultCode");

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
        else
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void sendData(final GraphResponse response)
    {
        // Util.toast("Started");
        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();
        String url = "http://shoppinfever-thnkin.rhcloud.com/JsonServerTest/ReceiveData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Util.toast(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Util.toast(error.toString());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                try
                {
                    params.put("name", response.getJSONObject().getString("name"));

                    params.put("email", response.getJSONObject().getString("email"));

                    params.put("id", response.getJSONObject().getString("id"));
                }
                catch (JSONException e)
                {
                    Util.toast(e.toString());
                }
                params.put("service", "Facebook");
                return params;
            }
        };
        queue.add(postRequest);
        Util.toast("Ended");
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.fbLoginButton)
        {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        }
        else if (v.getId() == R.id.gLoginButton)
        {
            onSignInClicked();
        }
    }

    private void onSignInClicked()
    {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        Util.toast("Signing in...");
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Util.log("onConnected:" + bundle);
        mShouldResolve = false;

        Util.toast("Logged in.");
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Util.log("onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve)
        {
            if (connectionResult.hasResolution())
            {
                try
                {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIsResolving = true;
                }
                catch (IntentSender.SendIntentException e)
                {
                    Util.log("Could not resolve ConnectionResult:" + e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
            else
            {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        }
        else
        {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    private void showErrorDialog(ConnectionResult connectionResult)
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (apiAvailability.isUserResolvableError(resultCode))
            {
                apiAvailability.getErrorDialog(getActivity(), resultCode, RC_SIGN_IN,
                        new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                mShouldResolve = false;
                                //showSignedOutUI();
                            }
                        }).show();
            }
            else
            {
                Util.log("Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Util.toast(errorString);

                mShouldResolve = false;
                //showSignedOutUI();
            }
        }
    }

    private class GetIdTokenTask extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

            String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID; // Not the app's client ID.
            try
            {
                return GoogleAuthUtil.getToken(MyApplication.getAppContext(), account, scopes);
            }
            catch (IOException e)
            {
                Util.log("Error retrieving ID token." + e);
                return null;
            }
            catch (GoogleAuthException e)
            {
                Util.log("Error retrieving ID token." + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Util.log("ID token: " + result);
            if (result != null)
            {
                // Successfully retrieved ID Token
                // ...
            }
            else
            {
                // There was some error getting the ID Token
                // ...
            }
        }

    }
}