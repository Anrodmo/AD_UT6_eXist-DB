
package com.ad.ad_ut6_exist.db;

import java.lang.reflect.InvocationTargetException;
import org.xmldb.api.*;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;

public class AccesoLibros {
    private Collection col = null;
    private XPathQueryService servicio;
    
    public void conectar(){
        String driver = "org.exist.xmldb.DatabaseImpl";
        this.col = null;
        
        try {
            Class<?> cl = Class.forName(driver);
            System.out.println("Driver encontrado para : "+driver);
            //Database db = (Database) cl.newInstance();  // .newIntacnce() esta obsoleto
            Database db = (Database)cl.getDeclaredConstructor().newInstance();
           
            System.out.println("Instancia de la BBDD creada.");
            
            DatabaseManager.registerDatabase(db);
            System.out.println("BBDD registrada.");
            
            // asiganoms la conexión añ atributo col de la clase que sera accesible por todos
            // los métodos
            col = DatabaseManager.getCollection(
                    DatosAcceso.getUri(), 
                    DatosAcceso.getUser(),
                    DatosAcceso.getPass()
            );
            System.out.println("comexión exitosa a la BBDD "+DatosAcceso.getUri());
            
            // comprobamos si la colección tiene recursos (archivos o colecciones)
            if(col == null){
                System.out.println("La colección está vacía o no enconrtada");
            }else{
                System.out.println("Nº recursos de la colección: "+col.getResourceCount() );
                System.out.println("Primer recurso : "+col.listResources()[0]);
            }
            
            servicio = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            
        } catch (XMLDBException ex) {
            System.out.println("Error al registrar la BD en la instancia");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error, no enconrtado el driver: "+driver);
        }  catch (SecurityException ex) {
            System.out.println("Error al crear la inatancia de la BBDD");
        }catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            System.out.println("Error al crear el new instance del driver");
        }catch (NoSuchMethodException ex) {
            System.out.println("Error al crear la inatancia de la BBDD");
        }   
    }
    
    public void desconectar(){
        try{
            col.close();
        }catch (Exception e) {
            System.out.println("Error al desconectar de la BBDD");
            e.printStackTrace();
        }
    }
    
    public void consultarTitulos(){
        try{
//            ResourceSet result = 
//                    servicio.query("for $b in //title/text() return $b");
            ResourceSet result = 
                    servicio.query("for $b in //book return ($b/title/text() )");
            System.out.println("Nº de resultados = "+result.getSize());
            // Creo un irerador del resultado
            ResourceIterator it = result.getIterator();
            // y recorro el iterador
            while(it.hasMoreResources()){
                Resource unResultado = (Resource) it.nextResource();
                System.out.println(unResultado.getContent().toString());
            }           
        }catch(Exception e) {  // el codigo no pide ecepcion pero capturo por si acaso
            e.printStackTrace();
        }
       
    }
    
    public void consultarTitulosYAutor(){
        try{
            ResourceSet result = 
                    servicio.query("for $b in //book return ($b/title/text(),$b/author/text())");
            System.out.println("Nº de resultados = "+result.getSize());
            // Creo un irerador del resultado
            ResourceIterator it = result.getIterator();
            // y recorro el iterador
            while(it.hasMoreResources()){
                Resource unResultado = (Resource) it.nextResource();
                System.out.println(unResultado.getContent().toString());
            }           
        }catch(Exception e) {  // el codigo no pide excepcion pero capturo por si acaso
            System.out.println("Error al consultar título / autor");
            e.printStackTrace();
        }       
    }
    
    public void consultarTitulosYAutorBonito(){
        try{
            ResourceSet result = 
                    servicio.query("for $b in //book return ($b/title/text(),$b/author/text())");
            System.out.println("Nº de resultados = "+result.getSize());
            // Creo un irerador del resultado
            ResourceIterator it = result.getIterator();
            // y recorro el iterador
            while(it.hasMoreResources()){
                Resource r = (Resource) it.nextResource();
                System.out.print("Titulo:"+ (String) r.getContent()+"---");
                r = (Resource) it.nextResource();
                System.out.println("Autor:"+(String) r.getContent());
            }           
        }catch(Exception e) {  // el codigo no pide excepcion pero capturo por si acaso
            System.out.println("Error al consultar título / autor");
            e.printStackTrace();
        }       
    }
    
    public void insertarLibro(Libro libro) {
        try { 
            
            XMLResource xmlResource = (XMLResource) col.getResource("Books.xml");
            // Crear el nuevo recurso XML (libro)
            String contenidoActual = (String) xmlResource.getContent();
            
            
            String  nuevoLibro=(generarXMLLibro(libro));

            contenidoActual = contenidoActual.replace("</catalog>", nuevoLibro + "\n</catalog>");
            xmlResource.setContent(contenidoActual);
            col.storeResource(xmlResource);
            
            System.out.println("Libro insertado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al insertar el libro en el XML.");
            e.printStackTrace();
        }
    }
    
    public void insertarLibro2(Libro libro) {
    try {       
        Resource resource = col.getResource("Books.xml");
        String xmlContent = (String) resource.getContent();
        int index = xmlContent.lastIndexOf("</catalog>");
        
        if (index != -1) {
            StringBuilder newXmlContent = new StringBuilder(xmlContent);
            newXmlContent.insert(index, generarXMLLibro(libro));
            resource.setContent(newXmlContent.toString());
            col.storeResource(resource);
            System.out.println("Empleado agregado correctamente.");
        } else {
                System.out.println("No se encontró el elemento <EMPLEADOS> en el XML.");
        }      
        System.out.println("Libro insertado correctamente.");
    } catch (Exception e) {
        System.out.println("Error al insertar el libro con update insert.");
        e.printStackTrace();
    }
}
    
    public boolean libroDuplicado(String bookId) {
        boolean libroDuplicado = false;
        try {
            String query = String.format("count(/catalog/book[@id='%s'])", bookId);
            ResourceSet result = servicio.query(query);

            if (result != null && result.getSize() > 0) {
                ResourceIterator it = result.getIterator();
                while (it.hasMoreResources() && !libroDuplicado) {
                    Resource resource = it.nextResource();
                    long count = Long.parseLong(resource.getContent().toString());
                    libroDuplicado = count > 1; // Si count > 1, significa que el ID del libro está duplicado
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Imprimir el error en la consola
        }
    return libroDuplicado; // En caso de error o si no se encontraron resultados
}
    
    public void actualizarLibroPorId(String bookId, Libro nuevoLibro) {
        try {
        // Construir la consulta XQuery para actualizar el libro por su ID
        String query = String.format(
            "update replace /catalog/book[@id='%s'] with %s", 
            bookId, 
            generarXMLLibro(nuevoLibro)
        );
            // Ejecutar la consulta
            servicio.query(query);

            System.out.println("Libro actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("Error al actualizar el libro.");
            e.printStackTrace();
        }        
    }
    
    public String buscarLibroPorTitulo(String titulo) {
    StringBuilder resultado = new StringBuilder();
    
    try {
        //String query = "for $book in //book[title='" + titulo + "'] return $book";
        // si quiero que la consulta ea case insensitive
        //String query = "for $book in //book[lower-case(title)='" + titulo.toLowerCase() + "'] return $book";     
        // Si el titulo contiene ' hay que escapar el caracter
        String query = "for $book in //book[title='" + titulo.replace("'", "''") + "'] return $book";
       
        
        
        ResourceSet result = servicio.query(query);
        ResourceIterator it = result.getIterator();

        while (it.hasMoreResources()) {
            resultado.append(it.nextResource().getContent()).append("\n");
        }
    } catch (Exception e) {
        System.err.println("Error al buscar libro por título.");
        e.printStackTrace();
    }
    return resultado.toString();
    }
    
    public void eliminarLibroPorTitulo(String titulo) {
    try {
        // Construir la consulta XPath para eliminar el libro por título
        String query = String.format("update delete //book[title='%s']", titulo);

        // Ejecutar la consulta
        servicio.query(query);

        System.out.println("Libro eliminado correctamente.");
    } catch (Exception e) {
        System.out.println("Error al eliminar el libro por título.");
        e.printStackTrace();
    }
}
    

    /**
     * Método auxiliar que dado un objeto libro devuelve la consulta para
     * isertarlo en la BBDD.
     * @param libro
     * @return
     */
    private  String generarXMLLibro(Libro libro){       
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<book id=\"")
                .append(libro.getId()).append("\">");
        xmlBuilder.append("<author>")
                .append(libro.getAutor()).append("</author>");
        xmlBuilder.append("<title>")
                .append(libro.getTitulo()).append("</title>");
        xmlBuilder.append("<genre>")
                .append(libro.getGenero()).append("</genre>");
        xmlBuilder.append("<price>")
                .append(libro.getPrecio()).append("</price>");
        xmlBuilder.append("<publish_date>")
                .append(libro.getFecha()).append("</publish_date>");
        xmlBuilder.append("<description>")
                .append(libro.getDescripcion()).append("</description>");
        xmlBuilder.append("</book>");
        return xmlBuilder.toString();
    }
    
}
