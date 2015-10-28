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
package co.edu.icesi.i2t.loxo.bib;

import co.edu.icesi.i2t.loxo.mixstrings.MixStrings;
import co.edu.icesi.i2t.loxo.config.Database;
import co.edu.icesi.i2t.loxo.config.SLRConfig;
import co.edu.icesi.i2t.loxo.config.SLRConfigReader;
import co.edu.icesi.i2t.loxo.bib.filter.FilterBib;
import co.edu.icesi.i2t.loxo.bib.transformations.TransformBibACM;
import co.edu.icesi.i2t.loxo.bib.transformations.TransformBibIEEEXplore;
import co.edu.icesi.i2t.loxo.bib.transformations.TransformBibScienceDirect;
import co.edu.icesi.i2t.loxo.bib.transformations.TransformBibSpringerLink;
import co.edu.icesi.i2t.utils.Utils;

import static co.edu.icesi.i2t.loxo.bib.filter.FilterBib.saveReferencesToBibFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ArrayUtils;
import org.xml.sax.SAXException;

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class SLRBib {
	
	static List<String>  filterReferences = new ArrayList<String>();
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
        		
        		
        	String  folder = "F3F2591EC5ABC54979BF50E0C473FD41";
        	List<SLRConfig> list = SLRConfigReader.loadSLRConfiguration(folder);
        	
        	for (int i = 0; i < list.size(); i++){ 
            	
	            SLRConfig slrConfig = list.get(i);
	            
	            if (slrConfig != null) {
	                if (!slrConfig.isDomainSearchStringsEmpty()) {
	                    String mixedStrings = MixStrings.mixStrings(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings());
	                    if (slrConfig.allDatabases()) {
	                        for (Database database : Database.values()) {
	                            transformAndFilter(slrConfig, database, mixedStrings, folder);
	                        }
	                    } else {
	                        for (Database database : slrConfig.getDatabases()) {
	                            transformAndFilter(slrConfig, database, mixedStrings, folder);
	                        }
	                    }
	                } else {
	                    System.out.println("[WARNING] No domain search strings. Application will exit.");
	                }
	            } else {
	                System.out.println("[WARNING] The configuration file is not correctly formated. Application will exit.");
	            }
        	}

        } catch (ParserConfigurationException | SAXException | IOException ex) {

            Logger.getLogger(SLRBib.class.getName()).log(Level.SEVERE, "[ERROR] There was a problem loading the configuration file", ex);
        }
    }
    
    public static void transformAndFilter(String isSesion) {
        try {
        	List<SLRConfig> list = SLRConfigReader.loadSLRConfiguration(isSesion);
        	
        	for (int i = 0; i < list.size(); i++){ 
            	
	            SLRConfig slrConfig = list.get(i);
	            
	            if (slrConfig != null) {
	                if (!slrConfig.isDomainSearchStringsEmpty()) {
	                    String mixedStrings = MixStrings.mixStrings(slrConfig.getDomainSearchStrings(), slrConfig.getFocusedSearchStrings());
	                    if (slrConfig.allDatabases()) {
	                        for (Database database : Database.values()) {
	                            transformAndFilter(slrConfig, database, mixedStrings, isSesion);
	                        }
	                    } else {
	                        for (Database database : slrConfig.getDatabases()) {
	                            transformAndFilter(slrConfig, database, mixedStrings, isSesion);
	                        }
	                    }
	                } else {
	                    System.out.println("[WARNING] No domain search strings. Application will exit.");
	                }
	            } else {
	                System.out.println("[WARNING] The configuration file is not correctly formated. Application will exit.");
	            }
        	}
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(SLRBib.class.getName()).log(Level.SEVERE, "[ERROR] There was a problem loading the configuration file", ex);
        }
         
       
        
    }
    
    @SuppressWarnings("unchecked")
	public static void filterReference(List<String>  filterReferences, String [] reference, Database database){
    	
    	String titleFilter = "";
    	String title = "";
    	String bookTitle = "";
    	String bookTitleFilter = "";
    	String temp = "";
    	boolean flag = false;
    
    	try{
		for(int j= 0; j < reference.length ; j++ ){
			
			if (reference[j] != null) {
				flag = false;
				title = FilterBib.getTitle(reference[j]);
				bookTitle = FilterBib.getBookTitle(reference[j]);
				System.out.println("title: " + title + " bookTitle: " + bookTitle);
				if(filterReferences != null || filterReferences.size() != 0){
					for(int i = 0; i < filterReferences.size(); i++){
						titleFilter = FilterBib.getTitle(filterReferences.get(i).toString());
						bookTitleFilter = FilterBib.getBookTitle(filterReferences.get(i).toString());
						
		    			if(title.equals(titleFilter) && bookTitle.equals(bookTitleFilter)){
		    				flag = true;
		    				temp = filterReferences.get(i).replace(", keywords = {", ", keywords = {" + database.getName() + ",");
		    				temp = temp.replace(", mendeley-tags = {", ", mendeley-tags = {" + database.getName() + ",");
		    				
		    				System.out.println(temp);
		    				filterReferences.set(i, temp);
		    				System.out.println("filtrado: " + title);
		    				break;
		    			}    			
		    		}
					if(flag == false){						
						filterReferences.add(reference[j] + ", keywords = {" + database.getName() + "}, mendeley-tags = {" + database.getName() + "}") ;
						//filterReferences.add(reference[j]);
					}
				}
			}
    	}
    	}catch(Exception ex){
    		System.out.println("Error" + ex.getMessage());
    	}
   	
    }
    
    /**
     * 
     * @param slrConfig
     * @param database
     * @param mixedStrings
     */
    public static void transformAndFilter(SLRConfig slrConfig, Database database, String mixedStrings, String idSesion) {
    	
    	
    	Properties propiedades = new Properties();
    	String filePath = "";    	

    	try {
			propiedades.load( SLRConfigReader.class.getClassLoader().getResourceAsStream("co/edu/icesi/i2t/slrtools/properties/file.properties"));
			filePath = propiedades.getProperty("filePath");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
        boolean bibFileCreated = false;
        String exportedFilesPath = "";
        
        String sourceBibFilePath = filePath + File.separator + idSesion + File.separator + propiedades.getProperty("downloadBIB").toString();
        String bibFileName = database.getName() + ".bib";
        String targetBibDirectory = filePath+ File.separator + idSesion + File.separator + propiedades.getProperty("downloadBIB").toString();
        
        if (database == Database.ACM) {
        	exportedFilesPath = filePath+ File.separator + idSesion + File.separator + propiedades.getProperty("downloadFilepathACM").toString();        	
            bibFileCreated = TransformBibACM.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        
        if (database == Database.IEEE_XPLORE) {
        	exportedFilesPath = filePath+ File.separator + idSesion + File.separator + propiedades.getProperty("downloadFilepathIEEE").toString();
            bibFileCreated = TransformBibIEEEXplore.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        
        if (database == Database.SCIENCE_DIRECT) {
        	exportedFilesPath = filePath+ File.separator + idSesion + File.separator + propiedades.getProperty("downloadFilepathScienceDirect").toString();        	
            bibFileCreated = TransformBibScienceDirect.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        
        if (database == Database.SPRINGER_LINK) {
        	exportedFilesPath = filePath+ File.separator + idSesion + File.separator + propiedades.getProperty("downloadFilepathSpringer").toString();
            bibFileCreated = TransformBibSpringerLink.transformFiles(exportedFilesPath, sourceBibFilePath, bibFileName);
        }
        
        if (bibFileCreated) {
        	
        	System.out.println("references_path: " + sourceBibFilePath + File.separator + bibFileName);
        	String[] references = FilterBib.loadBibContent(sourceBibFilePath + File.separator + bibFileName);
            System.out.println("reference: " + references.length);
            String[] referencesTitle = FilterBib.includeReferencesByTitle(mixedStrings, references);
            String[] referencesAbstract = FilterBib.includeReferencesByAbstract(mixedStrings, references);
            references = new String[referencesTitle.length + referencesAbstract.length];
            for (int i = 0; i < referencesTitle.length; i++) {
                references[i] = referencesTitle[i];
            }
            for (int i = 0, j = referencesTitle.length; i < referencesAbstract.length; i++, j++) {
                if (!Utils.arrayContainsElement(references, referencesAbstract[i])) {
                    references[j] = referencesAbstract[i];
                }
            }
            if (!slrConfig.isExclusionCriteriaEmpty()) {
                references = FilterBib.removeReferencesByAbstract(slrConfig.getExclusionCriteria(), references);
            }
            try {
            	System.out.println("Ruta BIB: " + targetBibDirectory );
                saveReferencesToBibFile(references, targetBibDirectory, "filtered_" + bibFileName, true);
                filterReference(filterReferences, references, database);
                
                String[] stockArr = new String[filterReferences.size()];
                stockArr = filterReferences.toArray(stockArr);
                
                saveReferencesToBibFile(stockArr, targetBibDirectory, "filtered_Title.bib", false);
            } catch (Exception e) {
                System.out.println("[ERROR] Failed to create filtered BibTeX file. " + e.getMessage());
            }
        }
    }

    
    
}
