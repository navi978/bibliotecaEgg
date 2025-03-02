package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.EditorialRepositorio;

@Service
public class EditorialServicio {
    
    @Autowired
    private EditorialRepositorio editorialRespositorio;
    
    @Transactional
    public void crearEditorial(String nombre) throws MiException{
        try {
        validar(nombre);
        Editorial editorial = new Editorial();
        editorial.setNombre(nombre);
        editorialRespositorio.save(editorial);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    private void validar(String nombre) throws MiException {
        if (nombre.isEmpty() || nombre==null){
            throw new MiException("El nombre de la editorial no puede ser nulo o estar vacío");
        }
    }

    @Transactional
    public void modificarEditorial(String nombre, String id) throws MiException{
        validar(nombre);
        Optional<Editorial> respuesta = editorialRespositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setNombre(nombre);
            editorialRespositorio.save(editorial);
        }
    }

    @Transactional(readOnly = true)
    public List<Editorial> listarEditoriales() {

        List<Editorial> editoriales = new ArrayList<>();
        editoriales = editorialRespositorio.findAll();
        return editoriales;
    }
}
