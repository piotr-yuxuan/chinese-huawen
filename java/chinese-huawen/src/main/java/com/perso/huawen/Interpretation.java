/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.perso.huawen;

/**
 * Interprétation possible de la structure. Si l'on voulait être rigoureusement
 * rigide, il faudrait compter un caractère chinois par tracé. si
 * l'interprétation préférée n'est pas disponible, la première est choisie.
 */
public enum Interpretation {

    G, J, K, M, T, V;

    public static final Interpretation preferred = Interpretation.G;
};
