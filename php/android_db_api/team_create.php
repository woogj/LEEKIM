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
$name = $_REQUEST['teamName'];
$title = $_REQUEST['object'];
$summary = $_REQUEST['summary'];
$master = $_REQUEST['masterID'];


$sql = "INSERT INTO teamList (teamName, object, summary, masterID, reg_date) values('$name', '$title', '$summary','$master', now())";
$result = mysqli_query($link,$sql);

$sql2 = "SELECT teamID FROM teamList WHERE teamName = '$name'";
$teamID = mysqli_query($link,$sql2);
$row = mysqli_fetch_row($teamID);
echo $row[0];

$sql3 = "INSERT INTO team(teamID, userID, grade) VALUES ('$row[0]','$master','1')";
$result3 = mysqli_query($link,$sql3);
  if($result){
        echo "success";
    }else{
        echo "error to insert data";
    }

mysqli_close($link);
?>