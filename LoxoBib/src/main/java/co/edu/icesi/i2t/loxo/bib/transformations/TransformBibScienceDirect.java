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

/**
 * funcion que se encarga de consolodidar los archivos BIB de ScienceDirect
 * descargados en un consolodidado BIB
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class TransformBibScienceDirect {

    /**
     * Funcion principal para la transformacion de los archivos BIB en un solo
     * arhivo consolidado BIB
     *
     * @param sourceFilesPath String con la ruta de la carpeta donde se
     * encuentran los archivos BIB a consolidar
     * @param targetFileDirectory String con la ruta fisica del directorio donde
     * se guardara el archivo consolodidao
     * @param targetFileName
     */
    public static boolean transformFiles(String sourceFilesPath, String targetFileDirectory, String targetFileName) {
        boolean bibFileCreated = false;

        System.out.println("");
        System.out.println("-----------------------------");
        System.out.println("Generating BibTeX for Science Direct results...");
        System.out.println("");

        File sourceDirectory = new File(sourceFilesPath);
        File targetDirectory = new File(targetFileDirectory);
        targetDirectory.mkdir();
        File bibFile = new File(targetDirectory, targetFileName);
        
    	System.out.println("sourceFilesPath: " + sourceFilesPath);
    	System.out.println("targetFileDirectory: " + targetFileDirectory);
    	System.out.println("targetFileName: " + targetFileName);
        
        try (FileWriter fileWriter = new FileWriter(bibFile)) {
            String line = "";
            File[] files = sourceDirectory.listFiles();
            System.out.println("[INFO] Saving BibTeX...");
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".bib")) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        while ((line = br.readLine()) != null) {
                            fileWriter.write(line.replace("\n", ""));
                            fileWriter.write(System.lineSeparator());
                        }
                    } catch (IOException e) {
                        System.out.println("[ERROR] Failed to read file «" + file.getName() + "». " + e.getMessage());
                    }
                }
            }
            fileWriter.flush();
            System.out.println("[INFO] BibTeX saved");
            bibFileCreated = true;
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to create BibTeX file." + e.getMessage());
        }
        System.out.println("-----------------------------");
        return bibFileCreated;
    }

}
