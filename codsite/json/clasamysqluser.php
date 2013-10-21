<?php
    class loginmysql
    {
	protected $mysq = ''; 
        public function start_con() {

            $this->mysq= @new mysqli("localhost", "hackaton", "facebook", "user");
            if (mysqli_connect_errno()) die("Error connection to database");

        }
       public function select_datab()
        {       
                $this->mysq->select_db("user");
        }
       public function add_user($name, $password, $group)
       {
		$sql = "insert into user (`username`, `password`, `group`) VALUES ( '" . $name ."', '" . $password . "', '" . $group . "');";
		//echo $sql;
		$result = $this->mysq->query($sql)  or die($this->mysq->error . "[sql]");
       }
       public function del_user($username)
       {
		$sql="delete from user where username='".$username."'";
         	$result = $this->mysq->query($sql) or die ($this->mysq->error ."[sql]");
       }
       public function get_grup($username,$current_password)
       {
		$result = $this->mysq->query("SELECT group  FROM user WHERE username= '". $username ." AND password= '".$current_password ." ';"); 
		$arr = array()
		while ($row=$result->fetch_array())
		{
			unset($row[0]);
			unset($row[1]);
			$arr[] = $row;
		}
		return $arr[0]['grup'] ;
/*		for($i=0;$i<count($arr);$i++)
		{
		$username = $arr[i]['username'];
 		$sql="Select password from user where username='". $username . "';";
		$result = $this->mysq->query($sql) or die($this->mysq->error ."[sql]");
		$uvalid = array();
                if(mysql_num_rows($result) 
		{
			return 0;
		}
		}
		return 1;
*/			
}
