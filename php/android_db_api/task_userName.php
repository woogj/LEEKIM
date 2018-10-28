<?php
$con=mysqli_connect("220.67.115.32","yjkim_1812","yjkim_1812","yjkim_1812");
mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
} 

mysqli_set_charset($con,"utf8");


//$id = $_REQUEST['userID'];

$sql = "select a.userID, a.name, b.teamID from users a inner join team b on a.userID = b.userID inner join teamList c on b.teamID = c.teamID where b.teamID = 1;";
$result = mysqli_query($con,$sql);
$data = array();

while($row = mysqli_fetch_array($result)) {

//array_push($data, array('teamID'=>$row["teamID"],'userID'=>$row["userID"], 'name'=>$row["name"]));
array_push($data, array('userID'=>$row["userID"], 'name'=>$row["name"]));
}                  
echo json_encode(array("result"=>$data));

mysqli_close($con);

?>