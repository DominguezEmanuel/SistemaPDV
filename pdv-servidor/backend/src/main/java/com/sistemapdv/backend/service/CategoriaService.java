package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.CategoriaDTO;
import com.sistemapdv.backend.entity.Categoria;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.CategoriaMapper;
import com.sistemapdv.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public List<CategoriaDTO> findAllCategories(){
        List<Categoria> categorias = categoriaRepository.findAll();
        List<CategoriaDTO> listadoCategorias = new ArrayList<CategoriaDTO>();
        for (Categoria c: categorias){
            listadoCategorias.add(categoriaMapper.toCategoriaDTO(c));
        }
        return listadoCategorias;
    }

    public CategoriaDTO findById(Integer id){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria no encontrada"));
        return categoriaMapper.toCategoriaDTO(categoria);
    }

    public CategoriaDTO addCategory(CategoriaDTO request){
        if(categoriaRepository.existsByNombre(request.getNombre()))
            throw new ResourceDuplicatedException("El nombre de categoria ya existe");

        Categoria categoria = categoriaMapper.toCategoria(request);

        Categoria categoriaCreada = categoriaRepository.save(categoria);

        return categoriaMapper.toCategoriaDTO(categoriaCreada);
    }
}
