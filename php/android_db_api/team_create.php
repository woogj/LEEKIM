<?php

$con=mysqli_connect("localhost", "yjkim_1812", "yjkim_1812", "yjkim_1812");

mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$teamName = $_POST['tName'];
$teamTitle = $_POST['tTitle'];
$teamSummary = $_POST['tSummary'];
$teamURL = $_POST['tURL'];
$masterID = $_POST['mID'];


$result = mysqli_query($con, "insert into teamList (teamName, object, summary, URL, masterID) values('$teamName', '$teamTitle', '$teamSummary', '$teamURL','$masterID')");

if($result) {
	echo 's';
} else {
	echo 'n';
}

mysqli_close($con);

?>