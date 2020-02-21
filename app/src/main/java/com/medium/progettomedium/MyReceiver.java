package com.medium.progettomedium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class MyReceiver extends BroadcastReceiver {
    private final Context mContext;

    boolean canGetInternet = false;
    int var=0;

    public MyReceiver(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        getConnectivityStatusString(context);





    }

    public void getConnectivityStatusString(Context context) {
        String status = null;
        ConnectivityManager cm = (ConnectivityManager)           context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && var ==1) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = "Wifi abilitato";
                Toast.makeText(context, status, Toast.LENGTH_LONG).show();

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = "Dati mobili abilitati";
                Toast.makeText(context, status, Toast.LENGTH_LONG).show();
                var=0;
            }
        } if (activeNetwork != null && var ==0){

        } else{
           status = "Abilita internet";
            var =1;
            showSettingsAlert();
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        }
    }
    public void showSettingsAlert(){
       // AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        // Setting Dialog Title
        builder.setTitle("Internet non disponibile");

        // Setting Dialog Message
        builder.setMessage("Non hai internet attivo. Vuoi attivarlo tramite le impostazioni del cellulare?");

        // On pressing the Settings button.
        builder.setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // On pressing the cancel button
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        builder.show();
    }


}