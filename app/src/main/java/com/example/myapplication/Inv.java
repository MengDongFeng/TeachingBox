package com.example.myapplication;

import static com.example.myapplication.Constant.*;
import static com.example.myapplication.Fragment.OperateFragment.seekbar_change;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by 孟东风 on {DATE}.
 */

public class Inv {
/*    public static float[] Mat_to_pos(float T[][]) {   //矩阵转位姿
        float nx, ny, nz, ox, oy, oz, ax, ay, az, px, py, pz;
        nx = T[0][0];
        ox = T[0][1];
        ax = T[0][2];
        px = T[0][3];
        ny = T[1][0];
        oy = T[1][1];
        ay = T[1][2];
        py = T[1][3];
        nz = T[2][0];
        oz = T[2][1];
        az = T[2][2];
        pz = T[2][3];
        float ra, rb, rc;

        //zyz旋转
        rb =(float) (atan2(sqrt(pow(nz, 2) + pow(oz, 2)), az));
        if (abs(rb) < 0.0001) {
            ra = 0;
            rc = (float) (atan2(-ox, nx));
        } else if (abs(rb - PI) < 0.0001) {
            ra = 0;
            rc= (float) (atan2(ox, -nx));
        } else {
            rc =(float) (atan2(oz, -nz));
            ra =(float) (atan2(ay, ax));
        }
        return new float[] { px,py, pz,(float) (ra * 180 / PI),(float) (rb * 180 / PI),(float) (rc * 180 / PI)};
    }*/


    public static float[] Mat_to_pos(float T[][]){
        float  px, py, pz;
        px = T[0][3];
        py = T[1][3];
        pz = T[2][3];
        //xyz旋转
        float ra,rb,rc;
        ra=(float) -atan2(T[1][2],T[2][2]);
        rc=(float) -atan2(T[0][1],T[0][0]);
        rb=(float) atan2(T[0][2],(cos(ra)*T[2][2]-sin(ra)*T[1][2]));
        return new float[] { px,py, pz,(float) (ra * 180 / PI),(float) (rb * 180 / PI),(float) (rc * 180 / PI)};
    }

    public static float[][] Pos_to_mat(float pos[]) {           //位姿转矩阵
        float tt[][] = new float[4][4];
        tt[0][3] = pos[0];
        tt[1][3] = pos[1];
        tt[2][3] = pos[2];//位置
        float a = (float) (pos[3] * PI / 180), b = (float) (pos[4] * PI / 180), c = (float) (pos[5] * PI / 180);
       /* //ZYZ
        tt[0][0] = (float) (cos(a) * cos(b) * cos(c) - sin(a) * sin(c));
        tt[0][1] = (float) (-cos(a) * cos(b) * sin(c) - sin(a) * cos(c));
        tt[0][2] = (float) (cos(a) * sin(b));
        tt[1][0] = (float) (sin(a) * cos(b) * cos(c) + cos(a) * sin(c));
        tt[1][1] = (float) (-sin(a) * cos(b) * sin(c) + cos(a) * cos(c));
        tt[1][2] = (float) (sin(a) * sin(b));
        tt[2][0] = (float) (-sin(b) * cos(c));
        tt[2][1] = (float) (sin(b) * sin(c));
        tt[2][2] = (float) (cos(b));*/

        // xyz旋转顺序
        tt[0][0] =(float) (cos(b) * cos(c));
        tt[0][1] =(float) (-cos(b) * sin(c));
        tt[0][2] =(float) sin(b);
        tt[1][0] =(float) (sin(a) * sin(b) * cos(c) +
                cos(a) * sin(c));
        tt[1][1] =(float) (-sin(a) * sin(b) * sin(c) +
                cos(a) * cos(c));
        tt[1][2] =(float) (-sin(a) * cos(b));
        tt[2][0] =(float) (-cos(a) * sin(b) * cos(c) +
                sin(a) * sin(c));
        tt[2][1] =(float) (cos(a) * sin(b) * sin(c) +
                sin(a) * cos(c));
        tt[2][2] =(float) (cos(a) * cos(b));

        tt[3][0] = 0;
        tt[3][1] = 0;
        tt[3][2] = 0;
        tt[3][3] = 1;//最后一行的四位
        return tt;
    }

    public static float[][] MetMuti44(float a[][], float b[][]) { //矩阵相乘
        float c[][] = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                c[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    public static float[][] Inv_matrix44(float a[][]) {  //矩阵求逆
        float b[][] = new float[4][4];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                b[i][j] = a[j][i];
                //	temp[i][j] = in[i][i];
            }
        }
        for (int i = 0; i < 3; i++) {
            b[i][3] = -(b[i][0] * a[0][3] + b[i][1] * a[1][3] + b[i][2] * a[2][3]);
        }
        b[3][0] = 0.0f;
        b[3][1] = 0.0f;
        b[3][2] = 0.0f;
        b[3][3] = 1.0f;
        return b;
    }

    public static float[][] DH_mat(float a, float d, float alpha,float theta) {  //改进型DH参数描述法
        float[][] aa = new float[4][4];
        aa[0][0] = (float) (cos(theta));
        aa[0][1] = (float) (-sin(theta));
        aa[0][2] = 0;
        aa[0][3] = a;
        aa[1][0] = (float) (sin(theta) * cos(alpha));
        aa[1][1] = (float) (cos(theta) * cos(alpha));
        aa[1][2] = (float) (-sin(alpha));
        aa[1][3] = (float) (-d * sin(alpha));
        aa[2][0] = (float) (sin(theta) * sin(alpha));
        aa[2][1] = (float) (cos(theta) * sin(alpha));
        aa[2][2] = (float) (cos(alpha));
        aa[2][3] = (float) (d * cos(alpha));
        aa[3][0] = 0;
        aa[3][1] = 0;
        aa[3][2] = 0;
        aa[3][3] = 1;
        return aa;
    }
  /*  public static void DD_Calculate(float x, float y, float z)   //末端执行器姿态
    {
        float a = (float) (endpose[5] * PI / 180);   //rz
        float b = (float) (endpose[4] * PI / 180);  //ry
        float[][] DD=new float[3][3] ;
        if (abs(sin(b)) > 0.001)  //弧度b不为0时
        {
            DD[0][0] = (float) (-cos(a) * cos(b) / sin(b));
            DD[0][1] = (float) (-cos(b) * sin(a) / sin(b));
            DD[0][2] = 1;
            DD[1][0] = (float) (-sin(a));
            DD[1][1] = (float) (cos(a));
            DD[1][2] = 0;
            DD[2][0] = (float) (cos(a) / sin(b));
            DD[2][1] = (float) (sin(a) / sin(b));
            DD[2][2] = 0;
            float m = 0, n = 0, p = 0;  //分别 代表三个欧拉角上的增量
            p = DD[0][0] * x + DD[0][1] * y + DD[0][2] * z;
            n = DD[1][0] * x + DD[1][1] * y + DD[1][2] * z;
            m = DD[2][0] * x + DD[2][1] * y + DD[2][2] * z;
            float[] endpos_target = endpose;
            endpos_target[3] += m;
            endpos_target[4] += n;
            endpos_target[5] += p;
           // inv_Solve(endpos_target); //求反解
            beta=Beta_Cal(joint_current);
            invKIN(joint_current,endpos_target,beta);
            seekbar_change(0);
        }
    }*/




    //求两个向量的叉积
    public static float[] getCrossProduct(float[] in1,float[] in2)
    {
        float[] out=new float[3];
        //两矢量叉积的矢量在XYZ轴的分量ABC
        out[0] = in1[1] * in2[2] - in1[2] * in2[1];
        out[1] = in1[2] * in2[0] - in1[0] * in2[2];
        out[2] = in1[0] * in2[1] - in1[1] * in2[0];
        return out;
    }

    //向量规格化
    public static float[] vectorNormal(float[] vector) {
        //求向量的模
        float module = (float) sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new float[]{vector[0] / module, vector[1] / module, vector[2] / module};
    }
/*
    public static float[][] Kin(float[] Angle)
    {
        float[][] T1= {{(float) cos(Angle[0]),(float) -sin(Angle[0]), 0, 0}, {(float) sin(Angle[0]),(float) cos(Angle[0]), 0, 0}, {0, 0, 1.0f, 0}, {0, 0, 0, 1.0f}};
        float[][] T2 = {{(float) cos(Angle[1]),(float) -sin(Angle[1]), 0, 0}, {0, 0, 1.0f, 0}, {(float) -sin(Angle[1]),(float) -cos(Angle[1]), 0, 0},{0, 0, 0, 1.0f}};
        float[][] T3 = {{(float) cos(Angle[2]),(float) -sin(Angle[2]), 0, 0}, {0, 0, -1.0f, -d3}, {(float) sin(Angle[2]),(float) cos(Angle[2]), 0, 0},{0, 0, 0, 1.0f}};
        float[][] T4 = {{(float) cos(Angle[3]),(float) -sin(Angle[3]), 0, 0}, {0, 0, 1.0f, 0}, {(float) -sin(Angle[3]),(float) -cos(Angle[3]), 0, 0},{0, 0, 0, 1.0f}};
        float[][] T5 = {{(float) cos(Angle[4]),(float) -sin(Angle[4]), 0, 0}, {0, 0, -1.0f, -d5}, {(float) sin(Angle[4]),(float) cos(Angle[4]), 0, 0}, {0, 0, 0, 1.0f}};
        float[][] T6 = {{(float) cos(Angle[5]),(float) -sin(Angle[5]), 0, 0}, {0, 0, 1.0f, 0}, {(float) -sin(Angle[5]),(float) -cos(Angle[5]), 0, 0},{0, 0, 0, 1.0f}};
        float[][] T7 = {{(float) cos(Angle[6]),(float) -sin(Angle[6]), 0, 0}, {0, 0, -1.0f, 0}, {(float) sin(Angle[6]),(float) cos(Angle[6]), 0, 0},{0, 0, 0, 1.0f}};
        float[][] T_Hand = {{1.0f, 0.0f, 0, Tool_Position[0]}, {0, 1.0f, 0.0f, Tool_Position[1]}, {0.0f, 0.0f, 1.0f, Tool_Position[2]},{0, 0, 0, 1.0f}};


        float[][] T02, T03, T04, T05, T06, T07,TransMatrix;

        T02=MetMuti44(T1,T2);
        T03=MetMuti44(T02,T3);
        T04=MetMuti44(T03,T4);
        T05=MetMuti44(T04,T5);
        T06=MetMuti44(T05,T6);
        T07=MetMuti44(T06,T7);
        TransMatrix=MetMuti44(T07,T_Hand);

        return TransMatrix;
    }
*/
    public static void DD_Calculate(float x, float y, float z){
        float[] endpos_target=endpose;
        endpos_target[3]+=x;
        endpos_target[4]+=y;
        endpos_target[5]+=z;
        inv_calculate(m_theta,endpos_target);
        seekbar_change(0);
    }

    public static void inv_calculate(float[] angle,float[] end_position){
        beta=Beta_Cal(angle);
        invKIN(angle,end_position,beta);
    }

    public static void invKIN(float[] Angle_now, float[] end_position,float Beta)
    {
        float px, py, pz, alfa ;
        float[] ang1 = new float[8],ang2 = new float[8],ang3 = new float[8],ang4 = new float[8],ang5 = new float[8],ang6 = new float[8],ang7 = new float[8];
        float[] p=new float[3],p_unit;
        float x0[] ={1.0f, 0.0f, 0.0f };
        float[] a=new float[3],a_unit=new float[3], a_unit_vertical=new float[3], b_unit=new float[3], z3=new float[3], y3_1=new float[3], y3=new float[3],  x3 =new float[3];
        float[][] T1=new float[4][4], T2=new float[4][4], T3=new float[4][4], T4=new float[4][4], T02=new float[4][4], T03=new float[4][4], T04=new float[4][4], T07=new float[4][4], T57_2_1=new float[4][4], T57_2_2=new float[4][4], INV_T04=new float[4][4];
        float[][] T_Hand_inv ={{ 1.0f, 0.0f, 0, -Tool_Position[0]},{ 0, 1.0f, 0.0f, -Tool_Position[1] },{ 0.0f, 0.0f, 1.0f, -Tool_Position[2] },{ 0, 0, 0, 1.0f }};
        //double T_Hand_inv[4][4] = { { 1.0, 0.0, 0, 0 }, { 0, 1.0, 0.0, 0 }, { 0.0, 0.0, 1.0, 0 }, { 0, 0, 0, 1.0 } };
        float[][] TransMatrix;
       /* float[] Angle_now=new float[7];
        for (int i = 0; i < 7; i++) {
            Angle_now[i]=(float) (angle_now[i]*PI/180);
        }*/

        TransMatrix =Pos_to_mat(end_position);

        //初始化数组T1，T2，T3,T4
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                T1[i][j] = 0.0f;//初始化数组全为0
                T2[i][j] = 0.0f;
                T3[i][j] = 0.0f;
                T4[i][j] = 0.0f;
            }
        }

        T07=MetMuti44(TransMatrix, T_Hand_inv);//新矩阵相乘，T07输出

        px = T07[0][3];
        py = T07[1][3];
        pz = T07[2][3];

        float norm;
        norm=px * px + py * py + pz * pz;

        alfa =(float) acos(((norm) + d[2] * d[2] - d[4] * d[4]) / (2.0f * d[2] * sqrt(norm)));
        //ang4有两组解，限定为只能大于0度，与人运动相仿
        ang4[0] =(float) -acos(((norm) - d[2] * d[2] - d[4] * d[4]) / (2.0 * d[2] * d[4]));  //先求角4
        ang4[1] = ang4[0];
        ang4[2] = ang4[0];
        ang4[3] = ang4[0];
        ang4[4] =(float) acos(((norm) - d[2] * d[2] - d[4] * d[4]) / (2.0 * d[2] * d[4]));
        ang4[5] = ang4[4];
        ang4[6] = ang4[4];
        ang4[7] = ang4[4];

        p[0] = px;
        p[1] = py;
        p[2] = pz;

        p_unit=vectorNormal(p);

       /* for (i = 0; i < 3; i++)
        {
            p_unit[i] =(float) (p[i] / sqrt(px * px + py * py + pz * pz));//unit单位
        }*/

        a=getCrossProduct(x0,p_unit);
        for (int i = 0; i < 3; i++) {
            a_unit[i] =(float) (a[i] / sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]));
        }

        a_unit_vertical=getCrossProduct(p_unit, a_unit);//a_unit_vertical = cross(p_unit, a_unit);

        for (int i = 0; i < 3; i++) {
            b_unit[i] =(float) (a_unit[i] * cos(Beta) + a_unit_vertical[i] * sin(Beta));
            z3[i] =(float) (p_unit[i] * cos(alfa) + b_unit[i] * sin(alfa));
        }

        if (abs(ang4[0]) < 1e-8)        //再角2
        {
            ang2[0] =(float) acos(z3[2]);
            ang2[1] = ang2[0];
            ang2[2] = -ang2[0];
            ang2[3] = -ang2[0];

            for (int i = 0; i < 4; i++) {
                ang3[i]=Angle_now[2];
                if (abs(ang2[i]) < 1e-6)   //角1
                {
                    ang1[i] = Angle_now[0];
                } else {
                    ang1[i] =(float) atan2(z3[1] / sin(ang2[i]), z3[0] / sin(ang2[i]));
                }
            }
        } else {
            y3_1=getCrossProduct(p_unit, z3);// y3_1 = cross(p_unit,z3);
            for (int i = 0; i < 3; i++) {
                y3[i] =(float) (y3_1[i] / sqrt(y3_1[0] * y3_1[0] + y3_1[1] * y3_1[1] + y3_1[2] * y3_1[2]));
            }

            x3=getCrossProduct(y3, z3);//   xx3 = cross(y3, z3);
            //T32 = [xx3 y3 z3];

            //		T32 = [x3 y3 z3];自带

            ang2[0] =(float) acos(z3[2]);// x2(1) = acos(T32(3,3));
            ang2[1] = ang2[0];
            ang2[2] = -ang2[0];
            ang2[3] = -ang2[0];

            for (int i = 0; i < 4; i++) {
                if (abs(ang2[i]) < 1e-6) {
                    ang1[i] = Angle_now[0];
                    ang3[i] =(float) (atan2(x3[1], x3[0]) - ang1[i]);
                } else {
                    ang1[i] =(float) atan2(z3[1] / sin(ang2[i]), z3[0] / sin(ang2[i]));//sin(ang2[i])=arm
                    ang3[i] =(float) atan2(y3[2] / sin(ang2[i]), -x3[2] / sin(ang2[i]));//x1(i) = atan2(T32(2,3)/sin(x2(i)), T32(1,3)/sin(x2(i)));
                    //x3(i) = atan2(T32(3, 2) / sin(x2(i)), -T32(3, 1) / sin(x2(i)));
                }
            }
        }

        //i=0
        T1[0][0]= (float) cos(ang1[0]);
        T1[0][1] =(float) -sin(ang1[0]);
        T1[1][0] =(float) sin(ang1[0]);
        T1[1][1] =(float) cos(ang1[0]);
        T1[2][2] = 1.0f;
        T1[3][3] = 1.0f;

        T2[0][0] =(float) cos(ang2[0]);
        T2[0][1] = (float) -sin(ang2[0]);
        T2[1][2] = 1.0f;
        T2[2][0] = (float) -sin(ang2[0]);
        T2[2][1] = (float) -cos(ang2[0]);
        T2[3][3] = 1.0f;

        T3[0][0] =(float) cos(ang3[0]);
        T3[0][1] = (float) -sin(ang3[0]);
        T3[1][2] = -1.0f;
        T3[2][0] =(float) sin(ang3[0]);
        T3[2][1] =(float) cos(ang3[0]);
        T3[3][3] = 1.0f;

        T4[0][0] =(float) cos(ang4[0]);
        T4[0][1] =(float) -sin(ang4[0]);
        T4[1][2] = 1.0f;
        T4[2][0] =(float) -sin(ang4[0]);
        T4[2][1] =(float) -cos(ang4[0]);
        T4[3][3] = 1.0f;

        T02=MetMuti44(T1,T2);
        T03=MetMuti44(T02,T3);
        T04=MetMuti44(T03,T4);
        //T04 = T1*T2*T3*T4;

        INV_T04=Inv_matrix44(T04);//求逆矩阵，INV_T04 = inv(T04);
        T57_2_1=MetMuti44(INV_T04, TransMatrix);//T57_2_1 = INV_T04*T07;

        //i=2
        T1[0][0] =(float) cos(ang1[2]);
        T1[0][1] =(float) -sin(ang1[2]);
        T1[1][0] =(float) sin(ang1[2]);
        T1[1][1] =(float) cos(ang1[2]);
        T1[2][2] = 1.0f;
        T1[3][3] = 1.0f;//4x4矩阵

        T2[0][0] =(float) cos(ang2[2]);
        T2[0][1] =(float) -sin(ang2[2]);
        T2[1][2] = 1.0f;
        T2[2][0] =(float) -sin(ang2[2]);
        T2[2][1] =(float) -cos(ang2[2]);
        T2[3][3] = 1.0f;

        T3[0][0] =(float) cos(ang3[2]);
        T3[0][1] =(float) -sin(ang3[2]);
        T3[1][2] = -1.0f;
        T3[2][0] =(float) sin(ang3[2]);
        T3[2][1] =(float) cos(ang3[2]);
        T3[3][3] = 1.0f;

        T4[0][0] =(float) cos(ang4[2]);
        T4[0][1] =(float) -sin(ang4[2]);
        T4[1][2] = 1.0f;
        T4[2][0] =(float) -sin(ang4[2]);
        T4[2][1] =(float) -cos(ang4[2]);
        T4[3][3] = 1.0f;

        T02=MetMuti44(T1,T2);
        T03=MetMuti44(T02,T3);
        T04=MetMuti44(T03,T4);
        //T04 = T1*T2*T3*T4;

        INV_T04=Inv_matrix44(T04);//求逆矩阵，INV_T04 = inv(T04);
        T57_2_2=MetMuti44(INV_T04, TransMatrix);//T57_2_2 = INV_T04*T07;

        ang6[0] =(float) acos(-T57_2_1[1][2]);
        ang6[1] =(float) -acos(-T57_2_1[1][2]);
        ang6[2] =(float) acos(-T57_2_2[1][2]);
        ang6[3] =(float) -acos(-T57_2_2[1][2]);

        for (int i = 0; i < 2; i++) {
            if (abs(ang6[i]) < 1e-6) {
                ang5[i] = Angle_now[4];
                ang7[i] =(float) atan2(T57_2_1[2][0], T57_2_1[0][0]) - ang5[i];
            } else {
                ang5[i] =(float) atan2(T57_2_1[2][2] / sin(ang6[i]), T57_2_1[0][2] / sin(ang6[i]));
                ang7[i] =(float) atan2(-T57_2_1[1][1] / sin(ang6[i]), T57_2_1[1][0] / sin(ang6[i]));
            }
        }

        for (int i = 2; i < 4; i++) {
            if (abs(ang6[i]) < 1e-6) {
                ang5[i] = Angle_now[4];
                ang7[i] =(float) atan2(T57_2_2[2][0], T57_2_2[0][0]) - ang5[i];
            } else {
                ang5[i] =(float) atan2(T57_2_2[2][2] / sin(ang6[i]), T57_2_2[0][2] / sin(ang6[i]));
                ang7[i] =(float) atan2(-T57_2_2[1][1] / sin(ang6[i]), T57_2_2[1][0] / sin(ang6[i]));
            }
        }

        //后4组解
        if (abs(ang4[0]) < 1e-8) {
            ang2[4] =(float) acos(z3[2]);
            ang2[5] = ang2[0];
            ang2[6] = -ang2[0];
            ang2[7] = -ang2[0];

            for (int i = 4; i < 8; i++) {
                ang3[i] = Angle_now[2];// x3(i) = angle_in(3);
                if (abs(ang2[i]) < 1e-6) {
                    ang5[i] = Angle_now[4];
                } else {
                    ang1[i] =(float) atan2(z3[1] / sin(ang2[i]), z3[0] / sin(ang2[i]));
                }
            }
        } else {
            y3_1=getCrossProduct( z3,p_unit);// y3_1 = cross(p_unit,z3);
            for (int i = 0; i < 3; i++) {
                y3[i] =(float) (y3_1[i] / sqrt(y3_1[0] * y3_1[0] + y3_1[1] * y3_1[1] + y3_1[2] * y3_1[2]));
            }
            x3=getCrossProduct(y3,z3);//xx3 = cross(y3, z3);

            //		T32 = [x3 y3 z3];自带

            ang2[4] =(float) acos(z3[2]);
            ang2[5] = ang2[0];
            ang2[6] = -ang2[0];
            ang2[7] = -ang2[0];

            for (int i = 4; i < 8; i++) {
                if (abs(ang2[i]) < 1e-6) {
                    ang1[i] = Angle_now[0];
                    ang3[i] =(float) atan2(x3[1], x3[0]) - ang1[i];
                } else {
                    ang1[i] =(float) atan2(z3[1] / sin(ang2[i]), z3[0] / sin(ang2[i]));
                    ang3[i] =(float) atan2(y3[2] / sin(ang2[i]), -x3[2] / sin(ang2[i]));
                }
            }
        }

        //i=4
        T1[0][0] =(float) cos(ang1[4]);
        T1[0][1] =(float) -sin(ang1[4]);
        T1[1][0] =(float) sin(ang1[4]);
        T1[1][1] =(float) cos(ang1[4]);
        T1[2][2] = 1.0f;
        T1[3][3] = 1.0f;

        T2[0][0] =(float) cos(ang2[4]);
        T2[0][1] =(float) -sin(ang2[4]);
        T2[1][2] = 1.0f;
        T2[2][0] =(float) -sin(ang2[4]);
        T2[2][1] =(float) -cos(ang2[4]);
        T2[3][3] = 1.0f;

        T3[0][0] =(float) cos(ang3[4]);
        T3[0][1] =(float) -sin(ang3[4]);
        T3[1][2] = -1.0f;
        T3[2][0] =(float) sin(ang3[4]);
        T3[2][1] =(float) cos(ang3[4]);
        T3[3][3] = 1.0f;

        T4[0][0] =(float) cos(ang4[4]);
        T4[0][1] =(float) -sin(ang4[4]);
        T4[1][2] = 1.0f;
        T4[2][0] =(float) -sin(ang4[4]);
        T4[2][1] =(float) -cos(ang4[4]);
        T4[3][3] = 1.0f;

        T02=MetMuti44(T1,T2);
        T03=MetMuti44(T02,T3);
        T04=MetMuti44(T03,T4);
        //T04 = T1*T2*T3*T4;

        INV_T04=Inv_matrix44(T04);//求逆矩阵，INV_T04 = inv(T04);
        T57_2_1=MetMuti44(INV_T04, TransMatrix);//T57_2_1 = INV_T04*T07;

        //i=6
        T1[0][0] =(float) cos(ang1[6]);
        T1[0][1] =(float) -sin(ang1[6]);
        T1[1][0] =(float) sin(ang1[6]);
        T1[1][1] =(float) cos(ang1[6]);
        T1[2][2] = 1.0f;
        T1[3][3] = 1.0f;

        T2[0][0] =(float) cos(ang2[6]);
        T2[0][1] =(float) -sin(ang2[6]);
        T2[1][2] = 1.0f;
        T2[2][0] =(float) -sin(ang2[6]);
        T2[2][1] =(float) -cos(ang2[6]);
        T2[3][3] = 1.0f;

        T3[0][0] =(float) cos(ang3[6]);
        T3[0][1] =(float) -sin(ang3[6]);
        T3[1][2] = -1.0f;
        T3[2][0] =(float) sin(ang3[6]);
        T3[2][1] =(float) cos(ang3[6]);
        T3[3][3] = 1.0f;

        T4[0][0] =(float) cos(ang4[6]);
        T4[0][1] =(float) -sin(ang4[6]);
        T4[1][2] = 1.0f;
        T4[2][0] =(float) -sin(ang4[6]);
        T4[2][1] =(float) -cos(ang4[6]);
        T4[3][3] = 1.0f;

        T02=MetMuti44(T1,T2);
        T03=MetMuti44(T02,T3);
        T04=MetMuti44(T03,T4);
        //T04 = T1*T2*T3*T4;

        INV_T04=Inv_matrix44(T04);//求逆矩阵，INV_T04 = inv(T04);
        T57_2_2=MetMuti44(INV_T04, TransMatrix);//T57_2_2 = INV_T04*T07;

        ang6[4] =(float) acos(-T57_2_1[1][2]);
        ang6[5] =(float) -acos(-T57_2_1[1][2]);
        ang6[6] =(float) acos(-T57_2_2[1][2]);
        ang6[7] =(float) -acos(-T57_2_2[1][2]);

        for (int i = 4; i < 6; i++) {
            if (abs(ang6[i]) < 1e-6) {
                ang5[i] = Angle_now[4];
                ang7[i] =(float) atan2(T57_2_1[2][0], T57_2_1[0][0]) - ang5[i];
            } else {
                ang5[i] =(float) atan2(T57_2_1[2][2] / sin(ang6[i]), T57_2_1[0][2] / sin(ang6[i]));
                ang7[i] =(float) atan2(-T57_2_1[1][1] / sin(ang6[i]), T57_2_1[1][0] / sin(ang6[i]));
            }
        }

        for (int i = 6; i < 8; i++) {
            if (abs(ang6[i]) < 1e-6) {
                ang5[i] = Angle_now[4];
                ang7[i] =(float) atan2(T57_2_2[2][0], T57_2_2[0][0]) - ang5[i];
            } else {
                ang5[i] =(float) atan2(T57_2_2[2][2] / sin(ang6[i]), T57_2_2[0][2] / sin(ang6[i]));
                ang7[i] =(float) atan2(-T57_2_2[1][1] / sin(ang6[i]), T57_2_2[1][0] / sin(ang6[i]));
            }
        }

        float[][] Solve=new float[8][7];
        for (int i = 0; i < 8; i++) {
            Solve[i][0] = ang1[i];
            Solve[i][1] = ang2[i];
            Solve[i][2] = ang3[i];
            Solve[i][3] = ang4[i];
            Solve[i][4] = ang5[i];
            Solve[i][5] = ang6[i];
            Solve[i][6] = ang7[i];
        }
//    for (i = 0; i < 8;i++)
//    {
//    Solve[i][0] =(float) (Solve[i][0] * 180 / PI);
//    Solve[i][1] =(float) (Solve[i][1] * 180 /PI);
//    Solve[i][2]=(float) (Solve[i][2] * 180 / PI);
//    Solve[i][3] =(float) (Solve[i][3] * 180 / PI);
//    Solve[i][4] =(float) (Solve[i][4] * 180 / PI);
//    Solve[i][5] =(float) (Solve[i][5] * 180 / PI);
//    Solve[i][6] =(float) (Solve[i][6] * 180 / PI);
//    }
        int index = ChooseSolve(Angle_now, Solve);
        for (int i = 0; i < 7; i++) {
            joint_target[i]=(float) ((Solve[index][i]-theta[i]-theta_0[i])*180/PI);
        }
    }

    public static int ChooseSolve(float[] Angle_now, float[][] Solve)
    {
        float MinSum = 10000.0f;
        int MinIndex = -1;
        int index, i;
        // 	for (int j = 0; j<8; j++)
        // 	{
        // 		if ((abs(Solve[j][0])<1.57) && (abs(Solve[j][1])<1.57) && (abs(Solve[j][2])<1.57) && (abs(Solve[j][3])<1.57) && (abs(Solve[j][4])<2.79) && (abs(Solve[j][5])<1.57) && (abs(Solve[j][6])<2.79))
        // 		{
        //
        // 		}
        // 	}

        for (index = 0; index < 8; index++)
        {
            float AngleSum=0;
            for (i = 0; i < 7; i++)			// 计算当前解的关节运动总量
            {
                AngleSum += abs(Solve[index][i] - Angle_now[i]);
            }
            if (AngleSum < MinSum)
            {
                MinSum = AngleSum;
                MinIndex = index;
            }
        }
        return MinIndex;    //选出解的索引
    }

    public static float Beta_Cal(float[] angle)
    {
      /*  float[] angle=new float[7];

        for(int i=0;i<7;i++){
            angle[i]=(float) (Angle[i]*PI/180);
        }*/
        int i;
        //double beta;
        float[][] T1 = { {(float) cos(angle[0]),(float) -sin(angle[0]), 0, 0 }, {(float) sin(angle[0]),(float) cos(angle[0]), 0, 0 }, { 0, 0, 1.0f, 0 }, { 0, 0, 0, 1.0f } };
        float[][] T2 = { {(float) cos(angle[1]),(float) -sin(angle[1]), 0, 0 }, { 0, 0, 1.0f, 0 }, {(float) -sin(angle[1]),(float) -cos(angle[1]), 0, 0 }, { 0, 0, 0, 1.0f } };
        float[][] T3 = { {(float) cos(angle[2]),(float) -sin(angle[2]), 0, 0 }, { 0, 0, -1.0f, -450 }, {(float) sin(angle[2]),(float) cos(angle[2]), 0, 0 }, { 0, 0, 0, 1.0f } };
        float[][] T4 = { {(float) cos(angle[3]),(float) -sin(angle[3]), 0, 0 }, { 0, 0, 1.0f, 0 }, {(float) -sin(angle[3]),(float) -cos(angle[3]), 0, 0 }, { 0, 0, 0, 1.0f } };
        float[][] T5 = { {(float) cos(angle[4]),(float) -sin(angle[4]), 0, 0 }, { 0, 0, -1.0f, -440 }, {(float) sin(angle[4]),(float) cos(angle[4]), 0, 0 }, { 0, 0, 0, 1.0f } };
        float px, py, pz;
        float[] x0 = { 1.0f, 0.0f, 0.0f };
        float[] a, a_unit_vertical;
        float[] a_unit=new float[3];
        float[] p3=new float[3];
        float[] b_origin=new float[3];
        float[] p_unit=new float[3];
        float beta_x,beta_y,dot_punit_p3;
        float[][] T02, T03, T04, T05;
        float beta_origin;

        T02=MetMuti44(T1, T2);
        T03=MetMuti44(T02, T3);
        T04=MetMuti44(T03, T4);
        T05=MetMuti44(T04, T5);

        px = T05[0][3];
        py = T05[1][3];
        pz = T05[2][3];

        float norm=px*px + py*py + pz*pz;
        p_unit[0] =(float) (px/sqrt(norm));
        p_unit[1] =(float) (py/sqrt(norm));
        p_unit[2] =(float) (pz/sqrt(norm));

        a=getCrossProduct(x0, p_unit);
        for (i = 0; i < 3; i++)
        {
            a_unit[i] =(float) (a[i] / sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]));
        }
        //cross( a_unit,p_unit,a_unit_vertical);
        a_unit_vertical=getCrossProduct(p_unit, a_unit);
        for (i = 0; i < 3; i++)
        {
            p3[i] = T03[i][3];
        }

//        dot_punit_p3 = p_unit[0]*p3[0] + p_unit[1]*p3[1] + p_unit[2]*p3[2];
//
//        for(i=0; i<3; i++)
//        {
//            b_origin[i] = p3[i] - dot_punit_p3*p_unit[i];
//        }


        for (i = 0; i < 3; i++)
        {
            b_origin[i] = p3[i];
        }

        beta_x = b_origin[0] * a_unit[0] + b_origin[1] * a_unit[1] + b_origin[2] * a_unit[2];
        beta_y = b_origin[0] * a_unit_vertical[0] + b_origin[1] * a_unit_vertical[1] + b_origin[2] * a_unit_vertical[2];

        beta_origin =(float) atan2(beta_y, beta_x);
        //beta = beta_origin;
        return beta_origin;
    }
}