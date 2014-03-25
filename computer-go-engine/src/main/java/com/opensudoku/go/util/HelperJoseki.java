/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opensudoku.go.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author mark
 */
public class HelperJoseki {

    public static void main(String[] args) throws IOException {

    }
    /**
     *
     * @param f
     * @return
     * @throws IOException
     */
    public String getSgfData(Path f) {
        show("to read sgf files from directory, /home/mark/go/db-joseki");
        String JOSEKI_HOME = "/home/mark/go/db-joseki/joseki-1.sgf";
        Path file = Paths.get(JOSEKI_HOME);
        StringBuilder sb = new StringBuilder();

        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                sb.append(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return sb.toString();
    }

    public static void show(String str) {
        System.out.println(str);
    }

}
