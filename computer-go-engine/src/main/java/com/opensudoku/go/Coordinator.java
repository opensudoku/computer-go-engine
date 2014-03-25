/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opensudoku.go;

import static com.opensudoku.go.Coordinate.SS;
import com.opensudoku.go.exception.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.opensudoku.go.exception.*;

/**
 *
 * @author mark
 */
public class Coordinator implements Coordinate {

    //
    /**
     * aa to ss, [col][row]
     */
    public List<String> ssList = new ArrayList<>();
    public List<String> t19List = new ArrayList<>();

    public List<Integer> getBasicAreaList(int areaId) throws GoBadNotOnBoardException {
        switch (areaId) {
            case CORNER_UPPER_RIGHT:
                return getAreaList("L18", "S11");
            case CORNER_LOWER_RIGHT:
                return getAreaList("L9", "S2");
            case CORNER_LOWER_LEFT:
                return getAreaList("B9", "J2");
            case CORNER_UPPER_LEFT:
                return getAreaList("B18", "J11");
            default:
                throw new GoBadNotOnBoardException("getBasicAreaList int=" + areaId);

        }
    }

    public List<Integer> getAreaList(String areaFrom, String areaTo) throws GoBadNotOnBoardException {
        List<Integer> list = new ArrayList<>();
        int p1 = t19List.indexOf(areaFrom);
        int p2 = t19List.indexOf(areaTo);
        if ((p1 < 0 || p1 > 360) || (p2 < 0 || p2 > 360)) {
            throw new GoBadNotOnBoardException("getAreaList from=" + areaFrom + "=" + p1 + " to=" + areaTo + "=" + p2);
        }

        int p1Row = ROW_NUM[p1];
        int p1Col = COL_NUM[p1];
        int p2Row = ROW_NUM[p2];
        int p2Col = COL_NUM[p2];

        for (int row = p1Row; row <= p2Row; row++) {
            for (int col = p1Col; col <= p2Col; col++) {
                list.add(19 * row + col);
            }
        }

        return list;
    }

    public List<Integer> getJosekiDataInList(String str) {
        /**
         * (;GM[1]FF[4] CA[UTF-8] AP[Quarry:0.2.0] SZ[19] KM[6.5] PB[Black]
         * PW[White] ;B[aa];W[sa];B[ss];W[as] )
         */

        String REGEX = ";";
        List<Integer> list = new ArrayList<>();
        Pattern p = Pattern.compile(REGEX);
        String[] items = p.split(str);
        int id = -1;
//        StringBuilder sb = new StringBuilder();
        for (String s : items) {
            if ((s.startsWith("B[")) || (s.startsWith("W["))) {
//                System.out.print(s.substring(2, 4));
//                sb.append(s.substring(2, 4));
                id = ssList.indexOf(s.substring(2, 4));
                list.add(id);
            }
        }
        return list;
    }

    public Coordinator() {

        for (int k = 0; k < 361; k++) {
            ssList.add(SS[k]);
            t19List.add(T19[k]);
        }

    }

    public int transform(int style, int id) throws GoBadNotOnBoardException {
        int newId = -1;

        int row = ROW_NUM[id];
        int col = COL_NUM[id];

        /*
        
         0. (x,y)       4. (y,x)         
         1. (x,18-y)    5. (y,18-x)
         2. (18-x,y)    6. (18-y,x)
         3. (18-x,18-y) 7. (18-y,18-x) 
        
        
         */
        switch (style) {
            case TRANSFORM_0:    // (row,col) (3,16)=>(3,16)
                // id= col+19*row
                // use (X, Y) perspective
                newId = (col) + CONST19 * (row);
                break;
            case TRANSFORM_1:    // (row,col) (3,16)=>(2,15)
                // id= col+19*row
                newId = (col) + CONST19 * (CONST18 - row);
                break;
            case TRANSFORM_2:    // (row,col) (3,16)=>(15,16)
                newId = (CONST18 - col) + CONST19 * (row);
                break;
            case TRANSFORM_3:    // (row,col) (3,16)=>(15,16)
                newId = (CONST18 - col) + CONST19 * (CONST18 - row);
                break;

            case TRANSFORM_4:    // (row,col) (3,16)=>(3,16)
                // id= col+19*row
                // use (X, Y) perspective
                newId = (row) + CONST19 * (col);
                break;
            case TRANSFORM_5:    // (row,col) (3,16)=>(2,15)
                // id= col+19*row
                newId = (row) + CONST19 * (CONST18 - col);
                break;
            case TRANSFORM_6:    // (row,col) (3,16)=>(15,16)
                newId = (CONST18 - row) + CONST19 * (col);
                break;
            case TRANSFORM_7:    // (row,col) (3,16)=>(15,16)
                newId = (CONST18 - row) + CONST19 * (CONST18 - col);
                break;

            default:
                newId = 999;

        }

        if (newId < 0 || newId > 360) {
            throw new GoBadNotOnBoardException("style=" + style + " id=" + id + " newId=" + newId);
        }
        return newId;
    }

}
