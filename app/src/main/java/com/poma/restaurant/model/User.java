package com.poma.restaurant.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class User implements Parcelable {

    private String username;
    private String password;
    private long date;
    private String email;
    private String location;
    private String name;
    private String surname;
    public static final String USER_DATA_EXTRA = "com.poma.restaurant.model.USER_DATA_EXTRA";

    private static final byte PRESENT = 1;
    private static final byte NOT_PRESENT = 0;

    //Obbligatorio
    //From parcel to java class
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[0];
        }
    };

    //costructor to pass from parcel to java object
    public User(Parcel in) {
        this.username = in.readString();
        this.password = in.readString();

        if(in.readByte() == PRESENT)
        {
            this.date = in.readLong();
        }
        if(in.readByte() == PRESENT)
        {
            this.email = in.readString();
        }

        if(in.readByte() == PRESENT)
        {
            this.location = in.readString();
        }

        if(in.readByte() == PRESENT)
        {
            this.name = in.readString();
        }
        if(in.readByte() == PRESENT)
        {
            this.surname = in.readString();
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    //from object to parcel
    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(this.username);
        dest.writeString(this.password);

        if(!(Long.valueOf(this.date)==null))
        {
            dest.writeByte(PRESENT);
            dest.writeLong(this.date);
        } else {
            dest.writeByte(NOT_PRESENT);
        }

        if(!TextUtils.isEmpty(this.email))
        {
            dest.writeByte(PRESENT);
            dest.writeString(this.email);
        } else {
            dest.writeByte(NOT_PRESENT);
        }

        if(!TextUtils.isEmpty(this.location))
        {
            dest.writeByte(PRESENT);
            dest.writeString(this.location);
        } else {
            dest.writeByte(NOT_PRESENT);
        }

        if(!TextUtils.isEmpty(this.name))
        {
            dest.writeByte(PRESENT);
            dest.writeString(this.name);
        } else {
            dest.writeByte(NOT_PRESENT);
        }

        if(!TextUtils.isEmpty(this.surname))
        {
            dest.writeByte(PRESENT);
            dest.writeString(this.surname);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }

    private User(final String username, final String password) {

        this.username = username;
        this.password = password;
    }

    public static User create(final String username, final String password) {
        final User userModel = new User(username, password);
        return userModel;
    }

    public User withDate(long date) {
        this.date = date;
        return this;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public User withLocation(String location) {
        this.location = location;
        return this;
    }

    public User withName(String name) {
        this.name = name;
        return this;
    }

    public User withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public long getDate() {
        return this.date;
    }
}
