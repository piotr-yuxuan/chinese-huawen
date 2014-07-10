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
public enum Structure {

    Radical('.'),
    LeftToRight('⿰'),
    AboveToBelow('⿱'),
    LeftToMiddleAndRight('⿲'),
    AboveToMiddleAndBelow('⿳'),
    FullSurround('⿴'),
    SurroundFromAbove('⿵'),
    SurroundFromBelow('⿶'),
    SurroundFromLeft('⿷'),
    SurroundFromUpperLeft('⿸'),
    SurroundFromUpperRight('⿹'),
    SurroundFromLowerLeft('⿺'),
    Overlaid('⿻');

    public final char value;

    private Structure(char value) {
        this.value = value;
    }

    public static final boolean contains(char c) {
        for (Structure s : Structure.values()) {
            if (s.value == c) {
                return true;
            }
        }
        return false;
    }

    public static final Structure fromChar(char c) {
        for (Structure s : Structure.values()) {
            if (s.value == c) {
                return s;
            }
        }
            // On considère que le caractère n'est pas aberrant.
        // throw new InstantiationException(); 
        return Structure.Radical;
    }

    @Override
    public String toString() {
        return Character.toString(this.value);
    }
}
