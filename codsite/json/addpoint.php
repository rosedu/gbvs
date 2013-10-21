<?php
require 'clasamysql.php';
        $m = new loginmysql();
        $m->start_con();
        $m->select_datab();
       
//	echo $_SERVER['PHP_AUTH_USER'] . " " . $_SERVER['PHP_AUTH_PW'] ; die();
	$string1=str_replace("%20"," ",$_GET['name']);
	$string2=str_replace("%20"," ",$_GET['group']);
	$string3=str_replace("%20"," ",$_GET['description']);
	$image = http_get_request_body();
        
        if (!isset($image)) $image=1;
	$m->add_points($_GET['lat'],$_GET['lon'],$string1,$_GET['bikes'],$string2,$string3,$_GET['email'],$_GET['phone'],$image);
?>




