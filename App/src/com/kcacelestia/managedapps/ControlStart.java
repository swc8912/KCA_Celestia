package com.kcacelestia.managedapps;

import java.util.Random;

import com.kcacelestia.managedapps.network.Network;
import com.kcacelestia.managedapps.network.RecvActivity;
import com.kcacelestia.managedapps.packet.Packet;
import com.managed.zxing.client.android.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ControlStart extends Activity {

	private String IdText = "";
	private int num;
	private String numString = "";
	// String cmds;

	private String cmd = "";

	static final int DIALOG_CUSTOM = 0;

	Dialog customDialogInstance;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_start);
	}

	@Override
	public void onBackPressed() {
		Toast awesomeToast = Toast
				.makeText(this, "수업중입니다.", Toast.LENGTH_SHORT);
		Toast awesomeToast2 = Toast.makeText(this, "수업에 집중해주세요.",
				Toast.LENGTH_SHORT);
		awesomeToast.show();
		awesomeToast2.show();
		// 해당 엑티비티에서 Back버튼을 눌러도 Back버튼이 동작되지 않도록 하는 메소드
	}

	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
		// 홈키를 제어하는 메소드
	}

	public void exitBtn(View v) {
		//강제종료 신청 기능의 미완성으로
		//로그아웃 기능 삽입
		LoginPage lg = new LoginPage();
		Packet packet = new Packet();
		packet.setCmd("logout");
		packet.setRegnum(lg.IdNum);
		try {
			Network.socket.close();
			LoginPage.IdNum = "";
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
			moveTaskToBack(true);
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("socket ", "logOut");
		}
		/*
		// 강제종료 신청 버튼
		Packet packet = new Packet();

		packet.setCmd("force");
		packet.setRegnum(LoginPage.IdNum);

		Network.SendPacket(packet);
		*/
	}

	public void numCheck(View v) {
		if (LoginPage.surprisenum.equals("")) {
			Toast.makeText(this, "중간 출석체크 시간이 아닙니다.", Toast.LENGTH_SHORT)
					.show();
		} else {
			CustomDialog3 customDialog3 = new CustomDialog3(ControlStart.this);
			customDialog3.show();
		}
	}

	// 중간 출석 다이얼로그
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		dialog = null;
		return dialog;
	}

	// ui 정의부분
	class CustomDialog3 extends Dialog implements OnClickListener {
		EditText number;
		Button okButton;
		Context mContext;

		public CustomDialog3(Context context) {
			super(context);
			

			mContext = context;
			/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			setContentView(R.layout.numberbox);
			
			number = (EditText) findViewById(R.id.dlgDisplayNum);
			okButton = (Button) findViewById(R.id.button1);
			okButton.setOnClickListener(this);
			
			setCanceledOnTouchOutside(false);
			/*
			 * 다이얼로그 박스 외부 화면을 터치 하였을때 다이얼로그 박스를 종료, 또는 박스 이외에는 터치가 안되고 다이얼로그
			 * 박스가 유지하도록 함
			 */
		}
		@Override
		public void onBackPressed() {
			Toast awesomeToast = Toast.makeText(ControlStart.this, "입력 하지 않으시면 출석체크가 되지 않습니다.",
					Toast.LENGTH_SHORT);
			awesomeToast.show();
			// 해당 엑티비티에서 Back버튼을 눌러도 Back버튼이 동작되지 않도록 하는 메소드
		}

		@Override
		public void onAttachedToWindow() {
			this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
			super.onAttachedToWindow();
			// 홈키를 제어하는 메소드
		}

		@Override
		public void onClick(View v) {
			onclick();
		}

		public void onclick() {
			// 중간 출석체크 다이얼로그 기능
			Network nt = new Network();
			Packet packet = new Packet();
			numString = LoginPage.surprisenum;
			Log.e("1111", "numString" + numString);

			if (TextUtils.isEmpty(number.getText())) {
				Toast.makeText(mContext, "숫자가 입력되지 않았습니다.", Toast.LENGTH_SHORT)
						.show();
			} else if (numString.equals(number.getText().toString())) {
				Toast.makeText(mContext, "숫자가 일치합니다.", Toast.LENGTH_SHORT)
						.show();
				packet.setCmd("surpriseok");
				packet.setRegnum(LoginPage.IdNum);
				LoginPage.surprisenum = "";
				dismiss();
			} else {
				Toast.makeText(mContext, "숫자가 일치하지 않습니다.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
