﻿<?php
$con=mysqli_connect("220.67.115.32","yjkim_1812","yjkim_1812","yjkim_1812");
mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
} 

mysqli_set_charset($con,"utf8");


$id= $_REQUEST['id'];


$sql = "SELECT teamList.teamID, teamList.teamName FROM teamList INNER JOIN (SELECT * FROM team WHERE userID='$id')t ON teamList.teamID = t.teamID";
$result = mysqli_query($con,$sql);
$data = array();

while($row = mysqli_fetch_array($result)) {

array_push($data, array('teamID'=>$row["teamID"], 'teamName'=>$row["teamName"]));
                  
}
 header('Content-Type: application/json; charset=utf8');
echo json_encode(array("result"=>$data));

mysqli_close($con);

?>
