/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opensudoku.go.util;

import com.opensudoku.go.Coordinate;
import com.opensudoku.go.Coordinator;
import com.opensudoku.go.exception.GoBadNotOnBoardException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SplitDemo implements Coordinate {

    private static final String REGEX = ";";
//    private static final String INPUT =        "one:two:three:four:five";
    private String INPUT;
    List<String> ssList;

    public SplitDemo() throws IOException {

        ssList = new ArrayList<>();
        for (int k = 0; k < Coordinate.SS.length; k++) {
            ssList.add(SS[k]);
        }

        Path file = Paths.get("/home/mark");
        INPUT = new HelperJoseki().getSgfData(file);
    }

    public static void main(String[] args) throws IOException, GoBadNotOnBoardException {
        SplitDemo sd = new SplitDemo();
        String str = sd.getJosekiData(sd.INPUT);
        System.out.println(str);
        
        
        List<Integer> joseki=new ArrayList<>();
        joseki=sd.getJosekiDataInList(sd.INPUT);
        
         System.out.println("\n transfer style=0 ");
        
        for (Integer s:joseki){
            System.out.print(s+" ");
        }
        
            System.out.println("\n transfer style=1 ");
        
        for (Integer s:joseki){
            System.out.print(new Coordinator().transform(TRANSFORM_1, s)+" ");
        }
        
        

    }

    public String getJosekiData(String str) {
        Pattern p = Pattern.compile(REGEX);
        String[] items = p.split(str);
        StringBuilder sb = new StringBuilder();
        for (String s : items) {
            if ((s.startsWith("B[")) || (s.startsWith("W["))) {
//                System.out.print(s.substring(2, 4));
                sb.append(s.substring(2, 4));
            }
        }
        return sb.toString();
    }

    public List getJosekiDataInList(String str) {
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
}
