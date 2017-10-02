package oak.oakapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class openFeedback extends AppCompatActivity {

    private static final String TAG = "bicykel";
    private DatabaseReference mFB;
    private DatabaseReference mTel;
    private DatabaseReference mFeedbackRef;
    private Feedback Fb;
    private Feedback Tel;
    private Feedback Mail;
    private String cast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_feedback);
        Intent intent = getIntent();
        cast = intent.getStringExtra("cast");
        Log.d(TAG, cast);

        mFeedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback").child(cast);
        mFB = mFeedbackRef.child("Facebook");

        mFB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Fb = dataSnapshot.getValue(Feedback.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
/*
        mTel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tel = dataSnapshot.getValue(Feedback.class);
                Log.d(TAG, getString(Tel.mType1_x));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        mMail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Mail = dataSnapshot.getValue(Feedback.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/
    }

}
