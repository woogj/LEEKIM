<?php
$con=mysqli_connect("220.67.115.32","yjkim_1812","yjkim_1812","yjkim_1812");
mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
} 

mysqli_set_charset($con,"utf8");


//$id = $_REQUEST['userID'];

$sql = "SELECT no, title, text, update_date FROM memo";
$result = mysqli_query($con,$sql);
$data = array();

while($row = mysqli_fetch_array($result)) {

array_push($data, array('no'=>$row["no"], 'title'=>$row["title"], 'text'=>$row["text"], 'update_date'=>$row["update_date"]));


                   
}
echo json_encode(array("result"=>$data));

mysqli_close($con);

?>