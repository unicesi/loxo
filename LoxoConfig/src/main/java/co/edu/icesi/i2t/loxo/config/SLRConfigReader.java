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
package co.edu.icesi.i2t.loxo.config;

import co.edu.icesi.i2t.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public class SLRConfigReader {
    

    /**
     * 
     * @return
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */
    public static List<SLRConfig> loadSLRConfiguration(String idSesion) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException {
    	
    	//String idSesion = "Prueba";
    	System.out.println("Entrando");
    	File folder = new File("C:\\SLR" + File.separator + idSesion);
		//folder.mkdir();
		
    	String CONFIG_FILE = "";
    	Properties propiedades = new Properties();

    	propiedades.load( SLRConfigReader.class.getClassLoader().getResourceAsStream("co/edu/icesi/i2t/slrtools/properties/file.properties"));
    	
    	CONFIG_FILE = propiedades.getProperty("configFile").toString();      
 	
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document slrConfigDocument = documentBuilder.parse(folder + File.separator +CONFIG_FILE);
        
        
        
        if (slrConfigDocument == null) {
            throw new FileNotFoundException("[ERROR] SLR configuration file not found.");
        }
        
        List<SLRConfig> ArraySlrConfig  = new ArrayList<SLRConfig>();
        SLRConfig slrConfig = null;

        Database[] databases = null;
        Database[] trimmedDatabases = null;
        String[] exclusionCriteria = null;
        String[] trimmedExclusionCriteria = null;
        String[] domainSearchStrings = null;
        String[] trimmedDomainSearchStrings = null;
        String[] trimmedFocusedSearchStrings = null;

        NodeList slrConfigRoot = slrConfigDocument.getElementsByTagName("slr");

        if (slrConfigRoot.getLength() != 0) {
       
        	for(int h = 0; h < slrConfigRoot.getLength(); h++ ) {
	
	            Node root = slrConfigRoot.item(h);   
	
	            if (root.getNodeName().equalsIgnoreCase("slr") && root.hasChildNodes()) {
	
	                NodeList slrConfigChildren = root.getChildNodes();	                
	
	                for (int i = 0; i < slrConfigChildren.getLength(); i++) {
	                    Node node = slrConfigChildren.item(i);
	                    node.getTextContent();
	
	                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("databases") && node.hasChildNodes()) {
	                        NodeList databasesList = node.getChildNodes();
	                        databases = new Database[databasesList.getLength()];
	
	                        System.out.println("[INFO] Selected databases:");
	                        for (int j = 0; j < databasesList.getLength(); j++) {
	                            Node databaseNode = databasesList.item(j);
	                            
	                            if (databaseNode.getNodeType() == Node.ELEMENT_NODE && databaseNode.getNodeName().equalsIgnoreCase("database")) {
	                                if (!databaseNode.getTextContent().equals("")) {
	                                	try{
	                                		databases[j] = Database.getByValue(Integer.parseInt(databaseNode.getTextContent().replace("\n", "").replace("\t", "").trim()));
	                                	}catch(Exception ex){
	                                		databases[j] = Database.getByName(databaseNode.getTextContent().replace("\n", "").replace("\t", "").trim());	                                		
	                                	}
	                                    System.out.println(databases[j]);
	                                }
	                            }
	                        }
	                        
	                        trimmedDatabases = Utils.trimArray(databases);
	                    }
	
	                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("inclusioncriteria") && node.hasChildNodes()) {
	                        System.out.println("");
	                        System.out.println("[INFO] Inclusion criteria:");
	                    }
	
	                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("exclusioncriteria") && node.hasChildNodes()) {
	                        NodeList exclusionCriteriaList = node.getChildNodes();
	                        exclusionCriteria = new String[exclusionCriteriaList.getLength()];
	
	                        System.out.println("");
	                        System.out.println("[INFO] Exclusion criteria:");
	                        for (int j = 0; j < exclusionCriteriaList.getLength(); j++) {
	                            Node stringNode = exclusionCriteriaList.item(j);
	
	                            if (stringNode.getNodeType() == Node.ELEMENT_NODE && stringNode.getNodeName().equalsIgnoreCase("string")) {
	                                if (!stringNode.getTextContent().equals("")) {
	                                    exclusionCriteria[j] = stringNode.getTextContent();
	                                    System.out.println(exclusionCriteria[j]);
	                                }
	                            }
	                        }
	                        
	                        trimmedExclusionCriteria = Utils.trimArray(exclusionCriteria);
	                    }
	
	                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("searchstrings") && node.hasChildNodes()) {
	                        NodeList searchStringsList = node.getChildNodes();
	
	                        for (int j = 0; j < searchStringsList.getLength(); j++) {
	                            Node searchSetNode = searchStringsList.item(j);
	
	                            if (searchSetNode.getNodeType() == Node.ELEMENT_NODE && searchSetNode.getNodeName().equalsIgnoreCase("domainstrings") && searchSetNode.hasChildNodes()) {
	                                NodeList domainStringsList = searchSetNode.getChildNodes();
	                                domainSearchStrings = new String[domainStringsList.getLength()];
	
	                                System.out.println("");
	                                System.out.println("[INFO] Domain strings:");
	                                for (int k = 0; k < domainStringsList.getLength(); k++) {
	                                    Node stringNode = domainStringsList.item(k);
	
	                                    if (stringNode.getNodeType() == Node.ELEMENT_NODE && stringNode.getNodeName().equalsIgnoreCase("string")) {
	                                        if (!stringNode.getTextContent().equals("")) {
	                                            domainSearchStrings[k] = stringNode.getTextContent();
	                                            System.out.println(domainSearchStrings[k]);
	                                        }
	                                    }
	                                }
	                                
	                                trimmedDomainSearchStrings = Utils.trimArray(domainSearchStrings);
	                            }
	                            
	                            if (searchSetNode.getNodeType() == Node.ELEMENT_NODE && searchSetNode.getNodeName().equalsIgnoreCase("focusedstrings") && searchSetNode.hasChildNodes()) {
	                                NodeList focusedStringsList = searchSetNode.getChildNodes();
	                                //focusedSearchStrings = new String[focusedStringsList.getLength()];
	
	                                System.out.println("");
	                                System.out.println("[INFO] Focused strings:");
	                                
	                                for (int k = 0; k < focusedStringsList.getLength(); k++) {
	                                    
	                                	Node stringNode = focusedStringsList.item(k);	                                	
	
	                                    if (stringNode.getNodeType() == Node.ELEMENT_NODE && stringNode.getNodeName().equalsIgnoreCase("string")) {
	                                        if (!stringNode.getTextContent().equals("")) {
	                                            //focusedSearchStrings[k] = stringNode.getTextContent();
	                                            String[] focusedSearchStrings = {stringNode.getTextContent().toString()};
	                                            System.out.println(focusedSearchStrings[0]);	                                            
                                                trimmedFocusedSearchStrings = Utils.trimArray(focusedSearchStrings);
	        	                            }
	                                        
	                                        if (trimmedDatabases != null || trimmedExclusionCriteria != null || trimmedDomainSearchStrings != null || trimmedFocusedSearchStrings != null) {
        	    		                    	slrConfig = new SLRConfig(trimmedDatabases, trimmedExclusionCriteria, trimmedDomainSearchStrings, trimmedFocusedSearchStrings);
        	    			                    ArraySlrConfig.add(slrConfig);	   
        	    		                    }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	                
	            }
	        }
        }
        return ArraySlrConfig;
    }

}
