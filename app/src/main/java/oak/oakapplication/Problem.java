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

    }

    public boolean addWorker(String userID) {
        if (mWorking.size() < 6) {
            mStartedWorking.add(System.currentTimeMillis());
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
            return  true;
        }
        return false;

    }

    public Long getStartedTime(String userID) {
        return  mStartedWorking.get(mWorking.indexOf(userID));
    }

    public boolean workingOn() {
        if (mWorking.size() > 1) {
            return true;
        }
        return false;
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
    public List<String> mWorking;
    public List<Long> mStartedWorking;
}
