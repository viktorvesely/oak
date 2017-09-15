package oak.oakapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vikto on 8/31/2017.
 */

class CommentView{
    public TextView mOwner;
    public TextView mText;
    public TextView mTime;
    public TextView mOwnerReputation; // this will change
    public TextView mUpvotes;
    public ImageView mPicture;
    public Button mPlus;
    public Button mMinus;

    CommentView (View v) {
        mOwner = (TextView) v.findViewById(R.id.tv_commentOwner);
        mText = (TextView) v.findViewById(R.id.tv_commentText);
        mTime = (TextView) v.findViewById(R.id.tv_timeSince);
        mOwnerReputation = (TextView) v.findViewById(R.id.tv_ownerReputation);
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
    public View getView(int position, View viewConverter, ViewGroup parent) {
        final Comment comment = getItem(position);

        switch (getItemViewType(position)){
            case COMMENT:
                CommentView commentView = null;

                if (viewConverter == null) {
                    viewConverter = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
                    commentView = new CommentView(viewConverter);


                }

                else commentView = (CommentView) viewConverter.getTag();


                commentView.mMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Comment com =  comment;
                        com.mUpvotes -= 1;
                        OakappMain.SaveCommentByKey(com);
                    }
                });
                commentView.mPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Comment com =  comment;
                        com.mUpvotes += 1;
                        OakappMain.SaveCommentByKey(com);
                    }
                });
                commentView.mUpvotes.setText(comment.mUpvotes);
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

