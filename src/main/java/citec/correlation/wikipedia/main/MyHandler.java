/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * @author elahi
 */
public class MyHandler extends StreamHandler {

    public MyHandler() {
    }

    @Override
    public void publish(LogRecord record) {
        //add own logic to publish
        super.publish(record);
    }

    @Override
    public void flush() {
        super.flush();
    }

    @Override
    public void close() throws SecurityException {
        super.close();
    }

}
