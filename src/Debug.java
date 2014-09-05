/**
 * Created by eric on 4/24/14.
 */

package erflink;

public class Debug
{
    static final boolean debuggingON = true;

    static void println(String msg)
    {
        if (debuggingON)
            System.err.println(msg);
    }

    static void print(String msg)
    {
        if (debuggingON)
            System.err.print(msg);
    }
}
