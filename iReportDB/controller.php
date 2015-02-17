<?php
$command = "";

//tbl_report vars
$report_id= "";
$report_username="";
// $report_murl="";
$report_capt="";
$report_lat="";
$report_long="";
// $report_loc="";

//tbl_users vars
$user_email = "";
$user_name = "";
$user_password = "";

if (isset($_POST['command']))
{
	$command = $_POST['command'];
}

//Set variables per module
setReportVariables();
setUserInsertVariables();

$cons = mysql_connect("localhost", "root", "mark");//field input
mysql_select_db("ireportdb", $cons);//declaration of $cons and connection to the database itself

switch ($command)
{
	case 'insertReport';
	InsertReport();
	break;
	case 'insertUser';
	InsertUser();
	break;
	case 'getAccountData';
	GetAccountData();
	break;
	case 'viewStatus':
	viewStatus();
	break;
	default;
	echo "Unknown Command";
	break;
}

function setReportVariables()//tbl_reports insertion fields
{
	if (isset($_POST['report_id']))
	{
		$report_id = $_POST['report_id'];
	}
	if (isset($_POST['report_username']))
	{
		$report_username = $_POST['report_username'];
	}
	if (isset($_POST['report_lat']))
	{
		$report_username = $_POST['report_lat'];
	}
	if (isset($_POST['report_long']))
	{
		$report_long = $_POST['report_long'];
	}
/* 	if (isset($_POST['report_murl']))
	{
		$report_murl = $_POST['report_murl'];
	} */
	if (isset($_POST['report_capt']))
	{
		$report_capt = $_POST['report_capt'];
	}
/* 	if (isset($_POST['report_loc']))
	{
		$report_loc = $_POST['report_loc'];
	} */
}
function setUserInsertVariables()//tbl_user insertion fields
{
	if (isset($_POST['user_email']))
	{
		$user_email = $_POST['user_email'];
	}
	if (isset($_POST['user_name']))
	{
		$user_name = $_POST['user_name'];
	}
	if (isset($_POST['user_password']))
	{
		$user_password = $_POST['user_password'];
	}
}
function InsertReport()
{
	// $sql = "INSERT INTO tbl_reports VALUES('".$_POST['report_id']."' , '".$_POST['report_username']."' , '".$_POST['report_murl']."' , '".$_POST['report_capt']."' , '".$_POST['report_loc']."');";
	$sql = "INSERT INTO tbl_reports VALUES('".$_POST['report-id']."' , '".$_POST['report_username']."' , '".$_POST['report_capt']."' , '".$_POST['report_lat']."' , '".$_POST['report_long']."');";
	$q = mysql_query($sql);
	echo $q;
	echo "\n Report Inserted";
}
function InsertUser()
{
	include 'db_connect.php';
	
	if ($stmt=$mysqli->prepare("INSERT INTO tbl_users VALUES (?, ?, ?)"))
	{
		$stmt->bind_param("sss", $_POST['user_email'], $_POST['user_name'], $_POST['user_password']);
		$stmt->execute();
		echo "\n User Has been Added";
	}
}
function GetAccountData()
{
	include 'db_connect.php';
	
	if ($stmt=$mysqli->prepare("SELECT user_name FROM tbl_users WHERE user_name = ? AND user_password = ?"))
	{
		$user = $_POST['user_name'];
		$pass = $_POST['user_password'];
		$stmt->bind_param("ss",$user,$pass);
		$stmt->execute();
		$stmt->bind_result($username);
		$stmt->fetch();
		echo $username;
	}
	else
	{
		echo $mysqli->error;
	}

/* 	$sql = "SELECT user_name FROM tbl_users WHERE user_name = '".$_POST['user_name']."' AND user_password = '".$_POST['user_password']."';";
	$q = mysql_query($sql);
	//$values = mysql_fetch_array($q);
	while ($result = mysql_fetch_assoc($q))
	{
		$r = implode(",",$result);
		echo $r;
	} */
}
function viewStatus()
{
	$sql = "SELECT * FROM tbl_reports WHERE reportusername = '".$_POST['user_name']."';";
	$q = mysql_query($sql);
	
	while ($row = mysql_fetch_array($q))
	{
		echo $row['reportid']." / ".$row['reportdate']." / ".$row['reportprogress'].";";
	}
}

?>