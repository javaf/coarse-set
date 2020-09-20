import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

// Coarse Set is a collection of unique elements
// maintained as a linked list. The list of nodes
// are arranged in ascending order by their key,
// which is obtained using `hashCode()`. This
// facilitates the search of a item within the
// list. When the list is empty, it contains two
// sentinel nodes `head` and `tail` with minimum
// and maximum key values respectively. These
// sentinel nodes are not part of the set.
// 
// It uses a common, coarse-grained lock, for all
// method calls. This set performs well only when
// contention is low. If however, contention is
// high, despite the performance of lock, all
// methods calls will be essential sequential. The
// main advantage of this algorithms is that its
// obviously correct.

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

  // 1. Acquire lock before any action.
  // 2. Find node previous to search key.
  // 3. Check if next node matches search key.
  // 4. Release the lock.
  @Override
  public boolean contains(Object v) {
    int k = v.hashCode();
    lock.lock(); // 1
    Node<T> p = findNode(k);       // 2
    boolean has = p.next.key == k; // 3
    lock.unlock(); // 4
    return has;
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
    while (p.next != null) {
      a.add(p.value);
      p = p.next;
    }
    return a.iterator();
  }

  @Override
  public int size() {
    return size.get();
  }
}
