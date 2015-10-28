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
package co.edu.icesi.i2t.loxo.webdrivers;

import co.edu.icesi.i2t.loxo.mixstrings.MixStrings;
import co.edu.icesi.i2t.loxo.config.Database;

/**
 * Clase principal del programa, con el uso de selenium web driver, la base de datos seleccionada y las palabras a buscar, se realiza de forma automatica la busqueda y descarga de resultados
 * 
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 * 
 * @version 1.0, December 2014
 */
public class WebDriver {
    
    /**
     * funcion principal para realizar la automatizacion de las busquedas, la funcion realiza la descarga de los archivos que cada base de datos permite
     * @param domainStrings El parametro lWords1 define el primer listado de palabras con el cual se realizara la combinacion para la creación de las cadenas de busqueda, es un String separando las palabras por ;
     * @param focusedStrings El parametro lWords2 define el segundo listado de palabras con el cual se realizara la combinacion para la creación de las cadenas de busqueda, es un String separando las palabras por ;
     * @param database
     */
    public static void webSearch(String[] domainStrings, String[] focusedStrings, Database database, String idSesion)
    {
        String searchStrings = MixStrings.mixStrings(domainStrings, focusedStrings, database);
        
        if (database == Database.SCIENCE_DIRECT)
        {
            WebDriverScienceDirect.searchWeb(searchStrings, database.getUrl(), idSesion);
        }
        else if (database == Database.IEEE_XPLORE)
        {
            WebDriverIEEEXplore.searchWeb(searchStrings, database.getUrl(), idSesion);
        }
        else if (database == Database.SPRINGER_LINK)
        {
            WebDriverSpringerLink.searchWeb(searchStrings, database.getUrl(), idSesion);
        }
        else if (database == Database.ACM)
        {
            WebDriverACM.searchWeb(searchStrings, database.getUrl(), idSesion);
        }
    }
    
}
