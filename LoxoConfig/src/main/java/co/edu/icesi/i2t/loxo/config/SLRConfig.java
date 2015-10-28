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

/**
 *
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public class SLRConfig {
    
	/**
	 * 
	 */
    private Database[] databases;
    /**
     * 
     */
    private String[] exclusionCriteria;
    /**
     * 
     */
    private String[] domainSearchStrings;
    /**
     * 
     */
    private String[] focusedSearchStrings;

    /**
     * 
     * @param databases
     * @param exclusionCriteria
     * @param domainSearchStrings
     * @param focusedSearchStrings
     */
    SLRConfig(Database[] databases, String[] exclusionCriteria, String[] domainSearchStrings, String[] focusedSearchStrings) {
        this.databases = databases;
        this.exclusionCriteria = exclusionCriteria;
        this.domainSearchStrings = domainSearchStrings;
        this.focusedSearchStrings = focusedSearchStrings;
    }

    /**
     * 
     * @return
     */
    public Database[] getDatabases() {
        return databases;
    }
    
    /**
     * 
     * @return
     */
    public boolean allDatabases() {
        return (this.databases == null) || (this.databases.length == 0);
    }

    /**
     * 
     * @return
     */
    public String[] getExclusionCriteria() {
        return exclusionCriteria;
    }
    
    /**
     * 
     * @return
     */
    public boolean isExclusionCriteriaEmpty() {
        return (this.exclusionCriteria == null) || (this.exclusionCriteria.length == 0);
    }
    
    /**
     * 
     * @return
     */
    public String[] getDomainSearchStrings() {
        return this.domainSearchStrings;
    }

    /**
     * 
     * @return
     */
    public boolean isDomainSearchStringsEmpty() {
        return (this.domainSearchStrings == null) || (this.domainSearchStrings.length == 0);
    }

    /**
     * 
     * @return
     */
    public String[] getFocusedSearchStrings() {
        return this.focusedSearchStrings;
    }
    
}
