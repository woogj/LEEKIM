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
$id = $_REQUEST['userID'];
$pw = $_REQUEST['userPW'];

$sql = "SELECT userPW, name FROM users WHERE userID = '$id'";
$result = mysqli_query($link,$sql);
$data = array();
if($result) {
	$row_count = mysqli_num_rows($result);
	while($row=mysqli_fetch_array($result)){
    		array_push($data, array('name'=>$row["name"]));
            if($pw == $row["userPW"]){
                header('Content-Type: application/json; charset=utf8');
                $json = json_encode(array("data"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                echo $json;
            }else{
                echo "FAIL";
            }
    	}
	mysqli_free_result($result);
}else{
	echo "error";
}

mysqli_close($link);
?>