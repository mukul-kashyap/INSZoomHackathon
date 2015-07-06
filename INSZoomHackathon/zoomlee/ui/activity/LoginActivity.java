package com.zoomlee.Zoomlee.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.Error;
import com.zoomlee.Zoomlee.net.ZoomleeCallback;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.AuthApi;
import com.zoomlee.Zoomlee.ui.MaterialDialog;
import com.zoomlee.Zoomlee.ui.view.LoadingView;
import com.zoomlee.Zoomlee.ui.view.ZMEditText;
import com.zoomlee.Zoomlee.utils.DeveloperUtil;
import com.zoomlee.Zoomlee.utils.PreferencesKeys;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;
import com.zoomlee.Zoomlee.utils.Util;

import retrofit.RestAdapter;


public class LoginActivity extends Activity {

    private ZMEditText emailPhoneView;
    private Button actionButton;
    private LoadingView loadingView;
    private String login;

    private final AuthApi api = new RestAdapter.Builder()
            .setEndpoint(ApiUrl.API_URL)
            .build().create(AuthApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPreferenceUtils.getUtils().isPinSetuped()) {
            MainActivity.startActivity(this, true);
            finish();
            return;
        }

        initUi();
        initListeners();
        try2Prefill();
    }

    private void try2Prefill() {
        // first try to get from saved session
        String savedEmailPhone = SharedPreferenceUtils.getUtils().getStringSetting(PreferencesKeys.LOGIN);

        // if not found try to get my phone number
        if (TextUtils.isEmpty(savedEmailPhone)) {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            savedEmailPhone = tMgr.getLine1Number();
        }

        // set found number
        if (savedEmailPhone != null) {
            emailPhoneView.setText(savedEmailPhone);
            emailPhoneView.setSelection(emailPhoneView.length());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        CharSequence emailPhone = emailPhoneView.getText();
        if (!TextUtils.isEmpty(emailPhone)) {
            SharedPreferenceUtils.getUtils().setStringSetting(PreferencesKeys.LOGIN, emailPhone.toString());
        }
    }

    private void initUi() {
        emailPhoneView = (ZMEditText) findViewById(R.id.emailPhoneEt);
        actionButton = (Button) findViewById(R.id.actionLogin);
        loadingView = (LoadingView) findViewById(R.id.loading);
    }

    private void initListeners() {
        DeveloperUtil.michaelLog();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeveloperUtil.michaelLog();
                login = emailPhoneView.getText().toString();
                blockUi();
                emailPhoneView.setError(false);
                if (Util.isEmail(login))
                    loginByEmail();
                else
                    loginByPhone();
            }
        });
    }

    private void loginByPhone() {
        String formatedPhone = Util.formatNumberToE164(login, Util.getCountryIsoCode(this));
        if (formatedPhone != null)
            login = formatedPhone;
        DeveloperUtil.michaelLog(login);
        api.login(null, login, loginCallback);
    }

    private void loginByEmail() {
        DeveloperUtil.michaelLog();
        api.login(login, null, loginCallback);
    }

    private void blockUi() {
        actionButton.setVisibility(View.INVISIBLE);
        loadingView.show();
    }

    private void unlockUi() {
        actionButton.setVisibility(View.VISIBLE);
        loadingView.hide();
    }

    private ZoomleeCallback<CommonResponse<Object>> loginCallback = new ZoomleeCallback<CommonResponse<Object>>() {

        @Override
        protected void success(Object response) {
            DeveloperUtil.michaelLog(response);
            PasscodeActivity.startActivity(LoginActivity.this, login);
            finish();
        }

        @Override
        protected void error(Error error) {
            unlockUi();
            emailPhoneView.setError(true);
            if (error.getCode() == Error.NO_CONNECTION_CODE)
                showNoConnectionAlert();
            else
                Toast.makeText(LoginActivity.this, error.getReason(), Toast.LENGTH_LONG).show();
        }
    };

    private void showNoConnectionAlert() {
        MaterialDialog mMaterialDialog = new MaterialDialog(this)
                .setTitle(R.string.noconnection_title)
                .setMessage(R.string.noconnection_message)
                .setPositiveButton(getString(R.string.ok).toUpperCase(), null);

        mMaterialDialog.show();
    }
}
