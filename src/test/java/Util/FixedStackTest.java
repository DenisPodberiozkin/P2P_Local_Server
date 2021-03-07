package Util;

import org.junit.jupiter.api.Test;

class FixedStackTest {

    @Test
    public void test() {
        FixedStack<Integer> fixedStack = new FixedStack<>(5);
        fixedStack.push(1);
        fixedStack.push(2);
        fixedStack.push(3);
        fixedStack.push(4);
        fixedStack.push(5);
        fixedStack.push(6);
        fixedStack.push(7);
        fixedStack.push(8);
        fixedStack.push(9);

        for (int i = 0; i < fixedStack.size(); i++) {
            System.out.println(fixedStack.pop());
        }
    }

}