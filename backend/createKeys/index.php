<?php
require_once("./generateRSAKeys.php");
require_once("./rsa.php");
require_once("./phpqrcode/qrlib.php");
require_once('./fpdf/fpdf.php');

$payload=$_REQUEST["daten"];
$rsa=new Rsa();
$keyPair=createRSAKeys();
$publicKey=$keyPair->public;
//$testCrypt=$rsa->encrypt("Hallo Welt1", $publicKey);
$algo="aes-256-cbc";
$key=openssl_random_pseudo_bytes(32);
$iv=openssl_random_pseudo_bytes(openssl_cipher_iv_length($algo));
$payloadCrypt=openssl_encrypt($payload, $algo, $key, OPENSSL_RAW_DATA, $iv);
$payloadBase64=base64_encode($payloadCrypt)."::".base64_encode($iv);
$testCrypt=openssl_public_encrypt(base64_encode($key), $encrypted, $publicKey);
$rndBytes=openssl_random_pseudo_bytes(20, $strong);
$fileName=base64_encode($rndBytes);
if($testCrypt){
    $myfile = fopen("./tmp/".$fileName, "w") or die("Unable to open file");
    fwrite($myfile, base64_encode($encrypted)."\n");
    fwrite($myfile, "++++Payload++++\n");
    fwrite($myfile, $payloadBase64);
    fclose($myfile);
}

$qrData=new stdClass();
$qrData->key=$keyPair->secret;
$qrData->fileName=$fileName;
$toHash=$keyPair->secret."::".$fileName;
$hash=hash("SHA512", $toHash);
$signature=base64_encode($rsa->signMessage($hash));
$qrData->signature=$signature;
$fileName= $fileName.".png";
QrCode::png(json_encode($qrData), $fileName);


$pdf = new FPDF();
$pdf->AddPage();
$pdf->SetFont('Arial','B',12);
$pdf->Cell(40,10,'Dies ist ein Anschreiben neu1');
$pdf->Ln();
$pdf->Cell(40, 10, "Key");
$pdf->Ln();
$pdf->MultiCell(170,10, base64_encode($encrypted));
$pdf->Ln();
$pdf->Cell(40, 10, "Encrypted");
$pdf->Ln();
$pdf->MultiCell(170,10, $encrypted);
$pdf->Ln();
$pdf->Cell(40, 10, "Base64 coded AES Key");
$pdf->Ln();
$pdf->MultiCell(170, 10, base64_encode($key));
$pdf->Ln();
$pdf->Image($fileName);
$pdf->Output();
?>