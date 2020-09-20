Coarse Set is a collection of unique elements
maintained as a linked list. The list of nodes
are arranged in ascending order by their key,
which is obtained using `hashCode()`. This
facilitates the search of a item within the
list. When the list is empty, it contains two
sentinel nodes `head` and `tail` with minimum
and maximum key values respectively. These
sentinel nodes are not part of the set.

It uses a common, coarse-grained lock, for all
method calls. This set performs well only when
contention is low. If however, contention is
high, despite the performance of lock, all
methods calls will be essential sequential. The
main advantage of this algorithms is that its
obviously correct.

```java
add():
1. Create new node beforehand.
2. Acquire lock before any action.
3. Find node after which to insert.
4. Add node, only if key is unique.
5. Increment size if node was added.
6. Release the lock.
```

```java
remove():
1. Acquire lock before any action.
2. Find node after which to remove.
3. Remove node, only if key matches.
4. Decrement size if node was removed.
5. Release the lock.
```

```java
contains():
1. Acquire lock before any action.
2. Find node previous to search key.
3. Check if next node matches search key.
4. Release the lock.
```

See [CoarseSet.java] for code, [Main.java] for test, and [repl.it] for output.

[CoarseSet.java]: https://repl.it/@wolfram77/coarse-set#CoarseSet.java
[Main.java]: https://repl.it/@wolfram77/coarse-set#Main.java
[repl.it]: https://coarse-set.wolfram77.repl.run


### references

- [The Art of Multiprocessor Programming :: Maurice Herlihy, Nir Shavit](https://dl.acm.org/doi/book/10.5555/2385452)
