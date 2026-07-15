package com.teste.paperclipai.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfValidatorTest {

    @Test
    void cpfValidoSemMascara() {
        assertTrue(CpfValidator.isValido("52998224725"));
    }

    @Test
    void cpfValidoComMascara() {
        assertTrue(CpfValidator.isValido("529.982.247-25"));
    }

    @Test
    void cpfComDigitoVerificadorErrado() {
        assertFalse(CpfValidator.isValido("52998224724"));
    }

    @Test
    void cpfComSequenciaRepetida() {
        assertFalse(CpfValidator.isValido("11111111111"));
    }

    @Test
    void cpfNuloOuCurto() {
        assertFalse(CpfValidator.isValido(null));
        assertFalse(CpfValidator.isValido("123"));
    }

    @Test
    void normalizarRemoveMascara() {
        assertEquals("52998224725", CpfValidator.normalizar("529.982.247-25"));
    }
}
