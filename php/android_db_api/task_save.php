<?php

$con=mysqli_connect("220.67.115.32", "yjkim_1812", "yjkim_1812", "yjkim_1812");

mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

//$teamID = $_POST['teamID'];
$userID = $_POST['userID'];
$text = $_POST['text'];

$result = mysqli_query($con, "insert into division (teamID, userID, text, deadline_date, importance, lastupdate_date) values('1', '$userID', '$text', now(), 4 , now());");


if($result) {
	echo 'success';
} else {
	echo 'failure';
}

mysqli_close($con);

?>