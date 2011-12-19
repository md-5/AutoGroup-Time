<?php
error_reporting(E_ALL);

//Edit these to match your configuration
$host = "localhost";
$username = "minecraft";
$password = "minecraft";
$database = "minecraft";
$table = "donators";
$table2 = "players";
$myemail = "YOUR EMAIL HERE";
//$url = "ssl://www.paypal.com";
//$url = "ssl://www.sandbox.paypal.com";

//DEBUG INFO
$payment_status = 'Complete'; //DO NOT CHANGE
$receiver_email = 'YOUR EMAIL HERE'; //DO NOT CHANGE
$first_name = 'John';
$last_name = 'Smith';
$payer_email = 'john.smith@example.com';
$txn_id = 'qwerty1234';
$payment_amount = '50';

/*DUMMY REMOVED

// read the post from PayPal system and add 'cmd'
$req = 'cmd=_notify-validate';

foreach ($_POST as $key => $value) {
	$value = urlencode(stripslashes($value));
	$req .= "&$key=$value";
}

// post back to PayPal system to validate
$header .= "POST /cgi-bin/webscr HTTP/1.0\r\n";
$header .= "Content-Type: application/x-www-form-urlencoded\r\n";
$header .= "Content-Length: " . strlen($req) . "\r\n\r\n";
$fp = fsockopen ($url, 443, $errno, $errstr, 30);

// assign posted variables to local variables
$payment_status = $_POST['payment_status'];
$receiver_email = $_POST['receiver_email'];
$first_name = $_POST['first_name'];
$last_name = $_POST['last_name'];
$payer_email = $_POST['payer_email'];
$txn_id = $_POST['txn_id'];
$payment_amount = $_POST['mc_gross'];

if (!$fp) {
	// HTTP ERROR
} else {
	fputs ($fp, $header . $req);
	while (!feof($fp)) {
		$res = fgets ($fp, 1024);
		if (strcmp ($res, "VERIFIED") == 0) {
			// check the payment_status is Completed
			// check that txn_id has not been previously processed
			// check that receiver_email is your Primary PayPal email
			// process payment
*/
			if (strcmp($payment_status, "Complete") != 0){
				die("Payement status not Complete");
			}

			if (strcmp($receiver_email, $myemail) != 0){
				die("Invalid receiver email address!");
			}

			$con = mysql_connect($host, $username, $password);
			if (!$con)
			{
				die('Could not connect: ' . mysql_error());
			}

			mysql_query("CREATE DATABASE IF NOT EXISTS $database",$con);
			mysql_select_db($database, $con);

			$sql = "CREATE TABLE IF NOT EXISTS $table
			(
			first_name text,
			last_name text,
			payer_email text,
			txn_id text,
			payment_amount int,
			claimed boolean
			)";
			mysql_query($sql,$con);

			$sql = "CREATE TABLE IF NOT EXISTS $table2
						(
						name text,
						amount int
						)";
			mysql_query($sql,$con);


			if(mysql_num_rows(mysql_query("SELECT txn_id FROM $table WHERE txn_id='$txn_id'")) != 0 ){
				die("Transaction id $txn_id already exists in the database");
			}
			$sql = "INSERT INTO $table VALUES
										(
										'$first_name',
										'$last_name',
										'$payer_email',
										'$txn_id',
										$payment_amount,
										0			
										)";
			mysql_query($sql,$con);
			mysql_close($con);
			mail ($myemail, "AutoGroup: Donation Recieved", "$first_name $last_name
			 has donated $payment_amount with id $txn_id");
		/*DUMMY REMOVED}
		else if (strcmp ($res, "INVALID") == 0) {
			foreach ($_POST as $key => $value){
				$emailtext .= $key . " = " .$value ."\n\n";
			}
			mail ($myemail, "AutoGroup: Invalid Donation Recieved", "Something has gone wrong.
			The following information is provided for investigation:\n\n$emailtext");
		}
	}
	fclose ($fp);
}*/
?>