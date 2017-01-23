package com.android.philip.photoapp;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.philip.photoapp.api.XAuth500pxTask;
import com.android.philip.photoapp.api.auth.AccessToken;
import com.android.philip.photoapp.api.auth.FiveHundredException;


public class LoginActivity extends Activity  implements XAuth500pxTask.Delegate {
    private static final String TAG = "LoginActivity";

    private Button mLoginButton;
    private EditText mUsrname, mPwd;
    private AccessToken mAccessToken;
    XAuth500pxTask mLoginTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (Button)findViewById(R.id.login_btn);
        mUsrname = (EditText)findViewById(R.id.usr_name_input);
        mPwd = (EditText)findViewById(R.id.pwd_input);

        mAccessToken = null;
        mLoginTask = new XAuth500pxTask(this);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsrname.getText().toString();
                String pwd = mPwd.getText().toString();

                try {
                    mAccessToken = mLoginTask.execute(getString(R.string.consumer_key), getString(R.string.consumer_secret),
                            username, pwd).get();
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }

                if (mAccessToken == null) {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(getString(R.string.ACCESS_TOKEN), mAccessToken);
                    resultIntent.putExtra(getString(R.string.USERNAME), username);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onSuccess(AccessToken result) {
        Log.w(TAG, "success "+result);
    }

    @Override
    public void onFail(FiveHundredException e) {}
}