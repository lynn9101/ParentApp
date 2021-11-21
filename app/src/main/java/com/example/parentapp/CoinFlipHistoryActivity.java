package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.parentapp.models.Child;
import com.example.parentapp.models.ChildrenManager;
import com.example.parentapp.models.CoinFlip;
import com.example.parentapp.models.CoinFlipManager;
import com.example.parentapp.models.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * The CoinFlipHistoryActivity class is an android activity that handles the listing of past coin flip results
 */

public class CoinFlipHistoryActivity extends AppCompatActivity {

    private List<CoinFlip> history = new ArrayList<>();
    private ChildrenManager childrenManager = ChildrenManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip_history);

        getSupportActionBar().setTitle(R.string.coin_flip_history_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_title_color)));

        populateCoinFlipHistoryList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
    private void populateCoinFlipHistoryList() {
        Context ctx = getApplicationContext();
        CoinFlipManager coinManager = CoinFlipManager.getInstance();
        SharedPreferences sharedPreference = Helpers.getSharedPreference(ctx);
        String historyKey = ctx.getResources().getString(R.string.shared_pref_coin_flip_history_key);

        if (sharedPreference.contains(historyKey)) {
            coinManager.setCoinFlipHistory(Helpers.getObjectFromSharedPreference(ctx, historyKey, Helpers.getListOfClassType(CoinFlip.class)));
        }

        this.history = coinManager.getCoinFlipHistory();
        ListView historyList = findViewById(R.id.coinflipHistoryList);
        ArrayAdapter<CoinFlip> adapter = new CoinFlipHistoryListAdapter();

        historyList.setAdapter(adapter);
    }

    private class CoinFlipHistoryListAdapter extends ArrayAdapter<CoinFlip> {
        public CoinFlipHistoryListAdapter() {
            super(CoinFlipHistoryActivity.this, R.layout.coinflip_history_list, history);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.coinflip_history_list, parent, false);
            }

            CoinFlip coinFlipInstance = history.get(position);

            ImageView statusIcon = itemView.findViewById(R.id.statusIcon);
            ImageView kidsPortrait = itemView.findViewById(R.id.imgChildPortrait);

            Child picker = null;

            if (Helpers.isStringNullOrEmpty(coinFlipInstance.getPickerID())) {
                kidsPortrait.setImageDrawable(getResources().getDrawable(R.drawable.child_image_listview));
            } else {
                picker = childrenManager.getChildren().stream().filter(x -> x.getUniqueID().equals(coinFlipInstance.getPickerID())).findFirst().orElse(null);

                if (picker != null) {
                    kidsPortrait.setImageBitmap(picker.getPortrait());
                } else {
                    kidsPortrait.setImageDrawable(null);
                }
            }

            int iconID;
            if (coinFlipInstance.didPickerWin()) {
                iconID = R.drawable.ic_check_circle_48;
            } else {
                iconID = R.drawable.ic_remove_circle_48;
            }
            statusIcon.setImageResource(iconID);

            TextView pickerStatus = itemView.findViewById(R.id.pickerStatus);
            pickerStatus.setText(coinFlipInstance.getPickerStatus(picker));

            TextView coinFlipDate = itemView.findViewById(R.id.coinFlipDate);
            coinFlipDate.setText(coinFlipInstance.getFormattedCoinFlipTime());

            return itemView;
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,CoinFlipActivity.class));
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}