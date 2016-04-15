import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("Duplicates")
public final class Quicksort<T extends Comparable<T>>
{
    private static final int processors;
    private static final ExecutorService es;
    private final T[] input;
    static
    {
        es = Executors.newCachedThreadPool();
        processors = Runtime.getRuntime().availableProcessors();
    }
    public Quicksort(T[] input)
    {
        this.input=input;
    }
    private void swap(int i, int j)
    {
        T temp=input[i];
        input[i]=input[j];
        input[j]=temp;
    }
    private int partition(int lo, int hi)
    {
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        swap(hi,rnd.nextInt(hi-lo+1)+lo);

        T pivot = input[hi];

        int i=lo;
        for(int j=lo;j<hi;j++)
            if(input[j].compareTo(pivot)<0)
                swap(i++,j);
        swap(i,hi);
        return i;
    }
    public void sort(int lo, int hi)
    {
        if(hi-lo>=1)
        {
            if (hi - lo >= (input.length / Quicksort.processors))
            {
                int p = partition(lo, hi);
                Thread a = new Thread()
                {
                    public void run()
                    {
                        sort(lo, p - 1);
                    }
                };
                Future f = es.submit(a);
                sort(p + 1, hi);
                try
                {
                    f.get();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            } else if (hi - lo <= 100)
                new InsertionSort<>(input).sort(lo, hi);
            else
            {
                int p = partition(lo, hi);
                sort(lo, p - 1);
                sort(p + 1, hi);
            }
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

        Quicksort qs = new Quicksort<>(arr);
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
