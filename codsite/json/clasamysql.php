<?php
    class loginmysql 
    {
	protected $mys = ''; 
	public function start_con() {

	    $this->mys= @new mysqli("localhost", "hackaton", "facebook", "biciclete");
	    if (mysqli_connect_errno()) die("Error connection to database");
     
	}
       public function select_datab()
	{	
		$this->mys->select_db("biciclete");
		
	}
	public function print_points($lat=0,$lon=0,$distance=0)
	{
	$result = $this->mys->query("SELECT `lat` , `lon`, `name`, `group`,`bikes`, `description`, `email`, `phone`  FROM `punctedeinchiriere`" )  or die($this->mys->error . "[sql]");
	$arr = array();
	while($row = $result->fetch_array())
  	  {
  		
		//echo $row['lat'] . '<br />';
 	        //echo "<br>"
//		print_r($row);
		unset($row[0]);
		unset($row[1]);		unset($row[2]);
		unset($row[3]);
		unset($row[4]);
		unset($row[5]);
		unset($row[6]);
		unset($row[7]);
	//	print_r($row);
		$arr[] = $row;
	  }
	if ($lat!=0 && $lon!=0 && $distance!=0 )
	{
		$names=array();
		for ($i=0;$i<count($arr);$i++)
		{
			$euclid=sqrt(($lat-$arr[$i]['lat'])*($lat-$arr[$i]['lat'])+($lon-$arr[$i]['lon'])*($lon-$arr[$i]['lon']));
			if($euclid<=$distance)
			{
				$names[] = $arr[$i];
			}
		}
		echo json_encode($names);	
	}
	else echo json_encode($arr);
	}
       public function print_status($name)
        {
	$sql="SELECT `lat` , `lon`, `name`, `group`,`bikes`, `description`, `email`, `phone` , `photo`  FROM `punctedeinchiriere` WHERE name='" . $name . "'";
	$result = $this->mys->query($sql)  or die($this->mys->error . "[sql]");
        $row = $result->fetch_array();
        //	print_r($row); 
              unset($row[0]);
              unset($row[1]);
              unset($row[2]);
	      unset($row[3]);
	      unset($row[4]);
	      unset($row[5]);
	      unset($row[6]);
	      unset($row[7]);
              unset($row[8]);
	//      print_r($row);         //        $arr[] = $row;
        //  } 
        echo json_encode($row); 
        }
	public function add_bikes($name,$bikes)
	{
	 //   if (!isset($_SERVER['PHP_AUTH_USER'])) {
         //     header('HTTP/1.0 401 Unauthorized');
         //    exit;
	 //   } else 
	 //   {
		$sql="SELECT bikes FROM `punctedeinchiriere` WHERE name='" . $name . "'";
		$result = $this->mys->query($sql)  or die($this->mys->error . "[sql]");
		$row = $result->fetch_array();
		unset($row[0]);
		//print_r($row); 
		$x=intval($row['bikes']);
		//echo $x;
		$x=$x+$bikes;
		if ($x<0) {
			header("HTTP/1.0 500 Internal Server Error");
			die();
		}
		//echo " ";
		//echo $x;
		$sql="update punctedeinchiriere set bikes=".$x ." where name='" . $name ."'";
		//print_r($string);
		$result = $this->mys->query($sql)  or die($this->mys->error . "[sql]");
                //$t = $result->fetch_array();
                //unset($t[0]);
                //print_r($t); 
	   // }
	}
	public function add_points($lat=0,$lon=0,$name=0,$bikes=0,$group=0,$description=0,$email,$phone,$image=0)
//      {
	 //   if (!isset($_SERVER['PHP_AUTH_USER'])) {
         //     header("HTTP/1.0 204 No Response");
         //    exit;
         //   } else 
              {
        // $sql = 'SELECT `id` FROM punctedeinchiriere WHERE `name` = '.$name;
	//$result = $this->mys->query($sql);
	//$row = $result->fetch_array();
	//$id = $row['id'];
	//file_put_contents('../images/'.$id.'.jpg', $image);
	//$path = "www.site.valexandru.biz/images/" .$id. '.jpg';	
	$path = 0;
 	$sql="INSERT INTO punctedeinchiriere (`lat`,`lon`,`timestamp`,`name`,`bikes`,`group`,`description`,`email`,`phone`,`image`) VALUES (".$lat.", ".$lon.", ' CURRENT_TIMESTAMP', '".$name."', ".$bikes.", '". $group ."', '" . $description  . "', '" . $email . "', '" . $phone ."', '".$path. "');";
                echo $sql;
	        
		$result = $this->mys->query($sql)  or die($this->mys->error . "[slq]");
	              }
   //     }

        public function del_point($name=0)
        {
         //   if (!isset($_SERVER['PHP_AUTH_USER'])) {
         //     header("HTTP/1.0 204 No Response");
         //    exit;
         //   } else 
         //   {
		$sql="delete from punctedeinchiriere where name='".$name."'";
		$result = $this->mys->query($sql)  or die($this->mys->error . "[sql]");
        //    }
	}
    }
