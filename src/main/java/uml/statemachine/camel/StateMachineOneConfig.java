package uml.statemachine.camel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.uml.UmlStateMachineModelFactory;

@Configuration
@EnableStateMachineFactory(name = "config_one")
public class StateMachineOneConfig
        extends StateMachineConfigurerAdapter<String, String> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<String, String> config)
            throws
            Exception {
        config.withConfiguration().autoStartup(true).listener(listener1()).machineId("machineone");
    }

    @Override
    public void configure(StateMachineModelConfigurer<String, String> model)
            throws
            Exception {
        model.withModel().factory(modelFactory1());
    }

    @Bean
    public StateMachineModelFactory<String, String> modelFactory1() {
        return new UmlStateMachineModelFactory("classpath:papyrus/StateMachine1.uml");
    }
	
	/*@Bean
    public StateMachine<String, String> stateMachine(StateMachineListener<String, String> listener) throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();

        builder.configureStates()
                .withStates();

        builder.configureTransitions()
                .withExternal();

        StateMachine<String, String> stateMachine = builder.build();
        stateMachine.addStateListener(listener);
        return stateMachine;
    }*/

    @Bean
    public StateMachineListener<String, String> listener1() {
        return new StateMachineListenerAdapter<String, String>() {
            @Override
            public void stateChanged(State<String, String> from, State<String, String> to) {
                System.out.println("State 1 changed to " + to.getId());
                //log.info("State 1 changed to " + to.getId());
            }
        };

    }

}
