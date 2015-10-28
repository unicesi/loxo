/*
 * Copyright © 2015Loxo Universidad Icesi
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
 * Bibliographical database registry.
 * 
 * @author Andres Paz <afpaz at icesi.edu.co>
 * @version 1.0, December 2014
 */
public enum Database {

	/**
	 * Science Direct Database.
	 */
	SCIENCE_DIRECT(
			1,
			"ScienceDirect",
			"http://www.sciencedirect.com/science?_ob=MiamiSearchURL&_method=requestForm&_temp=all_boolSearch.tmpl&md5=052b06d957a9d8c82e07acf1d7eef1b7"),
	/**
     * IEEE Xplore Database.
     */
	IEEE_XPLORE(2, 
			"IEEEXplore",
			"http://ieeexplore.ieee.org/search/advsearch.jsp?expression-builder"),
	/**
	 * IEEE Computer Society Database.
	 * Has been removed because it is already contained in the IEEE Xplore Database.
	 */
//	IEEE_CS(3, 
//			"IEEE Computer Society",
//			"http://www.computer.org/portal/web/guest/home"),
	/**
     * Springer Link Database.
     */
	SPRINGER_LINK(
			4,
			"SpringerLink",
			"http://link.springer.com/search?facet-discipline=%22Computer+Science%22&facet-language=%22En%22&dc.title="),
	/**
     * ACM Digital Library Database.
     */
	ACM(
			5,
			"ACM",
			"http://dl.acm.org/advsearch.cfm?coll=DL&dl=ACM&CFID=394916650&CFTOKEN=76324152&query=");

	/**
	 * The identifier of the database.
	 */
	private final int value;
	/**
	 * The name of the database.
	 */
	private final String name;
	/**
	 * The string representation of the database's URL.
	 */
	private final String url;

	/**
	 * Creates a new database instance.
	 * 
	 * @param value
	 *            The identifier for the database's.
	 * @param name
	 *            The name of the database.
	 * @param url
	 *            The URL of the database.
	 */
	private Database(int value, String name, String url) {
		this.value = value;
		this.name = name;
		this.url = url;
	}

	/**
	 * Returns the identifier of the database.
	 * 
	 * @return An integer representing the identifier of the database.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the name of the database.
	 * 
	 * @return The name of the database.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the URL of the database.
	 * 
	 * @return A string representations of the URL of the database.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Database getByValue(int value)
			throws IllegalArgumentException {
		for (Database database : values()) {
			if (database.getValue() == value) {
				return database;
			}
		}
		throw new IllegalArgumentException(value + " is not a valid database.");
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Database getByName(String name)
			throws IllegalArgumentException {
		for (Database database : values()) {
			if (database.getName().equals(name)) {
				return database;
			}
		}
		throw new IllegalArgumentException(name + " is not a valid database.");
	}

}
