<?php
function createRSAKeys(){
$rsaKey=openssl_pkey_new(array( 
    'private_key_bits' => 1024, 
    'private_key_type' => OPENSSL_KEYTYPE_RSA));
$privKey = openssl_pkey_get_private($rsaKey); 
openssl_pkey_export($privKey, $pem); //Private Key
$public_key_pem = openssl_pkey_get_details($rsaKey)['key'];
$keyPair=new stdClass();
$keyPair->secret=$pem;
$keyPair->public=$public_key_pem;
return $keyPair;
}
?>