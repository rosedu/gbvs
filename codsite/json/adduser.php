<?php
require 'clasamysqluser.php';
ini_set('include_path', '/usr/lib/php5/lib');
require 'password.php';
        $m = new loginmysql();
        $m->start_con();
        $m->select_datab();
        $password=password_hash($_POST['password'], PASSWORD_DEFAULT);
	$m->add_user($_POST['username'],$password,$_POST['group']);
?>
