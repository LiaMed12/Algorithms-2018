package lesson3;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
@SuppressWarnings("WeakerAccess")
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        T point = (T) o;
        boolean checkContains = contains(o);
        if (checkContains == true) {
            deleteItemInSubtree(root, point);
            size--;
        }
        return true;
    }

    private Node<T> deleteItemInSubtree(Node<T> rootTree, T point) {
        if (size == 1) {
            return root = null;
        }
        int comparisonSearchTree = point.compareTo(rootTree.value);
        if (comparisonSearchTree < 0) {
            rootTree.left = deleteItemInSubtree(rootTree.left, point);
        } else if (comparisonSearchTree > 0) {
            rootTree.right = deleteItemInSubtree(rootTree.right, point);
        } else if ((rootTree.left != null && rootTree.right != null) || rootTree.right != null) {
            rootTree.value = minimumItemInSubtree(rootTree.right).value;
            rootTree.right = deleteItemInSubtree(rootTree.right, rootTree.value);
        } else {
            if (rootTree.left != null) {
                rootTree.value = maximumItemInSubtree(rootTree.left).value;
                rootTree.left = deleteItemInSubtree(rootTree.left, rootTree.value);
            } else {
                rootTree = null;
            }
        }
        return rootTree;
    }
    //Идея для реализации была взята с сайта https://neerc.ifmo.ru/wikiindex.php?title=Дерево_поиска,_наивная_реализация
    // (удаление рекурсивная реализация)
    //Ресурсоемкость:O(h), h - высота дерева
    //Трудоемкость: O(h) - худший случай, O(logN) - в остальных случаях

    private Node<T> minimumItemInSubtree(Node<T> t) {
        if (t.left != null)
            return minimumItemInSubtree(t.left);
        return t;
    }

    private Node<T> maximumItemInSubtree(Node<T> t) {
        if (t.right != null)
            return maximumItemInSubtree(t.right);
        return t;
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    public class BinaryTreeIterator implements Iterator<T> {

        private Node<T> current = null;

        private BinaryTreeIterator() {
        }

        /**
         * Поиск следующего элемента
         * Средняя
         */
        private Node<T> findNext() {
            Node<T> point;
            if (root == null) {
                return null;
            }
            if (current == null) {
                return minimumItemInSubtree(root);
            } else {
                point = current;
            }
            if (point.right != null) {
                return minimumItemInSubtree(point.right);
            } else {
                Node<T> searchPoint = null;
                Node<T> ancestor = root;
                while (ancestor != point && ancestor != null) {
                    int comparison = point.value.compareTo(ancestor.value);
                    if (comparison > 0) {
                        ancestor = ancestor.right;
                    } else {
                        searchPoint = ancestor;
                        ancestor = ancestor.left;
                    }
                }
                return searchPoint;
            }
        }

        //Ресурсоемкость:O(h), h - высота дерева
        //Трудоемкость: O(h) - худший случай, O(logN) - в остальных случаях


        @Override
        public boolean hasNext() {
            return findNext() != null;
        }

        @Override
        public T next() {
            current = findNext();
            if (current == null) throw new NoSuchElementException();
            return current.value;
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        @Override
        public void remove() {
            if (current != null) {
                BinaryTree.this.remove(current.value);
            }
        }
        //Трудоемкость: O(h) - худший случай, O(logN) - в остальных случаях
        //Ресурсоемкость: O(1)
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }


    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
