package com.example.myapplication.Fragment;



import android.os.Bundle;
import android.os.Message;
/*import android.support.annotation.NonNull;
import android.support.annotation.Nullable;*/
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.example.myapplication.R;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

import android.os.StrictMode;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;


import static com.example.myapplication.Constant.endpos_from_pc;
import static com.example.myapplication.Constant.joint_from_pc;


public class RealClientFragment extends Fragment {
    
    public static final String TAG="RealClientFragment";

    public static boolean net_connected=false;
    private TextView txtReceiveInfo;
    private EditText edtRemoteIP,edtRemotePort;
    private Button Connect;
    private boolean isConnected =false;
    private Socket socketClient =null;
    static PrintWriter printWriterClient=null;
    static OutputStream outputStreamClient;
    static BufferedReader bufferedReaderClient =null;
    private static String receiveInfoClient;
    private String receiveInfoServer;

    static String[] array = new String[320];

    private MainActivity mainActivity=(MainActivity) getActivity();


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realclient, container,false);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        Connect=(Button) view.findViewById(R.id.Connect);
        //        robot_run=(Button) view.findViewById(R.id.robot_run);
        txtReceiveInfo=(TextView) view.findViewById(R.id.textReceiveInfo);
        edtRemoteIP=(EditText) view.findViewById(R.id.editRemoteIP);
        edtRemotePort=(EditText) view.findViewById(R.id.edtRemotePort);

        Log.d(TAG, "onCreateView: ");

        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected)   //if 网络已连接，点击按钮网络断开，
                {
                    isConnected=false;
                    if(socketClient!=null) //如果客户端不为空
                    {
                        try
                        {
                            socketClient.close();
                            socketClient=null;
                            printWriterClient.close();
                            printWriterClient = null;
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    new tcpClientThread().interrupt();//中断线程的执行，使线程进入死亡状态
                    Connect.setText("开始连接");
                    edtRemoteIP.setEnabled(true);
                    edtRemotePort.setEnabled(true);
                    txtReceiveInfo.setText("ReceiveInfo:\n");
                }
                else
                {
                    isConnected=true;
                    Connect.setText("停止连接");
                    edtRemoteIP.setEnabled(false);
                    edtRemotePort.setEnabled(false);
//                    new tcpClientThread().start();  //线程开启
                    new tcpClientThread().start();
                }
            }
        });

        return view;
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }*/

    //向PC发送数据
    public static void send_to_pc(float[] index)
    {
        if(net_connected)
        {
            String dot = ",  ";
            String message_to_pc = index[0]+dot+index[1]+dot+index[2]+dot+index[3]+dot+index[4]+dot+index[5]+dot+index[6]+dot+index[7]+dot;
            try{
                outputStreamClient .write(message_to_pc.getBytes());
//                receiveInfoClient = "发送信息 "+"\""+message_to_pc+"\""+"\n";//消息换行
            }
            catch (Exception e){
 //               receiveInfoClient = e.getMessage() + "\n";
//                printWriterClient.print(message_to_pc);//发送给服务器
                e.printStackTrace();
            }
        }
    }

    private class tcpClientThread extends Thread{
        public void run() {
            try {
                socketClient = new Socket(edtRemoteIP.getText().toString(), Integer.parseInt(edtRemotePort.getText().toString()));
                outputStreamClient = socketClient.getOutputStream();   //获取输出流
                //取得输入、输出流
                bufferedReaderClient = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                printWriterClient = new PrintWriter(socketClient.getOutputStream(), true); //缓冲字符输出流，行自动刷新
                receiveInfoClient = "连接服务器成功!\n";
                Message msg = new Message();
                msg.what=0x123;
                handler.sendMessage(msg);
            } catch (Exception e ){
                receiveInfoClient = e.getMessage() + "\n";
                Message msg = new Message();
                msg.what=0x123;
                handler.sendMessage(msg);
                return;
            }

            char[] buffer = new char[1024];
            int count;
            while (isConnected)
            {
                net_connected = true;
                try
                {
                    if((count = bufferedReaderClient.read(buffer))>0)
                    {
                        receiveInfoClient = "接收信息 "+"\""+getInfoBuff(buffer, count)+"\""+"\n";//消息换行
                        //同时将接收到的信息转化成六个数显示在框中//将字符串用“,”分拆，转成六个string,然后再转成float
                        StringTokenizer token = new StringTokenizer(getInfoBuff(buffer, count),",");  //按照逗号进行截取
                        int i = 0;
                        while(token.hasMoreElements()){
                            array[i] = token.nextToken();
                            i++;
                        }
                        if(i<14){//通信偶尔有错，会把两次发的信息并在一起过来，容易报错，此处加以差别
                            for(int m=0; m<6; m++){
                                //if(array[m].indexOf(".",array[m].indexOf("."))==-1) //保证是浮点数，否则可能会出错
                                //{
                                joint_from_pc[m] = Float.parseFloat(array[m]);
                                endpos_from_pc[m] = Float.parseFloat(array[m + 6]);
                                //}
                            }
                        }
                        Message msg = new Message();
                        msg.what=0x123;
                        handler.sendMessage(msg);
                    }
                }
                catch (Exception e)
                {
                    receiveInfoClient = e.getMessage() + "\n";
                    Message msg = new Message();
                    msg.what=0x123;
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    }

/*    Handler handler=new Handler() {
        public void handleMessage(Message msg){
           if (msg.what==0x123){
               txtReceiveInfo.append("TCPClient: "+receiveInfoClient);

               OperateFragment operateFragment=(OperateFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.operate_layout);
               operateFragment.netCondition.append("TCPClient: "+receiveInfoClient);
//                OperateFragment operateFragment=(OperateFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.operate_layout);
//                 operateFragment.netCondition.append("TCPClient: "+receiveInfoClient);
           }
        }
    };*/

    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what==0x123)
            {
                txtReceiveInfo.append("TCPClient: "+receiveInfoClient);	// 刷新
            }
            if(msg.what==0x456)
            {
                txtReceiveInfo.append("TCPServer: "+receiveInfoServer);
            }
        }
    };

    private String getInfoBuff(char[] buff, int count) {

        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i] = buff[i];
        }
        return new String(temp);
    }

 }



































/*
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import static com.example.myapplication.Constant.*;

*/
/**
 * <pre>
 *     author : 孟东风
 *     time   : 2019/12/11
 *     project_name  :TeachingBox4
 *     desc   :
 *//*

public class RealClientFragment extends Fragment {

    private TextView txtReceiveInfo;
    private EditText edtRemoteIP,edtRemotePort;
    private Button Connect;

    boolean isConnected=false;  //是否连接;初始化连接状态
    private Socket socketClient=null;    //初始化socketClient,socket
    private String receiveInfoClient,receiveInfoServer;  //接受客户端字符串信息
    static BufferedReader bufferedReaderClient	= null,bufferedReaderServer=null;  //客户端读
    static PrintWriter printWriterClient = null,printWriterServer=null;   //客户端写
    static OutputStream Chuanshu;                               //输出流，发送给服务器
    static boolean net_connected = false;      //初始化网络连接状况
    private View view;

    static String[] array = new String[320];  //存放六个数字string型

    */
/**
     * 加载布局
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     *//*

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_realclient, container, false);
        */
/*
        StrictMode是一个开发人员工具，它可以检测出您可能无意中做的事情，并将其提交给您，以便您可以修复它们。
        StrictMode最常用来捕获应用程序主线程上意外的磁盘或网络访问，在这里接收UI操作并执行动画。
        将磁盘和网络操作与主线程分离，可以使应用程序更加流畅、响应速度更快。通过保持应用程序的主线程响应性，还可以防止ANR对话框显示给用户。
         *//*

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //构造StrictMode
                .detectDiskReads() //当发生磁盘读操作时输出
                .detectDiskWrites()  //当发生磁盘写操作时输出
                .detectNetwork()  ////访问网络时输出，这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
                .penaltyLog()  //以日志的方式输出
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()   //探测SQLite数据库操作
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        Connect=(Button) view.findViewById(R.id.Connect);
//        robot_run=(Button) view.findViewById(R.id.robot_run);
        txtReceiveInfo=(TextView) view.findViewById(R.id.textReceiveInfo);
        edtRemoteIP=(EditText) view.findViewById(R.id.editRemoteIP);
        edtRemotePort=(EditText) view.findViewById(R.id.edtRemotePort);

*/
/*        robot_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] index = {0,0,0,0,0,0,2};//末位2表示机器人启动
                send_to_pc(index);
            }
        });*//*


        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected)   //if 网络已连接，点击按钮网络断开，
                {
                    isConnected=false;
                    if(socketClient!=null) //如果客户端不为空
                    {
                        try
                        {
                            socketClient.close();
                            socketClient=null;
                            printWriterClient.close();
                            printWriterClient = null;
                        } catch (IOException e){
                        }
                    }
                    new tcpClientThread().interrupt();//中断线程的执行，使线程进入死亡状态
                    Connect.setText("开始连接");
                    edtRemoteIP.setEnabled(true);
                    edtRemotePort.setEnabled(true);
                    txtReceiveInfo.setText("ReceiveInfo:\n");
                }
                else
                {
                    isConnected=true;
                    Connect.setText("停止连接");
                    edtRemoteIP.setEnabled(false);
                    edtRemotePort.setEnabled(false);
                    new tcpClientThread().start();  //线程开启
                }
            }
        });
        return view;
    }

    //向PC发送数据
    public static void send_to_pc(float[] index)
    {
        if(net_connected)
        {
            String dot = ",  ";
            */
/*            String message_to_pc = index[0]+dot+index[1]+dot+index[2]+dot+index[3]+dot+index[4]+dot+index[5]+dot+index[6];*//*

            String message_to_pc = index[0]+dot+index[1]+dot+index[2]+dot+index[3]+dot+index[4]+dot+index[5];
            try{
                Chuanshu.write(message_to_pc.getBytes());
            }catch(Exception e){
                printWriterClient.print(message_to_pc);//发送给服务器
            }
        }
    }

*/
/*    public void ConnectButtonClick(View view)
    {

    }*//*


    //TCP客户端线程
    private class tcpClientThread extends Thread
    {
        public void run()
        {
            try  //使用自动关闭资源的try语句关闭文件输入
            {
                //连接服务器
                IP = edtRemoteIP.getText()+"";
                socketClient = new Socket(edtRemoteIP.getText().toString(), Integer.parseInt(edtRemotePort.getText().toString()));//将字符串转变为int
                Chuanshu = socketClient.getOutputStream();
                //取得输入、输出流
                bufferedReaderClient = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                printWriterClient = new PrintWriter(socketClient.getOutputStream(), true);

                //    printWriterClient = new PrintWriter(socketClient.getOutputStream(), true);
                receiveInfoClient = "连接服务器成功!\n";
                Message msg = new Message();
                msg.what=0x123;
                handler.sendMessage(msg);
            }
            catch (Exception e)
            {
                receiveInfoClient = e.getMessage() + "\n";
                Message msg = new Message();
                msg.what=0x123;
                handler.sendMessage(msg);
                return;
            }

            char[] buffer = new char[1024];   //buffer 缓冲//创建一个字符串
            int count;
            while (isConnected)
            {
                net_connected = true;
                try
                {
                    if((count = bufferedReaderClient.read(buffer))>0)   //count为客户端获得的缓冲数据的长度
                    {
                        receiveInfoClient = "接收信息 "+"\""+getInfoBuff(buffer, count)+"\""+"\n";//消息换行
                        //同时将接收到的信息转化成六个数显示在框中//将字符串用“,”分拆，转成六个string,然后再转成float
                        StringTokenizer token = new StringTokenizer(getInfoBuff(buffer, count),",");  //按照逗号进行截取
                        // StringTokenizer(String str, String delim) ：构造一个用来解析 str 的 StringTokenizer 对象，并提供一个指定的分隔符。
                        int i = 0;
                        while(token.hasMoreElements()){  //boolean hasMoreElements()：返回是否还有分隔符。
                            array[i] = token.nextToken();  //String nextToken()：返回从当前位置到下一个分隔符的字符串。
                            i++;
                        }

                        if(i<14){//通信偶尔有错，会把两次发的信息并在一起过来，容易报错，此处加以差别
                            for(int m=0; m<7; m++){
                                //if(array[m].indexOf(".",array[m].indexOf("."))==-1) //保证是浮点数，否则可能会出错
                                //{
                                joint_from_pc[m] = Float.parseFloat(array[m]);
                                endpos_from_pc[m] = Float.parseFloat(array[m + 7]);
                                //}
                            }
                        }
                        Message msg = new Message();
                        msg.what=0x123;
                        handler.sendMessage(msg);
                    }
                }
                catch (Exception e)
                {
                    receiveInfoClient = e.getMessage() + "\n";
                    Message msg = new Message();
                    msg.what=0x123;
                    handler.sendMessage(msg);
                    return;
                }
            }
        }

    }

    Handler handler = new  Handler(){
        public void handleMessage(Message msg)
        {
            if(msg.what==0x123)
            {
                txtReceiveInfo.append("TCPClient: "+receiveInfoClient);	// 刷新
//                OperateFragment operateFragment=(OperateFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.operate_layout);
//                operateFragment.netCondition.append("TCPClient: "+receiveInfoClient);
            }
            if(msg.what==0x456)
            {
                txtReceiveInfo.append("TCPServer: "+receiveInfoServer);
            }
        }
    };
    private String getInfoBuff(char[] buff, int count) {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i] = buff[i];
        }
        return new String(temp);
    }
}
*/
