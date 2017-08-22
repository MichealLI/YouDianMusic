
package com.example.ming.youdianmusic.register_model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ming.youdianmusic.R;
import com.example.ming.youdianmusic.common.MusicUser;
import com.example.ming.youdianmusic.login_model.LoginActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends Activity implements OnClickListener{

	EditText regUsername, regPassword,regTelnumber;
	TextView logInfo;
	Button btLogin, btRegister;
	String ip = "192.168.235.21";
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		regUsername = (EditText)this.findViewById(R.id.register_username);
		regPassword = (EditText)this.findViewById(R.id.register_pwd);
		regTelnumber = (EditText)this.findViewById(R.id.register_telnumber);
		logInfo = (TextView)this.findViewById(R.id.reg_err);
		btLogin = (Button)this.findViewById(R.id.registerbt_login);
		btRegister = (Button)this.findViewById(R.id.registerbt_reg);
		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				boolean isAddUser = (Boolean) msg.obj;
				if (!isAddUser){
					logInfo.setText("注册失败！");
				} else {
					logInfo.setText("注册成功！");
					// 若登录成功跳转到另一个activity，加入如下代码
					Bundle bundle = new Bundle();
					bundle.putString("username", regUsername.getText().toString());
					Intent intent1 = new Intent(RegisterActivity.this,
							LoginActivity.class);
					intent1.putExtras(bundle);
					startActivity(intent1);
				}
			}
		};
	}


	public boolean register(MusicUser musicUser){

		boolean result = false;
		URL url = null;
		try {
			url = new URL("http://" + ip
					+ ":8080/YDMusic/RegisterServlet");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setRequestMethod("POST");// 设置为post方法
			DataOutputStream outobj = new DataOutputStream(
					connection.getOutputStream());

			String data = "password="+ URLEncoder.encode(musicUser.getPassword(),"UTF-8")+
					"&username="+URLEncoder.encode(musicUser.getUsername(),"UTF-8")+
					"&telnumber="+URLEncoder.encode(musicUser.getTelnumber(),"UTF-8");
			outobj.writeBytes(data);
			outobj.flush();
			outobj.close();

			InputStreamReader isr = new InputStreamReader(
					connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(isr);
			String line = null;
			String inrest = "";
			while((line=bufferedReader.readLine())!=null){
				inrest+=line;
			}
			bufferedReader.close();
			connection.disconnect();
			Log.e("inrest", inrest);
			if (inrest.equals("OK")){
				result=true;
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
		switch(v.getId()){
			case R.id.registerbt_reg:
				new Thread(new Runnable() {
					@Override
					public void run() {
						String uname = regUsername.getText().toString()
								.trim();
						String pwd = regPassword.getText().toString()
								.trim();
						String tel = regTelnumber.getText().toString()
								.trim();
						Boolean isUser= register(new MusicUser(uname, pwd,tel));
						Log.e("isUser", isUser.toString());
						Message msg = Message.obtain();
						msg.obj = isUser;
						mHandler.sendMessage(msg);
					}
				}).start();
				break;
			case R.id.registerbt_login:
				Intent intent1 = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent1);
				break;
		}
	}
}
