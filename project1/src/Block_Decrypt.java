import java.io.*;

public class Block_Decrypt {
    private static long lcg(long current) {
        long a = 1103515245;
        int c = 12345;
        int m = 256;
        return (a * current + c) % m;
    }

    private static long sdbm(String str) {
        long hash = 0;
        for (char c_char : str.toCharArray()) {
            int c_int = ((int)c_char) + 128;
            hash = c_int + (hash << 6) + (hash << 16) - hash;
        }
        return hash;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Incorrect number of arguments");
            return;
        }
        String password = args[0];
        String end_path = args[1];
        String start_path = args[2];
        long seed = sdbm(password);
        long lcg = lcg(seed);
        try
        {
            FileInputStream end_stream = new FileInputStream(end_path);
            FileOutputStream start_stream = new FileOutputStream(start_path);
            byte[] previousBlock = new byte[16];
            for (int i = 0; i < 16; i++)
            {
                previousBlock[i] = (byte)lcg;
                lcg = lcg(lcg);
            }
            byte[] cipherBlock = new byte[16];
            int bytesRead;
            while ((bytesRead = end_stream.read(cipherBlock)) != -1)
            {
                // Keystream generation (easy)
                byte[] keystream = new byte[16];
                for (int i = 0; i < 16; i++)
                {
                    keystream[i] = (byte)lcg;
                    lcg = lcg(lcg);
                }
                // Reverse shuffling (scary)
                for (int i = 15; i >= 0; i--)
                {
                    int index1 = keystream[i] & 0x0F;
                    int index2 = (keystream[i] & 0xF0) >> 4;
                    byte temp = cipherBlock[index1];
                    cipherBlock[index1] = cipherBlock[index2];
                    cipherBlock[index2] = temp;
                }
                // XOR operations (also scary)
                byte[] decryptedBlock = new byte[16];
                for (int i = 0; i < 16; i++)
                {
                    decryptedBlock[i] = (byte)(cipherBlock[i] ^ keystream[i] ^ previousBlock[i]);
                }
                // Final stuff
                if (bytesRead != 0)
                System.arraycopy(cipherBlock, 0, previousBlock, 0, bytesRead);
                // Padding (note to self test for multiples of 16)
                if (end_stream.available() == 0)
                {
                    int paddingLength = decryptedBlock[15] & 0xFF;
                    bytesRead -= paddingLength;
                }
                if (bytesRead != 0)
                start_stream.write(decryptedBlock, 0, bytesRead);
            }
            start_stream.close();
            end_stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }
}