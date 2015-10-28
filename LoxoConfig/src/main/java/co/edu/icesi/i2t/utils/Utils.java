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
package co.edu.icesi.i2t.utils;

import java.lang.reflect.Array;

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public class Utils {

	/**
	 * 
	 * @param inputArray
	 * @return
	 */
    @SuppressWarnings("unchecked")
	public static <T> T[] trimArray(T[] inputArray) {
        T[] trimmedArray = null;
        if (inputArray != null) {
            int i = 0;
            for (int j = 0; j < inputArray.length; j++) {
                if (inputArray[j] != null) {
                    i++;
                }
            }
            if (i > 0) {
                trimmedArray = (T[]) Array.newInstance(inputArray.getClass().getComponentType(), i);
                i = 0;
                for (int j = 0; j < inputArray.length; j++) {
                    if (inputArray[j] != null) {
                        trimmedArray[i] = inputArray[j];
                        i++;
                    }
                }
            }
        }
        return trimmedArray;
    }
    
    /**
     * 
     * @param array
     * @param element
     * @return
     */
    public static <T> boolean arrayContainsElement(T[] array, T element) {
        boolean containsElement = false;
        for (int i = 0; (i < array.length) && !containsElement; i++) {
            T arrayElement = array[i];
            if (arrayElement != null && arrayElement.equals(element)) {
                containsElement = true;
            }
        }
        return containsElement;
    }
    
}
