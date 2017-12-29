package com.piyush.lintbuilder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

@SuppressLint("Registered")
public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GroupStateEnum enumS =  GroupStateEnum.ACTIVE;
        if(DemoEnum.ENUM1.equals(GroupStateEnum.ACTIVE)){

        }
    }
}

