<?php

$con=mysqli_connect("220.67.115.32", "yjkim_1812", "yjkim_1812", "yjkim_1812");

mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$no = $_POST['no'];
$text = $_POST['text'];

$result = mysqli_query($con, "update division set text = '$text' where no = '$no';");

if($result) {
	echo 'success';
} else {
	echo 'failure';
}

mysqli_close($con);

?>