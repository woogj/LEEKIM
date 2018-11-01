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
$teamID = $_REQUEST['teamID'];



$sql = "DELETE FROM teamList WHERE teamID='$teamID'";
$result = mysqli_query($link,$sql);

  if($result){
        echo "success";
    }else{
        echo "error to insert data";
    }

mysqli_close($link);
?>