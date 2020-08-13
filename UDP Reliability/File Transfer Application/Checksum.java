import java.security.MessageDigest;
import java.math.BigInteger;    
import java.security.NoSuchAlgorithmException; 
import java.nio.charset.StandardCharsets; 
class Checksum{

    static byte[] gettingSHA(String inp){  
        try{ 
            MessageDigest mdig = MessageDigest.getInstance("SHA-256");  
            return mdig.digest(inp.getBytes(StandardCharsets.UTF_8)); 
        } 
        catch(NoSuchAlgorithmException e){
            System.out.println(e);
            return null;
        }
    } 
        
    static String toHex(byte[] hashing) { 
        BigInteger num = new BigInteger(1, hashing);  
        StringBuilder hex = new StringBuilder(num.toString(16));  
        while (hex.length() < 32)  
        {  
            hex.insert(0, '0');  
        }  

        return hex.toString();  
    }
}