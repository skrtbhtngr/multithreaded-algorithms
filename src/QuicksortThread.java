import java.util.concurrent.Future;

/**
 * Created by skrtbhtngr on 4/2/16.
 */
@SuppressWarnings("Duplicates")
public class QuicksortThread implements Runnable
{
    private int lo;
    private int hi;
    public QuicksortThread(int lo, int hi)
    {
        this.lo=lo;
        this.hi=hi;
    }
    public void sort(int lo, int hi)
    {
        if(lo<hi)
        {
            if(hi-lo>=(Quicksort.input.length/Quicksort.processors))
            {
                int p = Quicksort.partition(lo, hi);
                Thread a = new Thread(new QuicksortThread(lo, p - 1));
                Future f = Quicksort.es.submit(a);
                sort(p + 1, hi);
                try
                {
                    f.get();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(hi-lo<=100)
            {
                InsertionSort is = new InsertionSort();
                is.input=Quicksort.input;
                is.sort(lo,hi);
            }
            else
            {
                int p = Quicksort.partition(lo, hi);
                sort(lo, p - 1);
                sort(p + 1, hi);
            }
        }
    }
    public void run()
    {
        if(lo<hi)
        {
            if(hi-lo>=(Quicksort.input.length/Quicksort.processors))
            {
                int p = Quicksort.partition(lo, hi);
                Thread a = new Thread(new QuicksortThread(lo, p - 1));
                Future f = Quicksort.es.submit(a);
                sort(p + 1, hi);
                try
                {
                    f.get();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(hi-lo<=100)
            {
                InsertionSort is = new InsertionSort();
                is.input=Quicksort.input;
                is.sort(lo,hi);
            }
            else
            {
                int p = Quicksort.partition(lo, hi);
                sort(lo, p - 1);
                sort(p + 1, hi);
            }
        }
    }
}
