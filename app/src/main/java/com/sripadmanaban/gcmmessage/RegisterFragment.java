package com.sripadmanaban.gcmmessage;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Register fragment
 * Created by Sripadmanaban on 1/19/2015.
 */
public class RegisterFragment extends Fragment implements ConstantsHolder
{
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String SENDER_ID = "128098291713";

    private String regId;
    private String firstName, lastName, email;

    private GoogleCloudMessaging gcm;
    private Context context;
    private Activity activity;

    private SharedPreferences preferences;

    private EditText editText_firstName, editText_lastName, editText_email;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        context = activity.getApplicationContext();
        this.activity = activity;
        preferences = this.activity.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(checkPlayServices())
        {
            gcm = GoogleCloudMessaging.getInstance(activity);
            regId = getRegistrationId();

            Log.i(TAG, "RegId : " + regId);

            if (regId.isEmpty())
            {
                registerInBackground();
                Log.i(TAG, "RegId : " + regId);
            }
        }
        else
        {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        editText_email = (EditText) view.findViewById(R.id.register_editText_email);
        editText_firstName = (EditText) view.findViewById(R.id.register_editText_firstName);
        editText_lastName = (EditText) view.findViewById(R.id.register_editText_lastName);

        Button button = (Button) view.findViewById(R.id.register_button_register);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                email = editText_email.getText().toString();
                firstName = editText_firstName.getText().toString();
                lastName = editText_lastName.getText().toString();

                if(regId == null)
                {
                    Toast.makeText(activity, "Registration Failed. Please Try Again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AsyncRegisterTask registerTask = new AsyncRegisterTask();
                    registerTask.execute(null, null);
                }
            }
        });

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        context = null;
        activity = null;
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "This device is not supported");
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId()
    {
        String temp = preferences.getString(REG_ID, "");
        if(temp.isEmpty())
        {
            Log.d(TAG, "The Registration ID has not been found");
            return "";
        }
        return temp;
    }

    private void registerInBackground()
    {
        new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    if(gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regId = gcm.register(SENDER_ID);

                    Log.d(TAG, regId + " here");

                    storeRegistrationId(regId);


                }
                catch (IOException e)
                {
                    Log.d(TAG, "Error inside registration");
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(null, null);
    }

    private void storeRegistrationId(String regs)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(REG_ID, regs);
        editor.apply();
    }

    private class AsyncRegisterTask extends AsyncTask<Void, Void, String>
    {
        private String urlString = "http://1-dot-t-bond-830.appspot.com/message/register";
        private HttpURLConnection connection;
        private StringBuilder response = new StringBuilder();

        @Override
        protected String doInBackground(Void... params)
        {

            try
            {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                jsonObjectBuilder.add(FIRST_NAME, firstName);
                jsonObjectBuilder.add(LAST_NAME, lastName);
                jsonObjectBuilder.add(EMAIL, email);
                jsonObjectBuilder.add(REG_ID, regId);

                JsonObject json = jsonObjectBuilder.build();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, ENCODING));

                writer.write(json.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStream input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String line;

                while((line = reader.readLine()) != null)
                {
                    response.append(line);
                    response.append("\n");
                }
                input.close();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(connection != null)
                {
                    connection.disconnect();
                }
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String s)
        {
            Log.d(TAG, s);
            Toast.makeText(activity, "Registration was Successful!!", Toast.LENGTH_SHORT).show();
        }

    }
}
