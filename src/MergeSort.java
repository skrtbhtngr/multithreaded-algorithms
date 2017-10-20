import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Mergesort is a stable and comparison-based efficient sorting algorithm. It is a divide and conquer
 * algorithm that was invented by John von Neumann in 1945.
 * <p>
 * This class provides the multithreaded version of the mergesort algorithm.
 *
 * @param <T> This describes the type parameter
 * @author Sukrit Bhatnagar
 * @version 1.0
 * @see Comparable
 */
public final class MergeSort<T extends Comparable<T>>
{
    /**
     * Defines the value of array length below which the algorithm switches to Insertion Sort.
     */
    private final int threshold;

    private ExecutorService es;
    private final T[] input;

    /**
     * Creates a new instance by taking in an array.
     *
     * @param input the array to be sorted
     */
    public MergeSort(T[] input)
    {
        this.input = input;
        threshold = (input.length / Runtime.getRuntime().availableProcessors()) / 2;
    }

    /**
     * Implements the in-place merge algorithm.
     *
     * @param lo lowest index of the array
     * @param hi highest index of the array
     */
    private void inPlaceMerge(int lo, int hi)
    {
        int mid = lo + ((hi - lo) / 2);
        int l = lo, r = mid + 1;
        T tmp;
        if (input[mid].compareTo(input[r]) <= 0)
            return;
        while (l <= mid && r <= hi)
        {
            if (input[l].compareTo(input[r]) <= 0)
                l++;
            else
            {
                tmp = input[r];
                System.arraycopy(input, l, input, l + 1, hi - l);
                input[l] = tmp;
                l++;
                r++;
                mid++;
            }
        }
    }

    /**
     * Implements the merge method used in Mergesort.
     *
     * @param lo lowest index of the array
     * @param hi highest index of the array
     */
    private void merge(int lo, int hi)
    {
        int mid = lo + ((hi - lo) / 2);
        int l = lo, r = mid + 1, k = 0;

        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Comparable[hi - lo + 1];

        while (l <= mid && r <= hi)
        {
            if (input[l].compareTo(input[r]) < 0)
                aux[k++] = input[l++];
            else
                aux[k++] = input[r++];
        }
        while (l <= mid)
            aux[k++] = input[l++];
        while (r <= hi)
            aux[k++] = input[r++];
        System.arraycopy(aux, 0, input, lo, hi - lo + 1);
    }

    /**
     * Sorts the array.
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
            int mid = lo + ((hi - lo) / 2);
            if (hi - lo >= threshold)
            {
                Future f = es.submit(new Thread(() -> sort(lo, mid)));
                sort(mid + 1, hi);
                try
                {
                    f.get();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                if (input[mid].compareTo(input[mid + 1]) <= 0)
                    return;
                merge(lo, hi);
            } else if (hi - lo <= 10)
                new InsertionSort<>(input).sort(lo, hi);
            else
            {
                sort(lo, mid);
                sort(mid + 1, hi);
                if (input[mid].compareTo(input[mid + 1]) <= 0)
                    return;
                merge(lo, hi);
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

        MergeSort ms = new MergeSort<>(arr);
        long a = System.nanoTime();
        ms.sort();
        long b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
        System.out.println(ms.check());

        System.out.println(((ThreadPoolExecutor) ms.es).getPoolSize());

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
        ms = new MergeSort<>(arr);
        a = System.nanoTime();
        ms.sort();
        b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
    }
}
