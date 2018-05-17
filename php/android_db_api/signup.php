<?php

$con=mysqli_connect("220.67.115.32", "yjkim_1812", "yjkim_1812", "yjkim_1812");

mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");

$userID = $_POST['userID'];
$userPW = $_POST['userPW'];
$name = $_POST['name'];
$email = $_POST['email'];


$result = mysqli_query($con, "insert into users (userID, userPW, name, email, phone, interest, reg_date) values('$userID', '$userPW', '$name', '$email', '010-9999-9999', 'music', now())");

if($result) {
	echo 's';
} else {
	echo 'n';
}

mysqli_close($con);

?>