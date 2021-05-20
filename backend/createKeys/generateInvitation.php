<?php
require_once("./generateRSAKeys.php");
require_once("./rsa.php");
require_once("./phpqrcode/qrlib.php");
require_once('./fpdf/fpdf.php');

$rsa=new Rsa();
$keyPair=createRSAKeys();
$rndBytes=openssl_random_pseudo_bytes(20, $strong);
$fileName=base64_encode($rndBytes);
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
$pdf->SetFont('Arial','B',16);
$pdf->Cell(40,10,'Dies ist ein Anschreiben');
$pdf->Ln();
$pdf->Image($fileName, 30, 30);
$pdf->Output();

?>