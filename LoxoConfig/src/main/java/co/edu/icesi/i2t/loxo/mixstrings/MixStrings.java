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
package co.edu.icesi.i2t.loxo.mixstrings;

import co.edu.icesi.i2t.loxo.config.Database;

/**
 * clase principal para el combinatorio de palabras segun cada base de datos
 * seleccionado
 *
 * @author Nasser Abdala
 * @author Andres Paz <afpaz at icesi.edu.co>
 */
public class MixStrings {

    /**
     * funcion principal para realizar el combinatorio de palabras a partir de
     * dos listados de palabras separados por ;
     *
     * @param domainStrings El parametro lWords1 define el primer listado de
     * palabras con el cual se realizara la combinacion para la creación de las
     * cadenas de busqueda, es un String separando las palabras por ;
     * @param focusedStrings El parametro lWords2 define el segundo listado de
     * palabras con el cual se realizara la combinacion para la creación de las
     * cadenas de busqueda, es un String separando las palabras por ;
     * @param database cadena para seleccionar la base de datos sobre la cual se
     * desea realizar la busqueda 1=SciendeDirect 2=IEEEXplore 4=SpringerLink
     * 5=ACM Digital Library.
     * @return String la funcion devuelve el listado en forma de cadena
     * separando las diferentes combinaciones por ;utiliza la opcion de busqueda
     * solo titulo
     */
    public static String mixStrings(String[] domainStrings, String[] focusedStrings, Database database) {
        String result = "";
        if (database == Database.SCIENCE_DIRECT) {
            result = MixStringsScienceDirect.mixStrings(domainStrings, focusedStrings);
        } else if (database == Database.IEEE_XPLORE) {
            result = MixStringsIEEEXplore.mixStrings(domainStrings, focusedStrings);
        } else if (database == Database.SPRINGER_LINK) {
            result = MixStringsSpringer.mixStrings(domainStrings, focusedStrings);
        } else if (database == Database.ACM) {
            result = MixStringsACM.mixStrings2(domainStrings, focusedStrings);
        }
        return result;
    }

    /**
     * 
     * @param domainStrings
     * @param focusedStrings
     * @return
     */
    public static String mixStrings(String[] domainStrings, String[] focusedStrings) {
        String result = "";
        if (focusedStrings != null) {
            for (String domainString : domainStrings) {
                for (String focusedString : focusedStrings) {
                    result += domainString + "&" + focusedString + ";";
                }
            }
        } else {
            for (String domainString : domainStrings) {
                result += domainString + ";";
            }
        }
        return result.substring(0, result.length() - 1);
    }

}
