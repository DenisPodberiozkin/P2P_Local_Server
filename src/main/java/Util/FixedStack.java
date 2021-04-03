package Util;


/**
 * Idea from
 *
 * @author Martijn Courteaux (2017)
 * From https://stackoverflow.com/questions/7727919/creating-a-fixed-size-stack
 */
public class FixedStack<T> {
    private final T[] stack;
    private final int size;
    private int top;

    public FixedStack(int size) {
        this.stack = (T[]) new Object[size];
        this.top = -1;
        this.size = size;
    }

    public void push(T obj) {
        if (top >= size - 1) {
            if (stack.length - 1 >= 0) System.arraycopy(stack, 1, stack, 0, stack.length - 1);
            top--;
        }
        stack[++top] = obj;
    }

    public T pop() {
        if (top < 0) return null;
        T obj = stack[top--];
        stack[top + 1] = null;
        return obj;
    }

    public T peek() {
        if (top < 0) return null;
        return stack[top];
    }

    public int size() {
        return size;
    }

}