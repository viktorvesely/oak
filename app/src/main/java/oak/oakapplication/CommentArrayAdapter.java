package oak.oakapplication;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vikto on 8/31/2017.
 */

class CommentView{
    public TextView mOwner;
    public TextView mText;
    public TextView mTime;
    public TextView mUpvotes;
    public ImageView mPicture;
    public Button mPlus;
    public Button mMinus;
    public Button mEditComment;
    public int position;

    CommentView (View v, int position) {
        mEditComment = (Button) v.findViewById(R.id.b_editComment);
        mOwner = (TextView) v.findViewById(R.id.tv_commentOwner);
        mText = (TextView) v.findViewById(R.id.tv_commentText);
        mTime = (TextView) v.findViewById(R.id.tv_timeSince);
        mPicture = (ImageView) v.findViewById(R.id.iv_ownerPicture);
        mPlus = (Button) v.findViewById(R.id.b_plus);
        mMinus = (Button) v.findViewById(R.id.b_minus);
        mUpvotes = (TextView) v.findViewById(R.id.tv_commentReputation);
    }

}

public class CommentArrayAdapter extends ArrayAdapter<Comment> {

    private ArrayList<Comment> mComments;

    private static final int COMMENT = -1;

    public CommentArrayAdapter(Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
        mComments = comments;
    }

    @Override
    public View getView(int position, View viewConverter, final ViewGroup parent) {
        Comment comment = getItem(position);

        switch (getItemViewType(position)){
            case COMMENT:
                CommentView commentView = null;

                if (comment.mDirectmsg && comment.mDirectTarget.equals(OakappMain.user.mUniqueName) || comment.mComOwner.equals(OakappMain.user.mUniqueName))

                if (viewConverter == null) {
                    viewConverter = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
                    commentView = new CommentView(viewConverter, position);
                    viewConverter.setTag(commentView);

                }

                else commentView = (CommentView) viewConverter.getTag();

                if (OakappMain.firebaseUser.getUid().equals(comment.mComOwner)) {
                    commentView.mEditComment.setVisibility(View.VISIBLE);
                }
                else commentView.mEditComment.setVisibility(View.GONE);


                commentView.mMinus.setTag(commentView);
                commentView.mPlus.setTag(commentView);
                commentView.mMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentView cv = (CommentView) v.getTag();
                        Comment com = CommentArrayAdapter.this.getItem(cv.position) ;
                        com.mUpvotes -= 1;
                        OakappMain.SaveCommentByKey(com);
                    }
                });
                commentView.mPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentView cv = (CommentView) v.getTag();
                        Comment com = CommentArrayAdapter.this.getItem(cv.position) ;
                        com.mUpvotes += 1;
                        OakappMain.SaveCommentByKey(com);
                    }
                });


                final View view =  LayoutInflater.from(getContext()).inflate(R.layout.dialog_box_create_comment,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                commentView.mEditComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommentView cv = (CommentView) v.getTag();
                        Comment com = CommentArrayAdapter.this.getItem(cv.position) ;
                        Button cancel = (Button) view.findViewById(R.id.b_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.hide();
                            }
                        });
                        Button confirm = (Button) view.findViewById(R.id.b_change);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // checks and saving
                            }
                        });

                        dialog.show();
                    }
                });
                commentView.mUpvotes.setText(String.valueOf(comment.mUpvotes));
                commentView.mOwner.setText(OakappMain.firebaseUser.getDisplayName());
                commentView.mText.setText(comment.mComText);
                commentView.mTime.setText(OakappMain.getTimeAgo(comment.mTimestamp));
                break;
        }

        return viewConverter;
    }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public int getItemViewType(int position)
    {
        return COMMENT;
    }
}

