<?php
$algo="aes-128-cbc";
$iv=openssl_random_pseudo_bytes(openssl_cipher_iv_length($algo));
$b=unpack("C*", $iv);
var_dump($b);
?>