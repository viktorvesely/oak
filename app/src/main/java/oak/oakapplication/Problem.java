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
    }

    Problem() {
        this.mWorking = new ArrayList<>();
        this.mStartedWorking = new ArrayList<>();
        this.mBanList = new ArrayList<>();
    }

    public boolean kickUser(String userID) {
        int index = mWorking.indexOf(userID);
        if (index == -1)
            return false;
        mBanList.add(userID);
        mWorking.remove(index);
        return true;
    }


    public int addWorker(String userID) {
        if (mWorking.size() < 6) {
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

            if (! userID.equals("INIT")) {
                OakappMain.getPostByKey(mParent, new PostListener() {
                    @Override
                    public void OnFinished(Post p) {
                        p.mIsWorkedOn = true;
                        OakappMain.SavePostByKey(p);
                    }
                });
            }
            return Responses.ADDED;
        }
        return Responses.FULL;

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
        FirebaseDatabase.getInstance().getReference().child("Problems").child(mParent).setValue(this);
    }

    public void load(final ProblemListener listener) {
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
    public boolean mActive;
    public List<String> mBanList;
    public List<String> mWorking;
    public List<Long> mStartedWorking;

    public class Responses {
        public static final int BANNED = 0;
        public static final int ALREADYIN = 1;
        public static final int FULL = 2;
        public static final int ADDED = 3;
    }

}
