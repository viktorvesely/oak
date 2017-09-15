package oak.oakapplication;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class openPost extends AppCompatActivity {

    private Post mPost;
    private User mOwner;
    private ArrayList<Comment> mComments;
    private ArrayAdapter<Comment> adapter;
    private boolean isThisDirectMessage;

    private static final int MINPLUSESTOBETOP = 10;

    private OakappMain main;

    private ListView listView_comments;
    private EditText mCommentAction;
    private  EditText target;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_post);


        mCommentAction = (EditText) findViewById(R.id.et_comment);
        adapter = new CommentArrayAdapter(this,mComments);
        main = (OakappMain) getApplicationContext();
        mComments = null;
        listView_comments = (ListView) findViewById(R.id.lv_commentsInPost);
        mPost = OakappMain.postsToShow.get(getIntent().getIntExtra("id", 0));
        OakappMain.getUserByUid(mPost.mOwner, new UserInterface() {
            @Override
            public void UserListener(User u) {
                mOwner = u;
                //also draw him
            }
        });

        mCommentAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(openPost.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_box_create_comment,null);
                EditText text = (EditText) view.findViewById(R.id.et_commentTextDialog);
                CheckBox directMsg = (CheckBox) view.findViewById(R.id.cb_directComment);
                target = (EditText) view.findViewById(R.id.et_targetName);
                directMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            target.setVisibility(View.VISIBLE);
                            isThisDirectMessage = true;
                        }
                        else {
                            target.setVisibility(View.INVISIBLE);
                            isThisDirectMessage = false;
                        }
                    }
                });

                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Query filter =  FirebaseDatabase.getInstance().getReference().child("Comments").orderByChild("mMotherPost").equalTo(mPost.mKey);
        filter.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment c =  dataSnapshot.getValue(Comment.class);
                adapter.add(c);
                if (mComments.size() == mPost.comments.size()) {
                    //mComments.sort(new CommentComparator()); also find out why the fuck it wont let me to

                    listView_comments.setAdapter(adapter);
                    //int specialID = FindSpecialComment(); maybe in next update (returns -1 if there is no special comment)
                }
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

            }
        });

    }


    private int FindSpecialComment() {
        int min = -1;
        int index = -1;

        for (int i = 0; i < mComments.size(); ++i) {
            if (mComments.get(i).mUpvotes > min) {
                min = mComments.get(i).mUpvotes;
                index = i;
            }
        }

        if (index == -1 || min < MINPLUSESTOBETOP || mComments.size() > 10)
            return -1;
        return index;
    }

}
