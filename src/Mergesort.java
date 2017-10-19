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

public final class Mergesort<T extends Comparable<T>> {
    private final int threshold;
    private ExecutorService es;
    private final T[] input;

    public Mergesort(T[] input) {
        this.input = input;
        threshold = (input.length / Runtime.getRuntime().availableProcessors()) / 2;
    }

    private void inPlaceMerge(int lo, int mid, int hi) {
        int l = lo, r = mid + 1;
        T tmp;
        if (input[mid].compareTo(input[r]) <= 0)
            return;
        while (l <= mid && r <= hi) {
            if (input[l].compareTo(input[r]) <= 0)
                l++;
            else {
                tmp = input[r];
                System.arraycopy(input, l, input, l + 1, hi - l);
                input[l] = tmp;
                l++;
                r++;
                mid++;
            }
        }
    }

    private void merge(int lo, int mid, int hi) {
        int l = lo, r = mid + 1, k = 0;
        T[] aux = (T[]) new Comparable[hi - lo + 1];
        while (l <= mid && r <= hi) {
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

    public void sort() {
        es = Executors.newCachedThreadPool();
        sort(0, input.length - 1);
        es.shutdownNow();
    }

    public void sort(int lo, int hi) {
        if (hi - lo >= 1) {
            int mid = lo + ((hi - lo) >> 1);
            if (hi - lo >= threshold) {
                Future f = es.submit(new Thread() {
                    public void run() {
                        sort(lo, mid);
                    }
                });
                sort(mid + 1, hi);
                try {
                    f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (input[mid].compareTo(input[mid + 1]) <= 0)
                    return;
                merge(lo, mid, hi);
            } else if (hi - lo <= 10)
                new InsertionSort<>(input).sort(lo, hi);
            else {
                sort(lo, mid);
                sort(mid + 1, hi);
                if (input[mid].compareTo(input[mid + 1]) <= 0)
                    return;
                merge(lo, mid, hi);
            }
        }
    }

    public boolean check() {
        for (int i = 0; i < input.length - 1; i++)
            if (input[i].compareTo(input[i + 1]) > 0)
                return false;
        return true;
    }

    public T[] getResults() {
        return input;
    }

    @Override
    public String toString() {
        return Arrays.toString(input);
    }

    public static void main(String[] args) {
        Integer[] arr = new Integer[10000000];
        Random rnd = new Random();
        rnd.setSeed(System.nanoTime());
        for (int i = 0; i < arr.length; i++)
            arr[i] = rnd.nextInt(1000000);

        Mergesort ms = new Mergesort<>(arr);
        long a = System.nanoTime();
        ms.sort();
        long b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
        System.out.println(ms.check());
        //System.out.println(ms);

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
        ms = new Mergesort<>(arr);
        a = System.nanoTime();
        ms.sort();
        b = System.nanoTime();
        System.out.println("Time taken: " + (b - a) / 1E9);
    }
}
