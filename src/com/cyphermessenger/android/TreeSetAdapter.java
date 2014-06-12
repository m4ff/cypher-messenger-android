package com.cyphermessenger.android;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cyphermessenger.client.CypherMessage;

import java.util.Collections;
import java.util.TreeSet;

/**
 * Created by paolo on 12/06/14.
 */
public abstract class TreeSetAdapter<T> extends BaseAdapter {

    protected final TreeSet<T> treeSet;
    protected T[] treeSetArray;

    public TreeSetAdapter(TreeSet<T> treeSet, T[] treeSetArray) {
        this.treeSet = treeSet;
        this.treeSetArray = treeSet.toArray(treeSetArray);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.treeSetArray = treeSet.toArray(treeSetArray);
    }

    @Override
    public int getCount() {
        return treeSet.size();
    }

    @Override
    public T getItem(int i) {
        return treeSetArray[i];
    }
}
