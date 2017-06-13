package games.whitetiger.beacarthief;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements ICallback {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private APIAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = new APIAuth(this);

        if (auth.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                int errors = 0;

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getString(R.string.empty_email));
                    errors ++;
                } else if (!Helper.isValidEmail(email)) {
                    inputEmail.setError(getString(R.string.non_valid_email));
                    errors ++;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError(getString(R.string.empty_password));
                    errors ++;
                } else if (password.length() < 6) {
                    inputPassword.setError(getString(R.string.minimum_password));
                    errors ++;
                }

                if (errors > 0) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                RequestParams params = new RequestParams();
                params.put("email", email);
                params.put("password", password);

                RestClient.post("user/login", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        handleJSONObject(response);
                    }
                }, getString(R.string.api_key));
            }
        });
    }

    @Override
    public void handleJSONObject(JSONObject object) {
        try {
            int responseCode = object.getInt("responseCode");

            switch (responseCode) {
                case IConstants.USER_LOGIN_SUCCESS:
                    JSONObject user = object.getJSONObject("user");
                    auth.saveLoggedInData(user);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
                case IConstants.USER_NOT_EXISTS:
                    Toast.makeText(LoginActivity.this, getString(R.string.user_not_exists), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.PASSWORD_NOT_CORRECT:
                    Toast.makeText(LoginActivity.this, getString(R.string.password_not_correct), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.USER_NOT_ENABLED:
                    Toast.makeText(LoginActivity.this, getString(R.string.user_not_enabled), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.USER_IS_BANNED:
                    Toast.makeText(LoginActivity.this, getString(R.string.user_is_banned), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.AUTHORIZATION_FAIL:
                    Toast.makeText(LoginActivity.this, getString(R.string.authorization_fail), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.API_SQL_FAIL:
                    Toast.makeText(LoginActivity.this, getString(R.string.api_sql_fail), Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}