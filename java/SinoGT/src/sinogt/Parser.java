/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinogt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author caocoa
 */
public class Parser {

    public void method() {
        System.out.println(String.format("%04x", (int) '㇑'));
        try {

            Scanner s;
            s = new Scanner(new File("../../db/cjk-decomp-0.4.0.txt"));

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("table");
            doc.appendChild(rootElement);

            while (s.hasNextLine()) {
                String line = s.nextLine();

                System.out.print("new line: " + line + " end\n");
                int ind0 = line.indexOf(":");
                int ind1 = line.indexOf("(");

                // staff elements
                Element sinogram = doc.createElement("sinogram");
                rootElement.appendChild(sinogram);

                // set attribute to staff element
                Attr attr = doc.createAttribute("value");
                attr.setValue("1");
                sinogram.setAttributeNode(attr);

                // shorten way
                // staff.setAttribute("id", "1");
                // firstname elements
                Element firstname = doc.createElement("firstname");
                firstname.appendChild(doc.createTextNode("yong"));
                sinogram.appendChild(firstname);

                // lastname elements
                Element lastname = doc.createElement("lastname");
                lastname.appendChild(doc.createTextNode("mook kim"));
                sinogram.appendChild(lastname);

                // nickname elements
                Element nickname = doc.createElement("nickname");
                nickname.appendChild(doc.createTextNode("mkyong"));
                sinogram.appendChild(nickname);

                // salary elements
                Element salary = doc.createElement("salary");
                salary.appendChild(doc.createTextNode("100000"));
                sinogram.appendChild(salary);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("../../db/output.txt"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException | TransformerException pce) {
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SinoGT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
