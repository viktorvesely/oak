package oak.oakapplication;

/**
 * Created by matis on 17.09.2017.
 */

public class Feedback {

    public Feedback() {}

    public Feedback(int t1_x, int t1_n, int t2_x, int t2_n, int t3_x, int t3_n){
        this.mType1_n = t1_n;
        this.mType1_x = t1_x;
        this.mType2_n = t2_n;
        this.mType2_x = t2_x;
        this.mType3_n = t3_n;
        this.mType3_x = t3_x;

        if ((t1_n+t2_n+t3_n)>0) {
            mAvg = ((t1_x+t2_x+t3_x)/(t1_n+t2_n+t3_n));
        }
    }

    public int mType1_n;
    public int mType1_x;
    public int mType2_n;
    public int mType2_x;
    public int mType3_n;
    public int mType3_x;
    public int mAvg;

}
