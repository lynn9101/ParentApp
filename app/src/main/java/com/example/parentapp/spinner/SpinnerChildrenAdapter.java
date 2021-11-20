package com.example.parentapp.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.parentapp.R;
import com.example.parentapp.models.Child;
import java.util.List;

public class SpinnerChildrenAdapter extends ArrayAdapter<Child> {

    public SpinnerChildrenAdapter(Context context, List<Child> childrenList) {
        super(context,0, childrenList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position,View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.children_spinner_row,parent,false);
        }
        ImageView imgViewChild = convertView.findViewById(R.id.selectedChildPortrait);
        TextView textViewChild = convertView.findViewById(R.id.selectedChildName);
        Child currentItem = getItem(position);
        if (currentItem != null) {
            if (currentItem.hasPortrait()){
                imgViewChild.setImageBitmap(currentItem.getPortrait());
            }
            String fullName = currentItem.getFirstName() + " " + currentItem.getLastName();
            textViewChild.setText(fullName);
        }
        return convertView;
    }
}