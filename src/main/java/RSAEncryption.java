import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.sql.Timestamp;
import java.math.*;
import java.util.Random;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class RSAEncryption {


    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void GetTimestamp(String info){
        System.out.println(info + new Timestamp((new Date()).getTime()));
    }

    public static AsymmetricCipherKeyPair GenerateKeys(int keySize) throws NoSuchAlgorithmException{
        RSAKeyPairGenerator generator = new RSAKeyPairGenerator();
        generator.init(new RSAKeyGenerationParameters(new BigInteger("10001", 16),SecureRandom.getInstance("SHA1PRNG"),keySize,80));
        return generator.generateKeyPair();
    }

    public static String Encrypt(byte[] data, AsymmetricKeyParameter publicKey) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        RSAEngine engine = new RSAEngine();
        engine.init(true, publicKey);
        byte[] hexEncodedCipher = engine.processBlock(data, 0, data.length);
        return getHexString(hexEncodedCipher);
    }

    public static String Decrypt(String encrypted, AsymmetricKeyParameter privateKey) throws InvalidCipherTextException{
        Security.addProvider(new BouncyCastleProvider());
        AsymmetricBlockCipher engine = new RSAEngine();
        engine.init(false, privateKey);
        byte[] encryptedBytes = hexStringToByteArray(encrypted);
        byte[] hexEncodedCipher = engine.processBlock(encryptedBytes, 0, encryptedBytes.length);
        return new String (hexEncodedCipher);
    }

    public static String[] generateValues(int qtde){
        String[] out = new String[qtde];
        Random rand = new Random();
        int i;
        for(i = 0;i<qtde;i++){
            out[i]="";
            out[i]+=rand.nextInt(10)+":"+rand.nextInt(10)+":"+rand.nextInt(10)+":"+rand.nextInt(10)+":"+rand.nextInt(10);
            System.out.println(out[i]);
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        int tamanhoArray=100000;
        int keySize=4096;
        String[] values = generateValues(tamanhoArray);
        AsymmetricCipherKeyPair keyPair = GenerateKeys(keySize);
        String encryptedValues[] = new String[tamanhoArray];
        int i = 0;
        long start1 = System.currentTimeMillis();
        for(String v:values){
            String encryptedMessage = Encrypt(v.getBytes("UTF-8"), keyPair.getPublic());
            encryptedValues[i]=encryptedMessage;
            i+=1;
        }
        long end1 = System.currentTimeMillis();
        System.out.println("encryptedValues len");
        for(String v:encryptedValues){
            System.out.println(v.length());
        }
        System.out.println("Encryption time "+keySize+" "+ tamanhoArray +": "+ (end1-start1));
        start1 = System.currentTimeMillis();
        for(String v:encryptedValues){
            String decryptedMessage = Decrypt(v, keyPair.getPrivate());
        }
        end1 = System.currentTimeMillis();
        System.out.println("Decryption time "+keySize+" "+ tamanhoArray +": "+ (end1-start1));

    }

}