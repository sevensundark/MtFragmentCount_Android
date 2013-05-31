package com.cn.zhangl.MtFragmentCount.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.cn.zhangl.MtFragmentCount.R;
import com.cn.zhangl.MtFragmentCount.common.ConstUtil;
import com.cn.zhangl.MtFragmentCount.model.MtApplication;

/**
 * Created with IntelliJ IDEA.
 * User: zhangl
 * Date: 13/05/31
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class MtService extends Service {
    WindowManager wm = null;
    WindowManager.LayoutParams wmParams = null;
    View view;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    int state;
    private TextView todayDoneCountView;
    private TextView nextLeftCountView;
    private TextView showUserView;
    private Button addBtn;
    private Button reduceBtn;

    private int arrDoneIndex = 0;
    private int doneIndex = 0;

    int delaytime = 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.floating, null);

        todayDoneCountView = (TextView) view.findViewById(R.id.todayDoneTxtView);
        nextLeftCountView = (TextView) view.findViewById(R.id.nextLeftTxtView);
        showUserView = (TextView) view.findViewById(R.id.userTextView);
        addBtn = (Button) view.findViewById(R.id.addBtn);
        reduceBtn = (Button) view.findViewById(R.id.reduceBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                todayDoneCountView.setText(String.valueOf(++doneIndex));

//				int rollCount = doneIndex / ConstUtil.ROLL_COUNT > 0 ? doneIndex - ConstUtil.ROLL_COUNT * (doneIndex / ConstUtil.ROLL_COUNT) : doneIndex;
                int rollCount = 0;
                if (doneIndex / ConstUtil.ROLL_COUNT > 0 && doneIndex % ConstUtil.ROLL_COUNT == 0) {
                    rollCount = doneIndex - (doneIndex / ConstUtil.ROLL_COUNT - 1) * ConstUtil.ROLL_COUNT;
                } else {
                    rollCount = doneIndex % ConstUtil.ROLL_COUNT;
                }

                if (rollCount > ConstUtil.REAL_COUNT[arrDoneIndex]) {
                    arrDoneIndex++;
                }

                nextLeftCountView.setText(String.valueOf(ConstUtil.REAL_COUNT[arrDoneIndex] - rollCount));

                if (doneIndex % ConstUtil.ROLL_COUNT == 0) {
                    arrDoneIndex = 0;
                }
            }
        });

        reduceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (doneIndex == 0) {
                    return;
                }

                todayDoneCountView.setText(String.valueOf(--doneIndex));

//				if (doneIndex % ConstUtil.ROLL_COUNT == 0) {
//					arrDoneIndex = ConstUtil.REAL_COUNT.length - 1;
//				}

//				int rollCount = doneIndex / ConstUtil.ROLL_COUNT > 0 ? doneIndex - ConstUtil.ROLL_COUNT * (doneIndex / ConstUtil.ROLL_COUNT) : doneIndex;

                int rollCount = 0;
                if (doneIndex / ConstUtil.ROLL_COUNT > 0 && doneIndex % ConstUtil.ROLL_COUNT == 0) {
                    arrDoneIndex = ConstUtil.REAL_COUNT.length - 1;
                    rollCount = doneIndex - (doneIndex / ConstUtil.ROLL_COUNT - 1) * ConstUtil.ROLL_COUNT;
                } else {
                    rollCount = doneIndex % ConstUtil.ROLL_COUNT;
                }

                if (arrDoneIndex >= 1) {
                    if (rollCount <= ConstUtil.REAL_COUNT[arrDoneIndex - 1]) {
                        arrDoneIndex--;
                    }
                }

                nextLeftCountView.setText(String.valueOf(ConstUtil.REAL_COUNT[arrDoneIndex] - rollCount));
            }
        });

//		iv = (ImageView) view.findViewById(R.id.img2);
//		iv.setVisibility(View.GONE);
        createView();
//		handler.postDelayed(task, delaytime);
    }

    private void createView() {
        SharedPreferences shared = getSharedPreferences("float_flag",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("float", 1);
        editor.commit();
        // 获取WindowManager
        wm = (WindowManager) getApplicationContext().getSystemService("window");
        // 设置LayoutParams(全局变量）相关参数
        wmParams = ((MtApplication) getApplication()).getMtwmParams();
        wmParams.type = 2002;//android.view.WindowManager.LayoutParams.TYPE_PHONE
        wmParams.flags |= 8;//android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;

        wm.addView(view, wmParams);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY() - 25; // 25是系统状态栏的高度
//				Log.i("currP", "currX" + x + "====currY" + y);// 调试信息
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        state = MotionEvent.ACTION_DOWN;
//                        StartX = x;
//                        StartY = y;
                        // 获取相对View的坐标，即以此View左上角为原点
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
//					Log.i("startP", "startX" + mTouchStartX + "====startY"
//							+ mTouchStartY);// 调试信息
                        break;
                    case MotionEvent.ACTION_MOVE:
                        state = MotionEvent.ACTION_MOVE;
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        state = MotionEvent.ACTION_UP;

                        updateViewPosition();
//					showImg();
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });

//		iv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent serviceStop = new Intent();
//				serviceStop.setClass(FloatService.this, FloatService.class);
//				stopService(serviceStop);
//			}
//		});

    }

//	public void showImg() {
//		if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5
//				&& !iv.isShown()) {
//			iv.setVisibility(View.VISIBLE);
//		} else if (iv.isShown()) {
//			iv.setVisibility(View.GONE);
//		}
//	}

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            dataRefresh();
            handler.postDelayed(this, delaytime);
            wm.updateViewLayout(view, wmParams);
        }
    };

    public void dataRefresh() {
//		tx.setText("" + memInfo.getmem_UNUSED(this) + "KB");
//		tx1.setText("" + memInfo.getmem_TOLAL() + "KB");
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        wm.updateViewLayout(view, wmParams);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        setForeground(true);
        initControl();
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
//		handler.removeCallbacks(task);
        saveData();
        wm.removeView(view);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveData() {
        SharedPreferences shared = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        String currentUser = shared.getString(ConstUtil.KEY_CURRENT_USER, "error user");

        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(currentUser + "_" + ConstUtil.DONE_COUNT, doneIndex);
        editor.putInt(currentUser + "_" + ConstUtil.DONE_ARR_INDEX, arrDoneIndex);
        editor.commit();
    }

    private void initControl() {
        SharedPreferences shared = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        String currentUser = shared.getString(ConstUtil.KEY_CURRENT_USER, "error user");

        doneIndex = shared.getInt(currentUser + "_" + ConstUtil.DONE_COUNT, 0);
        arrDoneIndex = shared.getInt(currentUser + "_" + ConstUtil.DONE_ARR_INDEX, 0);

        todayDoneCountView.setText(String.valueOf(doneIndex));
        nextLeftCountView.setText(String.valueOf(ConstUtil.REAL_COUNT[arrDoneIndex] - doneIndex));

        if (ConstUtil.USER_TWO.equals(currentUser)) {
            showUserView.setText("二区用户");
        } else if (ConstUtil.USER_THREE.equals(currentUser)) {
            showUserView.setText("三区用户");
        } else {
            showUserView.setText("unknown user");
        }
    }
}
