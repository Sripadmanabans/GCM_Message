package com.sripadmanaban.gcmmessage;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Listing the Names
 * Created by Sripadmanaban on 1/20/2015.
 */
public class ListNamesFragment extends Fragment implements ConstantsHolder
{
    private List<Profile> list;
    private Activity activity;
    private ListNameCallBack mCallBack;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
        try
        {
            mCallBack = (ListNameCallBack) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("The activity must implement ListNameCallBack");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        list = bundle.getParcelableArrayList(PROFILE_LIST);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list_names, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_items);
        ArrayAdapter<Profile> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectPosition(list.get(position));
            }
        });

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        activity = null;
        mCallBack = null;
    }

    private void selectPosition(Profile profile)
    {
        if(mCallBack != null)
        {
            mCallBack.ListItemSelected(profile);
        }
    }

    public interface ListNameCallBack
    {
        public void ListItemSelected(Profile profile);
    }
}
