import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FibonacciSeries
{
    static int processors;
    static ExecutorService es;
    static
    {
        es = Executors.newCachedThreadPool();
        processors = Runtime.getRuntime().availableProcessors();
    }
    public static long fibonacci(long n)
    {
        if(n<=1)
            return n;
        else if(n<=100000)
            return fibonacciSerial(n);
        else
        {
            final long[] x = new long[1];
            class FibonacciSeriesThread implements Runnable
            {
                long n;
                public FibonacciSeriesThread(long n)
                {
                    this.n=n;
                }
                public void run()
                {
                    x[0] =FibonacciSeries.fibonacci(n);
                }
            }
            Thread t = new Thread(new FibonacciSeriesThread(n-1));
            t.start();
            long y=fibonacci(n-2);
            try
            {
                t.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return x[0] +y;
        }
    }
    public static long fibonacciSerial(long n)
    {
        if(n<=1)
            return n;
        else
        {
            return fibonacciSerial(n-1)+fibonacciSerial(n-2);
        }
    }
    public static void main(String[] args)
    {
        long n=(long)Math.sqrt(System.currentTimeMillis());
        System.out.println("n: "+n);
        long a=System.nanoTime();
        long f=fibonacci(n);
        long b=System.nanoTime();
        System.out.println("fibonacci(n)"+f);
        System.out.println("Time taken: "+(b-a)/1E9);

        a=System.nanoTime();
        f=fibonacci(n);
        b=System.nanoTime();
        System.out.println("fibonacci(n)"+f);
        System.out.println("Time taken: "+(b-a)/1E9);
    }
}
