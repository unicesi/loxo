/*
 * Copyright © 2015 Universidad Icesi
 * 
 * This file is part of Loxo.
 * 
 * Loxo is free software: you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General 
 * Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Loxo is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU Lesser General Public License for 
 * more details.
 * 
 * You should have received a copy of the GNU Lesser General 
 * Public License along with Loxo. If not, 
 * see <http://www.gnu.org/licenses/>.
 */
package co.edu.icesi.i2t.loxo.bib.transformations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * funcion que se encarga de transformar los archivos CSV de IEEEXplore
 * descargados en un consolodidado BIB
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class TransformBibIEEEXplore {

    /**
     * Funcion principal para la transformacion de los archivos CSV en un solo
     * arhivo consolidado BIB, la funcion se encarga de extraer toda la
     * informacion de cada linea y transformar cada linea en un bibtex para
     * finalizar con la union de todos en un solo archivo BIB
     *
     * @param sourceFilesPath String con la ruta de la carpeta donde se
     * encuentran los archivos CSV a transformas
     * @param targetFileDirectory String con la ruta fisica del directorio donde
     * se guardara el archivo consolodidao
     * @param targetFileName
     */
    public static boolean transformFiles(String sourceFilesPath, String targetFileDirectory, String targetFileName) {
        boolean bibFileCreated = false;

        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Generating BibTex for IEEE Xplore Digital Library results...");
        System.out.println("");

        File sourceDirectory = new File(sourceFilesPath);
        String line = "";
        String bibContent = "";
        //String cvsSplitBy = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
        String cvsSplitBy = ",";

        File[] files = sourceDirectory.listFiles();
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith("csv")) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                    System.out.println("[INFO] Reading file «" + file.getName() + "»...");
                    bufferedReader.readLine();
                    bufferedReader.readLine();
                    while ((line = bufferedReader.readLine()) != null) {
                    	System.out.println("Linea: " + StringEscapeUtils.unescapeHtml3(line));
                        bibContent += createBib(StringEscapeUtils.unescapeHtml3(line).replace("\n", "").split(cvsSplitBy));
                    }
                } catch (IOException e) {
                    System.out.println("[ERROR] Failed to read file «" + file.getName() + "». " + e.getMessage());
                }
            }
        }
        try {
            saveBibFile(bibContent, targetFileDirectory, targetFileName);
            if (!bibContent.equals("")) {
                bibFileCreated = true;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to create BibTeX file. " + e.getMessage());
        }
        System.out.println("-----------------------------");
        return bibFileCreated;
    }

    /**
     * Funcion que se encarga de recibir una linea del archivo CSB y
     * transformarla en un BIBTex
     *
     * @param csvRecord Arreglo de String con cada campo del CSV
     * @return String con la informacion del arreglo en la estructura BIBTex
     * @throws IOException excepcion si la linea no contiene los datos minimos
     * para la construccion del BIBTex
     */
    private static String createBib(String[] csvRecord) throws IOException {
        String bib = "";
        String tag = "";
        try{
        	tag = "" + csvRecord[14];
        }catch(Exception e){
        	System.out.println("Prueba: " + csvRecord);
        }
        String tipo = selectReferenceType(csvRecord[32]);
        try {
            bib += System.lineSeparator() + "@" + tipo + "{" + tag.substring(1, tag.length() - 1) + ",";
            bib += "author = {" + splitAuthor(csvRecord[1].substring(1, csvRecord[1].length() - 1)) + ",},";
            bib += "title = {" + csvRecord[0].substring(1, csvRecord[0].length() - 1) + "},";
            //bib += "venue = {" + parts[3].substring(1, parts[3].length()-1) + "},";
            if (tipo.equalsIgnoreCase("article")) {
                bib += "journal = {" + csvRecord[3].substring(1, csvRecord[3].length() - 1) + "},";
            } else if (tipo.equalsIgnoreCase("inproceedings")) {
                bib += "booktitle = {" + csvRecord[3].substring(1, csvRecord[3].length() - 1) + "},";
            } else if (tipo.equalsIgnoreCase("chapter")) {
                bib += "booktitle = {" + csvRecord[3].substring(1, csvRecord[3].length() - 1) + "},";
            }
            bib += "doi = {" + csvRecord[14].substring(1, csvRecord[14].length() - 1) + "},";
            bib += "url = {" + csvRecord[15].substring(1, csvRecord[15].length() - 1) + "},";
            bib += "year = {" + csvRecord[5].substring(1, csvRecord[5].length() - 1) + "},";
            bib += "abstract = {" + csvRecord[10].substring(1, csvRecord[10].length() - 1) + "}}";
        } catch (Exception e) {
            System.err.println("[ERROR] CSV to BibTeX conversion failed. " + e.getMessage());
        }
        return bib;
    }

    /**
     * Funcion que se encarga de seleccionar el tipo de articulo para la
     * construccion del BIBTex
     *
     * @param documentIdentifier Cadena con el tipo de articulo
     * @return String con el tipo de articulo para la construccion del BIBTex
     */
    private static String selectReferenceType(String documentIdentifier) {
        String referenceType = "article";
        if (documentIdentifier.contains("IEEE Conference Publications")) {
            referenceType = "inproceedings";
        }
        if (documentIdentifier.contains("IEEE Early Access Articles")) {
            referenceType = "article";
        }
        if (documentIdentifier.contains("IEEE Journals & Magazines")) {
            referenceType = "article";
        }
        if (documentIdentifier.contains("IEEE Standards")) {
            referenceType = "article";
        }
        if (documentIdentifier.contains("IET Conference Publications")) {
            referenceType = "inproceedings";
        }
        if (documentIdentifier.contains("IET Journals & Magazines")) {
            referenceType = "inproceedings";
        }
        if (documentIdentifier.contains("Wiley-IEEE Press eBook Chapters")) {
            referenceType = "chapter";
        }
        return referenceType;
    }

    /**
     * Funcion que se encarga de guardar el archivo BIB consolodidado con los
     * datos recolectados en la ruta de entrada deseada
     *
     * @param content String con todos los BIBTex encontrados en los HTML
     * descargados de la base de datos ACM
     * @param path String con la ruta fisica del directorio donde se guardara el
     * archivo consolodidao
     * @param fileName
     * @throws IOException Excepcion por si el sistema es incapaz de guardar el
     * archivo en el directorio destinado.
     */
    public static void saveBibFile(String content, String path, String fileName) throws IOException {
        if (!content.equals("")) {
            System.out.println("[INFO] Saving BibTeX...");
            File targetDirectory = new File(path);
            targetDirectory.mkdir();
            File bibFile = new File(targetDirectory, fileName);
            FileWriter fileWriter = new FileWriter(bibFile);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            System.out.println("[INFO] BibTeX saved");
        } else {
            System.out.println("[INFO] No content to save");
        }
    }
    
    /**
     * Funcion que se encarga de separar los autores por coma "," de una referencia 
     *
     * @param content String texto a separar
     */
    public static String splitAuthor(String text){
    	
    	String[] texto = text.trim().split("\\s+");
        StringBuffer newAuthors = new StringBuffer();
        int j = 0;
        
        while (j < texto.length) {
        	char[] cadena = texto[j].toCharArray();
        	
        	newAuthors.append(" ");
        	
            for (int i=0; i < cadena.length; i++)
            {	
                   if (Character.isUpperCase(cadena[i]) && i != 0) {
                	   newAuthors.append(", " + cadena[i]);
                   } else {
                	   newAuthors.append(cadena[i]);
                   }
            }
            j++;
        }
        
        return newAuthors.toString();
    	
    }

}
