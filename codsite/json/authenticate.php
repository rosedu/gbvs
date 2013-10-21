<?php
require 'clasamysqluser.php';
ini_set('include_path', '/usr/lib/php5/lib');
require 'password.php';
        $m = new loginmysql();
        $m->start_con();
        $m->select_datab();
	
	$realm = 'Restricted area';
	
	if (empty($_SERVER['PHP_AUTH_DIGEST'])) {
	    header('HTTP/1.1 401 Unauthorized');
  	    header('WWW-Authenticate: Digest realm="'.$realm.
           '",qop="auth",nonce="'.uniqid().'",opaque="'.md5($realm).'"');
	    die();
	}
	
	if (!($data = http_digest_parse($_SERVER['PHP_AUTH_DIGEST']))) 
	{
		header('HTTP/1.1 401 Unauthorized');
		die();
	}
        $current_password=password_hash($_REQUEST['password'], PASSWORD_DEFAULT);
	$m->checkusername($_REQUEST['username'],$current_password);
	$m->add_user($_REQUEST['username'],$password,$_REQUEST['group']);
?>
