package com.sripadmanaban.gcmmessage;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Sending the message
 * Created by Sripadmanaban on 1/20/2015.
 */
public class SendFragment extends Fragment implements ConstantsHolder
{
    private EditText editText_message;
    private String message;
    private Bundle bundle;
    private ListView listView;

    private Activity activity;

    private ArrayList<Profile> messagesList;

    private MessageListAdapter adapter;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messagesList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_send, container, false);
        editText_message = (EditText) view.findViewById(R.id.send_message_editText);
        listView = (ListView) view.findViewById(R.id.send_message_list);

        bundle = getArguments();

        adapter = new MessageListAdapter(activity, messagesList);
        listView.setAdapter(adapter);

        Button button = (Button) view.findViewById(R.id.send_button_send);



        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                message = editText_message.getText().toString();
                editText_message.setText("");
                editText_message.setHint(R.string.send_message_hint);
                Profile profile = new Profile();
                profile.setMessage(message);
                profile.setEmail("you");
                messagesList.add(profile);
                adapter.notifyDataSetChanged();
                AsyncSendMessageTask send = new AsyncSendMessageTask();
                send.execute(null, null);
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(activity).registerReceiver(localBroadcastReceiver, new IntentFilter(TAG));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(localBroadcastReceiver);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        activity = null;
    }

    private class AsyncSendMessageTask extends AsyncTask<Void, Void, String>
    {
        private String urlString = "http://1-dot-t-bond-830.appspot.com/message/send";
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
                jsonObjectBuilder.add(EMAIL, bundle.getString(EMAIL));
                jsonObjectBuilder.add(MESSAGE, message);

                JsonObject json = jsonObjectBuilder.build();

                OutputStream out = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

                writer.write(json.toString());
                writer.flush();
                writer.close();
                out.close();

                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;

                while((line = reader.readLine()) != null)
                {
                    response.append(line);
                    response.append("\n");
                }
                in.close();


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
        }
    }

    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Profile profile = new Profile();
            profile.setEmail(intent.getStringExtra(EMAIL_GCM));
            profile.setMessage(intent.getStringExtra(MESSAGE_GCM));
            messagesList.add(profile);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, intent.getExtras().getString(MESSAGE_GCM), Toast.LENGTH_SHORT).show();
        }
    };

}
