package network.tcp;

import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;



public class TcpClient implements Runnable{
    private String TAG = "TcpClient";
    private String  serverIP;
    private int serverPort;
    private PrintWriter mPrintWriter;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private DataInputStream mDataInputStream;
    private boolean isRun = true;
    private Socket mSocket = null;
    byte buff[]  = new byte[4096];      // 接收的缓存

    private int receiveMsgLen;

    private TcpClient.IReceiverListener receiverListener;

    public TcpClient(String ip , int port, TcpClient.IReceiverListener receiverListener){
        this.serverIP = ip;
        this.serverPort = port;
        this.receiverListener = receiverListener;
    }

    public void closeSelf(){
        isRun = false;
    }

    public void send(String msg){
        mPrintWriter.println(msg);
        mPrintWriter.flush();
    }

    public void sendBytes(byte[] bytes) {
        try {
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Log.e(TAG, "开始连接");
        try {
            // 初始化
            mSocket = new Socket(serverIP,serverPort);
            mSocket.setSoTimeout(5000);
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            mDataInputStream = new DataInputStream(mInputStream);
            mPrintWriter = new PrintWriter(mOutputStream, true);
            Log.e(TAG, "连接成功");
        } catch (IOException e) {
            Log.e(TAG, "连接出错");
            e.printStackTrace();
            return;
        }
        Log.e(TAG, "连接完");
        while (isRun){
            try {
                receiveMsgLen = mDataInputStream.read(buff);
                if (receiveMsgLen != 0) {
                    byte[] dataReceive = new byte[receiveMsgLen];
                    System.arraycopy(buff, 0, dataReceive, 0, receiveMsgLen);
                    receiverListener.onReceiveData(dataReceive);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mPrintWriter.close();
            mInputStream.close();
            mDataInputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface IReceiverListener {
        void onReceiveData(byte[] data);
    }
}
