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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SignupActivity extends AppCompatActivity implements ICallback {

    private EditText inputEmail, inputPassword, inputUsername;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, SignupActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                int errors = 0;

                if (TextUtils.isEmpty(username)) {
                    inputUsername.setError(getString(R.string.empty_username));
                    errors ++;
                }

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
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);

                RestClient.post("user/create", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
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
                case IConstants.USER_CREATED_SUCCESSFULLY:
                    Toast.makeText(SignupActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                    break;
                case IConstants.USER_ALREADY_EXISTED:
                    Toast.makeText(SignupActivity.this, getString(R.string.user_already_exists), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.AUTHORIZATION_FAIL:
                    Toast.makeText(SignupActivity.this, getString(R.string.authorization_fail), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.API_SQL_FAIL:
                    Toast.makeText(SignupActivity.this, getString(R.string.api_sql_fail), Toast.LENGTH_LONG).show();
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