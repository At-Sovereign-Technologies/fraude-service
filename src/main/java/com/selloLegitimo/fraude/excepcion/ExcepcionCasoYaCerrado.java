package com.selloLegitimo.fraude.excepcion;

public class ExcepcionCasoYaCerrado extends RuntimeException {

    public ExcepcionCasoYaCerrado(String message) {
        super(message);
    }
}
