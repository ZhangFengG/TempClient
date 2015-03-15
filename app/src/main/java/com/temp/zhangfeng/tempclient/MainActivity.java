package com.temp.zhangfeng.tempclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.temp.zhangfeng.bluetooth.ConnectThread;


public class MainActivity extends ActionBarActivity {
    private static final String CAR_ADR = "00:14:03:05:08:CD";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int TEMPERATURE_NUM = 1;
    private static final int CALLBACK_BR = 2;
    private static TextView tempNum;//温度文本
    private TextView targetTemp;//目标温度
    private Switch sw;
    public SeekBar sb;
    public BluetoothAdapter bluetoothAdapter; //蓝牙适配器
    public ConnectThread ct;//连接线程-客户端

    private final static Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TEMPERATURE_NUM:

                    //tempNum.setText(null);
                    String str = (String) msg.obj;
                    if(str.equals("$")){
                       return ;
                    }
                    tempNum.setText(str);
//                    Log.i("BLUE", str);
                    break;
                case CALLBACK_BR:
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempNum = (TextView)findViewById(R.id.textView2);//获取温度的文本框
        targetTemp = (TextView)findViewById(R.id.textView4);//获取目标温度

         /*蓝牙*/
        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(CAR_ADR);
        //判断是否支持
        if(bluetoothAdapter!=null){
            //判断是否打开
            if(bluetoothAdapter.isEnabled()){
                //显示已配对信息
//        		findBluetooth();
        		/*客户端连接*/
                ct = new ConnectThread(mHandler,bluetoothAdapter, device);
                ct.start();
            }else{
                //调用intent打开
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth,REQUEST_ENABLE_BT);
            }
        }

        /*开关*/
        sw = (Switch)findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                byte[] b = new byte[1];
                if(isChecked){
                    b[0]= (byte) 0x81;
                    //Log.i("SW",Byte.toString(b[0]));
                    ct.write(b);
                }else{
                    b[0]=(byte)0x80;
                    ct.write(b);
                }
            }
        });
        /*拖动条*/
        sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetTemp.setText(getTargetTempToString(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String numStr = getTargetTempToString(seekBar.getProgress());
                byte[] b = new byte[1];
                b[0] = getTempToByte(seekBar.getProgress());
                ct.write(b);
            }
        });


    }

    /**
     * 获取一位数据发送到单片机
     * @param progress
     * @return
     */
    private byte getTempToByte(int progress){
        byte b;
        //b = (byte) ((byte) ((progress / 16) << 4) | (byte) (progress % 16));
        b = (byte)(0x00|((progress/16)<<4));
        b = (byte)(b|(progress%16));
        return b;
    }
    /**
     *
     * @param progress
     * @return
     */
    private String getTargetTempToString(int progress) {
        final int MaxTemp = 50;//最大值
        final int MaxProgress = 100;
        float f = (float)MaxTemp*progress/MaxProgress;//获取当前值
        DecimalFormat df = new DecimalFormat("#0.0");//格式化数据 保留一位小数
        return df.format(f);
    }
}
