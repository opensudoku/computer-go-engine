/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opensudoku.go;

import com.opensudoku.go.exception.GoBadException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mark
 */
public class Group implements Coordinate {

    private final int color;
    private final int[] member = new int[361];
    private int cnt;

    public Group(int color) {
        this.color = color;
        cnt = 0;
    }

    public List<Integer> getList() {
        ArrayList list = new ArrayList();
        for (int k = 0; k < cnt; k++) {
            list.add(member[k]);
        }

        return list;
    }

    public int[] getMember() {
        return member;
    }

    public boolean isMember(int id) {
        for (int k = 0; k < cnt; k++) {
            if (member[k] == id) {
                return true;
            }
        }

        return false;
    }

    public int getColor() {
        return color;
    }

    public int get(int id) {
        return member[id];
    }

    /**
     * to ignore repeat number
     *
     * @param val
     */
    public void add(int val) {
        for (int k = 0; k < cnt; k++) {
            if (member[k] == val) {

                return;
            }
        }

        member[cnt++] = val;
    }

    public int size() {
        return cnt;
    }

    public void show() throws GoBadException {
//        StringBuilder sb = new StringBuilder();
//        for (int k = 0; k < cnt; k++) {
//            sb.append(member[k]).append(" ");
//        }
//        System.out.println("DEBUG...cnt=" + cnt);
//        System.out.println("DEBUG..." + sb.toString());
        Core core = new Core();

        for (int k = 0; k < cnt; k++) {
            core.play(member[k], BLACK);
        }

        core.show();

    }
}
