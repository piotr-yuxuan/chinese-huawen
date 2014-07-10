/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.perso.huawen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author caocoa
 */
public class Parser {

    public HashMap<Character, Ideogram> dataBase;
    public HashMap<Character, String[]> upComingLines;
    public File file;

    public Parser() {
        dataBase = new HashMap<>();
        upComingLines = new HashMap<>();
        file = new File("ids.txt");
    }

    public void putInBuffer() {
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        int linesCount = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.length() >= 3 && "U+".equals(line.substring(0, 2))) {
                linesCount++;
                String[] row = line.split("\t");

                if ("α".equals(row[1]) && "α".equals(row[2])) {
                    continue;
                }

                String code;
                if (row[0].matches("U\\+\\w{4}")) {
                    code = row[0];
                } else {
                    continue;
                }

                /* Dans toute la première partie du fichier (sans compter les CDP de la fin),
                 le caractère décomposé est connu d'utf8 donc c'est valide. */
                Character character;
                if (row[1].matches(".")) {
                    character = row[1].charAt(0);
                } else {
                    continue;
                }

                upComingLines.put(character, row);
            }
        }
        System.out.println("Nombre d'entrées intégrées dans le tapon :\t" + upComingLines.size());
        System.out.println("Nombre de caractère non intégrés :\t" + String.valueOf(linesCount - upComingLines.size()));
        System.out.println("Nombre total de caractères :\t\t" + String.valueOf(linesCount));
    }

    public void parse() {
        Iterator it = upComingLines.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            loop((Character) pair.getKey());
            it.remove();
        }
    }

    private void loop(Character c) {
        if (dataBase.containsKey(c) || !upComingLines.containsKey(c)) {
            return;
        }
        String[] row = upComingLines.get(c);
        // parse c
        Ideogram ideogram = parser(row[0], row[1], row[2]);
        // if components c1
        if (ideogram.components != null && ideogram.components.size() > 0) {
            for (String comp : ideogram.components) {
                Character component = comp.charAt(0);
                if (!dataBase.containsKey(component)) {
                    if (upComingLines.containsKey(component)) {
                        loop(component);
                    } else {
                        // La base ne permet pas la décomposition jusqu'au bout
                    }
                }
            }
        }
        dataBase.put(c, ideogram);
        System.out.println("Nombre d'entrées dans le tampon : " + upComingLines.size());
        System.out.println("Idéogramme inscrit en base :\t" + ideogram.character);
        System.out.println("Nombre d'entrées en mémoire :     " + dataBase.size());
    }

    private Ideogram parser(String code, String character, String comp) {

        Structure structure = null;
        ArrayList<String> components = null;

        if (comp.length() == 1) {
            // Radical, cas simple
            structure = Structure.Radical;
            components = new ArrayList<>();
        } else if (comp.length()
                == 3 && Structure.contains(comp.charAt(0))) {
            // Caractère composé, cas simple
            structure = Structure.fromChar(comp.charAt(0));
            components = new ArrayList<>(Arrays.asList(new String[]{
                Character.toString(comp.charAt(1)),
                Character.toString(comp.charAt(2))
            }));
        } else if (false) {
            // Radical encodé
        } else if (false) {
            // Caractère composé avec radical encodé
        } else if (false) {
            // Différentes versions
        } else {
            //Logger.getLogger(Huawen.class.getName()).log(Level.WARNING, null, "Analyse trop grossière, cas inattendu");
        }

        Ideogram ideogram = new Ideogram();

        ideogram.character = character;
        ideogram.structure = structure;
        ideogram.components = components;
        ideogram.interpretation = Interpretation.preferred;
        ideogram.codePoint = code;

        if (character.contains("肀")) {
            int a = 1;
            a++;
            a = a + 2;
        }

        System.out.println("Idéogramme retourné :\t" + character);
        return ideogram;
    }
}
