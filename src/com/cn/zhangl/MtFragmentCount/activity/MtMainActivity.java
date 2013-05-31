package com.cn.zhangl.MtFragmentCount.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cn.zhangl.MtFragmentCount.R;
import com.cn.zhangl.MtFragmentCount.common.ConstUtil;
import com.cn.zhangl.MtFragmentCount.service.MtService;

public class MtMainActivity extends Activity {
    private Button btnstart;
    private Button btnstop;
    private TextView tv;

    private Button chooseTwoBtn;
    private Button chooseThreeBtn;
    private TextView chooseInfo;

    private boolean serviceFlg = true;
    private boolean choosenFlg = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        chooseInfo = (TextView) findViewById(R.id.choosenUserView);

        chooseTwoBtn = (Button) findViewById(R.id.chooseTwoBtn);
        chooseTwoBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInfo.setText("你选择了二区用户!");
                saveCurrentUser(ConstUtil.USER_TWO);
                choosenFlg = true;
            }
        });

        chooseThreeBtn = (Button) findViewById(R.id.chooseThreeBtn);
        chooseThreeBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInfo.setText("你选择了三区用户!");
                saveCurrentUser(ConstUtil.USER_THREE);
                choosenFlg = true;
            }
        });

        btnstart = (Button) findViewById(R.id.btnstart);
        btnstart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!choosenFlg) {
                    return;
                }

                if (!serviceFlg) {
                    return;
                }

                Intent service = new Intent();
                service.setClass(MtMainActivity.this, MtService.class);
                startService(service);
                serviceFlg= false;
            }
        });

        btnstop = (Button) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceStop = new Intent();
                serviceStop.setClass(MtMainActivity.this, MtService.class);
                stopService(serviceStop);
                serviceFlg = true;
            }
        });
        tv = (TextView) findViewById(R.id.tv);

        String str = new StringBuilder().append("\n").append("说明：")
                .append("\n").append("1.悬浮窗可随意移动").append("\n")
                .append("2.实时显示当前内存数据").append("\n").append("3.上层数据表示可用内存值")
                .append("\n").append("4.下层数据表示总内存值").append("\n")
                .append("5.点击悬浮窗出现关闭小图标可直接关闭").append("\n").append("\n")
                .toString();
        tv.setText(str);
    }

    private void saveCurrentUser(String user) {
        SharedPreferences shared = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(ConstUtil.KEY_CURRENT_USER, user);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
