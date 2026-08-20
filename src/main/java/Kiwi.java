import java.util.Scanner;

public class Kiwi {
    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _  ___          _ \n"
            + "| |/ (_)_      _(_)\n"
            + "| ' /| \\ \\ /\\ / / |\n"
            + "| . \\| |\\ V  V /| |\n"
            + "|_|\\_\\_| \\_/\\_/ |_|\n";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println(LINE);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Kiwi.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
        String input = in.nextLine();
        while (!input.equals("bye")) {
            System.out.println(LINE);
            System.out.println(input);
            System.out.println(LINE);
            input = in.nextLine();
        }
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
        return;
    }
}
