package com.shoppinfever.services;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.shoppinfever.R;

public class Util
{
    private static Context context;
    public static void toast(String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void log(String msg)
    {
        Log.d("Brandfiva", msg);
    }

    public static boolean isConnected(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                return true;
            }
        }
        return false;
    }
    public static void createDialog(final Activity activity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.no_network_access);
        builder.setPositiveButton(R.string.action_settings,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        dialog.dismiss();
                    }
                });

        builder.setCancelable(false);
        builder.setTitle(R.string.internet_failure);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener()
        {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP
                        && !event.isCanceled())
                {
                    dialog.dismiss();
                    activity.finish();
                }
                return false;
            }
        });
        builder.show();
    }

    public static Context getContext()
    {
        return context;
    }

    public static void setContext(Context context)
    {
        Util.context = context;
    }
}
