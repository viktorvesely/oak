package oak.oakapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class openFeedback extends Menu {

    private DatabaseReference mFeedbackRef;
    private Feedback Fb;
    private Feedback Tel;
    private Feedback Mail;
    private String cast;
    private int current;
    private ArrayAdapter<String> komunikacia;
    private boolean initialDisplay = true;
    private ArrayList<FeedbackComment> mComments;
    private FeedbackCommentArrayAdapter adapter;
    private openFeedback selfPointer;

    private RatingBar mRB1;
    private RatingBar mRB2;
    private RatingBar mRB3;
    private Spinner mMediumSpinner;
    private ListView mCommentList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter menu;
    private Intent intent;
    private Button mContactButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_feedback);
        intent = getIntent();
        cast = intent.getStringExtra("cast");

        mRB1 = (RatingBar) findViewById(R.id.rb_t1);
        mRB2 = (RatingBar) findViewById(R.id.rb_t2);
        mRB3 = (RatingBar) findViewById(R.id.rb_t3);

        mComments = new ArrayList<FeedbackComment>();
        mCommentList = (ListView) findViewById(R.id.lv_com);

        mFeedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback").child(cast);
        mContactButton = (Button)findViewById(R.id.b_contact);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.lv_drawerlist);
        menu = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu));
        mDrawerList.setAdapter(menu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OnMenuItemSelected(position);
                mDrawerLayout.closeDrawers();

            }
        });

        mFeedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("0")) {
                    Fb = dataSnapshot.child("0").child("Ratings").getValue(Feedback.class);
                } else {
                    Fb = new Feedback(0,0,0,0);
                }
                if (dataSnapshot.hasChild("1")) {
                    Tel = dataSnapshot.child("1").child("Ratings").getValue(Feedback.class);
                } else {
                    Tel = new Feedback(0,0,0,0);
                }
                if (dataSnapshot.hasChild("2")) {
                    Mail = dataSnapshot.child("2").child("Ratings").getValue(Feedback.class);
                } else {
                    Mail = new Feedback(0,0,0,0);
                }

                int avg_fb = Fb.Avg();
                int avg_tel = Tel.Avg();
                int avg_mail = Mail.Avg();

                if ((avg_fb >= avg_mail) && (avg_fb >= avg_tel)) {
                    ChangeMedium(Fb, 0);
                } else if ((avg_tel >= avg_fb) && (avg_tel >= avg_mail)) {
                    ChangeMedium(Tel, 1);
                } else {
                    ChangeMedium(Mail, 2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mMediumSpinner = (Spinner) findViewById(R.id.sp_komunikacia);
        komunikacia = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.komunikacia));
        komunikacia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMediumSpinner.setAdapter(komunikacia);

        mMediumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {   if (initialDisplay == false) {
                if (pos==0) {
                    ChangeMedium(Fb, 0);
                } else if (pos==1) {
                    ChangeMedium(Tel, 1);
                } else {
                    ChangeMedium(Mail, 2);
                }
            } else {
                initialDisplay = false;
            }


            }
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
/*
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (current) {

                    case 0:

                        break;

                    case 1:
                        break;

                    case 2:
                        String TO = getResources().getString(R.array.mail);
                        Intent mail = new Intent(Intent.ACTION_SEND);

                        mail.setType("text/plain");
                        mail.putExtra(Intent.EXTRA_EMAIL, TO);
                        try {
                            startActivity(Intent.createChooser(mail, "Send mail..."));
                            finish();
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                }
                    default:
                        break;
            }
        });
*/
    };

        public void ChangeMedium (Feedback q, int i) {
            current = i;
            if (adapter != null) {
                adapter.clear();
            }
                if (q.mN>0) {
                    mRB1.setRating(q.mT_1/q.mN);
                    mRB2.setRating(q.mT_2/q.mN);
                    mRB3.setRating(q.mT_3/q.mN);
                } else {
                    mRB1.setRating(0);
                    mRB2.setRating(0);
                    mRB3.setRating(0);
                }
                mFeedbackRef.child(Integer.toString(mMediumSpinner.getSelectedItemPosition())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Comments")) {
                            for (DataSnapshot postSnapshot : dataSnapshot.child("Comments").getChildren()) {
                                mComments.add(postSnapshot.getValue(FeedbackComment.class));
                            }
                            adapter = new FeedbackCommentArrayAdapter(getApplicationContext(), mComments);
                            mCommentList.setAdapter(adapter);

                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    @Override
    public void onBackPressed(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }

}

