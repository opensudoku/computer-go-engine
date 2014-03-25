/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opensudoku.go;

import com.opensudoku.go.Go;
import com.opensudoku.go.exception.GoBadException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Scanner;

/**
 *
 * @author mark
 */
public class GoTextProtocol {

    private  Go go;// as one player

    public GoTextProtocol() throws GoBadException, FileNotFoundException {
        go=new Go();
    }
    
    
    public static void main(String[] args) throws GoBadException, IOException {
     //   gtp = new GoTextProtocol();
        new GoTextProtocol().run();

    }

    public void run() throws GoBadException, IOException {
        InputStreamReader cin = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(cin);
        String cmd = "";
        while (true) {
            cmd = in.readLine();

            if (!go.gtpCommand(cmd)) {
                go.sendGtp("? unknown command\n\n");
            }
        }

    }

}
