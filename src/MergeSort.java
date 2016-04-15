import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("Duplicates")
public final class MergeSort<T extends Comparable<T>>
{
    private static final int processors;
    private static final ExecutorService es;
    private final T[] input;
    static
    {
        es = Executors.newCachedThreadPool();
        processors = Runtime.getRuntime().availableProcessors();
    }
    public MergeSort(T[] input)
    {
        this.input=input;
    }
    private void inPlaceMerge(int lo, int mid, int hi)
    {
        int l=lo, r=mid+1;
        T tmp;
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
    private void merge(int lo, int mid, int hi)
    {
        int l=lo, r=mid+1, k=0;
        T[] aux = (T[])new Comparable[hi-lo+1];
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
        System.arraycopy(aux,0,input,lo,hi-lo+1);
    }
    public void sort(int lo, int hi)
    {
        if(hi-lo>=1)
        {
            int mid=lo+(hi-lo)/2;
            if(hi-lo>=(input.length/MergeSort.processors))
            {
                Thread a = new Thread(){
                    public void run()
                    {
                        sort(lo,mid);
                    }
                };
                Future f = es.submit(a);
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
                merge(lo,mid,hi);
            }
            else if(hi-lo<=10)
                new InsertionSort<>(input).sort(lo,hi);
            else
            {
                sort(lo,mid);
                sort(mid+1,hi);
                if(input[mid].compareTo(input[mid+1])<=0)
                    return;
                merge(lo,mid,hi);
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
        for(int i=0;i<arr.length;i++)
            arr[i]=rnd.nextInt(1000000);

        MergeSort ms = new MergeSort<>(arr);
        long a = System.nanoTime();
        ms.sort(0,arr.length-1);
        long b = System.nanoTime();
        System.out.println("Time taken: "+(b-a)/1E9);
        System.out.println(ms.check());

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
