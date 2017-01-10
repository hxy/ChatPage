package hy.chatpage;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by huangyue on 2017/1/5.
 */

public class ChatAdapter extends BaseAdapter {
    private ArrayList<String> chatDataList;
    private Context context;
    private ListView listView;
    private LayoutTransition mLeftTransitioner;
    private LayoutTransition mRightTransitioner;
    /*记录最近一次更新view时聊天列表中数据的个数*/
    private int oldCount = 0;

    public ChatAdapter(Context context,ArrayList<String> chatDataList){
        this.context = context;
        this.chatDataList = chatDataList;
        oldCount = chatDataList.size();
//        this.listView =listView;

        /*使用setLayoutTransition方法时当listview的item增加到使listview可以滑动时进度条对话框消失后聊天内容进入的动画效果就会消失，固不使用*/
//        /*初始化左边聊天动画*/
//        mLeftTransitioner = new LayoutTransition();
//        ObjectAnimator animator = ObjectAnimator.ofFloat(null,"TranslationX",-getScreenWidth(context),0f);
//        mLeftTransitioner.setAnimator(LayoutTransition.APPEARING,animator);
//        mLeftTransitioner.setDuration(LayoutTransition.APPEARING,200);
//        mLeftTransitioner.setInterpolator(LayoutTransition.APPEARING,new AccelerateInterpolator());
//        /*初始化右边聊天动画*/
//        mRightTransitioner = new LayoutTransition();
    }
    @Override
    public int getCount() {
        return chatDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chatlist,null);
            holder = new ViewHolder();
            holder.loadingIcon = (ImageView)convertView.findViewById(R.id.loading_icon);
            holder.textView = (TextView)convertView.findViewById(R.id.text);
            Glide.with(context).load(R.mipmap.loading).asGif().into(holder.loadingIcon);
            convertView.setTag(holder);
        }
        holder = (ViewHolder)convertView.getTag();
        holder.textView.setText(chatDataList.get(position));
        holder.loadingIcon.setVisibility(View.GONE);
        holder.textView.setVisibility(View.VISIBLE);
        if(chatDataList.get(position).equals("loading")){
            holder.loadingIcon.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.GONE);
            /*使用setLayoutTransition方法时当listview的item增加到使listview可以滑动时进度条消失后聊天内容进入的动画效果就会消失，固不使用*/
//            listView.setLayoutTransition(mLeftTransitioner);
            if(position > oldCount-1){
                /*当position大于更新前聊天数据量时标明当前view是新加的，执行动画*/
                startLeftSideInAnim(convertView);
            }
        }else {
            if((position+1)%2!=0){
                holder.textView.setBackgroundResource(R.mipmap.voice_bg_get);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.textView.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                /*使用setLayoutTransition方法时当listview的item增加到使listview可以滑动时进度条消失后聊天内容进入的动画效果就会消失，固不使用*/
//                listView.setLayoutTransition(mLeftTransitioner);
                if(position > oldCount-1) {
                    /*当position大于更新前聊天数据量时标明当前view是新加的，执行动画；*/
                    startLeftSideInAnim(convertView);
                    oldCount = getCount();
                }
            }else {
                holder.textView.setBackgroundResource(R.mipmap.voice_bg_send);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)holder.textView.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                /*使用setLayoutTransition方法时当listview的item增加到使listview可以滑动时进度条消失后聊天内容进入的动画效果就会消失，固不使用*/
//                listView.setLayoutTransition(mRightTransitioner);
            }
        }
        return convertView;
    }

    private class ViewHolder{
        ImageView loadingIcon;
        TextView textView;
    }

    private int getScreenWidth(Context context){
        int[] deviceSize = new int[2];
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        deviceSize[0] = display.getWidth();
        deviceSize[1] = display.getWidth();
        return deviceSize[0];
    }

    private void startLeftSideInAnim(View view){
        view.setTranslationX(-getScreenWidth(context));
        view.animate().translationX(0).setInterpolator(new AccelerateInterpolator()).setDuration(500).start();
    }
}
