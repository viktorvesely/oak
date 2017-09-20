package oak.oakapplication;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class openFeedback extends AppCompatActivity {

    public Feedback Fb;
    public Feedback Tel;
    public Feedback Mail;
    private DatabaseReference mFb;
    private DatabaseReference mTel;
    private DatabaseReference mMail;
    private String cast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_feedback);
        Intent intent = getIntent();
        cast = intent.getStringExtra("cast");

        mFb = FirebaseDatabase.getInstance().getReference().child(cast).child("Facebook");
        mTel = FirebaseDatabase.getInstance().getReference().child(cast).child("Tel");
        mMail = FirebaseDatabase.getInstance().getReference().child(cast).child("Mail");

        mFb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Fb = dataSnapshot.getValue(Feedback.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mTel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tel = dataSnapshot.getValue(Feedback.class);
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

    }

}
