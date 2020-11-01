import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        try {
            File file = new File("in1.txt");
            Scanner scanner = new Scanner(file);
            Lexer lexer = new Lexer();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lexer.process(line);
            }
            lexer.write_ans("out1.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }


    }
}
