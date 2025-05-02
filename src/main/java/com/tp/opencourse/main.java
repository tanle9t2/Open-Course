package com.tp.opencourse;

import java.util.*;

public class main {

    public static void main(String[] args) {
        //up casting
        Integer x1 = 2;
        Double d1 = x1.doubleValue();
        Double d2 = (double) x1;

        int x2 = 2;
        Double ef = ((Number) x2).doubleValue();
        Double ww = (double) x2;


        //down casting
        Double y1 = 2.0;
        Integer i1 = y1.intValue();
//        Integer i2 = (int) y1;    //error

        double y2 = 2.0;
        Integer i3 = ((Number) y2).intValue();
        Integer i4 = (int) y2;

    }
}

//explicit casting works for both upcasting and downcasting
//implicit casting only works with upcasting and primitive for downcasting