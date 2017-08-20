package SynchronizedAgain;

/**
 * Created by Violetta on 2017-08-20.
 */
class Counter {

    private int c = 0;

    public synchronized void inc() { // надо писать synchronized, т.к. в этом методе операции не атомарные
        c++;                         // и если этого не написать наш код приводит к непредсказуемым результатам
        System.out.println( c == 1 ? "Yes" : "No");
        c--;
    }

    public int getC() { return c; }
}

public class SynchronizedAgain {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> counter.inc());
        Thread t2 = new Thread(() -> counter.inc());
        t1.start();
        t2.start();
    }
}
