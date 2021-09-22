package test.umlspringstatemachine;

import ch.qos.logback.classic.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MyStateListener {

    static Logger log;
    
  @Bean
  public static StateMachineListener<String, String> listener() {
      return new StateMachineListenerAdapter<String, String>() {
    	  
          @Override 
          public void stateChanged(State<String, String> from, State<String, String> to) {
              log.info(  " - State change to " + to.getId());
          }
      };
  }
}
