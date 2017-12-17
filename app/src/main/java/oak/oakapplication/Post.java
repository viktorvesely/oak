package oak.oakapplication;

import android.nfc.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viktor on 31.7.2017.
 */

public class Post {

    public Post() {
        this.comments = new ArrayList<String>();
        this.comments.add("INIT");
    }

    public Post (String postText, String title ,String ownerOfPost, String ImgURL, String ImgURL2,
                 String Tags, long Category, double Latitude, double Longitude, boolean wasCreated) {
        this.comments = new ArrayList<String>();
        this.comments.add("INIT");
        this.mText = postText;
        this.mTitle = title;
        this.mOwner = ownerOfPost;
        this.mImgUrl1 = ImgURL;
        this.mImgUrl2 = ImgURL2;
        this.mTags = Tags;
        this.mCategory = Category;
        this.mLatitude = Latitude;
        this.mLongitude = Longitude;

        this.mReputation = (int)(OakappMain.user.mReputation / 100 * headtstartPercentage);
        this.mTimestamp = System.currentTimeMillis();
        this.mLastActivity = System.currentTimeMillis();
        //init comments ?
        this.mActive = true;
        this.mNotificationKey = "NONE";
    }




    //user set
    public String mText;
    public String mTitle;
    public String mOwner;
    public String mImgUrl1;
    public String mImgUrl2;
    public String mTags;
    public long mCategory;
    public double mLatitude;
    public double mLongitude;
    public String mKey;
    //generated
    public int mReputation;
    public String mNotificationKey;
    public long mTimestamp;
    public long mLastActivity;
    public boolean mActive;

    //may change in future
    public List<String> comments;


    public int numberOfImgs() {
        int count = 0;
        if (! mImgUrl1.isEmpty()) { count++;}

        if (! mImgUrl2.isEmpty()) {count ++;}

        return count;
    }

    //static
    private static final int defaultRep = 0;
    private static final int headtstartPercentage = 5;
}
