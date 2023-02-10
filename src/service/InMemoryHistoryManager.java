package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager{
    private CustomLinkedList<Task> taskHistory = new CustomLinkedList<>();

    private HashMap<Integer, Node<Task>> historyHash = new HashMap<>();

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory.getTasks();
    }

    @Override
    public void addTask(Task task) {
        if (historyHash.containsKey(task.getId())) {
            taskHistory.removeNode(historyHash.get(task.getId()));
            historyHash.remove(historyHash.get(task.getId()));
        }
        taskHistory.linkLast(task);
        historyHash.put(task.getId(), taskHistory.tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyHash.get(id);
        taskHistory.removeNode(node);
    }

    class CustomLinkedList<T> extends LinkedList<T> {

        private Node<T> head;
        private Node<T> tail;


        @Override
        public void addLast(T element) {

        }
        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }

        public void removeNode(Node<T> node) {
            if (node.prev == null) {
                head = node.next;
            } else {
                node.prev.next = node.next;
            }

            if (node.next == null) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> historyArrayList = new ArrayList<>();
            if (head != null) {
                historyArrayList.add(head.data);
            } else {
                return historyArrayList;
            }
            Node<T> headNext = head.next;
            while(headNext != null) {
                historyArrayList.add(headNext.data);
                headNext = headNext.next;
            }
            return historyArrayList;
        }
    }
}
