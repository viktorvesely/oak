package oak.oakapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Tips extends AppCompatActivity {

    private JsonTips mJsonTips;
    private String mJson;
    private HorizontalScrollView hsvTips;
    private TipsAdapter adapter;
    private RecyclerView rvTips;
    private CustomGridLayoutManager manager;

    private int mCurrentPosition;
    private GestureDetector mGestureDetector;
    private final float MINDELTATOSWIPE = 260;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        try {
            mJson = readFile("tipy.json");
        }
        catch (IOException e) {
            Log.e(TAG, "Error while reading asset file: " + e.getMessage());
        }

        mCurrentPosition = 0;

        mJsonTips = new Gson().fromJson(mJson, JsonTips.class);
        adapter = new TipsAdapter(mJsonTips.mTips, this);
        SpeedSwipe speedSwipe = new SpeedSwipe();
        mGestureDetector = new GestureDetector(this, speedSwipe);

        rvTips = (RecyclerView) findViewById(R.id.rv_tips);
        manager = new CustomGridLayoutManager(this);
        manager.setScrollEnabled(false);

        rvTips.setLayoutManager(manager);
        rvTips.setAdapter(adapter);

        rvTips.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });


    }


    public String readFile(String fileName) throws IOException
    {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName), "UTF-8"));

        String content = "";
        String line;
        while ((line = reader.readLine()) != null) {
            content = content + line;
        }

        return content;

    }

    private final String TAG = "TipsActivity";

    @Override
    protected void onStart() {
        super.onStart();
    }

    private class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipView> {
        private ArrayList<JsonTips.JsonTip> mTips;
        private int mLastPosition;
        private Context mContext;

        public TipsAdapter (ArrayList<JsonTips.JsonTip> tips, Context context) {
            mTips = tips;
            mLastPosition = -1;
            mContext = context;
        }


        public class TipView  extends RecyclerView.ViewHolder{
            public TextView mText;

            public  TipView(View v) {
                super(v);
                mText = (TextView) v.findViewById(R.id.tv_tipText);
            }
        }


        @Override
        public TipView onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tip_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            TipView tv = new TipView(v);



            return tv;
        }

        @Override
        public void onBindViewHolder(TipView holder, int position) {

            int currentPos = holder.getAdapterPosition();

            holder.mText.setText(mTips.get(position).mText);
            if (currentPos > mLastPosition) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.from_right_to_left);
                holder.itemView.startAnimation(animation);
                mLastPosition = currentPos;
            }
            else if (currentPos < mLastPosition) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.from_left_to_right);
                holder.itemView.startAnimation(animation);
                mLastPosition = currentPos;
            }

        }


        @Override
        public int getItemCount() {
            return mTips.size();
        }


    }


    private class SpeedSwipe implements GestureDetector.OnGestureListener {


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float delta = e1.getX() - e2.getX();
            if (Math.abs(delta) <  MINDELTATOSWIPE )
                return true;
            delta = delta / Math.abs(delta);
            mCurrentPosition += delta;
            manager.scrollToPosition(mCurrentPosition);
            return true;
        }
    }

    public class CustomGridLayoutManager extends LinearLayoutManager {
        private  boolean isScrollEnabled = true;

        public CustomGridLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }


}
