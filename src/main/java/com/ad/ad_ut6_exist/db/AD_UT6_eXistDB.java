

package com.ad.ad_ut6_exist.db;

import java.time.LocalDate;
import java.time.Month;

/**
 *
 * @author anrod
 */
public class AD_UT6_eXistDB {

    public static void main(String[] args) {
         AccesoLibros conexionDB = new AccesoLibros();
         conexionDB.conectar();         
         conexionDB.consultarTitulos();
         System.out.println("\n\n");
         
               
         Libro libro = new Libro(
                 "bk003", 
                 "J.R.R.Tolkien",
                 "The hobbit",
                 "Fantasy", 
                 13.50,
                 LocalDate.of(2005, Month.MARCH, 24),
                 "The hobbit, or there and back again"
         );
         
        //conexionDB.insertarLibro2(libro);
 
        
        //conexionDB.actualizarLibroPorId("bk002", libro);
        
        conexionDB.consultarTitulosYAutorBonito();
        
        System.out.println("\n\nEl libro bk002 está duplicado? =>"
                +conexionDB.libroDuplicado("bk002"));
        
        System.out.println("\n\n Libros con el título : XML Developer's Guide");
        System.out.println(conexionDB.buscarLibroPorTitulo("XML Developer's Guide"));
        
        conexionDB.eliminarLibroPorTitulo("pepe");
               
        
        conexionDB.desconectar();
    }
}
