package com.example.myapplication.Fragment;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.QNumberPicker;
import com.example.myapplication.R;
import com.example.myapplication.VirtualSimulation.MySurfaceView;
import static com.example.myapplication.Inv.*;
import static com.example.myapplication.Fragment.RealClientFragment.*;

import java.lang.reflect.Field;
import java.util.Vector;

import static com.example.myapplication.Constant.*;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.round;

/**
 * <pre>
 *     author : 孟东风
 *     time   : 2019/12/11
 *     project_name  :TeachingBox4
 *     desc   :
 */
public class OperateFragment extends Fragment {

    Button record_begin, record_end, record_point, record_process, send;    //虚拟轨迹规划
    Button button_fuwei;
    Button fixture, fixture_none, fixture_hand;

    ImageView x_plus, y_plus, z_plus, rx_plus, ry_plus, rz_plus, x_min, y_min, z_min, rx_min, ry_min, rz_min; //末端位姿控制12个按钮
    LinearLayout joint_select_layout;
//    Button[] joint_select=new Button[7];  //7个关节选择
    TextView[] joint=new TextView[7];

    int joint_activated=1;   //当前哪个关节别激活
    QNumberPicker numberpicker;
//    float number_picked=0.0f;
    public float number_picked=5.0f;

    Button joint_min,joint_max,button_right_bottom;

    static SeekBar[] seekbar_select=new SeekBar[7];

    static TextView joint_left_bottom;

    public static EditText x,y,z,rx,ry,rz;

    Vector<Vector<Float>> position=new  Vector<Vector<Float>>(50);

    private View view;

    TextView netCondition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_operate, container, false);

        MySurfaceView mSurfaceView=new MySurfaceView(this.getActivity());
        mSurfaceView.requestFocus();
        mSurfaceView.setFocusableInTouchMode(true);

        LinearLayout model=view.findViewById(R.id.model);
        model.addView(mSurfaceView);

        init();
//        initNumberPicker();
        //单击事件
        z_plus.setOnClickListener(onClickListener);
        z_min.setOnClickListener(onClickListener);
        y_plus.setOnClickListener(onClickListener);
        y_min.setOnClickListener(onClickListener);
        x_plus.setOnClickListener(onClickListener);
        x_min.setOnClickListener(onClickListener);
        rz_plus.setOnClickListener(onClickListener);
        rz_min.setOnClickListener(onClickListener);
        ry_plus.setOnClickListener(onClickListener);
        ry_min.setOnClickListener(onClickListener);
        rx_plus.setOnClickListener(onClickListener);
        rx_min.setOnClickListener(onClickListener);
        // 长按事件
        z_plus.setOnLongClickListener(onLongClickListener);
        z_min.setOnLongClickListener(onLongClickListener);
        y_plus.setOnLongClickListener(onLongClickListener);
        y_min.setOnLongClickListener(onLongClickListener);
        x_plus.setOnLongClickListener(onLongClickListener);
        x_min.setOnLongClickListener(onLongClickListener);
        rz_plus.setOnLongClickListener(onLongClickListener);
        rz_min.setOnLongClickListener(onLongClickListener);
        ry_plus.setOnLongClickListener(onLongClickListener);
        ry_min.setOnLongClickListener(onLongClickListener);
        rx_plus.setOnLongClickListener(onLongClickListener);
        rx_min.setOnLongClickListener(onLongClickListener);
        // 触摸事件
        z_plus.setOnTouchListener(onTouchListener);
        z_min.setOnTouchListener(onTouchListener);
        y_plus.setOnTouchListener(onTouchListener);
        y_min.setOnTouchListener(onTouchListener);
        x_plus.setOnTouchListener(onTouchListener);
        x_min.setOnTouchListener(onTouchListener);
        rz_plus.setOnTouchListener(onTouchListener);
        rz_min.setOnTouchListener(onTouchListener);
        ry_plus.setOnTouchListener(onTouchListener);
        ry_min.setOnTouchListener(onTouchListener);
        rx_plus.setOnTouchListener(onTouchListener);
        rx_min.setOnTouchListener(onTouchListener);

        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float[] endpos_target= endpose;
            switch (v.getId()) {
                case R.id.X_PLUS:
                    //末端位姿反解后再赋给机械臂
                    endpos_target[0]+=number_picked;
                    inv_calculate(m_theta,endpos_target);
                    number_Change();
                    break;
                case R.id.X_MIN:
                    endpos_target[0]-=number_picked;
                    inv_calculate(m_theta,endpos_target);
                    number_Change();
                    break;
                case R.id.Y_PLUS:
                    endpos_target[1]+=number_picked;
                    inv_calculate(m_theta,endpos_target);
                    number_Change();
                    break;
                case R.id.Y_MIN:
                    endpos_target[1]-=number_picked;
                    inv_calculate(m_theta,endpos_target);
                    number_Change();
                    break;
                case R.id.Z_PLUS:
                    endpos_target[2]+=number_picked;
                    inv_calculate(m_theta,endpos_target);
                    number_Change();
                    break;
                case R.id.Z_MIN:
                    endpos_target[2]-=number_picked;
                    inv_calculate(m_theta,endpos_target);  //以求得各个关节的角度
                    number_Change();
                    break;
                case R.id.RX_PLUS:
                    DD_Calculate(number_picked,0,0);
                    number_Change();
                    break;
                case R.id.RX_MIN:
                    DD_Calculate(-number_picked,0,0);
                    number_Change();
                    break;
                case R.id.RY_PLUS:
                    DD_Calculate(0,number_picked,0);
                    number_Change();
                    break;
                case R.id.RY_MIN:
                    DD_Calculate(0,-number_picked,0);
                    number_Change();
                    break;
                case R.id.RZ_PLUS:
                    DD_Calculate(0,0,number_picked);
                    number_Change();
                    break;
                case R.id.RZ_MIN:
                    DD_Calculate(0,0,-number_picked);
                    number_Change();
                    break;
            }
        }
    };


    private SparseArray<Boolean> mBtnTouchMap =new SparseArray<>();

    public static final int LONG_CLICK=10001;

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mBtnTouchMap.put(v.getId(), true);
            mHandler.sendEmptyMessage(LONG_CLICK);
            return false;
        }
    };

    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            float[] endpos_target= endpose;
            switch (msg.what) {
                case LONG_CLICK:
                    for (int i = 0; i < mBtnTouchMap.size(); i++) {
                        int viewId = mBtnTouchMap.keyAt(i);
                        // 长按结束，就不再继续往下走了
                        if (!mBtnTouchMap.valueAt(i))
                            continue;
                        switch (viewId) {
                            case R.id.X_PLUS:
                                //末端位姿反解后再赋给机械臂
                                endpos_target[0]+=number_picked;
                                inv_calculate(m_theta,endpos_target);
                                number_Change();
                                break;
                            case R.id.X_MIN:
                                endpos_target[0]-=number_picked;
                                inv_calculate(m_theta,endpos_target);
                                number_Change();
                                break;
                            case R.id.Y_PLUS:
                                endpos_target[1]+=number_picked;
                                inv_calculate(m_theta,endpos_target);
                                number_Change();
                                break;
                            case R.id.Y_MIN:
                                endpos_target[1]-=number_picked;
                                inv_calculate(m_theta,endpos_target);
                                number_Change();
                                break;
                            case R.id.Z_PLUS:
                                endpos_target[2]+=number_picked;
                                inv_calculate(m_theta,endpos_target);
                                number_Change();
                                break;
                            case R.id.Z_MIN:
                                endpos_target[2]-=number_picked;
                                inv_calculate(m_theta,endpos_target);  //以求得各个关节的角度
                                number_Change();
                                break;
                            case R.id.RX_PLUS:
                                DD_Calculate(number_picked,0,0);
                                number_Change();
                                break;
                            case R.id.RX_MIN:
                                DD_Calculate(-number_picked,0,0);
                                number_Change();
                                break;
                            case R.id.RY_PLUS:
                                DD_Calculate(0,number_picked,0);
                                number_Change();
                                break;
                            case R.id.RY_MIN:
                                DD_Calculate(0,-number_picked,0);
                                number_Change();
                                break;
                            case R.id.RZ_PLUS:
                                DD_Calculate(0,0,number_picked);
                                number_Change();
                                break;
                            case R.id.RZ_MIN:
                                DD_Calculate(0,0,-number_picked);
                                number_Change();
                                break;
                        }
                        mHandler.sendEmptyMessageDelayed(LONG_CLICK, 150);
                    }
                    break;
            }
        }
    };

    private View.OnTouchListener onTouchListener=new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_UP:
                    mBtnTouchMap.put(v.getId(),false);
                    break;
            }
            return false;
        }
    };

    /*    *//**
     * 将findViewbyid和一些初始化数据的内容放置到onViewCreated中，
     * 其是在加载完布局后调用。因为只有在onCreateView后界面才能加载出来了，
     * 所以将findViewbyid和初始化一些数据放置其中可以避免出现如切换界面时出现卡顿的现象，
     * 提高用户体验。
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }*/

    private void Reset(){
        a_horizontal=0;//视角回到初始位置
        seekbar_select[0].setProgress(1800);seekbar_select[1].setProgress(900);seekbar_select[2].setProgress(1800);seekbar_select[3].setProgress(2400);
        seekbar_select[4].setProgress(1800);seekbar_select[5].setProgress(450);seekbar_select[6].setProgress(1800);
        //各关节角度范围 关节一：正负180  关节二：-90到90（初始0）关节三：正负180 关节四：-60到240（初始0）  关节五：正负180 关节六：-45，225 关节七：正负360
        joint_target[0] = 0; joint_target[1] = 0; joint_target[2] = 0;  joint_target[3] = 0; joint_target[4] = 0; joint_target[5] = 0;joint_target[6]=0;

        Endpos_Calculate();
        number_Change();
    }

    private void init() {
        netCondition =(TextView) view.findViewById(R.id.textReceiveInfo);
        //语音id

        //六个关节角的度数显示
        //末端位姿显示
        x = (EditText) view.findViewById(R.id.x); y = (EditText) view.findViewById(R.id.y); z = (EditText) view.findViewById(R.id.z);
        ry = (EditText)  view.findViewById(R.id.ry);rx = (EditText)  view.findViewById(R.id.rx); rz = (EditText)  view.findViewById(R.id.rz);

        //开始，记录，结束，执行，暂停，停止
        record_begin = (Button)  view.findViewById(R.id.Record_begin);
        record_point = (Button)  view.findViewById(R.id.Record_point);
        record_end = (Button)  view.findViewById(R.id.Record_end);
        record_process = (Button)  view.findViewById(R.id.Record_process);
/*        record_pause = (Button)  view.findViewById(R.id.Record_pause);
        record_stop = (Button)  view.findViewById(R.id.Record_stop);*/

        //复位
        button_fuwei = (Button)  view.findViewById(R.id.Reset);
        //向pc端发送数据
        send = (Button)  view.findViewById(R.id.Send);

        x_plus =(ImageView)  view.findViewById(R.id.X_PLUS);
        y_plus = (ImageView) view.findViewById(R.id.Y_PLUS);
        z_plus = (ImageView) view.findViewById(R.id.Z_PLUS);
        rx_plus = (ImageView) view.findViewById(R.id.RX_PLUS);
        ry_plus = (ImageView) view.findViewById(R.id.RY_PLUS);
        rz_plus = (ImageView) view.findViewById(R.id.RZ_PLUS);

        x_min = (ImageView) view.findViewById(R.id.X_MIN);
        y_min = (ImageView) view.findViewById(R.id.Y_MIN);
        z_min = (ImageView) view.findViewById(R.id.Z_MIN);
        rx_min = (ImageView) view.findViewById(R.id.RX_MIN);
        ry_min = (ImageView) view.findViewById(R.id.RY_MIN);
        rz_min = (ImageView) view.findViewById(R.id.RZ_MIN);

        joint[0]=(TextView) view.findViewById(R.id.joint1);
        joint[1]=(TextView) view.findViewById(R.id.joint2);
        joint[2]=(TextView) view.findViewById(R.id.joint3);
        joint[3]=(TextView) view.findViewById(R.id.joint4);
        joint[4]=(TextView) view.findViewById(R.id.joint5);
        joint[5]=(TextView) view.findViewById(R.id.joint6);
        joint[6]=(TextView) view.findViewById(R.id.joint7);

        //控制机器人的7个流动条
        seekbar_select[0] = (SeekBar) view.findViewById(R.id.seekBar1);
        seekbar_select[1] = (SeekBar) view.findViewById(R.id.seekBar2);
        seekbar_select[2] = (SeekBar) view.findViewById(R.id.seekBar3);
        seekbar_select[3] = (SeekBar) view.findViewById(R.id.seekBar4);
        seekbar_select[4] = (SeekBar) view.findViewById(R.id.seekBar5);
        seekbar_select[5] = (SeekBar) view.findViewById(R.id.seekBar6);
        seekbar_select[6] = (SeekBar) view.findViewById(R.id.seekBar7);
        //下面是控件的设定
/*        //机器人在初始状态下的末端位姿
        x.setText("-440");
        y.setText("0.0");
        z.setText("378.0");
        rx.setText("180.0");
        ry.setText("0.0");
        rz.setText("-180.0");*/

        //滚动条属性设置，由于在seekbar中不能设置小数，只此在此将其全部乘以10，在计算和显示的时候再除以10
        //各关节角度范围 关节一：正负180  关节二：-90到90（初始0）关节三：正负180 关节四：-240到60（初始0）  关节五：正负180 关节六：正45，-225 关节七：正负360
        seekbar_select[0].setMax(3600);
        seekbar_select[0].setProgress(1800); //三个参数分别为最大值，初始显示数据以及初始位置
        seekbar_select[1].setMax(1800);
        seekbar_select[1].setProgress(900);
        seekbar_select[2].setMax(3600);
        seekbar_select[2].setProgress(1800);
        seekbar_select[3].setMax(3000);
        seekbar_select[3].setProgress(600);
        seekbar_select[4].setMax(3600);
        seekbar_select[4].setProgress(1800);
        seekbar_select[5].setMax(2700);
        seekbar_select[5].setProgress(450);
        seekbar_select[6].setMax(3600);
        seekbar_select[6].setProgress(1800);


        //数字选择器及两端加减按钮
        numberpicker = (QNumberPicker) view.findViewById(R.id.numberpicker);

        numberpicker.setMaxValue(100);
        numberpicker.setMinValue(1);
        numberpicker.setValue(50);   //设置初始值
        numberpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);//禁止打开键盘，关闭编辑模式

        numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                number_picked = abs(((float) (newVal * 0.1) ));
                //               number_picked = abs((float) ((float) (newVal * 0.1)- 0.1 ));
            }
        });
        //        Button record_begin, record_end, record_point, record_process, send;    //虚拟轨迹规划
        record_end.setEnabled(false);
        record_point.setEnabled(false);
        record_process.setEnabled(false);

        x.setEnabled(false);
        y.setEnabled(false);
        z.setEnabled(false);
        rx.setEnabled(false);
        ry.setEnabled(false);
        rz.setEnabled(false);

        //示教再现功能的实现

        //开始记录
        record_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                float index[] = {0,0,0,0,0,0,11};
                send_to_pc(index);*/
                record_begin.setEnabled(false);
                record_end.setEnabled(true);
                record_point.setEnabled(true);
                x.setEnabled(true);y.setEnabled(true);z.setEnabled(true);rx.setEnabled(true);ry.setEnabled(true);rz.setEnabled(true);
            }
        });

        //结束
        record_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                float index[] = {0,0,0,0,0,0,13};
                send_to_pc(index);*/
                record_begin.setEnabled(true);
                record_end.setEnabled(false);
                record_point.setEnabled(false);
                record_process.setEnabled(false);
                x.setEnabled(false);y.setEnabled(false);z.setEnabled(false);rx.setEnabled(false);ry.setEnabled(false);rz.setEnabled(false);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] ={joint_target[0],joint_target[1],joint_target[2],joint_target[3],joint_target[4],joint_target[5],joint_target[6],12.0f};
                send_to_pc(index);
            }
        });


/*        //开始记录
        record_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,11};
                send_to_pc(index);
            }
        });
        //虚拟轨迹规划
        //打点
        record_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {joint_target[0],joint_target[1],joint_target[2],joint_target[3],joint_target[4],joint_target[5],12};
                send_to_pc(index);
            }
        });
        //结束
        record_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,13};
                send_to_pc(index);
            }
        });
        //执行
        record_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,14};
                send_to_pc(index);
            }
        });
        record_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,15};
                send_to_pc(index);
            }
        });
        record_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,16};
                send_to_pc(index);
            }
        });*/

/*        //打点
        record_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*//*
                float index[] = {joint_target[0],joint_target[1],joint_target[2],joint_target[3],joint_target[4],joint_target[5],12};
                send_to_pc(index);*//*

                float varX= Float.parseFloat(x.getText().toString()), varY= Float.parseFloat(y.getText().toString()),varZ= Float.parseFloat(z.getText().toString());
                float varRX= Float.parseFloat(rx.getText().toString()),varRY= Float.parseFloat(ry.getText().toString()),varRZ= Float.parseFloat(rz.getText().toString());
                float[] index={varX,varY,varZ,varRX,varRY,varRZ};//获取位姿信息
                //                send_to_pc(index);
                //                String varX=x.getText().toString(),varY= y.getText().toString(),varZ= z.getText().toString();
                //                String varRX=x.getText().toString(),varRY= y.getText().toString(),varRZ= z.getText().toString();
                //                Collection index=new ArrayList();//获取位姿信息


                Vector<Float> temp=new Vector<Float>();
                temp.addElement(varX);temp.addElement(varY);temp.addElement(varZ);
                temp.addElement(varRX);temp.addElement(varRY);temp.addElement(varRZ);
                position.addElement(temp);


                record_process.setEnabled(true);
                //               float position= vector.toArray();

*//*                for (int i=0;i<6;i++) {
                    pose[poseCount][i]=index[i];
                }*//*
                //                int mPoseCount=poseCount;
                //                pose[poseCount]=index;
                //                poseCount++;
            }
        });*/

        //打点
        record_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {joint_target[0],joint_target[1],joint_target[2],joint_target[3],joint_target[4],joint_target[5]};
                send_to_pc(index);
            }
        });

/*        //执行
        //* 在这里实现虚拟模型的运动

        record_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*/
/*                float index[] = {0,0,0,0,0,0,14};
                send_to_pc(index);*//*

                //  if (abs(joint_target[0]-joint_current[0])>1){
                //
                //   }

                for (int i=0;i<position.size();i++){
                    Vector<Float> index = position.get(i);
                    //               Float[] endpos_target=index.toArray(new Float[index.size()]);
                    float[] endpos_target=new float[index.size()];
                    for(int j=0;j<index.size();j++){
                        endpos_target[j]=index.get(j);
                    }
                    send_to_pc(endpos_target);
                    inv_calculate(m_theta,endpos_target);
                    number_Change();
                }
                position.clear();
            }
        });*/

        button_fuwei.setOnClickListener(new View.OnClickListener() {//回到初始位置
            @Override
            public void onClick(View v) {
                Reset();
            }
        });

        //调节滚动条一
        seekbar_select[0].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[0]=(float) (i-1800)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  //该方法拖动进度条开始拖动的时候调用。
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {    //该方法拖动进度条停止拖动的时候调用。
                joint_target[0]=(float) (seekBar.getProgress()-1800)/10;
                number_Change();
            }
        });

        seekbar_select[1].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[1] = (float)(i-900)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {    }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[1] = (float)(seekBar.getProgress()-900)/10;
                number_Change();
            }
        });

        seekbar_select[2].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[2] = (float)(i-1800)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[2] = (float)(seekBar.getProgress()-1800)/10;
                number_Change();
            }
        });

        seekbar_select[3].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[3] = (float)(i-600)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[3] = (float)(seekBar.getProgress()-600)/10;
                number_Change();
            }
        });

        seekbar_select[4].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[4] = (float)(i-1800)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[4] = (float)(seekBar.getProgress()-1800)/10;
                number_Change();
            }
        });

        seekbar_select[5].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[5] = (float)(i-450)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[5] = (float)(seekBar.getProgress()-450)/10;
                number_Change();
            }
        });

        seekbar_select[6].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[6] = (float)(i-1800)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[6] = (float)(seekBar.getProgress()-1800)/10;
                number_Change();
            }
        });
    }

    //集中处理数据更新显示工作
    public void number_Change(){
        Endpos_Calculate();
        joint[0].setText("J1 "+(float)(round(joint_target[0]*10))/10);
        joint[1].setText("J2 "+(float)(round(joint_target[1]*10))/10);
        joint[2].setText("J3 "+(float)(round(joint_target[2]*10))/10);
        joint[3].setText("J4 "+(float)(round(joint_target[3]*10))/10);
        joint[4].setText("J5 "+(float)(round(joint_target[4]*10))/10);
        joint[5].setText("J6 "+(float)(round(joint_target[5]*10))/10);
        joint[6].setText("J7 "+(float)(round(joint_target[6]*10))/10);
//        joint_left_bottom.setText((float)(round(joint_target[joint_activated-1]*10))/10+"");
    }


/*    //初始化数字选择滚动框布局
    private void initNumberPicker(){
//        numberpicker.setFocusable(false);
//        numberpicker.setFocusableInTouchMode(false);
        numberpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
 //       setNumberPickerDividerColor(numberpicker);
    }*/

    //自定义滚动框分割线颜色
    private void setNumberPickerDividerColor(NumberPicker number){
        Field[] pickerFields=NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (TRUE) {
                pf.setAccessible(true);
                try {
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(this.getActivity(), R.color.gree)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static void Endpos_Calculate(){  //末端位姿计算
        //        float[] m_theta=new float[7];
        for(int i=0;i<7;i++){
            m_theta[i]=(float)(joint_target[i]*PI/180+theta[i]+ theta_0[i]);  //各关节目标值+theta值+theta初始值
        }
        float t10[][],t21[][],t32[][], t43[][], t54[][], t65[][], t76[][];   //调用inv中的DH_mat方法计算连续变化矩阵
        t10 = DH_mat(a[0], d[0], alpha[0], m_theta[0]); t21 = DH_mat(a[1], d[1], alpha[1], m_theta[1]);
        t32 = DH_mat(a[2], d[2], alpha[2], m_theta[2]); t43 = DH_mat(a[3], d[3], alpha[3], m_theta[3]);
        t54 = DH_mat(a[4], d[4], alpha[4], m_theta[4]); t65 = DH_mat(a[5], d[5], alpha[5], m_theta[5]);
        t76 = DH_mat(a[6], d[6], alpha[6], m_theta[6]);

        float t20[][],t30[][],t40[][], t50[][], t60[][],t70[][],t_end[][];   //连续变换矩阵相乘得到关节变化矩阵
        t20 = MetMuti44(t10, t21); t30 = MetMuti44(t20, t32); t40 = MetMuti44(t30, t43);
        t50 = MetMuti44(t40, t54); t60 = MetMuti44(t50, t65); t70 = MetMuti44(t60, t76);
        t_end = MetMuti44(t70,tt7);
        endpose = Mat_to_pos(t_end);  //矩阵转化为位姿
        //位姿输出
        x.setText((float)(round(endpose[0]*10))/10+""); y.setText((float)(round(endpose[1]*10))/10+""); z.setText((float)(round(endpose[2]*10))/10+"");//先乘10四捨五入再除以10，可以達到保存以為有效數字的效果
        rx.setText((float)(round(endpose[3]*10))/10+"");ry.setText((float)(round(endpose[4]*10))/10+""); rz.setText((float)(round(endpose[5]*10))/10+"");
    }

    public static void seekbar_change(int i){    //进行末端位姿控制时seekbar不能动，否则程序会认为是人在拖动，会干扰计算
        if(i==1)
        {
            seekbar_select[0].setProgress(Float.valueOf(joint_target[0]*10).intValue()+2000);
            seekbar_select[1].setProgress(Float.valueOf(joint_target[1]*10).intValue()+1800);
            seekbar_select[2].setProgress(Float.valueOf(joint_target[2]*10).intValue()+3600);
            seekbar_select[3].setProgress(Float.valueOf(joint_target[3]*10).intValue()+1800);
            seekbar_select[4].setProgress(Float.valueOf(joint_target[4]*10).intValue()+3600);
            seekbar_select[5].setProgress(Float.valueOf(joint_target[5]*10).intValue()+3600);
            seekbar_select[6].setProgress(Float.valueOf(joint_target[6]*10).intValue()+3600);
        }
    }
}
