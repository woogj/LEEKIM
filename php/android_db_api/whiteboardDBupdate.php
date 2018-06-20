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
$content_path = $_REQUEST['content_path'];
$new_content_path = $_REQUEST['new_content_path'];
$content_type = $_REQUEST['content_type'];
$contentX = $_REQUEST['contentX'];
$contentY = $_REQUEST['contentY'];
$content_width = $_REQUEST['content_width'];
$content_height = $_REQUEST['content_height'];

if($content_path != $new_content_path && $content_type == "Text") {
	$sql = "UPDATE whiteboard SET content_path = '$new_content_path' WHERE whiteboardID = '$whiteboardID' and teamID = '$teamID' and content_path = '$content_path' and content_type = '$content_type'";
}else {
	$sql = "UPDATE whiteboard SET contentX = '$contentX', contentY = '$contentY', content_path = '$new_content_path' WHERE whiteboardID = '$whiteboardID' and teamID = '$teamID' and content_path = '$content_path' and content_type = '$content_type' and content_width = '$content_width' and content_height = '$content_height'";

}

$result = mysqli_query($link,$sql);

if($result) {
    echo "success";
}else{
    echo "error";
}

mysqli_close($link);
?>