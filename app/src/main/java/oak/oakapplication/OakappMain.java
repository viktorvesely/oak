package oak.oakapplication;

import android.app.Application;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.firebase.client.utilities.Base64;
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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

    public static boolean HasInternetAcces() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -W 1 -c 2 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(ipProcess.getInputStream()));

            String s;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
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
}
