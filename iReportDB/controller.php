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
	case 'insertUser';
	InsertUser();
	break;
	case 'getAccountData';
	GetAccountData();
	break;
	case 'viewStatus':
	viewStatus();
	break;
	case 'testConnection':
	testConnection();
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
function InsertUser()
{
	include 'db_connect.php';
	
	if ($stmt=$mysqli->prepare("INSERT INTO tbl_users VALUES (?, ?, ?)"))
	{
		$stmt->bind_param("sss", $_POST['user_email'], $_POST['user_name'], $_POST['user_password']);
		$stmt->execute();
		echo "\n User Has been Added";
	}
	else
	{
		$mysqli->error;
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
}
function viewStatus()
{
	$sql = "SELECT reportdate, reportprogress, reportmediacaption FROM tbl_reports WHERE reportusername = '".$_POST['user_name']."' ORDER BY reportdate DESC;";
	$q = mysql_query($sql);
	
	while ($row = mysql_fetch_array($q))
	{
		// echo $row['reportdate']." / ".$row['reportprogress']." / ".substr($row['reportmediacaption'], 0 , 30)."...".";";
		echo $row['reportdate']." / ".$row['reportprogress']." / ".GetCaption($row['reportmediacaption'])."~";
	}
}

function GetCaption($string)
{
	$maxlength = 35;
	$text = "";
	if (strlen($string) > $maxlength)
	{
		$text = substr($string,0,$maxlength);
		$text .= "...";
	}
	else
	{
		$text = $string;
	}
	return $text;
}

function testConnection()
{
	echo "1";
}

?>