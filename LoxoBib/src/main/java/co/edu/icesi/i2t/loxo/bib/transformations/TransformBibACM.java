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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * funcion que se encarga de transformar los archivos html ACM descargados en un
 * consolodidado BIB
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class TransformBibACM {

    /**
     * Funcion principal para la transformacion de los archivos html en un solo
     * arhivo consolidado BIB, la funcion se encarga de extraer del html cada
     * link de articulo, despues de tener el listado de url se dispone con la
     * ayuda de selenium webdriver el ingreso a cada url y descargar el BIBtex
     * para finalizar con la union de todos en un solo archivo BIB
     *
     * @param sourceFilesPath String con la ruta de la carpeta donde se
     * encuentran los archivos html a transformas
     * @param targetFilePath String con la ruta fisica del directorio donde se
     * guardara el archivo consolodidao
     * @param targetFileName
     */
    public static boolean transformFiles(String sourceFilesPath, String targetFilePath, String targetFileName) {
        boolean bibFileCreated = false;

        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Generating BibTeX for ACM Digital Library results...");
        System.out.println("");

        String bibContent = "";
        List<String> urls = extractURL(sourceFilesPath);
        FirefoxProfile profile = new FirefoxProfile();
        WebDriver webDriver = new FirefoxDriver(profile);
        for (int i = 0; i < urls.size(); i++) {
            try {
                System.out.println("[INFO] Retrieving BibTeX from URL: " + urls.get(i));
                String idFile = urls.get(i).split("id=")[1].split("&")[0];
                idFile = idFile.substring(idFile.indexOf(".") + 1, idFile.length());
                String url = (urls.get(i));
                webDriver.get(url);
                WebElement bibField = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.linkText("BibTeX")));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                bibField.click();
                WebElement textBib = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(idFile)));
                String reference = textBib.getText();
                String referenceAbstract = webDriver.findElement(By.id("abstract")).getText();
                referenceAbstract = "abstract = {" + referenceAbstract + "}}" + System.lineSeparator();
                bibContent += reference.substring(0, reference.length() - 2) + referenceAbstract;
                //Thread.sleep(10000);
                //tempurl.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println("[ERROR] Failed to retrieve BibTex. The application may have been blocked by ACM Digital Library. Try again later.");
            } catch (Exception e) {
                System.out.println("[ERROR] Failed to retrieve BibTeX. " + e.getMessage());
            }
        }
        webDriver.quit();
        try {
            saveBibFile(bibContent, targetFilePath, targetFileName);
            if (!bibContent.equals("")) {
                bibFileCreated = true;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to create BibTeX file." + e.getMessage());
        }
        System.out.println("-----------------------------");
        return bibFileCreated;
    }

    /**
     * Funcion que se encarga de extraer de todos los HTML de ACM las rutas de
     * los url de los diferentes articlos
     *
     * @param sourceFilesPath String con la direccion fisica donde se encuentran
     * los archivos HTML de la busqueda en ACM
     * @return Lista de String con cada una de las url extraidas
     */
    public static List<String> extractURL(String sourceFilesPath) {
        List<String> urls = new ArrayList<String>();

        File sourceDirectory = new File(sourceFilesPath);
        File[] files = sourceDirectory.listFiles();
        for (File file : files) {
            if (file.getName().toLowerCase().endsWith(".html")) {
                try {
                    System.out.println("[INFO] Reading file " + file.getName() + "...");
                    String finalHTML = transformHTML("file:///" + file.getAbsolutePath());
                    String[] cantidadArticulos = finalHTML.split("<td colspan=\"3\"> ");
                    for (int j = 1; j < cantidadArticulos.length; j++) {
                        String[] temp = cantidadArticulos[j].split("href=\"");
                        String url = "http://dl.acm.org/" + temp[1].split("\">")[0];
                        urls.add(url);
                    }
                } catch (Exception e) {
                    System.out.println("[ERROR] Failed to read file «" + file.getName() + "». " + e.getMessage());
                }
            }
        }
        return urls;
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
     * Funcion que se encarga de transformar el html de entrada en un String
     * para su posterior uso
     *
     * @param address ruta donde se encuentra el archivo a transformar a String
     * @return String con toda la informacion del html
     * @throws IOException excepcion si no es posible encontrar el archivo
     * @throws Exception excepecion si no es posible transformar el archivo
     */
    public static String transformHTML(String address) throws IOException, Exception {
        String strLine = "";
        String finalHTML = "";
        try {
            URL url = new URL(address);
            URLConnection spoof;
            spoof = url.openConnection();
            // Spoof the connection so we look like a web browser
            spoof.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)");
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
            while ((strLine = in.readLine()) != null) {
                finalHTML += strLine;
            }
            in.close();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load the search results page «" + address + "». " + e.getMessage());
            return null;
        }
        return finalHTML;
    }

}
