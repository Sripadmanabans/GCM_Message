package com.sripadmanaban.gcmmessage;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallBack,
        ListNamesFragment.ListNameCallBack, ConstantsHolder
{

    private Bundle bundleRecipient;
    private Toolbar toolbar;

    private ArrayList<String> messagesList;
    private ArrayList<Profile> profilesList;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messagesList = new ArrayList<>();
        setUpNavigationDrawer();

        openFragmentSetup(0);

        profilesList = new ArrayList<>();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AsyncListTask listTask = new AsyncListTask();

        try
        {
            profilesList = listTask.execute(null, null).get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void setUpNavigationDrawer()
    {
        NavigationDrawerFragment navigationDrawerFragment =
                (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        navigationDrawerFragment.setUp(R.id.fragment_navigation_container, drawerLayout, toolbar);
    }

    @Override
    public void NavigationDrawerClickListener(int position)
    {
        openFragmentSetup(position);
    }

    private void openFragmentSetup(int position)
    {
        Fragment fragment;
        switch(position)
        {
            case 0:
                fragment = new HomeFragment();
                toolbar.setTitle("Home");
                openFragment(fragment, "Home");
                break;
            case 1:
                fragment = new RegisterFragment();
                toolbar.setTitle("Register");
                openFragment(fragment, "Register");
                break;
            case 2:
                fragment = new ListNamesFragment();
                fragment.setArguments(setListBundle());
                toolbar.setTitle("List of Names");
                openFragment(fragment, "List");
                break;
            case 3:
                fragment = new SendFragment();
                fragment.setArguments(bundleRecipient);
                toolbar.setTitle(bundleRecipient.getString(FIRST_NAME) + " " + bundleRecipient.getString(LAST_NAME));
                openFragment(fragment, "Send");
                break;
        }
    }

    private void openFragment(Fragment fragment, String tag)
    {
        Fragment frag = getFragmentManager().findFragmentByTag(tag);
        if(frag == null)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, tag);
            transaction.commit();
        }
    }

    @Override
    public void ListItemSelected(Profile profile)
    {
        Log.d(TAG, profile.toString() + " main");
        bundleRecipient = new Bundle();
        bundleRecipient.putString(FIRST_NAME, profile.getFirstName());
        bundleRecipient.putString(LAST_NAME, profile.getLastName());
        bundleRecipient.putString(EMAIL, profile.getEmail());
        bundleRecipient.putStringArrayList(MESSAGE_LIST, messagesList);

        openFragmentSetup(3);
    }

    private class AsyncListTask extends AsyncTask<Void, Void, ArrayList<Profile>>
    {
        private String urlString = "http://1-dot-t-bond-830.appspot.com/message/list";
        private HttpURLConnection connection;
        private ArrayList<Profile> profileList = new ArrayList<>();
        private DBAdapter dbAdapter = DBAdapter.getInstance(context);

        @Override
        protected ArrayList<Profile> doInBackground(Void... params)
        {
            try
            {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                JsonReader reader = Json.createReader(input);

                JsonObject jsonObject = reader.readObject();

                reader.close();
                input.close();

                Log.d(TAG, jsonObject.toString());

                dbAdapter.open();
                dbAdapter.truncateTable();
                JsonArray jsonArray = jsonObject.getJsonArray(PROFILE);

                for(JsonValue value : jsonArray)
                {
                    JsonObject object = (JsonObject) value;
                    Profile profile = new Profile();
                    profile.setFirstName(object.getString(FIRST_NAME));
                    profile.setLastName(object.getString(LAST_NAME));
                    profile.setEmail(object.getString(EMAIL));

                    Log.d(TAG, profile.toString());

                    dbAdapter.insertContact(profile.getFirstName(), profile.getLastName(), profile.getEmail());

                    profileList.add(profile);
                }

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(connection != null)
                {
                    connection.disconnect();
                }
                dbAdapter.close();
            }
            return profileList;
        }
    }

    private Bundle setListBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PROFILE_LIST, profilesList);
        return bundle;
    }
}
