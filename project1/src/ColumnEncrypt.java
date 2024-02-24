import java.io.*;
import java.util.*;

public class ColumnEncrypt
{
    private static final int BLOCK_SIZE_DEFAULT = 16; // Measured in bytes
    public static void main(String[] args)
    {
        Integer block_size = null;
        String key = null, path = null;
        if (args.length < 2 || args.length > 5)
        {
            return;
        }
        int i = 0;
        while (i + 1 < args.length)
        {
            if (args[i].equals("-b") && block_size == null)
            {
                try
                {
                    block_size = Integer.parseInt(args[i + 1]);
                    if (block_size < 1)
                    {
                        return;
                    }
                }
                catch(Exception e)
                {
                    return;
                }
            }
            else if (args[i].equals("-k") && key == null && args[i + 1].length() >= 1)
            {
                key = args[i + 1];
            }
            else
            {
                return;
            }
            i += 2;
        }
        if (key == null)
        {
            return;
        }
        if (block_size == null)
        {
            block_size = BLOCK_SIZE_DEFAULT;
        }
        if (i < args.length)
        {
            path = args[i];
        }
        int x = key.length();
        int y = (int)Math.ceil((float)block_size / x);
        char[] key_sorted = key.toCharArray();
        Arrays.sort(key_sorted);
        boolean[] found = new boolean[x];
        int[] order = new int[x];
        for (int j = 0; j < x; j++)
        {
            char target = key.charAt(j);
            for (int k = 0; k < x; k++)
            {
                if (key_sorted[k] == target && found[k] == false)
                {
                    found[k] = true;
                    order[j] = k;
                    break;
                }
            }
        }
        if (path == null)
        {
            try
            {
                byte[] stream_buffer = new byte[block_size];
                int bytes_read;
                while ((bytes_read = System.in.read(stream_buffer)) != -1)
                {
                    Byte[][] table = new Byte[x][y];
                    int tmp = bytes_read;
                    // Put into table left to right, top to bottom
                    for (int _y = 0; _y < y && tmp > 0; _y++)
                    {
                        for (int _x = 0; _x < x && tmp > 0; _x++)
                        {
                            table[_x][_y] = stream_buffer[bytes_read - tmp--];
                        }
                    }
                    // Now extract from top to bottom in order of keys
                    byte[] output_buffer = new byte[bytes_read];
                    tmp = 0;
                    for (int _x = 0; _x < x; _x++)
                    {
                        int _x_actual = order[_x];
                        for (int _y = 0; _y < y; _y++)
                        {
                            if (table[_x_actual][_y] != null)
                            {
                                output_buffer[tmp++] = table[_x_actual][_y];
                            }
                        }
                    }
                    OutputStream stream = System.out;
                    stream.write(output_buffer, 0, tmp);
                    stream.flush();
                    stream_buffer = new byte[block_size];
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.err.println(e);
                return;
            }
        }
        else
        {
            try
            {
                FileInputStream file_stream = new FileInputStream(path);
                byte[] stream_buffer = new byte[block_size];
                int bytes_read;
                while ((bytes_read = file_stream.read(stream_buffer)) != -1)
                {
                    Byte[][] table = new Byte[x][y];
                    int tmp = bytes_read;
                    // Put into table left to right, top to bottom
                    for (int _y = 0; _y < y && tmp > 0; _y++)
                    {
                        for (int _x = 0; _x < x && tmp > 0; _x++)
                        {
                            table[_x][_y] = stream_buffer[bytes_read - tmp--];
                        }
                    }
                    // Now extract from top to bottom in order of keys
                    byte[] output_buffer = new byte[bytes_read];
                    tmp = 0;
                    for (int _x = 0; _x < x; _x++)
                    {
                        int _x_actual = order[_x];
                        for (int _y = 0; _y < y; _y++)
                        {
                            if (table[_x_actual][_y] != null)
                            {
                                output_buffer[tmp++] = table[_x_actual][_y];
                            }
                        }
                    }
                    OutputStream stream = System.out;
                    stream.write(output_buffer, 0, tmp);
                    stream.flush();
                    stream_buffer = new byte[block_size];
                }
                file_stream.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.err.println(e);
                return;
            }
        }
    }
}