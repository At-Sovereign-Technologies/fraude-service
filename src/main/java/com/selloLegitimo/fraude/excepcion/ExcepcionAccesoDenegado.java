package com.selloLegitimo.fraude.excepcion;

public class ExcepcionAccesoDenegado extends RuntimeException {

    public ExcepcionAccesoDenegado(String message) {
        super(message);
    }
}
