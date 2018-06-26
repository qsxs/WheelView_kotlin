package com.lihb.library2d.adapter;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseQuickWheelAdapter<T> extends AbstractWheelAdapter {
    private List<T> mData;
    private int itemResourceId;

    public BaseQuickWheelAdapter(@LayoutRes int itemLayoutId) {
        itemResourceId = itemLayoutId;
    }

    public BaseQuickWheelAdapter(@LayoutRes int itemLayoutId, List<T> data) {
        itemResourceId = itemLayoutId;
        mData = data;
    }

    public void setNewData(List<T> data) {
        mData = data;
    }

    public List<T> getData() {
        return mData == null ? new ArrayList<T>() : mData;
    }

    @Override
    public int getItemsCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(itemResourceId, parent, false);
            }
            onBindData(index, mData.get(index), convertView);
            return convertView;
        }
        return null;
    }

    protected abstract void onBindData(int index, T bean, View convertView);
}
