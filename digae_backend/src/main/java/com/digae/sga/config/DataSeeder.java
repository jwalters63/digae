package com.digae.sga.config;

import com.digae.sga.entity.*;
import com.digae.sga.entity.enums.TipoInstalacion;
import com.digae.sga.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final FacultadRepository facultadRepository;
    private final InstalacionRepository instalacionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            System.out.println("Nuked DB detected. Seeding fresh data...");

            // 1. Create Facultades
            Facultad facIngenieria = Facultad.builder()
                    .nombre("Facultad de Ingeniería")
                    .siglas("FI")
                    .build();
            facultadRepository.save(facIngenieria);

            Facultad facCiencias = Facultad.builder()
                    .nombre("Facultad de Ciencias")
                    .siglas("FC")
                    .build();
            facultadRepository.save(facCiencias);

            // 2. Create Instalaciones (Adapter logic relies on matching names)
            Instalacion instIng = Instalacion.builder()
                    .nombre("Facultad de Ingeniería")
                    .tipo(TipoInstalacion.FACULTAD)
                    .ubicacion("Campus Principal")
                    .build();
            instalacionRepository.save(instIng);

            Instalacion instCiencias = Instalacion.builder()
                    .nombre("Facultad de Ciencias")
                    .tipo(TipoInstalacion.FACULTAD)
                    .ubicacion("Campus Norte")
                    .build();
            instalacionRepository.save(instCiencias);
            
            Instalacion instLab = Instalacion.builder()
                    .nombre("Laboratorio de Química")
                    .tipo(TipoInstalacion.LABORATORIO)
                    .ubicacion("Edificio Q")
                    .build();
            instalacionRepository.save(instLab);

            // 3. Create Admin
            Administrador admin = new Administrador();
            admin.setNombre("Administrador");
            admin.setApellido("General");
            admin.setEmail("admin@digae.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            usuarioRepository.save(admin);

            // 4. Create Usuario Operativo
            UsuarioOperativo uo = new UsuarioOperativo();
            uo.setNombre("Coordinador");
            uo.setApellido("Ingeniería");
            uo.setEmail("coordinador@digae.com");
            uo.setPassword(passwordEncoder.encode("coord123"));
            uo.setFacultad(facIngenieria);
            usuarioRepository.save(uo);

            System.out.println("Seeding completed.");
        }
    }
}
