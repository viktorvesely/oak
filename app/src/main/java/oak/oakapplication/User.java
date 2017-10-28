package oak.oakapplication;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
        mOwnPosts = new ArrayList<String>();
        mOwnComments = new ArrayList<String>();
        mFavoritePosts = new ArrayList<String>();
        mRatedComments = new ArrayList<String>();
        mLikedPosts = new ArrayList<String>();
        mDislikedComments = new ArrayList<String>();
        mUniqueName = "NONE";
        mUsername = "NONE";
        mReputation = 0;
        mJudgePower = 0;
        mActive = true;
        mAdmin = false;
        mPhoto = "NONE";
        mId = "NONE";
        mFCMToken = "NONE";
        lastLogged = 0;
    }

    public String mUsername;
    public String mUniqueName;
    public String mFCMToken;
    public int mReputation;
    public long mJudgePower;
    public boolean mActive;
    public boolean mAdmin;
    public String mPhoto;
    public List<String> mOwnPosts;
    public List<String> mOwnComments;
    public List<String>mFavoritePosts;
    public List<String>mLikedPosts;
    public List<String>mRatedComments;
    public List<String>mDislikedComments;
    public String mId;
    public long lastLogged;

    public int Level() {return OakappMain.getRankLevelFromRep(mReputation);}
    public String Rank() {return OakappMain.getRankFromLevel(OakappMain.getRankLevelFromRep(mReputation));}


    public boolean ActivateUser (String username, int reputation, boolean isActive, boolean isAdmin, String id)
    {
        mActive = isActive;
        if (mActive == false)
            return false;

        mUsername = username;
        mReputation = reputation;
        mAdmin = isAdmin;
        mOwnPosts.add("INIT");
        mFavoritePosts.add("INIT");
        mOwnComments.add("INIT");
        mRatedComments.add("INIT");
        mLikedPosts.add("INIT");
        mDislikedComments.add("INIT");
        mId = id;
        mJudgePower = 10;
        lastLogged = System.currentTimeMillis();

        return true;
    }

    public void AddReputation(int repBoost) {
        mReputation += repBoost;
    }

}
