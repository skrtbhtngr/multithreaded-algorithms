import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by skrtbhtngr on 3/31/16.
 */
@SuppressWarnings("Duplicates")
public class MergeSortThread implements Runnable
{
    private int lo;
    private int hi;
    public MergeSortThread(int lo, int hi)
    {
        this.hi=hi;
        this.lo=lo;
    }
    public void sort(int lo, int hi)
    {
        if(hi-lo>=1)
        {
            int mid=lo+(hi-lo)/2;
            if(hi-lo>=(MergeSort.input.length/MergeSort.processors))
            {
                //System.out.println((hi-lo)+" "+((ThreadPoolExecutor)MergeSort.es).getActiveCount()+" "+Thread.currentThread().getName());
                Thread a = new Thread(new MergeSortThread(lo, mid));
                Future f = MergeSort.es.submit(a);
                sort(mid + 1, hi);
                try
                {
                    f.get();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                if(MergeSort.input[mid].compareTo(MergeSort.input[mid+1])<=0)
                    return;
                MergeSort.merge(lo,mid,hi);
            }
            else if(hi-lo<=100)
            {
                InsertionSort is = new InsertionSort();
                is.input=MergeSort.input;
                is.sort(lo,hi);
            }
            else
            {
                //System.out.println((hi-lo)+" "+((ThreadPoolExecutor)MergeSort.es).getActiveCount()+" "+Thread.currentThread().getName());
                sort(lo,mid);
                sort(mid+1,hi);
                if(MergeSort.input[mid].compareTo(MergeSort.input[mid+1])<=0)
                    return;
                MergeSort.merge(lo,mid,hi);
            }
        }
    }
    public void run()
    {
        if(hi-lo>=1)
        {
            int mid=lo+(hi-lo)/2;
            if(hi-lo>=(MergeSort.input.length/MergeSort.processors))
            {
                //System.out.println((hi-lo)+" "+((ThreadPoolExecutor)MergeSort.es).getActiveCount()+" "+Thread.currentThread().getName());
                Thread a = new Thread(new MergeSortThread(lo, mid));
                Future f = MergeSort.es.submit(a);
                sort(mid + 1, hi);
                try
                {
                    f.get();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                MergeSort.merge(lo,mid,hi);
            }
            else if(hi-lo<=100)
            {
                InsertionSort is = new InsertionSort();
                is.input=MergeSort.input;
                is.sort(lo,hi);
            }
            else
            {
                //System.out.println((hi-lo)+" "+((ThreadPoolExecutor)MergeSort.es).getActiveCount()+" "+Thread.currentThread().getName());
                sort(lo,mid);
                sort(mid+1,hi);
                MergeSort.merge(lo,mid,hi);
            }
        }
    }
}
