package oak.oakapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by rohal on 4.10.2017.
 */
public class FeedbackCommentArrayAdapter extends ArrayAdapter<FeedbackComment> {

    public FeedbackCommentArrayAdapter(Context context, ArrayList<FeedbackComment> comments) {
        super(context, 0, comments);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        FeedbackComment comment = getItem(position);

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.feedback_comment_item, parent, false);
        }

        TextView mOwner = (TextView) v.findViewById(R.id.tv_fb_owner);
        TextView mText = (TextView) v.findViewById(R.id.tv_fb_com_txt);
        TextView mTime = (TextView) v.findViewById(R.id.tv_fb_time);

        mText.setText(comment.mComText);
        mTime.setText(OakappMain.getTimeAgo(comment.mTimestamp));

        return v;
    }
}
