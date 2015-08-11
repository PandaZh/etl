package cc.changic.platform.etl.schedule.producer;

/**
 * @author Panda.Z
 */
public class Test {

    public static void main(String[] args) {
        try {
            test1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test1() {
        System.out.println("test1");
        test2();
    }

    public static void test2() {
        System.out.println("test2");
        test3();
    }

    public static void test3() {
        System.out.println("test3");
        test4();
    }

    public static void test4() {
        System.out.println("test4");
        test5();
    }

    public static void test5() {
        throw new RuntimeException("test5");
    }
}
