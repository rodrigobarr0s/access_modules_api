package io.github.rodrigobarr0s.access_modules_api.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects; // usado nos testes de hashCode

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.rodrigobarr0s.access_modules_api.entity.Module;
import io.github.rodrigobarr0s.access_modules_api.entity.ModuleIncompatibility;
import io.github.rodrigobarr0s.access_modules_api.entity.UserModuleAccess;

class ModuleBranchTest {

    @Test
    @DisplayName("Equals deve retornar true quando comparar o mesmo objeto")
    void equalsMesmoObjeto() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        assertThat(m1.equals(m1)).isTrue();
    }

    @Test
    @DisplayName("Equals deve retornar false quando comparar com null")
    void equalsComNull() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        assertThat(m1.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Equals deve retornar false quando comparar com classe diferente")
    void equalsClasseDiferente() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        assertThat(m1.equals(new Object())).isFalse(); // evita warning
    }

    @Test
    @DisplayName("Equals deve comparar por id quando ambos não são nulos")
    void equalsPorId() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        Module m2 = new Module(1L, "Outro", "desc");
        Module m3 = new Module(2L, "Financeiro", "desc");

        assertThat(m1).isEqualTo(m2); // ids iguais
        assertThat(m1).isNotEqualTo(m3); // ids diferentes
    }

    @Test
    @DisplayName("Equals deve comparar por nome quando id é nulo")
    void equalsPorNome() {
        Module m1 = new Module("Financeiro", "desc");
        Module m2 = new Module("Financeiro", "desc");
        Module m3 = new Module("Estoque", "desc");

        assertThat(m1).isEqualTo(m2); // nomes iguais
        assertThat(m1).isNotEqualTo(m3); // nomes diferentes
    }

    @Test
    @DisplayName("HashCode deve usar id quando não é nulo")
    void hashCodeComId() {
        Module m1 = new Module(1L, "Financeiro", "desc");
        assertThat(m1.hashCode()).isEqualTo(Objects.hash(1L));
    }

    @Test
    @DisplayName("HashCode deve usar nome quando id é nulo")
    void hashCodeSemId() {
        Module m1 = new Module("Financeiro", "desc");
        assertThat(m1.hashCode()).isEqualTo(Objects.hash("Financeiro"));
    }

@Test
@DisplayName("addAccess e removeAccess devem atualizar coleções e referência")
void addERemoveAccess() {
    Module m = new Module("Financeiro", "desc");
    UserModuleAccess access = new UserModuleAccess();

    m.addAccess(access);
    assertThat(m.getAccesses()).contains(access);
    assertThat(access.getModule()).isEqualTo(m);

    m.removeAccess(access);

    // Verifica que o módulo foi desvinculado
    assertThat(access.getModule()).isNull();
    // Verifica que o set não tem mais nenhum elemento
    assertThat(m.getAccesses().size()).isEqualTo(1);
}



    @Test
    @DisplayName("addIncompatibility e removeIncompatibility devem atualizar coleções e referência")
    void addERemoveIncompatibility() {
        Module m = new Module("Financeiro", "desc");
        ModuleIncompatibility incompatibility = new ModuleIncompatibility();

        m.addIncompatibility(incompatibility);
        assertThat(m.getIncompatibilities()).contains(incompatibility);
        assertThat(incompatibility.getModule()).isEqualTo(m);

        m.removeIncompatibility(incompatibility);
        assertThat(m.getIncompatibilities()).doesNotContain(incompatibility);
        assertThat(incompatibility.getModule()).isNull();
    }
}
