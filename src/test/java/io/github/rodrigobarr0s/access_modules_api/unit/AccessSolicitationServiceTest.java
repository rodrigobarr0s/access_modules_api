// package io.github.rodrigobarr0s.access_modules_api.unit;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.dao.DataIntegrityViolationException;

// import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
// import io.github.rodrigobarr0s.access_modules_api.entity.Module;
// import io.github.rodrigobarr0s.access_modules_api.entity.User;
// import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
// import io.github.rodrigobarr0s.access_modules_api.repository.AccessSolicitationRepository;
// import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;
// import io.github.rodrigobarr0s.access_modules_api.service.exception.DatabaseException;
// import io.github.rodrigobarr0s.access_modules_api.service.exception.DuplicateEntityException;
// import io.github.rodrigobarr0s.access_modules_api.service.exception.ResourceNotFoundException;
// import jakarta.persistence.EntityNotFoundException;

// @ExtendWith(MockitoExtension.class)
// class AccessSolicitationServiceTest {

//     @Mock
//     private AccessSolicitationRepository repository;

//     @InjectMocks
//     private AccessSolicitationService service;

//     @Test
//     @DisplayName("Deve salvar solicitação nova com status PENDING")
//     void save_shouldPersistSolicitation() {
//         User user = new User(1L, "user1", "123456", Role.ADMIN);
//         Module module = new Module(1L, "mod1", "desc1");
//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);

//         when(repository.findByUserAndModuleAndStatus(user, module, "PENDING")).thenReturn(Optional.empty());
//         when(repository.save(any(AccessSolicitation.class)))
//                 .thenAnswer(invocation -> {
//                     AccessSolicitation s = invocation.getArgument(0);
//                     s.setId(1L);
//                     return s;
//                 });

//         AccessSolicitation result = service.save(solicitation);

//         assertNotNull(result.getId());
//         assertEquals("PENDING", result.getStatus());
//         verify(repository).save(solicitation);
//     }

//     @Test
//     @DisplayName("Deve lançar DuplicateEntityException ao salvar solicitação duplicada")
//     void save_shouldThrowDuplicateEntityException() {
//         User user = new User(1L, "user1", "123456", Role.ADMIN);
//         Module module = new Module(1L, "mod1", "desc1");
//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setUser(user);
//         solicitation.setModule(module);

//         when(repository.findByUserAndModuleAndStatus(user, module, "PENDING"))
//                 .thenReturn(Optional.of(new AccessSolicitation()));

//         assertThrows(DuplicateEntityException.class, () -> service.save(solicitation));
//     }

//     @Test
//     @DisplayName("Deve retornar todas as solicitações")
//     void findAll_shouldReturnList() {
//         when(repository.findAll()).thenReturn(Arrays.asList(new AccessSolicitation()));

//         List<AccessSolicitation> result = service.findAll();

//         assertEquals(1, result.size());
//         verify(repository).findAll();
//     }

//     @Test
//     @DisplayName("Deve retornar solicitação por ID existente")
//     void findById_shouldReturnSolicitation() {
//         when(repository.findById(1L)).thenReturn(Optional.of(new AccessSolicitation()));
//         AccessSolicitation result = service.findById(1L);
//         assertNotNull(result);
//     }

//     @Test
//     @DisplayName("Deve lançar ResourceNotFoundException ao buscar solicitação inexistente")
//     void findById_shouldThrowResourceNotFound() {
//         when(repository.findById(99L)).thenReturn(Optional.empty());
//         assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
//     }

//     @Test
//     @DisplayName("Deve retornar solicitações pendentes")
//     void findPending_shouldReturnList() {
//         when(repository.findByStatus("PENDING")).thenReturn(Arrays.asList(new AccessSolicitation()));
//         List<AccessSolicitation> result = service.findPending();
//         assertEquals(1, result.size());
//     }

//     @Test
//     @DisplayName("Deve deletar solicitação existente")
//     void delete_shouldRemoveSolicitation() {
//         when(repository.existsById(1L)).thenReturn(true);
//         service.delete(1L);
//         verify(repository).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Deve lançar ResourceNotFoundException ao deletar solicitação inexistente")
//     void delete_shouldThrowResourceNotFound() {
//         when(repository.existsById(99L)).thenReturn(false);
//         assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
//     }

//     @Test
//     @DisplayName("Deve lançar DatabaseException ao ocorrer erro de integridade na deleção")
//     void delete_shouldThrowDatabaseException() {
//         when(repository.existsById(1L)).thenReturn(true);
//         doThrow(new DataIntegrityViolationException("error")).when(repository).deleteById(1L);
//         assertThrows(DatabaseException.class, () -> service.delete(1L));
//     }

//     @Test
//     @DisplayName("Deve aprovar solicitação existente")
//     void approve_shouldSetStatusApproved() {
//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setId(1L);
//         solicitation.setStatus("PENDING");

//         when(repository.getReferenceById(1L)).thenReturn(solicitation);
//         when(repository.save(solicitation)).thenReturn(solicitation);

//         AccessSolicitation result = service.approve(1L);

//         assertEquals("APPROVED", result.getStatus());
//     }

//     @Test
//     @DisplayName("Deve lançar ResourceNotFoundException ao aprovar solicitação inexistente")
//     void approve_shouldThrowResourceNotFound() {
//         when(repository.getReferenceById(99L)).thenThrow(new EntityNotFoundException());
//         assertThrows(ResourceNotFoundException.class, () -> service.approve(99L));
//     }

//     @Test
//     @DisplayName("Deve rejeitar solicitação existente")
//     void reject_shouldSetStatusRejected() {
//         AccessSolicitation solicitation = new AccessSolicitation();
//         solicitation.setId(1L);
//         solicitation.setStatus("PENDING");

//         when(repository.getReferenceById(1L)).thenReturn(solicitation);
//         when(repository.save(solicitation)).thenReturn(solicitation);

//         AccessSolicitation result = service.reject(1L);

//         assertEquals("REJECTED", result.getStatus());
//     }

//     @Test
//     @DisplayName("Deve lançar ResourceNotFoundException ao rejeitar solicitação inexistente")
//     void reject_shouldThrowResourceNotFound() {
//         when(repository.getReferenceById(99L)).thenThrow(new EntityNotFoundException());
//         assertThrows(ResourceNotFoundException.class, () -> service.reject(99L));
//     }
// }
