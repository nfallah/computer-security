public class ColumnEncrypt {
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
        while (i + 1 < args.length) {
            if (args[i].equals("-b") && block_size == null)
            {
                try
                {
                    block_size = Integer.parseInt(args[i + 1]);
                    if (block_size < 1) {
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
        if (block_size == null) {
            block_size = BLOCK_SIZE_DEFAULT;
        }
        if (i < args.length)
        {
            path = args[i];
        }
        System.out.println("-b: " + block_size);
        System.out.println("-k: " + key);
        System.out.println("path: " + path);
    }
}