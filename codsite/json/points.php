<?php
require 'clasamysql.php';
        header("Content-type:application/json");
        $m = new loginmysql();
	$m->start_con();
        $m->select_datab();
        if (!isset($_GET['distance'])) $_GET['distance']=0;
	if (!isset($_GET['lat'])) $_GET['lat']=0;
	if (!isset($_GET['lon'])) $_GET['lon']=0;
	$m->print_points($_GET['lat'],$_GET['lon'],$_GET['distance']);
?>
