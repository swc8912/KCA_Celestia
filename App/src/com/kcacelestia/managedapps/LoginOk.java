package com.kcacelestia.managedapps;

import com.kcacelestia.managedapps.network.Network;
import com.kcacelestia.managedapps.packet.Packet;
import com.managed.zxing.client.android.CaptureActivity;
import com.managed.zxing.client.android.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class LoginOk extends Activity {

	public String s_dns1;
	public String s_dns2;
	public String s_gateway;
	public String s_ipAddress;
	public String s_leaseDuration;
	public String s_netmask;
	public String s_serverAddress;
	public static boolean lgCheck = false;

	TextView info;
	DhcpInfo d;
	WifiManager wifii;

	public String temp_gateway = "192.168.1.1";
	public String temp_serverAdress = "192.168.1.1";

	// 통상적으로 게이트웨이와 서버아이피는 같음.일반 가정에서도 가상ip를 통해 악용할수 있으니 보완이 시급
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginok);
		TextView tv = (TextView)findViewById(R.id.textView10);
		tv.setText("Connector ID : " + LoginPage.IdNum);
	}

	@Override
	public void onBackPressed() {
		Toast awesomeToast = Toast.makeText(this, "로그아웃 버튼을 눌러주세요.",
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

	public void logOut(View v) {
		//로그아웃 버튼
		LoginPage lg = new LoginPage();
		Packet packet = new Packet();
		packet.setCmd("logout");
		packet.setRegnum(lg.IdNum);
		
		Network.SendPacket(packet);
		//로그아웃 기능
		//소켓과 연결을 끊고 앱의 프로세스를 종료
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
	}
	public void qrStart(View v) {
		  //qr스캐너 실행 버튼
		  //지정된 와이파이 검사 버튼
		
		Intent qrIntent = new
				  Intent(this, CaptureActivity.class); startActivity(qrIntent);
				  finish();
/*				  
	 시연의 편리함을 위하여 와이파이 체크를 주석처리 하였으므로
	 혹시라도 사용하실 경우 주석을 해제하고
		    temp_gateway
			temp_serverAdress
	 위 두 변수를 허용하실 와이파이에 맞게 수정하여 접속 하셔야함을 유의 하시길 바랍니다.
*/				  
				  
//		  NetWorkCheck(); dhcpInfo();
//		  
//		  if (2 == NetWorkCheck()) { if (s_gateway.equals(temp_gateway) &&
//		  s_serverAddress.equals(temp_serverAdress)) { Intent qrIntent = new
//		  Intent(this, CaptureActivity.class); startActivity(qrIntent);
//		  finish(); } else { Toast MbFuck = Toast.makeText(this,
//		  "지정된 wifi가 아닙니다.", Toast.LENGTH_LONG); MbFuck.show(); } } else if (1
//		  == NetWorkCheck()) { Toast MbFuck = Toast.makeText(this,
//		  "모바일네트워크입니다, WIFI쓰세요", Toast.LENGTH_LONG); MbFuck.show(); }
//			
	}

	public int NetWorkCheck() {
		//와이파이와 모바일 네트워크 체크 기능
		int MB = 1;
		int WF = 2;
		int NoNet = 0;
		ConnectivityManager cons = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		if (cons.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
			// Toast moBiles = Toast.makeText(this, "모바일",
			// Toast.LENGTH_LONG);
			// moBiles.show();
			return MB;
		}

		else if (cons.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			// Toast WiFis = Toast.makeText(this, "와이파이",
			// Toast.LENGTH_SHORT);
			// WiFis.show();
			return WF;
		} else {
			// Toast NoNetwork = Toast.makeText(this, "인터넷이 연결되어있지 않습니다.",
			// Toast.LENGTH_LONG);
			// NoNetwork.show();
			return NoNet;
		}
	}

	public void dhcpInfo() {

		wifii = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		d = wifii.getDhcpInfo();

		s_dns1 = FormatIP(d.dns1);
		s_dns2 = FormatIP(d.dns2);
		s_gateway = FormatIP(d.gateway);
		s_ipAddress = FormatIP(d.ipAddress);
		s_leaseDuration = FormatIP(d.leaseDuration);
		s_netmask = FormatIP(d.serverAddress);
		s_serverAddress = FormatIP(d.serverAddress);
	}

	public String FormatIP(int IpAddress) {
		return Formatter.formatIpAddress(IpAddress);
	}

	public void DataStu(View v) {
		//학생정보 호출 버튼
		Intent intent = new Intent(LoginOk.this, StudentData.class);
		startActivity(intent);
	}
}

