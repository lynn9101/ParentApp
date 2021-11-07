package com.example.parentapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

public class FlipCoinMessageFragment extends AppCompatDialogFragment {
    private boolean sideResult;
    private String messageResult = "";

    public FlipCoinMessageFragment (Boolean sideResult, String messageResult) {
        this.sideResult = sideResult;
        this.messageResult = messageResult;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the view to show
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.flip_coin_popup_message, null);

        // Create button listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(((Dialog)dialog).getContext(),CoinFlipHistoryActivity.class));
            }
        };

        ImageView resultIcon = (ImageView)v.findViewById(R.id.coinResult);
        Drawable drawable;
        if (sideResult) {
            drawable = getResources().getDrawable(R.drawable.head_icon);
        } else {
            drawable = getResources().getDrawable(R.drawable.tail_icon);
        }
        resultIcon.setImageDrawable(drawable);

        TextView resultText = (TextView)v.findViewById(R.id.textResult);
        resultText.setText(messageResult);

        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Coin Flip Result!")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }
}
