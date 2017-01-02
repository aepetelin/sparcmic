package se.lenvo.sparkmics;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.ComponentScan;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

//@EnableAutoConfiguration

//@ComponentScan
public class AppConfig {

    public static void main(String[] args) throws InterruptedException {
        System.out.print("App started (1) ..." + '\n');

        Rpi.getInputState();
        Rpi.lampOnTime(5000);

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(200);
        }

        //SpringApplication.run(AppConfig.class, args);
    }
}


