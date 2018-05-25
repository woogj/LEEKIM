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
$userID = $_REQUEST['userID'];
$content_path = "somewhere"; // 임시 값
$content_type = $_REQUEST['content_type'];
$contentX = $_REQUEST['contentX'];
$contentY = $_REQUEST['contentY'];

$sql = "INSERT into whiteboard (whiteboardID, teamID, userID, content_path, content_type, contentX, contentY) values('$whiteboardID', '$teamID', '$userID', '$content_path', '$content_type', '$contentX', '$contentY')";
$result = mysqli_query($link,$sql);

if($result) {
    echo "success";
}else{
    echo "error";
}

mysqli_close($link);
?>