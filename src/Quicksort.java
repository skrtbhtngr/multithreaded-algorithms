import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("Duplicates")
public class Quicksort
{
    static int processors;
    static ExecutorService es;
    static Comparable[] input;
    public Quicksort()
    {
        es = Executors.newCachedThreadPool();
        processors = Runtime.getRuntime().availableProcessors();
    }
    public static void swap(int i, int j)
    {
        Comparable temp=input[i];
        input[i]=input[j];
        input[j]=temp;
    }
    public static int partition(int lo, int hi)
    {
        Comparable temp;
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        int r = rnd.nextInt(hi-lo+1)+lo;
        temp=input[r];
        input[r]=input[hi];
        input[hi]=temp;

        Comparable pivot = input[hi];

        int i=lo, j;
        for(j=lo;j<hi;j++)
            if(input[j].compareTo(pivot)<0)
                swap(i++,j);
        swap(i,hi);
        return i;
    }
    public void sort(int lo, int hi)
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
    public boolean check()
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
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(1000000);

        Quicksort qs = new Quicksort();
        qs.input = arr;
        long a = System.nanoTime();
        qs.sort(0, arr.length - 1);
        long b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
        System.out.println(qs.check());

        System.out.println(((ThreadPoolExecutor) Quicksort.es).getPoolSize());
        Quicksort.es.shutdown();

        rnd.setSeed(System.nanoTime());
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(1000000);
        a = System.nanoTime();
        Arrays.parallelSort(arr);
        b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
    }
}
