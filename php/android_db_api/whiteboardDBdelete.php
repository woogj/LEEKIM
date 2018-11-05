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
$content_path = $_REQUEST['content_path'];
$contentX = $_REQUEST['contentX'];
$contentY = $_REQUEST['contentY'];
	
$sql = "DELETE FROM whiteboard WHERE (content_path = '$content_path' AND contentX = '$contentX' AND contentX = '$contentY')";

$result = mysqli_query($link,$sql);

if($result) {
    echo "success";
}else{
    echo "error";
}

mysqli_close($link);
?>