<?php
$command = "";

//tbl_report vars
$report_id= "";
$report_username="";
$report_murl="";
$report_capt="";
$report_loc="";
//tbl_users vars
$user_email = "";
$user_name = "";
$user_password = "";


if (isset($_POST['command']))
{
	$command = $_POST['command'];
}

setReportVariables();
setUserInsertVariables();

$cons = mysql_connect("localhost", "root", "mark");//field input
mysql_select_db("ireportdb", $cons);//declaration of $cons and connection to the database itself

switch ($command)
{
	case 'insertReport';
	InsertReport();
	break;
	case 'insertUsers';
	InsertUsers();
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
	if (isset($_POST['report_murl']))
	{
		$report_murl = $_POST['report_murl'];
	}
	if (isset($_POST['report_capt']))
	{
		$report_capt = $_POST['report_capt'];
	}
	if (isset($_POST['report_loc']))
	{
		$report_loc = $_POST['report_loc'];
	}
}
function setUserInsertVariables()//tbl user insertion fields
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
	$sql = "INSERT INTO tbl_reports VALUES('".$_POST['report_id']."' , '".$_POST['report_username']."' , '".$_POST['report_murl']."' , '".$_POST['report_capt']."' , '".$_POST['report_loc']."');";
	$q = mysql_query($sql);
	echo $q;
	echo "\n Inserted";
}
function InsertUsers()
{
	$sql = "INSERT INTO tbl_users VALUES('".$_POST['user_email']."' , '".$_POST['user_name']."' , '".$_POST['user_password']."');";
	$q = mysql_query($sql);
	echo $q;
	echo "\n User Inserted";
}
?>