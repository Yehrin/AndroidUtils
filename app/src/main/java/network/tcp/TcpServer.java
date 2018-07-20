package network.tcp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TcpServer implements Runnable{
    private String TAG = "TcpServer";
    private int port;
    private boolean isListen = true;            // 线程监听标志位
    private IReceiverListener receiverListener;

    public ArrayList<ServerSocketThread> mServerSocketThreadList = new ArrayList<>();
    public TcpServer(int port, IReceiverListener receiverListener){
        this.port = port;
        this.receiverListener = receiverListener;
    }

    // 更改监听标志位
    public void setIsListen(boolean listen){
        isListen = listen;
    }

    // 更改端口
    public void setPort(int port) {
        this.port = port;
    }

    // 关闭线程
    public void closeSelf(){
        isListen = false;
        for (ServerSocketThread s : mServerSocketThreadList){
            s.isRun = false;
        }
        mServerSocketThreadList.clear();
    }

    private Socket getSocket(ServerSocket serverSocket){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "run: 监听超时");
            return null;
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port); // 输入端口
            serverSocket.setSoTimeout(5000);
            Log.i(TAG, "run: 开始监听...");
            while (isListen){
                // 不断侦听是否有新的客户端连接
                Socket socket = getSocket(serverSocket);
                if (socket != null){
                    new ServerSocketThread(socket);
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ServerSocketThread extends Thread{
        Socket socket = null;
        private PrintWriter printerWriter;
        private InputStream inputStream = null;
        private OutputStream outputStream = null;
        private String ip = null;       // 客户端的IP
        private boolean isRun = true;

        ServerSocketThread(Socket socket){
            this.socket = socket;
            ip = socket.getInetAddress().toString();
            Log.i(TAG, "ServerSocketThread:检测到新的客户端联入 ip:" + ip);
            try {
                // 初始化
                socket.setSoTimeout(5000);
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                printerWriter = new PrintWriter(outputStream, true);
                // 开启线程
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String msg){
            printerWriter.println(msg);
            printerWriter.flush();      // 强制送出数据
        }

        @Override
        public void run() {
            byte buff[]  = new byte[4096];
            int receiveLen;
            mServerSocketThreadList.add(this);
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()){
                try {
                    if ((receiveLen = inputStream.read(buff)) != -1 ){
                        byte[] dataReceive = new byte[receiveLen];
                        System.arraycopy(buff, 0, dataReceive, 0, receiveLen);
                        receiverListener.onReceiveData(ip, dataReceive);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
                mServerSocketThreadList.clear();
                Log.i(TAG, "run: 断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface IReceiverListener {
        void onReceiveData(String ip, byte[] data);
    }
}
