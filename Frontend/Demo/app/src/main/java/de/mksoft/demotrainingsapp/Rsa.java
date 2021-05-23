package de.mksoft.demotrainingsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.TreeSet;

import javax.crypto.Cipher;

public class Rsa {
    private MainActivity view;

    public Rsa(MainActivity view){
        this.view=view;
    }

    private String getKey() throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(this.view.getAssets().open("rsapublickey.pem")));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }

    public RSAPrivateKey getPrivateKeyFromPreferences() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SharedPreferences pref=view.getSharedPreferences("TrainingApp", Context.MODE_PRIVATE);
        String pem=pref.getString("privateKey", "");
        if(!pem.equals("")){
            pem = pem.replace("-----BEGIN PRIVATE KEY-----\n", "");
            pem=pem.replace("-----END PRIVATE KEY-----", "");
            //byte[] encoded = Base64.decodeBase64(privateKeyPEM);
            byte[] encoded=Base64.decode(pem, Base64.DEFAULT);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
            return privKey;

        }
        return null;
    }

    public RSAPrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        String privateKeyPEM = this.getKey();
        return getPrivateKeyFromString(privateKeyPEM);
    }

    public  RSAPrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        //byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        byte[] encoded=Base64.decode(privateKeyPEM, Base64.DEFAULT);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
        return privKey;
    }


    public  RSAPublicKey getPublicKey() throws IOException, GeneralSecurityException {
        String publicKeyPEM = getKey();
        return getPublicKeyFromString(publicKeyPEM);
    }

    public  RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decode(publicKeyPEM, Base64.DEFAULT);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    public  String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature sign = Signature.getInstance("SHA512withRSA");
        sign.initSign(privateKey);
        sign.update(message.getBytes("UTF-8"));
        return new String(Base64.encode(sign.sign(), Base64.DEFAULT));
    }


    public  boolean verify(PublicKey publicKey, String message, String signature) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Signature sign = Signature.getInstance("SHA512withRSA");
        sign.initVerify(publicKey);
        sign.update(message.getBytes("UTF-8"));
        return sign.verify(Base64.decode(signature.getBytes("UTF-8"), Base64.DEFAULT));
    }

    public  String encrypt(String rawText, PublicKey publicKey) throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.encodeToString(cipher.doFinal(rawText.getBytes("UTF-8")), Base64.DEFAULT);
    }

    public  String decrypt(String cipherText, PrivateKey privateKey) throws IOException, GeneralSecurityException {
        //Cipher cipher = Cipher.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)), "UTF-8");
    }

    public void getAlgos(){
        TreeSet<String> algorithms = new TreeSet<>();
        for (Provider provider : Security.getProviders())
            for (Provider.Service service : provider.getServices())
                if (service.getType().equals("Signature"))
                    algorithms.add(service.getAlgorithm());
        for (String algorithm : algorithms)
            Log.v("CRYPTO", algorithm);
    }
}
