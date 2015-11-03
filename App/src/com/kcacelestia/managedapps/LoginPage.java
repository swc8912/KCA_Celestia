package com.kcacelestia.managedapps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kcacelestia.managedapps.network.Network;
import com.kcacelestia.managedapps.packet.Packet;
import com.managed.zxing.client.android.R;

public class LoginPage extends Activity {
	//private String serverIP = "14.63.223.201";
	private String serverIP = "192.168.1.2";
	private int serverPort = 8124;
//	public final static String TAG = "ManagedApps";
	private String IdText = "";
	private String PwText = "";
	private String cmd = "";
	public final static int LOGINPAGE = 3;
	public static String IdNum = "";
	public static String surprisenum="";

	private Network net = new Network();

	public static Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		onWindowFocusChanged(true);
		// 서버 on, off 여부에 따라 엑티비티 전환
		LoginOk lg = new LoginOk();
		if (lg.lgCheck == false) {
			if (net.ConnectToServer(serverIP, serverPort)) {
				printToast("서버 접속");
				Log.e("1111", "server connected");
			} else {
				printToast("서버 접속불가");
				Log.e("1111", "server not connected");
				Intent intent = new Intent(this, MainPage.class);
				startActivity(intent);
				finish();
			}
		} else {
			Intent intent = new Intent(this, LoginOk.class);
			startActivity(intent);
			finish();
		}

		// Network.runningMessageReceiver = true;
		// PacketReceiver mr = new PacketReceiver();
		// final Thread threadReceiver = new Thread(mr);
		// threadReceiver.setDaemon(true);
		// threadReceiver.start();
		// printToast("리시브 스레드 시작");
		// Log.i(TAG, "recv thread start");

		context = this;
	}

	public void login_Click(View v) {
		// 로그인 버튼
		Packet packet = new Packet();

		EditText IdEt = (EditText) findViewById(R.id.IdText);
		EditText PwEt = (EditText) findViewById(R.id.PwText);
		IdText = IdEt.getText().toString();
		PwText = PwEt.getText().toString();

		if (IdText == null) {
			printToast("ID를 입력 해 주세요.");
			return;
		}
		if (PwText == null) {
			printToast("PW를 입력 해 주세요.");
			return;
		}

		packet.setCmd("login");
		packet.setRegnum(IdText);
		packet.setPasswd(PwText);

		Log.e("1111", "login cmd: " + packet.getCmd());
		Log.e("1111", "login regnum: " + packet.getRegnum());
		Log.e("1111", "login passwd: " + packet.getPasswd());

		Network.SendPacket(packet);
		login_check(Network.RecvPacket("loginok", "loginfail"));
	}

	public void login_check(Packet packet) {
		// 로그인 버튼 입력시 계정 확인
		cmd = packet.getCmd();
		Log.e("1111", "cmd: " + cmd);
		if (cmd.equals("loginok")) {
			try {
				LoginOk lg = new LoginOk();
				lg.lgCheck = true;
				IdNum = IdText;
				Intent loginOk = new Intent(LoginPage.context, LoginOk.class);
				Log.e("1111", "loginOk: " + loginOk.toString());
				startActivity(loginOk);
				finish();
			} catch (NullPointerException e) {
				Log.e("1111", "exception: " + e.getMessage());
				e.printStackTrace();
			}

		} else if (cmd.equals("loginfail")) {
			IdNum = "";
			printToast("ID 혹은 PW가 다릅니다..");
		}
	}

	public void CloseBtn2(View v) {
		// 종료 버튼
		try {
			Network.socket.close();
			IdNum = "";
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
			moveTaskToBack(true);
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("socket ", "CloseBtn");
		}
	}

	public static void printToast(String data) {
		if (data != null && data.length() > 0) {
			Message message = MainPage.Handler.obtainMessage();
			message.what = Network.MSG_PRINT;
			message.obj = data;
			MainPage.Handler.sendMessage(message);
		}
	}

	// class PacketReceiver extends Thread {
	// private String cmd = "";
	// private String url = "";
	// public boolean a = false;
	// private Packet rPacket = new Packet();
	//
	// private void putWhiteList(String white) {
	// com.kcacelestia.managedapps.BackGroundControl.filter().add(white);
	// }
	//
	// public void run() {
	// while (Network.runningMessageReceiver) {
	// if (Network.socket == null)
	// continue;
	// if (Network.reader == null) {
	// // Log.i(LoginPage.TAG, "reader is null");
	// }
	//
	// try { // 리시브 패킷 받는부분
	// String jsonStr = Network.reader.readLine();
	//
	// if (jsonStr == null) {
	// // Log.i(LoginPage.TAG, "jsonStr is null");
	// try {
	// Thread.sleep(2000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// } else {
	// JSONObject js = (JSONObject) JSONValue.parse(jsonStr);
	// Log.i(LoginPage.TAG, "jsonStr: " + jsonStr);
	// cmd = js.get("cmd").toString();
	// Log.i(LoginPage.TAG, "recved cmd: " + cmd);
	// Network.InitPacket(rPacket);
	// rPacket.setCmd(cmd);
	// if (cmd.equals("info"))
	// rPacket.setUrl(url);
	// else if (cmd.equals("classstart")) { // 제어 실행 부분
	// // 화이트리스트 추가
	// String temp = js.get("white").toString();
	// Log.e("asdf", temp);
	// // 리플레이스로 필요없는 문자 없애기
	// temp = temp.replaceAll("\"", "");
	// temp = temp.replaceAll("package:", "");
	// temp = temp.replace('[', ' ');
	// temp = temp.replace(']', ' ');
	// temp = temp.trim();
	// Log.e("asdf", temp);
	//
	// // 화이트리스트 뽑아내서 넣기
	// int first, second;
	// for (int i = 0; i < temp.lastIndexOf('}'); i = second) {
	// first = temp.indexOf('{', i);
	// second = temp.indexOf('}', first + 1);
	// Log.e("좌 표 : ",
	// first
	// + " "
	// + second
	// + ", 패 키 지 이 름 : "
	// + temp.substring(first + 1,
	// second));
	// putWhiteList(temp.substring(first + 1, second));
	// }
	// // 서비스 실행
	// Intent startControl = new Intent(LoginPage.this,
	// BackGroundControl.class);
	// startService(startControl);
	// } else if (cmd.equals("classend")) { // 제어 종료
	// Intent stopControl = new Intent(LoginPage.this,
	// BackGroundControl.class);
	// stopService(stopControl);
	// } else if (cmd.equals("message")) {
	//
	// } else if (cmd.equals("qrcodeok")
	// || cmd.equals("qrcodefail")) {
	//
	// }
	//
	// Network.recvMsgQueue.offer(rPacket);
	//
	// }
	// } catch (IOException e) {
	// LoginPage.printToast(e.getMessage());
	// } catch (NullPointerException e) {
	// LoginPage.printToast(e.getMessage());
	// // Log.e(LoginPage.TAG, "read null error");
	// }
	// if (rPacket != null) {
	// // Network.message = MainPage.Handler.obtainMessage();
	// // Network.message.what = Network.RECV_MSG;
	// // //Network.message.obj=Network.recvPacket;
	// // MainPage.Handler.sendMessage(Network.message); // 핸들러를
	// // 사용하여
	// // 스레드에 메세지 전송
	// }
	// if (rPacket == null) {
	// LoginPage.printToast("접속 끊김");
	// } else {
	// try {
	// Thread.sleep(5000);
	// } catch (InterruptedException e) {
	// LoginPage.printToast(e.getMessage());
	// }
	// }
	// }
	// }
	// }

	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView img = (ImageView) findViewById(R.id.ImageView2);
		AnimationDrawable ani;

		ani = getAnimation();
		img.setBackgroundDrawable(ani);
		ani.start();
	}

	public AnimationDrawable getAnimation() {
		AnimationDrawable ani = new AnimationDrawable();
		BitmapDrawable[] frame = new BitmapDrawable[17];

		frame[0] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white1_m);
		frame[1] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white2_m);
		frame[2] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white3_m);
		frame[3] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white4_m);
		frame[4] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white5_m);
		frame[5] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white6_m);
		frame[6] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white7_m);
		frame[7] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white8_m);
		frame[8] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white9_m);
		frame[9] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white10_m);
		frame[10] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white11_m);
		frame[11] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white12_m);
		frame[12] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white13_m);
		frame[13] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white14_m);
		frame[14] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white15_m);
		frame[15] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white16_m);
		frame[16] = (BitmapDrawable) getResources().getDrawable(
				R.drawable.windmill_white17_m);

		for (int x = 0; x < 17; x++) {
			ani.addFrame(frame[x], 35);
		}

		ani.setOneShot(false);
		return ani;
	}

}
