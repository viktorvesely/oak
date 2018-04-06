package oak.oakapplication;

import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by Viktor on 1/22/2018.
 */

public class EventManager {

    private View mCreatedView;
    private Event mData;
    private boolean mCreated;

    EventManager(View view) {
        mCreatedView = view;
        mData = new Event();
        mCreated = true;
    }

    EventManager(String postID , final EventInterface eventInterface) {
        mCreated = false;
        mData = null;

        Query filter = FirebaseDatabase.getInstance().getReference().child("Events").orderByChild("mParent").equalTo(postID);
        filter.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mData = dataSnapshot.getValue(Event.class);
                eventInterface.OnEventLoad(true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Could not load event from database " + databaseError.getMessage());
                eventInterface.OnEventLoad(false);
            }
        });
    }

    int addParticipant(String displayName, boolean notifications, String id) {


        Participant participant = new Participant(mData.mParent, displayName, notifications, id);
        participant.save();
        mData.mNumOfParticipants++;
        save();
        return Respones.ADDED;
    }

    EventManager activate() {
        //TODO dialog box with all the info about event -> save
        save();
        return this;
    }

    boolean cancel() {

        return true;
    }

    boolean leave(String userID) {

        return true;
    }

    void save() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        eventsRef.child(mData.mParent).setValue(mData);
    }

    public class Event{

        Event() {
            mTimeOfEvent = -1;
            mTimeOfEvent = -1;
            mParent = "INIT";
            mName = "INIT";
            mDescription = "INIT";
            mHost = "INIT";
            mActive = true;
            mNumOfParticipants = 0;
        }

        public long mTimeStarted;
        public long mTimeOfEvent;
        public boolean mActive;
        public String mParent;
        public String mName;
        public String mDescription;
        public String mHost;
        public int mNumOfParticipants;

        private ArrayList<Participant> mParticipants;

        public void getParticipants(final ParticipantsInterface participantsInterface) {
            Query filter = FirebaseDatabase.getInstance().getReference().child("Participants").orderByChild(mParent).equalTo(this.mParent);
            mParticipants = new ArrayList<>();
            filter.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mParticipants.add(dataSnapshot.getValue(Participant.class));
                    if (mParticipants.size() == mNumOfParticipants) {
                        participantsInterface.onParticipantsLoad(mParticipants);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Error while loading participants " + databaseError.getMessage());
                }
            });
        }


    }

    public class Respones {
        public static final int ADDED = 1;
        public static final int FULL = 2;
        public static final int ERROR = 3;
        public static final int TOOLATE = 4;
    }

    public class Participant {
        Participant() {
            mParent = "INIT";
            mDisplayName = "INIT";
            mWillBenotified = true;
            mID = "INIT";
        }

        Participant(String post, String displayName, boolean willBeNotified, String id) {
            mParent = post;
            mDisplayName = displayName;
            mWillBenotified = willBeNotified;
            mID = id;
        }


        public String mParent;
        public String mDisplayName;
        public  boolean mWillBenotified;
        public String mID;

        void save() {
            FirebaseDatabase.getInstance().getReference().child("Participants").push().setValue(this);
        }

    }

    private static String TAG = "EventManager";


}
