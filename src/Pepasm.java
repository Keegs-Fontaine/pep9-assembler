import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pepasm {
    public static void addInstToHashMap(HashMap<String, String> map, String mnemonic, String opcode, String addrModes) {
        if (Objects.equals(addrModes, "")) {
            // handles instructions that do not have an addressing mode (STOP, ASLA, ASRA, etc)
            map.put(mnemonic, opcode);
        } else {
            // If there are addressing modes specified, the mnemonic will be added to the map with each addressing mode
            // immediate addressing mode is a "0", direct is "1". These values are concatenated with the opcode or hex number specified
            if (addrModes.contains("i")) {
                map.put(mnemonic+"i", opcode + "0");
            }
            if (addrModes.contains("d")) {
                map.put(mnemonic+"d", opcode + "1");
            }
        }
    }

    public static HashMap<String, String> getInstructionMap() {
        HashMap<String, String> instructionMap = new HashMap<>();

        addInstToHashMap(instructionMap, "STBA", "F", "d");
        addInstToHashMap(instructionMap, "LDBA", "D", "id");
        addInstToHashMap(instructionMap, "STWA", "E", "d");
        addInstToHashMap(instructionMap, "LDWA", "C", "id");
        addInstToHashMap(instructionMap, "ADDA", "6", "id");
        addInstToHashMap(instructionMap, "ASLA", "0A", "");
        addInstToHashMap(instructionMap, "ASRA", "0C", "");
        addInstToHashMap(instructionMap, "STOP", "00", "");
        addInstToHashMap(instructionMap, "CPBA", "B", "id");
        addInstToHashMap(instructionMap, "BRNE", "1A", "");

        return instructionMap;
    }

    public static void printAssembledInstruction(String line, HashMap<String, String> instructionMap) {
        // regex to capture all "words" in a line, ignores whitespace and commas
        String regex = "[^,\\s]+";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(line);
        ArrayList<String> groups = new ArrayList<>();
        while (matcher.find()) groups.add(matcher.group());

        // if no regex matches are found, do nothing
        if (groups.isEmpty()) return;

        // for instructions like STOP, ASLA, ASRA, etc.
        else if (groups.size() == 1) {
            String opcode = instructionMap.get(groups.getFirst());
            if (opcode != null) System.out.printf("%s", instructionMap.get(groups.getFirst()));
        } else {
            // gets the hex number without the "0x" prefix, with a space in between (Ex: 0xFC16 -> FC 16)
            String operand = groups.get(1)
                    .substring(2,4) + " " + groups.get(1).substring(4);

            // for instructions that have a mnemonic and operand only
            if (groups.size() == 2) {
                System.out.printf("%s %s", instructionMap.get(groups.getFirst()), operand);
            }
            // for instructions that have a mnemonic, operand, and addressing mode
            else if (groups.size() == 3) {
                System.out.printf("%s %s", instructionMap.get(groups.getFirst() + groups.getLast()), operand);
            }
        }

        System.out.println();
    }

    public static String stripCommentFromLine(String line) {
        // checks for comment, strips it from the line if there is one
        int semicolonIndex = line.indexOf(';');
        if (semicolonIndex != -1) line = line.substring(0, semicolonIndex - 1);
        return line;
    }

    public static void main(String[] args) {
        String filename = args[0];
        HashMap<String, String> instructionMap = getInstructionMap();

        try {
            File myObj = new File(filename);
            Scanner scanner = new Scanner(myObj);

            while (scanner.hasNextLine()) {
                String line = stripCommentFromLine(scanner.nextLine().trim());
                printAssembledInstruction(line, instructionMap);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred!\n" + e.getMessage());
        }
    }
}
