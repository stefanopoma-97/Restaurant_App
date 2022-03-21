package com.poma.restaurant.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class User implements Parcelable {

    private String username;
    private String password;
    private long date;
    private String email;
    private String location;
    private String name;
    private String surname;
    private String city_id;
    private String id;
    private boolean admin;
    public static final String USER_DATA_EXTRA = "com.poma.restaurant.model.USER_DATA_EXTRA";

    //shared preferences
    private static final String USER_MODEL_PREFERENCES = "user_pref";
    private static final String ADMIN_KEY = "user_pref_admin";
    private static final String EMAIL_KEY = "user_pref_email";
    private static final String ID_KEY = "user_pref_id";
    private static final String PASSWORD_KEY = "user_pref_password";


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
        if(in.readByte() == PRESENT)
        {
            this.admin = in.readInt() == 1;
        }
        if(in.readByte() == PRESENT)
        {
            this.city_id = in.readString();
        }
        if(in.readByte() == PRESENT)
        {
            this.id = in.readString();
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

        if(!(Boolean.valueOf(this.admin)==null))
        {
            dest.writeByte(PRESENT);
            dest.writeInt(this.admin ? 1 : 0);
        } else {
            dest.writeByte(NOT_PRESENT);
        }

        if(!TextUtils.isEmpty(this.city_id))
        {
            dest.writeByte(PRESENT);
            dest.writeString(this.city_id);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
        if(!TextUtils.isEmpty(this.id))
        {
            dest.writeByte(PRESENT);
            dest.writeString(this.id);
        } else {
            dest.writeByte(NOT_PRESENT);
        }
    }

    private User(final String username, final String password) {

        this.username = username;
        this.password = password;
        this.admin = false;
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

    public User withAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    public User withCity_id(String city_id) {
        this.city_id = city_id;
        return this;
    }

    public User withId(String id) {
        this.id = id;
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

    public boolean getAdmin() {
        return this.admin;
    }

    public String getCity_id() {
        return this.city_id;
    }

    public String getID() {
        return this.id;
    }


    public void save(final Context ctx) {
        Log.d("USER", "invoco save su shared preferences");
        final SharedPreferences prefs = ctx.getSharedPreferences(USER_MODEL_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ADMIN_KEY,admin);
        editor.putString(EMAIL_KEY,email);
        editor.putString(PASSWORD_KEY,password);
        editor.putString(ID_KEY,id);
        editor.commit();

    }

    public static User load(final Context ctx) {
        Log.d("USER", "invoco load su shared preferences");
        final SharedPreferences prefs = ctx.getSharedPreferences(USER_MODEL_PREFERENCES,
                Context.MODE_PRIVATE);
        boolean r_admin = prefs.getBoolean(ADMIN_KEY,false);
        String r_email = prefs.getString(EMAIL_KEY,"");
        String r_password = prefs.getString(PASSWORD_KEY,"");
        String r_id = prefs.getString(ID_KEY,"");
        User user = null;
        if(r_email != "") {
            user = User.create(r_email, r_password).withAdmin(r_admin).withId(r_id);
            return user;
        }
        return user;
    }

    public void logout(final Context ctx) {
        Log.d("USER", "invoco logout su shared preferences");
        ctx.getSharedPreferences(USER_MODEL_PREFERENCES,
                Context.MODE_PRIVATE).edit().clear().commit();
    }


}
