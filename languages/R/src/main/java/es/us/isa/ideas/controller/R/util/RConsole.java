/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.ideas.controller.R.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 *
 * @author japarejo
 */
public class RConsole {

    public static void main(String[] args) {
        try {
            RConnection c = new RConnection((args.length > 0) ? args[0] : "127.0.0.1");

            BufferedReader ir = new BufferedReader(new InputStreamReader(System.in));
            String s = null;
            System.out.print("> ");
            while ((s = ir.readLine()).length() > 0) {
                if (s.equals("shutdown")) {
                    System.out.println("Sending shutdown request");
                    c.shutdown();
                    System.out.println("Shutdown successful. Quitting console.");
                    return;
                } else {
                    REXP rx = c.parseAndEval(s);
                    System.out.println("result(debug): " + rx.toDebugString());
                }
                System.out.print("> ");
            }

        } catch (RserveException rse) {
            System.out.println(rse);
            /*		} catch (REXPMismatchException mme) {
             System.out.println(mme);
             mme.printStackTrace(); */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
