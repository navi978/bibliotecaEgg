package com.egg.biblioteca.servicios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.egg.biblioteca.entidades.Imagen;
import com.egg.biblioteca.repositorios.ImagenRepositorio;

@Service
public class ImagenServicio {
    @Autowired
    private ImagenRepositorio imagenRepositorio;

    public Imagen guardar(MultipartFile archivo) throws Exception {
        if (archivo == null) {
            throw new Exception("El archivo no puede ser nulo");
        }

        try {
            Imagen imagen = new Imagen();

            imagen.setMime(archivo.getContentType());
            imagen.setNombre(archivo.getName());
            imagen.setContenido(archivo.getBytes());

            return imagenRepositorio.save(imagen);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Imagen actualizar(MultipartFile archivo, UUID imagenId) throws Exception {

        if (archivo == null) {
            return null;
        }

        try {
            Imagen imagen = new Imagen();

            if (imagenId != null) {
                Optional<Imagen> respuesta = imagenRepositorio.findById(imagenId);
                if (respuesta.isPresent()) {
                    imagen = respuesta.get();
                }
            }

            imagen.setMime(archivo.getContentType());
            imagen.setNombre(archivo.getName());
            imagen.setContenido(archivo.getBytes());

            return imagenRepositorio.save(imagen);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Imagen getOne(String id) {
        UUID uuidImagen = UUID.fromString(id);
        return imagenRepositorio.getReferenceById(uuidImagen);
    }

    @Transactional(readOnly = true)
    public List<Imagen> listarTodos() {
        return imagenRepositorio.findAll();
    }

}