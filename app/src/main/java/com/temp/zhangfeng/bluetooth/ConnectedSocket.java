package com.temp.zhangfeng.bluetooth;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ConnectedSocket extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    /**
     * @param socket
     * 构造函数
     */
    public ConnectedSocket(Handler mHandler,BluetoothSocket socket){
        InputStream tmIn = null;
        OutputStream tmOut = null;
        mmSocket = socket;
        try {
            tmIn = mmSocket.getInputStream();
            tmOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmInStream = tmIn;
        mmOutStream = tmOut;
        this.mHandler = mHandler;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     * 线程进行读取socket数据
     */
    public void run(){

//    	byte[] buffer = new byte[1024];
        int bytes=-1;
        while(true){
            try {
                char[] buffer = new char[20];//在循环里面实例化
                String strBuffer;//在循环里面实例化，避免数据叠加导致错误
//    			Thread.sleep(100);
                BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));
//    			bytes = mmInStream.read(buffer);
//    			String str = Byte.toString(buffer[0]);
                if((bytes=br.read(buffer))!=-1){
                    strBuffer=new String(buffer);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = strBuffer.toString().trim();
//    				Log.i("BLUE", strBuffer.toString().trim());
                    mHandler.sendMessage(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
    public void write(char[] chars){
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(mmOutStream));
        try {
            bw.write(chars);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
            mmInStream.close();
        } catch (IOException e) { }
    }
}
