/**
 *
 *  @author Trauth Szymon  S30749
 *
 */

package zad1;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable
{

    private final Client client;
    private final List<String> reqs;
    private final boolean showSendRes;
    private StringBuilder clientLog = new StringBuilder();
    private boolean isDone = false;

    private ClientTask(Client client, List<String> reqs, boolean showSendRes)
    {
        this.client = client;
        this.reqs = reqs;
        this.showSendRes = showSendRes;
    }

    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes)
    {
        return new ClientTask(c, reqs, showSendRes);
    }

    @Override
    public void run()
    {
        try {
            client.connect();
            clientLog.append("=== ").append(client.getId()).append(" log start ===\n");
            client.send("login " + client.getId());
            clientLog.append("logged in\n");
            for (String req : reqs)
            {
                String response = client.send(req);
                clientLog.append("Request: ").append(req).append("\n");
                clientLog.append(response).append("\n");
                if (showSendRes)
                {
                    System.out.println(response);
                }
            }
            client.send("bye and log transfer");
            clientLog.append("logged out\n");
            clientLog.append("=== ").append(client.getId()).append(" log end ===\n");
            isDone = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            isDone = true;
        }
    }

    public String get() throws InterruptedException, ExecutionException
    {
        while (!isDone) {
            Thread.sleep(10);
        }
        return clientLog.toString();
    }
}
