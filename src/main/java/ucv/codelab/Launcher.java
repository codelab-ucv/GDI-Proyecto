package ucv.codelab;

/**
 * Launcher class para evitar problemas de JavaFX con JAR ejecutable
 * Esta clase NO extiende Application, evitando el error de runtime
 */
public class Launcher {
    public static void main(String[] args) {
        // Simplemente llamamos al Main original
        Main.main(args);
    }
}