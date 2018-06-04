<?php

$con=mysqli_connect("220.67.115.32", "yjkim_1812", "yjkim_1812", "yjkim_1812");

mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$userID = $_POST['userID'];
$title = $_POST['title'];
$text = $_POST['text'];



$result = mysqli_query($con, "insert into memo (userID, title, text, insert_date, update_date) values('$userID', '$title', '$text', now(), now())");

if($result) {
	echo 's';
} else {
	echo 'n';
}

mysqli_close($con);

?>