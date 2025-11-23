// package io.github.rodrigobarr0s.access_modules_api.integration;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.util.List;
// import java.util.Set;
// import java.util.concurrent.Callable;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.stream.Collectors;
// import java.util.stream.IntStream;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;

// import io.github.rodrigobarr0s.access_modules_api.entity.AccessSolicitation;
// import io.github.rodrigobarr0s.access_modules_api.entity.Module;
// import io.github.rodrigobarr0s.access_modules_api.entity.User;
// import io.github.rodrigobarr0s.access_modules_api.entity.enums.Role;
// import io.github.rodrigobarr0s.access_modules_api.repository.ModuleRepository;
// import io.github.rodrigobarr0s.access_modules_api.repository.UserRepository;
// import io.github.rodrigobarr0s.access_modules_api.service.AccessSolicitationService;

// @SpringBootTest
// @ActiveProfiles("test")
// class AccessSolicitationConcurrencyTest {

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
//     @DisplayName("Deve gerar protocolos únicos em chamadas concorrentes")
//     void concurrentCreate_shouldGenerateUniqueProtocols() throws Exception {
//         User user = createUser("concUser");
//         Module module = createModule("concModule");

//         // Executor com 10 threads
//         var executor = Executors.newFixedThreadPool(10);

//         // 50 tarefas concorrentes criando solicitações
//         List<Callable<AccessSolicitation>> tasks = IntStream.range(0, 50)
//                 .mapToObj(i -> (Callable<AccessSolicitation>) () -> {
//                     AccessSolicitation solicitation = new AccessSolicitation();
//                     solicitation.setUser(user);
//                     solicitation.setModule(module);
//                     return solicitationService.create(solicitation);
//                 })
//                 .collect(Collectors.toList());

//         List<Future<AccessSolicitation>> futures = executor.invokeAll(tasks);

//         // Coletar todos os protocolos gerados
//         Set<String> protocolos = futures.stream()
//                 .map(f -> {
//                     try {
//                         return f.get().getProtocolo();
//                     } catch (Exception e) {
//                         throw new RuntimeException(e);
//                     }
//                 })
//                 .collect(Collectors.toSet());

//         executor.shutdown();

//         // Verificar se todos são únicos
//         assertEquals(50, protocolos.size(), "Protocolos devem ser únicos mesmo em concorrência");
//     }
// }
