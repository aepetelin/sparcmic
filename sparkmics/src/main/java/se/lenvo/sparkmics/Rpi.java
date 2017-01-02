package se.lenvo.sparkmics;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.util.Timer;
import java.util.TimerTask;

public class Rpi {
    private final static GpioController gpio = GpioFactory.getInstance();

    private final static GpioPinDigitalInput pinDI02 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02);

    private final static GpioPinDigitalOutput lamp1 = gpio.provisionDigitalOutputPin(
            RaspiPin.GPIO_08, "Lamp", PinState.LOW);

    private final static GpioPinDigitalInput button1 = gpio.provisionDigitalInputPin(
            RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);

    static {
        System.out.print("Rpi class constructor ...");
        lamp1.setShutdownOptions(true, PinState.LOW);

        button1.setShutdownOptions(true);
        button1.addListener(listenerDigital());
        System.out.println(" ... complete the GPIO circuit and see the listener feedback here in the console.");
    }

    private static GpioPinListenerDigital listenerDigital() {
        return (new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                publish_event(event);
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }
        });
    }

    private static void publish_event(GpioPinDigitalStateChangeEvent event) {
        // TODO: publish to RubbitMQ
        System.out.println("Publish event to RabbitMQ");

        final String EXCHANGE_NAME = "logs";
         //         throws java.io.IOException {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.0.10");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String message = "event " + event.toString();

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

            channel.close();
            connection.close();
        }
        catch (java.io.IOException ex1) {
            System.out.print("IOException! publish_event()" + '\n' + ex1.getMessage());
        }
        catch (java.util.concurrent.TimeoutException ex2) {
            System.out.print("TimeoutException! publish_event()");
        }
    }

    private static void subscribe_cmd() {
        // TODO: read from RabbitMQ
        // load lamp on time
    }

    public static void lampOnTime(int interval) throws InterruptedException {
        lamp1.high();
        System.out.println("Lamp (1) should be: ON " + lamp1.getState());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                lamp1.low();
                System.out.println("Lamp (1) should be: OFF " + lamp1.getState());
            }
        }, interval);
    }

    public static void getInputState() {
        GpioPinDigitalInput pin = pinDI02;
        System.out.println("--> DI_02 (" + pin.getName() + ") state: " + pin.getState());
    }
}
