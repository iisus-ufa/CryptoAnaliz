
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;

public class CaesarCipherApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Шифр Цезаря");
        System.out.println("1. Зашифровать файл");
        System.out.println("2. Расшифровать файл");
        System.out.print("Выберите действие (1 или 2): ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            if (choice != 1 && choice != 2) {
                System.out.println("Ошибка: нужно ввести 1 или 2");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число 1 или 2");
            return;
        }

        try {
            System.out.print("Введите путь к исходному файлу: ");
            String inputFile = scanner.nextLine();

            System.out.print("Введите путь для сохранения результата: ");
            String outputFile = scanner.nextLine();

            System.out.print("Введите ключ (число от 0 до " + (CipherEngine.ALPHABET.length() - 1) + "): ");
            int key = Integer.parseInt(scanner.nextLine());

            Command command = new Command(
                    choice == 1 ? "encrypt" : "decrypt",
                    inputFile,
                    outputFile,
                    key
            );

            CipherProcessor processor = new CipherProcessor();
            processor.process(command);
            System.out.println("Операция выполнена успешно. Результат записан в " + command.outputFile());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом");
        } catch (IllegalArgumentException | IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

class Command {
    private final String mode;
    private final String inputFile;
    private final String outputFile;
    private final int key;

    public Command(String mode, String inputFile, String outputFile, int key) {
        this.mode = mode;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.key = key;
    }

    public String mode() { return mode; }
    public String inputFile() { return inputFile; }
    public String outputFile() { return outputFile; }
    public int key() { return key; }
}

class CipherProcessor {
    public void process(Command command) throws IOException {
        String text = Files.readString(Paths.get(command.inputFile()), StandardCharsets.UTF_8);
        String result;

        if ("encrypt".equals(command.mode())) {
            result = CipherEngine.encrypt(text, command.key());
        } else {
            result = CipherEngine.decrypt(text, command.key());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(command.outputFile()))) {
            writer.write(result);
        }
    }
}

class CipherEngine {
    public static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encrypt(String text, int key) {
        return processText(text, key);
    }

    public static String decrypt(String text, int key) {
        return processText(text, -key);
    }

    private static String processText(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index != -1) {
                int newIndex = (index + shift) % ALPHABET.length();
                if (newIndex < 0) {
                    newIndex += ALPHABET.length();
                }
                result.append(ALPHABET.charAt(newIndex));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}