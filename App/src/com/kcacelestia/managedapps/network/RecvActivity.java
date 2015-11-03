package com.kcacelestia.managedapps.network;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kcacelestia.managedapps.BackGroundControl;
import com.kcacelestia.managedapps.ControlStart;
import com.kcacelestia.managedapps.LoginPage;
import com.kcacelestia.managedapps.MainPage;
import com.kcacelestia.managedapps.packet.Packet;
import com.managed.zxing.client.android.R;

public class RecvActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
		Network.runningMessageReceiver = true;
		PacketReceiver mr = new PacketReceiver();
		final Thread threadReceiver = new Thread(mr);
		threadReceiver.setDaemon(true);
		threadReceiver.start();
	//	LoginPage.printToast("리시브 스레드 시작");
		Log.e("1111", "recv thread start");
		Intent intent = new Intent(RecvActivity.this, LoginPage.class);
		startActivity(intent);
	}

	class PacketReceiver extends Thread {

		private String cmd = "";
		private String url = "";
		public boolean a = false;
		private Packet rPacket = new Packet();

		private void putWhiteList(String white) {
			com.kcacelestia.managedapps.BackGroundControl.filter().add(white);
		}

		public void run() {
			while (Network.runningMessageReceiver) {
				if (Network.socket == null)
					continue;
				if (Network.reader == null) {
					// Log.i(LoginPage.TAG, "reader is null");
				}

				try { // 리시브 패킷 받는부분
					String jsonStr = Network.reader.readLine();
					Log.e("1111", "recved jsonStr: " + jsonStr);
					if (jsonStr == null) {
						// Log.i(LoginPage.TAG, "jsonStr is null");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						JSONObject js = (JSONObject) JSONValue.parse(jsonStr);
						Log.e("1111", "jsonStr: " + jsonStr);
						cmd = js.get("cmd").toString();
						Log.e("1111", "recved cmd: " + cmd);
						Network.InitPacket(rPacket);
						rPacket.setCmd(cmd);
						if (cmd.equals("info")) 
							rPacket.setUrl(url);
						else if (cmd.equals("classstart")) { // 제어 실행 부분
							 // 화이트리스트 추가
							String temp = js.get("white").toString();
							Log.e("asdf",temp);
							// 리플레이스로 필요없는 문자 없애기
							temp = temp.replaceAll("\"", "");
							temp = temp.replaceAll("package:", "");
							temp = temp.replace('[', ' ');
							temp = temp.replace(']', ' ');
							temp = temp.trim();
							Log.e("asdf",temp);
							
							// 화이트리스트 뽑아내서 넣기
							int first, second;
							for (int i = 0; i < temp.lastIndexOf('}') ; i = second){
								first = temp.indexOf('{', i); 
								second = temp.indexOf('}', first+1);
								Log.e("좌 표 : ", first + " " + second + ", 패 키 지 이 름 : " + temp.substring(first+1,second));
								putWhiteList(temp.substring(first+1,second));
							}
							// 서비스 실행
							Intent startControl = new Intent(RecvActivity.this, BackGroundControl.class);
							startService(startControl);
						} else if (cmd.equals("classend")) { // 제어 종료
							Intent stopControl = new Intent(RecvActivity.this, BackGroundControl.class);
							stopService(stopControl);
						} else if (cmd.equals("message")) {

						} else if (cmd.equals("qrcodeok") || cmd.equals("qrcodefail")) {

						} else if (cmd.equals("forceok") || cmd.equals("forcefail")){
							if(cmd.equals("forceok")){
								try {
									// 소켓 클로즈
									Network.socket.close();
									// 제어 서비스 종료
									Intent stopControl = new Intent(RecvActivity.this, BackGroundControl.class);
									stopService(stopControl);
									// 프로세스 종료
									ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
									am.restartPackage(getPackageName());
									moveTaskToBack(true);
									android.os.Process.killProcess(android.os.Process.myPid());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							else{
								Toast.makeText(RecvActivity.this, "강제종료 신청이 거절되었습니다.", Toast.LENGTH_LONG).show();
							}
						}
						else if(cmd.equals("surprise")){ // 서버에서 값 도착 
							LoginPage.surprisenum=js.get("surprise").toString();
							Log.e("1111","surprisenum" + LoginPage.surprisenum);
						} 
						Network.recvMsgQueue.offer(rPacket);

					}
				} catch (IOException e) {
					LoginPage.printToast(e.getMessage());
				} catch (NullPointerException e) {
					LoginPage.printToast(e.getMessage());
					// Log.e(LoginPage.TAG, "read null error");
				}
				if (rPacket != null) {
					// Network.message = MainPage.Handler.obtainMessage();
					// Network.message.what = Network.RECV_MSG;
					// //Network.message.obj=Network.recvPacket;
					// MainPage.Handler.sendMessage(Network.message); // 핸들러를
					// 사용하여
					// 스레드에 메세지 전송
				}
				if (rPacket == null) {
					LoginPage.printToast("접속 끊김");
				} else {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						LoginPage.printToast(e.getMessage());
					}
				}
			}
		}
	}
}
