<?php
	$date = date("Ymd_His", time());
	$myfile = fopen("Whiteboard/$date.json", "w") or die("Unable to open file!");
	$json = $_REQUEST['content_path'];
	fwrite($myfile, $json);
	fclose($myfile);

	echo "Whiteboard/$date.json";
?>