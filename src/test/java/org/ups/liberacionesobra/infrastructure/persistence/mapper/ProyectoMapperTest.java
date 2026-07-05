package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ups.liberacionesobra.domain.model.Proyecto;
import org.ups.liberacionesobra.infrastructure.persistence.entity.ProyectoJpaEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProyectoMapper")
class ProyectoMapperTest {

    private final ProyectoMapper mapper = new ProyectoMapper();

    @Test
    @DisplayName("Dada una entidad JPA, Cuando toDomain, Entonces mapea todos los campos")
    void dadaEntidad_cuandoToDomain_entoncesMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        ProyectoJpaEntity entity = ProyectoJpaEntity.builder()
                .id(id).nombre("Proyecto Alfa").descripcion("Torre residencial")
                .build();

        Proyecto domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getNombre()).isEqualTo("Proyecto Alfa");
        assertThat(domain.getDescripcion()).isEqualTo("Torre residencial");
    }

    @Test
    @DisplayName("Dado un dominio, Cuando toEntity, Entonces mapea todos los campos")
    void dadoDominio_cuandoToEntity_entoncesMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Proyecto domain = Proyecto.builder()
                .id(id).nombre("Proyecto Beta").descripcion("Centro comercial")
                .build();

        ProyectoJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNombre()).isEqualTo("Proyecto Beta");
        assertThat(entity.getDescripcion()).isEqualTo("Centro comercial");
    }
}
