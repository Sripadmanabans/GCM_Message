package com.sripadmanaban.gcmmessage;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to hold data about people
 * Created by Sripadmanaban on 1/20/2015.
 */
public class Profile implements Parcelable
{
    private String firstName;
    private String lastName;
    private String email;
    private String message;

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        return firstName + " " + lastName;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags)
    {
        destination.writeString(firstName);
        destination.writeString(lastName);
        destination.writeString(email);
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>()
    {
        @Override
        public Profile createFromParcel(Parcel source)
        {
            Profile profile = new Profile();
            profile.firstName = source.readString();
            profile.lastName = source.readString();
            profile.email = source.readString();
            return profile;
        }

        @Override
        public Profile[] newArray(int size)
        {
            return new Profile[size];
        }
    };
}
