import java.io.Serializable;
import java.util.ArrayList;

public class messageInfo implements Serializable{
    private static final long serialVersionUID = 1L;  // allows to communicate
    ArrayList<Integer> allClients;
    ArrayList<Integer> clickedClients;
    String msg;
    boolean listclicked = false;

    messageInfo() {
        allClients = new ArrayList<>();
        clickedClients = new ArrayList<>();
        msg = "";
    }
}