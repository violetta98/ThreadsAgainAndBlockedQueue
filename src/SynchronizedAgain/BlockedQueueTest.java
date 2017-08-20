package SynchronizedAgain;

import java.util.LinkedList;

/**
 * Created by Violetta on 2017-08-20.
 */

/**
 * Блокирующая очередь блокирует поток если она пустая (а мы хотим взять элемент) или если нет места положить
 * новый элемент (а мы хотим положить), а как только эта ситуация меняется - потоки просыпаются и все работает
 * как в обычной очереди.
 * @param <T>
 */

class BlockedQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxSize = 1; // для простоты

    public T poll() {
        synchronized (queue) { // заходит поток и блокирует лок/монитор queue если лок открыт, если нет, то он засыпает и ждет когда он будет открыт
            while (queue.isEmpty()) { // и смотрит пуста ли очередь, если да то он засыпает и отпускает монитор queue
                try {  // тут обязательно нужен цикл
                    System.out.println("Thread in poll() is gonna wait! (From poll)");
                    queue.wait(); // дает указание монитору queue перевести наш thread в режим ожидания и при этом открывается наш лок queue
                    // когда сюда приходит поток (после notifyAll) эта операция является не атомарной
                    // и может произойти ситуация, когда данные уже кто-то перед нашим потоком стоит и ждет (а мы вторые)
                    // и он уже считал наши данные и мы с нашим вторым потоком пытаемся второй раз вынять данные из очереди
                    // поэтому тут необходимо писать while, а не if
                    System.out.println("Thread in poll() finished waiting! (From poll)");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T data = queue.remove();
            System.out.println("All threads, which wait is gonna wake up! (From poll)");
            queue.notify();
            System.out.println("notifyAll! (From poll)");
            return data;
        }
    }

    public void push(final T elem) { // в это время сюда заходит другой поток
        synchronized (queue) { // этот поток блокирует лок queue если лок открыт, если нет, то он засыпает и ждет когда он будет открыт
            while (queue.size() == maxSize) {
                try {
                    System.out.println("Thread in push() is gonna wait! (From push)");
                    queue.wait(); // дает указание монитору queue перевести наш thread в режим ожидания и при этом открывается наш лок queue
                    // когда сюда приходит поток (после notifyAll) эта операция является не атомарной
                    // и может произойти ситуация, когда кто-то перед нашим потоком стоит и ждет (а мы вторые)
                    // и он уже добавил элемент в очередь и мы пытаемся с нашим вторым потоком второй раз добавить
                    // элемент в очередь, поэтому тут необходимо писать while, а не if
                    System.out.println("Thread in push() finished waiting! (From push)");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.add(elem);
            System.out.println("All threads, which wait is gonna wake up! (From push)");
            queue.notifyAll(); // дает указание монитору queue, что все потоки, что ждали появления новых событий должны проснуться
            System.out.println("notifyAll! (From push)");
        }  // когда мы выйдем из этого блока synchronized, у нас произойдет возобновление того потока, что ждал в методе poll
    }

}

public class BlockedQueueTest {
    public static void main(String[] args) {
        BlockedQueue<Integer> queue = new BlockedQueue<Integer>();
        Thread t1 = new Thread(() -> queue.poll());
        Thread t2 = new Thread(() -> queue.push(43));
        Thread t3 = new Thread(() -> queue.push(497));
        t3.start();
        t2.start();
        t1.start();
    }
}
