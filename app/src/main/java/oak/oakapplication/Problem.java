package oak.oakapplication;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viktor on 12/17/2017.
 */

public class Problem {
    Problem(String post) {
        this.mParent = post;
        this.mWorking = new ArrayList<>();
        this.mStartedWorking = new ArrayList<>();
        this.mBanList = new ArrayList<>();
        this.mName = "INIT";
        this.mJoinable = true;
        this.mOwner = "INIT";
        this.mActive = true;
        this.mDisplayNames = new ArrayList<>();
    }

    Problem() {
        this.mWorking = new ArrayList<>();
        this.mStartedWorking = new ArrayList<>();
        this.mBanList = new ArrayList<>();
        this.mDisplayNames = new ArrayList<>();
        this.mName = "INIT";
        this.mJoinable = true;
        this.mOwner = "INIT";
        this.mParent = "INIT";
        this.mActive = true;
    }

    public boolean kickUser(String userID) {
        int index = mWorking.indexOf(userID);
        if (index == -1)
            return false;
        mBanList.add(userID);
        mWorking.remove(index);
        mDisplayNames.remove(index);
        return true;
    }



    public List<String> getParticipantsIDs() {
        if (mWorking.size() <= 1) {return null;}
        return mWorking.subList(1, mWorking.size() -1);
    }

    public String getParticipantName(int index) {return mDisplayNames.get(index);}

    public List<String> getParticipantNames(){
        if (mDisplayNames.size() <= 1) {return null;}
        return mDisplayNames.subList(1, mDisplayNames.size() -1);
    }

    public void setName(String name) { mName = name; }

    public String getName () { return mName; }

    public void setJoinAbilitie(boolean status) {mJoinable = status;}
    public boolean canJoin() {
        return mWorking.size() == 0 || mWorking.get(mWorking.size() -1).equals("INIT") || mWorking.size() < 6 || mJoinable;
    }


    public int addWorker(String userID, String displayName) {
        if (canJoin()) {
            mStartedWorking.add(System.currentTimeMillis());

            if (mWorking.contains(userID)) {
                return Responses.ALREADYIN;
            }

            if (mWorking.size() == 1) {
                mOwner = userID;
            }
            else if (mBanList.contains(userID)) {
                return Responses.BANNED;
            }
            mWorking.add(userID);
            mDisplayNames.add(displayName);
            if (! userID.equals("INIT")) {
                OakappMain.getPostByKey(mParent, new PostListener() {
                    @Override
                    public void OnFinished(Post p) {
                        p.mIsWorkedOn = true;
                        OakappMain.SavePostByKey(p);
                    }
                });
            }
            if (mOwner.equals(userID)) {
                return Responses.OWNER;
            }
            else {
                return Responses.ADDED;
            }
        }
        return Responses.FULL;

    }

    public boolean removeLast() {
        if (mWorking.get(mWorking.size() -1).equals("INIT")) { return false;}
        mWorking.remove(mWorking.size() -1);
        return true;
    }

    public Long getStartedTime(String userID) {
        return  mStartedWorking.get(mWorking.indexOf(userID));
    }

    public int numOfWorkers() {
        return mWorking.size() - 1;
    }

    public void solved() {
        mActive = false;
        save();
    }



    public void save() {
        if (mParent == null || mParent.isEmpty()) { return; }
        FirebaseDatabase.getInstance().getReference().child("Problems").child(mParent).setValue(this);
    }

    public void load(final ProblemListener listener) {
        if (mParent == null || mParent.isEmpty()) { return; }
        Query filter =  FirebaseDatabase.getInstance().getReference().child("Problems").orderByChild("mParent").equalTo(mParent);

        filter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Problem p = dataSnapshot.child(mParent).getValue(Problem.class);
                listener.problem(p);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PROBLEMCLASS", "Error while loading problem" + databaseError.getMessage());
            }
        });
    }

    public String mParent;
    public String mOwner;
    public String mName;
    public boolean mActive;
    public List<String> mBanList;
    public List<String> mWorking;
    public List<Long> mStartedWorking;
    public List<String> mDisplayNames;
    public boolean mJoinable;

    public class Responses {
        public static final int BANNED = 0;
        public static final int ALREADYIN = 1;
        public static final int FULL = 2;
        public static final int ADDED = 3;
        public static final int OWNER = 4;
    }

}
