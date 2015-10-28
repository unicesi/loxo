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
package co.edu.icesi.loxo.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;

import co.edu.icesi.i2t.loxo.bib.SLRBib;
import co.edu.icesi.i2t.loxo.robot.SLRRobot;


/**
 * Servlet implementation class slrtools
 */
@WebServlet("/SLRWeb")
public class SLRWeb extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SLRWeb() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String archivo = request.getParameter("archivo");
		System.out.println(archivo);
		String fileName = "";
		String rutaDescarga = "";
		String textoSalida = "";
		String filePath = "";
		Properties propiedades = new Properties();
		
		request.getSession().setAttribute("servletMsg", "Si funciona");
		
		try{
        	propiedades.load( SLRWeb.class.getClassLoader().getResourceAsStream("co/edu/icesi/i2t/slrtools/properties/file.properties"));
	    	fileName = propiedades.getProperty("configFile");
	    	rutaDescarga = propiedades.getProperty("downloadBIB");
	    	filePath = propiedades.getProperty("filePath");
        }catch(Exception ex){
        	System.out.println("[ERROR] No se encuentra el archivo de busquedad.");
        	
        }
		
		File folder = new File(filePath + File.separator + request.getSession().getId().toString());
		folder.mkdir();

		
		try {

	            // construimos el objeto que es capaz de parsear la perici�n
	            DiskFileUpload fu = new DiskFileUpload();
	            // maximo numero de bytes
	            fu.setSizeMax(1024*512); // 512 K
	            // tama�o por encima del cual los ficheros son escritos directamente en disco
	            fu.setSizeThreshold(4096);
	            // directorio en el que se escribir�n los ficheros con tama�o superior al soportado en memoria
	            fu.setRepositoryPath("c:\\SLR\\");
	            // ordenamos procesar los ficheros
	            List fileItems = fu.parseRequest(request);

	            // Iteramos por cada fichero
	            Iterator i = fileItems.iterator();
	            FileItem actual = null;
	            boolean flag = false;
	            //String[] databases = null;
	            List databases = new ArrayList();
	            
	            String domainsStringsText = "";
	            String focusedsStringsText = "";
	            String exclusionsCriteriaText = "";
	            String temp = "";

	            while (i.hasNext()){
	            	
	                actual = (FileItem)i.next();
	                
	                System.out.println("campo: " + actual.getFieldName());
	                
	                String encoding = "UTF-8";
	                BufferedReader in = new BufferedReader(new InputStreamReader(actual.getInputStream(), encoding));	
	                temp = new BufferedReader(new InputStreamReader(actual.getInputStream(), encoding)).readLine();
	                System.out.println("Prueba: " + temp);
	                
	                if(temp != null ){
		                		                
		                if (actual.getFieldName().equals("database")){
			            	databases.add(temp);
		                }
		                
		                if (actual.getFieldName().equals("domainstrings")){
		                	try{
			                	domainsStringsText = temp;
		                	}catch(Exception ex){}
		                }
		                
		                if (actual.getFieldName().equals("focusedstrings")){
		                	try{
			                	focusedsStringsText = temp;
		                	}catch(Exception ex){}
		                }
		                
		                if (actual.getFieldName().equals("exclusioncriteria")){
		                	try{
			                	exclusionsCriteriaText = temp;
		                	}catch(Exception ex){}
		                }
		                
		                if (actual.getFieldName().equals("archivo")){
		                	if(temp != null || !temp.equals("")){
				                File fichero = new File(folder + File.separator + fileName);	                
				                // escribimos el fichero colgando del nuevo path
				                actual.write(fichero);
				                flag = true;		          
		                	}
		                }
		                
		                in.close();
	                }
	                
	            }

	          
	            //Si no cargamos un archivo, entonces procedemos a generarlo desde las opciones escogigas en pantalla
	            if(flag == false){
	            	
	            		            	
	            	//String[] databases = request.getParameterValues("database");// != "" ?  request.getParameter("database").split(";") : null;
	    	        
	    	        String [] exclusionsCriteria = exclusionsCriteriaText != "" ? exclusionsCriteriaText.split(";") : null;
	    	        String [] domainsStrings = domainsStringsText != "" ? domainsStringsText.split(";") : null;
	    	        String [] focusedsStrings = focusedsStringsText != "" ? focusedsStringsText.split(";") : null;
	    	        String texto = "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>" + "\n <slrs> \n";
	    	        
	    	        FileOutputStream fileOS = new java.io.FileOutputStream(folder + File.separator + fileName, false);
	    	        OutputStreamWriter writer = new java.io.OutputStreamWriter(fileOS,"UTF-8");
	    	        BufferedWriter file = new java.io.BufferedWriter(writer);
	    	        	        	        
	    	        if (domainsStrings != null){
	    	        	for (int k = 0; k < domainsStrings.length; k++){
	    	        		texto = texto + "<slr>\n";
	    	                if (databases != null){
	    			        	if (!databases.get(0).toString().trim().equalsIgnoreCase("")){
	    				        	texto = texto + "	<databases> \n"; 
	    				        	
	    					        for (int m = 0; m < databases.size() ; m++){
	    					        	texto = texto + "		<database> \n " + "			<string>" + databases.get(m).toString().trim() + "</string> \n" + "		</database>\n";        	
	    					        }
	    					        
	    					        texto = texto + "	</databases>\n";
	    			        	}
	    			        }
	    			        
	    			        if (exclusionsCriteria != null) {	        			        	
	    			        	texto = texto + "	<exclusioncriteria> \n ";	        			        	
	    			        	for (int j = 0; j < exclusionsCriteria.length ; j++) {
	    				        	texto = texto + "		<string>" +exclusionsCriteria[j] + "</string> \n";
	    			        	}	        			        	
	    			        	texto = texto + "	</exclusioncriteria> \n";	        			        	
	    			        }
	    			        
	    			        texto = texto + "	<searchstrings> \n" + "		<domainstrings> \n" + 
	    	                		"			<string>" + domainsStrings[k] + "</string>\n" +
	    	                		"		</domainstrings> \n";    
	    			        
	    			        if (focusedsStrings != null) {	        			        	
	    			        	texto = texto + "		<focusedstrings> \n";	        			        	
	    			        	for ( int l = 0; l < focusedsStrings.length; l++){
	    				        	texto = texto + "			<string>" + focusedsStrings[l] + "</string>\n";
	    			        	}	        			        	
	    			        	texto = texto + "		</focusedstrings> \n";
	    			        } 
	    			        
	    			        texto = texto + "	</searchstrings> \n" + "</slr>\n";
	    			  } 
	    	        	texto = texto + "</slrs>";
	    	        }
	    	        
	    	        file.write(texto);
	    	        file.close();
	            }
	            

	            SLRRobot.ejecBusqueda(folder.getName().toString());
	    		
	    		textoSalida = textoSalida + "Creamos el archivo BIB" + "\n";
	    		request.getSession().setAttribute("idTextConsola", textoSalida);

	            SLRBib.transformAndFilter(folder.getName().toString());  
	    		
	    		try{ 
	    			
	    			response.setContentType("Content-type: application/zip");
	    			response.setHeader("Content-Disposition", "attachment; filename=BIB.zip");
	    			ServletOutputStream salida = response.getOutputStream();
	    			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(salida));
	    			
	    			//Descargo el archivo BIB generado
	    			rutaDescarga = filePath + File.separator + folder.getName().toString() + File.separator + propiedades.getProperty("downloadBIB").toString();
	    			File sourceDirectory = new File(rutaDescarga);
	    			File[] files = sourceDirectory.listFiles();
	    			System.out.println("Numero Archivos: " + files.length);
	    			for (File file : files) {
	    				
	    							
	    				System.out.println("Archivo: " + file.getName());
	    				if (file.getName().toLowerCase().endsWith(".bib")) {
	    					
	    					System.out.println("Adding " + file.getName());
	    					zos.putNextEntry(new ZipEntry(file.getName()));
	    					
	    			        FileInputStream fichero= new FileInputStream(file); 
	    			        
	    			        BufferedInputStream fif = new BufferedInputStream(fichero);
	    			        
	    			        int data = 0;
	    					while ((data = fif.read()) != -1) {
	    						zos.write(data);
	    					}
	    					fif.close();

	    					zos.closeEntry();
	    					System.out.println("Finishedng file " + file.getName());
	    			        

	    				}
	    			}
	    			
	    			
	    			//Descargo el archivo de la consulta
	    			File sourceDirectoryConfig = new File(filePath + File.separator + folder.getName().toString());
	    			File[] filesConfig = sourceDirectoryConfig.listFiles();
	    			System.out.println("Numero Archivos: " + filesConfig.length);
	    			for (File file : filesConfig) {
	    				System.out.println("Archivo: " + file.getName());
	    				if (file.getName().toLowerCase().endsWith(".xml")) {
	    					System.out.println("Adding " + file.getName());
	    					zos.putNextEntry(new ZipEntry(file.getName()));
	    					
	    			        FileInputStream fichero= new FileInputStream(file); 
	    			        
	    			        BufferedInputStream fif = new BufferedInputStream(fichero);
	    			        
	    			        int data = 0;
	    					while ((data = fif.read()) != -1) {
	    						zos.write(data);
	    					}
	    					fif.close();

	    					zos.closeEntry();

	    					System.out.println("Finished file " + file.getName());
	    				}
	    			}
	    			 
	    			
	    			zos.close();
	    			
	    			response.setHeader("Refresh", "0");
	    			
	    		}catch(Exception e){  
	  	          e.printStackTrace();  
		  	    }finally{
		  	    	
		  	    	File directory = new File(filePath + File.separator + folder.getName().toString());
		  		    deleteDirectory(directory);
		  		} 
	    		
	        
		}catch(Exception e) {
	        	System.out.println("Error : " + e.getMessage());
	    }	
		
	}

}
