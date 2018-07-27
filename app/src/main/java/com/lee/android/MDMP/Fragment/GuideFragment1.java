package com.lee.android.MDMP.Fragment;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lee.android.MDMP.R;

public class GuideFragment1 extends Fragment {
    private int imageId=R.drawable.pic0;
    private ImageView iv_picture;

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        iv_picture=new ImageView(getActivity());
        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv_picture.setImageResource(imageId);
        iv_picture.setLayoutParams(LayoutParams);
        return iv_picture;
    }
}
