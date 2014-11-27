<?php
$to  = $_GET['c'];
$subject = 'ListIn - Detalle de Asistencias';
$message = '<!doctype html> 
<html style="height: 100%;margin: 0; padding: 0; border: 0;
font-size: 100%; font: inherit; vertical-align: baseline;"> 
<head> 
<meta name="viewport" content="width=device-width,
initial-scale=1, maximum-scale=1, user-scalable=no"> <meta charset="UTF-8">
<meta http-equiv="Content-type" content="text/html; charset=utf-8" /> 
</head> 
<body style="background: #d6d6d6; font-family: Segoe, Segoe UI, DejaVu Sans,
Trebuchet MS, Verdana, 
sans-serif !important;min-height: 100%; height: 100%;"> <table width="100%" 
border="0" cellspacing="0" cellpadding="20" >
<tr> 
<td> 
<div id="espacio" class="espacio" style=" width: 100%; height: 100%;
display: block;box-sizing: border-box;margin: 0; padding: 0; border: 0; 
font-size: 100%; font: inherit; vertical-align: baseline;color: #333;">
<center>
<h1 style="margin-top: 0; margin-bottom: 0; font-size: 16px; color: inherit;margin: 0;
    padding: 0; border: 0; font-size: 100%; font: inherit; vertical-align: baseline;
    box-sizing: border-box;display: block; font-size: 1.17em; -webkit-margin-before: 1em;
    -webkit-margin-after: 1em; -webkit-margin-start: 0px; -webkit-margin-end: 0px;
    font-weight: bold;">ListIn</h1>
<img src= "http://icons.iconarchive.com/icons/ebiene/e-commerce/256/checklist-icon.png" class="img-responsive" >
<div class="panel panel-primary" style="border-color: #428bca;margin-bottom: 20px; 
background-color: #fff; border: 1px solid transparent; border-radius: 4px;
box-shadow: 0 1px 1px rgba(0,0,0,.05);margin: 0; padding: 0; border: 0; 
font-size: 100%; font: inherit; vertical-align: baseline;box-sizing: 
border-box;display: block;text-align: -webkit-center;color: #333;"> 
<div class="panel-heading" style="color: #fff; background-color: #428bca;
border-color: #428bca;padding: 10px 15px; border-bottom: 1px solid transparent; 
border-top-right-radius: 3px; border-top-left-radius: 3px;margin: 0; padding: 0; 
border: 0; font-size: 100%; font: inherit; vertical-align: baseline;box-sizing: 
border-box;display: block;text-align: -webkit-center;">
 <h3 class="panel-title"
style="margin-top: 0; margin-bottom: 0; font-size: 16px; color: inherit;margin: 0; 
padding: 0; border: 0; font-size: 100%; font: inherit; vertical-align: baseline;
box-sizing: border-box;display: block; font-size: 1.17em; -webkit-margin-before: 1em; 
-webkit-margin-after: 1em; -webkit-margin-start: 0px; -webkit-margin-end: 0px; 
font-weight: bold;">
Detalle de Asistencias
</h3></div><div class="panel-body" style="padding: 15px;margin: 0;padding: 0;border: 0;
font-size: 100%;font: inherit;vertical-align: baseline;box-sizing: border-box;display: 
block;text-align: -webkit-center;color: #333;">
Estimado '.$_GET['n']." " .$_GET['a']." sus asistencias son las siguientes: ";
$asistencias = explode("!", $_GET['d']);
$detallessss="";
for ($bucle=0; $bucle<(count($asistencias)-1);$bucle++){
	$detaAsis = explode("--", $asistencias[$bucle]);
	switch ($detaAsis[1]) {
		case '1':
		$detallessss  = $detallessss.'<br>'.$detaAsis[0].': Presente';
			break; 
		case '2':
		$detallessss  = $detallessss.'<br>'.$detaAsis[0].': Tarde';
			break; 
		case '3':
		$detallessss  = $detallessss.'<br>'.$detaAsis[0].': Ausente';
			break; 
	}
}
$message = $message.$detallessss.'
</div>
</div>
</center>
<br>
<center>
<p style="color: #999;"> &copy; <a href="https://www.facebook.com/svsoftnicaragua">SvSoft-Nicaragua</a></p>
</center>
</div>
</td>
</tr>
</table>
</body>
';
// To send HTML mail, the Content-type header must be set
$headers  = 'MIME-Version: 1.0' . "\r\n";
$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
// Additional headers
$headers .= 'To: '.$_GET['n'].' <'.$_GET['c'].'>' . "\r\n";
$headers .= 'From: ListIn - SvSoft-Nicaragua <svsoftnic@gmail.com>' . "\r\n";
$headers .= 'Cc: '.$_GET['c']. "\r\n";
$headers .= 'Bcc: '.$_GET['c']. "\r\n";
// Mail it
mail($to, $subject, $message, $headers);
?>
