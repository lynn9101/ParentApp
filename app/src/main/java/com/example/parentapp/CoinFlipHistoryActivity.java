package com.example.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
            int iconID;
            if (coinFlipInstance.didPickerWin()) {
                iconID = R.drawable.ic_check_circle_48;
            } else {
                iconID = R.drawable.ic_remove_circle_48;
            }
            statusIcon.setImageResource(iconID);

            TextView pickerStatus = itemView.findViewById(R.id.pickerStatus);
            pickerStatus.setText(coinFlipInstance.getPickerStatus());

            TextView coinFlipDate = itemView.findViewById(R.id.coinFlipDate);
            coinFlipDate.setText(coinFlipInstance.getFormattedCoinFlipTime());

            return itemView;
        }
    }
}