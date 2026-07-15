package com.teste.paperclipai.util;

/**
 * Validação de CPF pelos dígitos verificadores (Receita Federal).
 */
public final class CpfValidator {

    private CpfValidator() {
    }

    /**
     * @param cpf CPF com ou sem máscara.
     * @return true se o CPF é válido.
     */
    public static boolean isValido(String cpf) {
        if (cpf == null) {
            return false;
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            return false;
        }
        // Rejeita sequências repetidas (111.111.111-11 etc.)
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        int dv1 = calcularDigito(digitos, 9, 10);
        int dv2 = calcularDigito(digitos, 10, 11);
        return dv1 == Character.getNumericValue(digitos.charAt(9))
                && dv2 == Character.getNumericValue(digitos.charAt(10));
    }

    /** Remove máscara, mantendo apenas dígitos. */
    public static String normalizar(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    private static int calcularDigito(String digitos, int tamanho, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
