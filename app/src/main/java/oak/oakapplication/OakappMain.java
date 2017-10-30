package oak.oakapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.client.utilities.Base64;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationListener;
import com.firebase.client.Firebase;
import com.firebase.client.core.Context;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by viktor on 17.7.2017.
 */

public class OakappMain extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        user = new User();
        firebaseUser = null;
        selfPointer = this;
        postsToShow = new ArrayList<Post>();
        rankBorders = new ArrayList<Long>();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        loacationProvider = LocationServices.getFusedLocationProviderClient(this);


        long numberOfRanks = remoteConfig.getLong("count_levels");
        for (int i = 2; i < numberOfRanks + 1; ++i) {
            OakappMain.rankBorders.add(remoteConfig.getLong("rep_min_" + String.valueOf(i) + "_level" ));
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location l)
            {
                OakappMain.lastLocation = l;
            }
        };

        mLocationManager = new LocationRequest();

        mLocationManager.setInterval(LOCATION_REFRESH_TIME);
        mLocationManager.setSmallestDisplacement(LOCATION_REFRESH_DISTANCE);


    }


    static String  getRankFromLevel(int level)  {
        if (level == -1) {
            return  "ERROR";
        }
        return Resources.getSystem().getString(R.string.default_rank +  level); }

    static int getRankLevelFromRep(int reputation) {

        for (int i = 0; i < rankBorders.size(); ++i)
        {
            if (reputation < rankBorders.get(i))
                return i;
        }

        return -1;
    }


    public static void getUserByUid(final String uid,final UserInterface ui)
    {
        Query filter =  FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("mId").equalTo(uid);


        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child(uid).getValue(User.class);
                ui.UserListener(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getUserByUsername(final User user,final UserInterface callBack)
    {
        Query filter =  FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("mUniqueName").equalTo(user.mUniqueName);


        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child(user.mId).getValue(User.class);
                callBack.UserListener(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static String getAccount(android.content.Context c) {
        Account[] accounts = AccountManager.get(c).
                getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0].name;
    }

    public static void getPostByKey(final String id, final PostListener listener)
    {
        Query filter = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("mKey").equalTo(id);

        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.child(id).getValue(Post.class);
                listener.OnFinished(p);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
;    }

    public static String createGroup(String senderId, String groupId, String registrationId, String apiKey)
            throws IOException, JSONException {
        URL url = new URL("https://android.googleapis.com/gcm/notification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", apiKey);
        con.setRequestProperty("project_id", senderId);

        /*con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Authorization", "key=" + apiKey);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST"); */
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "create");
        data.put("notification_key_name", groupId);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        Log.i(TAG, "Response code: " + con.getResponseCode());
        // Log.i(TAG, "ErrorStream: " + con.getErrorStream());
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");

    }

    public static String addToGroup(String senderId, String userEmail, String registrationId, String idToken)
            throws IOException, JSONException {
        URL url = new URL("https://android.googleapis.com/gcm/googlenotification");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);

        // HTTP request header
        con.setRequestProperty("project_id", senderId);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");
        con.connect();

        // HTTP request
        JSONObject data = new JSONObject();
        data.put("operation", "add");
        data.put("notification_key_name", userEmail);
        data.put("registration_ids", new JSONArray(Arrays.asList(registrationId)));
        data.put("id_token", idToken);

        OutputStream os = con.getOutputStream();
        os.write(data.toString().getBytes("UTF-8"));
        os.close();

        // Read the response into a string
        InputStream is = con.getInputStream();
        String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
        is.close();

        // Parse the JSON string and return the notification key
        JSONObject response = new JSONObject(responseString);
        return response.getString("notification_key");

    }

    public static void getCommentByKey(final String id, final CommentListener listener)
    {
        Query filter = FirebaseDatabase.getInstance().getReference().child("Comments").orderByChild("mKey").equalTo(id);

        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Comment c = dataSnapshot.child(id).getValue(Comment.class);
                listener.OnFinished(c);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public static String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }



    public static boolean WordLimit(String text, int maxChar, int minChar, String object,  View v) {
        object = String.valueOf(Character.toUpperCase(object.charAt(0))) + object.substring(1);
        if (text.length() > maxChar) {
            Snackbar.make(v, object +  " nemoze mat viac ako " + String.valueOf(maxChar) + " pismenok", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return  false;
        }

        if (text.length() < minChar) {
            Snackbar.make(v, object +  " nemoze mat menej ako " + String.valueOf(minChar) + " pismenok", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return false;
        }

        return true;
    }

    public static void SaveUserByUid(final User user) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(user.mId).setValue(user);
    }

    public static void SavePostByKey(final Post post) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(post.mKey).setValue(post);
    }

    public static void SaveFeedbackComByKey(final FeedbackComment feedbackComment, String cast, String komunikacia) {
        FirebaseDatabase.getInstance().getReference().child("Feedback").child(cast).child(komunikacia).child("Comments").child(feedbackComment.mKey).setValue(feedbackComment);
    }

    public static void SaveCommentByKey(final Comment comment) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(comment.mKey).setValue(comment);
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;}

        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return selfPointer.getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return selfPointer.getString(R.string.one_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + selfPointer.getString(R.string.x_minutes_ago);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return selfPointer.getString(R.string.one_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + selfPointer.getString(R.string.x_hours_ago);
        } else if (diff < 48 * HOUR_MILLIS) {
            return selfPointer.getString(R.string.yesterday);
        } else {
            return diff / DAY_MILLIS + selfPointer.getString(R.string.x_days_ago);
        }
    }


    public static LocationListener locationListener;
    public static Location lastLocation;
    private static FusedLocationProviderClient loacationProvider;
    public static LocationRequest mLocationManager;
    private static final float LOCATION_REFRESH_DISTANCE = 100;
    private static final long LOCATION_REFRESH_TIME = 1000 * 60 *1;

    public static FirebaseRemoteConfig remoteConfig;

    public static ArrayList<Post> postsToShow;
    public static User user;
    public static FirebaseUser firebaseUser;

    public static OakappMain selfPointer;
    public static final int MAXRANKLEVEL = 30;
    public static final String NO_PICTURE = "no_pic";
    public static final String THIS_USER = "this";
    public static boolean UserAlreadyExist = false;


    public static ArrayList<Long> rankBorders;
    private static String TAG = "MAIN";
}
