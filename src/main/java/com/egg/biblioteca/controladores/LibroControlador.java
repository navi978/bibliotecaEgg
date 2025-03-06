package com.egg.biblioteca.controladores;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.servicios.AutorServicio;
import com.egg.biblioteca.servicios.EditorialServicio;
import com.egg.biblioteca.servicios.LibroServicio;

@Controller
@RequestMapping("/libro")
public class LibroControlador {
    
    @Autowired
    AutorServicio autorServicio;

    @Autowired
    EditorialServicio editorialServicio;

    @Autowired
    LibroServicio libroServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        List<Autor> autores = autorServicio.listarAutores();

        List<Editorial> editoriales = editorialServicio.listarEditoriales();

        modelo.addAttribute("autores", autores);
        modelo.addAttribute("editoriales", editoriales);

        return "libro_form.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam(required = false) Long isbn, @RequestParam String titulo, @RequestParam(required = false) Integer ejemplares, @RequestParam String idAutor, @RequestParam UUID idEditorial, ModelMap modelo){
        try {
            // Handle invalid UUID
            UUID uuidAutor = null;
    
            // Check if idAutor is valid and not "Seleccionar Autor"
            if (idAutor != null && !idAutor.equals("Seleccionar Autor")) {
                uuidAutor = UUID.fromString(idAutor); // Convert String to UUID
            }
    
            // If the UUID is invalid or not selected, return an error or handle accordingly
            if (uuidAutor == null) {
                modelo.put("error", "Debe seleccionar un autor válido.");
                return "libro_form.html"; // Or another view to display the error
            }
    
            libroServicio.crearLibro(isbn, titulo, ejemplares, uuidAutor, idEditorial);
            modelo.put("exito", "El libro fue cargado correctamente");

        } catch (IllegalArgumentException ex) {
            modelo.put("error", "El ID del autor no es válido.");
            return "libro_form.html";

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "libro_form.html";
        }
        return "index.html";
    }
}
