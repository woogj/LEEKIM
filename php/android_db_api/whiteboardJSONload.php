<?php
	$url = $_REQUEST['content_path'];
	$json_string = file_get_contents($url);
	$json = json_decode($json_string, true);
	
	echo $json_string;
?>