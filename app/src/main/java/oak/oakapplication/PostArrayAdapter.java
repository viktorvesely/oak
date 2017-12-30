package oak.oakapplication;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.zip.Inflater;

import oak.oakapplication.Post;

/**
 * Created by viktor on 9.8.2017.
 */


class NoImgView
{
    NoImgView(View view) {
        this.mNoImgTitle = (TextView) view.findViewById(R.id.tv_noImgTitle);
        this.mNoImgText = (TextView) view.findViewById(R.id.tv_noImgText);
        this.mWorkedOn = view.findViewById(R.id.rb_checkMark);
    }
    public TextView mNoImgTitle;
    public TextView mNoImgText;
    public RadioButton mWorkedOn;

}

class ImgView
{
    ImgView (View view) {
        this.mImage = (ImageView) view.findViewById(R.id.iv_postimg);
        this.mTitle = (TextView) view.findViewById(R.id.tv_titlePost);
        this.mText = (TextView) view.findViewById(R.id.tv_textPost);
        this.mWorkedOn = view.findViewById(R.id.rb_checkMark);
    }

    public TextView mTitle;
    public TextView mText;
    public ImageView mImage;
    public RadioButton mWorkedOn;
}

public class PostArrayAdapter extends ArrayAdapter<Post> {
    public PostArrayAdapter (Context context, ArrayList<Post> posts, int filter) {
        super(context,0, posts);
        mPosts = posts;
        mFilter = filter;
    }

    private int mFilter;
    private ArrayList<Post> mPosts;
    private static final int NO_IMG_VIEW = 0;
    private static final int IMG_VIEW = 1;



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);
        if (post == null || mFilter == post.mCategory) {
            return LayoutInflater.from(getContext()).inflate(R.layout.empty, parent, false);
        }

        int viewType = getItemViewType(position);

        switch (viewType)
        {
            case IMG_VIEW:{
                ImgView v = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_item, parent, false);
                    v = new ImgView(convertView);
                    convertView.setTag(v);
                }
                else v = (ImgView)convertView.getTag();

                v.mTitle.setText(post.mTitle);
                v.mText.setText(post.mText);

                if (post.mImgUrl2.isEmpty())
                    Glide.with(v.mImage.getContext()).load(post.mImgUrl1).into(v.mImage);

                else  Glide.with(v.mImage.getContext()).load(post.mImgUrl2).into(v.mImage);

                if (post.mCategory == 0 && post.mIsWorkedOn) {
                    //convertView.setBackgroundColor(Color.parseColor("#2c65c1"));
                    v.mWorkedOn.setChecked(true);
                    v.mWorkedOn.setText(R.string.is_worked_on);
                }
                else if (post.mCategory == 0) {
                    //convertView.setBackgroundColor(Color.parseColor("#0f9b18"));
                    v.mWorkedOn.setText(R.string.free_problem);
                }
                v.mWorkedOn.setFocusable(false);

                break;
            }

            case NO_IMG_VIEW:{
                NoImgView nv = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_item_no_img,parent, false);
                    nv = new NoImgView(convertView);
                    convertView.setTag(nv);
                }
                else nv = (NoImgView) convertView.getTag();

                nv.mNoImgTitle.setText(post.mTitle);
                nv.mNoImgText.setText(post.mText);
                if (post.mCategory == 0 && post.mIsWorkedOn) {
                    //convertView.setBackgroundColor(Color.parseColor("#2c65c1"));
                    nv.mWorkedOn.setChecked(true);
                    nv.mWorkedOn.setText(R.string.is_worked_on);
                }
                else if (post.mCategory == 0) {
                    //convertView.setBackgroundColor(Color.parseColor("#0f9b18"));
                    nv.mWorkedOn.setText(R.string.free_problem);
                }
                nv.mWorkedOn.setFocusable(false);
                break;
            }


        }


        return convertView;
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public int getItemViewType(int position)
    {
        Post p = mPosts.get(position);
        if (p.mImgUrl2.isEmpty() && p.mImgUrl1.isEmpty())
            return NO_IMG_VIEW;
        else return IMG_VIEW;
    }



}

