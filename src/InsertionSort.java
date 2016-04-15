import java.util.Arrays;
import java.util.Random;

public class InsertionSort<T extends Comparable<T>>
{
    private T[] input;
    public InsertionSort(T[] input)
    {
        this.input=input;
    }
    public void sort(int lo, int hi)
    {
        T x;
        int i, j;
        for(i=lo+1;i<=hi;i++)
        {
            x=input[i];
            j=i-1;
            while(j>=lo && input[j].compareTo(x)>0)
            {
                input[j+1]=input[j];
                j--;
            }
            input[j+1]=x;
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
        Integer[] arr = new Integer[1000]; //Works for a 100 million numbers!
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        for(int i=0;i<arr.length;i++)
            arr[i]=rnd.nextInt(1000000);

        InsertionSort is = new InsertionSort<>(arr);
        System.out.println(Arrays.toString(is.input));
        long a = System.nanoTime();
        is.sort(0,arr.length-1);
        long b = System.nanoTime();
        System.out.println("Time taken: "+(b-a)/1E9);
        System.out.println(is.check());
        System.out.println(Arrays.toString(is.input));
    }
}
