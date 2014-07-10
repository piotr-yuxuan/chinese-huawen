/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.perso.huawen;

/**
 *
 * @author caocoa
 */
public class Huawen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.putInBuffer();
        parser.parse();
    }

}
