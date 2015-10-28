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
package co.edu.icesi.i2t.loxo.bib.filter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Paquete con las funciones encargadas de realizar revision de los abstract y
 * realizar la inclusion o no dependiendo de las palabras claves que el usuario
 * defina
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class FilterBib {

	/**
	 * 
	 * @param bibFilePath
	 * @return
	 */
    public static String[] loadBibContent(String bibFilePath) {
        String[] references = null;
        File bibFile = new File(bibFilePath);
        String bibContent = "";
        String line = "";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(bibFile))) {
            while ((line = bufferedReader.readLine()) != null) {
                bibContent += line;
            }            
            
            references = bibContent.replace(",\n", ",").split("}@");
            
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to read BibTeX Â«" + bibFilePath + "Â». " + e.getMessage());
        }
        return references;
    }

    /**
     * Funcion que se encarga de extraer el abstract de una cadena con toda la
     * informacion de un bibtex
     *
     * @param reference String con toda la informacion de una referencia
     * bibliografica
     * @return String con la informacion del abstract
     */
    private static String getAbstract(String reference) {
        String referenceAbstract = "";
        if (reference.contains("abstract = {")) {
            referenceAbstract = reference.substring(reference.indexOf("abstract = {") + 12);
            referenceAbstract = referenceAbstract.substring(0, referenceAbstract.indexOf("}"));
            referenceAbstract = referenceAbstract.replaceAll("\\p{Punct}", " ");
        } else if (reference.contains("abstract = \"")) {
            referenceAbstract = reference.substring(reference.indexOf("abstract = \"") + 12);
            referenceAbstract = referenceAbstract.substring(0, referenceAbstract.indexOf("\""));
            referenceAbstract = referenceAbstract.replaceAll("\\p{Punct}", " ");
        }
        return StringEscapeUtils.unescapeHtml3(referenceAbstract).toLowerCase();
    }

    /**
     * Funcion que se encarga de extraer el titulo de una cadena con toda la
     * informacion de un bibtex
     *
     * @param reference String con toda la informacion de una referencia
     * bibliografica
     * @return String con la informacion del titulo
     */
    public static String getTitle(String reference) {
        String referenceTitle = "";
        if (reference.contains("title = {")) {
            referenceTitle = reference.substring(reference.indexOf("title = {") + 9);
            referenceTitle = referenceTitle.substring(0, referenceTitle.indexOf("}"));
            referenceTitle = referenceTitle.replaceAll("\\p{Punct}", " ");
        } else if (reference.contains("title = \"")) {
            referenceTitle = reference.substring(reference.indexOf("title = \"") + 9);
            referenceTitle = referenceTitle.substring(0, referenceTitle.indexOf("\""));
            referenceTitle = referenceTitle.replaceAll("\\p{Punct}", " ");
        }
        return StringEscapeUtils.unescapeHtml3(referenceTitle).toLowerCase();
    }
    
    /**
     * Funcion que se encarga de extraer el Book Title de una cadena con toda la
     * informacion de un bibtex
     *
     * @param reference String con toda la informacion de una referencia
     * bibliografica
     * @return String con la informacion del Book Title
     */
    public static String getBookTitle(String reference) {
        String referenceTitle = "";
        if (reference.contains("booktitle = {")) {
            referenceTitle = reference.substring(reference.indexOf("booktitle = {") + 9);
            referenceTitle = referenceTitle.substring(0, referenceTitle.indexOf("}"));
            referenceTitle = referenceTitle.replaceAll("\\p{Punct}", " ");
        } else if (reference.contains("booktitle = \"")) {
            referenceTitle = reference.substring(reference.indexOf("booktitle = \"") + 9);
            referenceTitle = referenceTitle.substring(0, referenceTitle.indexOf("\""));
            referenceTitle = referenceTitle.replaceAll("\\p{Punct}", " ");
        }
        return StringEscapeUtils.unescapeHtml3(referenceTitle).toLowerCase();
    }

    /**
     * Funcion que se encarga de revisar si los abstract contienen una serie de
     * palabras claves definidas por el usuario, si contiene al menos una el
     * articulo es incluido, la funcion permite busqueda independiente de
     * palabras o una combinacion de maximo dos palabras
     *
     * @param searchStrings String con la composicion de palabras a revisar
     * separadas por ; si se desea hacer una comparacion doble la palabras deben
     * estar separadas por el simbolo &
     *
     * @param references
     * @return
     */
    public static String[] includeReferencesByAbstract(String searchStrings, String[] references) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Adding references by abstract...");
        System.out.println("");

        searchStrings = searchStrings.replaceAll("\\Q*\\E", "");
        String[] strings = searchStrings.split(";");
        for (String searchString : strings) {
            int referenceCounter = 0;
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                    String referenceAbstract = getAbstract(references[i]);
                    
                    if (searchString.contains("&")) {
                        String[] temp = searchString.split("&");
                        if (!(referenceAbstract.toLowerCase().contains(temp[0].toLowerCase()) && referenceAbstract.toLowerCase().contains(temp[1].toLowerCase()))) {
                            references[i] = null;
                        } else {
                            referenceCounter++;
                        }
                    } else if (!referenceAbstract.toLowerCase().contains(searchString.toLowerCase())) {
                        references[i] = null;
                    } else {
                        referenceCounter++;
                    }
                }

            }
            System.out.println("[INFO] The search string Â«" + searchString + "Â» has " + referenceCounter + (referenceCounter == 1 ? " match" : " matches"));
        }
        System.out.println("-----------------------------");
        return references;
    }

    /**
     * Funcion que se encarga de revisar si los abstract contienen una serie de
     * palabras claves definidas por el usuario, si contiene al menos una el
     * articulo es excluido, la funcion permite busqueda independiente de
     * palabras o una combinacion de maximo dos palabras
     *
     * @param exclusionCriteria String con la composicion de palabras a revisar
     * separadas por ; si se desea hacer una comparacion doble la palabras deben
     * estar separadas por el simbolo &
     *
     * @param references
     * @return
     */
    public static String[] removeReferencesByAbstract(String[] exclusionCriteria, String[] references) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Removing references by abstract...");
        System.out.println("");

        for (String exclusionCriterion : exclusionCriteria) {
            int referenceCounter = 0;
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                    String referenceAbstract = getAbstract(references[i]);
                    if (referenceAbstract.toLowerCase().contains(exclusionCriterion.toLowerCase())) {
                        referenceCounter++;
                        references[i] = null;
                    }
                }
            }
            System.out.println("[INFO] The exclusion criterion Â«" + exclusionCriterion + "Â» has " + referenceCounter + (referenceCounter == 1 ? " match" : " matches"));
        }
        System.out.println("-----------------------------");
        return references;
    }

    /**
     * Funcion que se encarga de revisar si los titulos contienen una serie de
     * palabras claves definidas por el usuario, si contiene al menos una el
     * articulo es incluido, la funcion permite busqueda independiente de
     * palabras o una combinacion de maximo dos palabras
     *
     * @param searchStrings String con la composicion de palabras a revisar
     * separadas por ; si se desea hacer una comparacion doble la palabras deben
     * estar separadas por el simbolo &
     *
     * @param references
     * @return
     */
    public static String[] includeReferencesByTitle(String searchStrings, String[] references) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Adding references by title...");
        System.out.println("");

        searchStrings = searchStrings.replaceAll("\\Q*\\E", "");
        String[] strings = searchStrings.split(";");
        for (String searchString : strings) {
            int referenceCounter = 0;
            for (int i = 0; i < references.length; i++) {
            	System.out.println("reference: " + references[i]);
                if (references[i] != null) {
                    String referenceTitle = getTitle(references[i]);
                    if (searchString.contains("&")) {
                        String[] temp = searchString.split("&");
                        if (!(referenceTitle.toLowerCase().contains(temp[0].toLowerCase()) && referenceTitle.toLowerCase().contains(temp[1].toLowerCase()))) {
                            references[i] = null;
                        } else {
                            referenceCounter++;
                        }
                    } else if (!referenceTitle.toLowerCase().contains(searchString.toLowerCase())) {
                        references[i] = null;
                    } else {
                        referenceCounter++;
                    }
                }
            }
            System.out.println("[INFO] The search string Â«" + searchString + "Â» has " + referenceCounter + (referenceCounter == 1 ? " match" : " matches"));
        }
        System.out.println("-----------------------------");
        return references;
    }

    /**
     * Funcion que se encarga de guardar el archivo BIB consolodidado con los
     * datos recolectados en la ruta de entrada deseada
     *
     * @param references String con todos los BIBTex encontrados en los HTML
     * descargados de la base de datos ACM
     * @param path String con la ruta fisica del directorio donde se guardara el
     * archivo consolodidao
     * @param fileName
     * @param create Boolean que define si se escribe soebre el archivo existente o se crea uno nuevo
     * @throws IOException Excepcion por si el sistema es incapaz de guardar el
     * archivo en el directorio destinado.
     */
    public static void saveReferencesToBibFile(String[] references, String path, String fileName, boolean create) throws IOException {
        System.out.println("[INFO] Saving filtered BibTeX...");
        File targetDirectory = new File(path);

        File bibFile = new File(targetDirectory, fileName);
                
        if (create){
        	bibFile.createNewFile();
        }
        
        try (FileWriter fileWriter = new FileWriter(bibFile, create)) {
            String bibContent = "";
            String temp = "";
            for (int i = 0; i < references.length; i++) {
                if (references[i] != null) {
                	temp = "@" + references[i] + "}" +System.lineSeparator();
                	bibContent += temp.replace("@@", "@");
                }
            }
            fileWriter.append(bibContent);
            fileWriter.flush();
        }
        System.out.println("[INFO] Filtered BibTeX saved");
    }
    
    /**
     * Funcion que se encarga de unficar todos los archivos BIB generados, despues de ser filtrados     *
     * @param references String con todos los BIBTex encontrados en los HTML
     * descargados de la base de datos ACM
     * @param path String con la ruta fisica del directorio donde se guardara el
     * archivo consolodidao
     * @param fileName
     * @throws IOException Excepcion por si el sistema es incapaz de guardar el
     * archivo en el directorio destinado.
     */
    public static void saveUnitedBib(String[] references, String path, String fileName) throws IOException{
    	System.out.println("[INFO] Saving United filtered BibTeX...");
        
        File targetDirectory = new File(path);

        File bibFile = new File(targetDirectory, "united.bib");
        
        if (!bibFile.exists()){
        	bibFile.createNewFile();
        }
        
        FileOutputStream fileOS = new java.io.FileOutputStream("c:\\SLR" + File.separator + fileName, false);
        OutputStreamWriter writer = new java.io.OutputStreamWriter(fileOS,"UTF-8");
        BufferedWriter file = new java.io.BufferedWriter(writer);
    	
    }
    
    public static void joinFiles(File destination, File[] sources) throws Exception, FileNotFoundException {
        OutputStream output = null;
        try {
            output = createAppendableStream(destination);
            for (File source : sources) {
                appendFile(output, source);
            }
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private static BufferedOutputStream createAppendableStream(File destination) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    private static void appendFile(OutputStream output, File source)
            throws IOException {
        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(source));
            IOUtils.copy(input, output);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
    
    private static void copyfile(String srFile, String dtFile){
        try{
             File f1 = new File(srFile);
             File f2 = new File(dtFile);
             InputStream in = new FileInputStream(f1);
             
             
             OutputStream out = new FileOutputStream(f2,true);
             out.write("Prueba\n".getBytes());

             byte[] buf = new byte[8192];
             int len;
             while ((len = in.read(buf)) > 0){
            	  out.write(buf, 0, len);
             }
             in.close();
             out.close();
             System.out.println("File copied.");
        }
        catch(FileNotFoundException ex){
             System.out.println(ex.getMessage() + " in the specified directory.");
             System.exit(0);
        }
        catch(IOException e){
             System.out.println(e.getMessage());               
        }
   }
    
    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String removeSpecialCaracter(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }
        return output;
    }
   
    public static String cleanInvalidCharacters(String in) {
        StringBuilder out = new StringBuilder();
        char current;
        if (in == null || ("".equals(in))) {
            return "";
        }
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current == 0x9)
                    || (current == 0xA)
                    || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF))) {
                out.append(current);
            }

        }
        return out.toString().replaceAll("\\s", " ");
    }
    
    public static void main(String arg[]){
    	
    	String input = "Mart&#x0131;&#x0131;nez-Ruiz, T.; Garc&#x0131;&#x0131;a, F.; Piattini, M.; Mu&#x0308;nch, J.";
    	
    	//System.out.println(StringUtils.stripAccents(input));
        
    	System.out.println("Prueba 2: " + StringEscapeUtils.unescapeHtml3(input));//cleanInvalidCharacters("Garc&#x0131;&#x0131;a"));
    	
    }
}
