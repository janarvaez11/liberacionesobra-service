package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ups.liberacionesobra.domain.model.Frente;
import org.ups.liberacionesobra.infrastructure.persistence.entity.FrenteJpaEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FrenteMapper")
class FrenteMapperTest {

    private final FrenteMapper mapper = new FrenteMapper();

    @Test
    @DisplayName("Dada una entidad JPA, Cuando toDomain, Entonces mapea todos los campos")
    void dadaEntidad_cuandoToDomain_entoncesMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID proyectoId = UUID.randomUUID();
        UUID residenteId = UUID.randomUUID();
        FrenteJpaEntity entity = FrenteJpaEntity.builder()
                .id(id).proyectoId(proyectoId)
                .nombre("Frente Norte").residenteId(residenteId)
                .build();

        Frente domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getProyectoId()).isEqualTo(proyectoId);
        assertThat(domain.getNombre()).isEqualTo("Frente Norte");
        assertThat(domain.getResidenteId()).isEqualTo(residenteId);
    }

    @Test
    @DisplayName("Dado un dominio, Cuando toEntity, Entonces mapea todos los campos")
    void dadoDominio_cuandoToEntity_entoncesMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID proyectoId = UUID.randomUUID();
        UUID residenteId = UUID.randomUUID();
        Frente domain = Frente.builder()
                .id(id).proyectoId(proyectoId)
                .nombre("Frente Sur").residenteId(residenteId)
                .build();

        FrenteJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getProyectoId()).isEqualTo(proyectoId);
        assertThat(entity.getNombre()).isEqualTo("Frente Sur");
        assertThat(entity.getResidenteId()).isEqualTo(residenteId);
    }
}
