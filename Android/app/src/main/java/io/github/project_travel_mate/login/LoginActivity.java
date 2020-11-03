package io.github.project_travel_mate.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.processbutton.FlatButton;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.project_travel_mate.MainActivity;
import io.github.project_travel_mate.R;
import utils.TravelmateSnackbars;
import utils.Utils;

import static utils.Constants.USER_EMAIL;
import static utils.Constants.USER_TOKEN;

/**
 * Initiates login
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginView,
        TravelmateSnackbars, OnOtpCompletionListener {

    private final LoginPresenter mLoginPresenter = new LoginPresenter();
    @BindView(R.id.signup)
    TextView signup;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.back_to_login)
    TextView mBackToLogin;

    @BindView(R.id.signup_layout)
    LinearLayout sig;
    @BindView(R.id.loginlayout)
    LinearLayout log;
    @BindView(R.id.forgot_password_layout)
    LinearLayout mForgotPasswordLayout;
    @BindView(R.id.reset_code_layout)
    LinearLayout mResetCodeLayout;
    @BindView(R.id.new_password_layout)
    LinearLayout mNewPasswordLayout;

    @BindView(R.id.input_email_login)
    EditText email_login;
    @BindView(R.id.input_pass_login)
    EditText pass_login;
    @BindView(R.id.input_email_signup)
    EditText email_signup;
    @BindView(R.id.input_pass_signup)
    EditText pass_signup;
    @BindView(R.id.input_confirm_pass_signup)
    EditText confirm_pass_signup;
    @BindView(R.id.input_firstname_signup)
    EditText firstName;
    @BindView(R.id.input_lastname_signup)
    EditText lastName;
    @BindView(R.id.input_email_forgot_password)
    EditText mEmailForgotPassword;
    @BindView(R.id.input_pass_reset)
    EditText mNewPasswordReset;
    @BindView(R.id.input_confirm_pass_reset)
    EditText mConfirmNewPasswordReset;

    @BindView(R.id.ok_login)
    FlatButton ok_login;
    @BindView(R.id.ok_signup)
    FlatButton ok_signup;
    @BindView(R.id.ok_submit_pass_reset)
    FlatButton mOkSubmitReset;
    @BindView(R.id.ok_confirm_pass_reset)
    FlatButton mOkConfirmReset;

    @BindView(R.id.forgot_password)
    TextView mForgotPasswordText;
    @BindView(R.id.code_sent_alert)
    TextView mCodeSentAlert;
    @BindView(R.id.resend_code)
    TextView mResendCodeText;

    @BindView(R.id.reset_code)
    OtpView mResetCode;

    @BindView(R.id.input_layout_email_forgot_password)
    TextInputLayout mInputLayoutEmailForgotPassword;
    @BindView(R.id.input_layout_firstname_signup)
    TextInputLayout mInputLayoutFirstNameSignup;
    @BindView(R.id.input_layout_lastname_signup)
    TextInputLayout mInputLayoutLastNameSignup;

    private SharedPreferences mSharedPreferences;
    private MaterialDialog mDialog;
    private Handler mHandler;
    private String mOtpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        mLoginPresenter.bind(this);
        ButterKnife.bind(this);

        // Initialization
        mHandler = new Handler(Looper.getMainLooper());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get runtime permissions for Android M
        getRunTimePermissions();

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        ok_login.setOnClickListener(this);
        ok_signup.setOnClickListener(this);

        //listeners for handling 'forgot password' flow
        mForgotPasswordText.setOnClickListener(this);
        mBackToLogin.setOnClickListener(this);
        mResendCodeText.setOnClickListener(this);
        mOkSubmitReset.setOnClickListener(this);
        mOkConfirmReset.setOnClickListener(this);

        //listener for otp view to accept password reset code
        mResetCode.setOtpCompletionListener(this);
    }

    @Override
    protected void onDestroy() {
        mLoginPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Open signup
            case R.id.signup:
                signup.setVisibility(View.GONE);
                mForgotPasswordText.setVisibility(View.GONE);
                mBackToLogin.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                mLoginPresenter.signUp();
                break;
            // Open login
            case R.id.login:
                signup.setVisibility(View.VISIBLE);
                mForgotPasswordText.setVisibility(View.VISIBLE);
                mBackToLogin.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                mLoginPresenter.login(false);
                break;
            // Call login
            case R.id.ok_login:
                String emailString = email_login.getText().toString();
                String passString = pass_login.getText().toString();
                if (Utils.isNetworkConnected(this)) {
                    mLoginPresenter.ok_login(emailString, passString, mHandler);
                } else {
                    showNoNetwork();
                }
                break;
            // Call signup
            case R.id.ok_signup:
                emailString = email_signup.getText().toString();
                passString = pass_signup.getText().toString();
                String confirmPassString = confirm_pass_signup.getText().toString();
                String firstname = firstName.getText().toString();
                String lastname = lastName.getText().toString();
                if (validateName(firstname, lastname)) {
                    if (validateEmail(emailString)) {
                        if (validatePassword(passString)) {
                            if (passString.equals(confirmPassString)) {
                                if (Utils.isNetworkConnected(this)) {
                                    mLoginPresenter.ok_signUp(firstname, lastname, emailString, passString, mHandler);
                                } else {
                                    showNoNetwork();
                                }
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(findViewById(android.R.id.content),
                                                R.string.passwords_check, Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content),
                                        R.string.email_valid_check, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                break;
                // Open forgot password
            case R.id.forgot_password:
                login.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);
                mForgotPasswordText.setVisibility(View.GONE);
                mBackToLogin.setVisibility(View.VISIBLE);
                mLoginPresenter.forgotPassword();
                break;
                // Call submit password reset request
            case R.id.ok_submit_pass_reset:
                emailString = mEmailForgotPassword.getText().toString();
                if (validateEmail(emailString)) {
                    mBackToLogin.setVisibility(View.GONE);
                    mLoginPresenter.ok_password_reset_request(emailString, mHandler);
                }
                break;
                // Open login
            case R.id.back_to_login:
                signup.setVisibility(View.VISIBLE);
                mForgotPasswordText.setVisibility(View.VISIBLE);
                mBackToLogin.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                mLoginPresenter.login(false);
                break;
                // Call resend reset code request
            case R.id.resend_code:
                emailString = mEmailForgotPassword.getText().toString();
                mLoginPresenter.resendResetCode(emailString, mHandler);
                break;
                // Call confirm reset request
            case R.id.ok_confirm_pass_reset:
                emailString = mEmailForgotPassword.getText().toString();
                mLoginPresenter.ok_password_reset_confirm(emailString);
                break;
        }
    }

    @Override
    public void rememberUserInfo(String token, String email) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(USER_TOKEN, token);
        editor.putString(USER_EMAIL, email);
        editor.apply();
    }

    @Override
    public void startMainActivity() {
        Intent i = MainActivity.getStartIntent(this);
        startActivity(i);
        finish();
    }

    @Override
    public void showError() {
        TravelmateSnackbars.createSnackBar(findViewById(R.id.login_activity),
                R.string.toast_invalid_username_or_password, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNoNetwork() {
        TravelmateSnackbars.createSnackBar(findViewById(R.id.login_activity),
                R.string.toast_no_internet_detected, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoadingDialog() {
        mDialog = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .content(R.string.progress_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void dismissLoadingDialog() {
        mDialog.dismiss();
    }

    @Override
    public void getRunTimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.VIBRATE,
                }, 0);
            }
        }
    }

    @Override
    public void openSignUp() {
        log.setVisibility(View.GONE);
        mForgotPasswordLayout.setVisibility(View.GONE);
        sig.setVisibility(View.VISIBLE);
    }

    @Override
    public void openLogin(boolean showToastMessage) {
        if (showToastMessage) {
            showMessage(getString(R.string.text_password_updated_alert));
        }
        mForgotPasswordLayout.setVisibility(View.GONE);
        mNewPasswordLayout.setVisibility(View.GONE);
        sig.setVisibility(View.GONE);
        log.setVisibility(View.VISIBLE);
        mForgotPasswordText.setVisibility(View.VISIBLE);
        mEmailForgotPassword.setText("");
    }

    /**
     * displays input fields to get user's email when he/she requests for a password reset
     */
    @Override
    public void forgotPassword() {
        log.setVisibility(View.GONE);
        sig.setVisibility(View.GONE);
        mForgotPasswordLayout.setVisibility(View.VISIBLE);
    }

    /**
     * displays pin view for a user to enter 4-digit code that verifies his/her request for password reset
     * @param email user's e-mail address to which a newly generated 4-digit code will be sent
     */
    @Override
    public void openResetPin(String email) {
        mForgotPasswordLayout.setVisibility(View.GONE);
        mResetCodeLayout.setVisibility(View.VISIBLE);
        mCodeSentAlert.setText(String.format(getString(R.string.text_code_sent_alert), email));
        mResendCodeText.setVisibility(View.VISIBLE);
    }

    /**
     * displays a confirmation alert that a reset code has been resent upon request
     */
    @Override
    public void resendResetCode() {
        showMessage(getString(R.string.text_code_resent_alert));
    }

    /**
     * displays fields to input new password and confirm the new password
     */
    @Override
    public void newPasswordInput() {
        mResetCodeLayout.setVisibility(View.GONE);
        mResendCodeText.setVisibility(View.GONE);
        mNewPasswordLayout.setVisibility(View.VISIBLE);
    }

    /**
     * validates the new password and updates the details against the user's email id
     * @param email the user's email id for which the password is reset
     */
    @Override
    public void confirmPasswordReset(String email) {
        String newPassword = mNewPasswordReset.getText().toString();
        String confirmNewPassword = mConfirmNewPasswordReset.getText().toString();
        if (validatePassword(newPassword)) {
            if (confirmNewPassword.equals(newPassword)) {
                mLoginPresenter.ok_password_reset(email, mOtpCode, newPassword, mHandler);
            }
        }
    }

    public void setLoginEmail(String email) {
        email_login.setText(email);
    }

    public void showMessage(String message) {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Validates the given email, checks if given email proper format as standard email string
     * @param email email string to be validate
     * @return Boolean returns true if email format is correct, otherwise false
     */
    public boolean validateEmail(String email) {
        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        if (!email.equals("") && matcher.matches()) {
            mInputLayoutEmailForgotPassword.setErrorEnabled(false);
            return true;
        } else {
            mInputLayoutEmailForgotPassword.setError(getString(R.string.invalid_email));
            return false;
        }
    }

    /**
     * Validates first name and last name of user, checks if these are empty or filled
     * @param firstname first name of user
     * @param lastname last name of user
     * @return Boolean returns true if both are filled, otherwise false
     */
    public boolean validateName(String firstname, String lastname) {
        if (!firstname.isEmpty() && !lastname.isEmpty())
            return true;
        else {
            if (firstname.isEmpty())
                mInputLayoutFirstNameSignup.setError(getString(R.string.empty_first_name));
            else
                mInputLayoutFirstNameSignup.setErrorEnabled(false);
            if (lastname.isEmpty())
                mInputLayoutLastNameSignup.setError(getString(R.string.empty_last_name));
            else
                mInputLayoutLastNameSignup.setErrorEnabled(false);
            return false;
        }
    }

    /**
     * Validates the given password, checks if given password proper format as standard password string
     * @param passString password string to be validate
     * @return Boolean returns true if email format is correct, otherwise false
     */
    public boolean validatePassword(String passString) {
        if (passString.length() >= 8) {
            return true;              
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.password_length, Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    /**
     * handles validating password reset confirmation code
     * @param otp code entered by the user
     */
    @Override
    public void onOtpCompleted(String otp) {
        mOtpCode = otp;
        mLoginPresenter.newPasswordInput();
    }

}
