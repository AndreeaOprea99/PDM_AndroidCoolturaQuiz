package com.example.coolturaquiz;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class CatGridAdapter extends BaseAdapter {
    public CatGridAdapter(List<CategoryModel> catList) {
        this.catList = catList;
    }

    private List<CategoryModel> catList;

    @Override
    public int getCount() {
        return catList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if(convertView==null)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent,false);

        }else{
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.selected_category_index = position;
                Intent intent = new Intent(parent.getContext(),SetsActivity.class);
                parent.getContext().startActivity(intent);
            }
        });

        ((TextView) view.findViewById(R.id.categoryname)).setText(catList.get(position).getName());

        Random rnd = new Random();
        int color = Color.argb(255,rnd.nextInt(125),rnd.nextInt(125),rnd.nextInt(125));
        view.setBackgroundColor(color);


        return view;
    }
}
