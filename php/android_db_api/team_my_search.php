<?php
$con=mysqli_connect("220.67.115.32","yjkim_1812","yjkim_1812","yjkim_1812");
mysqli_set_charset($con, "utf8");

if(mysqli_connect_errno($con))
{
   echo "Failed to connect to MYSQL: " . mysqli_connect_error();
} 

mysqli_set_charset($con,"utf8");


$id = $_REQUEST['id'];

$sql = "SELECT teamList.* FROM teamList INNER JOIN (SELECT * FROM team WHERE userID='$id')t ON teamList.teamID = t.teamID ORDER BY t.grade ASC";
$sql2 = "SELECT s.teamID, s.members FROM (SELECT teamID, grade FROM team WHERE userID='$id')t INNER JOIN (SELECT teamID, count(*) AS members FROM team GROUP BY teamID)s ON t.teamID=s.teamID ORDER BY t.grade ASC";

$result = mysqli_query($con,$sql);
$totalMembers = mysqli_query($con,$sql2);

// $sql3 = "SELECT teamID, count(*) AS members FROM team GROUP BY teamID ";

$data = array();
$metaData = mysqli_fetch_array($totalMembers);

while($row = mysqli_fetch_array($result)) {
		
		//$str = strcmp($row["teamID"] , $rowMembers["teamID"])	
			array_push($data, array('teamID'=>$row["teamID"], 'teamName'=>$row["teamName"], 'object'=>$row["object"], 'summary'=>$row["summary"], 'masterID'=>$row["masterID"],'reg_date'=>$row["reg_date"]));  	      
}
header('Content-Type: application/json; charset=utf8');
echo json_encode(array("result"=>$data));
echo json_encode(array("metadata"=>$metaData));
//echo json_encode($arrayName = array('' => , );)

mysqli_close($con);

?>
