/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.perso.huawen;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author caocoa
 */
public class Ideogram implements Serializable {

    private int id;
    
    public String character; // Normalement de type Character mais certains sont de la même forme que &CDP-8C78;
 
    public Structure structure;
    /**
     * Composant du caractère. String en attendant hibernate.
     */
    public ArrayList<String> components; // si c'est un radical alors il n'a pas de composant. Cela permet d'avoir un arbre.
    public Interpretation interpretation;
    public String codePoint;

    public Ideogram() {
        this.interpretation = Interpretation.preferred;
    }
}
