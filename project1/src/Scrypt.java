import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Scrypt
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
        String start_path = args[1];
        String end_path = args[2];
        long seed = sdbm(password);
        long lcg = lcg(seed);
        //funnyTesting(password, 10);
        try
        {
            FileInputStream start_stream = new FileInputStream(start_path);
            FileOutputStream end_stream = new FileOutputStream(end_path);
            byte[] data = new byte[1];
            while (start_stream.read(data) != -1)
            {
                end_stream.write(new byte[] {(byte)(data[0] ^ lcg)});
                lcg = lcg(lcg);
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

    private static void funnyTesting(String str, int n)
    {
        long lcg = lcg(sdbm(str));
        int i = 0;
        while (i < n)
        {
            System.out.println(lcg);
            lcg = lcg(lcg);
            i++;
        }
    }
}
