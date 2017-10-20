import java.util.Arrays;
import java.util.Random;

/**
 * This class is a
 *
 * @author Sukrit Bhatnagar
 * @version 1.0
 */
public class SortTest
{
    private static final int INPUT_SIZE = 10000000;

    public static void main(String[] args) throws InterruptedException
    {
        Integer[] arr = new Integer[INPUT_SIZE];
        Random rnd = new Random();

        MergeSort ms;
        QuickSort qs;
        Integer[] input = new Integer[INPUT_SIZE];
        long a, b;
        int t;
        //Random values
        for (t = 1; t <= 10; t++)
        {
            rnd.setSeed(System.nanoTime());
            for (int i = 0; i < arr.length; i++)
                arr[i] = rnd.nextInt(Integer.MAX_VALUE);

            System.arraycopy(arr, 0, input, 0, arr.length);
            a = System.nanoTime();
            Arrays.sort(input);
            b = System.nanoTime();
            System.out.print(((b - a) / 1E9) + "\t");

            System.arraycopy(arr, 0, input, 0, arr.length);
            a = System.nanoTime();
            Arrays.parallelSort(input);
            b = System.nanoTime();
            System.out.print(((b - a) / 1E9) + "\t");

            System.arraycopy(arr, 0, input, 0, arr.length);
            ms = new MergeSort<>(input);
            a = System.nanoTime();
            ms.sort();
            b = System.nanoTime();
            System.out.print(((b - a) / 1E9) + "\t");

            System.arraycopy(arr, 0, input, 0, arr.length);
            qs = new QuickSort<>(input);
            a = System.nanoTime();
            qs.sort();
            b = System.nanoTime();
            System.out.println((b - a) / 1E9);
        }
    }
}
