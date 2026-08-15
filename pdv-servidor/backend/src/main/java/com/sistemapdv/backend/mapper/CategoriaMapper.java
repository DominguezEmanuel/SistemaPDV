package com.sistemapdv.backend.mapper;

import com.sistemapdv.backend.dto.CategoriaDTO;
import com.sistemapdv.backend.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaDTO toCategoriaDTO(Categoria categoria){
        CategoriaDTO categoriaDTO = new CategoriaDTO();

        categoriaDTO.setIdCategoria(categoria.getIdCategoria());
        categoriaDTO.setNombre(categoria.getNombre());

        return categoriaDTO;
    }

    public Categoria toCategoria(CategoriaDTO dto){
        Categoria categoria = new Categoria();

        categoria.setNombre(dto.getNombre().trim());

        return categoria;
    }
}
