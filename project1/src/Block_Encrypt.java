import java.io.*;

public class Block_Encrypt
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
        String start_path = args[1];
        String end_path = args[2];
        long seed = sdbm(password);
        long lcg = lcg(seed);
        try
        {
            FileInputStream start_stream = new FileInputStream(start_path);
            FileOutputStream end_stream = new FileOutputStream(end_path);
            byte[] last_block = new byte[16];
            for (int i = 0; i < 16; i++)
            {
                last_block[i] = (byte)lcg;
                lcg = lcg(lcg);
            }
            byte[] data = new byte[16];
            int bytes_read;
            boolean final_pad = true;
            boolean done = false;
            while (true)
            {
                bytes_read = start_stream.read(data);
                if (bytes_read == -1 && final_pad == false)
                {
                    break;
                }
                else if (bytes_read == -1)
                {
                    //System.err.println("PADDING*: 0");
                    for (int i = 0; i < 16; i++)
                    {
                        data[i] = ((Integer)16).byteValue();
                    }
                    done = true;
                }
                // Step 1 (add padding)
                if (bytes_read < 16 && bytes_read != -1)
                {
                    final_pad = false;
                    for (int i = bytes_read; i < 16; i++)
                    {
                        data[i] = ((Integer)(16 - bytes_read)).byteValue();
                    }
                    //System.err.println("PADDING-: " + (16-bytes_read));
                }
                byte[] tmp = new byte[16];
                // Step 2 (temp block)
                for (int i = 0; i < 16; i++)
                {
                    tmp[i] = (byte)((int)data[i] ^ (int)last_block[i]);
                }
                byte[] tmp2 = new byte[16];
                // Step 3 (16 keystream bytes)
                for (int i = 0; i < 16; i++)
                {
                    tmp2[i] = (byte)lcg;
                    lcg = lcg(lcg);
                }
                // Steps 4 (shuffling)
                for (int i = 0; i < 16; i++)
                {
                    int index1 = tmp2[i] & 0x0F;
                    int index2 = (tmp2[i] & 0xF0) >> 4;
                    byte temp = tmp[index1];
                    tmp[index1] = tmp[index2];
                    tmp[index2] = temp;
                }
                // Step 5 (XOR)
                for (int i = 0; i < 16; i++)
                {
                    tmp[i] = (byte)((int)tmp[i] ^ (int)tmp2[i]);
                }
                // Step 6 (writing)
                end_stream.write(tmp);
                System.arraycopy(tmp, 0, last_block, 0, 16);
                // If had to add 16 bytes of padding, begone
                if (done)
                {
                    break;
                }
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