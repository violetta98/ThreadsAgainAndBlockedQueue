package SynchronizedAgain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Violetta on 2017-08-21.
 */

class A implements Callable<String> {

    @Override
    public String call() throws Exception {
        Thread.sleep(1000);
        return Thread.currentThread().getName();
    }
}

public class CallableFuture {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10); // получаем ExecutorService утилитного класса Executors с размером пула потоков 10
        List<Future<String>> list = new ArrayList<>(); // создаем список с Future, которые ассоцированы с Callable
        Callable<String> callable = new A(); // создаем экземпляр A
        for (int i = 0; i < 50; i++) {
            Future<String> future = executor.submit(callable); // сабмитим таски, которые будут выполнены пулом потоков
            list.add(future); // и добавляем их в Future список
        }
        for (Future<String> future : list) {
            try {
                System.out.println(new Date() + "::" + future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }
}
