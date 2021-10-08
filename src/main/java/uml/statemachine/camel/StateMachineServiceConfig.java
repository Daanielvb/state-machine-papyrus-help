//package uml.statemachine.camel;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.statemachine.config.StateMachineFactory;
//import org.springframework.statemachine.persist.StateMachineRuntimePersister;
//import org.springframework.statemachine.service.DefaultStateMachineService;
//import org.springframework.statemachine.service.StateMachineService;
//
//@RequiredArgsConstructor
//@Configuration
//public class StateMachineServiceConfig {
//
//    private final StateMachineFactory<String, String> stateMachineFactory;
//    private final StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister;
//
//    @Bean
//    public StateMachineService<String, String> stateMachineService() {
//        return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
//    }
//
//
//
//}
