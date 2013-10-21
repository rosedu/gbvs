<?php
require 'clasamysql.php';
        $m = new loginmysql();
        $m->start_con();
        $m->select_datab();
        $string1=str_replace("%20"," ",$_GET['name']);
        $m->add_bikes($string1,$_GET['bikes']);
?>

