<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (!isset($_POST['patientName']) ) {
	$name = $_POST['name'];
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	// connecting to db
	$db = new DB_CONNECT();
		
	$result = mysql_query("SELECT * FROM patient WHERE cUserName='$name' ") or die('mysql error');
	if (mysql_num_rows($result) > 0) {
		// looping through all results
		// patient node
    	$numberOfPatients = mysql_num_rows($result);
		$response["success"] = 1;
		$response["message"] = "You have $numberOfPatients Patients";
		$response["patient"] = array();
 
    	while ($row = mysql_fetch_array($result)) {
			// temp user array
			$patient["pUserName"] = $row["pUserName"];
			$patient["currentStatus"] = $row["currentStatus"];
			// push single product into final response array
			array_push($response["patient"], $patient);
		}
		// success
	 
		// echoing JSON response
    	echo json_encode($response);
	} else {
		// no patient found
		$response["success"] = 0;
		$response["message"] = "You don't follow any patient";
		$response["patient"] = array();
		// echo no users JSON
		echo json_encode($response);
	
    
    
	}
} else{
	$name = $_POST['name'];
	$patient2 = $_POST['patientName'];
		
	//Require DataBase Handler File	
	require_once __DIR__ .  '/db_connect.php';
		
	//Connect to DataBase
	$db = new DB_CONNECT();
		
		
	//MySql Queries
	$updateQuery = "UPDATE patient SET cUserName= '$name' WHERE pUserName = '$patient2'";
	$selectQuery = "SELECT 	currentStatus FROM patient WHERE pUserName = '$patient2'";
	$checkPExistQuery = "SELECT * FROM patient WHERE pUserName = '$patient2'";
	$result=mysql_query($checkPExistQuery);
	if(mysql_num_rows($result) > 0){
		if(mysql_query($updateQuery)){
			//Build Successed 
			//Get Status from DataBase
			$result = mysql_query($selectQuery);
			$response["patientFound"] = "YES";
			$response["takeCare"] = "YES";
			$response["currentStatus"] = $result;
			$response["success"] = 1;
			echo json_encode($response);
		}
	} else{
			//The patient does not exist
			
			$response["success"] = 0;
			echo json_encode($response);
	}	
		
		//Response Builder
	
	
}
?>