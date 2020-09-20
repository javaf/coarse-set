import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

// Coarse Set is a collection of unique elements maintained as a linked list. It uses a coarse grained lock, and useful when contention is low.

// Locked Queue uses locks and conditions to block
// when queue is empty, or it is full. Just as
// locks are inherently vulnerable to deadlock,
// Condition objects are inherently vulnerable to
// lost wakeups in which one or more threads wait
// forever without realizing that the condition
// for which they are waiting has become true.
// 
// This queue signals "not empty" whenever an item
// is added to the queue, and "not full" whenever
// an item is removed from the queue. However,
// consider an optimization, where you only signal
// "not empty" if the queue was empty. Bang! Lost
// wakeup is suddenly possible.
// 
// To see how that is possible, consider 2
// consumers A & B and 2 producers C & D. When
// queue is empty and both A & B have to remove(),
// they are blocked until C or D can add(). If C
// add()s, followed by D, only 1 "not empty"
// condition would be active causing C to wakeup,
// but not D.
// 
// Hence, one needs to be careful when working with
// both locks and condition objects.
// 
// The functionality of this queue is similar to
// BlockingQueue and does not suffer from the lost
// wakeup problem.

class CoarseSet<T> extends AbstractSet<T> {
  final Lock lock;
  final AtomicInteger size;
  final Node<T> head;
  // lock: common (coarse) lock for set
  // size: number of items in set
  // head: points to begin of nodes in set

  public CoarseSet() {
    lock = new ReentrantLock();
    size = new AtomicInteger(0);
    head = new Node<>(null, Integer.MIN_VALUE);
    head.next = new Node<>(null, Integer.MAX_VALUE);
  }

  // 1. Create new node beforehand.
  // 2. Acquire lock before any action.
  // 3. Find node after which to insert.
  // 4. Add node, only if key is unique.
  // 5. Increment size if node was added.
  // 6. Release the lock.
  @Override
  public boolean add(T v) {
    Node<T> x = new Node<>(v);    // 1
    lock.lock();                  // 2
    Node<T> p = findNode(x.key);  // 3
    boolean done = addNode(p, x); // 4
    if (done) size.incrementAndGet(); // 5
    lock.unlock(); // 6
    return done;
  }

  // 1. Acquire lock before any action.
  // 2. Find node after which to remove.
  // 3. Remove node, only if key matches.
  // 4. Decrement size if node was removed.
  // 5. Release the lock.
  @Override
  public boolean remove(Object v) {
    int k = v.hashCode();
    lock.lock(); // 1
    Node<T> p = findNode(k);          // 2
    boolean done = removeNode(p, k);  // 3
    if (done) size.decrementAndGet(); // 4
    lock.unlock(); // 5
    return done;
  }

  private boolean addNode(Node<T> p, Node<T> x) {
    Node<T> q = p.next;
    if (q.key == x.key) return false;
    x.next = q;
    p.next = x;
    return true;
  }

  private boolean removeNode(Node<T> p, int k) {
    Node<T> q = p.next;
    if (q.key != k) return false;
    p.next = q.next;
    return true;
  }

  private Node<T> findNode(int k) {
    Node<T> p = head;
    while (p.next.key < k)
      p = p.next;
    return p;
  }

  @Override
  public Iterator<T> iterator() {
    Collection<T> a = new ArrayList<>();
    Node<T> p = head.next;
    while (p.next != null)
      a.add(p.value);
    return a.iterator();
  }

  @Override
  public int size() {
    return size.get();
  }
}
