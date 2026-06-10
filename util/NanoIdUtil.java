package util;

import java.security.SecureRandom;

/**
 * Utilitário para geração de códigos NanoID alfanuméricos de 10 caracteres.
 * Padrão: letras minúsculas, maiúsculas e números (62 caracteres possíveis)
 */
public class NanoIdUtil {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_SIZE = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Gera um NanoID padrão de 10 caracteres.
     * @return código alfanumérico único
     */
    public static String generate() {
        return generate(DEFAULT_SIZE);
    }

    /**
     * Gera um NanoID com tamanho personalizado.
     * @param size quantidade de caracteres (mínimo 1)
     * @return código alfanumérico único
     */
    public static String generate(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Tamanho deve ser pelo menos 1");
        }

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}
