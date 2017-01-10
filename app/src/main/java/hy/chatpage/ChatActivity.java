package hy.chatpage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RelativeLayout activityChatRL;
    /*模仿发送btn移动的view*/
    private Button btnToMoved;
    private ListView chatLV;
    private LinearLayout bottomLayout;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<String> chatList = new ArrayList<>();
    private int n = 0;
    /*handler模拟加载进度*/
    private Handler handler;
    private View footerView;
    private boolean isSendBtnGone = false;
    /*标记是否是用户使listview滚动*/
    private boolean isUserScrolling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 100:
                        chatList.add("loading");
                        adapter.notifyDataSetChanged();
                        chatLV.smoothScrollToPosition(adapter.getCount());
                        /*程序自动滚动时标记为false*/
                        isUserScrolling = false;
                        handler.sendEmptyMessageDelayed(200, 1000);
                        break;
                    case 200:
                        /*可以使用以下两句中的任意一句代码实时控制footerview的高度*/
//                        chatLV.getChildAt(adapter.getCount()).getLayoutParams().height = 200;
//                        footerView.getLayoutParams().height = 200;
                        chatList.remove(chatList.size() - 1);
                        chatList.add("聊天聊天" + n++);
                        adapter.notifyDataSetChanged();
                        chatLV.smoothScrollToPosition(adapter.getCount());
                        /*程序自动滚动时标记为false*/
                        isUserScrolling = false;
                        break;
                }
            }
        };

        setContentView(R.layout.activity_chat);
        activityChatRL = (RelativeLayout)findViewById(R.id.activity_chat);
        btnToMoved = (Button)findViewById(R.id.btn_to_moved);
        chatLV = (ListView) findViewById(R.id.chat_list);
        bottomLayout = (LinearLayout)findViewById(R.id.bottom_layout);
        sendBtn = (Button) findViewById(R.id.send_btn);
        adapter = new ChatAdapter(this, chatList);
        /*给listview设置footerview，使底部留有部分空白防止底部对话选项遮挡聊天列表*/
        footerView = new View(this);
        footerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,150,this.getResources().getDisplayMetrics())));
        chatLV.addFooterView(footerView);
        chatLV.setOnScrollListener(scrollListener);
        chatLV.setAdapter(adapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              showSendBtn(false);
                playMoveBtn();
            }
        });

        handler.sendEmptyMessage(100);
    }

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == SCROLL_STATE_IDLE){
                /*listview停止滑动后标记为true*/
                isUserScrolling = true;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (isUserScrolling) {
                int listViewBottom = view.getBottom();
                int lastItemBottom = 0;
                View lastItem = view.getChildAt(view.getChildCount() - 1);
                if (lastItem != null) {
                    lastItemBottom = lastItem.getBottom();
                }
                /*根据listview底部Y坐标和listview最后一个item的底部Y坐标的大小来确定listview当前的滑动状态*/
                if (lastItemBottom > listViewBottom) {
                    /*当最后个item底部Y大于listview时标明用户在向上滑动listview，底部button消失*/
                    showSendBtn(false);
                } else if (lastItemBottom == listViewBottom) {
                    /*当最后个item底部Y等于listview时标明listview滑动到了最底部，底部button显示*/
                    showSendBtn(true);
                }
            }
        }
    };

    private void showSendBtn(boolean show) {
            if (show && isSendBtnGone && bottomLayout.getTop()!=0) {
                bottomLayout.animate().y(bottomLayout.getTop()).setDuration(500).start();
                isSendBtnGone = false;
            } else if(!show && !isSendBtnGone){
                bottomLayout.animate().y(bottomLayout.getTop() + bottomLayout.getHeight()).setDuration(500).start();
                isSendBtnGone = true;
            }
    }


    private PathMeasure pathMeasure = new PathMeasure();
    float[] mCurrentPosition = new float[2];
    private void playMoveBtn(){

        int parentLoc[] = new int[2];
        activityChatRL.getLocationInWindow(parentLoc);

        /*根据sendbtn的位置获取其在window中的坐标*/
        int startLoc[] = new int[2];
        sendBtn.getLocationInWindow(startLoc);

        /*根据listview中最后一个item的位置获取移动停止大概坐标；减2是因为算上footerview*/
        final View endView = chatLV.getChildAt(chatLV.getChildCount()-2);
        float toX, toY;
        int endLoc[] = new int[2];
        endView.getLocationInWindow(endLoc);
        /*聊天页中item的padding,此处使用来调整btn停止移动时的位置*/
        float paddingLeftRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,this.getResources().getDisplayMetrics());
        float paddingTopBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,this.getResources().getDisplayMetrics());
        toX = endLoc[0]+(endView.getWidth()-sendBtn.getWidth())-paddingLeftRight;
        toY = endLoc[1]-paddingTopBottom;

        float startX = startLoc[0];
        /*getLocationInWindow:获取在当前屏幕内的绝对坐标,包括通知栏；所以此处要通过减去整个activity根布局来间接减去通知栏的高度*/
        float startY = startLoc[1] - parentLoc[1];

        Path path = new Path();
        path.moveTo(startX,startY);
        path.lineTo(toX,toY);
        pathMeasure.setPath(path,false);

        /*将移动的view设置为显示，准备移动*/
        btnToMoved.setVisibility(View.VISIBLE);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(800);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /*在动画过程中移动view*/
                float value = (Float) animation.getAnimatedValue();
                pathMeasure.getPosTan(value, mCurrentPosition, null);
                btnToMoved.setTranslationX(mCurrentPosition[0]);
                btnToMoved.setTranslationY(mCurrentPosition[1]);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                /*动画停止后，进行添加数据*/
                btnToMoved.setVisibility(View.GONE);
                chatList.add("聊天聊天" + n++);
                adapter.notifyDataSetChanged();
                chatLV.smoothScrollToPosition(adapter.getCount());
                /*程序自动滚动时标记为false*/
                isUserScrolling = false;
                handler.sendEmptyMessageDelayed(100,500);
            }
        });
        valueAnimator.start();
    }
}
