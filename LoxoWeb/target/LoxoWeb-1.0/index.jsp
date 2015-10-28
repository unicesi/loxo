<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 
 Copyright © 2015 Universidad Icesi
 
 This file is part of Loxo.
 
 Loxo is free software: you can redistribute it 
 and/or modify it under the terms of the GNU Lesser General 
 Public License as published by the Free Software 
 Foundation, either version 3 of the License, or (at your 
 option) any later version.
 
 Loxo is distributed in the hope that it will be 
 useful, but WITHOUT ANY WARRANTY; without even the implied 
 warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 PURPOSE. See the GNU Lesser General Public License for 
 more details.
 
 You should have received a copy of the GNU Lesser General 
 Public License along with Loxo. If not, 
 see <http://www.gnu.org/licenses/>.
 -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SLR Tools</title>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script src="js/jquery.fileupload.js"></script>

<style>
.InputNormal {
	font-family: Arial;
	font-size: small;
    border-width: 1pt    ; 
	border-color: gray  ; 
	border-style: solid ;   
	background-position: top;
	  
}

</style>

</head>


<script type=text/javascript>


function validate(){
	if (option1.checked == 1){
	    option2.checked = 1;
	    option3.checked = 1;
	    option4.checked = 1;
	    option5.checked = 1;
	}else{
	    option2.checked = false;
	    option3.checked = false;
	    option4.checked = false;
	    option5.checked = false;
	}
}

function validarCampos(){
	if (document.getElementById("iddomainstrings").value == ""  && document.getElementById("txtArchivo").value == "" ){
		alert("Debe escoger al menos un dominio");
		return false;
	}
	
	if (document.getElementById("idfocusedstrings").value == "" && document.getElementById("txtArchivo").value == ""){
		alert("Debe escoger al menos un criterio de inclusión");
		return false;
	}
	

	if ((document.getElementById("option1").checked == false && document.getElementById("option2").checked == false && document.getElementById("option3").checked == false && document.getElementById("option4").checked == false && document.getElementById("option5").checked == false) && document.getElementById("txtArchivo").value == ""){
		alert("Debe elegir una opción base de datos");
		return false;
	}
	
	document.getElementById("iddomainstrings").readOnly = true;
	document.getElementById("idfocusedstrings").readOnly = true;
	document.getElementById("idexclusioncriteria").readOnly = true;
	document.getElementById("txtArchivo").disabled = true;
	//document.getElementById("idSubmit").disabled = true;
	
	return true;

}

function mostrar_procesar(){
	document.getElementById('procesando_div').style.display ="block";
}


</script>
<body>
	
	<div align="center" >
 	
        <form form form="SLRWeb" action="SLRWeb"  onsubmit="return validarCampos()" method="post" enctype="multipart/form-data" >
        	<img id="imagen" src="./img/LogoLoxo.jpg">
        	<table id="tableCheck">
               <tr class="InputNormal">
	            	<td colspan="2" align="left" >
						<input type="checkbox" name="database" id="option1" value="" onclick="validate()" align="left" > Todas<br>
						<input type="checkbox" name="database" id="option2" value="1" align="left"> SCIENCE_DIRECT<br>
						<input type="checkbox" name="database" id="option3" value="2" align="left"> IEEE_XPLORE<br>
						<input type="checkbox" name="database" id="option4" value="4" align="left"> SPRINGER_LINK<br>
						<input type="checkbox" name="database" id="option5" value="5" align="left"> ACM
					</td>		            
	            </tr>
            </table>
            <br>
            <table >
	            <tr class="InputNormal"> 
	            	<td><label>Dominio(s):</label></td> <td><input id="iddomainstrings" type="text" name="domainstrings" size="20px" > </td>
	            </tr>
	            <tr class="InputNormal">
	            	<td><label>Filtro Positivo:</label></td> <td> <input id="idfocusedstrings" type="text" name="focusedstrings" size="20px"> </td>
	            </tr>
	             <tr class="InputNormal"> 
	            	<td><label>Filtro Negativo:</label></td> <td> <input id="idexclusioncriteria" type="text" name="exclusioncriteria" size="20px"> </td>
	            </tr>
            
            </table>
            <br>
            <table>
            	<tr>
            		<td><input type="file" name="archivo" id="txtArchivo" size="20" class="InputNormal" ></td>
            	</tr>
            </table>
            <br> 	
            <table align="center" >	
	            <tr>
	            	<td class="InputNormal" align="center">
	            	    <input type="submit" id="submit" value="Consultar" /> 
	        		</td>
	        	</tr>
	        	<tr>
	        		<td align="center">
	        			<span id="procesando_div" style="display: none; position:absolute">
							<img src="./img/loading.gif" id="procesando_gif"  />
						</span>
	        		</td>
	        	</tr>
			</table> 
        	<br>
        	<div id="tabla"></div>
        </form>
 
    </div>
    
    
    
</body>
</html>