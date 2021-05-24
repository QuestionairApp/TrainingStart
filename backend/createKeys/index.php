<?php
require_once("./generateRSAKeys.php");
require_once("./rsa.php");
require_once("./phpqrcode/qrlib.php");
require_once('./fpdf/fpdf.php');

$rsa=new Rsa();
$keyPair=createRSAKeys();
$publicKey=$keyPair->public;
//$testCrypt=$rsa->encrypt("Hallo Welt1", $publicKey);
$key=base64_encode(openssl_random_pseudo_bytes(32));
$testCrypt=openssl_public_encrypt(base64_encode($key), $encrypted, $publicKey);
$rndBytes=openssl_random_pseudo_bytes(20, $strong);
$fileName=base64_encode($rndBytes);
if($testCrypt){
    $myfile = fopen("./tmp/".$fileName, "w") or die("Unable to open file");
    fwrite($myfile, base64_encode($encrypted));
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
$pdf->MultiCell(170,10, base64_encode($key));
$pdf->Ln();
$pdf->Cell(40, 10, "Signatur");
$pdf->Ln();
$pdf->MultiCell(170,10, $signature);
$pdf->Ln();

$pdf->Cell(40,10, "toHash");
$pdf->Ln();

$pdf->MultiCell(170,10, $toHash);
$pdf->Ln();
$pdf->MultiCell(40, 10, $hash);
$pdf->Ln();
$pdf->Image($fileName);
$pdf->Output();
?>