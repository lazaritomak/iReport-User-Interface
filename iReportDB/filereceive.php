<?php
// get the "message" variable from the post request
// this is the data coming from the Android app
//$message=$_POST["rpt_desc"]; 

//Retrieve Report ID number and compare it to generated id report for conflict avoidance.
// $cons = mysql_connect("localhost", "root", "mark");//field input
// mysql_select_db("ireportdb", $cons);//declaration of $cons and connection to the database itself

// $retreiveIdSQL = "Select COUNT(reportid) from tbl_reports WHERE reportid = '".$rpt_id."';";
// $result = mysql_query(retreiveIdSQL);

// $rpt_id = rand(0, 9999999);
$rpt_id = generateRandomString();
$rpt_username = $_POST["rpt_username"];
$rpt_lat = $_POST["rpt_lat"];
$rpt_long = $_POST["rpt_long"];
$rpt_desc = $_POST["rpt_desc"];
$rpt_image = $_POST["rpt_image"];
$rpt_status = "pending";
$rpt_date = date('Y-m-d');

$filename="androidmessages.html";
file_put_contents($filename,$rpt_id."<br />".$rpt_username."<br />".$rpt_lat."<br />".$rpt_long."<br />".$rpt_desc."<br />".$rpt_image."<br />".$rpt_date);

$host = "localhost";
$user = "root";
$password = "mark";
$database = "ireportdb";
$mysqli = new mysqli($host, $user, $password, $database);

//INSERT reports
if ($stmt = $mysqli->prepare("INSERT INTO tbl_reports VALUES (?, ?, ?, ?, ?, ?, ?)"))
{
	$stmt->bind_param("sssssss", $rpt_id, $rpt_username, $rpt_desc, $rpt_lat, $rpt_long, $rpt_date, $rpt_status);
	$stmt->execute();
	echo "Your Report has been sent to the cops";
}
else
{
	echo "Your report did not send successfully: ".$mysqli->error;
}
//Image Decoding and Saving
$base=$_REQUEST['rpt_image'];
$binary=base64_decode($base);
header('Content-Type: bitmap; charset=utf-8');
$file = fopen("images\\$rpt_id.jpg" ,'wb');
fwrite($file, $binary);
fclose($file);

//generate random string for id 
//check if generated id exists in the database
//if not, use id
//if yes, generate again
function generateRandomString()
{
	//character maps
	$chars = "0123456789abcdefghijklmnopqrstuvwxyz";
	//initialize string for id
	$idString = "";
	//character length of generated string
	$max_char = 10;
	//generation
	for ($i = 0; $i < $max_char; $i++)
	{
		$idString .= $chars[rand(0, strlen($chars)-1)];
	}
	return $idString;
}

?>