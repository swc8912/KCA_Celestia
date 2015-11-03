package com.kcacelestia.managedapps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kcacelestia.managedapps.network.Network;
import com.kcacelestia.managedapps.network.RecvActivity;
import com.kcacelestia.managedapps.packet.Packet;
import com.managed.zxing.client.android.R;

public class MainPage extends Activity {

	//public final static String TAG = "ManagedApps";
	protected static Context context;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mainpage);
	    /*
	    로그인 체크
	    혹시라도 로그인 된 상태에서 앱이 홈으로 넘어갔을때 재 실행시
	    소켓과 연결이 되어있는 상태면 저장된 로그인 정보를 확인
	    바로 엑티비티 전환
	    */
	    LoginOk lg = new LoginOk();
	    if(lg.lgCheck == true){
	    	Intent intent = new Intent(this, LoginOk.class);
	    	Log.e("1111","loginok intent start");
	    	startActivity(intent);
	    	finish();
	    }
	} 
	public void login_Page(View v){
		/*로그인 페이지 전환 버튼*/
		Intent loginPageIntent	=	new Intent(this, RecvActivity.class);
		Log.e("1111","recvactivity intent start");
		startActivity(loginPageIntent);
		finish();
	}
	public void CloseBtn(View v){
		/*앱 종료버튼*/
		moveTaskToBack(true);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	//서버 체크
	public static Handler Handler=new Handler(){
//		String cmd="";
//		public LoginPage lg=(LoginPage) LoginPage.context;
		
		@Override
		public void handleMessage(Message msg){
			try {
				Toast toast;
				//Toast toast=new Toast(LoginPage.context);
				switch (msg.what) {
//					case Network.SEND_MSG:
//						Network.sendMsgQueue.offer((Packet)msg.obj);
//						LoginPage.printToast("패킷보내는 중");
//						Log.i(LoginPage.TAG, "sendpacket in handler");
//						break;
//					case Network.RECV_MSG:
//						Network.recvPacket = Network.recvMsgQueue.poll();
//						cmd = Network.recvPacket.getCmd();
//						Log.i(LoginPage.TAG,"handler recv cmd: " + cmd);
//						
//						if (cmd.equals("loginok") || cmd.equals("loginfail")) {
//							Log.i(LoginPage.TAG,"handler cmd: " + cmd);
//							//Network.passPacket=Network.recvPacket;
//							//lg.login_check(Network.recvPacket);
////							Intent loginOk = new Intent(LoginPage.context, LoginOk.class);
////							Log.i(LoginPage.TAG,"loginOk: " + loginOk.toString());
////							startActivity(loginOk);
////							LoginPage.context.notify();
////							Message message = LoginPage.loginHandler.obtainMessage();
////							message.what = LoginPage.LOGINPAGE;
////							message.obj = Network.recvPacket;
////							MainPage.Handler.sendMessage(message);
//							Log.i(LoginPage.TAG,"logincheck ok");
//							
//						} else if (cmd.equals("message")) {
//							Log.i(LoginPage.TAG,"message");
//						}
//	
//						break;
					case Network.MSG_PRINT:
						toast=Toast.makeText(LoginPage.context, (String)msg.obj,
						 Toast.LENGTH_SHORT);
						toast.show();
						Log.e("1111", "toast called: " + (String)msg.obj);
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("1111","handler exception");
			}
		}
	};
}
