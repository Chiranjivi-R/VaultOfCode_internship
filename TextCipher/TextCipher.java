import java.util.Scanner;

public class TextCipher {

    // Method that performs both encoding and decoding using a shift value
    public static String shiftText(String text, int shift) {
        StringBuilder result = new StringBuilder();

        shift = shift % 26;  // Limit shift to 0–25 range

        for (char ch : text.toCharArray()) {

            // Check if character is uppercase (A–Z)
            if (Character.isUpperCase(ch)) {
                char base = 'A';
                int newPos = (ch - base + shift + 26) % 26;  // Wrap-around logic
                result.append((char) (base + newPos));
            } 

            // Check if character is lowercase (a–z)
            else if (Character.isLowerCase(ch)) {
                char base = 'a';
                int newPos = (ch - base + shift + 26) % 26;  // Wrap-around logic
                result.append((char) (base + newPos));
            } 

            // For numbers, spaces, punctuation — keep as it is
            else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // User gives a shift value (can be positive or negative)
        System.out.print("Enter shift value: ");
        int shift = sc.nextInt();
        sc.nextLine(); // Clear buffer

        // Menu-driven program
        while (true) {
            System.out.println("\n1. Encode");
            System.out.println("2. Decode");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Clear buffer

            // Exit choice
            if (choice == 3) {
                System.out.println("Goodbye!");
                break;
            }

            // Read message from user
            System.out.print("Enter message: ");
            String msg = sc.nextLine();

            // Option 1: Encode using positive shift
            if (choice == 1) {
                System.out.println("Encoded: " + shiftText(msg, shift));
            } 

            // Option 2: Decode using negative shift
            else if (choice == 2) {
                System.out.println("Decoded: " + shiftText(msg, -shift));
            } 

            // Invalid menu option
            else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();  // Close scanner
    }
}
