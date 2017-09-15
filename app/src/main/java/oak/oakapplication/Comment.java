package oak.oakapplication;

/**
 * Created by matus on 3.8.2017.
 */

public class Comment {

    public Comment() { }

    public Comment (String comText, String comOwner, String motherPost, boolean direct_msg, String target){
        mComText = comText;
        mComOwner = comOwner;
        mMotherPost = motherPost;
        mDirectmsg = direct_msg;
        mDirectTarget = target;

        mActive = true;
        mTimestamp = System.currentTimeMillis();
        mUpvotes = 0;
    }

    //user set
    public String mComText;
    public String mComOwner;
    public String mMotherPost;
    public boolean mDirectmsg;
    public String mDirectTarget;

    //generated
    public String mKey;
    public long mTimestamp;
    public int mUpvotes;
    public boolean mActive;

}
