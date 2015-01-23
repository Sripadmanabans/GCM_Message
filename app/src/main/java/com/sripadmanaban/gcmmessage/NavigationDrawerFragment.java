package com.sripadmanaban.gcmmessage;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Sripadmanaban on 1/19/2015.
 */
public class NavigationDrawerFragment extends Fragment
{
    private NavigationDrawerCallBack mCallBack;

    private String options[] = {"Home", "Register", "List"};

    private ListView listView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private View mFragmentContainerView;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallBack = (NavigationDrawerCallBack) activity;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException("The activity should implement NavigationDrawerCallBack");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        listView = (ListView) view.findViewById(R.id.layout_drawer_list);
        listView.setClickable(true);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectPosition(position);
                mDrawerLayout.closeDrawer(listView);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar)
    {
        Log.d("GCMMessage", fragmentId+"a");
        mFragmentContainerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                toolbar.setTitle("Navigation Drawer");
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        if(mDrawerLayout != null && isDrawerOpen())
        {
            inflater.inflate(R.menu.menu_main, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallBack = null;
    }

    public boolean isDrawerOpen()
    {
        boolean status = mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
        return status;
    }

    private void selectPosition(int position)
    {
        if(mCallBack != null)
        {
            mCallBack.NavigationDrawerClickListener(position);
        }
    }

    public interface NavigationDrawerCallBack
    {
        public void NavigationDrawerClickListener(int position);
    }
}
