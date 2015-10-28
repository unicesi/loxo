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
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * funcion que se encarga de transformar los archivos CSV de SpringerLink
 * descargados en un consolodidado BIB
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class TransformBibSpringerLink {

    /**
     * Funcion principal para la transformacion de los archivos CSV en un solo
     * arhivo consolidado BIB, la funcion se encarga de extraer toda la
     * informacion de cada linea y transformar cada linea en un bibtex para
     * finalizar con la union de todos en un solo archivo BIB, la linea del CSV
     * no trae la informacion del Abstract, por esta razon de cada linea se
     * extrae el url y se descarga el html de la pagina del articulo donde
     * posteriormente se extrae el resumen y se agrega al BIBTex de cada
     * articulo
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
        System.out.println("Generating BibTeX for Springer Link results...");
        System.out.println("");

        File sourceDirectory = new File(sourceFilesPath);
        String line = "";
        String bibContent = "";
        String cvsSplitBy = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";

        File[] files = sourceDirectory.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("csv")) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                    System.out.println("[INFO] Reading file «" + file.getName() + "»...");
                    bufferedReader.readLine().replace("\n", "");
                    while ((line = bufferedReader.readLine()) != null) {
                        bibContent = bibContent + createBib(line.split(cvsSplitBy));
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
     * transformarla en un BIBTex, se debe utilizar la funcion
     * retrieveHtmlFromUrl la cual nos permite extraer todo el html del articulo
     * para la obtenccion del abstract
     *
     * @param csvRecord Arreglo de String con cada campo del CSV
     * @return String con la informacion del arreglo en la estructura BIBTex
     */
    public static String createBib(String[] csvRecord) {
        String reference = "";
        try{
	        if (csvRecord[8].contains("http://")) {
	            
	                System.out.println("[INFO] Retrieving abstract from URL «" + csvRecord[8] + "»");
	                String referenceAbstract = "";
	                String html = retrieveHtmlFromUrl(csvRecord[8]);
	                String[] htmlSections = html.split("<div class=\"abstract-content formatted\" itemprop=\"description\">");
	                if (htmlSections.length > 1) {
	                    referenceAbstract = htmlSections[1].substring(0, htmlSections[1].indexOf("</div>"));
	                    referenceAbstract = referenceAbstract.substring(referenceAbstract.indexOf(">") + 1,
	                            referenceAbstract.lastIndexOf("<"));
	                    referenceAbstract = referenceAbstract.replace("<p class=\"a-plus-plus\">", "");
	                    referenceAbstract = referenceAbstract.replace("</p>", "");
	                    referenceAbstract = referenceAbstract.replace("\t", "");
	                    //referenceAbstract = referenceAbstract.replace("\n", "");
	                    referenceAbstract = referenceAbstract.replace(System.lineSeparator(), " ");
	                }
	                reference += System.lineSeparator() + "@" + csvRecord[9].substring(1, csvRecord[9].length() - 1) + "{" + csvRecord[8].substring(1, csvRecord[8].length() - 1) + ",";
	                reference += "author = {" + splitAuthor(csvRecord[6].substring(1, csvRecord[6].length() - 1)) + ",},";
	                reference += "title = {" + csvRecord[0].substring(1, csvRecord[0].length() - 1) + "},";
	                //resultado += "venue = {" + csv[1].substring(1, csv[1].length()-1) + "},";
	                if (csvRecord[9].substring(1, csvRecord[9].length() - 1).equalsIgnoreCase("article")) {
	                    reference += "journal = {" + csvRecord[1].substring(1, csvRecord[1].length() - 1) + "},";
	                } else {
	                    reference += "booktitle = {" + csvRecord[1].substring(1, csvRecord[1].length() - 1) + "},";
	                }
	                reference += "url = {" + csvRecord[8].substring(1, csvRecord[8].length() - 1) + "},";
	                reference += "abstract = {" + referenceAbstract + "},";
	                reference += "doi = {" + csvRecord[5].substring(1, csvRecord[5].length() - 1) + "},";
	                reference += "year = {" + csvRecord[7].substring(1, csvRecord[7].length() - 1) + "}}";
	                
            }
        }catch (Exception e) {
            System.err.println("[ERROR] CSV to BibTeX conversion failed. Line " + csvRecord[0] + e.getMessage());
        }
        return reference;
    }

    /**
     * Funcion que se encarga de traer todo el codigo fuente de una pagina
     *
     * @param dir String con el url a cargar
     * @return String con la informacion del html
     * @throws IOException excepcion por si el html no se encuentra activo
     * @throws InterruptedException excepcion por si la pausa en cada busqueda
     * es interrumpida
     */
    public static String retrieveHtmlFromUrl(String address) throws IOException, InterruptedException {
        String line = "";
        String html = "";
        try {
            URL url = new URL(address.substring(1, address.length() - 1));
            URLConnection spoof;
            spoof = url.openConnection();
            //spoof.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)");
            spoof.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1240.0 Safari/537.11");
            BufferedReader bufferedReader;
            Thread.sleep(500);
            bufferedReader = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                html += line;
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to retrieve abstract from URL «" + address + "». " + e.getMessage());
            return "";
        }
        return html;
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
            try (FileWriter fileWriter = new FileWriter(bibFile)) {
                fileWriter.write(content);
                fileWriter.flush();
            }
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
