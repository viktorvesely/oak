package oak.oakapplication;

import java.util.Comparator;

/**
 * Created by vikto on 9/1/2017.
 */

public class CommentComparator implements Comparator<Comment>{
    @Override
    public int compare(Comment c1, Comment c2) {
        long sign = c2.mTimestamp - c1.mTimestamp;
        //sorting in ascending order
        return (int)(sign / Math.abs(sign));
    }


}
