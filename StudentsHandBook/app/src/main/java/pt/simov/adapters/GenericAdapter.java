package pt.simov.adapters;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class GenericAdapter<T> extends BaseAdapter {
    protected Context context;
    protected int rowLayout;
    protected ArrayList<T> data;
    public GenericAdapter(Context context, int rowLayout, ArrayList<T> data){
        this.context = context;
        this.rowLayout = rowLayout;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
}
