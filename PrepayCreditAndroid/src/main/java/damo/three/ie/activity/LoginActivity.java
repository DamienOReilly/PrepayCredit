/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2014  Damien O'Reilly
 *
 * Prepay Credit for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Prepay Credit for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Prepay Credit for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/PrepayCredit
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import damo.three.ie.R;
import damo.three.ie.prepay.Constants;

/**
 * Provides a login UI to the user on first run, or if login credentials are incomplete.
 *
 * @author Damien O'Reilly
 */
public class LoginActivity extends Activity {

    private SharedPreferences sharedPreferences;
    private EditText mobileNumber;
    private EditText password;
    private TextView forgotPass;
    private TextView register;
    private TextView textViewSimIccid;
    private Button continueButton;
    private String iccid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean firstrun = sharedPreferences.getBoolean("firstrun", true);
        // First run?, ask for credentials, otherwise go straight to login screen.
        if (firstrun || (sharedPreferences.getString("mobile", "").equals("") || sharedPreferences.getString
                ("password", "").equals(""))) {
            setUpLoginUI();
        } else {
            openPrepayCreditActivity();
        }
    }

    private void setUpLoginUI() {
        setContentView(R.layout.login_layout);
        mobileNumber = (EditText) findViewById(R.id.mobile_number);
        password = (EditText) findViewById(R.id.password);
        forgotPass = (TextView) findViewById(R.id.forgot_password_textview);
        register = (TextView) findViewById(R.id.register_textview);
        continueButton = (Button) findViewById(R.id.continue_button);

        LoginClickListener loginClickListener = new LoginClickListener();

        register.setOnClickListener(loginClickListener);
        forgotPass.setOnClickListener(loginClickListener);
        continueButton.setOnClickListener(loginClickListener);

        // To register or request lost password, 3 wants the last 6 digits from the SIM's ICCID. For convenience, we
        // will display it to the user.
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        iccid = tm.getSimSerialNumber();
        if (iccid != null && (iccid.length() >= 6)) {
            iccid = iccid.substring(iccid.length() - 6);

            textViewSimIccid = (TextView) findViewById(R.id.sim_iccid);
            textViewSimIccid.setText(String.format(getString(R.string.sim_iccid), iccid));
            textViewSimIccid.setOnClickListener(loginClickListener);
        }
    }

    private void showError(EditText field) {
        field.setError(getString(R.string.login_field_validation_error));
    }

    private void openUrl(String registerUrl) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(registerUrl));
        startActivity(i);
    }

    private boolean fieldIsEmpty(EditText field) {
        return field.getText().toString().trim().equals("");
    }

    private void copyIccidToClipboard() {
        @SuppressWarnings("deprecation")
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(iccid);
        Toast.makeText(this, getString(R.string.clipboard), Toast.LENGTH_SHORT).show();
    }

    private void openPrepayCreditActivity() {
        Intent intent = new Intent(this, PrepayCreditActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mobile", mobileNumber.getText().toString().trim());
        editor.putString("password", password.getText().toString().trim());
        editor.putBoolean("firstrun", false);
        editor.commit();
    }

    private class LoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == register) {
                openUrl(Constants.REGISTER_URL);
            }

            if (v == forgotPass) {
                openUrl(Constants.FORGOT_PASS_URL);
            }

            // Save credentials and open the usage activity.
            if (v == continueButton) {
                if (fieldIsEmpty(mobileNumber)) {
                    showError(mobileNumber);
                } else if (fieldIsEmpty(password)) {
                    showError(password);
                } else {
                    saveCredentials();
                    openPrepayCreditActivity();
                }
            }

            if (v == textViewSimIccid) {
                copyIccidToClipboard();
            }
        }
    }
}
