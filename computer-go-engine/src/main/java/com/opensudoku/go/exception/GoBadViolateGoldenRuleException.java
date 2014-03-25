/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opensudoku.go.exception;

/**
 * Computer Go must know how to end game properly.
 * The first Golden Rule is 
 * NEVER PLAY ON YOUR EYE
 * 
 * the basic definition for eye is
 * given point is surrounded by player's stones in single group
 * @author mark
 */
public class GoBadViolateGoldenRuleException extends GoBadException{

    public GoBadViolateGoldenRuleException() {
    }

    public GoBadViolateGoldenRuleException(String message) {
        super(message);
    }

  
    
}
