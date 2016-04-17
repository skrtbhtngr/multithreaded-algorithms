/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public final class Quicksort<T extends Comparable<T>>
{
    private Random rnd;
    private final int threshold;
    private ExecutorService es;
    private final T[] input;
    public Quicksort(T[] input)
    {
        rnd = new Random();
        this.input=input;
        threshold = input.length/Runtime.getRuntime().availableProcessors();
    }
    private void swap(int i, int j)
    {
        T temp=input[i];
        input[i]=input[j];
        input[j]=temp;
    }
    private int partition(int lo, int hi)
    {
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
    public void sort()
    {
        es = Executors.newCachedThreadPool();
        sort(0,input.length-1);
        es.shutdownNow();
    }
    public void sort(int lo, int hi)
    {
        if(hi-lo>=1)
        {
            if (hi - lo >= threshold)
            {
                int p = partition(lo, hi);
                Future f = es.submit(new Thread(){
                    public void run()
                    {
                        sort(lo,p-1);
                    }
                });
                sort(p+1, hi);
                try
                {
                    f.get();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (hi-lo <= 10)
                new InsertionSort<>(input).sort(lo, hi);
            else
            {
                int p = partition(lo, hi);
                sort(lo, p-1);
                sort(p+1, hi);
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

        Quicksort qs = new Quicksort<>(arr);
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
        qs = new Quicksort<>(arr);
        a = System.nanoTime();
        qs.sort();
        b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
    }
}
