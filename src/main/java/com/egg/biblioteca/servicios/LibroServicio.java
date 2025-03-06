package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.entidades.Libro;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.AutorRepositorio;
import com.egg.biblioteca.repositorios.EditorialRepositorio;
import com.egg.biblioteca.repositorios.LibroRepositorio;

@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void crearLibro(Long isbn, String titulo, Integer ejemplares, UUID idAutor, UUID idEditorial)
            throws MiException {
        validar(isbn, titulo, ejemplares, idAutor, idEditorial);

        // Autor autor = autorRepositorio.findById(idAutor).get();
        // Editorial editorial = editorialRepositorio.findById(idEditorial).get();

        Autor autor = autorRepositorio.findById(idAutor)
                .orElseThrow(() -> new MiException("Autor no encontrado con ID: " + idAutor));

        Editorial editorial = editorialRepositorio.findById(idEditorial)
                .orElseThrow(() -> new MiException("Editorial no encontrada con ID: " + idEditorial));

        Libro libro = new Libro();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setEjemplares(ejemplares);
        libro.setAlta(new Date());
        libro.setAutor(autor);
        libro.setEditorial(editorial);
        libroRepositorio.save(libro);
    }

    @Transactional
    private void validar(Long isbn, String titulo, Integer ejemplares, UUID idAutor, UUID idEditorial)
            throws MiException {
        if (isbn == null) {
            throw new MiException("No se puede generar la acción, el isbn está nulo o vacío");
        }
        if (titulo.isEmpty() || titulo == null) {
            throw new MiException("el titulo no puede ser nulo o estar vacio");
        }
        if (ejemplares == null) {
            throw new MiException("ejemplares no puede ser nulo");
        }
        if (idAutor == null) {
            throw new MiException("el Autor no puede ser nulo o estar vacio");
        }
        if (idEditorial == null) {
            throw new MiException("La Editorial no puede ser nula o estar vacia");
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> listarLibros() {
        List<Libro> libros = new ArrayList<>();
        libros = libroRepositorio.findAll();
        return libros;
    }

    @Transactional
    public void modificarLibro(Long isbn, String titulo, Integer ejemplares, UUID idAutor, UUID idEditorial)
            throws MiException {
        validar(isbn, titulo, ejemplares, idAutor, idEditorial);
        Optional<Editorial> respuesta = editorialRepositorio.findById(idEditorial);
        Optional<Autor> respuesta2 = autorRepositorio.findById(idAutor);
        Optional<Libro> respuesta3 = libroRepositorio.findById(isbn);

        if (respuesta.isPresent() & respuesta2.isPresent() & respuesta3.isPresent()) {
            Libro libro = new Libro();
            Autor autor = respuesta2.get();
            Editorial editorial = respuesta.get();
            libro = respuesta3.get();
            libro.setTitulo(titulo);
            libro.setEjemplares(ejemplares);
            libro.setAutor(autor);
            libro.setEditorial(editorial);
            libroRepositorio.save(libro);
        } else {
            System.out.println("No se pudo modificar el libro debido a que uno de sus atributos no existe");
        }
    }

}
