package src;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.management.RuntimeErrorException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Hash {
    /*
    Creates a salt value with the string input below.
     */
    private final byte[] salt = "cVKjMgU04etW9w==".getBytes();

    /*
    This method calls the private hash string method below to ensure that outside files cannot
    call the hash string method.
     */
    public String scrambleString(char[] unscrambledString){
        //Returns the string given after it is hashed.
        return hashString(unscrambledString);
    }

    /*
    Takes a char array and hashes it so that the password is not visible, and it is virtually impossible to recover
    the original password.
     */
    private String hashString(char[] unhashedString){
        /*
        Creates a PBEKeySpec with the unhashed string, the salt variable, the number of iterations, and the key
        length.
         */
        PBEKeySpec spec = new PBEKeySpec(unhashedString, salt, 65536, 128);
        try {
            /*
            Creates a secret key factory with the PBKDF2 algorithm.
             */
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            /*
            Creates a hashed variable with the PBEKeySpec provided above. This is the newly generated hashed string
             */
            byte[] hash = keyFactory.generateSecret(spec).getEncoded();

            /*
            Returns the hashed string after encoding the byte array into a string.
             */
            return Base64.getEncoder().encodeToString(hash);
        } catch (Error | NoSuchAlgorithmException | InvalidKeySpecException e) {
            assert e instanceof Error;
            throw new RuntimeErrorException((Error) e);
        }
    }
}
