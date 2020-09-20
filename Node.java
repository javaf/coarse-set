class Node<T> {
  T value;
  int key;
  Node<T> next;

  public Node(T v, int k) {
    value = v;
    key = k;
  }
  public Node(T v) {
    this(v, v.hashCode());
  }
}
