package com.kcacelestia.managedapps;

import com.kcacelestia.managedapps.network.Network;
import com.kcacelestia.managedapps.packet.Packet;
import com.managed.zxing.client.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class StudentData extends Activity {
	private String cms;
	private String DataUrl="";
	private String IdText;
	private int le = 2222;
	Button btnBack, btnForward, btnNew;
	WebView mWebView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.studentdata);
	    
	    LoginPage lg = new LoginPage();
	    Packet packet = new Packet();
	    
	    IdText = lg.IdNum;
	    
	    packet.setCmd("info");
	    packet.setRegnum(IdText);
	    packet.setLeccode(le);
	    
	    Network.SendPacket(packet);
	    stuData(Network.RecvPacket("info","info"));
	    
	}
	
	public void stuData(Packet packet){
		//학생정보 웹뷰
		btnBack = (Button) findViewById(R.id.btnback);
		btnForward = (Button) findViewById(R.id.btnforward);
		btnNew	=	(Button) findViewById(R.id.btnnew);
		
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new HelloWebViewClient());
		
		WebSettings set = mWebView.getSettings();
		
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(true);
		
		DataUrl = packet.getUrl();
		Log.e("1111","DataUrl: " + DataUrl);
	    mWebView.loadUrl(DataUrl);

		btnBack.setOnClickListener(new OnClickListener() {
			//페이지 뒤로 버튼
			@Override
			public void onClick(View v) {
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				}
			}
		});

		btnForward.setOnClickListener(new OnClickListener() {
			//페이지 앞으로 버튼
			@Override
			public void onClick(View v) {
				if (mWebView.canGoForward()) {
					mWebView.goForward();
				}
			}
		});
		btnNew.setOnClickListener(new OnClickListener() {
			//새로고침 버튼
			@Override
			public void onClick(View v) {
				mWebView.reload();
			}
		});
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
