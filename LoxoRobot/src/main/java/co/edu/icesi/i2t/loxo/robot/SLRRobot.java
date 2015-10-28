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
package co.edu.icesi.i2t.loxo.robot;

import co.edu.icesi.i2t.loxo.webdrivers.WebDriver;
import co.edu.icesi.i2t.loxo.webdrivers.WebDriverACM;
import co.edu.icesi.i2t.loxo.config.Database;
import co.edu.icesi.i2t.loxo.config.SLRConfig;
import co.edu.icesi.i2t.loxo.config.SLRConfigReader;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public class SLRRobot {
	
	public static void ejecBusqueda(String idSesion){
		System.out.println("");
        try {
        	
        	System.out.println( "nombre carpeta: " + idSesion);
        	
        	List<SLRConfig> list = SLRConfigReader.loadSLRConfiguration(idSesion);
        	       	
        	for (int i = 0; i < list.size(); i++){ 
        	
	            SLRConfig slrConfig = list.get(i);
	            
	            if (slrConfig != null) {
	                if (!slrConfig.isDomainSearchStringsEmpty()) {
	                    if (slrConfig.allDatabases()) {
	                        for (Database database : Database.values()) {
	                            WebDriver.webSearch(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings(),  database, idSesion);
	                        }
	                    } else {
	                        for (Database database : slrConfig.getDatabases()) {
	                            WebDriver.webSearch(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings(), database, idSesion);
	                        }
	                    }
	                    System.out.println("");
	                    System.out.println("[INFO] Finished searching. Save all exported files.");
	                    System.out.println("");
	                } else {
	                    System.out.println("[WARNING] No domain search strings. Application will exit.");
	                }
	            } else {
	                System.out.println("[WARNING] The configuration file is not correctly formated. Application will exit.");
	            }
        	}
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SLRRobot.class.getName()).log(Level.SEVERE, "[ERROR] There was a problem loading the configuration file", ex);
        }
		
	}
	
	
	
	/**
	 * 
	 * @param args
	 */

    public static void main(String[] args) {
        System.out.println("");
        try {
        	
        	String idSesion = "prueba";
        	List<SLRConfig> list = SLRConfigReader.loadSLRConfiguration(idSesion);
        	       	
        	for (int i = 0; i < list.size(); i++){ 
        	
	            SLRConfig slrConfig = list.get(i);
	            
	            if (slrConfig != null) {
	                if (!slrConfig.isDomainSearchStringsEmpty()) {
	                    if (slrConfig.allDatabases()) {
	                        for (Database database : Database.values()) {
	                            WebDriver.webSearch(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings(),   database, idSesion);
	                        }
	                    } else {
	                        for (Database database : slrConfig.getDatabases()) {
	                            WebDriver.webSearch(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings(),  database, idSesion);
	                        }
	                    }
	                    System.out.println("");
	                    System.out.println("[INFO] Finished searching. Save all exported files.");
	                    System.out.println("");
	                } else {
	                    System.out.println("[WARNING] No domain search strings. Application will exit.");
	                }
	            } else {
	                System.out.println("[WARNING] The configuration file is not correctly formated. Application will exit.");
	            }
        	}
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SLRRobot.class.getName()).log(Level.SEVERE, "[ERROR] There was a problem loading the configuration file", ex);
        }
    }

}
