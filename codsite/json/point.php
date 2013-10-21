<?php
require 'clasamysql.php';
        $m = new loginmysql();
        $m->start_con();
        $m->select_datab();
	$string=str_replace("%20", " ", $_GET['name']);
//	print_r($string);
	header("Content-type:application/json");
       	$m->print_status($string);
?>


