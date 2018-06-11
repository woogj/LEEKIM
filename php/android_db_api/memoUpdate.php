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
$no = $_POST['no'];



$result = mysqli_query($con, "update memo set title = '$title', text = '$text', update_date = now() where no = '$no' and userID = '$userID'");

if($result) {
	echo 'success';
} else {
	echo 'failure';
}

mysqli_close($con);

?>