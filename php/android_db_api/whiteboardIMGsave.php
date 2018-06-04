<?php
 
$file_path = "Whiteboard/"; //저장 되는 폴더 
$file_path = $file_path.basename($_FILES['uploaded_img']['name']);

if(move_uploaded_file($_FILES['uploaded_img']['tmp_name'], $file_path)){
    echo($file_path);
}else {
	echo("FAIL");
}
?>