package com.egg.biblioteca.controladores;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.entidades.Libro;
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

    private static final Logger libroLog = Logger.getLogger(Libro.class.getName());

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        List<Autor> autores = autorServicio.listarAutores();

        List<Editorial> editoriales = editorialServicio.listarEditoriales();

        modelo.addAttribute("autores", autores);
        modelo.addAttribute("editoriales", editoriales);

        return "libro_form.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam(required = false) Long isbn, @RequestParam String titulo,
            @RequestParam(required = false) Integer ejemplares, @RequestParam String idAutor,
            @RequestParam String idEditorial, ModelMap modelo) {
        try {
            // Handle invalid UUID
            UUID uuidAutor = null;
            UUID uuidEditorial = null;

            // Check if idAutor is valid and not "Seleccionar Autor"
            if (idAutor != null && !idAutor.equals("Seleccionar Autor")) {
                uuidAutor = UUID.fromString(idAutor); // Convert String to UUID
            }

            // If the UUID is invalid or not selected, return an error or handle accordingly
            if (uuidAutor == null) {
                modelo.put("error", "Debe seleccionar un autor válido.");
                return "libro_form.html"; // Or another view to display the error
            }

            if (idEditorial != null && !idEditorial.equals("Seleccionar Editorial")) {
                uuidEditorial = UUID.fromString(idEditorial);
            }

            if (uuidEditorial == null) {
                modelo.put("error", "Debe seleccionar una editorial válida.");
                return "libro_form.html"; // Or another view to display the error
            }

            libroServicio.crearLibro(isbn, titulo, ejemplares, uuidAutor, uuidEditorial);
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

    @GetMapping("/lista")
    public String listar(ModelMap modelo) {

        List<Libro> libros = libroServicio.listarLibros();
        modelo.addAttribute("libros", libros);
        return "libro_list.html";
    }

    @GetMapping("/modificar/{isbn}")
    public String modificar(@PathVariable Long isbn,ModelMap modelo){
        List<Autor> autores = autorServicio.listarAutores();
        List<Editorial> editoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("autores",autores);
        modelo.addAttribute("editoriales",editoriales);
        try {
            Libro libro = libroServicio.getOne(isbn);
            modelo.put("libro", libro);
        } catch (Exception e) {
            libroLog.log(Level.SEVERE, e.getMessage(), e);
        }
        return "libro_modificar.html";
        
    }

    @PostMapping("/modificar/{isbn}")
    public String modificar(@PathVariable Long isbn, String titulo, Integer ejemplares, String idAutor, String idEditorial, ModelMap modelo) {
        libroLog.log(Level.INFO, "idEditorial: "+idEditorial);
        try {
            List<Autor> autores = autorServicio.listarAutores();
            List<Editorial> editoriales = editorialServicio.listarEditoriales();

            modelo.addAttribute("autores", autores);
            modelo.addAttribute("editoriales", editoriales);

            UUID uuidAutor = UUID.fromString(idAutor);
            UUID uuidEditorial = UUID.fromString(idEditorial);
            
            libroServicio.modificarLibro(isbn, titulo, ejemplares, uuidAutor, uuidEditorial);
            
            return "redirect:../lista";

        } catch (Exception ex) {
            libroLog.log(Level.SEVERE, ex.getMessage(), ex);
            List<Autor> autores = autorServicio.listarAutores();
            List<Editorial> editoriales = editorialServicio.listarEditoriales();

            modelo.put("error", ex.getMessage());

            modelo.addAttribute("autores", autores);
            modelo.addAttribute("editoriales", editoriales);

            return "libro_modificar.html";
        }
    }

}
