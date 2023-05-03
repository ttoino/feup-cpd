package pt.up.fe.cpd.proj2.common;

import java.util.Arrays;
import java.util.Scanner;

public final class Input {
    private static final Scanner scanner = new Scanner(System.in);

    public static String get(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (input.length() > 0)
                return input;

            System.err.println("Invalid input");
        }
    }

    public static String get(String prompt) {
        return get(scanner, prompt);
    }

    public static String getFromOptions(Scanner scanner, String prompt, String ...options) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();

            if (Arrays.asList(options).contains(input))
                return input;

            System.err.println("Invalid input");
        }
    }

    public static String getFromOptions(String prompt, String ...options) {
        return getFromOptions(scanner, prompt, options);
    }

    public static int getInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
            }
        }
    }

    public static int getInt(String prompt) {
        return getInt(scanner, prompt);
    }

    public static int getInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max)
                    return value;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
            }
        }
    }

    public static int getInt(String prompt, int min, int max) {
        return getInt(scanner, prompt, min, max);
    }

    public static double getDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
            }
        }
    }

    public static double getDouble(String prompt) {
        return getDouble(scanner, prompt);
    }

    public static double getDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max)
                    return value;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input");
            }
        }
    }

    public static double getDouble(String prompt, double min, double max) {
        return getDouble(scanner, prompt, min, max);
    }

    public static boolean getBoolean(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("y") || input.equals("yes"))
                return true;
            else if (input.equals("n") || input.equals("no"))
                return false;

            System.err.println("Invalid input");
        }
    }

    public static boolean getBoolean(String prompt) {
        return getBoolean(scanner, prompt);
    }
}
