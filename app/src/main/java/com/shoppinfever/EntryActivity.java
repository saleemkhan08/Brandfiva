package com.shoppinfever;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.shoppinfever.fragments.LoginFragment;
import com.shoppinfever.services.Util;

public class EntryActivity extends AppCompatActivity
{
    private static final int LOGIN_FAIL = 0;
    private static final int PROFILE_SETUP_FAIL = 1;
    private static final int PRODUCT_COUNT_FAIL = 2;
    private static final int CATEGORY_COUNT_3_10 = 3 ;
    private static final int CATEGORY_COUNT_10 = 4;
    private static final String ENTRY_PREFERENCE = "ENTRY_PREFERENCE";
    private static final String ENTRY_STATUS = "ENTRY_STATUS";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);
        getSupportActionBar().hide();
        Util.setContext(this); // For Toast, Log
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!Util.isConnected(this))
        {
            Util.createDialog(this);
        }
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences(ENTRY_PREFERENCE, Context.MODE_PRIVATE);
            int entryPoint = sharedPreferences.getInt(ENTRY_STATUS, 0);
            switch(entryPoint)
            {
                case LOGIN_FAIL :showLoginFragment();
                    break;
                case PROFILE_SETUP_FAIL : showProfileSetupFragment();
                    break;
                case PRODUCT_COUNT_FAIL : showAddProductActivity();
                    break;
                default:showHomeActivity(entryPoint);
                    break;
            }
        }
    }

    private void showHomeActivity(int categoryCount)
    {
        if(categoryCount == CATEGORY_COUNT_3_10)
        {

        }else if(categoryCount == CATEGORY_COUNT_10)
        {

        }else
        {

        }
    }

    private void showAddProductActivity()
    {
    }

    private void showProfileSetupFragment()
    {

    }

    private void showLoginFragment()
    {
        replaceFragment(R.id.entryActivity, new LoginFragment(), "LoginFragment");
    }

    /*public void addFragment(int parentLayoutId, Fragment fragment,String fragmentTag )
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(parentLayoutId, fragment, fragmentTag);
        fragmentTransaction.commit();
    }*/

    public void replaceFragment(int parentLayoutId, Fragment fragment,String fragmentTag )
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(parentLayoutId, fragment, fragmentTag);
        fragmentTransaction.commit();
    }
}