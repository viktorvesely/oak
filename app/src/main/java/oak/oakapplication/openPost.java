package oak.oakapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;

public class openPost extends AppCompatActivity {

    private Post mPost;
    private User mOwner;
    private ArrayList<Comment> mComments;
    private ArrayAdapter<Comment> adapter;
    private boolean isThisDirectMessage;

    private static final int MINPLUSESTOBETOP = 10;
    private static  final int MAXWORKERS = 5;


    private ListView listView_comments;
    private TextView mText;
    private TextView mTitle;
    private ImageView mImg1;
    private ImageView mImg2;
    private EditText mCommentAction;
    private HorizontalScrollView mHSV_Images;
    private Button mJoin;
    private  EditText target;
    private Button mEditPost;
    private ImageView mOwnerPicture;
    private TextView mOwnerName;
    private TextView mParticipants;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_post);

        mComments = new ArrayList<Comment>();
        listView_comments = (ListView) findViewById(R.id.lv_commentsInPost);
        mCommentAction = (EditText) findViewById(R.id.et_comment);
        mText = (TextView) findViewById(R.id.tv_openPostText);
        mTitle = (TextView) findViewById(R.id.tv_openPostTitle);
        mImg1 = (ImageView) findViewById(R.id.iv_openPostImg1);
        mImg2 = (ImageView) findViewById(R.id.iv_openPostImg2);
        mHSV_Images = (HorizontalScrollView) findViewById(R.id.hsv_postImages);
        mEditPost = (Button) findViewById(R.id.b_editPost);
        mOwnerName = (TextView) findViewById(R.id.tv_postOwnerName);
        mJoin = findViewById(R.id.b_join);
        mParticipants = findViewById(R.id.tv_participants);
        mOwnerPicture = (ImageView) findViewById(R.id.iv_postOwner);

        adapter = new CommentArrayAdapter(this,mComments);
        listView_comments.setAdapter(adapter);
        mPost = new Gson().fromJson(getIntent().getStringExtra("post"), Post.class);
        final Problem  mProblem = new Problem(mPost.mKey);
        OakappMain.getUserByUid(mPost.mOwner, new UserInterface() {
            @Override
            public void UserListener(User u) {
                mOwner = u;
                if (mOwner.mUniqueName.equals(OakappMain.user.mUniqueName)) {
                    mEditPost.setVisibility(View.VISIBLE);
                    mEditPost.setEnabled(true);
                }
                mOwnerName.setText(mOwner.mUsername);
                Glide.with(mOwnerPicture.getContext()).load(mOwner.mPhoto).into(mOwnerPicture);
            }
        });

        mProblem.load(new ProblemListener() {
            @Override
            public void problem(final Problem p) {
                int workers = p.numOfWorkers();
                if (workers == 0) { mJoin.setText(getString(R.string.b_start_solving));}
                else if (workers < MAXWORKERS) { mJoin.setText(getString(R.string.b_join_problem)); mParticipants.setText(getString(R.string.tv_participants_num_of_workers) + " " + String.valueOf(workers) + " ľudí.");}
                else {
                    mJoin.setVisibility(View.GONE);
                    mParticipants.setText(getString(R.string.b_problem_full));
                }

                mJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (! p.canJoin()) {
                            Snackbar.make(view,R.string.problem_is_not_joinable, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            return;
                        }
                        if (OakappMain.user.mActiveProblems.size() > OakappMain.MAXPROBLEMSIZE)  {
                            Snackbar.make(view,R.string.max_problem_reached, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }


                        switch (p.addWorker(OakappMain.user.mId, OakappMain.user.mUniqueName)) {
                            case Problem.Responses.BANNED:
                                AlertDialog.Builder builder = new AlertDialog.Builder(openPost.this);
                                View v = getLayoutInflater().inflate(R.layout.dialog_box_kicked, null);
                                builder.setView(v);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                break;
                            case Problem.Responses.ADDED:
                                mPost.mIsWorkedOn = true;
                                OakappMain.SavePostByKey(mPost);
                                OakappMain.user.mActiveProblems.add(mPost.mKey);
                                Snackbar.make(view,R.string.added_to_your_problems, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;
                            case Problem.Responses.ALREADYIN:
                                Snackbar.make(view,R.string.problem_already_in, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;
                            case Problem.Responses.FULL:
                                Snackbar.make(view,R.string.problem_full, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                break;
                            case Problem.Responses.OWNER:
                                createProblem(p);
                                break;
                        }
                        p.save();
                    }
                });
            }
        });

        mEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        mEditPost.setVisibility(View.INVISIBLE);
        mEditPost.setEnabled(false);


        if (mPost.mImgUrl2.isEmpty()) {mImg2.setVisibility(View.GONE);}
        else {
            Glide.with(mImg2.getContext()).load(mPost.mImgUrl2).into(mImg2);
        }
        if (mPost.mImgUrl1.isEmpty()) {mImg1.setVisibility(View.GONE);}
        else {
            Glide.with(mImg1.getContext()).load(mPost.mImgUrl1).into(mImg1);
        }
        if (mPost.mImgUrl2.isEmpty() && mPost.mImgUrl1.isEmpty()) {mHSV_Images.setVisibility(View.GONE);}

        mText.setText(mPost.mText);
        mTitle.setText(mPost.mTitle);


        mCommentAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(openPost.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_box_create_comment,null);
                final EditText text = (EditText) view.findViewById(R.id.et_commentTextDialog);
                Button createComment = (Button) view.findViewById(R.id.b_createComment);
                final CheckBox directMsg = (CheckBox) view.findViewById(R.id.cb_directComment);
                target = (EditText) view.findViewById(R.id.et_targetName);
                directMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            target.setVisibility(View.VISIBLE);
                            isThisDirectMessage = true;
                        }
                        else {
                            target.setVisibility(View.GONE);
                            isThisDirectMessage = false;
                        }
                    }
                });

                builder.setView(view);
                final AlertDialog dialog = builder.create();

                createComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (text.getText().toString().isEmpty())
                            return;
                        if (isThisDirectMessage && target.getText().toString().isEmpty())
                            return;
                        String sTarget = "";
                        if (! isThisDirectMessage)
                            sTarget = "NONE";
                        else sTarget = target.getText().toString();
                        Comment c = new Comment(text.getText().toString(),mOwner.mUniqueName, mPost.mKey ,isThisDirectMessage, sTarget);
                        c.mKey = FirebaseDatabase.getInstance().getReference().child("Comments").push().getKey();
                        OakappMain.SaveCommentByKey(c);
                        OakappMain.user.mOwnComments.add(c.mKey);
                        OakappMain.SaveUserByUid(OakappMain.user);
                        mPost.comments.add(c.mKey);
                        mPost.mLastActivity = System.currentTimeMillis();
                        OakappMain.SavePostByKey(mPost);
                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });

        Query filter =  FirebaseDatabase.getInstance().getReference().child("Comments").orderByChild("mMotherPost").equalTo(mPost.mKey);
        filter.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment c =  dataSnapshot.getValue(Comment.class);
                if (! c.mActive)
                    return;
                adapter.add(c);
                if (mComments.size() == mPost.comments.size()) {
                    //mComments.sort(new CommentComparator()); also find out why the fuck it wont let me to

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

    private void createProblem(final Problem p) {
        AlertDialog.Builder problemBuilder = new AlertDialog.Builder(openPost.this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_box_create_problem, null);
        problemBuilder.setView(dialogView);
        final Switch joinAble = dialogView.findViewById(R.id.s_joinable);
        final EditText et_name = dialogView.findViewById(R.id.et_problem_name);
        Button create = dialogView.findViewById(R.id.b_create_problem);
        final AlertDialog dialog = problemBuilder.create();

        joinAble.toggle();


        dialog.show();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_name.getText().toString();
                if (name.isEmpty()) {
                    Snackbar.make( dialogView, R.string.db_create_problem_snackbar_invalid_name, Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    return;
                }
                p.setJoinAbilitie(joinAble.isChecked());
                p.setName(name);
                p.save();
                dialog.dismiss();
            }
        });


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                p.removeLast();
                p.save();
                mPost.mIsWorkedOn = false;
                OakappMain.SavePostByKey(mPost);
            }
        });


    }


    private final String TAG = "OpenPost";

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
