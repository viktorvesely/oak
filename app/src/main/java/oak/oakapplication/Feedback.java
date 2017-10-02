package oak.oakapplication;

/**
 * Created by matis on 17.09.2017.
 */

public class Feedback {

    public Feedback() {}

    public Feedback(int a, int b, int c, int d, int e, int f){
        this.mType1_n = a;
        this.mType1_x = b;
        this.mType2_n = c;
        this.mType2_x = d;
        this.mType3_n = e;
        this.mType3_x = f;

        if ((a+c+e)>0) {
            this.mAvg = ((b+d+f)/(a+c+e));
        }else{
            this.mAvg = 0;
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
