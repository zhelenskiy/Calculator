import java.util.Scanner;

import ParsingPackage.ExpressionParser;

public class Main {
    public static void main(String[] args) {
        java.util.Scanner in = new Scanner(System.in);
        while (true) {
            try {
                String expression = in.nextLine();
                int x = 0, y = 0, z = 0;
                System.out.print("x: ");
                String data = in.nextLine();
                if (data.length() > 0) {
                    x = Integer.valueOf(data);
                    System.out.print("y: ");
                    data = in.nextLine();
                    if (data.length() > 0) {
                        y = Integer.valueOf(data);
                        System.out.print("z: ");
                        data = in.nextLine();
                        z = data.length() > 0 ? Integer.valueOf(data) : 0;
                    }
                }

                System.out.println((new ExpressionParser()).parse(expression).evaluate(x, y, z));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                System.out.println("\n");
            }
        }
    }
}
