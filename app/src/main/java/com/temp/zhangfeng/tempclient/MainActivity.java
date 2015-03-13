package com.temp.zhangfeng.tempclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity {
    private TextView tempNum;//温度文本
    private TextView targetTemp;//目标温度
    private SeekBar sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tempNum = (TextView)findViewById(R.id.textView2);//获取温度的文本框
        targetTemp = (TextView)findViewById(R.id.textView4);//获取目标温度

        /*拖动条*/
        sb = (SeekBar)findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetTemp.setText(getTargetTemp(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    private String getTargetTemp(int progress) {
        final int MaxTemp = 50;//最大值
        final int MaxProgress = 100;
        float f = (float)MaxTemp*progress/MaxProgress;//获取当前值
        DecimalFormat df = new DecimalFormat("#0.0");//格式化数据 保留一位小数
        return df.format(f);
    }
}
