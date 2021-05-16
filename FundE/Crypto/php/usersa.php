<?php
require_once("./rsa.php");
require_once("./phpqrcode/qrlib.php");
$rsa=new RSA();
$hash=hash("sha256", "Hallo Welt");
$encryptedData=$rsa->privEncrypt("Hallo Welt");
$signature=base64_encode($rsa->signMessage("Hallo Welt"));
$obj=new stdClass();
$obj->key="Hallo Welt";
$obj->hash=$hash;
$obj->signature=$signature;
$obj->encrypted=$encryptedData;
$jObj=json_encode($obj);
QRcode::png($jObj, "key.png");
?>