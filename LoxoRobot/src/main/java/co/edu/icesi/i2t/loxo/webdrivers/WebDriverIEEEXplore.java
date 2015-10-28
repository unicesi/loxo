/*
 * Copyright � 2015 Universidad Icesi
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
package co.edu.icesi.i2t.loxo.webdrivers;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase que se encarga, con la ayuda de selenium WebDriver, automatizar el
 * proceso de busqueda en la base de datos IEEEXplore
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class WebDriverIEEEXplore {

    /**
     * Funcion que se encarga de realizar automaticamente la busqueda en la base
     * de datos IEEEXplore conforme a una cadena de busqueda introducida, la
     * automatizacion nos permite descargar los CSV que IEEEXplore dispone con
     * el resultaod de las busquedas, la ruta donde se guardan los archivos
     * dependen de la selecci�n del usuario, si por alguna razon el resultado de
     * la busqueda es mayor a 2000 datos, el buscador solo deja descargar los
     * primeros 2000 ordenados por importancia de acuerdo a las politicas de
     * IEEEXplore, los archivos descargados posteriormente son procesadas para
     * la construccion del BIB con los resultados obtenidos.
     *
     * @param searchStrings este parametro es la cadena de busqueda que retorna
     * la funcion mixIEEEXplore#mixWords, cada cadena de busqueda esta separada
     * por ;
     * @param url este paremetro es el URL de la busqueda avanzada de IEEEXplore
     * @see mixWords.mixIEEEXplore#mixWords(java.lang.String, java.lang.String)
     */
    public static void searchWeb(String searchStrings, String url, String idSesion) {
        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Searching IEEE Xplore Digital Library...");
        System.out.println("Search strings: �" + searchStrings + "�");
        System.out.println("");

        String downloadFilepathIEEE = "";
        String filePath = "";
        
        try{
        	Properties propiedades = new Properties();
        	propiedades.load( WebDriverACM.class.getClassLoader().getResourceAsStream("co/edu/icesi/i2t/slrtools/properties/file.properties"));
        	downloadFilepathIEEE = propiedades.getProperty("downloadFilepathIEEE");
        	filePath = propiedades.getProperty("filePath");
        }catch(Exception ex){
        	System.out.println("[ERROR] No se encuentra el archivo de busquedad.");
        	
        }
        
        File folder = new File(filePath + File.separator + idSesion + File.separator + downloadFilepathIEEE);
		folder.mkdir();
        
        FirefoxProfile profile = new FirefoxProfile();
        
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        System.out.println("Ruta descarga: " + folder.getPath());
        profile.setPreference("browser.download.dir", folder.getPath());
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/text, application/octet-stream");
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);  
        profile.setPreference("browser.download.useDownloadDir", true);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.useWindow", false);
        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);

        WebDriver webDriver = new FirefoxDriver(profile);
        
        String[] strings = searchStrings.split(";");
        for (int i = 0; i < strings.length; i++) {
            try {
                webDriver.get(url);
                WebElement searchField = webDriver.findElement(By.id("expression-textarea"));
                WebElement buttonSearch = webDriver.findElement(By.id("submit-search"));
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {

                }
                searchField.click();
                searchField.sendKeys(strings[i]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                buttonSearch.click();
                try {
                    WebElement exportField = (new WebDriverWait(webDriver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("popup-export-results")));
                    WebElement stringResult = webDriver.findElement(By.xpath("//div[contains(@id, 'content')]/span"));
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {

                    }
                    exportField.click();
                    System.out.println("[INFO] Search string " + (i + 1) + " �" + strings[i] + "� " + stringResult.getText());
                    WebElement exportButton = (new WebDriverWait(webDriver, 15)).until(ExpectedConditions.presenceOfElementLocated(By.id("export_results_ok")));
                    exportButton.click();
                } catch (Exception e) {
                    System.out.println("[WARNING] Search string " + (i + 1) + " �" + strings[i] + "� retrieves no results");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Search string " + (i + 1) + " �" + strings[i] + "� failed. " + e.getMessage());
            }
          //Espera de 45 segundos para cerrar la pagina
            try {
               Thread.sleep(45000);
           } catch (InterruptedException e) {

           }finally{
           	webDriver.close();
       		webDriver.quit();        	
           }
        }
        System.out.println("[INFO] Finished search in IEEE Xplore Digital Library");
        System.out.println("-----------------------------");

    }
}
