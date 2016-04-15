import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by skrtbhtngr on 3/31/16.
 */
@SuppressWarnings("Duplicates")
public class MergeSort
{
    static int processors;
    static ExecutorService es;
    static Comparable[] input;
    static
    {
        es = Executors.newCachedThreadPool();
        processors = Runtime.getRuntime().availableProcessors();
    }
    public static void inPlaceMerge(int lo, int mid, int hi)
    {
        int l=lo, r=mid+1;
        Comparable tmp;
        if(input[mid].compareTo(input[r])<=0)
            return;
        while(l<=mid && r<=hi)
        {
            if(input[l].compareTo(input[r])<=0)
                l++;
            else
            {
                tmp=input[r];
                System.arraycopy(input,l,input,l+1,hi-l);
                input[l]=tmp;
                l++;
                r++;
                mid++;
            }
        }
    }
    public static void merge(int lo, int mid, int hi)
    {
        int l=lo, r=mid+1, k=0;
        Comparable[] aux = new Comparable[hi-lo+1];
        while(l<=mid && r<=hi)
        {
            if(input[l].compareTo(input[r])<0)
                aux[k++]=input[l++];
            else
                aux[k++]=input[r++];
        }
        while(l<=mid)
            aux[k++]=input[l++];
        while(r<=hi)
            aux[k++]=input[r++];
        for(int i=lo;i<=hi;i++)
            input[i]=aux[i-lo];
    }
    public static void sort(int lo, int hi)
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
                if(input[mid].compareTo(input[mid+1])<=0)
                    return;
                MergeSort.merge(lo,mid,hi);
            }
            else if(hi-lo<=10)
            {
                InsertionSort is = new InsertionSort();
                is.input=MergeSort.input;
                is.sort(lo,hi);
            }
            else
            {
                sort(lo,mid);
                sort(mid+1,hi);
                if(input[mid].compareTo(input[mid+1])<=0)
                    return;
                MergeSort.merge(lo,mid,hi);
            }
        }
    }
    public static boolean check()
    {
        for(int i=0;i<input.length-1;i++)
            if(input[i].compareTo(input[i+1])>0)
                return false;
        return true;
    }
    public static void main(String[] args)
    {
        Integer[] arr = new Integer[10000000]; //Works for a 100 million numbers!
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        for(int i=0;i<arr.length;i++)
            arr[i]=rnd.nextInt(1000000);

        MergeSort.input=arr;
        long a = System.nanoTime();
        MergeSort.sort(0,arr.length-1);
        long b = System.nanoTime();
        System.out.println("Time taken: "+(b-a)/1E9);
        System.out.println(MergeSort.check());

        System.out.println(((ThreadPoolExecutor)MergeSort.es).getPoolSize());
        MergeSort.es.shutdown();

        rnd.setSeed(System.nanoTime());
        for(int i=0;i<arr.length;i++)
            arr[i]=rnd.nextInt(1000000);
        a = System.nanoTime();
        Arrays.parallelSort(arr);
        b = System.nanoTime();
        System.out.println("Time taken: "+(b-a)/1E9);
    }
}
