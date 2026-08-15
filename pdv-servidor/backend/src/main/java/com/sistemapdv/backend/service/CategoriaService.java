package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.CategoriaDTO;
import com.sistemapdv.backend.entity.Categoria;
import com.sistemapdv.backend.exception.ResourceDuplicatedException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.CategoriaMapper;
import com.sistemapdv.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAllCategories(){
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(categoriaMapper::toCategoriaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDTO findById(Integer id){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Categoria con ID " +
                        id + " no encontrada"));
        return categoriaMapper.toCategoriaDTO(categoria);
    }

    @Transactional
    public CategoriaDTO createCategory(CategoriaDTO request){
        if(categoriaRepository.existsByNombreIgnoreCase(request.getNombre()))
            throw new ResourceDuplicatedException("El nombre " + request.getNombre() +
                    " ya se encuentra registrado");

        Categoria categoria = categoriaMapper.toCategoria(request);

        categoriaRepository.save(categoria);

        return categoriaMapper.toCategoriaDTO(categoria);
    }
}
