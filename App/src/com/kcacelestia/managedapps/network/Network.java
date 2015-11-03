package com.kcacelestia.managedapps.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import org.json.simple.JSONObject;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kcacelestia.managedapps.LoginPage;
import com.kcacelestia.managedapps.MainPage;
import com.kcacelestia.managedapps.packet.Lecture;
import com.kcacelestia.managedapps.packet.Packet;

@SuppressWarnings("unchecked")
public class Network {
	public static Socket socket=null;
	public static JSONObject json=new JSONObject();
	public static BufferedReader reader=null;
	public static PrintWriter writer=null;
	public static boolean runningMessageReceiver=false;
	public static boolean runningMessageSender=false;
	public static Packet sendPacket=new Packet();
	public static Packet recvPacket=new Packet();
	public static Message message=null;
	public static final int SEND_MSG=0;		
	public static final int RECV_MSG=1;
	public static final int MSG_PRINT=2;
	public static Queue<Packet> sendMsgQueue=new LinkedList<Packet>();
	public static Queue<Packet> recvMsgQueue=new LinkedList<Packet>();
	private static Packet passPacket=new Packet();
	 
	private static String cmd="";
	private static String regnum="";
	private static String passwd="";
	private static Lecture lec;
	private static String qrcode="";
	private static int surprise;
	private static int leccode;
	private static String Url;
	private static int force;
	
	public boolean ConnectToServer(String ipaddr,int port){
		try{			
			ipaddr="14.63.223.201";
			//ipaddr="203.252.146.106";
			//ipaddr="192.168.1.2";
			port=8124;
			socket=new Socket(ipaddr,port);
			if(socket.isConnected()){
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				System.out.println("server connected");
				return true;
			}else{
				return false;
			}
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return false;
//		finally {
//			try {
//				System.out.println("socket close");
//				socket.close();
//			}catch(Exception e) {
//				System.out.println("err: " + e.getMessage());
//			}
//		}

	}
	
	public static Packet InitPacket(Packet packet){
		packet.setCmd("blank");
		packet.setRegnum("");
		packet.setPasswd("");
		packet.setQrcode("");
		packet.setLeccode(0);
		packet.setSurprise(0);
		packet.setUrl("");
		packet.setLec(null);
		
		Log.e("1111","initpacket");
		return packet;
	}
	
	public static void SendPacket(Packet sendPacket){
		Log.e("1111", "network sendpacket cmd: " + sendPacket.getCmd());
		Log.e("1111", "network sendpacket regnum: " + sendPacket.getRegnum());
		Log.e("1111", "network sendpacekt passwd: " + sendPacket.getPasswd());
		
//		Message message = MainPage.Handler.obtainMessage();
//		message.what = SEND_MSG;
//		message.obj = sendPacket;
//		MainPage.Handler.sendMessage(message);
//		Log.i(LoginPage.TAG,"sendpakcet handler called");
		
		cmd=sendPacket.getCmd();
		regnum=sendPacket.getRegnum();
		passwd=sendPacket.getPasswd();
		lec=sendPacket.getLec();
		qrcode=sendPacket.getQrcode();
		surprise=sendPacket.getSurprise();
		leccode=sendPacket.getLeccode();
		force=sendPacket.getForce();
		
		Log.e("1111","cmd: " + cmd);
		Log.e("1111","regnum: " + regnum);
		Log.e("1111","passwd: " + passwd);
		
		// json으로 패킷 내용 변경
		// cmd 입력
		Network.json.put("cmd", cmd);
		if(force==1){
			Network.json.put("force", force);
		}
		
		if(regnum!=null && !regnum.equals("")){
			Network.json.put("regnum", regnum);
		}
		if(passwd!=null && !passwd.equals("")){
			Network.json.put("passwd", passwd);
		}
		if(lec!=null && !lec.getProfessor().equals("")){
			//json.put("lec", lec);
		}
		if(qrcode !=null && !qrcode.equals("")){
			Network.json.put("qrcode", qrcode);
		}
		if(surprise>0 || surprise<1000){
			Network.json.put("surprise", surprise);
		}
		if(leccode!=0){
			Network.json.put("leccode", leccode);
		}
		
		

		String jsonStr = Network.json.toString();
		Network.writer.println(jsonStr);
		Network.writer.flush();

		LoginPage.printToast("패킷 전송");
		Log.e("1111", "jsonStr write");
		
		InitPacket(Network.sendPacket);
	}
	
	
//	public static Packet RecvPacket(){
//		String jsonStr="";
//		try {
//			jsonStr = Network.reader.readLine();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		JSONObject js = (JSONObject) JSONValue.parse(jsonStr);
//
//		Log.i(LoginPage.TAG, "jsonStr: " + jsonStr);
//		if (jsonStr == null) {
//			Log.i(LoginPage.TAG, "jsonStr is null");
//		} else {
//			cmd = js.get("cmd").toString();
//			Log.i(LoginPage.TAG, "recved cmd: " + cmd);
//			Network.recvPacket.setCmd(cmd);
//			if (cmd.equals("surprise"))
//				Network.recvPacket.setSurprise(Integer.parseInt(js.get("surprise").toString()));
//			//Network.recvMsgQueue.offer(Network.recvPacket);
//			else if(cmd.equals("info")){
//				Network.recvPacket.setUrl(js.get("url").toString());
//			}
//			
//			passPacket=Network.recvPacket;
//		}
//		return passPacket;
//	}
//}
	

	public static Packet RecvPacket(String cmd1, String cmd2) {
		Log.e("1111", "RecvPacket2 called");
		Log.e("1111", "in RecvPacket2 cmd1: " + cmd1);
		Log.e("1111", "in RecvPacket2 cmd2: " + cmd2);
		
		while (true) {
			try {
				// Log.i(LoginPage.TAG,"RecvPacket2 loop");
				Network.recvPacket = Network.recvMsgQueue.poll();
				cmd = Network.recvPacket.getCmd();
				if (cmd == null) {
					Log.e("1111", "RecvPacket2 sleep");
					Thread.sleep(2000);
					continue;
				}
				Log.e("1111", "in RecvPacket2 cmd: " + cmd);
				
				if (cmd.equals(cmd1) || cmd.equals(cmd2)) {
					passPacket = Network.recvPacket;
					Log.e("1111", "recv ok and break loop");
					break;
				} else {
					Network.recvMsgQueue.offer(Network.recvPacket);
				}
				Thread.sleep(2000);
			} catch (Exception e) {
				// e.printStackTrace();
				// Log.e(LoginPage.TAG,"in RecvPacket2 exception: " +
				// e.getMessage());
			}
			
		}
		
		// InitPacket(Network.recvPacket);
		return passPacket;
	}
	
}

