package com.example.ming.youdianmusic.login_model;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ming.youdianmusic.MainActivity;
import com.example.ming.youdianmusic.MyApp;
import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.MusicUser;
import com.example.ming.youdianmusic.util.JniUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class LoginActivity extends Activity implements OnClickListener {
    EditText loginUsername, loginPassword;
    TextView logInfo;
    Button btLogin, btRegister;
    String ip = "192.168.235.21";
    private Handler mHandler;

    public static final String SP_INFOS = "SPDATA_Files";
    public static final String USERNAME = "UserName";
    public static final String PASSWORD = "PassWord";
    private static CheckBox rememberme;

    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApp = (MyApp) getApplication();
        loginUsername = (EditText) this.findViewById(R.id.login_username);
        loginPassword = (EditText) this.findViewById(R.id.login_pwd);
        logInfo = (TextView) this.findViewById(R.id.login_err);
        btLogin = (Button) this.findViewById(R.id.loginbt_login);
        btRegister = (Button) this.findViewById(R.id.loginbt_reg);
        rememberme = (CheckBox) this.findViewById(R.id.login_remenberme);
        checkIfRemember();  //从SharedPreferences中读取用户的帐号和密码
        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                boolean isUser = (Boolean) msg.obj;
                if (!isUser) {
                    Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    rememberMe(loginUsername.getText().toString().trim(),
                            loginPassword.getText().toString().trim());
                    rememberme.setChecked(true);
                    // 若登录成功跳转到另一个activity，加入如下代码
                    Bundle bundle = new Bundle();
                    bundle.putString("username", loginUsername.getText().toString());
                    Intent intent1 = new Intent(LoginActivity.this,
                            MainActivity.class);
                    myApp.isLogin = true;
                    myApp.userName = loginUsername.getText().toString();
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
            }
        };
    }

    // 方法：将用户的id和密码存入SharedPreferences
    public void rememberMe(String uname, String pwd) {
        SharedPreferences sp = getSharedPreferences(SP_INFOS, MODE_PRIVATE);
        // 获得Preferences
        SharedPreferences.Editor editor = sp.edit(); // 获得Editor
        editor.putString(USERNAME, uname); // 将用户的帐号存入Preferences
        editor.putString(PASSWORD, pwd); // 将密码存入Preferences
        editor.commit();
    }

    // 方法：从SharedPreferences中读取用户的帐号和密码
    private void checkIfRemember() {
        SharedPreferences sp = getSharedPreferences(SP_INFOS, MODE_PRIVATE);
        // 获得Preferences
        String uname = sp.getString(USERNAME, null); // 取Preferences中的帐号
        String pwd = sp.getString(PASSWORD, null); // 取Preferences中的密码
        if (uname != null && pwd != null) {
            this.loginUsername.setText(uname); // 给EditText控件赋帐号
            this.loginPassword.setText(pwd); // 给EditText控件赋密码
//			rememberme.setChecked(true);

        }
    }


    public boolean login(MusicUser musicUser) {
        boolean result = false;
        URL url = null;
        try {
            url = new URL("http://" + ip
                    + ":8080/YDMusic/LoginServlet");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");// 设置为post方法
            DataOutputStream outobj = new DataOutputStream(
                    connection.getOutputStream());

            String data = "password=" + URLEncoder.encode(musicUser.getPassword(), "UTF-8") +
                    "&username=" + URLEncoder.encode(musicUser.getUsername(), "UTF-8");
            outobj.writeBytes(data);
            outobj.flush();
            outobj.close();

            InputStreamReader isr = new InputStreamReader(
                    connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line = null;
            String inrest = "";
            while ((line = bufferedReader.readLine()) != null) {
                inrest += line;
            }
            bufferedReader.close();
            connection.disconnect();

            Log.e("inrest", inrest);
            if (inrest.equals("OK")) {
                result = true;
            }
        } catch (Exception e) {
            this.logInfo.setText(e.toString());
            e.printStackTrace();
        } finally {

        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginbt_login:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String uname = loginUsername.getText().toString()
                                .trim();
                        String pwd = loginPassword.getText().toString()
                                .trim();

                        Boolean isUser = login(new MusicUser(uname, pwd, null));
                        Log.e("isUser", isUser.toString());
                        Message msg = Message.obtain();
                        msg.obj = isUser;
                        mHandler.sendMessage(msg);
                    }
                }).start();
                break;
            case R.id.loginbt_reg:
                String pwd = loginPassword.getText().toString()
                        .trim();
                String enPwd = JniUtil.encryptPassword(pwd);
                Toast.makeText(myApp, enPwd, Toast.LENGTH_SHORT).show();
//                Intent intent1 = new Intent(LoginActivity.this,
//                        RegisterActivity.class);
//                startActivity(intent1);
                break;
        }
    }
}
