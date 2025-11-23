// package io.github.rodrigobarr0s.access_modules_api.integration;

// import static org.junit.jupiter.api.Assertions.*;

// import java.util.List;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
// import io.github.rodrigobarr0s.access_modules_api.entity.Module;
// import io.github.rodrigobarr0s.access_modules_api.entity.User;
// import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
// import io.github.rodrigobarr0s.access_modules_api.entity.enums.SolicitationStatus;
// import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
// import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
// import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

// @SpringBootTest
// @ActiveProfiles("test")
// @Transactional
// class AccessSolicitationServiceIntegrationTest {

//     @Autowired
//     private AccessSolicitationService solicitationService;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private ModuleRepository moduleRepository;

//     private User createUser(String name) {
//         return userRepository.save(new User(null, name, "123456", Role.ADMIN));
//     }

//     private Module createModule(String name) {
//         return moduleRepository.save(new Module(null, name, "desc-" + name));
//     }

//     @Test
//     @DisplayName("Deve criar solicitação com protocolo e status PENDING")
//     void create_shouldPersistWithProtocoloAndPendingStatus() {
//         User user = createUser("user1");
//         Module module = createModule("mod1");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);

//         AccessSolicitation saved = solicitationService.create(solicitation);

//         assertNotNull(saved.getId());
//         assertNotNull(saved.getProtocolo());
//         assertEquals(SolicitationStatus.PENDING, saved.getStatus());
//     }

//     @Test
//     @DisplayName("Deve aprovar solicitação existente por protocolo")
//     void approve_shouldUpdateStatus() {
//         User user = createUser("user2");
//         Module module = createModule("mod2");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);
//         solicitation = solicitationService.create(solicitation);

//         AccessSolicitation approved = solicitationService.approve(solicitation.getProtocolo());

//         assertEquals(SolicitationStatus.APPROVED, approved.getStatus());
//     }

//     @Test
//     @DisplayName("Deve rejeitar solicitação existente por protocolo e definir motivo")
//     void reject_shouldUpdateStatusAndReason() {
//         User user = createUser("user3");
//         Module module = createModule("mod3");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);
//         solicitation = solicitationService.create(solicitation);

//         AccessSolicitation rejected = solicitationService.reject(solicitation.getProtocolo(), "Motivo de teste");

//         assertEquals(SolicitationStatus.REJECTED, rejected.getStatus());
//         assertEquals("Motivo de teste", rejected.getCancelReason());
//     }

//     @Test
//     @DisplayName("Deve cancelar solicitação existente por protocolo e definir motivo")
//     void cancel_shouldUpdateStatusAndReason() {
//         User user = createUser("user4");
//         Module module = createModule("mod4");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);
//         solicitation = solicitationService.create(solicitation);

//         AccessSolicitation canceled = solicitationService.cancel(solicitation.getProtocolo(), "Cancelado por teste");

//         assertEquals(SolicitationStatus.CANCELED, canceled.getStatus());
//         assertEquals("Cancelado por teste", canceled.getCancelReason());
//     }

//     @Test
//     @DisplayName("Deve renovar solicitação existente por protocolo e atualizar expiresAt")
//     void renew_shouldUpdateExpiresAtAndProtocolo() {
//         User user = createUser("user5");
//         Module module = createModule("mod5");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);
//         solicitation = solicitationService.create(solicitation);

//         AccessSolicitation renewed = solicitationService.renew(solicitation.getProtocolo());

//         assertEquals(SolicitationStatus.PENDING, renewed.getStatus());
//         assertNotNull(renewed.getExpiresAt());
//         assertTrue(renewed.getProtocolo().startsWith("SOL-"));
//     }

//     @Test
//     @DisplayName("Deve buscar solicitações por status")
//     void findByStatus_shouldReturnList() {
//         User user = createUser("user6");
//         Module module = createModule("mod6");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);
//         solicitation = solicitationService.create(solicitation);

//         List<AccessSolicitation> result = solicitationService.findByStatus(SolicitationStatus.PENDING);

//         assertFalse(result.isEmpty());
//         assertEquals(SolicitationStatus.PENDING, result.get(0).getStatus());
//     }

//     @Test
//     @DisplayName("Deve aplicar filtros dinâmicos corretamente")
//     void findWithFilters_shouldFilterCorrectly() {
//         User user = createUser("user7");
//         Module module = createModule("mod7");

//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);
//         solicitation.setUrgente(true);
//         solicitation = solicitationService.create(solicitation);

//         List<AccessSolicitation> result = solicitationService.findWithFilters(
//                 SolicitationStatus.PENDING,
//                 solicitation.getUser().getId(),
//                 solicitation.getModule().getId(),
//                 true);

//         assertEquals(1, result.size());
//     }
// }
