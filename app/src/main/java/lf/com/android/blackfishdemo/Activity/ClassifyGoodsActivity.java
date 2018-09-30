package lf.com.android.blackfishdemo.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import lf.com.android.blackfishdemo.R;
import lf.com.android.blackfishdemo.adapter.ClassifyCommonAdaoter;
import lf.com.android.blackfishdemo.adapter.ClassifyTitleAdapter;
import lf.com.android.blackfishdemo.bean.ClassifyGoodsInfo;
import lf.com.android.blackfishdemo.bean.ClassifyGridInfo;
import lf.com.android.blackfishdemo.bean.UrlInfoBean;
import lf.com.android.blackfishdemo.listener.OnClassifyItemClickListener;
import lf.com.android.blackfishdemo.listener.OnNetResultListener;
import lf.com.android.blackfishdemo.util.JsonUtil;
import lf.com.android.blackfishdemo.util.OkHttpUtil;
import lf.com.android.blackfishdemo.view.GridViewForScroll;

public class ClassifyGoodsActivity extends BaseActivity {
    private Context mContext;
    private ArrayList<String> mListTitles;
    private List<ClassifyGoodsInfo> mClassifyGoodsInfos;
    private ClassifyTitleAdapter mTitleAdapter;
    @BindView(R.id.rv_classify_goods_left_title)
    RecyclerView mRecyclerViewLeft;
    @BindView(R.id.iv_classify_goods_details_header)
    SimpleDraweeView mDraweeViewHeader;
    @BindView(R.id.gv_classify_common)
    GridViewForScroll mGridCommon;
    @BindView(R.id.gv_classify_hot)
    GridViewForScroll mGridHot;
    @BindView(R.id.rl_classify_heard_search)
    RelativeLayout mLayoutHeader;
    @BindView(R.id.iv_classify_goods_back)
    ImageView mImageBack;
    @BindView(R.id.iv_classify_heard_msg)
    ImageView mImageMsg;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    for (int i = 0; i < mClassifyGoodsInfos.size(); i++) {
                        String title = mClassifyGoodsInfos.get(i).getTitle();
                        mListTitles.add(title);
                        if (i == 0) {
                            setItemData(i);
                        }
                    }
                    mTitleAdapter = new ClassifyTitleAdapter(mContext, mListTitles);
                    mTitleAdapter.setListener(new OnClassifyItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            for (int i = 0; i < mRecyclerViewLeft.getChildCount(); i++) {
                                FrameLayout frameLayout = (FrameLayout) mRecyclerViewLeft.getChildAt(i);
                                TextView textView = (TextView) frameLayout.getChildAt(0);
                                if (i == position) {
                                    frameLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                    textView.setTextColor(Color.parseColor("#FECD15"));
                                } else {
                                    frameLayout.setBackgroundColor(Color.parseColor("#FAFAFA"));
                                    textView.setTextColor(Color.parseColor("#222222"));
                                }
                            }
                            setItemData(position);
                        }
                    });
                    mRecyclerViewLeft.setAdapter(mTitleAdapter);
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    @Override
    public int getlayoutId() {
        return R.layout.activity_classify_goods_layout;
    }

    @Override
    public void initView() {
        Fresco.initialize(this);
        mContext = ClassifyGoodsActivity.this;
        mLayoutHeader.setOnClickListener(this);
        mImageMsg.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
    }

    @Override
    public void intitdata() {
        mClassifyGoodsInfos = new ArrayList<>();
        mListTitles = new ArrayList<>();
        OkHttpUtil.getInstance().startGet(UrlInfoBean.classifyGoodsUrl, new OnNetResultListener() {
            @Override
            public void OnSuccessListener(String result) {
                JsonUtil jsonUtil = new JsonUtil();
                mClassifyGoodsInfos = jsonUtil.getDataFromJson(result, 2);
                Message message = mHandler.obtainMessage(0x01, mClassifyGoodsInfos);
                mHandler.sendMessage(message);
            }

            @Override
            public void OnFailureListener(String result) {

            }
        });

    }

    @Override
    public void ClickListener(View view) {

    }


    private void setItemData(int position) {
        List<ClassifyGridInfo> mGridInfosCommon = new ArrayList<>();
        List<ClassifyGridInfo> mGridInfosHot = new ArrayList<>();
        String hearderImageUrl = mClassifyGoodsInfos.get(position).getHeaderImageUrl();
        int commonSize = mClassifyGoodsInfos.get(position).getGridImageUrls1().size();
        int hotSize = mClassifyGoodsInfos.get(position).getGridImageUrls2().size();
        for (int i = 0; i < commonSize; i++) {
            mGridInfosCommon.add(mClassifyGoodsInfos.get(position).getGridImageUrls1().get(i));
        }

        for (int i = 0; i < hotSize; i++) {
            mGridInfosHot.add(mClassifyGoodsInfos.get(position).getGridImageUrls2().get(i));
        }

        mDraweeViewHeader.setImageURI(hearderImageUrl);
        mGridCommon.setAdapter(new ClassifyCommonAdaoter(mContext, mGridInfosCommon));
        mGridHot.setAdapter(new ClassifyCommonAdaoter(mContext, mGridInfosHot));
    }
}