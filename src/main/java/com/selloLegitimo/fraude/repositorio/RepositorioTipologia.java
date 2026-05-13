package com.selloLegitimo.fraude.repositorio;

import com.selloLegitimo.fraude.modelo.Tipologia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioTipologia extends JpaRepository<Tipologia, String> {
}
