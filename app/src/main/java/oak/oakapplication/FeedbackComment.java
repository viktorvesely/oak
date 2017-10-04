package oak.oakapplication;

/**
 * Created by rohal on 3.10.2017.
 */

public class FeedbackComment {

    public FeedbackComment() { }

    public FeedbackComment (String comText, String comOwner){
        this.mComText = comText;
        this.mComOwner = comOwner;

        this.mActive = true;
        this.mTimestamp = System.currentTimeMillis();
    }

   public String mComText;
   public String mComOwner;

    public String mKey;
    public long mTimestamp;
    public boolean mActive;

}