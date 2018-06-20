<?php
$link=mysqli_connect("220.67.115.32","yjkim_1812","yjkim_1812","yjkim_1812");
if (!$link)  
{  
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();  
}  

mysqli_set_charset($link,"utf8");

//post값 읽어오기
$whiteboardID = 1; //임시 값
$teamID = 1; //임시 값
#$_REQUEST['userID'];

$sql = "SELECT content_path, content_type, contentX, contentY, content_width, content_height FROM whiteboard WHERE whiteboardID = '$whiteboardID' and teamID = '$teamID'";
$result = mysqli_query($link,$sql);
$data = array();

if($result) {
    $row_count = mysqli_num_rows($result);
    while($row=mysqli_fetch_array($result)){
		array_push($data, array('content_path'=>$row["content_path"], 'content_type'=>$row["content_type"], 'contentX'=>$row["contentX"], 'contentY'=>$row["contentY"], 'content_width'=>$row["content_width"], 'content_height'=>$row["content_height"]));
		header('Content-Type: application/json; charset=utf8');
		$json = json_encode(array("data"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
	}
	echo $json;
    mysqli_free_result($result);
}else{
    echo "error";
}

mysqli_close($link);
?>