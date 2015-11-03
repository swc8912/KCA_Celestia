package com.kcacelestia.managedapps;

import java.util.ArrayList;

import com.managed.zxing.client.android.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BackGroundControl extends Service {
	// 백그라운드 컨트롤 실행여부
	private boolean isRunning;
	
	Thread mThread;
	
	// 화이트리스트 패키지 저장하는 공간
	private static ArrayList<String> mPackageFilter = new ArrayList<String>();
	
	// 외부에서 화이트리스트 접근하기 위해
	public static ArrayList<String> filter(){
		return mPackageFilter;
	}
	
	// 백그라운딩 패스할 앱인지 확인하는 boolean
	private static boolean mPassApp = false;
	
	// 로그 태깅용
	private final static String TAG = "App_log";

	public void runLog() { // 서비스 주소 내용
		Log.i(TAG, "runLog()");
		try {
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			Notification noti = new Notification(R.drawable.tray_icon, "앱잠금실행", System.currentTimeMillis());
			Context context = getApplicationContext( );  
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
			noti.setLatestEventInfo(this, "앱잠금", "실행중", contentIntent);
			nm.notify(1, noti);
			// 프로세스 검사 시작
			while (!isRunning) {
				// 1초마다 아래 명령어 실행
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				/************* 최상위 패키지 이름따기 ********/
				ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				RunningTaskInfo runningTaskInfo = activityManager.getRunningTasks(1).get(0);
				String topActivityName = runningTaskInfo.topActivity.getClassName();
				Log.i(TAG, topActivityName);
				/************* 최상위 패키지 이름따기 ********/
				
				// 화이트리스트 패키지 갯수만큼 반복
				for (int i = 0; i < mPackageFilter.size(); i++) {
					if (topActivityName.contains(mPackageFilter.get(i))) { // 필터에 있을때 아래 실행
						mPassApp = true;
						break; // 이미 true 로 바뀌었기 때문에 더이상의 검사는 무의미
					}
				}
				
				// 백그라운드로 날릴지 말지 검사
				if (!mPassApp) {
					back(); // 백그라운드!
					Log.i(TAG, "background");
					mPassApp = true;
				} else {
					mPassApp = false;
				}
			}
		} catch (Exception e) {
			Log.e(getPackageName(), e.toString());
		} finally {
			   NotificationManager mangager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE ) ;
			   mangager.cancel(1) ;  // 정지할 Notification의 아이디 */
		}
	}

	public void back() { // 백그라운드로 날리는 명령.
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
	
	Runnable worker = new Runnable() {
		public void run() {
			runLog();
		}
	};
	
	@Override
	public void onCreate() { // 서비스 생성시 호출되는 메소드
		super.onCreate();
		Toast.makeText(BackGroundControl.this, "제어를 시작합니다.", Toast.LENGTH_LONG).show();
		isRunning = false;
	}

	@Override
	public void onDestroy() { // 서비스 종료시 호출되는 메소드
		super.onDestroy();
		Toast.makeText(BackGroundControl.this, "제어를 종료합니다.", Toast.LENGTH_LONG).show();
		isRunning = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { // 실제 서비스 시작.
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "*********************** Service Start");
		mThread = new Thread(worker); // 스레드 생성
		mThread.setDaemon(true); // 데몬으로 설정
		mThread.start();
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
