package oak.oakapplication;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

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

import java.io.IOException;
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


    static String  getRankFromLevel(int level)  { return selfPointer.getString(R.string.default_rank +  level); }

    static int getRankLevelFromRep(int reputation) {

        for (int i = 0; i < rankBorders.size(); ++i)
        {
            if (reputation < rankBorders.get(i))
                return i;
        }

        return -1;
    }


    public static void getUserByUid(String uid,final UserInterface ui)
    {
        Query filter =  FirebaseDatabase.getInstance().getReference().child("Users").orderByKey().equalTo(uid);


        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                ui.UserListener(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void getPostByKey(final String id, final PostListener listener)
    {
        Query filter = FirebaseDatabase.getInstance().getReference().child("Posts").orderByKey().equalTo(id);

        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post p = dataSnapshot.getValue(Post.class);
                listener.OnFinished(p);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
;    }

    public static void getCommentByKey(final String id, final CommentListener listener)
    {
        Query filter = FirebaseDatabase.getInstance().getReference().child("Comments").orderByKey().equalTo(id);

        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Comment c = dataSnapshot.getValue(Comment.class);
                listener.OnFinished(c);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public static boolean HasInternetAcces() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public static void SaveUserByUid(final User user) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(user.mId).setValue(user);
    }

    public static void SavePostByKey(final Post post) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(post.mKey).setValue(post);
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

    public static ArrayList<Long> rankBorders;
}
