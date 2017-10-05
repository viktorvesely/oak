package oak.oakapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class openFeedback extends AppCompatActivity {

    private DatabaseReference mFeedbackRef;
    private Feedback Fb;
    private Feedback Tel;
    private Feedback Mail;
    private String cast;
    private ArrayAdapter<String> komunikacia;
    private boolean initialDisplay = true;

    private RatingBar mRB1;
    private RatingBar mRB2;
    private RatingBar mRB3;
    private Spinner mMediumSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_feedback);
        Intent intent = getIntent();
        cast = intent.getStringExtra("cast");

        mRB1 = (RatingBar) findViewById(R.id.rb_t1);
        mRB2 = (RatingBar) findViewById(R.id.rb_t2);
        mRB3 = (RatingBar) findViewById(R.id.rb_t3);

        mMediumSpinner = (Spinner) findViewById(R.id.sp_komunikacia);
        komunikacia = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.komunikacia));
        komunikacia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMediumSpinner.setAdapter(komunikacia);

        System.out.println(cast);
        mFeedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback").child(cast);

        mFeedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Facebook")) {
                    Fb = dataSnapshot.child("Facebook").child("Ratings").getValue(Feedback.class);
                    int avg_fb = (Fb.mT_1 + Fb.mT_2 + Fb.mT_3) / (Fb.mN * 15);
                } else {
                    Fb = new Feedback(0,0,0,0);
                }
                if (dataSnapshot.hasChild("Telefon")) {
                    Tel = dataSnapshot.child("Facebook").child("Ratings").getValue(Feedback.class);
                } else {
                    Tel = new Feedback(0,0,0,0);
                }
                if (dataSnapshot.hasChild("Mail")) {
                    Mail = dataSnapshot.child("Facebook").child("Ratings").getValue(Feedback.class);
                } else {
                    Mail = new Feedback(0,0,0,0);
                }

                int avg_fb = Fb.Avg();
                int avg_tel = Tel.Avg();
                int avg_mail = Mail.Avg();

                if ((avg_fb >= avg_mail) && (avg_fb >= avg_tel)) {
                    ChangeMedium(Fb);
                } else if ((avg_mail >= avg_fb) && (avg_mail >= avg_tel)) {
                    ChangeMedium(Mail);
                } else {
                    ChangeMedium(Tel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mMediumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                if (initialDisplay==false) {
                    if (pos==0) {
                        ChangeMedium(Fb);
                    } else if (pos==1) {
                        ChangeMedium(Tel);
                    } else {
                        ChangeMedium(Mail);
                    }
                } else {
                    initialDisplay = false;
                }
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    };
        public void ChangeMedium (Feedback q) {
            if (q.mN>0) {
                mRB1.setRating(q.mT_1/q.mN);
                mRB2.setRating(q.mT_2/q.mN);
                mRB3.setRating(q.mT_3/q.mN);
            } else {
                mRB1.setRating(0);
                mRB2.setRating(0);
                mRB3.setRating(0);
            }

    }

}

