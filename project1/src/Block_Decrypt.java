import java.io.*;

public class Block_Decrypt
{
    private static long lcg(long current)
    {
        long a = 1103515245;
        int c = 12345;
        int m = 256;
        return (a * current + c) % m;

    }
    private static long sdbm(String str)
    {
        long hash = 0;
        for (char c_char : str.toCharArray())
        {
            int c_int = ((int)c_char) + 128;
            hash = c_int + (hash << 6) + (hash << 16) - hash;
        }
        return hash;
    }

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            return;
        }
        String password = args[0];
        if (password.length() == 0) return;
        String end_path = args[1];
        String start_path = args[2];
        long seed = sdbm(password);
        long lcg = lcg(seed);
        try
        {
            FileInputStream end_stream = new FileInputStream(end_path);
            FileOutputStream start_stream = new FileOutputStream(start_path);
            byte[] last_block = new byte[16];
            for (int i = 0; i < 16; i++)
            {
                last_block[i] = (byte)lcg;
                lcg = lcg(lcg);
            }
            byte[] data = new byte[16];
            int bytes_read;
            while ((bytes_read = end_stream.read(data)) != -1)
            {
                //System.err.println(bytes_read);
                // Step 1: Read keystream
                byte[] keystream = new byte[16];
                for (int i = 0; i < 16; i++)
                {
                    keystream[i] = (byte)lcg;
                    lcg = lcg(lcg);
                }
                // Step 2: Obtain temp_block by XORING keystream_N with ciphertextblock_N
                byte[] temp_block = new byte[16];
                for (int i = 0; i < 16; i++)
                {
                    temp_block[i] = (byte)((int)keystream[i] ^ (int)data[i]);
                }
                // Step 3: Shuffle in reverse
                for (int i = 15; i >= 0; i--)
                {
                    int index1 = keystream[i] & 0x0F;
                    int index2 = (keystream[i] & 0xF0) >> 4;
                    byte temp = temp_block[index1];
                    temp_block[index1] = temp_block[index2];
                    temp_block[index2] = temp;
                }
                // Step 4: obtain plaintext by XORING temp_block_N with ciphertextblock_N-1
                byte[] plaintext_block = new byte[16];
                for (int i = 0; i < 16; i++)
                {
                    plaintext_block[i] = (byte)((int)temp_block[i] ^ (int)last_block[i]);
                }
                // Step 5 set last block to current ENCRYPTED block
                System.arraycopy(data, 0, last_block, 0, 16);
                // Step 6: deal with padding
                if (end_stream.available() == 0)
                {
                    //System.err.println("PADDING: " + plaintext_block[15]);
                    int padding = (int)plaintext_block[15];
                    //System.err.println("PADDING: " + (int)padding);
                    if (padding == 16) {
                        start_stream.close();
                        end_stream.close();
                        return;
                    }
                    int actual = 16-padding;
                    byte[] temp = new byte[actual];
                    for (int i = 0; i < actual; i++) {
                        temp[i] = plaintext_block[i];
                    }
                    plaintext_block = temp;
                }
                // Step 7: finally write to stdout
                start_stream.write(plaintext_block);
            }
            start_stream.close();
            end_stream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }
    }
}