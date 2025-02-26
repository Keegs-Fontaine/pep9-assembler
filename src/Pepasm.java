import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Pepasm {
    public static void main(String[] args) {
        String filename = args[0];
        System.out.println(filename);

        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
