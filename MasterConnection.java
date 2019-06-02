import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class MasterConnection implements Runnable {

    private Socket masterSocket;
    private BufferedReader in = null;
    int flag;

    public MasterConnection(Socket client) {
        this.masterSocket = client;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    masterSocket.getInputStream()));

            //get the filename,oper from a master
            String fname_oper = in.readLine();
            String[] pair = fname_oper.split(",");

            MutexServer mt = new MutexServer();

            if (pair[1].equals("check")) {
                flag = mt.checkMutex(pair[0]);
                //send flag to the master
                OutputStream os = masterSocket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeInt(flag); //TODO flush
            } else if (pair[1].equals("reset")) {
                mt.resetEntry(pair[0]);
            } else if (pair[1].equals("add")) {
                mt.addEntry(Arrays.asList(pair[0]));
            } else if (pair[1].equals("delete")) {
                mt.deleteEntry(Arrays.asList(pair[0]));
            }

        } catch (Exception e) {
            System.err.println(" error in master connection.. ");
        }
    }
}
