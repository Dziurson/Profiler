package profiler;
import skiplist.Skiplist;
import mypackage.graf.*;

public class RunProfiler
{
    public static void main(String[] args)
    {
        try
        {
            Profiler p = new Profiler(Graf.class); 
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }
}
