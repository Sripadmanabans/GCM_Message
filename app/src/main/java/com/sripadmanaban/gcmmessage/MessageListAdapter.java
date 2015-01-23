package com.sripadmanaban.gcmmessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom Adapter for message
 * Created by Sripadmanaban on 1/22/2015.
 */
public class MessageListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Profile> profileMessageList;

    public MessageListAdapter(Context context, ArrayList<Profile> profileMessageList)
    {
        this.context = context;
        this.profileMessageList = profileMessageList;
    }

    @Override
    public int getCount()
    {
        return profileMessageList.size();
    }

    @Override
    public Profile getItem(int position)
    {
        return profileMessageList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        int status = 0;
        if(profileMessageList.get(position).getEmail().equals("you"))
        {
            status = 1;
        }
        return status;
    }

    private static class ViewHolder
    {
        TextView textView;
    }

    @SuppressWarnings("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        View rowView = convertView;

        int type = getItemViewType(position);

        if(rowView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(type == 0)
            {
                rowView = inflater.inflate(R.layout.list_recipient, parent, false);
            }
            else
            {
                rowView = inflater.inflate(R.layout.list_sender, parent, false);
            }

            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) rowView.findViewById(R.id.list_message);

            rowView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.textView.setText(profileMessageList.get(position).getMessage());

        return rowView;
    }
}
