<?php
/**
 * @author alun (http://alunblog.duapp.com)
 * @version 1.0
 * @created 2013-5-17
 */
 
class Rsa
{
private static $PRIVATE_KEY = '-----BEGIN RSA PRIVATE KEY-----
MIICXQIBAAKBgQCsKPeVoi2tRZw6WPYuaxz6ISz3209SVx65Vl2NhWJMj4Gt03Nl
7zhOAqEqrWx+h80kXw+hFnUbT1FJwCorgORmFP96RlhrM2qhigphTnrHPV327caO
fPbyljV1giyJkoRM17WLtlbNBa/vt7r/aKpsErojnG36XIJojBCma0LEzwIDAQAB
AoGBAI8UgSgAIFwoRT4M35UWen+7gMcBorv5IdTA3YGXwGmGvyz7VqX6Gd5juxRg
C5JhRPIcez3TD0LCOfnEVofPWIHNBjl+k0lL3dQuzWT3kpTFYUIp50znG72Kt8r1
lcnuFKxXQ8W/cPaM/kKIpT9650H04xSSZh8uEEY0MDuIGUFBAkEA30CncIJRnr+q
gG2nbEuV8IY2esu7pNSC0Viy6GXAGnWEhskzq3ofkghKE4vgmMW9bLGIHYiyPUWF
v2V4WRZ8IQJBAMVpvXNocoUXVIKSNCR8adtInzf9zeveAAutVUGE80qAaPOVYfo4
aMia8eVnUZwn6IeQD9u87vNzGt6S8C24ou8CQE9XT+ppNg6f+T1ZOwX+utPXLudn
HlHPAIrb5gE3oBUHLMkmGknXxf6FGaZmsLQ6mj5VvZUZbpbd7VF/A01N82ECQQC1
ruI/w4GaAWWyoZHBfosB2G1IX5pTmEXceVK8cPpjt5hfjTzVx3KQJSG46gSOvZ/g
m3Is3k3f2jCBZSQMQCAbAkB4Qqi4RfcoSRzcHnG8evEmb5mgzxe5Qj2if7Z6lmUb
C+sXsyxl0bDp62Ps8iKwmvKDmKysq8kox4Y3oC66uZf9
-----END RSA PRIVATE KEY-----';
    /**
    *Returns the corresponding private key
    */
    private static function getPrivateKey(){
    
        $privKey = self::$PRIVATE_KEY;
         
        return openssl_pkey_get_private($privKey);      
    }
 
    /**
     * secret key encryption
     */
    public static function privEncrypt($data)
    {
        if(!is_string($data)){
                return null;
        }           
        return openssl_private_encrypt($data,$encrypted,self::getPrivateKey())? base64_encode($encrypted) : null;
    }
    
    public static function encrypt($data, $publicKey)
    {
        if(!is_string($data)){
            return null;
        }
        if(openssl_public_encrypt($data, $encrypted, $publicKey)){
            return $encrypted;
        } else {
            return null;
        }
    }
    
    /**
     * Private key decryption
     */
    public static function privDecrypt($encrypted)
    {
        if(!is_string($encrypted)){
                return null;
        }
        return (openssl_private_decrypt(base64_decode($encrypted), $decrypted, self::getPrivateKey()))? $decrypted : null;
    }
    public static function decrypt($encrypted, $privKey)
    {
        
        if(!is_string($encrypted)){
                return null;
        }
        return (openssl_private_decrypt(base64_decode($encrypted), $decrypted, $privKey))? $decrypted : null;
    }

    public static function signMessage($message){
        openssl_sign($message, $signature, self::getPrivateKey(), OPENSSL_ALGO_SHA512);
        return($signature);
    }
}
 
?>