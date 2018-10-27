<?php
$con=mysqli_connect("220.67.115.32","yjkim_1812","yjkim_1812","yjkim_1812");
mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
} 

mysqli_set_charset($con,"utf8");


//$id = $_REQUEST['userID'];

$sql = "select a.no from division a inner join team b on a.teamID = b.teamID and a.userID = b.userID inner join users c on a.userID = c.userID inner join teamList d on b.teamID = d.teamID;";
$result = mysqli_query($con,$sql);
$data = array();

while($row = mysqli_fetch_array($result)) {

array_push($data, array('no'=>$row["no"]));

}                  
echo json_encode(array("result"=>$data));

mysqli_close($con);

?>