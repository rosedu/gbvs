<?php
require 'clasamysqluser.php';
        $m = new loginmysql();
        $m->start_con();
        $m->select_datab();
        $m->del_user($_POST['name']);
?>

