package org.ups.liberacionesobra.infrastructure.persistence.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ups.liberacionesobra.domain.model.Actividad;
import org.ups.liberacionesobra.infrastructure.persistence.entity.ActividadJpaEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ActividadMapper")
class ActividadMapperTest {

    private final ActividadMapper mapper = new ActividadMapper();

    @Test
    @DisplayName("Dada una entidad JPA, Cuando toDomain, Entonces mapea todos los campos")
    void dadaEntidad_cuandoToDomain_entoncesMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID frenteId = UUID.randomUUID();
        ActividadJpaEntity entity = ActividadJpaEntity.builder()
                .id(id).frenteId(frenteId)
                .nombre("Encofrado columnas").descripcion("Nivel 3")
                .build();

        Actividad domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getFrenteId()).isEqualTo(frenteId);
        assertThat(domain.getNombre()).isEqualTo("Encofrado columnas");
        assertThat(domain.getDescripcion()).isEqualTo("Nivel 3");
    }

    @Test
    @DisplayName("Dado un dominio, Cuando toEntity, Entonces mapea todos los campos")
    void dadoDominio_cuandoToEntity_entoncesMapeaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        UUID frenteId = UUID.randomUUID();
        Actividad domain = Actividad.builder()
                .id(id).frenteId(frenteId)
                .nombre("Fundida losa").descripcion("Eje A-B")
                .build();

        ActividadJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFrenteId()).isEqualTo(frenteId);
        assertThat(entity.getNombre()).isEqualTo("Fundida losa");
        assertThat(entity.getDescripcion()).isEqualTo("Eje A-B");
    }
}
