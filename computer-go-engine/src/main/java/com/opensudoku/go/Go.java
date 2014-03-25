/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opensudoku.go;

import com.opensudoku.go.exception.GoBadException;
import com.opensudoku.go.exception.GoBadNotOnBoardException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * KO violation when BLACK just play point p1 to capture p2, and p1 is atari
 * which means p1 can be captured immediately. This is KO. By Go rules, WHITE is
 * not allowed to play p2 to capture p1 immediately. WHITE can play other places
 * or just pass for this turn. Mostly we call p1 is "Hot Stone", or invincible
 * during that move, it cannot be taken. Or traditionally, we call WHITE play p2
 * is violating KO rule, just like suicide, is not allowed.
 *
 *
 * @author mark
 */
public class Go implements Coordinate {

    private boolean isOpening;
    private Core core;
    private int passCnt = 0;

    private int lastMove; // for KO concern
    private int lastCapturePoint; // this will be KO

    private int playingColor = BLACK;
    Writer log;
    Coordinator coordinator = new Coordinator();

    public boolean isWithinArea(int id, int areaId) throws GoBadNotOnBoardException {
        for (Integer point : coordinator.getBasicAreaList(areaId)) {
            if (id == point) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmptyInArea(int areaId) throws GoBadNotOnBoardException {
        Coordinator coord = new Coordinator();
        List<Integer> list = new ArrayList<>();
//        list=coord.getAreaList("M17", "S14");
        list = coord.getBasicAreaList(areaId);

        for (Integer id : list) {
            if (core.getStone(id) != EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public boolean isIsOpening() {
        return isOpening;
    }

    public void setIsOpening(boolean isOpening) {
        this.isOpening = isOpening;
    }

//    public Go() throws FileNotFoundException {
//        log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.txt")));
//
//    }
    public void changeTurn() {
        if (playingColor == BLACK) {
            playingColor = WHITE;
        } else {
            playingColor = BLACK;

        }

    }

    public void logGtp(String str) throws IOException {
//        System.out.println(str);
        log.append("logging..." + str + "\n"); // append instead of write
        log.flush();
    }

    public void sendGtp(String str) throws IOException {
        Writer osr
                = new BufferedWriter(new OutputStreamWriter(System.out));
        osr.write(str + "\n\n");
        osr.flush();

    }

    public boolean gtpCommand(String cmd) throws IOException, GoBadException {
        String cleanCmd = cmd.toLowerCase().trim();
        switch (cleanCmd) {
            case "name": {
                sendGtp("= "+GTP_NAME);
                logGtp(GTP_NAME);
                return true;
            }
            case "protocol_version": {
                sendGtp("= 2 ");
                return true;
            }
            case "version": {
//                sendGtp("= 0.5 ");// genmove w
//                sendGtp("= 0.5.1 ");// change play to play
//                sendGtp("= 0.6 ");// fixing sucide 
//                sendGtp("= 0.7 ");// implements simple KO 
//                sendGtp("= 0.8 ");// implements Golden Rule, not to play to blind eye 
//                sendGtp("= 0.9.1 ");// doing PASS W
//                sendGtp("= 0.9.2 ");// doing PASS B, 
//                sendGtp("= 0.9.2.1 ");// vertex, color, move 
//Specification of the Go Text Protocol, version 2
//                sendGtp("= 1.1 ");// B also random run
                sendGtp("= " + Coordinate.OPENSUDOKU_GO_VERSION);// B also random run

                return true;
            }
            case "list_commands": {
                StringBuilder sb = new StringBuilder();
                sb.append("= name\n");
                sb.append("protocol_version\n");
                sb.append("version\n");
                sb.append("genmove b\n");
                sb.append("genmove w\n");
                sb.append("boardsize\n");
                sb.append("clear_board\n");
                sendGtp(sb.toString());
                return true;
            }
            case "boardsize 19": {
                sendGtp("= \n");
                return true;
            }
            case "clear_board": {
                core.init();
                sendGtp("= \n");
                return true;
            }
            case "komi 6.5": {
                sendGtp("= \n");
                return true;
            }

            case "genmove b": {

                sendGtp(gtp_genmove_b());
                return true;
            }
            case "play b pass": {

                sendGtp(gtp_play_b_pass());
                return true;
            }
            case "play w pass": {

                sendGtp(gtp_play_w_pass());
                return true;
            }
            case "genmove w": {

                sendGtp(gtp_genmove_w());
                return true;
            }

            default:
                if (cmd.toLowerCase().startsWith("play b ")) {
                    sendGtp(gtp_play_b(cmd.substring(7) + core.getComment()));
                    return true;
                }
                if (cmd.toLowerCase().startsWith("play w ")) {
                    String temp = gtp_play_w(cmd.substring(7) + core.getComment());
                    sendGtp(temp);
                    return true;
                }

                return false;
        }
    }

    //public String gtp_play_w_d16()
    public String gtp_play_b(String d16) throws GoBadException, IOException {
        if (core.isLegal(BLACK, d16)) {
            core.play(d16, BLACK);
            return ("= ");
        }
        return ("? unknown command or illegal move, by gtp_play_b," + d16);

    }

    public String gtp_play_w(String colrow) throws GoBadException, IOException {
        if (core.isLegal(WHITE, colrow)) {
            core.play(colrow, WHITE);
            return ("= ");
        }
        return ("? unknown command or illegal move, by gtp_play_w," + colrow);

    }

    public int getFirstBlackLegalMove() throws GoBadException {
        for (int k = 0; k < 361; k++) {

            // if it's eye, skip
            if (core.isEye(BLACK, k)) {
                continue;
            }
            if (core.isLegal(BLACK, k)) {
                return k;
            }

        }
        return -1;
    }

    public int getFirstWhiteLegalMove() throws GoBadException {
        for (int k = 0; k < 361; k++) {
            // if it's eye, skip
            if (core.isEye(WHITE, k)) {
                continue;
            }
            if (core.isLegal(WHITE, k)) {
                return k;
            }

        }
        return -1;
    }

    Random rnd = new Random();

    
    /**
     * reviewed by Mark, 3/25/2014
     * Black prefers Star
     * @return
     * @throws GoBadException 
     */
    public int getBlackRandomLegalMove() throws GoBadException {
        for (int area = 0; area < 4; area++) {
            if (isEmptyInArea(area)) {
                return Coordinate.NAMED_STAR[area];
            }

        }

        int id = -1;
        List<Integer> trymoves = new ArrayList<>();

        while (true) {
            id = rnd.nextInt(361);
            if (trymoves.contains(id)) { // if the position has been tried, ignore it
                continue;
            }
            //
            trymoves.add(id);

            //
            if (core.isEye(BLACK, id)) {
                continue;
            }
            if (core.isLegal(BLACK, id)) {
                return id;
            }
            if (trymoves.size() == 361) {
                return -1;
            }
        }

    }

    /**
     * reviewed by Mark, 3/25/2014
     * W prefers Kumoku 
     * @return
     * @throws GoBadException 
     */
    public int getWhiteRandomLegalMove() throws GoBadException {
        for (int area = 0; area < 4; area++) {
            if (isEmptyInArea(area)) {
                for (int kumoku : NAMED_KOMOKU) {
                    if (isWithinArea(kumoku, area)) {
                        return kumoku;
                    }
                }
                //return Coordinate.NAMED_STAR[area];
            }

        }
        int id = -1;
        List<Integer> trymoves = new ArrayList<>();

        while (true) {
            id = rnd.nextInt(361);
            if (trymoves.contains(id)) { // if the position has been tried, ignore it
                continue;
            }
            //
            trymoves.add(id);

            //
            if (core.isEye(WHITE, id)) {
                continue;
            }
            if (core.isLegal(WHITE, id)) {
                return id;
            }
            if (trymoves.size() == 361) {
                return -1;
            }
        }

    }

    public String gtp_genmove_b() throws GoBadException {
        //TODO...\
//        int move = getFirstBlackLegalMove();
        // ver 1.1
        int move = getBlackRandomLegalMove();

        if (move >= 0) {
            core.play(move, BLACK);
            return ("= " + T19[move]);
        } else {
            if (++passCnt > MAX_PASS_CNT) {
                return ("? (gtp_genmove_b) unknown command, PASS CNT IS " + passCnt + " and MAX_PASS_CNT IS" + MAX_PASS_CNT);
            }
            return ("= pass");
        }

    }

    public String gtp_play_b_pass() throws GoBadException {
        //TODO 
        return ("=");
    }

    public String gtp_play_w_pass() throws GoBadException {
        //TODO 

        return ("=");
    }

    public String gtp_genmove_w() throws GoBadException {
        //TODO...\
//        int move = getFirstWhiteLegalMove();
        int move = getWhiteRandomLegalMove(); // for ver 1.0

        if (move >= 0) {
            core.play(move, WHITE);
            return ("= " + T19[move]);
        } else {
            if (++passCnt > MAX_PASS_CNT) {
                return ("? unknown command, PASS CNT IS " + passCnt + " and MAX_PASS_CNT IS" + MAX_PASS_CNT);
            }
            return ("= pass");
        }
//        return ("? unknown command, by gtp_genmove_w");
    }

    public Core getCore() {
        return core;
    }

    public void setCore(Core core) {
        this.core = core;
    }

    public Go(Core core) throws FileNotFoundException {
        this.core = core;
        log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.txt")));

//
    }

    public Go() throws GoBadException, FileNotFoundException {
        core = new Core();
        log = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.txt")));
//
    }

}
