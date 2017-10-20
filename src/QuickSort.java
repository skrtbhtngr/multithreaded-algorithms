import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Quicksort is an in-place, comparison-based efficient sorting algorithm. It is a divide and
 * conquer algorithm developed by Tony Hoare in 1959, with his work published in 1961.
 * <p>
 * This class provides the multithreaded version of the quicksort algorithm.
 *
 * @param <T> This describes the type parameter
 * @author Sukrit Bhatnagar
 * @version 1.0
 * @see Comparable
 */
public final class QuickSort<T extends Comparable<T>>
{

    /**
     * Defines the value of array length below which the algorithm switches to Insertion Sort.
     */
    private final int threshold;

    private Random rnd;
    private ExecutorService es;
    private final T[] input;

    /**
     * Creates a new instance by taking in an array.
     *
     * @param input the array to be sorted
     */
    public QuickSort(T[] input)
    {
        rnd = new Random();
        this.input = input;
        threshold = (input.length / Runtime.getRuntime().availableProcessors()) * 2;
    }

    private void swap(int i, int j)
    {
        T temp = input[i];
        input[i] = input[j];
        input[j] = temp;
    }

    /**
     *
     * @param lo lowest index of the array
     * @param hi highest index of the array
     * @return index of the pivot element
     */
    private int partition(int lo, int hi)
    {
        rnd.setSeed(System.nanoTime());
        swap(hi, rnd.nextInt(hi - lo + 1) + lo);
        T pivot = input[hi];

        int i = lo;
        for (int j = lo; j < hi; j++)
            if (input[j].compareTo(pivot) < 0)
                swap(i++, j);
        swap(i, hi);
        return i;
    }

    /**
     * Sorts the array
     */
    public void sort()
    {
        es = Executors.newCachedThreadPool();
        sort(0, input.length - 1);
        es.shutdownNow();
    }

    /**
     * Sorts the array using threads, if possible.
     *
     * @param lo lowest index of the array
     * @param hi highest index of the array
     */
    private void sort(int lo, int hi)
    {
        if (hi - lo >= 1)
        {
            if (hi - lo >= threshold)
            {
                int p = partition(lo, hi);
                Future f = es.submit(new Thread(() -> sort(lo, p - 1)));
                sort(p + 1, hi);
                try
                {
                    f.get();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (hi - lo <= 10)
                new InsertionSort<>(input).sort(lo, hi);
            else
            {
                int p = partition(lo, hi);
                sort(lo, p - 1);
                sort(p + 1, hi);
            }
        }
    }

    /**
     * Checks if the array is sorted.
     *
     * @return true if the array is sorted; false otherwise
     */
    public boolean check()
    {
        for (int i = 0; i < input.length - 1; i++)
            if (input[i].compareTo(input[i + 1]) > 0)
                return false;
        return true;
    }

    /**
     * Accessor function for the array.
     *
     * @return the sorted array
     */
    public T[] getResults()
    {
        return input;
    }

    @Override
    public String toString()
    {
        return Arrays.toString(input);
    }

    public static void main(String[] args)
    {
        Integer[] arr = new Integer[10000000];
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(1000000);

        QuickSort qs = new QuickSort<>(arr);
        long a = System.nanoTime();
        qs.sort();
        long b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
        System.out.println(qs.check());

        System.out.println(((ThreadPoolExecutor) qs.es).getPoolSize());

        rnd.setSeed(System.nanoTime());
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(1000000);
        a = System.nanoTime();
        Arrays.parallelSort(arr);
        b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);

        rnd.setSeed(System.nanoTime());
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(1000000);
        qs = new QuickSort<>(arr);
        a = System.nanoTime();
        qs.sort();
        b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
    }
}
