package oak.oakapplication;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import oak.oakapplication.Post;

/**
 * Created by viktor on 9.8.2017.
 */


class NoImgView
{
    NoImgView(View view) {
        this.mNoImgTitle = (TextView) view.findViewById(R.id.tv_noImgTitle);
        this.mNoImgText = (TextView) view.findViewById(R.id.tv_noImgText);
    }
    public TextView mNoImgTitle;
    public TextView mNoImgText;

}

class ImgView
{
    ImgView (View view) {
        this.mImage = (ImageView) view.findViewById(R.id.iv_postimg);
        this.mTitle = (TextView) view.findViewById(R.id.tv_titlePost);
        this.mText = (TextView) view.findViewById(R.id.tv_textPost);
    }

    public TextView mTitle;
    public TextView mText;
    public ImageView mImage;
}

public class PostArrayAdapter extends ArrayAdapter<Post> {
    public PostArrayAdapter (Context context, ArrayList<Post> posts) {
        super(context,0, posts);
        mPosts = posts;
    }

    private ArrayList<Post> mPosts;
    private static final int NO_IMG_VIEW = 0;
    private static final int IMG_VIEW = 1;



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);

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

