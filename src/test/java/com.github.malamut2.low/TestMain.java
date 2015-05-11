package com.github.malamut2.low;

import java.util.ArrayList;

/**
 * TestMain
 * @author kgb
 */
public class TestMain {

    public static void main(String[] args) {
        int[] tests = new int[10000];
        for (int i = 0; i < tests.length; i++) {
            tests[i]++;
        }
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < tests.length; i++) {
            al.add(Integer.toString(tests[i]));
        }
        String[] st = new String[10000];
        System.out.println(al.size() + st.length);
    }

}
