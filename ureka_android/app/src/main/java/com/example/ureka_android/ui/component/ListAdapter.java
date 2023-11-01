package com.example.ureka_android.ui.component;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.example.ureka_android.databinding.DeviceListItemBinding;

import java.util.ArrayList;

public class ListAdapter<T> extends BaseAdapter {
    private final Context context;
    private ArrayList<T> list;
    private final int layoutId;
    private final int variableId;

    public ListAdapter(Context context, ArrayList list, int layoutId, int variableId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
        this.variableId = variableId;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewDataBinding listItemBinding = null;
        if (view == null) {
            listItemBinding = DeviceListItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        } else {
            listItemBinding = DataBindingUtil.getBinding(view);
        }
        listItemBinding.setVariable(variableId, list.get(i));
        return listItemBinding.getRoot();
    }
}