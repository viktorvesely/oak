package oak.oakapplication;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viktor on 8.8.2017.
 */

public class User {

    User () {

    }

    public String mUsername;
    public int mReputation;
    public long mJudgePower;
    public boolean mActive;
    public boolean mAdmin;
    public List<String> mOwnPosts;
    public List<String> mOwnComments;
    public List<String>mFavoritePosts;
    public String mId;
    public long lastLogged;



    //generated;
    private String mRank;
    private int mLevel;



    public void SetLevel(int level) {mLevel = level;}
    public int Level() {return mLevel;}
    public void SetRank (String rank) {mRank = rank;}
    public String Rank() {return mRank;}

    public boolean ActivateUser (String username, int reputation, boolean isActive, boolean isAdmin, String id)
    {
        mActive = isActive;
        if (mActive == false)
            return false;
        mUsername = username;
        mReputation = reputation;
        mLevel = OakappMain.getRankLevelFromRep(mReputation);
        mRank = OakappMain.getRankFromLevel(mLevel);
        mAdmin = isAdmin;
        mOwnPosts.add("INIT");
        mFavoritePosts.add("INIT");
        mId = id;
        mJudgePower = OakappMain.remoteConfig.getLong("judgepower_default_value");


        return true;
    }

    public void AddReputation(int repBoost) {
        mReputation += repBoost;
        mLevel = OakappMain.getRankLevelFromRep(mReputation);
        mRank = OakappMain.getRankFromLevel(mLevel);
    }

}
